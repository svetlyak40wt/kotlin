fun unreachable() {}

fun a() {
    do {
    } while (true)
    <!UNREACHABLE_CODE!>unreachable()<!>
}

fun b() {
    while (true) {
    }
    <!UNREACHABLE_CODE!>unreachable()<!>
}