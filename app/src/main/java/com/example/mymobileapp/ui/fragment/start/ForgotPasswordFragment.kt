package com.example.mymobileapp.ui.fragment.start

import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.util.Patterns
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.mymobileapp.databinding.FragmentForgotPasswordBinding
import com.example.mymobileapp.util.Resource
import com.example.mymobileapp.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest

@Suppress("DEPRECATION")
@AndroidEntryPoint
class ForgotPasswordFragment : Fragment() {
    private lateinit var binding: FragmentForgotPasswordBinding
    private lateinit var controller: NavController
    private val viewModel by viewModels<UserViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentForgotPasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        controller = Navigation.findNavController(view)

        binding.btnReset.setEnabled(false)
        binding.textEmail.addTextChangedListener(textWatcher)

        binding.imgBack.setOnClickListener {
            controller.popBackStack()
        }

        binding.btnReset.setOnClickListener {
            val email = binding.textEmail.text.toString().trim()
            if (validation()){
                viewModel.sendLinkToEmail(email)
                binding.tvMessage.visibility = View.GONE
            } else{
                binding.tvMessage.text = "Email không đúng định dạng!"
                binding.tvMessage.visibility = View.VISIBLE
            }
        }
        lifecycleScope.launchWhenStarted {
            viewModel.forgotPassword.collectLatest {
                when(it){
                    is Resource.Error -> {
                        binding.btnReset.revertAnimation()
                        binding.tvMessage.text = it.message
                        binding.tvMessage.visibility = View.VISIBLE
                    }
                    is Resource.Loading -> {
                        binding.btnReset.startAnimation()
                    }
                    is Resource.Success -> {
                        binding.btnReset.revertAnimation()
                        binding.tvMessage.text = it.message
                        binding.tvMessage.visibility = View.VISIBLE
                        Handler().postDelayed({
                            controller.popBackStack()
                        }, 2000)
                    }
                }
            }
        }
    }
    private fun validation(): Boolean {
        var isValid = true
        val email = binding.textEmail.text.toString().trim()

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches())
            isValid = false

        return isValid
    }
    private val textWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            val email = binding.textEmail.text.toString().trim()
            binding.btnReset.setEnabled(email.isNotEmpty())
        }
        override fun afterTextChanged(s: Editable) {}
    }
}