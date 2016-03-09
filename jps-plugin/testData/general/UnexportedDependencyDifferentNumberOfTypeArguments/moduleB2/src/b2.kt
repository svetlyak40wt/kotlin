package b

import a.A

interface B2 {
    fun consume(a: A<Int, String, Double>.Inner<B2>)
}
