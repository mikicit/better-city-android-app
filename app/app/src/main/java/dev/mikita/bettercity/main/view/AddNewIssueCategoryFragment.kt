package dev.mikita.bettercity.main.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.chip.Chip
import dagger.hilt.android.AndroidEntryPoint
import dev.mikita.bettercity.R
import dev.mikita.bettercity.databinding.FragmentAddNewIssueCategoryBinding
import dev.mikita.bettercity.main.viewmodel.AddNewIssueViewModel

@AndroidEntryPoint
class AddNewIssueCategoryFragment : Fragment() {
    // View Binding
    private var _binding: FragmentAddNewIssueCategoryBinding? = null
    private val binding get() = _binding!!

    // ViewModel
    private val viewModel: AddNewIssueViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddNewIssueCategoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViews()
        setUpEventListeners()
        setUpObservers()

        viewModel.isValidScreen.value = false

        // Load data
        if (viewModel.getCategoriesLiveData().value == null) {
            viewModel.loadCategories()
        } else {
            binding.progressBar.hide()
        }
    }

    private fun setUpViews() {
        binding.progressBar.setVisibilityAfterHide(View.INVISIBLE)
    }

    private fun setUpEventListeners() {
        binding.categoryChipGroup.setOnCheckedChangeListener { _, checkedId ->
            val selectedChipId = if (checkedId != View.NO_ID) checkedId else null
            viewModel.updateCategory(selectedChipId?.toLong())
        }
    }

    private fun setUpObservers() {
        viewModel.getCategoriesLiveData().observe(viewLifecycleOwner) { categories ->
            categories?.let {
                for (category in categories) {
                    val chip = layoutInflater.inflate(R.layout.chip_category, binding.categoryChipGroup, false) as Chip
                    chip.text = category.name
                    chip.id = category.id.toInt()

                    binding.categoryChipGroup.addView(chip)
                }
            }

            binding.progressBar.hide()
        }

        viewModel.getCategoryLiveData().observe(viewLifecycleOwner) { category ->
            if (category != null) {
                binding.categoryChipGroup.check(category.toInt())
                viewModel.isValidScreen.value = true
            } else {
                viewModel.isValidScreen.value = false
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}