package de.igorakkerman.demo.kafka.kafka.container

import de.igorakkerman.demo.kafka.application.Move
import org.apache.kafka.clients.consumer.ConsumerConfig
import org.apache.kafka.clients.producer.ProducerConfig
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory
import org.springframework.kafka.core.ConsumerFactory
import org.springframework.kafka.core.DefaultKafkaConsumerFactory
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.kafka.core.ProducerFactory
import org.testcontainers.containers.KafkaContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName

@TestConfiguration
@ConditionalOnProperty("test.container.enabled", havingValue = "true")
@Testcontainers
internal class TestContainersConfiguration {

    @Container
    val kafkaContainer = KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:5.4.3"))
        .apply { start() }

    private fun <K, V> MutableMap<K, V>.alsoMap(vararg pairs: Pair<K, V>) = apply { putAll(pairs) }

    @Bean
    fun consumerFactory(
        kafkaProperties: KafkaProperties,
    ) = DefaultKafkaConsumerFactory<String, Move>(
        kafkaProperties.buildConsumerProperties().alsoMap(
            ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG to kafkaContainer.bootstrapServers,
        )
    )

    @Bean
    fun producerFactory(
        kafkaProperties: KafkaProperties,
    ) = DefaultKafkaProducerFactory<String, Move>(
        kafkaProperties.buildProducerProperties().alsoMap(
            ProducerConfig.BOOTSTRAP_SERVERS_CONFIG to kafkaContainer.bootstrapServers,
        )
    )

    @Bean
    // Spring Boot autoconfiguration requires generic type <Any, Any>
    fun producerFactory(
        concreteFactory: ProducerFactory<String, Move>
    ): ProducerFactory<out Any, out Any> = concreteFactory

    @Bean
    fun kafkaListenerContainerFactory(
        kafkaProperties: KafkaProperties,
        consumerFactory: ConsumerFactory<String, Move>
    ) = ConcurrentKafkaListenerContainerFactory<String, Move>().also {
        it.consumerFactory = consumerFactory
    }
}