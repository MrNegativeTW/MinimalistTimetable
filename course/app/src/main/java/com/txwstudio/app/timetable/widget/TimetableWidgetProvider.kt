package com.txwstudio.app.timetable.widget

import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.util.Log
import android.widget.RemoteViews
import com.txwstudio.app.timetable.R
import com.txwstudio.app.timetable.utilities.INTENT_TIMETABLE_CHANGED
import java.util.*

/**
 * Implementation of App Widget functionality.
 */
class TimetableWidgetProvider : AppWidgetProvider() {

    override fun onDisabled(context: Context) {
        // Enter relevant functionality for when the last widget is disabled
        super.onDisabled(context)
    }

    override fun onEnabled(context: Context) {
        // Enter relevant functionality for when the first widget is created
        super.onEnabled(context)
    }

    /**
     * If intent action equals date or time change events, update widget content.
     */
    override fun onReceive(context: Context, intent: Intent) {
        super.onReceive(context, intent)
        val action = intent.action
        if (action.equals(INTENT_TIMETABLE_CHANGED)
            || action.equals(Intent.ACTION_DATE_CHANGED)
            || action.equals(Intent.ACTION_TIMEZONE_CHANGED)
            || action.equals(Intent.ACTION_TIME_CHANGED)
        ) {
//            Log.i("TEST", "onReceive")
            val appWidgetManager = AppWidgetManager.getInstance(context)
            val appWidgetIds =
                appWidgetManager.getAppWidgetIds(
                    ComponentName(
                        context,
                        TimetableWidgetProvider::class.java
                    )
                )

            for (appWidgetId in appWidgetIds) {
                updateAppWidget(context, appWidgetManager, appWidgetId)
                appWidgetManager.notifyAppWidgetViewDataChanged(
                    appWidgetId,
                    R.id.listview_appwidget
                )
            }

        }
    }

    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
//        Log.i("TEST", "onUpdate")

        // There may be multiple widgets active, so update all of them
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }

        super.onUpdate(context, appWidgetManager, appWidgetIds)
    }

}

internal fun updateAppWidget(
    context: Context,
    appWidgetManager: AppWidgetManager,
    appWidgetId: Int
) {
//    Log.i("TEST", "updateAppWidget")

    // Set up the intent that starts the TimetableWidgetService, which will
    // provide the views for this collection.
    val intent = Intent(context, TimetableWidgetService::class.java).apply {
        // Add the app widget ID to the intent extras.
        putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId)
        data = Uri.parse(toUri(Intent.URI_INTENT_SCHEME))
    }

    val c = Calendar.getInstance()
    val date = c[Calendar.DAY_OF_WEEK]
    val array = R.array.weekdayList

    // If DAY_OF_WEEK is 1 (SUNDAY), get string from array index 6.
    // If DAY_OF_WEEK is 2 ~ 7 (MONDAY ~ SATURDAY), get string from array index (2 ~ 7) - 1.
    val weekdayText = context.resources.getStringArray(array)[if (date == 1) 6 else date - 2]

    // Construct the RemoteViews object
    val views = RemoteViews(context.packageName, R.layout.timetable_widget).apply {
        setTextViewText(R.id.textView_appwidget_weekday, weekdayText)
        setRemoteAdapter(R.id.listview_appwidget, intent)
        setEmptyView(R.id.listview_appwidget, R.id.appwidget_text)
    }

    // Instruct the widget manager to update the widget
    appWidgetManager.updateAppWidget(appWidgetId, views)
}