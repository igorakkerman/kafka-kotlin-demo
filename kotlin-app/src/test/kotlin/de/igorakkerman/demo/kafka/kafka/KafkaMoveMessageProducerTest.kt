package de.igorakkerman.demo.kafka.kafka

import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter.ofPattern

@SpringBootTest(classes = [KafkaMoveMessageProducer::class, KafkaProducerConfig::class])
internal class KafkaMoveMessageProducerTest(
    @Autowired
    private val producer: KafkaMoveMessageProducer,
) {

    @Test
    internal fun `should be able to send a message`() {
        producer.sendMessage("${LocalDateTime.now().format(ofPattern("yyyy-MM-dd HH:mm:ss"))} Cinki sagt 'Hallo Kafka!'")
    }
}
