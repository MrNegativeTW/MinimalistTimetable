package com.txwstudio.app.timetable.widget

import android.content.Context
import android.content.Intent
import android.util.Log
import android.widget.RemoteViews
import android.widget.RemoteViewsService
import com.txwstudio.app.timetable.R
import com.txwstudio.app.timetable.data.AppDatabase
import com.txwstudio.app.timetable.data.Course3
import kotlinx.coroutines.flow.toList
import java.util.*

class TimetableWidgetService : RemoteViewsService() {

    override fun onGetViewFactory(intent: Intent): RemoteViewsFactory {
        return StackRemoteViewsFactory(this.applicationContext, intent)
    }
}

class StackRemoteViewsFactory(
    private val mContext: Context,
    val intent: Intent
) : RemoteViewsService.RemoteViewsFactory {

    private lateinit var mWidgetItems: List<Course3>

    /**
     * Called when the appwidget is created for the first time.
     * */
    override fun onCreate() {}

    /**
     * Called whenever the appwidget is updated.
     * */
    override fun onDataSetChanged() {
//        Log.i("TESTTT", "onDataSetChanged()")
        val c = Calendar.getInstance()
        val date = c[Calendar.DAY_OF_WEEK]

        mWidgetItems =
            AppDatabase.getInstance(mContext).courseDao()
                .getCourseByWeekdayAsList(if (date == 1) 8 else date - 2)
    }

    override fun onDestroy() {
//        Log.i("TESTTT", "onDestroy()")
    }

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
        val views = RemoteViews(mContext.packageName, R.layout.widget_course_card).apply {
            setTextViewText(R.id.textView_widget_courseBeginTime, mWidgetItems[position].courseStartTime)
            setTextViewText(R.id.textView_widget_courseName, mWidgetItems[position].courseName)
            setTextViewText(R.id.textView_widget_coursePlace, mWidgetItems[position].coursePlace)
        }
        // Next, we set a fill-intent which will be used to fill-in the pending intent template
        // which is set on the collection view in StackWidgetProvider.

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

}