// See https://youtrack.jetbrains.com/issue/KT-10785
package foo

class A(var x: Int) {
    operator fun plusAssign(other: A) {
        x += other.x
    }
}

object B {
    val foo = A(42)
}

fun box(): String {
    B.foo += A(23)
    if (B.foo.x != 65) return "failed: ${B.foo.x}"
    return "OK"
}