// See https://youtrack.jetbrains.com/issue/KT-10785
package foo

fun box(): String {
    val map = hashMapOf("a" to 1)
    map.keys -= "a"
    return "OK"
}