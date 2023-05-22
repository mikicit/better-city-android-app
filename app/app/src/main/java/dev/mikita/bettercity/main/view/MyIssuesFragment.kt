package dev.mikita.bettercity.main.view

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import dev.mikita.bettercity.R
import dev.mikita.bettercity.databinding.FragmentMyIssuesBinding
import dev.mikita.bettercity.main.view.adapter.MyIssuesViewPagerAdapter
import dev.mikita.bettercity.util.IssueStatus

class MyIssuesFragment : Fragment() {
    // View binding
    private var _binding: FragmentMyIssuesBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyIssuesBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val tabLayout = binding.tabLayout
        val viewPager = binding.viewPager

        viewPager.offscreenPageLimit = 1

        val adapter = MyIssuesViewPagerAdapter(childFragmentManager)
        adapter.addFragment(MyIssuesListFragment.newInstance(null), getString(R.string.my_issues_tab_all))
        adapter.addFragment(MyIssuesListFragment.newInstance(IssueStatus.PUBLISHED), getString(R.string.my_issues_tab_active))
        adapter.addFragment(MyIssuesListFragment.newInstance(IssueStatus.SOLVED), getString(R.string.my_issues_tab_solved))
        adapter.addFragment(MyIssuesListFragment.newInstance(IssueStatus.SOLVING), getString(R.string.my_issues_tab_solving))
        adapter.addFragment(MyIssuesListFragment.newInstance(IssueStatus.MODERATION), getString(R.string.my_issues_tab_moderation))
        viewPager.adapter = adapter

        tabLayout.setupWithViewPager(viewPager)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}