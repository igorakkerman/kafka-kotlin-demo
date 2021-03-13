package de.igorakkerman.demo.kafka.application

class GameService(private val moveNotifier: MoveNotifier) {

    val moves = ArrayList<Move>()

    fun registerMove(move: Move) {
        moves.add(move)
    }
}

