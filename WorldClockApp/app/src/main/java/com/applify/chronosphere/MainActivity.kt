package com.applify.chronosphere

import android.app.AlertDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.provider.CalendarContract
import android.widget.Toast
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.text.format.DateFormat
import android.view.Gravity
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.ListView
import android.widget.ScrollView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.google.android.material.card.MaterialCardView
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.text.DateFormatSymbols
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

class MainActivity : AppCompatActivity() {
    private val preferences by lazy { getSharedPreferences(PREFERENCES, MODE_PRIVATE) }
    private val viewModel by lazy { WorldClockViewModel(application) }
    private val timeViews = linkedMapOf<String, TextView>()
    private val dateViews = linkedMapOf<String, TextView>()
    private val differenceViews = linkedMapOf<String, TextView>()
    private val dayNightViews = linkedMapOf<String, TextView>()
    private var timeOffsetHours: Float = 0.0f
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

        val sliderTimeOffset = findViewById<com.google.android.material.slider.Slider>(R.id.sliderTimeOffset)
        val btnResetTimeTravel = findViewById<com.google.android.material.button.MaterialButton>(R.id.btnResetTimeTravel)
        val tvTimeTravelLabel = findViewById<TextView>(R.id.tvTimeTravelLabel)

        sliderTimeOffset?.addOnChangeListener { _, value, fromUser ->
            if (fromUser) {
                timeOffsetHours = value
                updateTimeTravelUI(tvTimeTravelLabel, btnResetTimeTravel)
                refreshTimes()
            }
        }
        btnResetTimeTravel?.setOnClickListener {
            sliderTimeOffset?.value = 0f
            timeOffsetHours = 0f
            updateTimeTravelUI(tvTimeTravelLabel, btnResetTimeTravel)
            refreshTimes()
        }
        ensureDefaultCities()
        renderCities()
        lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                viewModel.savedCities.collect {
                    renderCities()
                }
            }
        }
        updateJob = lifecycleScope.launch {
            repeatOnLifecycle(Lifecycle.State.STARTED) {
                while (isActive) {
                    refreshTimes()
                    delay(1_000)
                }
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

    private fun updateTimeTravelUI(label: TextView?, resetButton: View?) {
        if (timeOffsetHours == 0f) {
            label?.text = getString(R.string.time_travel_label_live)
            resetButton?.visibility = View.GONE
        } else {
            val sign = if (timeOffsetHours > 0) "+" else ""
            val formatted = if (timeOffsetHours % 1f == 0f) "${timeOffsetHours.toInt()}h" else "${timeOffsetHours}h"
            label?.text = getString(R.string.time_travel_label_offset, "$sign$formatted")
            resetButton?.visibility = View.VISIBLE
        }
    }

    private fun ensureDefaultCities() {
        viewModel.loadCities()
    }

    private fun cities(): List<String> = viewModel.savedCities.value

    private fun saveCities(cities: List<String>) {
        viewModel.saveCities(cities)
        renderCities()
    }

    private fun renderCities() {
        if (!::cityContainer.isInitialized) return
        cityContainer.removeAllViews()
        timeViews.clear()
        dateViews.clear()
        differenceViews.clear()
        dayNightViews.clear()
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
            val headerRow = LinearLayout(this).apply {
                orientation = LinearLayout.HORIZONTAL
                gravity = Gravity.CENTER_VERTICAL
            }
            val cityLabel = TextView(this).apply {
                text = cityName(zoneId)
                textSize = 17f
                setTextColor(getColor(R.color.chrono_text))
                setTypeface(typeface, android.graphics.Typeface.BOLD)
            }
            val dayNightLabel = TextView(this).apply {
                textSize = 11f
                setPadding(dp(6), dp(2), dp(6), dp(2))
                typeface = android.graphics.Typeface.DEFAULT_BOLD
            }
            headerRow.addView(cityLabel)
            val badgeParams = LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            ).apply { marginStart = dp(8) }
            headerRow.addView(dayNightLabel, badgeParams)

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
            val differenceLabel = TextView(this).apply {
                textSize = 12f
                setTextColor(getColor(R.color.chrono_muted))
            }
            details.addView(headerRow)
            details.addView(dateLabel)
            details.addView(differenceLabel)
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
            differenceViews[zoneId] = differenceLabel
            dayNightViews[zoneId] = dayNightLabel
        }
        findViewById<TextView>(R.id.emptyMessage)?.visibility =
            if (cities().isEmpty()) android.view.View.VISIBLE else android.view.View.GONE
        refreshTimes()
    }

    private fun refreshTimes() {
        val offsetMillis = (timeOffsetHours * 3600_000L).toLong()
        val now = Date(System.currentTimeMillis() + offsetMillis)
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
            val differenceMinutes = TimeDifferenceFormatter.minutesBetween(zone, localZone, now.time)
            differenceViews[zoneId]?.text = timeDifferenceLabel(differenceMinutes)

            val cal = Calendar.getInstance(zone).apply { time = now }
            val hour = cal.get(Calendar.HOUR_OF_DAY)
            val isDay = hour in 6..17
            val badge = dayNightViews[zoneId]
            if (isDay) {
                badge?.text = getString(R.string.day_indicator)
                badge?.setTextColor(getColor(R.color.chrono_day))
            } else {
                badge?.text = getString(R.string.night_indicator)
                badge?.setTextColor(getColor(R.color.chrono_night))
            }
        }
    }

    private fun timeDifferenceLabel(minutes: Int): String {
        if (minutes == 0) return getString(R.string.time_difference_same)

        val absoluteMinutes = kotlin.math.abs(minutes)
        val hours = absoluteMinutes / 60
        val remainingMinutes = absoluteMinutes % 60
        val amount = when {
            hours == 0 -> getString(R.string.time_difference_minutes, remainingMinutes)
            remainingMinutes == 0 -> getString(R.string.time_difference_hours, hours)
            else -> getString(R.string.time_difference_hours_minutes, hours, remainingMinutes)
        }
        return getString(
            if (minutes > 0) R.string.time_difference_ahead else R.string.time_difference_behind,
            amount,
        )
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
        val (workStart, workEnd) = workingHours()
        val duration = meetingDuration()
        val workDays = workingDays()
        val firstSlot = Calendar.getInstance().apply {
            timeInMillis = System.currentTimeMillis()
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
            val minutesToNextQuarter = (15 - get(Calendar.MINUTE) % 15) % 15
            add(Calendar.MINUTE, if (minutesToNextQuarter == 0) 15 else minutesToNextQuarter)
        }
        val candidates = (0 until 14 * 96).mapNotNull { index ->
            val time = firstSlot.timeInMillis + index * 15 * 60_000L
            val overlap = zones.count { isWorkingTime(time, TimeZone.getTimeZone(it), workStart, workEnd, duration, workDays) }
            if (overlap > 0) time to overlap else null
        }
        val bestOverlap = candidates.maxOfOrNull { it.second } ?: 0
        val best = candidates.filter { it.second == bestOverlap }.fold(mutableListOf<Pair<Long, Int>>()) { slots, candidate ->
            if (slots.size < 5 && slots.none { candidate.first - it.first < duration * 60_000L }) slots.add(candidate)
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
        var plannerDialog: AlertDialog? = null
        content.addView(Button(this).apply {
            text = getString(R.string.meeting_work_hours, formatMinuteOfDay(workStart), formatMinuteOfDay(workEnd))
            setOnClickListener {
                showWorkHoursPicker {
                    plannerDialog?.dismiss()
                    showMeetingPlanner()
                }
            }
        })
        content.addView(Button(this).apply {
            text = getString(R.string.meeting_duration_value, duration)
            setOnClickListener {
                showMeetingDurationPicker {
                    plannerDialog?.dismiss()
                    showMeetingPlanner()
                }
            }
        })
        content.addView(Button(this).apply {
            text = getString(R.string.meeting_work_days_value, workDays.sorted().joinToString { weekdayName(it) })
            setOnClickListener {
                showWorkDaysPicker {
                    plannerDialog?.dismiss()
                    showMeetingPlanner()
                }
            }
        })
        if (best.isEmpty()) {
            content.addView(TextView(this).apply { text = getString(R.string.meeting_empty) })
        } else {
            content.addView(TextView(this).apply {
                text = resources.getQuantityString(
                    R.plurals.meeting_best_overlap,
                    zones.size,
                    bestOverlap,
                    zones.size,
                    formatMinuteOfDay(workStart),
                    formatMinuteOfDay(workEnd),
                    best.size,
                    duration,
                )
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
                val cityTimePattern = if (DateFormat.is24HourFormat(this)) "EEE, d MMM yyyy · HH:mm" else "EEE, d MMM yyyy · h:mm a"
                val cityTimes = zones.joinToString("\n") { zoneId ->
                    val zone = TimeZone.getTimeZone(zoneId)
                    val cityFormatter = SimpleDateFormat(cityTimePattern, Locale.getDefault()).apply { timeZone = zone }
                    getString(
                        if (isWorkingTime(time, zone, workStart, workEnd, duration, workDays)) R.string.meeting_city_time_work else R.string.meeting_city_time,
                        cityName(zoneId),
                        cityFormatter.format(Date(time)),
                    )
                }
                slot.addView(TextView(this).apply {
                    text = cityTimes
                    textSize = 13f
                    setTextColor(getColor(R.color.chrono_muted))
                })
                slot.addView(Button(this).apply {
                    text = getString(R.string.meeting_add_calendar)
                    setOnClickListener {
                        val intent = Intent(Intent.ACTION_INSERT, CalendarContract.Events.CONTENT_URI).apply {
                            putExtra(CalendarContract.Events.TITLE, getString(R.string.meeting_event_title))
                            putExtra(CalendarContract.Events.DESCRIPTION, cityTimes.replace("\n", "; "))
                            putExtra(CalendarContract.EXTRA_EVENT_BEGIN_TIME, time)
                            putExtra(CalendarContract.EXTRA_EVENT_END_TIME, time + duration * 60_000L)
                        }
                        runCatching { startActivity(intent) }
                            .onFailure { Toast.makeText(this@MainActivity, R.string.meeting_calendar_unavailable, Toast.LENGTH_SHORT).show() }
                    }
                })
                slot.addView(Button(this).apply {
                    text = getString(R.string.meeting_share_time)
                    setOnClickListener {
                        val share = Intent(Intent.ACTION_SEND).apply {
                            type = "text/plain"
                            putExtra(Intent.EXTRA_SUBJECT, getString(R.string.meeting_event_title))
                            putExtra(Intent.EXTRA_TEXT, getString(R.string.meeting_share_text, duration, cityTimes))
                        }
                        startActivity(Intent.createChooser(share, getString(R.string.meeting_share_chooser)))
                    }
                })
                content.addView(slot)
            }
        }
        plannerDialog = AlertDialog.Builder(this)
            .setTitle("Meeting planner")
            .setView(ScrollView(this).apply { addView(content) })
            .setNegativeButton("Close", null)
            .create()
        plannerDialog?.show()
    }

    private fun showWorkHoursPicker(onSaved: () -> Unit) {
        val (workStart, workEnd) = workingHours()
        TimePickerDialog(this, { _, startHour, startMinute ->
            val start = startHour * 60 + startMinute
            TimePickerDialog(this, { _, endHour, endMinute ->
                val end = endHour * 60 + endMinute
                val window = (end - start + 24 * 60) % (24 * 60)
                if (window < MINIMUM_WORK_WINDOW_MINUTES) {
                    Toast.makeText(this, R.string.meeting_hours_invalid, Toast.LENGTH_SHORT).show()
                    return@TimePickerDialog
                }
                preferences.edit {
                    putInt(KEY_WORK_START, start)
                    putInt(KEY_WORK_END, end)
                }
                onSaved()
            }, workEnd / 60, workEnd % 60, DateFormat.is24HourFormat(this)).show()
        }, workStart / 60, workStart % 60, DateFormat.is24HourFormat(this)).show()
    }

    private fun showMeetingDurationPicker(onSaved: () -> Unit) {
        val durations = MEETING_DURATIONS
        AlertDialog.Builder(this)
            .setTitle(R.string.meeting_duration_title)
            .setSingleChoiceItems(
                durations.map { resources.getQuantityString(R.plurals.meeting_duration_option, it, it) }.toTypedArray(),
                durations.indexOf(meetingDuration()),
            ) { dialog, which ->
                durations.getOrNull(which)?.let { duration ->
                    preferences.edit { putInt(KEY_MEETING_DURATION, duration) }
                    dialog.dismiss()
                    onSaved()
                }
            }
            .setNegativeButton(android.R.string.cancel, null)
            .show()
    }

    private fun showWorkDaysPicker(onSaved: () -> Unit) {
        val days = (Calendar.SUNDAY..Calendar.SATURDAY).toList()
        val selected = workingDays()
        val checked = BooleanArray(days.size) { days[it] in selected }
        val dialog = AlertDialog.Builder(this)
            .setTitle(R.string.meeting_work_days_title)
            .setMultiChoiceItems(days.map(::weekdayName).toTypedArray(), checked) { _, which, isChecked ->
                checked[which] = isChecked
            }
            .setPositiveButton(android.R.string.ok, null)
            .setNegativeButton(android.R.string.cancel, null)
            .create()
        dialog.setOnShowListener {
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setOnClickListener {
                val workDays = days.filterIndexed { index, _ -> checked[index] }.toSet()
                if (workDays.isEmpty()) {
                    Toast.makeText(this, R.string.meeting_work_days_empty, Toast.LENGTH_SHORT).show()
                    return@setOnClickListener
                }
                preferences.edit {
                    putStringSet(KEY_WORK_DAYS, workDays.map { it.toString() }.toSet())
                }
                dialog.dismiss()
                onSaved()
            }
        }
        dialog.show()
    }

    private fun meetingDuration() = preferences.getInt(KEY_MEETING_DURATION, DEFAULT_MEETING_DURATION)
        .takeIf { it in MEETING_DURATIONS } ?: DEFAULT_MEETING_DURATION

    private fun workingDays(): Set<Int> = preferences.getStringSet(KEY_WORK_DAYS, null)
        ?.mapNotNull { it.toIntOrNull() }
        ?.filter { it in Calendar.SUNDAY..Calendar.SATURDAY }
        ?.toSet()
        ?.takeIf { it.isNotEmpty() }
        ?: DEFAULT_WORK_DAYS

    private fun weekdayName(day: Int) = DateFormatSymbols.getInstance(Locale.getDefault()).weekdays[day]

    private fun workingHours(): Pair<Int, Int> {
        val start = preferences.getInt(KEY_WORK_START, DEFAULT_WORK_START)
        val end = preferences.getInt(KEY_WORK_END, DEFAULT_WORK_END)
        val window = (end - start + 24 * 60) % (24 * 60)
        return if (start in 0 until 24 * 60 && end in 0 until 24 * 60 && window >= MINIMUM_WORK_WINDOW_MINUTES) {
            start to end
        } else {
            DEFAULT_WORK_START to DEFAULT_WORK_END
        }
    }

    private fun formatMinuteOfDay(minutes: Int): String = SimpleDateFormat(
        if (DateFormat.is24HourFormat(this)) "HH:mm" else "h:mm a",
        Locale.getDefault(),
    ).apply { timeZone = TimeZone.getTimeZone("UTC") }
        .format(Date(minutes * 60_000L))

    private fun cityName(zoneId: String): String = zoneId.substringAfterLast('/').replace('_', ' ')
        .replace("St Kitts", "St. Kitts")

    private fun dp(value: Int): Int = (value * resources.displayMetrics.density).toInt()

    companion object {
        const val PREFERENCES = "world_clock_preferences"
        const val KEY_CITIES = "saved_time_zones"
        private const val KEY_WORK_START = "meeting_work_start"
        private const val KEY_WORK_END = "meeting_work_end"
        private const val KEY_MEETING_DURATION = "meeting_duration"
        private const val KEY_WORK_DAYS = "meeting_work_days"
        private const val DEFAULT_WORK_START = 9 * 60
        private const val DEFAULT_WORK_END = 17 * 60
        private const val MINIMUM_WORK_WINDOW_MINUTES = 30
        private const val DEFAULT_MEETING_DURATION = 30
        private val MEETING_DURATIONS = listOf(15, 30, 60)
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
