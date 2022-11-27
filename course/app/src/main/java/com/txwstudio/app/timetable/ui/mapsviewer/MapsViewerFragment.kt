package com.txwstudio.app.timetable.ui.mapsviewer

import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.SharedPreferences
import android.net.Uri
import android.os.Bundle
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.documentfile.provider.DocumentFile
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import com.google.android.material.snackbar.Snackbar
import com.txwstudio.app.timetable.R
import com.txwstudio.app.timetable.databinding.FragmentMapsViewerBinding
import com.txwstudio.app.timetable.ui.preferences.PREFERENCE_MAP_DATA_TYPE
import com.txwstudio.app.timetable.ui.preferences.PREFERENCE_MAP_PATH
import com.txwstudio.app.timetable.utilities.DATA_TYPE_IMAGE
import com.txwstudio.app.timetable.utilities.DATA_TYPE_PDF

/**
 * Implementation of TouchImageView.
 */
class MapsViewerFragment : Fragment() {

    private lateinit var sharedPref: SharedPreferences

    private lateinit var binding: FragmentMapsViewerBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Check image file type, open it up if it's pdf.
        sharedPref = PreferenceManager.getDefaultSharedPreferences(requireContext())
        if (sharedPref.getString(PREFERENCE_MAP_DATA_TYPE, DATA_TYPE_IMAGE) == DATA_TYPE_PDF) {
            openPdfThenNavigateUp()
        }
    }

    override fun onCreateView(
            inflater: LayoutInflater, container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View {
        binding = FragmentMapsViewerBinding.inflate(inflater, container, false)

        setupToolBar()

        loadMapImage()

        return binding.root
    }

    private fun setupToolBar() {
        (activity as AppCompatActivity).setSupportActionBar(binding.toolbarMapsViewerFrag)
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(false)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)

        (requireActivity() as MenuHost).addMenuProvider(object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {

            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    android.R.id.home -> {
                        findNavController().navigateUp()
                        true
                    }
                    else -> false
                }
            }
        }, viewLifecycleOwner, Lifecycle.State.RESUMED)
    }

    /**
     * Open PDF when the file type is PDF and entry point isn't from HomeViewPagerFragment.
     */
    private fun openPdfThenNavigateUp() {
        val mapPdfFileUriInString = sharedPref.getString(PREFERENCE_MAP_PATH, "")

        val uri = Uri.parse(mapPdfFileUriInString)
        val target = Intent(Intent.ACTION_VIEW).apply {
            setDataAndType(uri, DATA_TYPE_PDF)
            flags = Intent.FLAG_ACTIVITY_NO_HISTORY
            addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
        }

        val intent = Intent.createChooser(target, java.lang.String.valueOf(R.string.pdfOpenWithMsg))

        try {
            startActivity(intent)
            findNavController().navigateUp()
        } catch (e: ActivityNotFoundException) {
            // Suitable app not found.
            Snackbar.make(requireView(), R.string.pdfNoAppMsg, Snackbar.LENGTH_SHORT).show()
        } catch (e: SecurityException) {
            // File not found.
            Snackbar.make(requireView(), R.string.pdfFileNotFound, Snackbar.LENGTH_SHORT).show()
        } catch (e: Exception) {
            // Unknown exception.
            Snackbar.make(requireView(), R.string.fileReadErrorMsg, Snackbar.LENGTH_SHORT).show()
        }
    }

    private fun loadMapImage() {
        // Get map image path from shared preferences.
        val mapImagePath = sharedPref.getString(PREFERENCE_MAP_PATH, "")

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

    /**
     * Check image file exist before setImage to prevent crashes.
     *
     * @param mapImageUri Parsed file uri.
     * */
    private fun checkMapImageExistsThenSetImage(mapImageUri: Uri) {
        DocumentFile.fromSingleUri(requireContext(), mapImageUri)?.exists()?.let {
            if (it) {
                binding.linearLayoutMapsViewerFragImageNotSet.visibility = View.GONE
                binding.touchImageView.setImageURI(mapImageUri)
            }
        }
    }

    companion object {
        private const val TAG = "MapsViewerFragment"
        fun newInstance() = MapsViewerFragment()
    }
}