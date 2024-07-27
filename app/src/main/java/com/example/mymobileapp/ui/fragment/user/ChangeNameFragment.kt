package com.example.mymobileapp.ui.fragment.user

import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.mymobileapp.databinding.FragmentChangeNameBinding
import com.example.mymobileapp.model.User
import com.example.mymobileapp.util.Resource
import com.example.mymobileapp.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@Suppress("DEPRECATION")
@AndroidEntryPoint
class ChangeNameFragment : Fragment() {
    private lateinit var userName: String
    private lateinit var binding: FragmentChangeNameBinding
    private lateinit var controller: NavController
    private val userViewModel by viewModels<UserViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentChangeNameBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        controller = Navigation.findNavController(view)

        val user = arguments?.getSerializable("user") as User
        userName = user.name
        binding.edtName.text = Editable.Factory.getInstance().newEditable(userName)
        binding.imgDone.visibility = View.GONE
        binding.edtName.addTextChangedListener(textWatcher)

        binding.imgDone.setOnClickListener {
            userViewModel.changeName(binding.edtName.text.toString())
        }
        lifecycleScope.launchWhenStarted {
            userViewModel.changeName.collectLatest {
                when (it) {
                    is Resource.Error -> {
                        Toast.makeText(requireContext(), "Lỗi hệ thống", Toast.LENGTH_SHORT).show()
                    }
                    is Resource.Loading -> {}
                    is Resource.Success -> {
                        user.name = binding.edtName.text.toString()
                        Toast.makeText(requireContext(), it.data, Toast.LENGTH_SHORT).show()
                        Handler().postDelayed({
                            controller.popBackStack()
                        }, 3000)
                    }
                }
            }
        }
        binding.imgBack.setOnClickListener { controller.popBackStack() }

    }
    private val textWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
        }

        override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
            val name = binding.edtName.text.toString().trim()
            if (name != userName) {
                binding.imgDone.visibility = View.VISIBLE
            } else {
                binding.imgDone.visibility = View.GONE
            }
        }

        override fun afterTextChanged(editable: Editable) {
        }
    }
}