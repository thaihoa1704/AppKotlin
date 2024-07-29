package com.example.mymobileapp.ui.fragment.manage

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.mymobileapp.R
import com.example.mymobileapp.databinding.FragmentCategoryBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CategoryFragment : Fragment() {
    private lateinit var binding: FragmentCategoryBinding
    private lateinit var controller: NavController

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCategoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        controller = Navigation.findNavController(view)

        binding.imgPhone.setOnClickListener { moveToNewFragment("Điện thoại") }
        binding.imgHeadPhone.setOnClickListener { moveToNewFragment("Tai nghe") }
        binding.imgLaptop.setOnClickListener { moveToNewFragment("Laptop") }
        binding.imgWatch.setOnClickListener { moveToNewFragment("Đồng hồ") }
        binding.imageAccessory.setOnClickListener { moveToNewFragment("Phụ kiện") }
        binding.imgSpecial.setOnClickListener { moveToNewFragment("Đặc biệt") }
        binding.imgBack.setOnClickListener { controller.popBackStack() }
    }
    private fun moveToNewFragment(category: String) {
        val bundle = Bundle()
        bundle.putString("category", category)
        bundle.putString("type", "admin")
        controller.navigate(R.id.action_categoryFragment_to_productListFragment2, bundle)
    }
}