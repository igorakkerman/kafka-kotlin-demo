package de.igorakkerman.demo.kafka.kafka

import de.igorakkerman.demo.kafka.application.MoveNotifier
import mu.KotlinLogging
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.support.SendResult
import org.springframework.stereotype.Component
import org.springframework.util.concurrent.ListenableFutureCallback

@Component
class KafkaMoveNotifier(
    private val kafkaTemplate: KafkaTemplate<String, String>,
) : MoveNotifier {
    private val log = KotlinLogging.logger {}

    override fun notifyPlayers(move: String) =
        log.info("Sending message: $move").run {
            kafkaTemplate.sendDefault(move).addCallback(
                object : ListenableFutureCallback<SendResult<String, String>> {
                    override fun onSuccess(result: SendResult<String, String>?) {
                        log.info("Message sent. message: $move, offset: ${result?.recordMetadata?.offset()}")
                    }

                    override fun onFailure(exception: Throwable) {
                        log.info("Unable to send message: $move, reason: ${exception.message}")
                    }
                }
            )
        }
}
