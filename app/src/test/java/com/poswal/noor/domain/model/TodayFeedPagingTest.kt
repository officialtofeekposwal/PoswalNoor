package com.poswal.noor.domain.model

import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class TodayFeedPagingTest {

    /**
     * Replicates the linear congruential generator (LCG) order used in ContentDao.getDeterministicPage:
     * (((id * (((:seed * 1103515245 + 12345) & 2147483647) | 1)) + 12345) & 2147483647) ASC
     */
    private fun computeDeterministicOrder(ids: List<Long>, seed: Long): List<Long> {
        val s = (((seed * 1103515245L + 12345L) and 0x7FFFFFFFL) or 1L)
        return ids.sortedBy { id ->
            (((id * s) + 12345L) and 0x7FFFFFFFL)
        }
    }

    /**
     * Replicates ContentRepositoryImpl.getVerseOfTheDay date-seeding formula:
     * (epochDay * 2654435761L) and 0x7FFFFFFF
     */
    private fun getVerseOfTheDayId(availableQuranIds: List<Long>, epochDay: Long): Long {
        val dailySeed = (epochDay * 2654435761L) and 0x7FFFFFFFL
        val ordered = computeDeterministicOrder(availableQuranIds, dailySeed)
        return ordered.first()
    }

    @Test
    fun testVerseOfTheDay_isDeterministicForSameDay() {
        val quranIds = (1L..6236L).toList()
        val epochDay = 20775L // Fixed representative epoch day

        val pick1 = getVerseOfTheDayId(quranIds, epochDay)
        val pick2 = getVerseOfTheDayId(quranIds, epochDay)
        val pick3 = getVerseOfTheDayId(quranIds, epochDay)

        assertEquals("Same epoch day must always produce the exact same Verse of the Day", pick1, pick2)
        assertEquals("Same epoch day must always produce the exact same Verse of the Day", pick2, pick3)
    }

    @Test
    fun testVerseOfTheDay_changesOnNextDay() {
        val quranIds = (1L..6236L).toList()
        val day1 = 20775L
        val day2 = 20776L

        val pickDay1 = getVerseOfTheDayId(quranIds, day1)
        val pickDay2 = getVerseOfTheDayId(quranIds, day2)

        assertNotEquals("Next day must produce a different Verse of the Day", pickDay1, pickDay2)
    }

    @Test
    fun testPagination_hasNoDuplicatesAcrossPagesWithSameSeed() {
        val totalPool = (1L..2000L).toList()
        val seed = 987654321L
        val pageSize = 20
        val totalPages = 10

        val ordered = computeDeterministicOrder(totalPool, seed)
        val collectedPages = mutableListOf<Long>()

        for (page in 0 until totalPages) {
            val offset = page * pageSize
            val pageItems = ordered.drop(offset).take(pageSize)
            assertEquals("Each page must have exactly $pageSize items", pageSize, pageItems.size)

            // Verify no item on this page has appeared in any previous page
            for (item in pageItems) {
                assertTrue("Item $item appeared twice across pages!", !collectedPages.contains(item))
            }
            collectedPages.addAll(pageItems)
        }

        assertEquals(pageSize * totalPages, collectedPages.size)
        assertEquals(pageSize * totalPages, collectedPages.toSet().size)
    }

    @Test
    fun testDifferentSeed_producesDifferentFeedOrder() {
        val totalPool = (1L..500L).toList()
        val seed1 = 11111L
        val seed2 = 22222L

        val order1 = computeDeterministicOrder(totalPool, seed1).take(20)
        val order2 = computeDeterministicOrder(totalPool, seed2).take(20)

        assertNotEquals("Different session seeds must produce different feed shuffles", order1, order2)
    }
}
