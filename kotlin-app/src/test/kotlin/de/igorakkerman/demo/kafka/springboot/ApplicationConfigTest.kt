package de.igorakkerman.demo.kafka.springboot

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.annotation.DirtiesContext

@SpringBootTest(classes = [Application::class])
@DirtiesContext
class ApplicationConfigTest {
    @Test
    fun contextLoads() {
    }
}