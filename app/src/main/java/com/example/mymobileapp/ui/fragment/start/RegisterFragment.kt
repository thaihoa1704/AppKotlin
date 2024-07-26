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
import com.example.mymobileapp.databinding.FragmentRegisterBinding
import com.example.mymobileapp.model.User
import com.example.mymobileapp.util.Resource
import com.example.mymobileapp.viewmodel.RegisterViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@Suppress("DEPRECATION")
@AndroidEntryPoint
class RegisterFragment : Fragment() {
    private lateinit var binding: FragmentRegisterBinding
    private lateinit var controller: NavController
    private val viewModel by viewModels<RegisterViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        controller = Navigation.findNavController(view)

        binding.btnRegister.setEnabled(false)
        binding.textName.addTextChangedListener(textWatcher)
        binding.textPhone.addTextChangedListener(textWatcher)
        binding.textEmail.addTextChangedListener(textWatcher)
        binding.textPass.addTextChangedListener(textWatcher)
        binding.textPass1.addTextChangedListener(textWatcher)

        binding.apply {
                btnRegister.setOnClickListener {
                    viewModel.userRegister(getUserObj())
                }
        }
        lifecycleScope.launchWhenStarted {
            viewModel.register.collectLatest {
                when(it){
                    is Resource.Error -> {
                        binding.tvMessage.text = it.message
                        binding.tvMessage.visibility = View.VISIBLE
                        binding.btnRegister.revertAnimation()
                    }
                    is Resource.Loading -> {
                        binding.btnRegister.startAnimation()
                    }
                    is Resource.Success -> {
                        binding.btnRegister.revertAnimation()
                        binding.tvMessage.text = "Đăng ký thành công!"
                        binding.tvMessage.visibility = View.VISIBLE
                        Handler().postDelayed({
                            controller.popBackStack()
                        }, 2000)
                    }
                }
            }
        }

        binding.imgBack.setOnClickListener {
            controller.popBackStack()
        }
    }
    private val textWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            val name = binding.textName.text.toString().trim()
            val phone = binding.textPhone.text.toString().trim()
            val email = binding.textEmail.text.toString().trim()
            val password = binding.textPass.text.toString().trim()
            val passwordConfirm = binding.textPass1.text.toString().trim()

            binding.btnRegister.setEnabled(email.isNotEmpty() && password.isNotEmpty()
                    && passwordConfirm.isNotEmpty() && name.isNotEmpty() && phone.isNotEmpty()
            )
        }
        override fun afterTextChanged(s: Editable) {}
    }

    private fun validation(): Boolean {
        var isValid = true
        val email = binding.textEmail.text.toString().trim()
        val password = binding.textPass.text.toString().trim()
        val passwordConfirm = binding.textPass1.text.toString().trim()

        if(!Patterns.EMAIL_ADDRESS.matcher(email).matches())
            isValid = false
        if (password.length < 8)
            isValid = false
        if (password != passwordConfirm)
            isValid = false

        return isValid
    }

    private fun getUserObj(): User {
        return User(
            id = "",
            name = binding.textName.text.toString(),
            phone = binding.textPhone.text.toString(),
            email = binding.textEmail.text.toString(),
            password = binding.textPass.text.toString(),
            type = "customer"
        )
    }
}