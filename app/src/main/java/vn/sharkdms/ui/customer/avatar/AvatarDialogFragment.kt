package vn.sharkdms.ui.customer.avatar

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import androidx.fragment.app.DialogFragment
import kotlinx.android.synthetic.main.fragment_create_avatar_dialog.view.*
import vn.sharkdms.R
import vn.sharkdms.ui.customer.gallery.ErrorMessageDialogListener
import vn.sharkdms.util.Constant

class AvatarDialogFragment : DialogFragment() {

    companion object {
        private const val TAG = "AvatarDialogFragment"
        private const val REQUEST_IMAGE_CAPTURE = 1001
        private const val REQUEST_CHOOSE_IMAGE = 1002
    }

    private var check: Int? = 0

    private val cameraPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()) { permissionGranted ->
        if (permissionGranted) startCameraIntentForResult()
    }

    private lateinit var onPhotoSelectedListener: OnPhotoSelectedListener

    fun newInstance(check: Int): AvatarDialogFragment {
        val args = Bundle()
        args.putInt("check", check)
        val fragment = AvatarDialogFragment()
        fragment.arguments = args
        return fragment
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val rootView: View = inflater.inflate(R.layout.fragment_create_avatar_dialog, container, false)

        check = arguments?.getInt("check")

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
        if (check!! > 0) intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
        startActivityForResult(
            Intent.createChooser(intent, "Select Picture"),
            REQUEST_CHOOSE_IMAGE
        )
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            val bitmap: Bitmap = data?.extras?.get("data") as Bitmap
            onPhotoSelectedListener.getImageBitmap(bitmap)
            dismiss()
        } else if (requestCode == REQUEST_CHOOSE_IMAGE && resultCode == Activity.RESULT_OK) {
            // In this case, imageUri is returned by the chooser, save it.
            var imageUri: Uri?
            if(data?.data != null) {
                imageUri = data.data
                val bm = MediaStore.Images.Media.getBitmap(context?.contentResolver, imageUri)
                onPhotoSelectedListener.getImageBitmap(bm)
            }
            else if(data?.clipData != null) {
                val item = data.clipData
                if (item?.itemCount!! < 30 - check!!) {
                    for (i in 0 until item.itemCount) {
                        imageUri = item.getItemAt(i).uri
                        val bm = MediaStore.Images.Media.getBitmap(context?.contentResolver, imageUri)
                        onPhotoSelectedListener.getImageBitmap(bm)
                    }
                } else {
                    val message = "Vượt quá số lượng ảnh cho phép!\n" +
                            "Đã có " + check + " ảnh, chỉ được chọn thêm " + (30 - check!!).toString() +
                            " ảnh."
                    val listener: ErrorMessageDialogListener = targetFragment as ErrorMessageDialogListener
                    listener.onErrorDismissDialog(message)
                }
            }
            dismiss()
        } else
            super.onActivityResult(requestCode, resultCode, data)
    }

    override fun onAttach(context: Context) {
        try {
            onPhotoSelectedListener = targetFragment as OnPhotoSelectedListener

        } catch (e: ClassCastException) {
            Log.e(TAG, "onAttach: ClassCastException: " + e.message )
        }
        super.onAttach(context)
    }

}