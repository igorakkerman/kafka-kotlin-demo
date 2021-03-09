package de.igorakkerman.demo.kafka.kafka

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
internal class EmbeddedKafkaMoveMessageProducerTest(
    @Autowired
    private val producer: KafkaMoveMessageProducer,
    @Autowired
    private val consumer: KafkaMoveMessageConsumer,
) {
    @Test
    internal fun `should be able to send a message`() {
        producer.sendMessage("${LocalDateTime.now().format(ofPattern("yyyy-MM-dd HH:mm:ss"))} Cinka liegt im Bett!'")
        consumer.latch.await(10, SECONDS)

        consumer.latch.count shouldBe 0
    }
}

