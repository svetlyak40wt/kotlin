package foo

// CHECK_NOT_CALLED: block
// CHECK_BREAKS_COUNT: function=testIfElse_za3lpa$ count=1
// CHECK_BREAKS_COUNT: function=testIfElseInverted_za3lpa$ count=1
// CHECK_BREAKS_COUNT: function=testRedundantIfElse_za3lpa$ count=1
// CHECK_BREAKS_COUNT: function=testWhileNotOptimized_za3lpa$ count=1
// CHECK_BREAKS_COUNT: function=testTryCatch count=0

inline fun block(p: () -> Unit): Unit = p()

fun testIfElse(x: Int): Int {
    var r = 0
    block a@ {
        block b@ {
            if (x > 0)
                return@a
            else
                return@b
        }
        r += 1
    }
    r += 10
    return r
}

fun testIfElseInverted(x: Int): Int {
    var r = 0
    block a@ {
        block b@ {
            if (x > 0)
                return@b
            else
                return@a
        }
        r += 1
    }
    r += 10
    return r
}

fun testRedundantIfElse(x: Int): Int {
    var r = 0
    block a@ {
        block b@ {
            if (x > 5) {
                r += 1
                return@b
            }
            if (x > 0)
                return@a
            else
                return@a
        }
    }
    r += 10
    return r
}

fun testWhileNotOptimized(x: Int): Int {
    var r = 0
    var c = x
    block a@ {
        while (true) {
            c--
            r++
            if (c > 0) {
                continue
            }
            return@a
        }
    }
    return r
}

fun testTryCatch(): Int {
    var r = 0
    block a@ {
        try {
            r += 10
            return@a
        } finally {
            r += 1;
        }
    }
    return r
}

fun box(): String {
    var result = testIfElse(2)
    if (result != 10) return "fail1a: $result"
    result = testIfElse(-2)
    if (result != 11) return "fail1b: $result"

    result = testIfElseInverted(2)
    if (result != 11) return "fail2a: $result"
    result = testIfElseInverted(-2)
    if (result != 10) return "fail2b: $result"

    result = testRedundantIfElse(7)
    if (result != 11) return "fail3a: $result"
    result = testRedundantIfElse(-2)
    if (result != 10) return "fail3b: $result"

    result = testWhileNotOptimized(3)
    if (result != 3) return "fail4a: $result"
    result = testWhileNotOptimized(5)
    if (result != 5) return "fail4b: $result"

    result = testTryCatch()
    if (result != 11) return "fail5: $result"

    return "OK"
}