package com.example.mymobileapp.ui.fragment.shopping

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mymobileapp.R
import com.example.mymobileapp.adapter.OrderAdapter
import com.example.mymobileapp.databinding.FragmentPurchaseHistoryBinding
import com.example.mymobileapp.listener.ClickItemOrderListener
import com.example.mymobileapp.model.Order
import com.example.mymobileapp.model.Version
import com.example.mymobileapp.util.Resource
import com.example.mymobileapp.viewmodel.OrderViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@Suppress("DEPRECATION")
@AndroidEntryPoint
class PurchaseHistoryFragment : Fragment(), ClickItemOrderListener {
    private lateinit var binding: FragmentPurchaseHistoryBinding
    private lateinit var controller: NavController
    private val orderViewModel by viewModels<OrderViewModel>()
    private lateinit var orderAdapter: OrderAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentPurchaseHistoryBinding.inflate(inflater, container, false)
        return  binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        controller = Navigation.findNavController(view)

        orderViewModel.getPurchaseHistory()
        setOrderRecycleView()
        lifecycleScope.launchWhenStarted {
            orderViewModel.orderList.collectLatest {
                when (it) {
                    is Resource.Error -> {
                        Toast.makeText(requireContext(), "Lỗi lấy dữ liệu", Toast.LENGTH_SHORT).show()
                    }
                    is Resource.Loading -> {}
                    is Resource.Success -> {
                        if (it.data!!.isEmpty()) {
                            binding.tvEmpty.visibility = View.VISIBLE
                        }else{
                            orderAdapter.differ.submitList(it.data)
                        }
                    }
                }
            }
        }

        binding.imgBack.setOnClickListener {
            controller.popBackStack()
        }
    }
    private fun setOrderRecycleView() {
        orderAdapter = OrderAdapter(this)
        binding.rcvOrder.apply {
            layoutManager = LinearLayoutManager(this@PurchaseHistoryFragment.context)
            adapter = orderAdapter
        }
    }

    override fun onClick(order: Order) {
        val bundle = Bundle()
        bundle.putSerializable("Order", order)
        controller.navigate(R.id.action_purchaseHistoryFragment_to_detailOrderFragment, bundle)
    }
}