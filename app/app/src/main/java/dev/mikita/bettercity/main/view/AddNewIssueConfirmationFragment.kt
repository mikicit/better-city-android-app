package dev.mikita.bettercity.main.view

import android.location.Geocoder
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import coil.api.load
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.MarkerOptions
import dagger.hilt.android.AndroidEntryPoint
import dev.mikita.bettercity.R
import dev.mikita.bettercity.databinding.FragmentAddNewIssueConfirmationBinding
import dev.mikita.bettercity.main.viewmodel.AddNewIssueViewModel
import java.util.Locale

@AndroidEntryPoint
class AddNewIssueConfirmationFragment : Fragment(), OnMapReadyCallback {
    // View Binding
    private var _binding: FragmentAddNewIssueConfirmationBinding? = null
    private val binding get() = _binding!!

    // ViewModel
    private val viewModel: AddNewIssueViewModel by activityViewModels()

    // Utils
    private lateinit var mMap: GoogleMap

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddNewIssueConfirmationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViews()
    }

    private fun setUpViews() {
        val mapFragment = childFragmentManager.findFragmentById(R.id.issue_map) as SupportMapFragment
        mapFragment.getMapAsync(this)

        binding.issueName.text = viewModel.getTitle()
        binding.issueDescription.text = viewModel.getDescription()
        binding.issuePhoto.load(viewModel.getPhotoLiveData().value)
        binding.issueCaterogy.text = viewModel.getCategoriesLiveData().value?.find {
            it.id == viewModel.getCategoryLiveData().value
        }?.name

        // Convert coordinate to string
        val geocoder = Geocoder(requireContext(), Locale.getDefault())
        val addresses = viewModel.getCoordinates()
            ?.let { geocoder.getFromLocation(it.latitude, it.longitude, 1) }

        if (addresses != null) {
            if (addresses.isNotEmpty()) {
                val address = addresses[0]
                val sb = StringBuilder()
                for (i in 0..address.maxAddressLineIndex) {
                    sb.append(address.getAddressLine(i))
                    if (i < address.maxAddressLineIndex) {
                        sb.append(", ")
                    }
                }
                binding.issueLocation.text = sb.toString()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onMapReady(googleMap: GoogleMap) {
        mMap = googleMap
        mMap.uiSettings.setAllGesturesEnabled(false)
        mMap.setOnMapClickListener(null)
        mMap.setOnMapLongClickListener(null)
        mMap.setOnMarkerClickListener(null)
        mMap.setOnMarkerDragListener(null)
        mMap.setOnInfoWindowClickListener(null)
        mMap.setOnInfoWindowLongClickListener(null)
        mMap.setOnInfoWindowCloseListener(null)

        // Add a marker in Sydney and move the camera
        viewModel.getCoordinates()?.let {
            val markerOptions = MarkerOptions()
            markerOptions.position(viewModel.getCoordinates()!!)
            markerOptions.icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED))
            mMap.addMarker(markerOptions)
            mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(it, 17f))
        }
    }
}