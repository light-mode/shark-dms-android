package vn.sharkdms.ui.account

import android.Manifest
import android.app.Activity
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.os.bundleOf
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.setFragmentResult
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import vn.sharkdms.R
import vn.sharkdms.SharedViewModel
import vn.sharkdms.databinding.DialogImageChooserBinding
import vn.sharkdms.util.Utils
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.*

@AndroidEntryPoint
class ImageChooserDialogFragment : DialogFragment() {
    companion object {
        const val UPLOAD_AVATAR = "UPLOAD_AVATAR"
        const val IMAGE_URL = "IMAGE_URL"
    }

    private val viewModel by viewModels<ImageChooserViewModel>()
    private val sharedViewModel by activityViewModels<SharedViewModel>()

    private lateinit var binding: DialogImageChooserBinding

    private val cameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()) { permissionGranted ->
        if (permissionGranted) launchCameraIntent()
    }

    private val cameraLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode != Activity.RESULT_OK) return@registerForActivityResult
        doBeforeRequest()
        viewModel.uploadAvatar(sharedViewModel.token)
    }

    private val getImageLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode != Activity.RESULT_OK) return@registerForActivityResult
        doBeforeRequest()
        result.data?.data?.let { uri ->
            val file = createTempFile()
            viewModel.currentPhotoPath = file.absolutePath
            val inputStream = requireActivity().contentResolver.openInputStream(uri)
            inputStream?.use { input ->
                val outputStream = FileOutputStream(file)
                outputStream.use { output ->
                    val buffer = ByteArray(4 * 1024)
                    while (true) {
                        val byteCount = input.read(buffer)
                        if (byteCount < 0) break
                        output.write(buffer, 0, byteCount)
                    }
                    output.flush()
                    viewModel.uploadAvatar(sharedViewModel.token)
                }
            }
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?): View {
        isCancelable = false
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        return inflater.inflate(R.layout.dialog_image_chooser, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        dialog?.window?.setLayout(ConstraintLayout.LayoutParams.MATCH_PARENT,
            ConstraintLayout.LayoutParams.MATCH_PARENT)
        super.onViewCreated(view, savedInstanceState)
        binding = DialogImageChooserBinding.bind(view)
        setTakePhotoButtonListener()
        setSelectImageButtonListener()
        setCancelButtonListener()
        setEventListener()
    }

    private fun setTakePhotoButtonListener() {
        binding.buttonTakePhoto.setOnClickListener {
            val cameraPermissionGranted = ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
            if (cameraPermissionGranted) launchCameraIntent()
            else cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun launchCameraIntent() {
        val file = createTempFile()
        viewModel.currentPhotoPath = file.absolutePath
        val photoUri = FileProvider.getUriForFile(requireContext(), "vn.sharkdms", file)
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri)
        cameraLauncher.launch(intent)
    }

    private fun createTempFile(): File {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val storageDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val file = File.createTempFile("JPEG_${timestamp}_", ".jpg", storageDir)
        file.deleteOnExit()
        return file
    }

    private fun setSelectImageButtonListener() {
        binding.buttonSelectImage.setOnClickListener {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            getImageLauncher.launch(intent)
        }
    }

    private fun setCancelButtonListener() {
        binding.buttonCancel.setOnClickListener {
            dismiss()
        }
    }

    private fun setEventListener() {
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.imageChooserEvent.collect { event ->
                when (event) {
                    is ImageChooserViewModel.ImageChooserEvent.OnUploadAvatarSuccess -> {
                        Toast.makeText(requireContext(), event.message, Toast.LENGTH_SHORT).show()
                        dismiss()
                    }
                    is ImageChooserViewModel.ImageChooserEvent.OnFailure -> {
                        doAfterResponse()
                        Utils.showConnectivityOffMessage(requireContext())
                    }
                    is ImageChooserViewModel.ImageChooserEvent.ShowUnauthorizedDialog -> {
                        doAfterResponse()
                        Utils.showUnauthorizedDialog(requireActivity())
                    }
                }
            }
        }
    }

    private fun doBeforeRequest() {
        binding.apply {
            cardView1.visibility = View.GONE
            cardView2.visibility = View.GONE
            progressBar.visibility = View.VISIBLE
        }
    }

    private fun doAfterResponse() {
        binding.apply {
            progressBar.visibility = View.GONE
            cardView1.visibility = View.VISIBLE
            cardView2.visibility = View.VISIBLE
        }
    }

    override fun onDismiss(dialog: DialogInterface) {
        super.onDismiss(dialog)
        setFragmentResult(UPLOAD_AVATAR, bundleOf(IMAGE_URL to viewModel.imageUrl))
    }
}