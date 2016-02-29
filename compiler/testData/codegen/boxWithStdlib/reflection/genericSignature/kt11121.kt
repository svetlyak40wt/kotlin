class B<M>

interface A<T, Y : B<T>> {

    fun <T, L> p(p: T): T {
        return p
    }
}


fun box(): String {
    val defaultImpls = Class.forName("A\$DefaultImpls")
    val declaredMethod = defaultImpls.getDeclaredMethod("p", A::class.java, Any::class.java)
    if (declaredMethod.toGenericString() != "public static <T_I1,Y,T,L> T A\$DefaultImpls.p(A<T_I1, Y>,T)") return "fail"

    return "OK"
}