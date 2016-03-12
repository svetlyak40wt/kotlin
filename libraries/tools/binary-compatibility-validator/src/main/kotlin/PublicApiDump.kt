package org.jetbrains.kotlin.tools

import org.objectweb.asm.*
import org.objectweb.asm.tree.*
import java.util.jar.JarFile
import kotlin.comparisons.compareBy

fun main(args: Array<String>) {
    // println("Hello, World1")
    //val src = """c:\Users\Ilya.Gorbunov\.m2\repository\org\jetbrains\kotlin\kotlin-stdlib\1.0.0\kotlin-stdlib-1.0.0.jar"""
    //val src = """c:\Users\Ilya.Gorbunov\.m2\repository\org\jetbrains\kotlin\kotlin-runtime\1.0.0\kotlin-runtime-1.0.0.jar"""
    var src = args[0]
    println(src)
    println("------------------\n");
    dumpBinaryAPI(JarFile(src))


}


val ACCESS_NAMES = mapOf(
        Opcodes.ACC_PUBLIC to "public",
        Opcodes.ACC_PROTECTED to "protected",
        Opcodes.ACC_PRIVATE to "private",
        Opcodes.ACC_STATIC to "static",
        Opcodes.ACC_FINAL to "final",
        Opcodes.ACC_ABSTRACT  to "abstract",
        Opcodes.ACC_SYNTHETIC to "synthetic",
        Opcodes.ACC_INTERFACE to "interface",
        Opcodes.ACC_ANNOTATION to "annotation")

fun JarFile.classEntries() = entries().asSequence().filter {
    !it.isDirectory && it.name.endsWith(".class")
}

data class ClassBinarySignature(val name: String, val signature: String, val memberSignatures: List<String>)

fun getBinaryAPI(jar: JarFile): List<ClassBinarySignature> = jar.classEntries()
        .map { entry ->
            jar.getInputStream(entry).use { stream ->
                val classNode = ClassNode()
                ClassReader(stream).accept(classNode, ClassReader.SKIP_CODE)
                classNode
            }
        }
        .filter { it.isEffectivelyPublic() }
        .map { with(it) {
            val supertypes = listOf(superName) - "java/lang/Object" + interfaces.sorted()

            val classSignature = "${getModifierString(access)} class $name" +
                    if (supertypes.isEmpty()) "" else ": ${supertypes.joinToString()}"

            val memberSignatures =
                    fields.filter { it.isPublic() }
                            .sortedBy { it.name }
                            .map { with(it) { "${getModifierString(access)} field $name $desc" } } +
                    methods.filter { it.isEffectivelyPublic() }
                            .sortedWith(compareBy({ it.name }, { it.desc }))
                            .map { with(it) { "${getModifierString(access)} fun $name $desc" } }

            ClassBinarySignature(name, classSignature, memberSignatures)
        }}
        .asIterable()
        .sortedBy { it.name }


fun dumpBinaryAPI(jar: JarFile) {
    getBinaryAPI(jar).forEach {
        println(it.signature)
        it.memberSignatures.forEach { println(it) }
        println("------------------\n")
    }
}



fun isPublic(access: Int) = access and Opcodes.ACC_PUBLIC != 0 || access and Opcodes.ACC_PROTECTED != 0
fun getModifiers(access: Int): List<String> = ACCESS_NAMES.entries.mapNotNull { if (access and it.key != 0) it.value else null }
fun getModifierString(access: Int): String = getModifiers(access).joinToString(" ")

fun ClassNode.isSynthetic() = access and Opcodes.ACC_SYNTHETIC != 0
fun MethodNode.isSynthetic() = access and Opcodes.ACC_SYNTHETIC != 0
fun ClassNode.isPublic() = isPublic(access)
fun MethodNode.isPublic() = isPublic(access)
fun FieldNode.isPublic() = isPublic(access)

fun ClassNode.isEffectivelyPublic() = isPublic() && !isLocal() && !isNonPublicFileOrFacade() && !isWhenMappings()
fun MethodNode.isEffectivelyPublic() = isPublic() && !isAccessMethod()


fun ClassNode.isLocal() = innerClasses.filter { it.name == name && it.innerName == null && it.outerName == null }.count() == 1
fun ClassNode.isWhenMappings() = isSynthetic() && name.endsWith("\$WhenMappings")
fun MethodNode.isAccessMethod() = isSynthetic() && name.startsWith("access\$")



private val FILE_OR_MULTIPART_FACADE_KINDS = listOf(2, 4)
fun ClassNode.isFileOrMultipartFacade() = kotlinClassKind.let { it != null && it in FILE_OR_MULTIPART_FACADE_KINDS }

fun ClassNode.isNonPublicFileOrFacade() = isFileOrMultipartFacade() && methods.none { it.isEffectivelyPublic() } && fields.none { it.isPublic() }

val ClassNode.kotlinClassKind: Int?
    get() = visibleAnnotations
            ?.filter { it.desc == "Lkotlin/Metadata;" }
            ?.map { (it.values.annotationValue("k") as? Int) }
            ?.firstOrNull()

private fun List<Any>.annotationValue(key: String): Any? {
    for (index in (0 .. size / 2 - 1)) {
        if (this[index*2] == key)
            return this[index*2 + 1]
    }
    return null
}