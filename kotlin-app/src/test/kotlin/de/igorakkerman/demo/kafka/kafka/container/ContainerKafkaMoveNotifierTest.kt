package de.igorakkerman.demo.kafka.kafka.container

import de.igorakkerman.demo.kafka.application.Move
import de.igorakkerman.demo.kafka.kafka.KafkaMoveNotifier
import de.igorakkerman.demo.kafka.kafka.container.ContainerKafkaMoveNotifierTest.KafkaTestContainersConfiguration
import de.igorakkerman.demo.kafka.springboot.Application
import org.apache.kafka.clients.producer.ProducerConfig
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Import
import org.springframework.kafka.core.DefaultKafkaProducerFactory
import org.springframework.test.annotation.DirtiesContext
import org.springframework.test.context.junit.jupiter.EnabledIf
import org.testcontainers.containers.KafkaContainer
import org.testcontainers.junit.jupiter.Container
import org.testcontainers.junit.jupiter.Testcontainers
import org.testcontainers.utility.DockerImageName
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter.ofPattern

@SpringBootTest(classes = [Application::class])
@EnabledIf(expression = "\${test.container.enabled:false}", loadContext = true)
@Testcontainers
@Import(KafkaTestContainersConfiguration::class)
@DirtiesContext
internal class ContainerKafkaMoveNotifierTest(
    @Autowired
    private val moveNotifier: KafkaMoveNotifier,
) {
    @TestConfiguration
    @ConditionalOnProperty("test.container.enabled", havingValue = "true")
    internal class KafkaTestContainersConfiguration {

        @Container
        val kafkaContainer = KafkaContainer(DockerImageName.parse("confluentinc/cp-kafka:5.4.3"))
            .apply { start() }

        private fun <K, V> MutableMap<K, V>.alsoMap(vararg pairs: Pair<K, V>) = apply { putAll(pairs) }

        @Bean
        fun producerFactory(
            kafkaProperties: KafkaProperties,
        ) = DefaultKafkaProducerFactory<Any, Any>(
            kafkaProperties.buildProducerProperties().alsoMap(
                ProducerConfig.BOOTSTRAP_SERVERS_CONFIG to kafkaContainer.bootstrapServers,
            )
        )
    }

    @Test
    internal fun `should be reading a message`() {
        val now = LocalDateTime.now().format(ofPattern("yyyy-MM-dd HH:mm:ss"))
        moveNotifier.notifyPlayers(Move("Cinka", 101, now))
        // TODO: verify message has been sent to Kafka
    }
}
