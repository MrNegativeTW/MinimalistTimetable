package com.txwstudio.app.timetable.ui.activity

import android.net.Uri
import android.os.Bundle
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.preference.PreferenceManager
import com.ortiz.touchview.TouchImageView
import com.txwstudio.app.timetable.R
import com.txwstudio.app.timetable.ui.settings.PREFERENCE_NAME_MAP_REQUEST

class CampusMapActivity : AppCompatActivity() {

    // TODO(Refactor need)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_campus_map)
        setSupportActionBar(findViewById(R.id.toolbar_campusMapAct))
        supportActionBar!!.setDisplayHomeAsUpEnabled(true)

        val touchImageView = findViewById<View>(R.id.touchImageView) as TouchImageView
        val campusMapErrorMsg = findViewById<View>(R.id.campusMapErrorTextView) as TextView

        val prefs = PreferenceManager.getDefaultSharedPreferences(this)
        val mapPath = prefs.getString(PREFERENCE_NAME_MAP_REQUEST, "")
        val mapUri = Uri.parse(mapPath)

        if (mapPath!!.isNotEmpty()) {
            campusMapErrorMsg.visibility = View.INVISIBLE
            touchImageView.setImageURI(mapUri)
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        val id = item.itemId
        if (id == android.R.id.home) {
            finish()
        }
        return super.onOptionsItemSelected(item)
    }
}