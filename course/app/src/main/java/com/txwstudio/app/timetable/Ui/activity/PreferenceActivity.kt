package com.txwstudio.app.timetable.ui.activity

import android.os.Bundle
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.commit
import androidx.preference.Preference
import androidx.preference.PreferenceFragmentCompat
import com.txwstudio.app.timetable.R
import com.txwstudio.app.timetable.databinding.ActivityPreferenceBinding
import com.txwstudio.app.timetable.ui.preferences.PreferenceFragment

class PreferenceActivity : AppCompatActivity() {

    // , PreferenceFragmentCompat.OnPreferenceStartFragmentCallback

    private lateinit var binding: ActivityPreferenceBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_preference)
        setSupportActionBar(binding.toolbarSettingsAct)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)


            }
        }
    }

    override fun onPreferenceStartFragment(
        caller: PreferenceFragmentCompat,
        pref: Preference
    ): Boolean {
        // Instantiate the new Fragment
        val args = pref.extras
        val fragment = supportFragmentManager.fragmentFactory.instantiate(
            classLoader,
            pref.fragment
        ).apply {
            arguments = args
            // setTargetFragment(caller, 0) // deprecated in java
        }

        // Replace the existing Fragment with the new Fragment
        supportFragmentManager.commit {
            setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
            replace(R.id.fragment_container_view, fragment)
            addToBackStack(null)
        }
        return true
    }
}