package de.igorakkerman.demo.kafka.kafka

import de.igorakkerman.demo.kafka.application.MoveMessageProducer
import mu.KotlinLogging
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.support.SendResult
import org.springframework.stereotype.Component
import org.springframework.util.concurrent.ListenableFutureCallback

@Component
class KafkaMoveMessageProducer(
    @Autowired
    private val kafkaTemplate: KafkaTemplate<String, String>,

    @Value("\${spring.kafka.template.default-topic}")
    private val topicName: String,
) : MoveMessageProducer {
    private val log = KotlinLogging.logger {}

    override fun sendMessage(message: String) =
        log.info("Sending message: $message").run {
//            kafkaTemplate.sendDefault(message).addCallback(
            kafkaTemplate.send(topicName, message).addCallback(
                // synchronous
                //    fun sendMessage(message: String) = kafkaTemplate.send(topicName, message)
                object : ListenableFutureCallback<SendResult<String, String>> {
                    override fun onSuccess(result: SendResult<String, String>?) {
                        log.info("Message sent. message: $message, offset: ${result?.recordMetadata?.offset()}")
                    }

                    override fun onFailure(exception: Throwable) {
                        log.info("Unable to send message: $message, reason: ${exception.message}")
                    }
                }
            )
        }.also { log.info("Message sending done.") }
}
