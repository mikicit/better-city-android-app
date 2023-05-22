package dev.mikita.bettercity.main.view

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import dev.mikita.bettercity.databinding.FragmentIssuesListBinding
import dev.mikita.bettercity.main.view.adapter.IssuesIssueCardAdapter
import dev.mikita.bettercity.main.viewmodel.IssuesViewModel

@AndroidEntryPoint
class IssuesListFragment : Fragment() {
    // ViewBinding
    private var _binding: FragmentIssuesListBinding? = null
    private val binding get() = _binding!!

    // ViewModel
    private val issuesViewModel: IssuesViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentIssuesListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViews()
        setUpObservers()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setUpViews() {
        binding.progressBar.setVisibilityAfterHide(View.INVISIBLE)

        binding.issuesCardRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
            adapter = IssuesIssueCardAdapter(requireActivity())
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setUpObservers() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            issuesViewModel.loadIssues()
        }

        issuesViewModel.getIssues().observe(viewLifecycleOwner) { issuesList ->
            issuesList?.let {
                binding.issuesCardRecyclerView.apply {
                    with(adapter as IssuesIssueCardAdapter) {
                        issues = it
                        notifyDataSetChanged()
                    }
                }
            }

            binding.progressBar.hide()
            binding.swipeRefreshLayout.isRefreshing = false
        }
    }
}