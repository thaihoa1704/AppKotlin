package com.example.mymobileapp.ui.fragment.user

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.mymobileapp.R
import com.example.mymobileapp.databinding.FragmentAddAddressBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddAddressFragment : Fragment() {
    private lateinit var binding: FragmentAddAddressBinding
    private lateinit var controller: NavController

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
        binding.imgBack.setOnClickListener{
            controller.popBackStack()
        }

    }
}