package de.igorakkerman.demo.kafka.application

interface MoveNotifier {
    fun notifyPlayers(move: Move)
}
