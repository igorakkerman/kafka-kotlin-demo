package de.igorakkerman.demo.kafka.application

import mu.KotlinLogging

class GameService(private val moveNotifier: MoveNotifier) {

    val moves = ArrayList<Move>()

    private val log = KotlinLogging.logger {}

    fun registerMove(move: Move) {
        log.info("Registering move: $move")
        moves.add(move)
    }
}

