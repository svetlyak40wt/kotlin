// !DIAGNOSTICS: -UNUSED_VARIABLE

class A

val a1 = <!CLASS_LITERAL_LHS_NOT_A_CLASS!>A?::class<!>
val a2 = <!CLASS_LITERAL_LHS_NOT_A_CLASS!>A?<!REDUNDANT_NULLABLE!>?<!>::class<!>

val l1 = <!CLASS_LITERAL_LHS_NOT_A_CLASS!>List<String>?::class<!>
val l2 = <!CLASS_LITERAL_LHS_NOT_A_CLASS!>List?::class<!>

fun <T : Any> foo() {
    val t1 = <!CLASS_LITERAL_LHS_NOT_A_CLASS!>T::class<!>
    val t2 = <!CLASS_LITERAL_LHS_NOT_A_CLASS!>T?::class<!>
}

val m = Map<!WRONG_NUMBER_OF_TYPE_ARGUMENTS_FOR_CLASS!><String><!>::class