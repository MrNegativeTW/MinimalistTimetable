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
        val action = intent.action
        if (action.equals(Intent.ACTION_DATE_CHANGED)
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
                appWidgetManager.notifyAppWidgetViewDataChanged(
                    appWidgetId,
                    R.id.listview_appwidget
                )
            }

        }
        super.onReceive(context, intent)
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

    // Construct the RemoteViews object
    val views = RemoteViews(context.packageName, R.layout.timetable_widget).apply {
        setTextViewText(R.id.appwidget_text, "io")
        setRemoteAdapter(R.id.listview_appwidget, intent)
    }

    // Instruct the widget manager to update the widget
    appWidgetManager.updateAppWidget(appWidgetId, views)
}