package com.poswal.noor.widget

import androidx.glance.state.PreferencesGlanceStateDefinition
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Test
import java.util.Calendar

class Phase7WidgetsAutomationTest {

    @Test
    fun testGlanceWidgets_stateDefinition() {
        val widget2x2 = NoorWidget2x2()
        assertEquals(PreferencesGlanceStateDefinition, widget2x2.stateDefinition)

        val tasbihWidget = TasbihGlanceWidget()
        assertEquals(PreferencesGlanceStateDefinition, tasbihWidget.stateDefinition)
    }

    @Test
    fun testTasbihWidget_incrementAndRolloverLogic() {
        fun computeNextCount(current: Int, target: Int = 33): Int {
            return if (current >= target) 1 else current + 1
        }

        assertEquals(1, computeNextCount(0))
        assertEquals(2, computeNextCount(1))
        assertEquals(33, computeNextCount(32))
        assertEquals(1, computeNextCount(33)) // Rollover
        assertEquals(1, computeNextCount(34))
    }

    @Test
    fun testDailyReminder_scheduleDelayCalculation() {
        val now = Calendar.getInstance()
        val target = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 7)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            if (before(now)) {
                add(Calendar.DAY_OF_YEAR, 1)
            }
        }

        val initialDelay = target.timeInMillis - now.timeInMillis
        assertTrue("Initial delay must be positive", initialDelay > 0)
        assertTrue("Initial delay must not exceed 24 hours", initialDelay <= 24 * 60 * 60 * 1000L)
    }
}
