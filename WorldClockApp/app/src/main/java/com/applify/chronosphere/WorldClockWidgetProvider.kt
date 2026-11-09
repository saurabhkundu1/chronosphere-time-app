package com.applify.chronosphere

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.text.format.DateFormat
import android.view.View
import android.widget.RemoteViews
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class WorldClockWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(context: Context, manager: AppWidgetManager, ids: IntArray) {
        ids.forEach { updateAppWidget(context, manager, it) }
    }

    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        when (intent.action) {
            Intent.ACTION_TIME_CHANGED, Intent.ACTION_TIMEZONE_CHANGED -> refresh(context)
        }
    }

    companion object {
        fun refresh(context: Context) {
            val manager = AppWidgetManager.getInstance(context)
            val component = ComponentName(context, WorldClockWidgetProvider::class.java)
            val ids = manager.getAppWidgetIds(component)
            if (ids.isNotEmpty()) WorldClockWidgetProvider().onUpdate(context, manager, ids)
        }

        private fun updateAppWidget(context: Context, manager: AppWidgetManager, id: Int) {
            val views = RemoteViews(context.packageName, R.layout.world_clock_widget)
            val preferences = context.getSharedPreferences(MainActivity.PREFERENCES, Context.MODE_PRIVATE)
            val cities = preferences.getString(MainActivity.KEY_CITIES, null)
                ?.split('|')?.filter { it in MainActivity.cityOptions }
                .orEmpty().take(5)
            val rowIds = intArrayOf(
                R.id.widgetCity1, R.id.widgetCity2, R.id.widgetCity3,
                R.id.widgetCity4, R.id.widgetCity5
            )
            val formatter = SimpleDateFormat(
                if (DateFormat.is24HourFormat(context)) "EEE · HH:mm" else "EEE · h:mm a",
                Locale.getDefault(),
            )
            val now = Date()
            val localZone = TimeZone.getDefault()
            rowIds.forEachIndexed { index, viewId ->
                val zoneId = cities.getOrNull(index)
                if (zoneId == null) {
                    views.setViewVisibility(viewId, View.GONE)
                } else {
                    val zone = TimeZone.getTimeZone(zoneId)
                    formatter.timeZone = zone
                    val city = zoneId.substringAfterLast('/').replace('_', ' ')
                    val difference = TimeDifferenceFormatter.minutesBetween(zone, localZone, now.time)
                    val differenceLabel = if (difference == 0) {
                        context.getString(R.string.widget_same_time)
                    } else {
                        val sign = if (difference > 0) "+" else "−"
                        val absoluteMinutes = kotlin.math.abs(difference)
                        context.getString(
                            R.string.widget_time_difference,
                            sign,
                            absoluteMinutes / 60,
                            absoluteMinutes % 60,
                        )
                    }
                    views.setTextViewText(
                        viewId,
                        context.getString(R.string.widget_city_time, "$city · $differenceLabel", formatter.format(now)),
                    )
                    views.setViewVisibility(viewId, View.VISIBLE)
                }
            }
            val openApp = PendingIntent.getActivity(
                context, 0, Intent(context, MainActivity::class.java),
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widgetRoot, openApp)
            manager.updateAppWidget(id, views)
        }
    }
}
