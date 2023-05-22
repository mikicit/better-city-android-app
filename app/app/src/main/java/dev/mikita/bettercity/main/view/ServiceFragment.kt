package dev.mikita.bettercity.main.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import coil.api.load
import dagger.hilt.android.AndroidEntryPoint
import dev.mikita.bettercity.databinding.FragmentServiceBinding
import dev.mikita.bettercity.main.viewmodel.ServiceViewModel

@AndroidEntryPoint
class ServiceFragment : Fragment() {
    // View Binding
    private var _binding: FragmentServiceBinding? = null
    private val binding get() = _binding!!

    // Args
    private val args: ServiceFragmentArgs by navArgs()

    // ViewModel
    private val viewModel: ServiceViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentServiceBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViews()
        setUpObservers()

        // Load data
        if (savedInstanceState == null || viewModel.getServiceLiveData().value == null) {
            viewModel.loadData(args.uid)
        } else {
            binding.progressBar.hide()
            binding.serviceScrollView.visibility = View.VISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setUpViews() {
        binding.progressBar.setVisibilityAfterHide(View.GONE)
        binding.serviceScrollView.visibility = View.INVISIBLE
    }

    private fun setUpObservers() {
        // Attach Event Listeners
        viewModel.getServiceLiveData().observe(viewLifecycleOwner) { service ->
            service?.let {
                binding.serviceImage.load(service.photo)
                binding.serviceName.text = service.name
                binding.serviceDescription.text = service.description
                binding.solvedIssues.text = service.solutionsCount.toString()
                binding.reservedIssues.text = service.reservationsCount.toString()
            }

            binding.progressBar.hide()
            binding.serviceScrollView.visibility = View.VISIBLE
        }
    }
}