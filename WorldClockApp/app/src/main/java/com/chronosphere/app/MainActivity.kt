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
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.google.android.material.card.MaterialCardView
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

class MainActivity : AppCompatActivity() {
    private val preferences by lazy { getSharedPreferences(PREFERENCES, MODE_PRIVATE) }
    private val timeViews = linkedMapOf<String, TextView>()
    private val dateViews = linkedMapOf<String, TextView>()
    private lateinit var cityContainer: LinearLayout
    private lateinit var localTimeText: TextView
    private lateinit var localDateText: TextView
    private var updateJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        cityContainer = findViewById(R.id.cityContainer)
        localTimeText = findViewById(R.id.localTimeText)
        localDateText = findViewById(R.id.localDateText)
        findViewById<Button>(R.id.addCityButton).setOnClickListener { showCityPicker() }
        findViewById<Button>(R.id.meetingPlannerButton).setOnClickListener { showMeetingPlanner() }
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
        WorldClockWidgetProvider.refresh(this)
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
            val card = MaterialCardView(this).apply {
                radius = dp(20).toFloat()
                cardElevation = dp(1).toFloat()
                setCardBackgroundColor(getColor(R.color.chrono_surface))
                strokeColor = getColor(R.color.chrono_card_stroke)
                strokeWidth = dp(1)
                layoutParams = LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
                ).apply { bottomMargin = dp(10) }
            }
            val row = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL
                setPadding(dp(16), dp(14), dp(10), dp(14))
                minimumHeight = dp(92)
            }
            val details = LinearLayout(this).apply { orientation = LinearLayout.VERTICAL }
            val cityLabel = TextView(this).apply {
                text = cityName(zoneId)
                textSize = 17f
                setTextColor(getColor(R.color.chrono_text))
                setTypeface(typeface, android.graphics.Typeface.BOLD)
            }
            val timeLabel = TextView(this).apply {
                textSize = 29f
                gravity = Gravity.END or Gravity.CENTER_VERTICAL
                setTextColor(getColor(R.color.chrono_card_time))
                setTypeface(typeface, android.graphics.Typeface.BOLD)
            }
            val dateLabel = TextView(this).apply {
                textSize = 12f
                setTextColor(getColor(R.color.chrono_card_date))
                setPadding(0, dp(4), 0, 0)
            }
            details.addView(cityLabel)
            details.addView(dateLabel)
            row.addView(details, LinearLayout.LayoutParams(0, ViewGroup.LayoutParams.WRAP_CONTENT, 1f))
            row.addView(timeLabel, LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT))
            val remove = TextView(this).apply {
                text = "×"
                contentDescription = "Remove ${cityName(zoneId)}"
                textSize = 23f
                gravity = Gravity.CENTER
                setTextColor(getColor(R.color.chrono_muted))
                setPadding(dp(10), dp(6), dp(4), dp(6))
                setOnClickListener { saveCities(cities().filterNot { it == zoneId }) }
            }
            row.addView(remove)
            card.addView(row)
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
        val timePattern = if (DateFormat.is24HourFormat(this)) "HH:mm" else "h:mm a"
        val timeFormatter = SimpleDateFormat(timePattern, Locale.getDefault())
        val dateFormatter = SimpleDateFormat("EEE, d MMM · z", Locale.getDefault())
        val localZone = TimeZone.getDefault()
        timeFormatter.timeZone = localZone
        dateFormatter.timeZone = localZone
        localTimeText.text = timeFormatter.format(now)
        localDateText.text = getString(R.string.date_device, dateFormatter.format(now))
        cities().forEach { zoneId ->
            val zone = TimeZone.getTimeZone(zoneId)
            timeFormatter.timeZone = zone
            dateFormatter.timeZone = zone
            timeViews[zoneId]?.text = timeFormatter.format(now)
            dateViews[zoneId]?.text = getString(R.string.date_city, dateFormatter.format(now), zoneId.replace('_', ' '))
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

    private fun showMeetingPlanner() {
        val zones = (listOf(TimeZone.getDefault().id) + cities()).distinct()
        val firstSlot = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            val minutesToNextQuarter = (15 - get(Calendar.MINUTE) % 15) % 15
            add(Calendar.MINUTE, if (minutesToNextQuarter == 0) 15 else minutesToNextQuarter)
        }
        val candidates = (0 until 14 * 96).mapNotNull { index ->
            val time = firstSlot.timeInMillis + index * 15 * 60_000L
            val overlap = zones.count { isWorkingTime(time, TimeZone.getTimeZone(it)) }
            if (overlap > 0) time to overlap else null
        }
        val bestOverlap = candidates.maxOfOrNull { it.second } ?: 0
        val best = candidates.filter { it.second == bestOverlap }.fold(mutableListOf<Pair<Long, Int>>()) { slots, candidate ->
            if (slots.size < 5 && slots.none { candidate.first - it.first < 30 * 60_000L }) slots.add(candidate)
            slots
        }
        val content = LinearLayout(this).apply {
            orientation = LinearLayout.VERTICAL
            setPadding(dp(20), dp(8), dp(20), dp(8))
        }
        val timePattern = if (DateFormat.is24HourFormat(this)) "EEE, d MMM · HH:mm" else "EEE, d MMM · h:mm a"
        val formatter = SimpleDateFormat(timePattern, Locale.getDefault()).apply {
            timeZone = TimeZone.getDefault()
        }
        if (best.isEmpty()) {
            content.addView(TextView(this).apply { text = getString(R.string.meeting_empty) })
        } else {
            content.addView(TextView(this).apply {
                text = resources.getQuantityString(R.plurals.meeting_best_overlap, zones.size, bestOverlap, zones.size, best.size)
                textSize = 14f
                setTextColor(getColor(R.color.chrono_text))
                setPadding(0, 0, 0, dp(12))
            })
            best.forEach { (time, overlap) ->
                val slot = LinearLayout(this).apply {
                    orientation = LinearLayout.VERTICAL
                    setPadding(0, dp(8), 0, dp(10))
                }
                slot.addView(TextView(this).apply {
                    text = resources.getQuantityString(R.plurals.meeting_slot, overlap, formatter.format(Date(time)), overlap, zones.size)
                    textSize = 16f
                    setTypeface(typeface, android.graphics.Typeface.BOLD)
                    setTextColor(getColor(R.color.chrono_accent))
                })
                val cityTimes = zones.joinToString("\n") { zoneId ->
                    val zone = TimeZone.getTimeZone(zoneId)
                    val cityFormatter = SimpleDateFormat("HH:mm", Locale.getDefault()).apply { timeZone = zone }
                    val label = if (zoneId == TimeZone.getDefault().id) "You" else cityName(zoneId)
                    getString(
                        if (isWorkingTime(time, zone)) R.string.meeting_city_time_work else R.string.meeting_city_time,
                        label,
                        cityFormatter.format(Date(time)),
                    )
                }
                slot.addView(TextView(this).apply {
                    text = cityTimes
                    textSize = 13f
                    setTextColor(getColor(R.color.chrono_muted))
                })
                content.addView(slot)
            }
        }
        AlertDialog.Builder(this)
            .setTitle("Meeting planner")
            .setView(ScrollView(this).apply { addView(content) })
            .setNegativeButton("Close", null)
            .show()
    }

    private fun isWorkingTime(timestamp: Long, zone: TimeZone): Boolean {
        val local = Calendar.getInstance(zone).apply { timeInMillis = timestamp }
        val weekday = local.get(Calendar.DAY_OF_WEEK) !in listOf(Calendar.SATURDAY, Calendar.SUNDAY)
        val minutes = local.get(Calendar.HOUR_OF_DAY) * 60 + local.get(Calendar.MINUTE)
        return weekday && minutes >= 9 * 60 && minutes + 30 <= 17 * 60
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
