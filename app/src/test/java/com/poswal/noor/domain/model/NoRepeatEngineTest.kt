package com.poswal.noor.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class NoRepeatEngineTest {

    /**
     * Simulated in-memory No-Repeat sliding window engine matching the SQL logic.
     */
    class SimulatedNoRepeatEngine(
        val totalPoolSize: Int = 1000,
        val windowSize: Int = 200
    ) {
        val seenHistory = mutableListOf<Long>()

        fun getNextItem(availableIds: List<Long>, seed: Long, step: Int): Long {
            // Unseen items first
            val recentlySeen = seenHistory.takeLast(windowSize).toSet()
            val candidatePool = availableIds.sortedWith(
                compareBy<Long> { id -> if (recentlySeen.contains(id)) 1 else 0 }
                    .thenBy { id -> ((id * 1103515245L + seed + step) and 0x7FFFFFFF) }
            )
            val selected = candidatePool.first()
            seenHistory.add(selected)
            return selected
        }
    }

    @Test
    fun testNoDuplicatesWithinWindow_in1000ConsecutivePicks() {
        val engine = SimulatedNoRepeatEngine(totalPoolSize = 500, windowSize = 200)
        val pool = (1L..500L).toList()
        val stream = mutableListOf<Long>()

        for (i in 0 until 1000) {
            val item = engine.getNextItem(pool, seed = 12345L, step = i)
            // Check that in the last 200 items, this item hasn't appeared
            val recentWindow = stream.takeLast(200)
            assertTrue(
                "Item $item repeated within 200-card window at index $i!",
                !recentWindow.contains(item)
            )
            stream.add(item)
        }
        assertEquals(1000, stream.size)
    }

    @Test
    fun testFilteredPool_withNoRepeat() {
        // Pool filtered down to 50 items (e.g. topic filter)
        val engine = SimulatedNoRepeatEngine(totalPoolSize = 50, windowSize = 40)
        val pool = (100L..150L).toList()
        val stream = mutableListOf<Long>()

        for (i in 0 until 200) {
            val item = engine.getNextItem(pool, seed = 999L, step = i)
            val recentWindow = stream.takeLast(40)
            assertTrue(!recentWindow.contains(item))
            stream.add(item)
        }
        assertEquals(200, stream.size)
    }

    @Test
    fun testSmallFilteredPool_cyclesGracefully() {
        // Very small topic pool (e.g. 5 items)
        val engine = SimulatedNoRepeatEngine(totalPoolSize = 5, windowSize = 4)
        val pool = listOf(1L, 2L, 3L, 4L, 5L)
        val stream = mutableListOf<Long>()

        for (i in 0 until 20) {
            val item = engine.getNextItem(pool, seed = 777L, step = i)
            val recentWindow = stream.takeLast(4)
            assertTrue(!recentWindow.contains(item))
            stream.add(item)
        }
        assertEquals(20, stream.size)
    }

    @Test
    fun testDeterministicSeed_generatesIdenticalOrder() {
        val engine1 = SimulatedNoRepeatEngine(totalPoolSize = 100, windowSize = 50)
        val engine2 = SimulatedNoRepeatEngine(totalPoolSize = 100, windowSize = 50)
        val pool = (1L..100L).toList()

        val list1 = (0 until 30).map { engine1.getNextItem(pool, seed = 555L, step = it) }
        val list2 = (0 until 30).map { engine2.getNextItem(pool, seed = 555L, step = it) }

        assertEquals(list1, list2)
    }
}
