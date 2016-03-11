//KT-304: Resolve supertype reference to class anyway

open class Foo() : <!WRONG_NUMBER_OF_TYPE_ARGUMENTS_FOR_CLASS!>Bar<!>() {
}

open class Bar<T>() {
}