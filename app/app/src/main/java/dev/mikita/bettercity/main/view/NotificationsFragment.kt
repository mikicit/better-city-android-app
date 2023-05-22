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
import dev.mikita.bettercity.databinding.FragmentNotificationsBinding
import dev.mikita.bettercity.main.view.adapter.NotificationsItemAdapter
import dev.mikita.bettercity.main.viewmodel.NotificationsViewModel

@AndroidEntryPoint
class NotificationsFragment : Fragment() {
    // ViewBinding
    private var _binding: FragmentNotificationsBinding? = null
    private val binding get() = _binding!!

    // ViewModel
    private val viewModel: NotificationsViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViews()
        setUpObservers()

        if (viewModel.getNotification().value == null) {
            viewModel.loadNotifications()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setUpViews() {
        binding.progressBar.setVisibilityAfterHide(View.INVISIBLE)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun setUpObservers() {
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.loadNotifications()
        }

        binding.notificationItemsRecyclerView.apply {
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
            adapter = NotificationsItemAdapter()
        }

        viewModel.getNotification().observe(viewLifecycleOwner) { notificationList ->
            notificationList?.let {
                binding.notificationItemsRecyclerView.apply {
                    with(adapter as NotificationsItemAdapter) {
                        notifications = it
                        notifyDataSetChanged()
                    }
                }
            }

            binding.progressBar.hide()
            binding.swipeRefreshLayout.isRefreshing = false
        }
    }
}