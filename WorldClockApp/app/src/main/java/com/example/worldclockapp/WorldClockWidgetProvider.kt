package com.example.worldclockapp

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.widget.RemoteViews
import java.text.SimpleDateFormat
import java.util.*

class WorldClockWidgetProvider : AppWidgetProvider() {

    companion object {
        const val ACTION_UPDATE_WIDGET = "com.example.worldclockapp.ACTION_UPDATE_WIDGET"
        
        private val cities = listOf(
            "America/New_York" to "New York",
            "Europe/London" to "London",
            "Asia/Tokyo" to "Tokyo",
            "Australia/Sydney" to "Sydney",
            "Asia/Dubai" to "Dubai"
        )
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    private fun updateAppWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        val views = RemoteViews(context.packageName, R.layout.world_clock_widget)
        
        try {
            val dateFormat = SimpleDateFormat("HH:mm:ss", Locale.getDefault())
            
            // Update all 5 cities
            views.setTextViewText(R.id.widgetCity1, formatCityTime(cities[0], dateFormat))
            views.setTextViewText(R.id.widgetCity2, formatCityTime(cities[1], dateFormat))
            views.setTextViewText(R.id.widgetCity3, formatCityTime(cities[2], dateFormat))
            views.setTextViewText(R.id.widgetCity4, formatCityTime(cities[3], dateFormat))
            views.setTextViewText(R.id.widgetCity5, formatCityTime(cities[4], dateFormat))
            
            appWidgetManager.updateAppWidget(appWidgetId, views)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun formatCityTime(cityPair: Pair<String, String>, dateFormat: SimpleDateFormat): String {
        dateFormat.timeZone = TimeZone.getTimeZone(cityPair.first)
        val time = dateFormat.format(Date())
        return "${cityPair.second}\n$time"
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when your first widget is created
    }

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when your last widget is disabled
    }
}
