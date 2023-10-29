package com.txwstudio.app.timetable.widget

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.txwstudio.app.timetable.R
import com.txwstudio.app.timetable.data.AppDatabase
import com.txwstudio.app.timetable.data.Course3
import java.util.Calendar

class TimetableWidgetService : RemoteViewsService() {

    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory {
        return StackRemoteViewsFactory(this.applicationContext, intent)
    }
}

class StackRemoteViewsFactory(
    private val context: Context,
    val intent: Intent
) : RemoteViewsService.RemoteViewsFactory {

    private lateinit var mWidgetItems: List<Course3>

    /**
     * Called when the appwidget is created for the first time.
     * */
    override fun onCreate() {
        // In onCreate(), set up any connections or cursors to your data
        // source. Heavy lifting, such as downloading or creating content,
        // must be deferred to onDataSetChanged() or getViewAt(). Taking
        // more than 20 seconds on this call results in an ANR.
        Log.i(TAG, "onCreate: ")

        // Initial it to prevent UninitializedPropertyAccessException, fill-in real data later
        mWidgetItems = listOf()
    }

    /**
     * Called whenever the appwidget is updated.
     * */
    override fun onDataSetChanged() {
        Log.i(TAG, "onDataSetChanged()")
        val c = Calendar.getInstance()
        val date = c[Calendar.DAY_OF_WEEK]

        mWidgetItems =
            AppDatabase.getInstance(context).courseDao()
                .getCourseByWeekdayAsList(if (date == 1) 8 else date - 2)
    }

    override fun onDestroy() {}

    /**
     * Returns the number of records in the cursor.
     * */
    override fun getCount(): Int {
        return mWidgetItems.size
    }

    /**
     * Handles all the processing work. It returns a RemoteViews object which in our case
     * is the single list item.
     * */
    override fun getViewAt(position: Int): RemoteViews {
        // We construct a remote views item based on our widget item xml file, and set the
        // text based on the position.
        val courseItem: Course3 = mWidgetItems[position]

        // Format time to human-friendly format
        val courseBeginTime = courseItem.courseStartTime?.replace("..(?!$)".toRegex(), "$0:")

        val views = RemoteViews(context.packageName, R.layout.widget_course_card).apply {
            setTextViewText(R.id.textView_widget_courseBeginTime, courseBeginTime)
            setTextViewText(R.id.textView_widget_courseName, courseItem.courseName)
            setTextViewText(R.id.textView_widget_coursePlace, courseItem.coursePlace)
        }

        return views
    }

    override fun getLoadingView(): RemoteViews? {
        return null
    }

    override fun getViewTypeCount(): Int {
        return 1
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun hasStableIds(): Boolean {
        return true
    }

    companion object {
        private const val TAG = "StackRemoteViewsFactory"
    }
}