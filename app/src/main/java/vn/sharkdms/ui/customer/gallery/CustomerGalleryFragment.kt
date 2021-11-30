package vn.sharkdms.ui.customer.gallery

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.DrawableCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import kotlinx.coroutines.flow.collect
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import vn.sharkdms.R
import vn.sharkdms.SharedViewModel
import vn.sharkdms.databinding.FragmentCustomerGalleryBinding
import vn.sharkdms.ui.customer.avatar.AvatarDialogFragment
import vn.sharkdms.ui.customer.avatar.OnPhotoSelectedListener
import vn.sharkdms.util.Constant
import vn.sharkdms.util.HttpStatus
import vn.sharkdms.util.Utils
import java.io.*
import android.graphics.BitmapFactory





class CustomerGalleryFragment : Fragment(R.layout.fragment_customer_gallery), OnPhotoSelectedListener {

    private val TAG = "CustomerGalleryFragment"
    private val REQUEST_CODE = 1000

    private val args by navArgs<CustomerGalleryFragmentArgs>()

    private lateinit var binding: FragmentCustomerGalleryBinding
    var initImages = ArrayList<Bitmap>()

    private lateinit var viewModel: CustomerGalleryViewModel
    private var connectivity: Boolean = false

    private lateinit var authorization: String
    private lateinit var sharedViewModel : SharedViewModel
    private var isFullImage = false

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentCustomerGalleryBinding.bind(view)
        bind(binding)
        setBtnBackOnClickListener(binding)
        setBtnUploadOnClickListener(binding)
        viewModel = ViewModelProvider(requireActivity())[CustomerGalleryViewModel::class.java]

        sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]
        authorization = Constant.TOKEN_PREFIX.plus(sharedViewModel.token)
        sharedViewModel.connectivity.observe(viewLifecycleOwner) { connectivity = it ?: false }
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.customerGalleryEvent.collect { event ->
                when(event) {
                    is CustomerGalleryViewModel.CustomerGalleryEvent.OnResponse ->
                        handleGalleryResponse(binding, event.code, event.message)
                    is CustomerGalleryViewModel.CustomerGalleryEvent.OnFailure ->
                        handleGalleryFailure(binding)

                    is CustomerGalleryViewModel.CustomerGalleryEvent.ShowUnauthorizedDialog ->
                        Utils.showUnauthorizedDialog(requireActivity())
                }
            }
        }

        childFragmentManager.setFragmentResultListener("overLimit", viewLifecycleOwner) { key, bundle ->
            val message = bundle.getString("bundleKey")
            Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Constant.hideSoftKeyboard(requireActivity() as AppCompatActivity)
    }

    override fun onDetach() {
        super.onDetach()
        Constant.hideSoftKeyboard(requireActivity() as AppCompatActivity)
    }

    private fun bind(binding: FragmentCustomerGalleryBinding) {
        initImages.add(getBitmapFromVectorDrawable(R.drawable.ic_add_gallery))
        val imagesAdapter = GalleryAdapter(initImages, requireContext())
        binding.apply {
            gvGallery.adapter = imagesAdapter
            gvGallery.setOnItemClickListener { parent, view, position, id ->
                if (position == gvGallery.adapter.count - 1) {
                    val dialog = AvatarDialogFragment()
                        .newInstance(if (initImages.size - 1 != 0) initImages.size - 1 else 1)
                    dialog.show(requireFragmentManager(), TAG)
                    dialog.setTargetFragment(this@CustomerGalleryFragment, REQUEST_CODE)
                }
            }
        }
    }

    private fun setBtnBackOnClickListener(binding: FragmentCustomerGalleryBinding) {
        binding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setBtnUploadOnClickListener(binding: FragmentCustomerGalleryBinding) {
        val locationArray = args.customer.customerPosition.split("_").toTypedArray()
        binding.apply {
            btnUpload.setOnClickListener {
                if (!connectivity) {
                    Toast.makeText(requireContext(),
                        getString(R.string.message_connectivity_off), Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }
                btnUpload.isEnabled = false
                btnUpload.text = ""
                progressBar.visibility = View.VISIBLE
                if (!isFullImage) initImages.removeLast()
                val builder: MultipartBody.Builder = MultipartBody.Builder().setType(MultipartBody.FORM)
                builder.addFormDataPart("user_kh_id", args.customer.customerId.toString())
                if(args.customer.customerAddress != null)
                    builder.addFormDataPart("address", args.customer.customerAddress)
                if (locationArray.isNotEmpty())
                    builder.addFormDataPart("lat", locationArray[0])
                        .addFormDataPart("long", locationArray[1])
                for (bm in initImages) {
                    val file: File = convertBitmapToFile("image", bm)
                    val bmp = BitmapFactory.decodeFile(file.getAbsolutePath())
                    val bos = ByteArrayOutputStream()
                    bmp.compress(Bitmap.CompressFormat.JPEG, 100, bos);
                    builder.addFormDataPart("image[]", file.name, RequestBody.create(MultipartBody.FORM, bos.toByteArray()))
                }
                viewModel.uploadGalleryRequest(authorization, builder.build())
            }
        }
    }

    private fun getBitmapFromVectorDrawable(drawableId: Int): Bitmap {
        var drawable = ContextCompat.getDrawable(requireContext(), drawableId)
        drawable = DrawableCompat.wrap(drawable!!).mutate()
        val bitmap = Bitmap.createBitmap(
            drawable.intrinsicWidth,
            drawable.intrinsicHeight, Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight())
        drawable.draw(canvas)
        return bitmap
    }

    private fun convertBitmapToFile(fileName: String, bitmap: Bitmap?): File {
        //create a file to write bitmap data
        val file = File(context?.cacheDir, fileName)
        file.createNewFile()

        //Convert bitmap to byte array
        val bos = ByteArrayOutputStream()
        bitmap?.compress(Bitmap.CompressFormat.JPEG, 100 /*ignored for PNG*/, bos)
        val bitMapData = bos.toByteArray()

        //write the bytes in file
        var fos: FileOutputStream? = null
        try {
            fos = FileOutputStream(file)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        try {
            fos?.write(bitMapData)
            fos?.flush()
            fos?.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return file
    }

    override fun getImagePath(imageUri: Uri?) {
        val bitmap = MediaStore.Images.Media.getBitmap(context?.contentResolver, imageUri)
        initImages.removeLast()
        initImages.add(bitmap)
        if (initImages.size < 30) initImages.add(getBitmapFromVectorDrawable(R.drawable.ic_add_gallery))
        else isFullImage = true
        val imagesAdapter = GalleryAdapter(initImages, requireContext())
        binding.apply {
            gvGallery.adapter = imagesAdapter
            btnUpload.isEnabled = true
            btnUpload.setBackgroundResource(R.drawable.button_primary)
        }
    }

    override fun getImageBitmap(imageBitmap: Bitmap?) {
        initImages.removeLast()
        initImages.add(imageBitmap!!)
        if (initImages.size < 30) initImages.add(getBitmapFromVectorDrawable(R.drawable.ic_add_gallery))
        else isFullImage = true
        val imagesAdapter = GalleryAdapter(initImages, requireContext())
        binding.apply {
            gvGallery.adapter = imagesAdapter
            btnUpload.isEnabled = true
            btnUpload.setBackgroundResource(R.drawable.button_primary)
        }
    }

    private fun handleGalleryResponse(binding: FragmentCustomerGalleryBinding, code: Int, message: String) {
        binding.apply {
            progressBar.visibility = View.GONE
            btnUpload.text = getString(R.string.fragment_customer_gallery_upload)
        }
        when (code) {
            HttpStatus.OK -> {
                val dialog = SuccessDialogFragment()
                dialog.show(requireFragmentManager(), TAG)
            }
            HttpStatus.BAD_REQUEST, HttpStatus.FORBIDDEN -> Toast.makeText(requireContext(),
                message, Toast.LENGTH_SHORT).show()
            else -> Log.e(TAG, code.toString())
        }
    }

    private fun handleGalleryFailure(binding: FragmentCustomerGalleryBinding) {
        binding.apply {
            progressBar.visibility = View.GONE
            btnUpload.text = getString(R.string.fragment_customer_gallery_upload)
        }
        Toast.makeText(requireContext(), getString(R.string.message_connectivity_off),
            Toast.LENGTH_SHORT).show()
    }
}