package c

import b.B1
import b.B2

fun bar(b1: B1, b2: B2) {
    b2.consume(b1.produce())
}
