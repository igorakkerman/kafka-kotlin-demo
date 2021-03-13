package de.igorakkerman.demo.kafka.application

data class Move(
    val playerName: String,
    val currentValue: Int,
    val dateTime: String,
)
