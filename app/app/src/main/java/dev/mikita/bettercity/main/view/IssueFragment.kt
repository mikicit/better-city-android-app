package dev.mikita.bettercity.main.view

import android.location.Geocoder
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import coil.api.load
import com.google.android.material.snackbar.Snackbar
import dagger.hilt.android.AndroidEntryPoint
import dev.mikita.bettercity.R
import dev.mikita.bettercity.databinding.FragmentIssueBinding
import dev.mikita.bettercity.main.viewmodel.IssueViewModel
import java.text.SimpleDateFormat
import java.util.Locale

@AndroidEntryPoint
class IssueFragment : Fragment() {
    // ViewBinding
    private var _binding: FragmentIssueBinding? = null
    private val binding get() = _binding!!

    // Args
    private val args: IssueFragmentArgs by navArgs()

    // View Model
    private val viewModel: IssueViewModel by viewModels()

    // Utils
    private val dateFormat = SimpleDateFormat("dd.MM.yyyy HH:mm", Locale.getDefault())

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentIssueBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViews()
        setUpEventListeners()
        setUpObservers()

        if (savedInstanceState == null || viewModel.getIssueLiveData().value == null) {
            viewModel.loadData(args.id)
        } else {
            binding.progressBar.hide()
            binding.issueScrollView.visibility = View.VISIBLE
        }

        if (savedInstanceState == null || viewModel.getIsLikedLiveData().value == null) {
            viewModel.isLiked(args.id)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun setUpViews() {
        binding.progressBar.setVisibilityAfterHide(View.INVISIBLE)
        binding.issueScrollView.visibility = View.INVISIBLE
    }

    private fun setUpEventListeners() {
        // Attach Event Listeners
        binding.swipeRefreshLayout.setOnRefreshListener {
            viewModel.loadData(args.id)
        }

        // Like Checkbox
        binding.issueLikeButton.setOnCheckedChangeListener { _, isChecked ->
            if (!isChecked) {
                if (viewModel.getIsLikedLiveData().value == true) {
                    viewModel.unlike(args.id)
                }
            } else {
                if (viewModel.getIsLikedLiveData().value == false) {
                    viewModel.like(args.id)
                }
            }
        }

        // Map Button
        binding.issueMapButton.setOnClickListener {
            val action =
                IssueFragmentDirections.actionIssueFragmentToIssueLocationFragment(viewModel.getIssueLiveData().value!!.coordinates)
            findNavController().navigate(action)
        }

        // Author Button
        binding.issueAuthorLayout.setOnClickListener {
            val action =
                IssueFragmentDirections.actionIssueFragmentToUserProfileFragment(viewModel.getIssueLiveData().value!!.author.uid)
            findNavController().navigate(action)
        }
    }

    private fun setUpObservers() {
        // Set Issue Data Observer
        viewModel.getIssueLiveData().observe(viewLifecycleOwner) { issue ->
            issue?.let {
                binding.issueImage.load(it.photo)
                binding.issueTitle.text = it.title
                binding.issueDescription.text = it.description
                binding.issueCreated.text = dateFormat.format(it.creationDate)
                binding.issueStatus.text = it.status
                binding.issueLikes.text = it.likes.toString()
                binding.issueCategory.text = it.category
                binding.issueAuthorName.text = String.format("%s %s", it.author.firstName, it.author.lastName)
                binding.issueAuthorAvatar.load(it.author.photo)

                // Convert coordinate to string
                val geocoder = Geocoder(requireContext(), Locale.getDefault())
                val addresses = geocoder.getFromLocation(it.coordinates.latitude, it.coordinates.longitude, 1)

                if (addresses != null) {
                    if (addresses.isNotEmpty()) {
                        val address = addresses[0]
                        val sb = StringBuilder()
                        for (i in 0..address.maxAddressLineIndex) {
                            sb.append(address.getAddressLine(i))
                            if (i < address.maxAddressLineIndex) {
                                sb.append(", ")
                            }
                        }
                        binding.issueAddress.text = sb.toString()
                    }
                }

                // Status background
                val background = when (it.status) {
                    "PUBLISHED" -> R.drawable.rounded_corner_published
                    "SOLVING" -> R.drawable.rounded_corner_solving
                    "MODERATION" -> R.drawable.rounded_corner_moderation
                    "SOLVED" -> R.drawable.rounded_corner_solved
                    else -> R.drawable.rounded_corner_moderation
                }

                binding.issueStatus.setBackgroundResource(background)

                // Status info button
                val status = viewModel.getIssueLiveData().value?.status
                val result = status == "SOLVED" || status == "SOLVING"

                if (result) {
                    val color = if (status == "SOLVING") {
                        R.color.colorIssueStatusSolving
                    } else {
                        R.color.colorIssueStatusSolved
                    }

                    binding.statusInfoButton.backgroundTintList =
                        resources.getColorStateList(color, null)
                    binding.statusInfoButton.visibility = View.VISIBLE

                    binding.statusInfoButton.setOnClickListener {
                        if (status == "SOLVING") {
                            val reservationBottomSheet = ReservationFragment.newInstance(viewModel.getIssueLiveData().value!!.id)
                            reservationBottomSheet.show(childFragmentManager, ReservationFragment.TAG)
                        } else {
                            val solutionBottomSheet = SolutionFragment.newInstance(viewModel.getIssueLiveData().value!!.id)
                            solutionBottomSheet.show(childFragmentManager, SolutionFragment.TAG)
                        }
                    }
                } else {
                    binding.statusInfoButton.visibility = View.GONE
                }
            }

            // Restore visibility
            binding.progressBar.hide()
            binding.swipeRefreshLayout.isRefreshing = false
            binding.issueScrollView.visibility = View.VISIBLE
        }

        // Set Is Liked Observer
        viewModel.getIsLikedLiveData().observe(viewLifecycleOwner) {
            binding.issueLikeButton.isChecked = it
        }

        // Set Error Observer
        viewModel.getErrorLiveData().observe(viewLifecycleOwner) {
            binding.progressBar.hide()
            binding.swipeRefreshLayout.isRefreshing = false
            binding.issueScrollView.visibility = View.VISIBLE

            Snackbar.make(binding.root, it.message, Snackbar.LENGTH_LONG).show()
        }
    }
}