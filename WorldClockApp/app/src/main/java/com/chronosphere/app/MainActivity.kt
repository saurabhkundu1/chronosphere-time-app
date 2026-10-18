package com.applify.chronosphere

import android.app.AlertDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.format.DateFormat
import android.view.Gravity
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class MainActivity : AppCompatActivity() {
    private val preferences by lazy { getSharedPreferences(PREFERENCES, MODE_PRIVATE) }
    private val timeViews = linkedMapOf<String, TextView>()
    private val dateViews = linkedMapOf<String, TextView>()
    private lateinit var cityContainer: LinearLayout
    private var updateJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        cityContainer = findViewById(R.id.cityContainer)
        findViewById<Button>(R.id.addCityButton).setOnClickListener { showCityPicker() }
        ensureDefaultCities()
        renderCities()
        updateJob = lifecycleScope.launch {
            while (isActive) {
                refreshTimes()
                delay(1_000)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        refreshTimes()
    }

    override fun onDestroy() {
        updateJob?.cancel()
        super.onDestroy()
    }

    private fun ensureDefaultCities() {
        if (preferences.contains(KEY_CITIES)) return
        saveCities(DEFAULT_CITIES)
    }

    private fun cities(): List<String> = preferences.getString(KEY_CITIES, null)
        ?.split('|')?.filter { it in cityOptions }
        .orEmpty()

    private fun saveCities(cities: List<String>) {
        preferences.edit().putString(KEY_CITIES, cities.distinct().joinToString("|")).apply()
        renderCities()
        WorldClockWidgetProvider.refresh(this)
    }

    private fun renderCities() {
        if (!::cityContainer.isInitialized) return
        cityContainer.removeAllViews()
        timeViews.clear()
        dateViews.clear()
        cities().forEach { zoneId ->
            val card = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL
                setPadding(dp(16), dp(12), dp(12), dp(12))
                setBackgroundResource(R.drawable.city_card_background)
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply { bottomMargin = dp(10) }
            }
            val details = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
            val cityLabel = TextView(this).apply {
                text = cityName(zoneId)
                textSize = 17f
                setTextColor(getColor(android.R.color.white))
                setTypeface(typeface, android.graphics.Typeface.BOLD)
            }
            val timeLabel = TextView(this).apply {
                textSize = 26f
                setTextColor(getColor(android.R.color.white))
                setTypeface(typeface, android.graphics.Typeface.BOLD)
            }
            val dateLabel = TextView(this).apply {
                textSize = 13f
                setTextColor(0xFFCFD8FF.toInt())
            }
            details.addView(cityLabel)
            details.addView(timeLabel)
            details.addView(dateLabel)
            card.addView(details, LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f))
            val remove = TextView(this).apply {
                text = "×"
                contentDescription = "Remove ${cityName(zoneId)}"
                textSize = 28f
                gravity = Gravity.CENTER
                setTextColor(getColor(android.R.color.white))
                setPadding(dp(12), dp(4), dp(4), dp(4))
                setOnClickListener { saveCities(cities().filterNot { it == zoneId }) }
            }
            card.addView(remove)
            cityContainer.addView(card)
            timeViews[zoneId] = timeLabel
            dateViews[zoneId] = dateLabel
        }
        findViewById<TextView>(R.id.emptyMessage)?.visibility =
            if (cities().isEmpty()) android.view.View.VISIBLE else android.view.View.GONE
        refreshTimes()
    }

    private fun refreshTimes() {
        val now = Date()
        val timePattern = if (DateFormat.is24HourFormat(this)) "HH:mm:ss" else "h:mm:ss a"
        val timeFormatter = SimpleDateFormat(timePattern, Locale.getDefault())
        val dateFormatter = SimpleDateFormat("EEE, d MMM · z", Locale.getDefault())
        cities().forEach { zoneId ->
            val zone = TimeZone.getTimeZone(zoneId)
            timeFormatter.timeZone = zone
            dateFormatter.timeZone = zone
            timeViews[zoneId]?.text = timeFormatter.format(now)
            dateViews[zoneId]?.text = "${dateFormatter.format(now)}  ·  ${zoneId.replace('_', ' ')}"
        }
    }

    private fun showCityPicker() {
        val search = EditText(this).apply {
            hint = "Search cities or time zones"
            setSingleLine(true)
            setPadding(dp(16), dp(10), dp(16), dp(10))
        }
        val list = ListView(this)
        val dialogContent = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            addView(search)
            addView(list, LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, dp(360)))
        }
        val dialog = AlertDialog.Builder(this)
            .setTitle("Add a time zone")
            .setView(dialogContent)
            .setNegativeButton("Close", null)
            .create()
        var filtered = cityOptions
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, filtered.map(::cityName))
        list.adapter = adapter
        search.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) = Unit
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                val query = s?.toString()?.trim().orEmpty()
                filtered = if (query.isEmpty()) cityOptions else cityOptions.filter {
                    cityName(it).contains(query, true) || it.contains(query.replace(' ', '_'), true)
                }
                adapter.clear()
                adapter.addAll(filtered.map(::cityName))
            }
            override fun afterTextChanged(s: Editable?) = Unit
        })
        list.setOnItemClickListener { _, _, position, _ ->
            val selected = filtered.getOrNull(position) ?: return@setOnItemClickListener
            if (selected !in cities()) saveCities(cities() + selected)
            dialog.dismiss()
        }
        dialog.show()
    }

    private fun cityName(zoneId: String): String = zoneId.substringAfterLast('/').replace('_', ' ')
        .replace("St Kitts", "St. Kitts")

    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()

    companion object {
        const val PREFERENCES = "world_clock_preferences"
        const val KEY_CITIES = "saved_time_zones"
        val DEFAULT_CITIES = listOf(
            "America/New_York", "Europe/London", "Asia/Tokyo", "Australia/Sydney", "Asia/Dubai"
        )
        val cityOptions = listOf(
            "Pacific/Honolulu", "America/Anchorage", "America/Los_Angeles", "America/Vancouver",
            "America/Phoenix", "America/Denver", "America/Chicago", "America/Mexico_City",
            "America/Toronto", "America/New_York", "America/Caracas", "America/Sao_Paulo",
            "America/Argentina/Buenos_Aires", "Atlantic/Reykjavik", "Europe/London", "Europe/Dublin",
            "Europe/Lisbon", "Europe/Paris", "Europe/Amsterdam", "Europe/Berlin", "Europe/Rome",
            "Europe/Madrid", "Europe/Zurich", "Europe/Athens", "Europe/Helsinki", "Europe/Moscow",
            "Africa/Cairo", "Africa/Lagos", "Africa/Nairobi", "Africa/Johannesburg", "Asia/Jerusalem",
            "Asia/Riyadh", "Asia/Dubai", "Asia/Karachi", "Asia/Kolkata", "Asia/Kathmandu",
            "Asia/Dhaka", "Asia/Bangkok", "Asia/Jakarta", "Asia/Singapore", "Asia/Hong_Kong",
            "Asia/Shanghai", "Asia/Taipei", "Asia/Seoul", "Asia/Tokyo", "Asia/Manila",
            "Asia/Kuala_Lumpur", "Asia/Colombo", "Asia/Kabul", "Asia/Tashkent", "Asia/Almaty",
            "Australia/Perth", "Australia/Adelaide", "Australia/Sydney", "Australia/Brisbane",
            "Australia/Melbourne", "Pacific/Auckland", "Pacific/Fiji"
        ).sortedBy { it.substringAfterLast('/').replace('_', ' ') }
    }
}
