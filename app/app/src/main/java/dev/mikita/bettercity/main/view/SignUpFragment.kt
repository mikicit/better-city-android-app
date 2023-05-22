package dev.mikita.bettercity.main.view

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.google.firebase.auth.FirebaseAuth
import dagger.hilt.android.AndroidEntryPoint
import dev.mikita.bettercity.databinding.FragmentSignUpBinding
import javax.inject.Inject

@AndroidEntryPoint
class SignUpFragment : Fragment() {
    // View Binding
    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!

    // Auth
    @Inject
    lateinit var mAuth: FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        if (mAuth.currentUser != null) {
            startMainActivity()
        } else {
            setUpEventListeners()
        }
    }

    private fun setUpEventListeners() {
        binding.signUpButton.setOnClickListener {
            // TODO: Implement sign up logic
        }

        binding.signInButton.setOnClickListener {
            findNavController().navigateUp()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    private fun startMainActivity() {
        val intent = Intent(requireActivity(), MainActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }
}