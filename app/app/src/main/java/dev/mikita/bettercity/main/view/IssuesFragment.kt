package dev.mikita.bettercity.main.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.commit
import androidx.navigation.Navigation
import dagger.hilt.android.AndroidEntryPoint
import dev.mikita.bettercity.R
import dev.mikita.bettercity.databinding.FragmentIssuesBinding
import dev.mikita.bettercity.main.viewmodel.IssuesViewModel
import dev.mikita.bettercity.util.ViewType

@AndroidEntryPoint
class IssuesFragment : Fragment() {
    // View Binding
    private var _binding: FragmentIssuesBinding? = null
    private val binding get() = _binding!!

    // ViewModel
    private val viewModel: IssuesViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentIssuesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViews()
        setUpObservers()

        if (viewModel.getIssues().value == null) {
            viewModel.loadIssues()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setUpViews() {

    }

    private fun setUpObservers() {
        binding.addNewIssue.setOnClickListener {
            val activity = requireActivity() as? MainActivity
            val mainNavController = Navigation.findNavController(activity!!, R.id.nav_host_fragment)
            val action = HomeFragmentDirections.actionHomeFragmentToAddNewIssueFragment()

            mainNavController.navigate(action)
        }

        binding.toggleButton.addOnButtonCheckedListener { _, checkedId, isChecked ->
            when (checkedId) {
                R.id.issues_view_type_list -> {
                    if (isChecked) {
                        viewModel.setCurrentViewType(ViewType.LIST)
                    }
                }
                R.id.issues_view_type_map -> {
                    if (isChecked) {
                        viewModel.setCurrentViewType(ViewType.MAP)
                    }
                }
            }
        }

        viewModel.getCurrentViewType().observe(viewLifecycleOwner) {
            it?.let {
                when (it) {
                    ViewType.LIST -> {
                        childFragmentManager.commit {
                            replace(R.id.fragment_container, IssuesListFragment())
                            binding.toggleButton.check(R.id.issues_view_type_list)
                            setReorderingAllowed(true)
                        }
                    }

                    ViewType.MAP -> {
                        childFragmentManager.commit {
                            replace(R.id.fragment_container, IssuesMapFragment())
                            binding.toggleButton.check(R.id.issues_view_type_map)
                            setReorderingAllowed(true)
                        }
                    }
                }
            }
        }
    }
}