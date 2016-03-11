package p

public fun foo(a: Any) {
    a is Map<!WRONG_NUMBER_OF_TYPE_ARGUMENTS_FOR_CLASS!><Int><!>
    a is <!NO_TYPE_ARGUMENTS_ON_RHS!>Map<!>
    a is Map<out Any?, Any?>
    a is Map<*, *>
    a is Map<<!SYNTAX!><!>>
    a is List<<!WRONG_NUMBER_OF_TYPE_ARGUMENTS_FOR_CLASS!>Map<!>>
    a is <!NO_TYPE_ARGUMENTS_ON_RHS!>List<!>
    a is Int

    (a as <!NO_TYPE_ARGUMENTS_ON_RHS!>Map<!>) is <!INCOMPATIBLE_TYPES!>Int<!>
}