package com.example.mymobileapp.ui.fragment.start

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.SpannableString
import android.text.TextWatcher
import android.text.style.UnderlineSpan
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.mymobileapp.R
import com.example.mymobileapp.databinding.FragmentLoginBinding
import com.example.mymobileapp.model.User
import com.example.mymobileapp.ui.activity.AdminActivity
import com.example.mymobileapp.ui.activity.ShoppingActivity
import com.example.mymobileapp.util.Resource
import com.example.mymobileapp.viewmodel.LoginViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@Suppress("DEPRECATION")
@AndroidEntryPoint
class LoginFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding
    private lateinit var controller: NavController
    private val viewModel by viewModels<LoginViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        controller = Navigation.findNavController(view)

        binding.btnLogin.setEnabled(false)
        binding.textEmail.addTextChangedListener(textWatcher)
        binding.textPass.addTextChangedListener(textWatcher)

        val forgotPass = SpannableString("Quên mật khẩu?")
        forgotPass.setSpan(UnderlineSpan(), 0, forgotPass.length, 0)
        binding.tvForgotPassword.text = forgotPass

        binding.apply {
            btnLogin.setOnClickListener {
                val email = textEmail.text.toString().trim()
                val password = textPass.text.toString().trim()
                viewModel.userLogin(email, password)
            }
        }
        binding.tvRegister.setOnClickListener {
            controller.navigate(R.id.action_loginFragment_to_registerFragment)
        }
        binding.tvForgotPassword.setOnClickListener {
            controller.navigate(R.id.action_loginFragment_to_forgotPasswordFragment)
        }

        lifecycleScope.launchWhenStarted {
            viewModel.login.collectLatest{
                when(it){
                    is Resource.Error ->{
                        binding.btnLogin.revertAnimation()
                        binding.tvMessage.text = "Email hoặc mật khẩu không đúng!"
                        binding.tvMessage.visibility = View.VISIBLE
                    }
                    is Resource.Loading -> {
                        binding.btnLogin.startAnimation()
                    }
                    is Resource.Success -> {
                        binding.btnLogin.revertAnimation()
                        Toast.makeText(requireContext(), "Đăng nhập thành công!", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.user.collectLatest {
                when (it) {
                    is Resource.Error -> {}
                    is Resource.Loading -> {}
                    is Resource.Success ->{
                        Handler().postDelayed({
                            changeActivity(it.data!!)
                        }, 3000)
                    }
                }
            }
        }
    }

    private fun changeActivity(user: User) {
        val type = user.type
        if (type == "admin"){
            val intent = Intent(requireContext(), AdminActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        } else{
            val intent = Intent(requireContext(), ShoppingActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }

    }
    private val textWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence, start: Int, count: Int, after: Int) {}
        override fun onTextChanged(s: CharSequence, start: Int, before: Int, count: Int) {
            val email = binding.textEmail.text.toString().trim()
            val password = binding.textPass.text.toString().trim()
            val check = email.isNotEmpty() && password.isNotEmpty()
            if (check) {
                binding.btnLogin.setEnabled(true)
                binding.tvMessage.visibility = View.GONE
            } else {
                binding.btnLogin.setEnabled(false)
                binding.tvMessage.visibility = View.GONE
            }
        }
        override fun afterTextChanged(s: Editable) {}
    }

    override fun onResume() {
        super.onResume()
        binding.apply {
            textEmail.text!!.clear()
            textPass.text!!.clear()
        }
    }
}