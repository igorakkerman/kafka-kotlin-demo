package de.igorakkerman.demo.kafka.springboot

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest

@SpringBootTest(classes = [Application::class])
class ApplicationConfigTest {
    @Test
    fun contextLoads() {
    }
}