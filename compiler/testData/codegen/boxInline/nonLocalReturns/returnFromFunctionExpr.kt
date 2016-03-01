// FILE: 1.kt

inline fun foo(f: () -> Unit) {
    f()
}

// FILE: 2.kt

//NO_CHECK_LAMBDA_INLINING
fun test1(): String = fun (): String {
    foo { return "OK" }
    return "fail"
} ()

fun test2(): String = (l@ fun (): String {
    foo { return@l "OK" }
    return "fail"
}) ()

fun box(): String {
    if (test1() != "OK") return "fail 1: ${test1()}"

    if (test2() != "OK") return "fail 2: ${test2()}"

    return "OK"
}
