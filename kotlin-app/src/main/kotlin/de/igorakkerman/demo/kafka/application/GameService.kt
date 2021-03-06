package de.igorakkerman.demo.kafka.application

import mu.KotlinLogging

class GameService(private val moveNotifier: MoveNotifier) {

    val moves = ArrayList<Move>()

    private val log = KotlinLogging.logger {}

    fun acceptMove(move: Move) {
        log.info("Accepting move: $move")
        moves.add(move)
    }
}

