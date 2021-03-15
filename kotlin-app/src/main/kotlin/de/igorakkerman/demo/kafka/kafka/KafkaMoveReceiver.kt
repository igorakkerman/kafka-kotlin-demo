package de.igorakkerman.demo.kafka.kafka

import de.igorakkerman.demo.kafka.application.GameService
import de.igorakkerman.demo.kafka.application.Move
import de.igorakkerman.demo.kafka.application.MoveReceiver
import mu.KotlinLogging
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.stereotype.Component

@Component
class KafkaMoveReceiver(
    val gameService: GameService
) : MoveReceiver {
    private val log = KotlinLogging.logger {}

    @KafkaListener(topics = ["game"])
    fun listen(move: Move) {
        log.info("Move received: $move")
        gameService.acceptMove(move)
    }
}

