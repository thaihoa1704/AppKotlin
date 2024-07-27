package com.example.mymobileapp.ui.fragment.user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.mymobileapp.R
import com.example.mymobileapp.databinding.FragmentProfileBinding
import com.example.mymobileapp.model.User
import dagger.hilt.android.AndroidEntryPoint

@Suppress("DEPRECATION")
@AndroidEntryPoint
class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding
    private lateinit var controller: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        controller = Navigation.findNavController(view)

        val user = arguments?.getSerializable("user") as User

        binding.apply {
            tvName.text = user.name
            tvEmail.text = user.email
            tvPhone.text = user.phone
        }
        binding.tvName.setOnClickListener {
            val bundle = Bundle()
            bundle.putSerializable("user", user)
            controller.navigate(R.id.action_profileFragment_to_changeNameFragment, bundle)
        }

        binding.tvChangePassword.setOnClickListener {
            val bundle = Bundle()
            bundle.putSerializable("user", user)
            controller.navigate(R.id.action_profileFragment_to_passwordFragment, bundle)
        }
        binding.imgBack.setOnClickListener { controller.popBackStack() }
    }
}