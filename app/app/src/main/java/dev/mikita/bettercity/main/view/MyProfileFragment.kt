package dev.mikita.bettercity.main.view

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import coil.api.load
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import dev.mikita.bettercity.databinding.FragmentMyProfileBinding
import dev.mikita.bettercity.main.viewmodel.MyProfileViewModel
import javax.inject.Inject

@AndroidEntryPoint
class MyProfileFragment : Fragment() {
    // View Binding
    private var _binding: FragmentMyProfileBinding? = null
    private val binding get() = _binding!!

    // ViewModel
    private val viewModel: MyProfileViewModel by viewModels()

    // Auth
    @Inject
    lateinit var mAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViews()
        setUpEventListeners()
        setUpObservers()

        // Load data
        if (savedInstanceState == null || viewModel.getResidentLiveData().value == null) {
            viewModel.loadData()
        } else {
            binding.progressBar.hide()
            binding.myProfileScrollView.visibility = View.VISIBLE
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setUpViews() {
        binding.progressBar.setVisibilityAfterHide(View.INVISIBLE)
        binding.myProfileScrollView.visibility = View.INVISIBLE
    }

    private fun setUpEventListeners() {
        binding.logoutButton.setOnClickListener {
            mAuth.signOut()
            startActivity(Intent(requireContext(), AuthActivity::class.java))
            requireActivity().finish()
        }
    }

    private fun setUpObservers() {
        // Attach Event Listeners
        viewModel.getResidentLiveData().observe(viewLifecycleOwner) { resident ->
            resident?.let {
                binding.myProfileImage.load(resident.photo)
                binding.myProfileName.text = String.format("%s %s", resident.firstName, resident.lastName)
                binding.myProfileEmail.text = resident.email
                binding.myProfilePublishedIssues.text = resident.issuesCount.toString()
            }

            binding.progressBar.hide()
            binding.myProfileScrollView.visibility = View.VISIBLE
        }

        viewModel.getErrorLiveData().observe(viewLifecycleOwner) { error ->
            error?.let {
                Toast.makeText(requireContext(), error.message, Toast.LENGTH_SHORT).show()
                binding.progressBar.hide()
            }
        }
    }
}