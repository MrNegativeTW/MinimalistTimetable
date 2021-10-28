package com.txwstudio.app.timetable.ui.mapsviewer

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import com.txwstudio.app.timetable.databinding.FragmentMapsViewerBinding
import com.txwstudio.app.timetable.ui.preferences.PREFERENCE_NAME_MAP_REQUEST

/**
 * Implementation of TouchImageView.
 */
class MapsViewerFragment : Fragment() {

    companion object {
        fun newInstance() = MapsViewerFragment()
    }

    private lateinit var binding: FragmentMapsViewerBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentMapsViewerBinding.inflate(inflater, container, false)

        val prefs = PreferenceManager.getDefaultSharedPreferences(requireContext())
        val mapPath = prefs.getString(PREFERENCE_NAME_MAP_REQUEST, "")

        val mapUri = Uri.parse(mapPath)

        mapPath?.isNotEmpty().let {
            if (it == true) {
                binding.textViewMapsViewerFragMapsNotFound.visibility = View.INVISIBLE
                binding.touchImageView.setImageURI(mapUri)
            }
        }

        setupToolBar()

        return binding.root
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                findNavController().navigateUp()
                return true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun setupToolBar() {
        (activity as AppCompatActivity).setSupportActionBar(binding.toolbarMapsViewerFrag)
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(false)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)
        setHasOptionsMenu(true)
    }

}