package de.igorakkerman.demo.kafka.kafka.embedded

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
import org.springframework.kafka.core.ProducerFactory
import org.springframework.kafka.test.context.EmbeddedKafka
import org.springframework.test.annotation.DirtiesContext
import java.time.LocalDateTime

@SpringBootTest(classes = [Application::class])
@EmbeddedKafka(partitions /* per topic */ = 1, brokerProperties = ["listeners=PLAINTEXT://localhost:9092", "port=9092"])
@DirtiesContext
internal class EmbeddedKafkaMoveReceiverTest(
    @Autowired
    private val producerFactory: ProducerFactory<String, Move>,
    @Value("\${spring.kafka.template.default-topic}")
    private val topic: String,
) {
    @MockkBean(relaxUnitFun = true)
    private lateinit var gameService: GameService

    @Test
    internal fun `should be able to receive a message`() {
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

