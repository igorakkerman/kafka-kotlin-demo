package de.igorakkerman.demo.kafka.application

interface MoveMessageProducer {
    fun sendMessage(message: String)
}
