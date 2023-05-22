package dev.mikita.bettercity.main.view

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import dagger.hilt.android.AndroidEntryPoint
import dev.mikita.bettercity.R
import dev.mikita.bettercity.databinding.FragmentAddNewIssueLocationBinding
import dev.mikita.bettercity.main.viewmodel.AddNewIssueViewModel

@AndroidEntryPoint
class AddNewIssueLocationFragment : Fragment(), OnMapReadyCallback {
    // View Binding
    private var _binding: FragmentAddNewIssueLocationBinding? = null
    private val binding get() = _binding!!

    // ViewModel
    private val viewModel: AddNewIssueViewModel by activityViewModels()

    // Utils
    private lateinit var mMap: GoogleMap
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>

    // State
    private var markerCoordinate: LatLng? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddNewIssueLocationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRequestPermissionLauncher()

        val mapFragment = childFragmentManager.findFragmentById(R.id.map_fragment) as SupportMapFragment
        mapFragment.getMapAsync(this)

        viewModel.isValidScreen.value = true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null

        markerCoordinate?.let { coordinate ->
            viewModel.updateCoordinate(coordinate)
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap

        if (ContextCompat.checkSelfPermission(requireContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            setUpMap(true)
        } else {
            requestPermissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
            return
        }
    }

    private fun setUpMap(isGranted: Boolean) {
        try {
            // Enable MyLocation Layer of Google Map
            if (isGranted) {
                mMap.isMyLocationEnabled = true
            }

            // Get last known location
            if (viewModel.getCoordinates() != null) {
                markerCoordinate = viewModel.getCoordinates()
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(markerCoordinate!!, 17f))
                setUpMarker()
            } else {
                if (isGranted) {
                    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireActivity())
                    fusedLocationClient.lastLocation
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                val location = task.result
                                if (location != null && markerCoordinate == null) {
                                    markerCoordinate = LatLng(location.latitude, location.longitude)
                                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(markerCoordinate!!, 17f))
                                    setUpMarker()
                                } else {
                                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(50.0755, 14.4378), 17f))
                                    setUpMarker()
                                }
                            } else {
                                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(50.0755, 14.4378), 17f))
                                setUpMarker()
                            }
                        }
                } else {
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(50.0755, 14.4378), 17f))
                    setUpMarker()
                }
            }

        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

    private fun setUpMarker() {
        if (markerCoordinate == null) {
            markerCoordinate = mMap.cameraPosition.target
        }

        mMap.setOnCameraMoveListener {
            markerCoordinate = mMap.cameraPosition.target
        }
    }

    private fun setupRequestPermissionLauncher() {
        requestPermissionLauncher =
            registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
                setUpMap(isGranted)
            }
    }
}