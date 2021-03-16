package de.igorakkerman.demo.kafka.kafka.container

import de.igorakkerman.demo.kafka.application.Move
import de.igorakkerman.demo.kafka.kafka.KafkaMoveNotifier
import io.kotest.matchers.shouldBe
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.boot.autoconfigure.EnableAutoConfiguration
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.context.annotation.Import
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.test.utils.KafkaTestUtils
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.junit.jupiter.EnabledIf
import org.testcontainers.junit.jupiter.Testcontainers
import java.time.LocalDateTime

@SpringBootTest(classes = [KafkaMoveNotifier::class])
@EnabledIf(expression = "\${test.container.enabled:false}", loadContext = true)
@EnableAutoConfiguration
@Import(TestContainersConfiguration::class)
@Testcontainers
@DirtiesContext
internal class ContainerKafkaMoveNotifierTest(
    @Autowired
    private val moveNotifier: KafkaMoveNotifier,

    @Autowired
    private val consumerFactory: ConsumerFactory<String, Move>,

    @Value("\${spring.kafka.template.default-topic}")
    private val topic: String,

    @Value("\${spring.kafka.consumer.group-id}")
    private val consumerGroupId: String,
) {

    @Test
    internal fun `should be reading a message`() {
        // given
        val move = Move("Cinka", 321, LocalDateTime.now().toString())

        // when
        moveNotifier.notifyPlayers(move)

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

