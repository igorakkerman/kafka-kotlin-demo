package de.igorakkerman.demo.kafka.kafka

import de.igorakkerman.demo.kafka.application.GameService
import de.igorakkerman.demo.kafka.application.Move
import de.igorakkerman.demo.kafka.application.MoveReceiver
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component
import java.util.concurrent.CountDownLatch

@Component
class KafkaMoveReceiver(
    val gameService: GameService
) : MoveReceiver {
    private val log = KotlinLogging.logger {}

    val latch = CountDownLatch(1)

    @KafkaListener(topics = ["game"])
    fun listen(move: Move) {
        latch.countDown()
        log.info("Move received: $move, new latch count: ${latch.count}")
        gameService.registerMove(move)
    }
}

