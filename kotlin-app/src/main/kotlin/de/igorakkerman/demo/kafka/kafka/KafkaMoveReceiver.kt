package de.igorakkerman.demo.kafka.kafka

import de.igorakkerman.demo.kafka.application.MoveReceiver
import mu.KotlinLogging
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import java.util.concurrent.CountDownLatch

@Component
class KafkaMoveReceiver : MoveReceiver {
    private val log = KotlinLogging.logger {}

    val latch = CountDownLatch(1)

    @KafkaListener(id = "openear", topics = ["game"])
    fun listen(message: String) {
        log.info("Message received: $message")
        latch.countDown()
    }

}

