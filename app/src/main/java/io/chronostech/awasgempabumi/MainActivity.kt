package io.chronostech.awasgempabumi

import android.Manifest
import android.annotation.SuppressLint
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
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
import io.chronostech.awasgempabumi.databinding.ActivityMainBinding
import io.chronostech.awasgempabumi.model.Gempa
import javax.inject.Inject

const val TAG = "Awas Gempa"
const val LOCATION_PERMISSION_REQUEST = 62

@AndroidEntryPoint
class MainActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {

    private lateinit var binding: ActivityMainBinding
    private lateinit var layoutManager: LinearLayoutManager
    private lateinit var adapter: EarthQuakeAdapter

    private val viewModel: EarthQuakeViewModel by viewModels()

    @Inject
    lateinit var mPref: SharedPreferences

    private var mMap: GoogleMap? = null
    private var lastKnownLocation: Location? = null
    private lateinit var fusedLocationProviderClient: FusedLocationProviderClient

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        if (checkLocationPermission()) setupMapView(savedInstanceState)
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

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.main_menu, menu)
        return true
    }

    private fun setupAds() {
        MobileAds.initialize(this) {
            val adRequest = AdRequest.Builder().build()
            binding.adView.loadAd(adRequest)
        }
    }

    private fun setupRecyclerView() {
        adapter = EarthQuakeAdapter(applicationContext)
        binding.recyclerView.adapter = adapter
        layoutManager = LinearLayoutManager(this.applicationContext)
        binding.recyclerView.layoutManager = layoutManager
        binding.recyclerView.setHasFixedSize(true)
        val dividerItemDecoration =
            DividerItemDecoration(this.applicationContext, DividerItemDecoration.VERTICAL)
        binding.recyclerView.addItemDecoration(dividerItemDecoration)
    }

    private fun setupMapView(savedInstanceState: Bundle?) {
        binding.mapView.onCreate(savedInstanceState)
        binding.mapView.onResume()
        try {
            MapsInitializer.initialize(applicationContext)
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
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this)

        mMap = googleMap
        mMap?.isMyLocationEnabled = true
        mMap?.setOnCameraMoveListener {
            viewModel.earthquakeList.value?.let { mapLatLong(it) }
        }
        showDeviceLocation()
    }

    private fun showDeviceLocation() {
        try {
            if (checkLocationPermission()) {
                val locationResult = fusedLocationProviderClient.lastLocation
                locationResult.addOnCompleteListener(this) { task ->
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

    private fun checkLocationPermission(): Boolean {
        if (ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                this, Manifest.permission.ACCESS_COARSE_LOCATION
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
                        this.applicationContext,
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

    override fun onMarkerClick(marker: Marker): Boolean {
        TODO("Not yet implemented")
    }
}