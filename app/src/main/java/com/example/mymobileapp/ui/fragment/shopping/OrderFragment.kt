package com.example.mymobileapp.ui.fragment.shopping

import android.os.Bundle
import android.os.Handler
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
import com.example.mymobileapp.listener.OnClickChoice
import com.example.mymobileapp.model.Address
import com.example.mymobileapp.model.CartProduct
import com.example.mymobileapp.model.User
import com.example.mymobileapp.ui.dialog.ChoiceDialog
import com.example.mymobileapp.util.Resource
import com.example.mymobileapp.viewmodel.AddressViewModel
import com.example.mymobileapp.viewmodel.CartViewModel
import com.example.mymobileapp.viewmodel.OrderViewModel
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
    private val orderViewModel by viewModels<OrderViewModel>()
    private var listOrder: List<CartProduct> = ArrayList()
    private var position = 0
    private var user = User()
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
                        user = it.data!!
                        binding.apply {
                            tvUserName.text = it.data.name
                            tvPhone.text = it.data.phone
                        }
                        addressViewModel.getAddressAccount(user)
                    }
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            addressViewModel.addressList.collectLatest {
                when (it) {
                    is Resource.Error -> {}
                    is Resource.Loading -> {}
                    is Resource.Success -> {
                        if (it.data!!.isEmpty()) {
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
            bundle.putSerializable("user", user)
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
                        listOrder = it.data!!.filter { cartProduct ->
                            cartProduct.select!!
                        }
                        orderProductAdapter.differ.submitList(listOrder)
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

        binding.btnOrder.setOnClickListener{
            if (binding.tvAddress.text.toString().isEmpty()) {
                Toast.makeText(requireContext(), "Bạn chưa chọn địa điểm giao hàng!", Toast.LENGTH_SHORT).show()
            } else {
                val orderDialog = ChoiceDialog("orderFragment", object : OnClickChoice {
                    override fun onClick(choice: Boolean?) {
                        if (choice == true) {
                            order(user.id)
                        }
                    }
                })
                orderDialog.show(requireActivity().supportFragmentManager, null)
            }
        }
        lifecycleScope.launchWhenStarted {
            orderViewModel.createOrder.collectLatest{
                when(it){
                    is Resource.Error ->{
                        Toast.makeText(requireContext(), "Lỗi đặt hàng!", Toast.LENGTH_SHORT).show()
                        binding.btnOrder.setBackgroundColor(android.graphics.Color.parseColor("#000000"))
                        binding.btnOrder.revertAnimation()
                        Handler().postDelayed({
                            controller.popBackStack()
                        }, 3000)
                    }
                    is Resource.Loading -> {
                        binding.btnOrder.setBackgroundColor(android.graphics.Color.parseColor("#FFFF5722"))
                        binding.btnOrder.startAnimation()
                    }
                    is Resource.Success -> {
                        binding.btnOrder.revertAnimation()
                        //Toast.makeText(requireContext(), "Đặt hàng thành công!", Toast.LENGTH_SHORT).show()
                        controller.navigate(R.id.action_orderFragment_to_handleOrderFragment)
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
    private fun order(userId: String) {
        val contact = binding.tvUserName.text.toString().trim() + " - " + binding.tvPhone.text.toString().trim()
        val address = binding.tvAddress.text.toString().trim()
        val total = Convert.ChuyenTien(binding.tvTotal.text.toString().trim()) / 1000
        orderViewModel.createOrder(userId, listOrder, contact, address, total)
    }
}