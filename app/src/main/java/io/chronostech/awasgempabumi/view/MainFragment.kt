package io.chronostech.awasgempabumi.view

import android.Manifest
import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.NavHostFragment
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapsInitializer
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import dagger.hilt.android.AndroidEntryPoint
import io.chronostech.awasgempabumi.EarthQuakeAdapter
import io.chronostech.awasgempabumi.R
import io.chronostech.awasgempabumi.databinding.FragmentMainBinding
import io.chronostech.awasgempabumi.model.Gempa
import io.chronostech.awasgempabumi.viewmodel.DetailViewModel
import io.chronostech.awasgempabumi.viewmodel.EarthQuakeViewModel
import javax.inject.Inject

const val LOCATION_PERMISSION_REQUEST = 62

@AndroidEntryPoint
class MainFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private var _binding: FragmentMainBinding? = null
    private val binding get() = _binding!!

    private val viewModel by activityViewModels<EarthQuakeViewModel>()
    private val detailViewModel by activityViewModels<DetailViewModel>()
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var adapter: EarthQuakeAdapter

    @Inject
    lateinit var mPref: SharedPreferences

    private var mMap: GoogleMap? = null
    private var lastKnownLocation: Location? = null
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        _binding = FragmentMainBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (allPermissionsGranted()) {
            setupMapView(savedInstanceState)
        } else {
            permReqLauncher.launch(REQUIRED_PERMISSIONS)
        }
        setupRecyclerView()

        viewModel.earthquakeList.observe(this, {
            if (it != null) adapter.setData(it)
            mapLatLong(it)
        })

        viewModel.loading.observe(this, {
            if (!it) {
                binding.swipeRefreshLayout.isRefreshing = false
            }
        })

        viewModel.getAllEarthquake()

        binding.swipeRefreshLayout.setOnRefreshListener {
            if (binding.swipeRefreshLayout.isRefreshing) {
                viewModel.getAllEarthquake()
            }
        }

        setupAds()
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.main_menu, menu)
    }

    private fun setupAds() {
        MobileAds.initialize(requireContext()) {
            val adRequest = AdRequest.Builder().build()
            binding.adView.loadAd(adRequest)
        }
    }

    private fun setupRecyclerView() {
        adapter = EarthQuakeAdapter(requireContext(),
            object : EarthQuakeAdapter.ItemListener {
                override fun itemClickListener(gempa: Gempa) {
                    detailViewModel.gempa.value = gempa
                    NavHostFragment.findNavController(this@MainFragment)
                        .navigate(R.id.action_mainFragment_to_detailFragment)
                }
            })
        binding.recyclerView.adapter = adapter
        layoutManager = LinearLayoutManager(requireContext())
        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.setHasFixedSize(true)
        val dividerItemDecoration =
            DividerItemDecoration(requireContext(), DividerItemDecoration.VERTICAL)
        binding.recyclerView.addItemDecoration(dividerItemDecoration)
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

    /**
     * Initial map view
     */
    @SuppressLint("MissingPermission")
    override fun onMapReady(googleMap: GoogleMap) {
        fusedLocationProviderClient =
            LocationServices.getFusedLocationProviderClient(requireActivity())

        mMap = googleMap
        mMap?.isMyLocationEnabled = true
        mMap?.setOnCameraMoveListener {
            viewModel.earthquakeList.value?.let { mapLatLong(it) }
        }
        showDeviceLocation()
    }

    private fun showDeviceLocation() {
        try {
            if (allPermissionsGranted()) {
                val locationResult = fusedLocationProviderClient.lastLocation
                locationResult.addOnCompleteListener(requireActivity()) { task ->
                    if (task.isSuccessful) {
                        lastKnownLocation = task.result
                        if (lastKnownLocation != null) {
                            mMap?.moveCamera(
                                CameraUpdateFactory.newLatLngZoom(
                                    LatLng(
                                        lastKnownLocation!!.latitude,
                                        lastKnownLocation!!.longitude
                                    ), 8f
                                )
                            )
                        }
                    } else {
                        Log.e(TAG, "Exception: %s", task.exception)
                    }
                }
            }
        } catch (e: SecurityException) {
            Log.e("Exception: %s", e.message, e)
        }
    }

    private fun mapLatLong(listGempa: List<Gempa>) {
        val boundaryMap = mMap?.projection?.visibleRegion?.latLngBounds ?: return

        listGempa.forEach {
            val coordinate = it.coordinates!!.split(",")
            val latitude: Double = coordinate.get(0).toDouble()
            val longitude: Double = coordinate.get(1).toDouble()
            val latLng = LatLng(latitude, longitude)

            if (boundaryMap.contains(latLng)) {
                mMap!!.addMarker(
                    MarkerOptions().position(latLng)
                        .title(it.magnitude)
                )
            }
        }
    }

    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(
            requireContext(), it
        ) == PackageManager.PERMISSION_GRANTED
    }

    private val permReqLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            val granted = permissions.entries.all {
                it.value
            }
            if (granted) {
                setupMapView(null)
            } else {
                Toast.makeText(
                    requireContext(),
                    "Permissions not granted by the user.",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    override fun onMarkerClick(marker: Marker): Boolean {
        TODO("Not yet implemented")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        private val REQUIRED_PERMISSIONS = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    }
}