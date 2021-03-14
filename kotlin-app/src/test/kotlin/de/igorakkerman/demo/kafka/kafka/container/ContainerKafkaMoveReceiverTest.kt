package de.igorakkerman.demo.kafka.kafka.container

import com.ninjasquad.springmockk.MockkBean
import de.igorakkerman.demo.kafka.application.GameService
import de.igorakkerman.demo.kafka.application.Move
import de.igorakkerman.demo.kafka.springboot.Application
import io.mockk.verify
import org.apache.kafka.clients.producer.ProducerRecord
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.kafka.core.ProducerFactory
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.junit.jupiter.EnabledIf
import org.testcontainers.junit.jupiter.Testcontainers
import java.time.LocalDateTime

@SpringBootTest(classes = [Application::class])
@EnabledIf(expression = "\${test.container.enabled:false}", loadContext = true)
@Import(TestContainersConfiguration::class)
@Testcontainers
@DirtiesContext
internal class ContainerKafkaMoveReceiverTest(
    @Autowired
    private val producerFactory: ProducerFactory<String, Move>,

    @Value("\${spring.kafka.template.default-topic}")
    private val topic: String,
) {
    @MockkBean(relaxUnitFun = true)
    private lateinit var gameService: GameService

    @Test
    internal fun `should be reading a message`() {
        // given
        val move = Move("Cinka", 321, LocalDateTime.now().toString())
        val producer = producerFactory.createProducer()

        // GameService mock is relaxed

        // when
        producer.send(ProducerRecord(topic, move))
        producer.flush()

        // then
        verify(timeout = 3000) { gameService.registerMove(move) }
    }
}

