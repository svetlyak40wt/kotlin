package hello2

import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.AnnotationConfigApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration

@Configuration
@ComponentScan
class Application {
    @Bean
    fun mockMessageService(): MessageService {
        return object : MessageService {
            override val message: String
                get() = "test"
        }
    }

    companion object {
        @JvmStatic fun main(args: Array<String>) {
            val context = AnnotationConfigApplicationContext(Application::class.java)
            val printer = context.getBean(MessagePrinter::class.java)
            printer.printMessage()
        }
    }
}
