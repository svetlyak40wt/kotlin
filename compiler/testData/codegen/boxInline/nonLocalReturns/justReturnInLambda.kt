// FILE: 1.kt

package test

public inline fun <R> doCall(block: ()-> R) : R {
    return block()
}

// FILE: 2.kt

import test.*

class Z {}

//var i = ""
//fun foo(a: String): String {
//    i += a
//    return i
//}
//
//fun foo(a: String, b: String, c: String) { i +=  "foo(...)"}
//
fun test1(nonLocal: String): String {

//    for (i in 0..9) {
//        foo(foo("a"), continue, foo("b"))
//    }
//
    val localResult = doCall {
        return nonLocal
    }
}

fun box(): String {
    val test2 = test1("OK_NONLOCAL")
//    if (i != "aaaaaaaaaa") return "i: ${i}"
    if (test2 != "OK_NONLOCAL") return "test2: ${test2}"

    return "OK"
}
