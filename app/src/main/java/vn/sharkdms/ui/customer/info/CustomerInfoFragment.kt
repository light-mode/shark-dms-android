package vn.sharkdms.ui.customer.info

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.os.Bundle
import android.os.Looper
import android.provider.Settings
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.bumptech.glide.Glide
import com.google.android.gms.location.*
import kotlinx.android.synthetic.main.dialog_discount.view.*
import kotlinx.coroutines.flow.collect
import vn.sharkdms.R
import vn.sharkdms.activity.SharedViewModel
import vn.sharkdms.api.CheckInRequest
import vn.sharkdms.api.DiscountInfo
import vn.sharkdms.databinding.FragmentCustomerInfoBinding
import vn.sharkdms.ui.customer.discount.DiscountDialogFragment
import vn.sharkdms.ui.customer.discount.DiscountDialogViewModelSale
import vn.sharkdms.util.Constant
import vn.sharkdms.util.Formatter
import vn.sharkdms.util.HttpStatus
import vn.sharkdms.util.Utils

class CustomerInfoFragment : Fragment(R.layout.fragment_customer_info) {

    companion object {
        private const val TAG = "CustomerInfoFragment"
        private const val PERMISSION_ID = 42
    }

    private lateinit var authorization: String
    private var latitude: String = ""
    private var longitude: String = ""
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    private val args by navArgs<CustomerInfoFragmentArgs>()

    private lateinit var checkInViewModel: CheckInViewModel
    private lateinit var discountViewModelSale: DiscountDialogViewModelSale
    private lateinit var sharedViewModel : SharedViewModel

    private var infoMin: DiscountInfo? = null
    private var infoMinMax: DiscountInfo? = null
    private var infoMax: DiscountInfo? = null

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val binding = FragmentCustomerInfoBinding.bind(view)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())

        discountViewModelSale = ViewModelProvider(requireActivity())[DiscountDialogViewModelSale::class.java]
        checkInViewModel = ViewModelProvider(requireActivity())[CheckInViewModel::class.java]
        sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]
        authorization = Constant.TOKEN_PREFIX.plus(sharedViewModel.token)

        bind(binding)
        setBtnBackOnClickListener(binding)
        setBtnMapOnClickListener(binding)
        setBtnCheckInOnClickListener(binding)
        setBtnCameraOnClickListener(binding)
        setBtnDiscountOnClickListener(binding)
        setBtnOrderOnClickListener(binding)
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            checkInViewModel.checkInEvent.collect { event ->
                when(event) {
                    is CheckInViewModel.CheckInEvent.OnResponse ->
                        handleCheckInResponse(event.code, event.message)
                    is CheckInViewModel.CheckInEvent.OnFailure ->
                        Utils.showConnectivityOffMessage(requireContext())
                    is CheckInViewModel.CheckInEvent.ShowUnauthorizedDialog ->
                        Utils.showUnauthorizedDialog(requireActivity())
                }
            }
        }
        viewLifecycleOwner.lifecycleScope.launchWhenStarted {
            discountViewModelSale.discountDialogEvent.collect { event ->
                when(event) {
                    is DiscountDialogViewModelSale.DiscountDialogEvent.OnResponse -> {
                        if (event.data?.size != 0)
                            handleGetDiscountInfoResponse(event.code, event.message, event.data)
                        else
                            handleGetDiscountInfoResponse(event.code, event.message, null)
                    }
                    is DiscountDialogViewModelSale.DiscountDialogEvent.OnFailure ->
                        Utils.showConnectivityOffMessage(requireContext())
                    is DiscountDialogViewModelSale.DiscountDialogEvent.ShowUnauthorizedDialog ->
                        Utils.showUnauthorizedDialog(requireActivity())
                }
            }
        }
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        Utils.hideSoftKeyboard(requireActivity() as AppCompatActivity)
    }

    override fun onDetach() {
        super.onDetach()
        Utils.hideSoftKeyboard(requireActivity() as AppCompatActivity)
    }

    private fun setBtnMapOnClickListener(binding: FragmentCustomerInfoBinding) {
        binding.btnCustomerInfoMap.setOnClickListener {
            val action = CustomerInfoFragmentDirections.
                actionCustomerInfoFragmentToCustomerLocationMapFragment(args.customer)
            findNavController().navigate(action)
        }
    }

    private fun setBtnCheckInOnClickListener(binding: FragmentCustomerInfoBinding) {
        binding.btnCustomerInfoCheckIn.setOnClickListener {
            getLastLocation()
        }
    }

    private fun setBtnCameraOnClickListener(binding: FragmentCustomerInfoBinding) {
        binding.btnCustomerInfoCamera.setOnClickListener {
            val action = CustomerInfoFragmentDirections.
            actionCustomerInfoFragmentToCustomerGalleryFragment(args.customer)
            findNavController().navigate(action)
        }
    }

    private fun setBtnDiscountOnClickListener(binding: FragmentCustomerInfoBinding) {
        binding.btnCustomerInfoDiscount.setOnClickListener {
            val dialog = DiscountDialogFragment().newInstance(infoMin, infoMinMax, infoMax, 0)
            dialog.show(childFragmentManager, TAG)
        }
    }

    private fun setBtnOrderOnClickListener(binding: FragmentCustomerInfoBinding) {
        binding.btnCustomerInfoOrder.setOnClickListener {

            val action = CustomerInfoFragmentDirections.
            actionCustomerInfoFragmentToProductsFragment2(args.customer)
            findNavController().navigate(action)
        }
    }

    private fun setBtnBackOnClickListener(binding: FragmentCustomerInfoBinding) {
        binding.ivBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    private fun bind(binding: FragmentCustomerInfoBinding) {
        binding.apply {
            if (args.customer.customerAvatar.isNotEmpty()) {
                Glide.with(this@CustomerInfoFragment).load(args.customer.customerAvatar).circleCrop()
                    .into(ivCustomerInfoAvatar)
            }
            tvCustomerInfoName.text = args.customer.customerName
            tvCustomerInfoPhone.text = args.customer.customerPhone
            tvCustomerInfoRankDetail.text = args.customer.rankName
            tvCustomerInfoAddressDetail.text =
                Formatter.collapseDisplay(args.customer.customerAddress, Constant.ADDRESS_LIMIT)
            tvCustomerInfoEmailDetail.text = args.customer.customerEmail?.let { email ->
                Formatter.collapseDisplay(email, Constant.ADDRESS_LIMIT)
            }
        }
        discountViewModelSale.sendGetEachCustomerDiscountInfo(sharedViewModel.token, args.customer.customerId)
    }

    @SuppressLint("MissingPermission")
    private fun getLastLocation() {
        if (checkPermissions()) {
            if (isLocationEnabled()) {
                fusedLocationClient.lastLocation.addOnCompleteListener(requireActivity()) { task ->
                    val location: Location? = task.result
                    if (location == null) {
                        requestNewLocationData()
                    } else {
                        latitude = location.latitude.toString()
                        longitude = location.longitude.toString()
                        val request = CheckInRequest(args.customer.customerId, args.customer.customerAddress, latitude, longitude, "")
                        checkInViewModel.checkInRequest(authorization, request)
                    }
                }
            } else {
                Toast.makeText(requireActivity(), "H??y b???t v??? tr?? c???a b???n", Toast.LENGTH_LONG).show()
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
        fusedLocationClient.requestLocationUpdates(
            locationRequest, locationCallback, Looper.myLooper()
        )
    }

    private val locationCallback = object : LocationCallback() {
        override fun onLocationResult(locationResult: LocationResult) {
            val lastLocation: Location = locationResult.lastLocation
            latitude = lastLocation.latitude.toString()
            longitude = lastLocation.longitude.toString()
            val request = CheckInRequest(args.customer.customerId, args.customer.customerAddress, latitude, longitude, "")
            checkInViewModel.checkInRequest(authorization, request)
        }
    }

    private fun isLocationEnabled(): Boolean {
        val locationManager: LocationManager = context?.getSystemService(Context.LOCATION_SERVICE) as LocationManager
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
        if (requestCode == PERMISSION_ID && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            getLastLocation()
        }
    }

    private fun handleCheckInResponse(code: Int, message: String) {
        when (code) {
            HttpStatus.OK -> {
                Toast.makeText(requireContext(), message, Toast.LENGTH_LONG).show()
                findNavController().navigateUp()
            }
            HttpStatus.BAD_REQUEST, HttpStatus.FORBIDDEN -> Toast.makeText(
                requireContext(),
                message, Toast.LENGTH_SHORT
            ).show()
            else -> Log.e(TAG, code.toString())
        }
    }

    private fun handleGetDiscountInfoResponse(code: Int, message: String, data: List<DiscountInfo>?) {
        when (code) {
            HttpStatus.OK -> {
                if (data != null) {
                    for (info in data) {
                        when (info.ruleCode) {
                            "max" -> infoMax = info
                            "min_max" -> infoMinMax = info
                            "min" -> infoMin = info
                        }
                    }
                }
            }
            HttpStatus.BAD_REQUEST, HttpStatus.FORBIDDEN -> {
                Log.e(TAG, message)
            }
            else -> Log.e(TAG, code.toString())
        }
    }
}