package dev.mikita.bettercity.main.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import dev.mikita.bettercity.R
import dev.mikita.bettercity.databinding.FragmentAddNewIssueBinding
import dev.mikita.bettercity.main.viewmodel.AddNewIssueViewModel

@AndroidEntryPoint
class AddNewIssueFragment : Fragment() {
    // View Binding
    private var _binding: FragmentAddNewIssueBinding? = null
    private val binding get() = _binding!!

    // ViewModel
    private val viewModel: AddNewIssueViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddNewIssueBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpListeners()
        setUpObservers()
    }

    private fun setUpListeners() {
        val navHostFragment = childFragmentManager.findFragmentById(R.id.nav_add_new_issue_fragment) as NavHostFragment
        val navController = navHostFragment.findNavController()

        binding.addNewIssueButtonBack.setOnClickListener {
            requireActivity().onBackPressed()
        }

        binding.cancelButton.setOnClickListener {
            requireActivity().onBackPressed()
        }

        binding.submitButton.setOnClickListener {
            viewModel.sendIssue()
        }

        navController.addOnDestinationChangedListener { _: NavController, navDestination: NavDestination, _: Bundle? ->
            when (navDestination.id) {
                R.id.addNewIssueLocationFragment -> viewModel.setCurrentStep(AddNewIssueViewModel.Step.LOCATION)
                R.id.addNewIssuePhotoFragment -> viewModel.setCurrentStep(AddNewIssueViewModel.Step.PHOTO)
                R.id.addNewIssueCategoryFragment -> viewModel.setCurrentStep(AddNewIssueViewModel.Step.CATEGORY)
                R.id.addNewIssueDescriptionFragment -> viewModel.setCurrentStep(AddNewIssueViewModel.Step.DESCRIPTION)
                R.id.addNewIssueConfirmationFragment -> viewModel.setCurrentStep(AddNewIssueViewModel.Step.CONFIRMATION)
            }
        }

        binding.addNewIssueButtonNext.setOnClickListener {
            when (navController.currentDestination?.id) {
                R.id.addNewIssueLocationFragment -> {
                    navController.navigate(R.id.action_addNewIssueLocationFragment_to_addNewIssuePhotoFragment)
                }
                R.id.addNewIssuePhotoFragment -> {
                    navController.navigate(R.id.action_addNewIssuePhotoFragment_to_addNewIssueCategoryFragment)
                }
                R.id.addNewIssueCategoryFragment -> {
                    navController.navigate(R.id.action_addNewIssueCategoryFragment_to_addNewIssueDescriptionFragment)
                }
                R.id.addNewIssueDescriptionFragment -> {
                    navController.navigate(R.id.action_addNewIssueDescriptionFragment_to_addNewIssueConfirmationFragment)
                }
            }
        }
    }

    private fun setUpObservers() {
        viewModel.isValidScreen.observe(viewLifecycleOwner) { isValid ->
            binding.addNewIssueButtonNext.isEnabled = isValid
        }

        viewModel.getCurrentStep().observe(viewLifecycleOwner) { step ->
            val stepCount = AddNewIssueViewModel.Step.values().size.toString()
            val stepText = when (step) {
                AddNewIssueViewModel.Step.LOCATION -> getString(R.string.add_new_issue_step_text, "1", stepCount)
                AddNewIssueViewModel.Step.PHOTO -> getString(R.string.add_new_issue_step_text, "2", stepCount)
                AddNewIssueViewModel.Step.CATEGORY -> getString(R.string.add_new_issue_step_text, "3", stepCount)
                AddNewIssueViewModel.Step.DESCRIPTION -> getString(R.string.add_new_issue_step_text, "4", stepCount)
                AddNewIssueViewModel.Step.CONFIRMATION -> getString(R.string.add_new_issue_step_text, "5", stepCount)
            }

            binding.addNewIssueStepText.text = stepText

            if (step == AddNewIssueViewModel.Step.CONFIRMATION) {
                binding.bottomNavContainer.visibility = View.GONE
                binding.bottomConfirmContainer.visibility = View.VISIBLE
            } else {
                binding.bottomNavContainer.visibility = View.VISIBLE
                binding.bottomConfirmContainer.visibility = View.GONE
            }
        }

        viewModel.getLoadingLiveData().observe(viewLifecycleOwner) { isLoading ->
            if (isLoading) {
                binding.progressFrameLayout.visibility = View.VISIBLE
            } else {
                binding.progressFrameLayout.visibility = View.GONE
            }
        }

        viewModel.getErrorLiveData().observe(viewLifecycleOwner) {
            if (it != null) {
                Snackbar.make(binding.root, it.message, Snackbar.LENGTH_LONG).show()
                viewModel.clearError()
            }
        }

        viewModel.getSuccessLiveData().observe(viewLifecycleOwner) {
            Snackbar.make(binding.root, getString(R.string.add_new_issue_success), Snackbar.LENGTH_LONG).show()

            val navController = requireActivity().findNavController(R.id.nav_host_fragment)
            navController.popBackStack(navController.graph.id, true)
            navController.navigate(R.id.homeFragment)

            viewModel.clear()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}