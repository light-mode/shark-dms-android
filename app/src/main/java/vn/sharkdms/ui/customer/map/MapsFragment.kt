package vn.sharkdms.ui.customer.map

import android.Manifest
import android.content.pm.PackageManager
import androidx.fragment.app.Fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityCompat
import com.google.android.gms.common.GooglePlayServicesNotAvailableException
import com.google.android.gms.maps.*

import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import vn.sharkdms.R

class MapsFragment : Fragment(), OnMapReadyCallback {

    private val TAG = "MapsFragment"

    private lateinit var googleMap: GoogleMap

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        var rootView =  inflater.inflate(R.layout.fragment_maps, container, false)

        val mapFragment = childFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        return rootView
    }

    override fun onMapReady(p0: GoogleMap) {
        googleMap = p0
        val hanoi = LatLng(21.028511, 105.804817)
        googleMap.addMarker(MarkerOptions().position(hanoi).title("Marker in Hanoi"))
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(hanoi, 15f))
    }
}