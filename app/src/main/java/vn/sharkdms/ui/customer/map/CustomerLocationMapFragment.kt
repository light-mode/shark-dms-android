package vn.sharkdms.ui.customer.map

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import vn.sharkdms.R

class CustomerLocationMapFragment : Fragment(), OnMapReadyCallback {

    private val TAG = "CustomerLocationMapFragment"

    private lateinit var googleMap: GoogleMap
    private val args by navArgs<CustomerLocationMapFragmentArgs>()

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
        googleMap = p0
        val locationArray = args.customer.customerPosition.split("_").toTypedArray()
        val location = LatLng(locationArray[0].toDouble(), locationArray[1].toDouble())
        googleMap.addMarker(MarkerOptions().position(location).title(args.customer.customerName)
            .snippet("Địa chỉ: " + args.customer.customerAddress))
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(location, 15f))
    }

    private fun setBtnBackOnClickListener(rootView: View?) {
        val btnBack = rootView?.findViewById<ImageView>(R.id.iv_back)
        btnBack?.setOnClickListener {
            findNavController().navigateUp()
        }
    }
}