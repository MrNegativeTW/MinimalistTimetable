package com.txwstudio.app.timetable.ui.mapsviewer

import android.content.ActivityNotFoundException
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.MenuHost
import androidx.core.view.MenuProvider
import androidx.documentfile.provider.DocumentFile
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.navigation.fragment.findNavController
import androidx.preference.PreferenceManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import com.google.android.material.snackbar.Snackbar
import com.txwstudio.app.timetable.R
import com.txwstudio.app.timetable.databinding.FragmentMapsViewerBinding
import com.txwstudio.app.timetable.ui.preferences.PREFERENCE_MAP_DATA_TYPE
import com.txwstudio.app.timetable.ui.preferences.PREFERENCE_MAP_PATH
import com.txwstudio.app.timetable.utils.DATA_TYPE_IMAGE
import com.txwstudio.app.timetable.utils.DATA_TYPE_PDF

/**
 * Implementation of TouchImageView.
 */
class MapsViewerFragment : Fragment(), MenuProvider {

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
        binding = FragmentMapsViewerBinding.inflate(layoutInflater)

        setupToolBar()

        loadMapImage()

        return binding.root
    }

    private fun setupToolBar() {
        (activity as AppCompatActivity).setSupportActionBar(binding.toolbarMapsViewerFrag)
        (activity as AppCompatActivity).supportActionBar?.setDisplayShowTitleEnabled(false)
        (activity as AppCompatActivity).supportActionBar?.setDisplayHomeAsUpEnabled(true)

        (requireActivity() as MenuHost).addMenuProvider(
            this,
            viewLifecycleOwner,
            Lifecycle.State.RESUMED
        )
    }

    override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {}

    override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
        return when (menuItem.itemId) {
            android.R.id.home -> {
                findNavController().navigateUp()
                true
            }

            else -> false
        }
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
        // Get map image path from shared preferences. Returns if null
        val mapImagePath = sharedPref.getString(PREFERENCE_MAP_PATH, "") ?: return

        // Return if string is empty
        mapImagePath.isEmpty().let { if (it) return }

        val mapImageUri = Uri.parse(mapImagePath)
        if (isFileExists(mapImageUri)) {
            binding.linearLayoutErrorMessageSection.visibility = View.GONE
            binding.progressBarLoadImgPlsWait.visibility = View.VISIBLE
            Glide.with(this)
                .asBitmap()
                .load(mapImageUri)
                .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                .into(object : CustomTarget<Bitmap>(3000, 3000) {
                    override fun onResourceReady(
                        resource: Bitmap,
                        transition: Transition<in Bitmap>?
                    ) {
                        binding.progressBarLoadImgPlsWait.visibility = View.GONE
                        binding.touchImageView.setImageBitmap(resource)
                    }

                    override fun onLoadCleared(placeholder: Drawable?) {
                    }
                })
        } else {
            binding.textViewErrorTitle.text = getString(R.string.mapViewer_notFoundTitle)
            binding.textViewErrorCause.text = getString(R.string.mapViewer_notFoundMsg)
        }
    }

    private fun isFileExists(mapImageUri: Uri): Boolean {
        // When Build.VERSION.SDK_INT >= 19, it shouldn't return null. This is some trust issue lol
        val documentFile = DocumentFile.fromSingleUri(requireContext(), mapImageUri)
        if (documentFile != null) {
            return documentFile.exists()
        }
        return false
    }

    companion object {
        private const val TAG = "MapsViewerFragment"
    }
}