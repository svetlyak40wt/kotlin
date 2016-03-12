/*
 * Copyright 2010-2016 JetBrains s.r.o.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.jetbrains.kotlin.codegen.state

import com.intellij.psi.PsiElement
import org.jetbrains.kotlin.codegen.ClassBuilder
import org.jetbrains.kotlin.codegen.ClassBuilderFactory
import org.jetbrains.kotlin.codegen.DelegatingClassBuilder
import org.jetbrains.kotlin.codegen.DelegatingClassBuilderFactory
import org.jetbrains.kotlin.descriptors.DeclarationDescriptor
import org.jetbrains.kotlin.renderer.DescriptorRenderer
import org.jetbrains.kotlin.resolve.jvm.diagnostics.JvmDeclarationOrigin
import org.jetbrains.kotlin.resolve.jvm.diagnostics.MemberKind
import org.jetbrains.kotlin.resolve.jvm.diagnostics.RawSignature
import org.jetbrains.org.objectweb.asm.FieldVisitor
import org.jetbrains.org.objectweb.asm.MethodVisitor
import java.io.BufferedWriter
import java.io.File


class SignatureDumpingBuilderFactory(
        builderFactory: ClassBuilderFactory,
        val destination: File
) : DelegatingClassBuilderFactory(builderFactory) {

    companion object {
        val MEMBER_RENDERER = DescriptorRenderer.COMPACT_WITH_MODIFIERS
        val TYPE_RENDERER = DescriptorRenderer.withOptions { }
    }

    private val outputStream: BufferedWriter by lazy {
        println("INFO: Dumping signatures to $destination")
        destination.bufferedWriter().apply { append("[\n") }
    }
    private var firstClassWritten: Boolean = false

    override fun close() {
        outputStream.append("\n]")
        outputStream.close()
        super.close()
    }

    override fun newClassBuilder(origin: JvmDeclarationOrigin): DelegatingClassBuilder {
        return SignatureDumpingClassBuilder(origin, delegate.newClassBuilder(origin))
    }


    private inner class SignatureDumpingClassBuilder(val origin: JvmDeclarationOrigin, val _delegate: ClassBuilder) : DelegatingClassBuilder() {
        override fun getDelegate() = _delegate

        private val signatures = mutableListOf<Pair<RawSignature, DeclarationDescriptor?>>()
        private var javaClassName: String? = null

        override fun defineClass(origin: PsiElement?, version: Int, access: Int, name: String, signature: String?, superName: String, interfaces: Array<out String>) {
            javaClassName = name

            super.defineClass(origin, version, access, name, signature, superName, interfaces)
        }

        override fun newMethod(origin: JvmDeclarationOrigin, access: Int, name: String, desc: String, signature: String?, exceptions: Array<out String>?): MethodVisitor {
            signatures += RawSignature(name, desc, MemberKind.METHOD) to origin.descriptor
            return super.newMethod(origin, access, name, desc, signature, exceptions)
        }

        override fun newField(origin: JvmDeclarationOrigin, access: Int, name: String, desc: String, signature: String?, value: Any?): FieldVisitor {
            signatures += RawSignature(name, desc, MemberKind.FIELD) to origin.descriptor
            return super.newField(origin, access, name, desc, signature, value)
        }

        override fun done() {
            if (firstClassWritten) outputStream.append(",\n") else firstClassWritten = true
            outputStream.append("\t{\n")
            origin.descriptor?.let {
                outputStream.append("\t\t").appendNameValue("declaration", TYPE_RENDERER.render(it)).append(",\n")
            }
            outputStream.append("\t\t").appendNameValue("class", javaClassName ?: "<unknown>").append(", \n")

            outputStream.append("\t\t").appendQuoted("members").append(": [\n")
            signatures.joinTo(outputStream, ",\n") { buildString {
                val (signature, descriptor) = it
                append("\t\t\t{")
                descriptor?.let {
                    appendNameValue("declaration", MEMBER_RENDERER.render(it)).append(", ")
                }
                appendNameValue("name", signature.name).append(", ")
                appendNameValue("desc", signature.desc).append("}")
            }}
            outputStream.append("\n\t\t]\n\t}")

            super.done()
        }
    }
}

private fun Appendable.appendQuoted(value: String?): Appendable = value?.let { append('"').append(jsonEscape(it)).append('"') } ?: append("null")
private fun Appendable.appendNameValue(name: String, value: String?): Appendable = appendQuoted(name).append(": ").appendQuoted(value)

private fun jsonEscape(value: String): String = value
