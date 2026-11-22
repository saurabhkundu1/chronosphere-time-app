package com.applify.chronosphere

import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class WorldClockStateTest {

    private class FakeCityPreferences : CityPreferences {
        private val data = mutableMapOf<String, String>()

        override fun contains(key: String): Boolean = data.containsKey(key)
        override fun getString(key: String, defaultValue: String?): String? = data[key] ?: defaultValue
        override fun putString(key: String, value: String) {
            data[key] = value
        }
    }

    @Test
    fun loadCities_populatesDefaultCitiesWhenEmpty() {
        val prefs = FakeCityPreferences()
        val state = WorldClockState(prefs)

        assertEquals(MainActivity.DEFAULT_CITIES, state.savedCities.value)
    }

    @Test
    fun addCity_appendsValidZoneIdAndRejectsDuplicates() {
        val prefs = FakeCityPreferences()
        val state = WorldClockState(prefs)

        val added = state.addCity("Europe/Paris")
        assertTrue(added)
        assertTrue(state.savedCities.value.contains("Europe/Paris"))

        // Duplicate
        val duplicateAdded = state.addCity("Europe/Paris")
        assertFalse(duplicateAdded)

        // Invalid zone not in cityOptions
        val invalidAdded = state.addCity("Mars/Olympus_Mons")
        assertFalse(invalidAdded)
    }

    @Test
    fun removeCity_removesSelectedZone() {
        val prefs = FakeCityPreferences()
        val state = WorldClockState(prefs)

        state.removeCity("Europe/London")
        assertFalse(state.savedCities.value.contains("Europe/London"))
    }

    @Test
    fun reorderCities_movesItemToNewPosition() {
        val prefs = FakeCityPreferences()
        val state = WorldClockState(prefs)

        val initialFirst = state.savedCities.value[0]
        val initialSecond = state.savedCities.value[1]

        state.reorderCities(0, 1)
        assertEquals(initialSecond, state.savedCities.value[0])
        assertEquals(initialFirst, state.savedCities.value[1])
    }
}
