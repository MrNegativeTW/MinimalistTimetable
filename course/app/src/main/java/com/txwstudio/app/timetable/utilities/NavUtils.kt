package com.txwstudio.app.timetable.utilities

import androidx.navigation.NavController
import androidx.navigation.NavDirections

/**
 * Prevent double click navigation
 */
fun NavController.safeNavigate(directions: NavDirections) {
    currentDestination?.getAction(directions.actionId)?.run {
        navigate(directions)
    }
}
