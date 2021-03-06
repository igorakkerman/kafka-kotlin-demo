package de.igorakkerman.demo.kafka.kafka

import de.igorakkerman.demo.kafka.application.MoveMessageProducer
import mu.KotlinLogging
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.support.SendResult
import org.springframework.stereotype.Component
import org.springframework.util.concurrent.ListenableFutureCallback

@Component
class KafkaMoveMessageProducer(
    private val kafkaTemplate: KafkaTemplate<String, String>,
) : MoveMessageProducer {
    private val log = KotlinLogging.logger {}

    override fun sendMessage(message: String) =
        log.info("Sending message: $message").run {
            kafkaTemplate.sendDefault(message).addCallback(
                object : ListenableFutureCallback<SendResult<String, String>> {
                    override fun onSuccess(result: SendResult<String, String>?) {
                        log.info("Message sent. message: $message, offset: ${result?.recordMetadata?.offset()}")
                    }

                    override fun onFailure(exception: Throwable) {
                        log.info("Unable to send message: $message, reason: ${exception.message}")
                    }
                }
            )
        }
}
