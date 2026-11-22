package com.applify.chronosphere

import android.app.Application
import android.content.Context
import androidx.core.content.edit
import androidx.lifecycle.AndroidViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

interface CityPreferences {
    fun contains(key: String): Boolean
    fun getString(key: String, defaultValue: String?): String?
    fun putString(key: String, value: String)
}

class SharedPreferencesCityPreferences(context: Context) : CityPreferences {
    private val prefs = context.getSharedPreferences(MainActivity.PREFERENCES, Context.MODE_PRIVATE)

    override fun contains(key: String): Boolean = prefs.contains(key)
    override fun getString(key: String, defaultValue: String?): String? = prefs.getString(key, defaultValue)
    override fun putString(key: String, value: String) {
        prefs.edit { putString(key, value) }
    }
}

class WorldClockState(
    private val cityPrefs: CityPreferences,
    private val onCitiesChanged: () -> Unit = {}
) {
    private val _savedCities = MutableStateFlow<List<String>>(emptyList())
    val savedCities: StateFlow<List<String>> = _savedCities.asStateFlow()

    init {
        loadCities()
    }

    fun loadCities() {
        if (!cityPrefs.contains(MainActivity.KEY_CITIES)) {
            saveCities(MainActivity.DEFAULT_CITIES)
        } else {
            val stored = cityPrefs.getString(MainActivity.KEY_CITIES, null)
                ?.split('|')
                ?.filter { it in MainActivity.cityOptions }
                .orEmpty()
            _savedCities.value = stored
        }
    }

    fun addCity(zoneId: String): Boolean {
        if (zoneId in MainActivity.cityOptions && zoneId !in _savedCities.value) {
            val updated = _savedCities.value + zoneId
            saveCities(updated)
            return true
        }
        return false
    }

    fun removeCity(zoneId: String) {
        val updated = _savedCities.value.filter { it != zoneId }
        saveCities(updated)
    }

    fun reorderCities(fromIndex: Int, toIndex: Int) {
        val list = _savedCities.value.toMutableList()
        if (fromIndex in list.indices && toIndex in list.indices && fromIndex != toIndex) {
            val item = list.removeAt(fromIndex)
            list.add(toIndex, item)
            saveCities(list)
        }
    }

    fun saveCities(cities: List<String>) {
        val distinct = cities.distinct().filter { it in MainActivity.cityOptions }
        cityPrefs.putString(MainActivity.KEY_CITIES, distinct.joinToString("|"))
        _savedCities.value = distinct
        onCitiesChanged()
    }
}

class WorldClockViewModel(application: Application) : AndroidViewModel(application) {
    private val state = WorldClockState(
        cityPrefs = SharedPreferencesCityPreferences(application),
        onCitiesChanged = {
            try {
                WorldClockWidgetProvider.refresh(getApplication())
            } catch (_: Exception) {
            }
        }
    )

    val savedCities: StateFlow<List<String>> = state.savedCities

    fun loadCities() = state.loadCities()
    fun addCity(zoneId: String) = state.addCity(zoneId)
    fun removeCity(zoneId: String) = state.removeCity(zoneId)
    fun reorderCities(fromIndex: Int, toIndex: Int) = state.reorderCities(fromIndex, toIndex)
    fun saveCities(cities: List<String>) = state.saveCities(cities)
}
