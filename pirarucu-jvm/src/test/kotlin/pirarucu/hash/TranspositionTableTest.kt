package pirarucu.hash

import pirarucu.board.factory.BoardFactory
import pirarucu.eval.EvalConstants
import pirarucu.move.Move
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class TranspositionTableTest {
    private val transpositionTable = TranspositionTable(1)

    @BeforeTest
    fun setup() {
        transpositionTable.reset()
        transpositionTable.baseDepth = 19
    }

    @Test
    fun testTableBits() {
        for (size in 0 until 15) {
            val sizeMb = 1 shl size
            val tableBits = TranspositionTable.calculateTableBits(sizeMb)
            assertEquals(size + 16, tableBits)
        }
    }

    @Test
    fun testTableElements() {
        for (size in 0 until 15) {
            val sizeMb = 1 shl size
            val tableBits = TranspositionTable.calculateTableBits(sizeMb)
            val maxIndex = TranspositionTable.calculateTableElements(tableBits)
            assertEquals(sizeMb shl 16, maxIndex)
        }
    }

    @Test
    fun testIndexShift() {
        for (size in 0 until 15) {
            val sizeMb = 1 shl size
            val tableBits = TranspositionTable.calculateTableBits(sizeMb)
            val indexShift = TranspositionTable.calculateIndexShift(tableBits)

            assertEquals(64 - size - 16, indexShift)
        }
    }

    @Test
    fun testIndex() {
        for (size in 0 until 15) {
            val sizeMb = 1 shl size
            val tableBits = TranspositionTable.calculateTableBits(sizeMb)
            val indexShift = TranspositionTable.calculateIndexShift(tableBits)
            val maxIndex = TranspositionTable.calculateTableElements(tableBits)
            val index0 = TranspositionTable.getIndex(0, indexShift)
            val index1 = TranspositionTable.getIndex(Long.MAX_VALUE, indexShift)
            val index2 = TranspositionTable.getIndex(-1, indexShift)

            assertEquals(index0, 0)
            assertEquals(index1, maxIndex / 2 - 1)
            assertEquals(index2, maxIndex - 1)
        }
    }

    @Test
    fun testMaxIndex() {
        for (size in 0 until 15) {
            val sizeMb = 1 shl size
            val tableBits = TranspositionTable.calculateTableBits(sizeMb)
            val indexShift = TranspositionTable.calculateIndexShift(tableBits)
            val totalElements = TranspositionTable.calculateTableElements(tableBits)

            val index0 = TranspositionTable.getIndex(0, indexShift)
            val index1 = TranspositionTable.getIndex(Long.MAX_VALUE, indexShift)
            val index2 = TranspositionTable.getIndex(-1, indexShift)

            val maxIndex0 = TranspositionTable.getMaxIndex(index0, totalElements)
            val maxIndex1 = TranspositionTable.getMaxIndex(index1, totalElements)
            val maxIndex2 = TranspositionTable.getMaxIndex(index2, totalElements)

            assertEquals(index0 + HashConstants.TRANSPOSITION_TABLE_BUCKET_SIZE, maxIndex0)
            assertEquals(index1 + HashConstants.TRANSPOSITION_TABLE_BUCKET_SIZE, maxIndex1)
            assertEquals(index2 + 1, maxIndex2)
        }
    }

    @Test
    fun testEmptyTable() {
        val board = BoardFactory.getBoard("4k3/8/8/8/8/8/8/4K3 b - -")
        val index = transpositionTable.findEntry(board)
        assertEquals(HashConstants.EMPTY_INFO, index)
    }

    @Test
    fun testSave() {
        val board = BoardFactory.getBoard("4k3/8/8/8/8/8/8/4K3 b - -")
        transpositionTable.save(board, 23, 5, HashConstants.SCORE_TYPE_BOUND_UPPER, 7, 9, 11)

        val info = transpositionTable.findEntry(board)
        assertNotEquals(HashConstants.EMPTY_INFO, info)
        assertEquals(23, transpositionTable.getEval(info))
        assertEquals(5, transpositionTable.getScore(info, 0))
        assertEquals(HashConstants.SCORE_TYPE_BOUND_UPPER, transpositionTable.getScoreType(info))
        assertEquals(7, transpositionTable.getDepth(info))
        assertEquals(11, transpositionTable.getMove(info))
    }

    @Test
    fun testUpdateEval() {
        val board = BoardFactory.getBoard("4k3/8/8/8/8/8/8/4K3 b - -")
        transpositionTable.save(board, 19, 0, HashConstants.SCORE_TYPE_BOUND_UPPER, 0, 0, 0)
        transpositionTable.save(board, 23, 0, HashConstants.SCORE_TYPE_BOUND_UPPER, 0, 0, 0)

        val info = transpositionTable.findEntry(board)
        assertNotEquals(HashConstants.EMPTY_INFO, info)
        assertEquals(23, transpositionTable.getEval(info))
    }

    @Test
    fun testSaveMateScore() {
        val board = BoardFactory.getBoard("4k3/8/8/8/8/8/8/4K3 b - -")
        transpositionTable.save(board, 23, EvalConstants.SCORE_MAX, HashConstants.SCORE_TYPE_BOUND_UPPER, 0, 0, 0)

        val info = transpositionTable.findEntry(board)
        assertNotEquals(HashConstants.EMPTY_INFO, info)
        assertEquals(EvalConstants.SCORE_MAX, transpositionTable.getScore(info, 0))
        assertEquals(EvalConstants.SCORE_MAX - 1, transpositionTable.getScore(info, 1))
    }

    @Test
    fun testUpdateScore() {
        val board = BoardFactory.getBoard("4k3/8/8/8/8/8/8/4K3 b - -")
        transpositionTable.save(board, 23, 3, HashConstants.SCORE_TYPE_BOUND_UPPER, 0, 0, 0)
        transpositionTable.save(board, 23, 5, HashConstants.SCORE_TYPE_BOUND_UPPER, 0, 0, 0)

        val info = transpositionTable.findEntry(board)
        assertNotEquals(HashConstants.EMPTY_INFO, info)
        assertEquals(5, transpositionTable.getScore(info, 0))
    }

    @Test
    fun testUpdateDepth() {
        val board = BoardFactory.getBoard("4k3/8/8/8/8/8/8/4K3 b - -")
        transpositionTable.save(board, 23, 0, 0, 1, 0, 0)
        transpositionTable.save(board, 23, 0, 0, 2, 0, 0)

        val info = transpositionTable.findEntry(board)
        assertNotEquals(HashConstants.EMPTY_INFO, info)
        assertEquals(2, transpositionTable.getDepth(info))
    }

    @Test
    fun testBaseDepth() {
        val board = BoardFactory.getBoard("4k3/8/8/8/8/8/8/4K3 b - -")
        transpositionTable.save(board, 23, 0, HashConstants.SCORE_TYPE_BOUND_UPPER, 11, 0, 0)

        val info = transpositionTable.findEntry(board)
        assertNotEquals(HashConstants.EMPTY_INFO, info)
        assertEquals(11, transpositionTable.getDepth(info))
        transpositionTable.baseDepth += 2
        assertEquals(9, transpositionTable.getDepth(info))
    }

    @Test
    fun testUpdateMove() {
        val board = BoardFactory.getBoard("4k3/8/8/8/8/8/8/4K3 b - -")
        transpositionTable.save(board, 23, 0, HashConstants.SCORE_TYPE_BOUND_UPPER, 0, 0, Move.NULL)
        transpositionTable.save(board, 23, 0, HashConstants.SCORE_TYPE_BOUND_UPPER, 0, 0, Move.NULL)
        transpositionTable.save(board, 23, 0, HashConstants.SCORE_TYPE_BOUND_UPPER, 0, 0, 1)

        val info = transpositionTable.findEntry(board)
        assertNotEquals(HashConstants.EMPTY_INFO, info)
        assertEquals(1, transpositionTable.getMove(info))
    }
}