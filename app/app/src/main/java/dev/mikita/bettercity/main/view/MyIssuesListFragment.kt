package dev.mikita.bettercity.main.view

import android.annotation.SuppressLint
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.LinearLayoutManager
import dagger.hilt.android.AndroidEntryPoint
import dev.mikita.bettercity.databinding.FragmentMyIssuesListBinding
import dev.mikita.bettercity.main.view.adapter.IssuesIssueCardAdapter
import dev.mikita.bettercity.main.viewmodel.MyIssuesListViewModel
import dev.mikita.bettercity.util.IssueStatus

private const val ARG_STATUS = "status"

@AndroidEntryPoint
class MyIssuesListFragment : Fragment() {
    // ViewBinding
    private var _binding: FragmentMyIssuesListBinding? = null
    private val binding get() = _binding!!

    // Args
    private var status: IssueStatus? = null

    // ViewModel
    private val viewModel: MyIssuesListViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            status = it.getSerializable(ARG_STATUS) as IssueStatus?
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyIssuesListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViews()
        setUpObservers()

        if (viewModel.getIssues().value == null) {
            viewModel.loadIssues(status)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setUpViews() {
        binding.progressBar.setVisibilityAfterHide(View.INVISIBLE)

        binding.recyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
            adapter = IssuesIssueCardAdapter(requireActivity())
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setUpObservers() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.loadIssues(status)
        }

        viewModel.getIssues().observe(viewLifecycleOwner) { issuesList ->
            issuesList?.let {
                binding.recyclerView.apply {
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

    companion object {
        @JvmStatic
        fun newInstance(status: IssueStatus?) =
            MyIssuesListFragment().apply {
                arguments = Bundle().apply {
                    if (status != null) {
                        putSerializable(ARG_STATUS, status)
                    }
                }
            }
    }
}