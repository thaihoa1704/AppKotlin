package com.example.mymobileapp.ui.fragment.manage

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mymobileapp.R
import com.example.mymobileapp.adapter.CountAdapter
import com.example.mymobileapp.databinding.FragmentProductOrderBinding
import com.example.mymobileapp.model.CartProduct
import com.example.mymobileapp.model.Temp
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProductOrderFragment : Fragment() {
    private lateinit var binding: FragmentProductOrderBinding
    private val countAdapter by lazy { CountAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProductOrderBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val temp = arguments?.getSerializable("list") as Temp

        setupProductRecyclerView()
        countAdapter.differ.submitList(temp.list)

        binding.imgBack.setOnClickListener {
            removeFragment()
        }
    }
        private fun setupProductRecyclerView() {
        binding.rcvProduct.apply {
            layoutManager = LinearLayoutManager(this@ProductOrderFragment.context)
            adapter = countAdapter
        }
    }
    private fun removeFragment() {
        val fragmentManager = requireActivity().supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.remove(this)
        fragmentTransaction.commit()
    }
}