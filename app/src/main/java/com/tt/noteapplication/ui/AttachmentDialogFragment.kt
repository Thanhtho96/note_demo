package com.tt.noteapplication.ui

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.DialogFragment
import com.google.android.material.snackbar.Snackbar
import com.tt.noteapplication.BuildConfig
import com.tt.noteapplication.R
import com.tt.noteapplication.util.Constants
import kotlinx.android.synthetic.main.activity_note.*
import kotlinx.android.synthetic.main.fragment_attachment.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

const val REQUEST_IMAGE_CAPTURE = 1
private const val PERMISSION_REQUEST_READ_STORAGE = 3
const val REQUEST_FILE = 67

class AttachmentDialogFragment : DialogFragment(), View.OnClickListener {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_attachment, container, false)
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        select_from_storage.setOnClickListener(this)
        take_picture.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v) {
            select_from_storage -> {
                startFileChooser()
            }
            take_picture -> {
                dispatchTakePictureIntent()
            }
        }
    }

    private fun dispatchTakePictureIntent() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        // Ensure that there's a camera activity to handle the intent
        if (takePictureIntent.resolveActivity(requireActivity().packageManager) != null) {
            // Create the File where the photo should go
            var photoFile: File? = null
            try {
                photoFile = createImageFile(requireContext(), "jpg")
            } catch (ex: IOException) {
                // Error occurred while creating the File
            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                val photoURI = FileProvider.getUriForFile(
                    requireContext(), BuildConfig.APPLICATION_ID + Constants.fileProviderExtension,
                    photoFile
                )
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
                requireActivity().startActivityForResult(
                    takePictureIntent,
                    REQUEST_IMAGE_CAPTURE
                )
            }
        }
    }

    private fun startFileChooser() {
        if (ActivityCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
            == PackageManager.PERMISSION_GRANTED
        ) {
            // Permission is already available, start pick file
            val intent = Intent()
            intent.type = "image/*"
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false)
            intent.action = Intent.ACTION_GET_CONTENT
            Toast.makeText(context, getString(R.string.txt_select_image), Toast.LENGTH_SHORT)
                .show()
            requireActivity().startActivityForResult(
                Intent.createChooser(
                    intent,
                    getString(R.string.txt_select_image)
                ), REQUEST_FILE
            )
//            }
        } else {
            // Permission is missing and must be requested.
            requestReadStoragePermission()
        }
    }

    private fun requestReadStoragePermission() {
        // Permission has not been granted and must be requested.
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                requireActivity(),
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        ) {
            // Provide an additional rationale to the user if the permission was not granted
            // and the user would benefit from additional context for the use of the permission.
            // Display a SnackBar with cda button to request the missing permission.
            Snackbar.make(
                requireActivity().container, getString(R.string.txt_need_read_permission),
                Snackbar.LENGTH_INDEFINITE
            ).setAction(
                getString(R.string.txt_grant_permission)
            ) { // Request the permission
                ActivityCompat.requestPermissions(
                    requireActivity(),
                    arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                    PERMISSION_REQUEST_READ_STORAGE
                )
            }.show()
        } else {
            Snackbar.make(
                requireActivity().container,
                getString(R.string.txt_read_permission_unavailable),
                Snackbar.LENGTH_SHORT
            ).show()
            // Request the permission. The result will be received in onRequestPermissionResult().
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),
                PERMISSION_REQUEST_READ_STORAGE
            )
        }
    }

    companion object {
        @JvmStatic
        fun newInstance() = AttachmentDialogFragment()
        lateinit var currentPhotoPath: String

        @Throws(IOException::class)
        fun createImageFile(context: Context, extension: String?): File? {
            // Create an image file name
            val timeStamp =
                SimpleDateFormat(
                    Constants.dateTimeSecondFileNamePattern,
                    Locale.getDefault()
                ).format(Date())
            val imageFileName = "IMG_$timeStamp" + "_"
            val storageDir =
                context.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
            val image = File.createTempFile(
                imageFileName,  /* prefix */
                ".$extension",  /* suffix */
                storageDir /* directory */
            )

            // Save a file: path for use with ACTION_VIEW intents
            currentPhotoPath = image.absolutePath
            return image
        }
    }
}