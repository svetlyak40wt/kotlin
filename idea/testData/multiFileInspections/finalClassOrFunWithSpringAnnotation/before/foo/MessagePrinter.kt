package hello2

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Component

@Component
class MessagePrinter @Autowired constructor(private val service: MessageService) {
    fun printMessage() {
        println(service.message)
    }
}
