package dev.mikita.bettercity.main.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.Navigation
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import dagger.hilt.android.AndroidEntryPoint
import dev.mikita.bettercity.R
import dev.mikita.bettercity.databinding.FragmentIssuesMapBinding
import dev.mikita.bettercity.main.viewmodel.IssuesViewModel


@AndroidEntryPoint
class IssuesMapFragment : Fragment(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener {
    // View Binding
    private var _binding: FragmentIssuesMapBinding? = null
    private val binding get() = _binding!!

    // Shared View Model
    private val issuesViewModel: IssuesViewModel by activityViewModels()

    // Utils
    private lateinit var mMap: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient

    // State
    private var savedCameraPosition: CameraPosition? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentIssuesMapBinding.inflate(inflater, container, false)

        if (savedInstanceState != null) {
            savedCameraPosition = savedInstanceState.getParcelable("camera_position");
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpView()
    }

    private fun setUpView() {
        val mapFragment = childFragmentManager.findFragmentById(R.id.map_fragment) as SupportMapFragment
        mapFragment.getMapAsync(this)
    }

    private fun setUpObservers() {
        issuesViewModel.getIssues().observe(viewLifecycleOwner) { issuesList ->
            issuesList?.let {
                mMap.clear()
                for (issue in issuesList) {
                    val markerOptions = MarkerOptions()
                    markerOptions.position(issue.coordinates)
                    markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
                    mMap.addMarker(markerOptions)?.tag = issue.id
                }
            }
        }

        mMap.setOnMarkerClickListener(this)
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(LatLng(50.0875, 14.4214), 12f))
        setUpObservers()
    }

    override fun onMarkerClick(marker: Marker): Boolean {
        val issueId = marker.tag as Long

        val mainNavController = Navigation.findNavController(requireActivity(), R.id.nav_host_fragment)
        val action = HomeFragmentDirections.actionHomeFragmentToIssueFragment(issueId)

        mainNavController.navigate(action)

        return true
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}