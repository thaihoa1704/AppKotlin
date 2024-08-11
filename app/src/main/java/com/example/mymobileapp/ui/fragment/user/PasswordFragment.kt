package com.example.mymobileapp.ui.fragment.user

import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.mymobileapp.databinding.FragmentPasswordBinding
import com.example.mymobileapp.model.User
import com.example.mymobileapp.util.Resource
import com.example.mymobileapp.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@Suppress("DEPRECATION")
@AndroidEntryPoint
class PasswordFragment : Fragment() {
    private lateinit var binding: FragmentPasswordBinding
    private lateinit var controller: NavController
    private val userViewModel by viewModels<UserViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPasswordBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        controller = Navigation.findNavController(view)

        val user = arguments?.getSerializable("user") as User

        binding.btnChange.isEnabled = false
        binding.edtOldPass.addTextChangedListener(textWatcher)
        binding.edtNewPass1.addTextChangedListener(textWatcher)
        binding.edtNewPass2.addTextChangedListener(textWatcher)

        binding.btnChange.setOnClickListener {
            val oldPass = binding.edtOldPass.text.toString().trim()
            val newPass1 = binding.edtNewPass1.text.toString().trim()
            val newPass2 = binding.edtNewPass2.text.toString().trim()
            if (newPass1 != newPass2) {
                binding.tvMessage.text = "Mật khẩu mới không khớp!"
                binding.tvMessage.visibility = View.VISIBLE
            } else {
                userViewModel.checkPassword(user, oldPass, newPass1)
            }
        }

        lifecycleScope.launchWhenStarted {
            userViewModel.changePassword.collectLatest {
                when(it){
                    is Resource.Error -> {
                        binding.btnChange.revertAnimation()
                        binding.tvMessage.text = it.message
                    }
                    is Resource.Loading -> {
                        binding.btnChange.startAnimation()
                    }
                    is Resource.Success -> {
                        binding.btnChange.revertAnimation()
                        binding.tvMessage.text = it.data
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
        override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
        }

        override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
            val oldPass = binding.edtOldPass.text.toString().trim()
            val newPass1 = binding.edtNewPass1.text.toString().trim()
            val newPass2 = binding.edtNewPass2.text.toString().trim()
            binding.btnChange.isEnabled =
                !oldPass.isEmpty() && !newPass1.isEmpty() && !newPass2.isEmpty()
        }

        override fun afterTextChanged(editable: Editable) {
        }
    }
}