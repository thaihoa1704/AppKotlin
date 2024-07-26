package com.example.mymobileapp.ui.fragment.shopping

import android.graphics.Color
import android.graphics.Typeface
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
import com.example.mymobileapp.adapter.OrderAdapter
import com.example.mymobileapp.databinding.FragmentOrderProcessBinding
import com.example.mymobileapp.listener.ClickItemOrderListener
import com.example.mymobileapp.model.Order
import com.example.mymobileapp.util.Resource
import com.example.mymobileapp.viewmodel.OrderViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@Suppress("DEPRECATION")
@AndroidEntryPoint
class OrderProcessFragment : Fragment(), ClickItemOrderListener {
    private lateinit var binding: FragmentOrderProcessBinding
    private lateinit var controller: NavController
    private lateinit var confirmAdapter: OrderAdapter
    private lateinit var shippingAdapter: OrderAdapter
    private lateinit var rateAdapter: OrderAdapter
    private val orderViewModel by viewModels<OrderViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOrderProcessBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        controller = Navigation.findNavController(view)

        var id = 0
        if (arguments != null) {
            id = requireArguments().getInt("id")
        }
        when (id) {
            1 -> {
                confirm()
            }
            2 -> {
                shipping()
            }
            3 -> {
                rate()
            }
        }
        orderViewModel.getConfirmOrder()
        orderViewModel.getShippingOrder()
        orderViewModel.getRateOrder()

        setConfirmOrderRecycleView()
        setShippingOrderRecycleView()
        setRateOrderRecycleView()

        lifecycleScope.launchWhenStarted {
            orderViewModel.confirmOrder.collectLatest {
                when(it){
                    is Resource.Error -> {
                        Toast.makeText(requireContext(), "Lỗi lấy dữ liệu", Toast.LENGTH_SHORT).show()
                    }
                    is Resource.Loading ->{}
                    is Resource.Success -> {
                        if (it.data!!.isNotEmpty()) {
                            confirmAdapter.differ.submitList(it.data)
                        }
                    }
                }
            }
        }
        lifecycleScope.launchWhenStarted {
            orderViewModel.shippingOrderList.collectLatest {
                when(it){
                    is Resource.Error -> {
                        Toast.makeText(requireContext(), "Lỗi lấy dữ liệu", Toast.LENGTH_SHORT).show()
                    }
                    is Resource.Loading ->{}
                    is Resource.Success -> {
                        if (it.data!!.isNotEmpty()) {
                            shippingAdapter.differ.submitList(it.data)
                        }
                    }
                }
            }
        }
        lifecycleScope.launchWhenStarted {
            orderViewModel.rateOrderList.collectLatest {
                when(it){
                    is Resource.Error -> {
                        Toast.makeText(requireContext(), "Lỗi lấy dữ liệu", Toast.LENGTH_SHORT).show()
                    }
                    is Resource.Loading ->{}
                    is Resource.Success -> {
                        if (it.data!!.isNotEmpty()) {
                            rateAdapter.differ.submitList(it.data)
                        }
                    }
                }
            }
        }

        binding.tvConfirm.setOnClickListener { confirm() }
        binding.tvShipping.setOnClickListener { shipping() }
        binding.tvRate.setOnClickListener { rate() }
        binding.imgBack.setOnClickListener {
            controller.popBackStack()
        }
    }

    private fun confirm() {
        binding.rcvConfirm.visibility = View.VISIBLE
        binding.rcvShipping.visibility = View.GONE
        binding.rcvRate.visibility = View.GONE
        binding.tvConfirm.setTextColor(Color.parseColor("#FF5722"))
        binding.tvConfirm.typeface = Typeface.DEFAULT_BOLD
        binding.lineConfirm.visibility = View.VISIBLE
        binding.tvShipping.setTextColor(Color.parseColor("#FF000000"))
        binding.tvShipping.typeface = Typeface.DEFAULT
        binding.lineShipping.visibility = View.GONE
        binding.tvRate.setTextColor(Color.parseColor("#FF000000"))
        binding.tvRate.typeface = Typeface.DEFAULT
        binding.lineRate.visibility = View.GONE
    }

    private fun setConfirmOrderRecycleView() {
        confirmAdapter = OrderAdapter(this)
        binding.rcvConfirm.apply {
            layoutManager = LinearLayoutManager(this@OrderProcessFragment.context)
            adapter = confirmAdapter
        }
    }

    private fun shipping() {
        binding.rcvConfirm.visibility = View.GONE
        binding.rcvShipping.visibility = View.VISIBLE
        binding.rcvRate.visibility = View.GONE
        binding.tvShipping.setTextColor(Color.parseColor("#FF5722"))
        binding.tvShipping.typeface = Typeface.DEFAULT_BOLD
        binding.lineShipping.visibility = View.VISIBLE
        binding.tvConfirm.setTextColor(Color.parseColor("#FF000000"))
        binding.tvConfirm.typeface = Typeface.DEFAULT
        binding.lineConfirm.visibility = View.GONE
        binding.tvRate.setTextColor(Color.parseColor("#FF000000"))
        binding.tvRate.typeface = Typeface.DEFAULT
        binding.lineRate.visibility = View.GONE
    }

    private fun setShippingOrderRecycleView() {
        shippingAdapter = OrderAdapter(this)
        binding.rcvShipping.apply {
            layoutManager = LinearLayoutManager(this@OrderProcessFragment.context)
            adapter = shippingAdapter
        }
    }

    private fun rate() {
        binding.rcvConfirm.visibility = View.GONE
        binding.rcvShipping.visibility = View.GONE
        binding.rcvRate.visibility = View.VISIBLE
        binding.tvRate.setTextColor(Color.parseColor("#FF5722"))
        binding.tvRate.typeface = Typeface.DEFAULT_BOLD
        binding.lineRate.visibility = View.VISIBLE
        binding.tvConfirm.setTextColor(Color.parseColor("#FF000000"))
        binding.tvConfirm.typeface = Typeface.DEFAULT
        binding.lineConfirm.visibility = View.GONE
        binding.tvShipping.setTextColor(Color.parseColor("#FF000000"))
        binding.tvShipping.typeface = Typeface.DEFAULT
        binding.lineShipping.visibility = View.GONE
    }

    private fun setRateOrderRecycleView() {
        rateAdapter = OrderAdapter(this)
        binding.rcvRate.apply {
            layoutManager = LinearLayoutManager(this@OrderProcessFragment.context)
            adapter = rateAdapter
        }
    }

    override fun onClick(order: Order) {
        val bundle = Bundle()
        bundle.putSerializable("Order", order)
        bundle.putString("From", "OrderProcessFragment")
        controller.navigate(R.id.action_orderProcessFragment_to_detailOrderFragment, bundle)
    }
}