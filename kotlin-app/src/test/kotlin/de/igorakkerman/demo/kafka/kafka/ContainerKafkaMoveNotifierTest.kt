package de.igorakkerman.demo.kafka.kafka

import de.igorakkerman.demo.kafka.application.Move
import de.igorakkerman.demo.kafka.kafka.ContainerKafkaMoveNotifierTest.KafkaTestContainersConfiguration
import de.igorakkerman.demo.kafka.springboot.Application
import io.kotest.matchers.shouldBe
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.producer.ProducerConfig
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.junit.jupiter.EnabledIf
import org.testcontainers.containers.KafkaContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter.ofPattern
import java.util.concurrent.TimeUnit.SECONDS

@SpringBootTest(classes = [Application::class])
@EnabledIf(expression = "\${test.container.enabled:false}", loadContext = true)
@Testcontainers
@Import(KafkaTestContainersConfiguration::class)
@DirtiesContext
internal class ContainerKafkaMoveNotifierTest(
    @Autowired
    private val producer: KafkaMoveNotifier,
    @Autowired
    private val consumer: KafkaMoveReceiver,
) {
    @TestConfiguration
    @ConditionalOnProperty("test.container.enabled", havingValue = "true")
    internal class KafkaTestContainersConfiguration {
        @Container
        val kafkaContainer = KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:5.4.3"))
            .also { it.start() }

        @Bean
        fun kafkaListenerContainerFactory(
            kafkaProperties: KafkaProperties,
        ) = ConcurrentKafkaListenerContainerFactory<Int, String>().also {
            it.consumerFactory = DefaultKafkaConsumerFactory(
                kafkaProperties.buildConsumerProperties().also {
                    it[ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG] = kafkaContainer.bootstrapServers
                }
            )
        }

        @Bean
        fun producerFactory(
            kafkaProperties: KafkaProperties,
        ) = DefaultKafkaProducerFactory<Any, Any>(
            kafkaProperties.buildProducerProperties().also {
                it[ProducerConfig.BOOTSTRAP_SERVERS_CONFIG] = kafkaContainer.bootstrapServers
            }
        )
    }

    @Test
    internal fun `should be reading a message`() {
        val now = LocalDateTime.now().format(ofPattern("yyyy-MM-dd HH:mm:ss"))
        producer.notifyPlayers(Move("Cinka", 101))
        consumer.latch.await(10, SECONDS)

        consumer.latch.count shouldBe 0
    }
}
