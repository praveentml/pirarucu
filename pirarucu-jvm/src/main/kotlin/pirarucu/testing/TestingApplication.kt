package pirarucu.benchmark

import pirarucu.board.factory.BoardFactory
import pirarucu.eval.EvalConstants
import pirarucu.hash.TranspositionTable
import pirarucu.search.MainSearch
import pirarucu.search.PrincipalVariation
import pirarucu.search.SearchInfo
import pirarucu.search.SearchOptions
import pirarucu.uci.UciOutput
import pirarucu.util.EpdFileLoader
import pirarucu.util.Utils
import java.util.concurrent.ExecutionException

object TestingApplication {

    @Throws(ExecutionException::class, InterruptedException::class)
    @JvmStatic
    fun main(args: Array<String>) {
        val startTime = Utils.specific.currentTimeMillis()
        val fileLoader = EpdFileLoader("G:/chess/epds/STS/STS.epd")
        val testScore = test(fileLoader, 3)
        val timeTaken = Utils.specific.currentTimeMillis() - startTime
        println("Time taken (ms) $timeTaken")
        println("Test score $testScore.")
    }

    fun test(testFile: EpdFileLoader, depth: Int): Int {
        EvalConstants.PAWN_EVAL_CACHE = false
        UciOutput.silent = true

        var testScore = 0
        val mainSearch = MainSearch()
        val searchOptions = SearchOptions()
        searchOptions.depth = 13
        searchOptions.minSearchTimeLimit = 60000L
        searchOptions.maxSearchTimeLimit = 60000L
        searchOptions.searchTimeIncrement = 1000L
        val searchInfo = SearchInfo()
        for (epdInfo in testFile.getEpdInfoList()) {
            val board = BoardFactory.getBoard(epdInfo.fenPosition)
            TranspositionTable.reset()
            searchOptions.stop = false

            mainSearch.search(board, searchInfo, searchOptions)

            val score = epdInfo.getMoveScore(board, searchInfo.bestMove)

            testScore += score
        }

        return testScore
    }
}
