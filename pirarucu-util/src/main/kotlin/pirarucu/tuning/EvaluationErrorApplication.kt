package pirarucu.tuning

import pirarucu.tuning.evaluator.EvaluationEvaluator
import pirarucu.util.epd.EpdFileLoader
import pirarucu.util.epd.EpdInfo
import pirarucu.util.epd.position.InvalidPositionFilter
import java.util.concurrent.ExecutionException
import kotlin.system.measureTimeMillis

object EvaluationErrorApplication {

    private const val THREADS = 6

    @Throws(ExecutionException::class, InterruptedException::class)
    @JvmStatic
    fun main(args: Array<String>) {
        val list = mutableListOf<EpdInfo>()
        val fileLoader = EpdFileLoader("g:\\chess\\epds\\texel-sets\\zuri_quiet_labeled.epd")
        list.addAll(fileLoader.epdList)
        val epdList = InvalidPositionFilter(THREADS).filter(list)
        println("Using ${epdList.size} positions")
        val evaluator = EvaluationEvaluator(THREADS)
        val timeTaken = measureTimeMillis {
            evaluator.evaluate(epdList)
        }
        println("Evaluation error " + ErrorUtil.calculate(epdList))
        println("Eval time taken $timeTaken")
    }
}
