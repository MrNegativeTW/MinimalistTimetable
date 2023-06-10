package com.txwstudio.app.timetable.utilities

import android.app.Activity
import android.util.Log
import android.widget.Toast

fun Any.logI(message: String) {
    Log.i(this::class.java.simpleName, message)
}

/**
 * If Logcat shows tag as "SafeCollector", use this instead.
 */
fun Any.logI(tag: String, message: String) {
    Log.i(tag, message)
}

fun Any.logW(message: String) {
    Log.w(this::class.java.simpleName, message)
}

fun Any.logE(message: String) {
    Log.w(this::class.java.simpleName, message)
}

fun Activity.showToast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}