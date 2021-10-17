package vn.sharkdms.ui.customer.gallery

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import androidx.fragment.app.Fragment
import vn.sharkdms.R
import vn.sharkdms.databinding.FragmentCustomerGalleryBinding
import androidx.core.graphics.drawable.DrawableCompat

import androidx.core.content.ContextCompat

import android.provider.MediaStore
import android.util.Log
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import kotlinx.coroutines.flow.collect
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import vn.sharkdms.SharedViewModel
import vn.sharkdms.ui.customer.avatar.AvatarDialogFragment
import vn.sharkdms.ui.customer.create.CreateCustomerFragment
import vn.sharkdms.ui.customer.create.CreateCustomerViewModel
import vn.sharkdms.ui.customer.info.CustomerInfoFragmentArgs
import vn.sharkdms.util.Constant
import vn.sharkdms.util.HttpStatus
import java.io.*


class CustomerGalleryFragment : Fragment(R.layout.fragment_customer_gallery), AvatarDialogFragment.OnPhotoSelectedListener {

    private val TAG = "CustomerGalleryFragment"
    private val REQUEST_CODE = 1000

    private val args by navArgs<CustomerGalleryFragmentArgs>()

    private lateinit var binding: FragmentCustomerGalleryBinding
    var initImages = ArrayList<Bitmap>()
    private var image: ArrayList<MultipartBody.Part>? = null

    private lateinit var viewModel: CustomerGalleryViewModel
    private var connectivity: Boolean = false

    private lateinit var authorization: String
    private lateinit var sharedViewModel : SharedViewModel

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

                }
            }
        }
    }

    private fun bind(binding: FragmentCustomerGalleryBinding) {
        initImages.add(getBitmapFromVectorDrawable(R.drawable.ic_add_gallery))
        val imagesAdapter = GalleryAdapter(initImages, requireContext())
        binding.apply {
            gvGallery.adapter = imagesAdapter
            gvGallery.setOnItemClickListener { parent, view, position, id ->
                if (position == gvGallery.adapter.count - 1) {
                    val dialog = AvatarDialogFragment()
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
                val id: RequestBody = RequestBody.create(
                    MediaType.parse("multipart/form-data"), args.customer.customerId.toString())
                val address: RequestBody = RequestBody.create(
                    MediaType.parse("multipart/form-data"), args.customer.customerAddress)
                val lat: RequestBody = RequestBody.create(
                    MediaType.parse("multipart/form-data"), locationArray[0])
                val long: RequestBody = RequestBody.create(
                    MediaType.parse("multipart/form-data"), locationArray[1])
                for (bm: Bitmap in initImages) {
                    image?.add(prepareImagePartFromBitmap("image", bm))
                }
                viewModel.uploadGalleryRequest(authorization, id, address, lat, long, image)
            }
        }
    }

    fun getBitmapFromVectorDrawable(drawableId: Int): Bitmap {
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

    class GalleryAdapter(
        var images: List<Bitmap>,
        var context: Context
    ) : BaseAdapter() {
        var layoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

        override fun getCount(): Int {
            return images.size
        }

        override fun getItem(position: Int): Any {
            return images[position]
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            var view = convertView
            if (view == null) {
                view = layoutInflater.inflate(R.layout.item_add_gallery, parent, false)
            }
            val ivGallery = view?.findViewById<ImageView>(R.id.iv_add_gallery)
            ivGallery?.setImageBitmap(images[position])

            return view!!
        }

    }

    private fun prepareImagePartFromBitmap(partName: String, bitmap: Bitmap?): MultipartBody.Part {
        val file: File = convertBitmapToFile(partName, bitmap)
        val requestBody: RequestBody = RequestBody.create(MediaType.parse("image/*"), file)
        return MultipartBody.Part.createFormData(partName, file.name, requestBody)
    }

    private fun convertBitmapToFile(fileName: String, bitmap: Bitmap?): File {
        //create a file to write bitmap data
        val file = File(context?.cacheDir, fileName)
        file.createNewFile()

        //Convert bitmap to byte array
        val bos = ByteArrayOutputStream()
        bitmap?.compress(Bitmap.CompressFormat.JPEG, 0 /*ignored for PNG*/, bos)
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
        initImages.add(getBitmapFromVectorDrawable(R.drawable.ic_add_customer))
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
        initImages.add(getBitmapFromVectorDrawable(R.drawable.ic_add_customer))
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
                Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
                findNavController().navigateUp()
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