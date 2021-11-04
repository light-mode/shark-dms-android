package vn.sharkdms.ui.customer.map

import android.Manifest
import android.content.pm.PackageManager
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.maps.*

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import vn.sharkdms.R
import vn.sharkdms.ui.customer.info.CustomerInfoFragmentArgs

class MapsFragment : Fragment(), OnMapReadyCallback {

    private val TAG = "MapsFragment"

    private lateinit var googleMap: GoogleMap
    private val args by navArgs<MapsFragmentArgs>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var rootView =  inflater.inflate(R.layout.fragment_maps, container, false)

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        setBtnBackOnClickListener(rootView)

        return rootView
    }

    override fun onMapReady(p0: GoogleMap) {
        Toast.makeText(requireContext(), args.customers.toString(), Toast.LENGTH_LONG).show()
        googleMap = p0
        googleMap.setPadding(0, 120, 0, 0)
        val hanoi = LatLng(21.028511, 105.804817)
        for (marker in args.customers) {
            val gpsArray = marker.customerPosition.split("_").toTypedArray()
            val gps = LatLng(gpsArray[0].toDouble(), gpsArray[1].toDouble())
            googleMap.addMarker(MarkerOptions().position(gps).title(marker.customerName)
                .snippet("Địa chỉ: " + marker.customerAddress))
        }
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(hanoi, 10f))
    }

    private fun setBtnBackOnClickListener(rootView: View?) {
        val btnBack = rootView?.findViewById<ImageView>(R.id.iv_back)
        btnBack?.setOnClickListener {
            findNavController().navigateUp()
        }
    }
}