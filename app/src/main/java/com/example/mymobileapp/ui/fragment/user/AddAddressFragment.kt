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
import com.example.mymobileapp.databinding.FragmentAddAddressBinding
import com.example.mymobileapp.util.Resource
import com.example.mymobileapp.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@Suppress("DEPRECATION")
@AndroidEntryPoint
class AddAddressFragment : Fragment() {
    private lateinit var binding: FragmentAddAddressBinding
    private lateinit var controller: NavController
    private val userViewModel by viewModels<UserViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddAddressBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        controller = Navigation.findNavController(view)

        //var position = requireArguments().getInt("position")

        binding.btnAdd.isEnabled = false
        binding.edtAddress1.addTextChangedListener(textWatcher)
        binding.edtAddress2.addTextChangedListener(textWatcher)

        binding.btnAdd.setOnClickListener {
            val address = binding.edtAddress1.text.toString().trim() +
                    " " + binding.edtAddress2.text.toString().trim()
            userViewModel.addAddress(address, false)
        }
        lifecycleScope.launchWhenStarted {
            userViewModel.addressList.collectLatest{
                when(it){
                    is Resource.Error ->{
                        binding.btnAdd.revertAnimation()
                        Toast.makeText(requireContext(), "Lỗi hệ thống", Toast.LENGTH_SHORT).show()
                        Handler().postDelayed({
                            controller.popBackStack()
                        },2000)
                    }
                    is Resource.Loading -> {
                        binding.btnAdd.startAnimation()
                    }
                    is Resource.Success -> {
                        binding.btnAdd.revertAnimation()
                        Toast.makeText(requireContext(), it.data, Toast.LENGTH_SHORT).show()
                        Handler().postDelayed({
                            controller.popBackStack()
                        },2000)
                    }
                }
            }
        }

        binding.imgBack.setOnClickListener{
            //controller.popBackStack()
            removeFragment()
        }
    }
    private val textWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
        }

        override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
            val email = binding.edtAddress1.text.toString().trim()
            val pass = binding.edtAddress2.text.toString().trim()
            binding.btnAdd.isEnabled = email.isNotEmpty() && pass.isNotEmpty()
        }

        override fun afterTextChanged(editable: Editable) {
        }
    }
    private fun removeFragment() {
        val fragmentManager = requireActivity().supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.remove(this)
        fragmentTransaction.commit()
    }
}