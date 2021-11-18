package vn.sharkdms.ui.customer.avatar

import android.Manifest
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.fragment.app.DialogFragment
import com.bumptech.glide.Glide
import com.google.android.gms.common.wrappers.Wrappers
import dagger.hilt.android.internal.Contexts
import kotlinx.android.synthetic.main.fragment_create_avatar_dialog.view.*
import kotlinx.android.synthetic.main.fragment_create_customer.view.*
import vn.sharkdms.R
import vn.sharkdms.util.Constant
import java.io.File
import java.lang.ClassCastException
import java.text.SimpleDateFormat
import java.util.*

class AvatarDialogFragment : DialogFragment() {

    companion object {
        private const val TAG = "AvatarDialogFragment"
        private const val REQUEST_CODE = 1000
        private const val REQUEST_IMAGE_CAPTURE = 1001
        private const val REQUEST_CHOOSE_IMAGE = 1002
    }

    private val cameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()) { permissionGranted ->
        if (permissionGranted) startCameraIntentForResult()
    }

    interface OnPhotoSelectedListener {
        fun getImagePath(imageUri: Uri?)
        fun getImageBitmap(imageBitmap: Bitmap?)
    }
    private lateinit var onPhotoSelectedListener: OnPhotoSelectedListener

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val rootView: View = inflater.inflate(R.layout.fragment_create_avatar_dialog, container, false)

        initializeDialog()
        setRowTakePictureOnClickListener(rootView)
        setRowChooseImageOnClickListener(rootView)
        setBtnCancelOnClickListener(rootView)

        return rootView
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NO_TITLE, R.style.AppTheme_AppCompat_Dialog_Alert_NoFloating)
    }

    override fun onAttach(activity: Activity) {
        super.onAttach(activity)
        Constant.hideSoftKeyboard(requireActivity() as AppCompatActivity)
    }

    override fun onDetach() {
        super.onDetach()
        Constant.hideSoftKeyboard(requireActivity() as AppCompatActivity)
    }

    private fun initializeDialog() {
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog?.window?.setLayout(ConstraintLayout.LayoutParams.MATCH_PARENT, ConstraintLayout.LayoutParams.MATCH_PARENT)
        dialog?.window?.setGravity(Gravity.BOTTOM)
    }

    private fun setRowTakePictureOnClickListener(rootView: View) {
        rootView.row_take_picture.setOnClickListener {
            val cameraPermissionGranted = ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
            if (cameraPermissionGranted) startCameraIntentForResult()
            else cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    private fun setRowChooseImageOnClickListener(rootView: View) {
        rootView.row_choose_image.setOnClickListener {
            startChooseImageIntentForResult()
        }
    }

    private fun setBtnCancelOnClickListener(rootView: View) {
        rootView.btn_cancel.setOnClickListener {
            dismiss()
        }
    }

    private fun startCameraIntentForResult() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(
            Intent.createChooser(intent, "Take Photo"),
            REQUEST_IMAGE_CAPTURE
        )
    }

    private fun startChooseImageIntentForResult() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(
            Intent.createChooser(intent, "Select Picture"),
            REQUEST_CHOOSE_IMAGE
        )
    }

    private fun createTempFile(): File {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())
        val storageDir = requireContext().getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val file = File.createTempFile("JPEG_${timestamp}_", ".jpg", storageDir)
        file.deleteOnExit()
        return file
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            val bitmap: Bitmap = data?.extras?.get("data") as Bitmap
            onPhotoSelectedListener.getImageBitmap(bitmap)
            dismiss()
        } else if (requestCode == REQUEST_CHOOSE_IMAGE && resultCode == Activity.RESULT_OK) {
            // In this case, imageUri is returned by the chooser, save it.
            val imageUri: Uri? = data?.data
            val bm = MediaStore.Images.Media.getBitmap(context?.contentResolver, imageUri)
            onPhotoSelectedListener.getImageBitmap(bm)
            dismiss()
        } else
            super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onAttach(context: Context) {
        try {
            onPhotoSelectedListener = targetFragment as OnPhotoSelectedListener

        } catch (e: ClassCastException) {
            Log.e(TAG, "onAttach: ClassCastException: " + e.message );
        }
        super.onAttach(context)
    }

}