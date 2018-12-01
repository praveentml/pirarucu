package pirarucu.search

import pirarucu.board.Color
import pirarucu.game.GameConstants
import pirarucu.util.Utils
import kotlin.math.max

class SearchOptions {

    // Search control
    var stop = false
    var startTime = 0L
    var maxSearchTimeLimit = 0L
    var minSearchTimeLimit = 0L

    var minSearchTime = 0L
    var maxSearchTime = 0L
    var searchTimeIncrement = 0L

    var depth = GameConstants.MAX_PLIES - 1

    var movesToGo = 0L

    var whiteTime = 0L
    var blackTime = 0L
    var whiteIncrement = 0L
    var blackIncrement = 0L

    fun setTime(color: Int) {
        val totalTime = if (color == Color.WHITE) {
            whiteTime
        } else {
            blackTime
        }
        val moves = when {
            movesToGo != 0L -> movesToGo + MIN_GAME_MOVES
            else -> GAME_MOVES
        }

        minSearchTime = totalTime / moves
        maxSearchTime = minSearchTime * MAX_TIME_RATIO

        searchTimeIncrement = max(1, (maxSearchTime - minSearchTime) / INCREMENT_RATIO)
    }

    fun startControl() {
        stop = false
        startTime = Utils.specific.currentTimeMillis()
        maxSearchTimeLimit = startTime + maxSearchTime
        minSearchTimeLimit = startTime + minSearchTime
    }

    companion object {
        // NOTE: this should be equal or below MIN_GAME_MOVES
        private const val MAX_TIME_RATIO = 5L

        private const val GAME_MOVES = 40L
        private const val MIN_GAME_MOVES = 15L

        private const val INCREMENT_RATIO = 40
    }
}
