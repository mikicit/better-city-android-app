package dev.mikita.bettercity.main.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import coil.api.load
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import dagger.hilt.android.AndroidEntryPoint
import dev.mikita.bettercity.databinding.FragmentReservationBinding
import dev.mikita.bettercity.main.viewmodel.ReservationViewModel
import java.text.SimpleDateFormat
import java.util.Locale

@AndroidEntryPoint
class ReservationFragment : BottomSheetDialogFragment() {
    // View Binding
    private var _binding: FragmentReservationBinding? = null
    private val binding get() = _binding!!

    // View Model
    private val viewModel: ReservationViewModel by viewModels()

    // Utils
    private val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentReservationBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViews()
        setUpObservers()

        val id = arguments?.getLong(ARGUMENT_KEY)
        if (viewModel.getReservationLiveData().value == null) {
            if (id != null) {
                viewModel.loadData(id)
            }
        } else {
            binding.progressBar.hide()
            binding.bottomSheetContent.visibility = View.VISIBLE
        }
    }

    private fun setUpViews() {
        binding.progressBar.setVisibilityAfterHide(View.INVISIBLE)
        binding.bottomSheetContent.visibility = View.INVISIBLE
    }

    private fun setUpObservers() {
        // Set Event Listeners
        binding.serviceProfileLayout.setOnClickListener {
            viewModel.getReservationLiveData().value?.let { reservation ->
                val action = IssueFragmentDirections.actionIssueFragmentToServiceFragment(reservation.service.uid)
                findNavController().navigate(action)
            }
        }

        // Set Reservation Data Observer
        viewModel.getReservationLiveData().observe(viewLifecycleOwner) { reservation ->
            reservation?.let {
                binding.serviceName.text = reservation.service.name
                binding.serviceImage.load(reservation.service.photo)
                binding.reservationDate.text = dateFormat.format(reservation.creationDate)
            }

            binding.progressBar.hide()
            binding.bottomSheetContent.visibility = View.VISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        const val TAG = "ReservationBottomSheetFragment"
        private const val ARGUMENT_KEY = "issue_id"

        fun newInstance(id: Long): ReservationFragment {
            val fragment = ReservationFragment()
            val args = Bundle()
            args.putLong(ARGUMENT_KEY, id)
            fragment.arguments = args
            return fragment
        }
    }
}