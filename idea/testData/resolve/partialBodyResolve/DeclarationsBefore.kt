fun foo(p: Int) {
    x()

    val v1 = p * p

    if (y()) {
        val v2 = v1 * v1
        val v3 = v1 * v2

        run {
            val v2 = 1
            println(v2)
        }

        print(<caret>v2)

        run {
            val v2 = 2
            println(v2)
        }
    }

    z()
}
