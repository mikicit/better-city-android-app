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
import dev.mikita.bettercity.databinding.FragmentUserProfileBinding
import dev.mikita.bettercity.main.viewmodel.UserProfileViewModel

@AndroidEntryPoint
class UserProfileFragment : Fragment() {
    // View Binding
    private var _binding: FragmentUserProfileBinding? = null
    private val binding get() = _binding!!

    // Args
    private val args: UserProfileFragmentArgs by navArgs()

    // ViewModel
    private val viewModel: UserProfileViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViews()
        setUpObservers()

        // Load data
        if (savedInstanceState == null || viewModel.getResidentLiveData().value == null) {
            viewModel.loadData(args.uid)
        } else {
            binding.progressBar.hide()
            binding.userProfileScrollView.visibility = View.VISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setUpViews() {
        binding.progressBar.setVisibilityAfterHide(View.GONE)
        binding.userProfileScrollView.visibility = View.INVISIBLE
    }

    private fun setUpObservers() {
        // Attach Event Listeners
        viewModel.getResidentLiveData().observe(viewLifecycleOwner) { resident ->
            resident?.let {
                binding.profileImage.load(resident.photo)
                binding.profileName.text = String.format("%s %s", resident.firstName, resident.lastName)
                binding.profilePublishedIssues.text = resident.issuesCount.toString()
            }

            binding.progressBar.hide()
            binding.userProfileScrollView.visibility = View.VISIBLE
        }
    }
}