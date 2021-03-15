package de.igorakkerman.demo.kafka.kafka.embedded

import de.igorakkerman.demo.kafka.application.Move
import de.igorakkerman.demo.kafka.kafka.KafkaMoveNotifier
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.test.context.EmbeddedKafka
import org.springframework.kafka.test.utils.KafkaTestUtils
import org.springframework.test.annotation.DirtiesContext
import java.time.LocalDateTime

@SpringBootTest(classes = [KafkaMoveNotifier::class])
@EnableAutoConfiguration
@EmbeddedKafka(partitions /* per topic */ = 1, brokerProperties = ["listeners=PLAINTEXT://localhost:9092", "port=9092"])
@DirtiesContext
internal class EmbeddedKafkaMoveNotifierTest(
    @Autowired
    private val notifier: KafkaMoveNotifier,
    @Autowired
    private val consumerFactory: ConsumerFactory<String, Move>,
    @Value("\${spring.kafka.template.default-topic}")
    private val topic: String,
    @Value("\${spring.kafka.consumer.group-id}")
    private val consumerGroupId: String,
) {
    @Test
    internal fun `should be able to send a message`() {
        // given
        val move = Move("Cinka", 321, LocalDateTime.now().toString())

        // when
        notifier.notifyPlayers(move)

        // then
        val receivedMove =
            // group id must be different from receiver's, so that both receive all messages
            consumerFactory.createConsumer("$consumerGroupId-test", null)
                .use { consumer ->
                    consumer.subscribe(listOf(topic))
                    return@use KafkaTestUtils.getSingleRecord(consumer, topic).value()
                }

        receivedMove shouldBe move
    }
}

