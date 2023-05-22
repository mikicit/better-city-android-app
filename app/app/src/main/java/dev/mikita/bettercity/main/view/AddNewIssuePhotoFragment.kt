package dev.mikita.bettercity.main.view

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import coil.api.load
import dagger.hilt.android.AndroidEntryPoint
import dev.mikita.bettercity.databinding.FragmentAddNewIssuePhotoBinding
import dev.mikita.bettercity.main.viewmodel.AddNewIssueViewModel
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@AndroidEntryPoint
class AddNewIssuePhotoFragment : Fragment() {
    // ViewBinding
    private var _binding: FragmentAddNewIssuePhotoBinding? = null
    private val binding get() = _binding!!

    // ViewModel
    private val viewModel: AddNewIssueViewModel by activityViewModels()

    // Utils
    private lateinit var photoFile: File
    private lateinit var photoUri: Uri
    private lateinit var picturePermissionLauncher: ActivityResultLauncher<Uri>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddNewIssuePhotoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupPicturePermissionLauncher()
        setUpEventListeners()
        setUpObservers()

        viewModel.isValidScreen.value = false
    }

    private fun setUpEventListeners() {
        binding.addNewIssueMakePhotoButton.setOnClickListener {
            checkCameraPermissionAndOpenCamera()
        }

        binding.addNewIssueAddPhotoButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, REQUEST_IMAGE_PICK)
        }
    }

    private fun setUpObservers() {
        viewModel.getPhotoLiveData().observe(viewLifecycleOwner) {
            binding.addNewIssuePhoto.load(it)
            if (it != null) {
                viewModel.isValidScreen.value = true
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Gallery
        if (requestCode == REQUEST_IMAGE_PICK && resultCode == Activity.RESULT_OK && data != null) {
            val imageUri: Uri? = data.data
            if (imageUri != null) {
                getFileFromUri(imageUri)?.let { viewModel.updatePhoto(it) }
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        when (requestCode) {
            REQUEST_IMAGE_CAPTURE -> {
                if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    photoFile = createPhotoFile()
                    photoUri = createPhotoUri(photoFile)
                    picturePermissionLauncher.launch(photoUri)
                } else {
                    Toast.makeText(requireContext(), "Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun getFileFromUri(uri: Uri): File? {
        val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
        val cursor: Cursor? = requireActivity().contentResolver.query(uri, filePathColumn, null, null, null)
        cursor?.moveToFirst()

        cursor?.let {
            val columnIndex: Int = cursor.getColumnIndex(filePathColumn[0])
            val filePath: String = cursor.getString(columnIndex)
            cursor.close()
            return File(filePath)
        }

        return null
    }

    private fun checkCameraPermissionAndOpenCamera() {
        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.CAMERA), REQUEST_IMAGE_CAPTURE)
        } else {
            photoFile = createPhotoFile()
            photoUri = createPhotoUri(photoFile)
            picturePermissionLauncher.launch(photoUri)
        }
    }

    private fun createPhotoFile(): File {
        val timeStamp: String =
            SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val storageDir: File? = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)

        return File.createTempFile("JPEG_${timeStamp}_", ".jpg", storageDir)
    }

    private fun createPhotoUri(file: File): Uri {
        return FileProvider.getUriForFile(requireContext(), "dev.mikita.bettercity.fileprovider", file)
    }

    private fun setupPicturePermissionLauncher() {
        picturePermissionLauncher =
            registerForActivityResult(ActivityResultContracts.TakePicture()) { isLoaded ->
                try {
                    if (isLoaded) {
                        viewModel.updatePhoto(photoFile)
                    }
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private const val REQUEST_IMAGE_CAPTURE = 1
        private const val REQUEST_IMAGE_PICK = 2
    }
}