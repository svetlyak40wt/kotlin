package b

import a.A

interface B1 {
    fun produce(): A<String>.Inner<Int, Unit>
}
