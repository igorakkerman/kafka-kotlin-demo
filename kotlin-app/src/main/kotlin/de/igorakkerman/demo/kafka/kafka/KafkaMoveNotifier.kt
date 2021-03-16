package de.igorakkerman.demo.kafka.kafka

import de.igorakkerman.demo.kafka.application.Move
import de.igorakkerman.demo.kafka.application.MoveNotifier
import mu.KotlinLogging
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.support.SendResult
import org.springframework.stereotype.Component
import org.springframework.util.concurrent.ListenableFutureCallback

@Component
class KafkaMoveNotifier(
    private val kafkaTemplate: KafkaTemplate<String, Move>,
) : MoveNotifier {
    private val log = KotlinLogging.logger {}

    override fun notifyPlayers(move: Move) {
        log.info("Sending move: $move")

        kafkaTemplate.sendDefault(move).addCallback(
            object : ListenableFutureCallback<SendResult<String, Move>> {
                override fun onSuccess(result: SendResult<String, Move>?) =
                    log.info("Move sent. move: $move, offset: ${result?.recordMetadata?.offset()}")

                override fun onFailure(exception: Throwable) =
                    log.info("Unable to send move: $move, reason: ${exception.message}")
            }
        )
    }
}
