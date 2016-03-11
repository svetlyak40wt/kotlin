interface Tr
interface G<T>

fun test(tr: Tr): Any {
    return tr as G<<!WRONG_NUMBER_OF_TYPE_ARGUMENTS_FOR_CLASS!>G<!>>
}

fun test1(tr: Tr): Any {
    return tr as <!WRONG_NUMBER_OF_TYPE_ARGUMENTS_FOR_CLASS!>G<!>.(<!WRONG_NUMBER_OF_TYPE_ARGUMENTS_FOR_CLASS!>G<!>) -> <!WRONG_NUMBER_OF_TYPE_ARGUMENTS_FOR_CLASS!>G<!>
}