package de.igorakkerman.demo.kafka.kafka

import de.igorakkerman.demo.kafka.application.Move
import de.igorakkerman.demo.kafka.springboot.Application
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.kafka.test.context.EmbeddedKafka
import org.springframework.test.annotation.DirtiesContext
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter.ofPattern
import java.util.concurrent.TimeUnit.SECONDS

@SpringBootTest(classes = [Application::class])
@EmbeddedKafka(partitions = 1, brokerProperties = ["listeners=PLAINTEXT://localhost:9092", "port=9092"])
@DirtiesContext
internal class EmbeddedKafkaMoveNotifierTest(
    @Autowired
    private val producer: KafkaMoveNotifier,
    @Autowired
    private val consumer: KafkaMoveReceiver,
) {
    @Test
    internal fun `should be able to send a message`() {
        val now = LocalDateTime.now().format(ofPattern("yyyy-MM-dd HH:mm:ss"))

        producer.notifyPlayers(Move("Cinka", 321))
        consumer.latch.await(10, SECONDS)

        consumer.latch.count shouldBe 0
    }
}

