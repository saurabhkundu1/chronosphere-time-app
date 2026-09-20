package com.chronosphere.app

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.*
import android.widget.TextView
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {

    private lateinit var city1Time: TextView
    private lateinit var city2Time: TextView
    private lateinit var city3Time: TextView
    private lateinit var city4Time: TextView
    private lateinit var city5Time: TextView
    
    private val cities = listOf(
        "America/New_York" to "New York",
        "Europe/London" to "London",
        "Asia/Tokyo" to "Tokyo",
        "Australia/Sydney" to "Sydney",
        "Asia/Dubai" to "Dubai"
    )

    private var updateJob: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        city1Time = findViewById(R.id.city1Time)
        city2Time = findViewById(R.id.city2Time)
        city3Time = findViewById(R.id.city3Time)
        city4Time = findViewById(R.id.city4Time)
        city5Time = findViewById(R.id.city5Time)

        startClockUpdates()
    }

    private fun startClockUpdates() {
        updateJob = lifecycleScope.launch {
            while (isActive) {
                updateCityTimes()
                delay(1000)
            }
        }
    }

    private fun updateCityTimes() {
        val dateFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
        
        try {
            // City 1: New York
            val nyTime = getTimeForTimeZone(cities[0].first, dateFormat)
            city1Time.text = "${cities[0].second}\n$nyTime"
            
            // City 2: London
            val londonTime = getTimeForTimeZone(cities[1].first, dateFormat)
            city2Time.text = "${cities[1].second}\n$londonTime"
            
            // City 3: Tokyo
            val tokyoTime = getTimeForTimeZone(cities[2].first, dateFormat)
            city3Time.text = "${cities[2].second}\n$tokyoTime"
            
            // City 4: Sydney
            val sydneyTime = getTimeForTimeZone(cities[3].first, dateFormat)
            city4Time.text = "${cities[3].second}\n$sydneyTime"
            
            // City 5: Dubai
            val dubaiTime = getTimeForTimeZone(cities[4].first, dateFormat)
            city5Time.text = "${cities[4].second}\n$dubaiTime"
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getTimeForTimeZone(timeZoneId: String, dateFormat: SimpleDateFormat): String {
        val calendar = Calendar.getInstance(TimeZone.getTimeZone(timeZoneId))
        dateFormat.timeZone = TimeZone.getTimeZone(timeZoneId)
        return dateFormat.format(calendar.time)
    }

    override fun onDestroy() {
        super.onDestroy()
        updateJob?.cancel()
    }
}
