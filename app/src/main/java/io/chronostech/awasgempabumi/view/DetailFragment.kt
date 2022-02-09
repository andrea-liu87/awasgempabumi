package io.chronostech.awasgempabumi.view

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.NavHostFragment
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import dagger.hilt.android.AndroidEntryPoint
import io.chronostech.awasgempabumi.Util.Companion.province
import io.chronostech.awasgempabumi.databinding.FragmentDetailBinding
import io.chronostech.awasgempabumi.viewmodel.DetailViewModel

@AndroidEntryPoint
class DetailFragment : Fragment(), OnMapReadyCallback {

    private var _binding: FragmentDetailBinding? = null
    private val binding get() = _binding!!

    private val viewModel by activityViewModels<DetailViewModel>()

    private var mMap: GoogleMap? = null
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentDetailBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val coordinate = viewModel.gempa.value?.coordinates!!.split(",")
        val latitude: Double = coordinate.get(0).toDouble()
        val longitude: Double = coordinate.get(1).toDouble()
        val latLng = LatLng(latitude, longitude)

        province.forEach {
            if (it.value.contains(latLng)) {
                binding.tvDetailPlace.text = it.key
            }
        }

        if (checkLocationPermission()) setupMapView(savedInstanceState)
        setupAds()

        binding.btnBack.setOnClickListener {
            NavHostFragment.findNavController(this).navigateUp()
        }
    }

    private fun setupAds() {
        MobileAds.initialize(requireContext()) {
            val adRequest = AdRequest.Builder().build()
            binding.adView.loadAd(adRequest)
        }
    }

    private fun setupMapView(savedInstanceState: Bundle?) {
        binding.mapView.onCreate(savedInstanceState)
        binding.mapView.onResume()
        try {
            MapsInitializer.initialize(requireContext())
        } catch (e: Exception) {
            e.printStackTrace()
        }
        binding.mapView.getMapAsync(this)
    }

    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireActivity())

        mMap = googleMap
        mMap?.isMyLocationEnabled = true
        if (viewModel.gempa.value?.coordinates != null) {
            val coordinate = viewModel.gempa.value?.coordinates!!.split(",")
            latitude = coordinate.get(0).toDouble()
            longitude = coordinate.get(1).toDouble()

            mMap!!.moveCamera(
                CameraUpdateFactory.newLatLngZoom(
                    LatLng(latitude!!, longitude!!),
                    6f
                )
            )
            mMap!!.addMarker(MarkerOptions().position(LatLng(latitude!!, longitude!!)))
        }
    }

    private fun checkLocationPermission(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                requireContext(), Manifest.permission.ACCESS_COARSE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermissions(
                arrayOf(
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ), LOCATION_PERMISSION_REQUEST
            )
            return false
        }
        return true
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        when (requestCode) {
            LOCATION_PERMISSION_REQUEST -> {
                if ((grantResults.isNotEmpty() &&
                            grantResults[0] == PackageManager.PERMISSION_GRANTED)
                ) {
                    setupMapView(null)
                } else {
                    Toast.makeText(
                        requireContext(),
                        "Ijin lokasi aplikasi ditolak",
                        Toast.LENGTH_LONG
                    ).show()
                }
                return
            }
            else -> {
                super.onRequestPermissionsResult(requestCode, permissions, grantResults)
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        var latitude: Double? = null
        var longitude: Double? = null
    }
}