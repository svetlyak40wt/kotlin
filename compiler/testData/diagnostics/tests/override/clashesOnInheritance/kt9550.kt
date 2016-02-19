interface A {
    fun <T> foo()
}

interface B {
    fun foo()
}

<!CONFLICTING_INHERITED_MEMBERS!>interface C<!> : A, B
