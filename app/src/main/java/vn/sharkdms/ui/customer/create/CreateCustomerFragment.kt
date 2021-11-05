package vn.sharkdms.ui.customer.create

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.drawable.Drawable
import android.location.Location
import android.location.LocationManager
import android.net.Uri

import android.os.Bundle
import android.os.Looper
import android.provider.MediaStore
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.NonNull
import androidx.appcompat.content.res.AppCompatResources
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import kotlinx.coroutines.flow.collect
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import vn.sharkdms.R
import vn.sharkdms.SharedViewModel
import vn.sharkdms.databinding.FragmentCreateCustomerBinding
import vn.sharkdms.ui.customer.avatar.AvatarDialogFragment
import vn.sharkdms.util.Constant
import vn.sharkdms.util.HttpStatus
import vn.sharkdms.util.Validator
import java.io.*

class CreateCustomerFragment: Fragment(R.layout.fragment_create_customer), AvatarDialogFragment.OnPhotoSelectedListener {

    companion object {
        const val TAG = "CreateCustomerFragment"
        private const val CHANGE_NAME = Activity.RESULT_FIRST_USER
        private const val CHANGE_USERNAME = CHANGE_NAME + 1
        private const val CHANGE_PASSWORD = CHANGE_USERNAME + 1
        private const val CHANGE_PHONE = CHANGE_PASSWORD + 1
        private const val CHANGE_EMAIL = CHANGE_PHONE + 1
        private const val CHANGE_ADDRESS = CHANGE_EMAIL + 1
        private const val PERMISSION_ID = 42
        private const val REQUEST_CODE = 1000
    }

    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var binding: FragmentCreateCustomerBinding

    private lateinit var viewModel: CreateCustomerViewModel
    private var connectivity: Boolean = false

    private var latitude: String = ""
    private var longitude: String = ""
    private var imageUri: Uri? = null
    private var bitmap: Bitmap? = null
    private var image: MultipartBody.Part? = null
    private lateinit var authorization: String
    private lateinit var sharedViewModel : SharedViewModel

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding = FragmentCreateCustomerBinding.bind(view)
        val clearIcon = AppCompatResources.getDrawable(requireContext(), R.drawable.ic_clear)
        val hideIcon = AppCompatResources.getDrawable(requireContext(), R.drawable.ic_hide)
        val showIcon = AppCompatResources.getDrawable(requireContext(), R.drawable.ic_show)
        viewModel = ViewModelProvider(requireActivity())[CreateCustomerViewModel::class.java]

        sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]
        authorization = Constant.TOKEN_PREFIX.plus(sharedViewModel.token)
        sharedViewModel.connectivity.observe(viewLifecycleOwner) { connectivity = it ?: false }

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        setEditTextListener(binding, clearIcon, hideIcon, showIcon)
        setBtnBackOnClickListener(binding)
        setBtnGetGpsOnClickListener(binding)
        setBtnCreateCustomerOnClickListener(binding)
        setIvAvatarOnClickListener(binding)
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            viewModel.createCustomerEvent.collect { event ->
                when(event) {
                    is CreateCustomerViewModel.CreateCustomerEvent.OnResponse ->
                        handleCreateCustomerResponse(binding, event.code, event.status, event.message, event.data)
                    is CreateCustomerViewModel.CreateCustomerEvent.OnFailure ->
                        handleCreateCustomerFailure(binding)

                }
            }
        }
    }

    private fun initTextWatcher(binding: FragmentCreateCustomerBinding, change: Int): TextWatcher {
        val textWatcher = object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun afterTextChanged(p0: Editable?) {
                afterTextChanged(binding, change)
            }
        }
        return textWatcher
    }

    private fun initOnTouchListener(binding: FragmentCreateCustomerBinding, editText: EditText, clearIcon: Drawable?,
            hideIcon: Drawable?, showIcon: Drawable?, isPass: Boolean, change: Int): View.OnTouchListener {
        val onTouchListener = object : View.OnTouchListener {
            @SuppressLint("ClickableViewAccessibility")
            override fun onTouch(view: View?, event: MotionEvent?): Boolean {
                if (!isPass) {
                    if (view == null || event == null || clearIcon == null || event.action !=
                        MotionEvent.ACTION_UP) return false
                    val currentClearIcon = editText.compoundDrawablesRelative[2]
                    if (event.rawX < editText.right - clearIcon.bounds.width() -
                        editText.paddingEnd * 2 || currentClearIcon == null) {
                        return false
                    }
                    editText.text.clear()
                    afterTextChanged(binding, change)
                    return true
                } else {
                    if (view == null || event == null || hideIcon == null || showIcon == null
                        || event.action != MotionEvent.ACTION_UP) return false
                    val currentEndIcon = editText.compoundDrawablesRelative[2]
                    if (event.rawX < editText.right - hideIcon.bounds.width() -
                        editText.paddingEnd * 2 || currentEndIcon == null) return false
                    val start = editText.selectionStart
                    val end = editText.selectionEnd
                    val currentPasswordIcon = editText.compoundDrawablesRelative[0]
                    if (editText.transformationMethod == null) {
                        editText.transformationMethod = PasswordTransformationMethod()
                        editText.setCompoundDrawablesRelativeWithIntrinsicBounds(
                            currentPasswordIcon, null, hideIcon, null)
                    } else {
                        editText.transformationMethod = null
                        editText.setCompoundDrawablesRelativeWithIntrinsicBounds(
                            currentPasswordIcon, null, showIcon, null)
                    }
                    editText.setSelection(start, end)
                    return true
                }
            }
        }
        return onTouchListener
    }

    private fun setEditTextListener(binding: FragmentCreateCustomerBinding, clearIcon: Drawable?,
            hideIcon: Drawable?, showIcon: Drawable?) {
        setEditTextTextChangedListener(binding)
        setEditTextOnTouchListener(binding, clearIcon, hideIcon, showIcon)
    }

    private fun setEditTextTextChangedListener(binding: FragmentCreateCustomerBinding) {
        binding.apply {
            etCreateCustomerName.addTextChangedListener(initTextWatcher(binding, CHANGE_NAME))
            etCreateCustomerAccount.addTextChangedListener(initTextWatcher(binding, CHANGE_USERNAME))
            etCreateCustomerPassword.addTextChangedListener(initTextWatcher(binding, CHANGE_PASSWORD))
            etCreateCustomerPhone.addTextChangedListener(initTextWatcher(binding, CHANGE_PHONE))
            etCreateCustomerEmail.addTextChangedListener(initTextWatcher(binding, CHANGE_EMAIL))
            etCreateCustomerAddress.addTextChangedListener(initTextWatcher(binding, CHANGE_ADDRESS))
        }
    }

    private fun setEditTextOnTouchListener(binding: FragmentCreateCustomerBinding,
            clearIcon: Drawable?, hideIcon: Drawable?, showIcon: Drawable?) {
        binding.apply {
            etCreateCustomerName.apply {
                setOnTouchListener(initOnTouchListener(binding, etCreateCustomerName,
                    clearIcon, hideIcon, showIcon, false, CHANGE_NAME
                ))
            }
            etCreateCustomerAccount.apply {
                setOnTouchListener(initOnTouchListener(binding, etCreateCustomerAccount,
                    clearIcon, hideIcon, showIcon, false, CHANGE_USERNAME
                ))
            }
            etCreateCustomerPassword.apply {
                setOnTouchListener(initOnTouchListener(binding, etCreateCustomerPassword,
                    clearIcon, hideIcon, showIcon, true, CHANGE_PASSWORD
                ))
            }
            etCreateCustomerPhone.apply {
                setOnTouchListener(initOnTouchListener(binding, etCreateCustomerPhone,
                    clearIcon, hideIcon, showIcon, false, CHANGE_PHONE
                ))
            }
            etCreateCustomerEmail.apply {
                setOnTouchListener(initOnTouchListener(binding, etCreateCustomerEmail,
                    clearIcon, hideIcon, showIcon, false, CHANGE_EMAIL
                ))
            }
            etCreateCustomerAddress.apply {
                setOnTouchListener(initOnTouchListener(binding, etCreateCustomerAddress,
                    clearIcon, hideIcon, showIcon, false, CHANGE_ADDRESS
                ))
            }
        }
    }

    private fun afterTextChanged(binding: FragmentCreateCustomerBinding, change: Int) {
        val name = binding.etCreateCustomerName.text.toString()
        val username = binding.etCreateCustomerAccount.text.toString()
        val password = binding.etCreateCustomerPassword.text.toString()
        val phone = binding.etCreateCustomerPhone.text.toString()
        val email = binding.etCreateCustomerEmail.text.toString()
        val address = binding.etCreateCustomerAddress.text.toString()
        val validName = Validator.isValidName(name)
        val validUsername = Validator.isValidUsername(username)
        val validPassword = Validator.isValidPassword(password)
        val validPhone = Validator.isValidPhone(phone)
        var validEmail: Boolean = false
        if (email.trim().isNotEmpty()) validEmail = Validator.isValidEmail(email)
        val validAddress = Validator.isValidAddress(address)
        when (change) {
            CHANGE_NAME -> {
                val clearIcon = if (name.isNotEmpty() && binding.etCreateCustomerName.hasFocus())
                    R.drawable.ic_clear else 0
                binding.etCreateCustomerName.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0,
                    clearIcon, 0)
                if (validName) binding.tvCustomerNameError.visibility = View.GONE
                else binding.tvCustomerNameError.visibility = View.VISIBLE
            }
            CHANGE_USERNAME -> {
                val clearIcon = if (username.isNotEmpty() && binding.etCreateCustomerAccount.hasFocus())
                    R.drawable.ic_clear else 0
                binding.etCreateCustomerAccount.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0,
                    clearIcon, 0)
                if (validUsername) binding.tvCustomerAccountNote.setTextColor(Color.parseColor("#00549A"))
                else binding.tvCustomerAccountNote.setTextColor(Color.RED)
            }
            CHANGE_PASSWORD -> {
                var endIcon = 0
                if (password.isNotEmpty() && binding.etCreateCustomerPassword.hasFocus()) {
                    endIcon = if (binding.etCreateCustomerPassword.transformationMethod == null) R.drawable.ic_show else
                        R.drawable.ic_hide
                }
                binding.etCreateCustomerPassword.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0, endIcon,
                    0)
                if (validPassword) binding.tvCustomerPasswordNote.setTextColor(Color.parseColor("#00549A"))
                else binding.tvCustomerPasswordNote.setTextColor(Color.RED)
            }
            CHANGE_PHONE -> {
                val clearIcon = if (phone.isNotEmpty() && binding.etCreateCustomerPhone.hasFocus())
                    R.drawable.ic_clear else 0
                binding.etCreateCustomerPhone.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0,
                    clearIcon, 0)
                if (validPhone) binding.tvCustomerPhoneError.visibility = View.GONE
                else binding.tvCustomerPhoneError.visibility = View.VISIBLE
            }
            CHANGE_EMAIL -> {
                val clearIcon = if (email.isNotEmpty() && binding.etCreateCustomerEmail.hasFocus())
                    R.drawable.ic_clear else 0
                binding.etCreateCustomerEmail.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0,
                    clearIcon, 0)
                if (email.trim().isNotEmpty() && validEmail || email.trim().isEmpty())
                    binding.tvCustomerEmailError.visibility = View.GONE
                else binding.tvCustomerEmailError.visibility = View.VISIBLE
            }
            CHANGE_ADDRESS -> {
                val clearIcon = if (address.isNotEmpty() && binding.etCreateCustomerAddress.hasFocus())
                    R.drawable.ic_clear else 0
                binding.etCreateCustomerAddress.setCompoundDrawablesRelativeWithIntrinsicBounds(0, 0,
                    clearIcon, 0)
                if (validAddress) binding.tvCustomerAddressError.visibility = View.GONE
                else binding.tvCustomerAddressError.visibility = View.VISIBLE
            }
        }
        if ((validAddress && validName && validPassword && validPhone && validUsername && email.trim().isNotEmpty() && validEmail)
                || (validAddress && validName && validPassword && validPhone && validUsername && email.trim().isEmpty())) {
            binding.apply {
                btnCreateCustomer.isEnabled = true
                btnCreateCustomer.setBackgroundResource(R.drawable.button_primary)
            }
        } else {
            binding.apply {
                btnCreateCustomer.isEnabled = false
                btnCreateCustomer.setBackgroundResource(R.drawable.button_disable)
            }
        }
    }

    private fun setBtnCreateCustomerOnClickListener(binding: FragmentCreateCustomerBinding) {
        binding.apply {
            btnCreateCustomer.setOnClickListener {
                if (!connectivity) {
                    Toast.makeText(requireContext(),
                        getString(R.string.message_connectivity_off), Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }
                val builder: MultipartBody.Builder = MultipartBody.Builder()
                builder.setType(MultipartBody.FORM)
                btnCreateCustomer.isEnabled = false
                btnCreateCustomer.text = ""
                progressBar.visibility = View.VISIBLE
//                builder.addFormDataPart("name", etCreateCustomerName.text.toString())
//                builder.addFormDataPart("username", etCreateCustomerAccount.text.toString())
//                builder.addFormDataPart("password", etCreateCustomerPassword.text.toString())
//                builder.addFormDataPart("phone", etCreateCustomerPhone.text.toString())
//                builder.addFormDataPart("email", etCreateCustomerEmail.text.toString())
//                builder.addFormDataPart("address", etCreateCustomerAddress.text.toString())
//                builder.addFormDataPart("lat", latitude)
//                builder.addFormDataPart("long", longitude)
                val name: RequestBody = RequestBody.create(
                    MediaType.parse("multipart/form-data"), etCreateCustomerName.text.toString())
                val username: RequestBody = RequestBody.create(
                        MediaType.parse("multipart/form-data"), etCreateCustomerAccount.text.toString())
                val password: RequestBody = RequestBody.create(
                        MediaType.parse("multipart/form-data"), etCreateCustomerPassword.text.toString())
                val phone: RequestBody = RequestBody.create(
                        MediaType.parse("multipart/form-data"), etCreateCustomerPhone.text.toString())
                val email: RequestBody = RequestBody.create(
                        MediaType.parse("multipart/form-data"), etCreateCustomerEmail.text.toString())
                val address: RequestBody = RequestBody.create(
                        MediaType.parse("multipart/form-data"), etCreateCustomerAddress.text.toString())
                val lat: RequestBody = RequestBody.create(
                    MediaType.parse("multipart/form-data"), latitude)
                val long: RequestBody = RequestBody.create(
                    MediaType.parse("multipart/form-data"), longitude)
                if(imageUri != null) image = prepareImagePartFromUri("image", imageUri, builder)
                else if (bitmap != null) image = prepareImagePartFromBitmap("image", bitmap, builder)
                viewModel.sendCreateCustomerRequest(authorization, name, username, password, address,
                    lat, long, phone, email, image)
            }
        }
    }

    private fun setBtnBackOnClickListener(binding: FragmentCreateCustomerBinding) {
        binding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun setBtnGetGpsOnClickListener(binding: FragmentCreateCustomerBinding) {
        binding.apply {
            btnAddGps.setOnClickListener {
                getLastLocation()
            }
        }
    }

    private fun setIvAvatarOnClickListener(binding: FragmentCreateCustomerBinding) {
        binding.ivAvatarCreateCustomer.setOnClickListener {
            val dialog = AvatarDialogFragment()
            dialog.show(requireFragmentManager(), TAG)
            dialog.setTargetFragment(this, REQUEST_CODE)
        }
    }

    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                fusedLocationClient.lastLocation.addOnCompleteListener(requireActivity()) { task ->
                    var location: Location? = task.result
                    if (location == null) {
                        requestNewLocationData()
                    } else {
                        latitude = location.latitude.toString()
                        longitude = location.longitude.toString()
                        binding.tvShowLocation.text = location.latitude.toString().plus(" : ")
                            .plus(location.longitude.toString())
                    }
                }
            } else {
                Toast.makeText(requireActivity(), "Hãy bật vị trí của bạn", Toast.LENGTH_LONG).show()
                val intent = Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS)
                startActivity(intent)
            }
        } else {
            requestPermissions()
        }
    }

    @SuppressLint("MissingPermission")
    private fun requestNewLocationData() {
        val locationRequest = LocationRequest()
        locationRequest.priority = LocationRequest.PRIORITY_HIGH_ACCURACY
        locationRequest.interval = 0
        locationRequest.fastestInterval = 0
        locationRequest.numUpdates = 1

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
        fusedLocationClient!!.requestLocationUpdates(
            locationRequest, locationCallback, Looper.myLooper()
        )
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            var lastLocation: Location = locationResult.lastLocation
            latitude = lastLocation.latitude.toString()
            longitude = lastLocation.longitude.toString()
            binding.tvShowLocation.text = "Lat: ".plus(lastLocation.latitude.toString()).plus("\n")
                                            .plus("Long: ").plus(lastLocation.longitude.toString())
        }
    }

    private fun isLocationEnabled(): Boolean {
        var locationManager: LocationManager = context?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) || locationManager.isProviderEnabled(
            LocationManager.NETWORK_PROVIDER
        )
    }

    private fun checkPermissions(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_COARSE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED &&
            ActivityCompat.checkSelfPermission(
                requireActivity(),
                Manifest.permission.ACCESS_FINE_LOCATION
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            return true
        }
        return false
    }

    private fun requestPermissions() {
        ActivityCompat.requestPermissions(
            requireActivity(),
            arrayOf(Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION),
            PERMISSION_ID
        )
    }


    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        if (requestCode == PERMISSION_ID) {
            if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                getLastLocation()
            }
        }
    }

    private fun handleCreateCustomerResponse(binding: FragmentCreateCustomerBinding, code: Int, status: String?, message: String, data: CreateCustomerAccount?) {
        binding.apply {
            progressBar.visibility = View.GONE
            btnCreateCustomer.text = getString(R.string.fragment_create_customer_create_button)
        }
        when (code) {
            HttpStatus.OK -> {
                Toast.makeText(requireContext(),
                    message.plus("\nAccount: ").plus(data?.account).plus(" / Password: ")
                        .plus(data?.password), Toast.LENGTH_LONG).show()
                findNavController().navigateUp()
            }
            HttpStatus.BAD_REQUEST, HttpStatus.FORBIDDEN -> Toast.makeText(requireContext(),
                status?.toUpperCase().plus("! ").plus(message), Toast.LENGTH_SHORT).show()
            else -> Log.e(TAG, code.toString())
        }
    }

    private fun handleCreateCustomerFailure(binding: FragmentCreateCustomerBinding) {
        binding.apply {
            progressBar.visibility = View.GONE
            btnCreateCustomer.text = getString(R.string.fragment_create_customer_create_button)
        }
        Toast.makeText(requireContext(), getString(R.string.message_connectivity_off),
            Toast.LENGTH_SHORT).show()
    }

    private fun prepareImagePartFromUri(partName: String, imageUri: Uri?, builder: MultipartBody.Builder): MultipartBody.Part {
        val bitmap = MediaStore.Images.Media.getBitmap(context?.contentResolver, imageUri)
        return prepareImagePartFromBitmap(partName, bitmap, builder)
    }

    private fun prepareImagePartFromBitmap(partName: String, bitmap: Bitmap?, builder: MultipartBody.Builder): MultipartBody.Part {
        val file: File = convertBitmapToFile(partName, bitmap)
        val requestBody: RequestBody = RequestBody.create(MediaType.parse("multipart/form-data"), file)
//        return builder.addFormDataPart(
//            "image[]",
//            file.name,
//            RequestBody.create(MediaType.parse("image/*"),
//            file)).build()
        return MultipartBody.Part.createFormData("image[]", file.name, requestBody)
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
        Glide.with(this@CreateCustomerFragment).load(imageUri).circleCrop()
            .into(binding.ivAvatarCreateCustomer)
        binding.tvAddAvatar.visibility = View.INVISIBLE
        this.imageUri = imageUri
        this.bitmap = null
    }

    override fun getImageBitmap(imageBitmap: Bitmap?) {
        binding.ivAvatarCreateCustomer.setImageBitmap(imageBitmap)
        binding.tvAddAvatar.visibility = View.INVISIBLE
        this.bitmap = imageBitmap
        this.imageUri = null
    }
}