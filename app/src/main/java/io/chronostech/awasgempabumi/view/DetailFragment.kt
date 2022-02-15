package io.chronostech.awasgempabumi.view

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Canvas
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
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
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.text.SimpleDateFormat
import java.util.*

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
        viewModel.gempa.value?.coordinates?.let { viewModel.getPotensiTsunami(it) }

        viewModel.gempa.observe(viewLifecycleOwner, {
            val coordinate = it.coordinates!!.split(",")
            val latitude: Double = coordinate.get(0).toDouble()
            val longitude: Double = coordinate.get(1).toDouble()
            val latLng = LatLng(latitude, longitude)

            val detailPlace = it.dirasakan?.replace("I", "")?.replace("V", "")?.removePrefix("-")
                ?.removePrefix(" ")?.split(", ")
            province.forEach { bound ->
                if (bound.value.contains(latLng)) {
                    binding.tvDetailPlace.text = "${detailPlace?.get(0)}, ${bound.key}"
                } else {
                    binding.tvDetailPlace.text = "${detailPlace?.get(0)}"
                }
            }

            binding.tvMagnitude.text = String.format("%.2f", it.magnitude?.toDouble())
            binding.tvTimeGempa.text = formatTime(it.dateTime!!)
            binding.tvLatlon.text = "${it.lintang}, ${it.bujur}"
            binding.tvDepth.text = it.kedalaman
            binding.clPotensi.visibility = View.INVISIBLE
        })

        viewModel.potensiTsunami.observe(viewLifecycleOwner, {
            if (it) {
                binding.clPotensi.visibility = View.VISIBLE
            }
        })

        if (allPermissionsGranted()) {
            setupMapView(savedInstanceState)
        } else {
            permReqLauncher.launch(REQUIRED_PERMISSIONS)
        }
        setupAds()

        binding.btnShare.setOnClickListener {
            shareAction()
        }
        binding.btnShare.visibility = View.GONE

        binding.btnBack.setOnClickListener {
            NavHostFragment.findNavController(this).navigateUp()
        }
    }

    private fun formatTime(dateTime: String): String {
        val serverFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssZ", Locale.US)
        serverFormat.timeZone = TimeZone.getTimeZone("UTC")
        val date = serverFormat.parse(dateTime)
        val displayFormat = SimpleDateFormat("dd MMM yyyy HH:mm", Locale.getDefault())
        displayFormat.timeZone = TimeZone.getDefault()
        return displayFormat.format(date)
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

    private fun shareAction() {
        val view = binding.clFrame
        val bitmap = Bitmap.createBitmap(
            view.measuredWidth,
            view.measuredHeight,
            Bitmap.Config.ARGB_8888
        )

        val canvas = Canvas(bitmap)
        view.layout(view.left, view.top, view.right, view.bottom)
        view.draw(canvas)

        val imageUri = saveImage(bitmap)

        val shareIntent: Intent = Intent().apply {
            action = Intent.ACTION_SEND
            putExtra(Intent.EXTRA_STREAM, imageUri)
            type = "image/*"
        }
        startActivity(Intent.createChooser(shareIntent, "Bagikan laman ini"))
    }

    private fun saveImage(bitmap: Bitmap): Uri? {
        var fos: OutputStream? = null
        var imageUri: Uri? = null

        val timestamp = SimpleDateFormat("yyyyMMdd_hhMMss").format(Date())

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val resolver = requireContext().contentResolver
            val contentValues = ContentValues()
            contentValues.put(MediaStore.MediaColumns.DISPLAY_NAME, timestamp)
            contentValues.put(MediaStore.MediaColumns.MIME_TYPE, "image/PNG")
            contentValues.put(MediaStore.MediaColumns.RELATIVE_PATH, "DCIM/" + "AwasGempaBumi")
            imageUri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
            fos = resolver.openOutputStream(imageUri!!)
        } else {
            val imagesDir = Environment.getExternalStoragePublicDirectory(
                Environment.DIRECTORY_DCIM
            ).toString() + File.separator + "AwasGempaBumi"

            val file = File(imagesDir)
            if (!file.exists()) file.mkdir()
            val image = File(imagesDir, timestamp + ".png")
            imageUri = Uri.fromFile(image)
            fos = FileOutputStream(image)
        }

        bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos)
        fos?.flush()
        fos?.close()
        return imageUri
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        var latitude: Double? = null
        var longitude: Double? = null

        private val REQUIRED_PERMISSIONS = arrayOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    }
}