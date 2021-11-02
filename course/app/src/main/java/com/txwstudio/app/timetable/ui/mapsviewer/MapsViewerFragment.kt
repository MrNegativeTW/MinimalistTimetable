package com.txwstudio.app.timetable.ui.mapsviewer

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.documentfile.provider.DocumentFile
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import com.txwstudio.app.timetable.databinding.FragmentMapsViewerBinding
import com.txwstudio.app.timetable.ui.preferences.PREFERENCE_MAP_PATH

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

        setupToolBar()

        loadMapImage()

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

    private fun loadMapImage() {
        // Get map image path from shared preferences.
        val prefs = PreferenceManager.getDefaultSharedPreferences(requireContext())
        val mapImagePath = prefs.getString(PREFERENCE_MAP_PATH, "")

        // Check if map path string has empty value or not.
        mapImagePath?.isNotEmpty()?.let {
            // If it's empty, we treat it as not set, do nothing.
            if (it) {
                // Path string not empty, check file exists or not.
                val mapImageUri = Uri.parse(mapImagePath)
                checkMapImageExistsThenSetImage(mapImageUri)
            }
        }
    }

    private fun checkMapImageExistsThenSetImage(mapImageUri: Uri) {
        DocumentFile.fromSingleUri(requireContext(), mapImageUri)?.exists()?.let {
            if (it) {
                binding.textViewMapsViewerFragMapsNotFound.visibility = View.INVISIBLE
                binding.touchImageView.setImageURI(mapImageUri)
            }
        }
    }

}