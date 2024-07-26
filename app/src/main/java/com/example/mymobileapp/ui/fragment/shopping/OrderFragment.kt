package com.example.mymobileapp.ui.fragment.shopping

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mymobileapp.R
import com.example.mymobileapp.adapter.OrderProductAdapter
import com.example.mymobileapp.databinding.FragmentOrderBinding
import com.example.mymobileapp.helper.Convert
import com.example.mymobileapp.model.Address
import com.example.mymobileapp.util.Resource
import com.example.mymobileapp.viewmodel.AddressViewModel
import com.example.mymobileapp.viewmodel.CartViewModel
import com.example.mymobileapp.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@Suppress("DEPRECATION")
@AndroidEntryPoint
class OrderFragment : Fragment() {
    private lateinit var binding: FragmentOrderBinding
    private lateinit var controller: NavController
    private val userViewModel by viewModels<UserViewModel>()
    private val cartViewModel by viewModels<CartViewModel>()
    private val addressViewModel by viewModels<AddressViewModel>()
    private var list: List<Address> = ArrayList()
    private var position = 0
    private val orderProductAdapter by lazy { OrderProductAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOrderBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        controller = Navigation.findNavController(view)

        lifecycleScope.launchWhenStarted {
            userViewModel.user.collectLatest {
                when (it) {
                    is Resource.Error -> {}
                    is Resource.Loading -> {}
                    is Resource.Success -> {
                        binding.apply {
                            tvUserName.text = it.data?.name
                            tvPhone.text = it.data?.phone
                        }
                    }
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            addressViewModel.addressList.collectLatest {
                when (it) {
                    is Resource.Error -> {
                    }

                    is Resource.Loading -> {
                    }

                    is Resource.Success -> {
                        list = it.data!!
                        if (it.data.isEmpty()) {
                            showEmptyAddress()
                        } else {
                            showAddress()
                            for (i in it.data) {
                                if (i.select) {
                                    binding.tvAddress.text = i.string
                                    position = it.data.indexOf(i)
                                }
                            }
                        }
                    }
                }
            }
        }

        binding.imgChangeAddress.setOnClickListener{
            val bundle = Bundle()
            bundle.putInt("position", position)
            controller.navigate(R.id.action_orderFragment_to_addressFragment, bundle)
        }

        //show order product
        setupOrderProductRecyclerView()
        lifecycleScope.launchWhenStarted {
            cartViewModel.cartList.collectLatest {
                when(it){
                    is Resource.Error -> {
                        Toast.makeText(requireContext(), "Lỗi lấy dữ liệu", Toast.LENGTH_SHORT).show()
                    }
                    is Resource.Loading ->{}
                    is Resource.Success -> {
                        val list = it.data!!.filter { cartProduct ->
                            cartProduct.select!!
                        }
                        orderProductAdapter.differ.submitList(list)
                    }
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            cartViewModel.totalPrice.collectLatest { price ->
                price.let {
                    binding.apply {
                        tvPriceProduct.text = Convert.DinhDangTien(it) + " đ"
                        tvTotal.text = Convert.DinhDangTien(it + 50) + " đ"
                        tvTotal1.text = Convert.DinhDangTien(it + 50) + " đ"
                    }
                }
            }
        }

        binding.imgBack.setOnClickListener{
            controller.popBackStack()
        }
    }

    private fun setupOrderProductRecyclerView() {
        binding.rcvCart.apply {
            layoutManager = LinearLayoutManager(this@OrderFragment.context)
            adapter = orderProductAdapter
        }
    }

    private fun showEmptyAddress() {
        binding.apply {
            tvAddress.visibility = View.GONE
            imgChangeAddress.visibility = View.GONE
            imgAddAddress.visibility = View.VISIBLE
            tvAddAddress.visibility = View.VISIBLE
        }
    }
    private fun showAddress() {
        binding.apply {
            imgChangeAddress.visibility = View.VISIBLE
            imgAddAddress.visibility = View.GONE
            tvAddAddress.visibility = View.GONE
        }
    }
}