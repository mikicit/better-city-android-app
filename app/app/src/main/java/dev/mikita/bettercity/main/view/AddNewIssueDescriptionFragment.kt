package dev.mikita.bettercity.main.view

import android.content.Context
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import dagger.hilt.android.AndroidEntryPoint
import dev.mikita.bettercity.R
import dev.mikita.bettercity.databinding.FragmentAddNewIssueDescriptionBinding
import dev.mikita.bettercity.main.viewmodel.AddNewIssueViewModel

@AndroidEntryPoint
class AddNewIssueDescriptionFragment : Fragment() {
    // View Binding
    private var _binding: FragmentAddNewIssueDescriptionBinding? = null
    private val binding get() = _binding!!

    // ViewModel
    private val viewModel: AddNewIssueViewModel by activityViewModels()

    // Utils
    private var isFirstTitleLoad = true
    private var isFirstDescriptionLoad = true

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAddNewIssueDescriptionBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setUpViews()
        setUpEventListeners()
        setUpObservers()
        viewModel.isValidScreen.value = false
    }

    private fun setUpViews() {
        binding.issueDescriptionInput.imeOptions = EditorInfo.IME_ACTION_DONE
        binding.issueDescriptionInput.setRawInputType(InputType.TYPE_CLASS_TEXT)

        if (viewModel.getDescription() != null) {
            binding.issueNameInput.setText(viewModel.getDescription())
        }

        if (viewModel.getDescription() != null) {
            binding.issueDescriptionInput.setText(viewModel.getDescription())
        }
    }

    private fun setUpEventListeners() {
        val titleInput = binding.issueNameInput
        val descriptionInput = binding.issueDescriptionInput

        titleInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_NEXT) {
                viewModel.updateTitle(titleInput.text.toString())
                if (viewModel.titleIsValid.value == true) {
                    descriptionInput.requestFocus()
                }
                true
            } else {
                false
            }
        }

        descriptionInput.setOnEditorActionListener { _, actionId, _ ->
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                viewModel.updateDescription(descriptionInput.text.toString())
                true
            } else {
                false
            }
        }

        titleInput.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                viewModel.updateTitle(titleInput.text.toString())
            }
        }

        descriptionInput.setOnFocusChangeListener { _, hasFocus ->
            if (!hasFocus) {
                viewModel.updateDescription(descriptionInput.text.toString())
            }
        }
    }

    private fun setUpObservers() {
        viewModel.titleIsValid.observe(viewLifecycleOwner) { isValid ->
            viewModel.isValidScreen.value = isValid && viewModel.descriptionIsValid.value == true

            if (isFirstTitleLoad && viewModel.getTitle() == null) {
                isFirstTitleLoad = false
                return@observe
            }

            binding.issueNameInputLayout.error = if (isValid) null else getString(R.string.add_new_issue_issue_name_helper_text)
        }

        viewModel.descriptionIsValid.observe(viewLifecycleOwner) { isValid ->
            viewModel.isValidScreen.value = isValid && viewModel.titleIsValid.value == true

            if (isFirstDescriptionLoad && viewModel.getDescription() == null) {
                isFirstDescriptionLoad = false
                return@observe
            }

            binding.issueDescriptionLayout.error = if (isValid) null else getString(R.string.add_new_issue_issue_description_helper_text)

            if (isValid) {
                if (binding.issueDescriptionInput.hasFocus()) {
                    binding.issueDescriptionInput.clearFocus()
                    val inputMethodManager = requireContext().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                    inputMethodManager.hideSoftInputFromWindow(binding.issueDescriptionInput.windowToken, 0)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}