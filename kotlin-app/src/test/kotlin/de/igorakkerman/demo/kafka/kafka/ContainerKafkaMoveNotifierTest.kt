package de.igorakkerman.demo.kafka.kafka

import de.igorakkerman.demo.kafka.kafka.ContainerKafkaMoveNotifierTest.KafkaTestContainersConfiguration
import de.igorakkerman.demo.kafka.springboot.Application
import io.kotest.matchers.shouldBe
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.producer.ProducerConfig.BOOTSTRAP_SERVERS_CONFIG
import org.apache.kafka.clients.producer.ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG
import org.apache.kafka.clients.producer.ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG
import org.apache.kafka.common.serialization.StringDeserializer
import org.apache.kafka.common.serialization.StringSerializer
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.ProducerFactory
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
        fun kafkaListenerContainerFactory(): ConcurrentKafkaListenerContainerFactory<Int, String> {
            return ConcurrentKafkaListenerContainerFactory<Int, String>().also {
                it.consumerFactory = DefaultKafkaConsumerFactory(
                    mapOf(
                        ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to kafkaContainer.bootstrapServers,
                        ConsumerConfig.AUTO_OFFSET_RESET_CONFIG to "earliest",
                        ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java,
                        ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG to StringDeserializer::class.java,
                    )
                )
            }
        }

        @Bean
        fun producerFactory(): ProducerFactory<Any, Any> = DefaultKafkaProducerFactory(
            mapOf(
                BOOTSTRAP_SERVERS_CONFIG to kafkaContainer.bootstrapServers,
                KEY_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java,
                VALUE_SERIALIZER_CLASS_CONFIG to StringSerializer::class.java,
            )
        )
    }

    @Test
    internal fun `should be reading a message`() {
        val now = LocalDateTime.now().format(ofPattern("yyyy-MM-dd HH:mm:ss"))
        producer.notifyPlayers("$now Cinka sitzt in der Box!")
        consumer.latch.await(10, SECONDS)

        consumer.latch.count shouldBe 0
    }
}
