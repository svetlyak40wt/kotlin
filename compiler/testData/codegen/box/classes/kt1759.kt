class Greeter(var name : String) {
    fun greet() {
        name = name.plus("")
        println("Hello, $name");
    }
}

fun box() : String {
    Greeter("OK").greet()
    return "OK"
}
