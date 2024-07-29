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
    private lateinit var packAdapter: OrderAdapter
    private lateinit var shippingAdapter: OrderAdapter
    private lateinit var completeAdapter: OrderAdapter
    private lateinit var cancelAdapter: OrderAdapter
    private val orderViewModel by viewModels<OrderViewModel>()
    private var confirm = -1
    private var pack = -1
    private var shipping = -1
    private var complete = -1
    private var cancel = -1

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

        orderViewModel.getConfirmOrder()
        orderViewModel.getPackOrder()
        orderViewModel.getShippingOrder()
        orderViewModel.getCompleteOrder()
        orderViewModel.getCancelOrder()

        setConfirmOrderRecycleView()
        setPackOrderRecycleView()
        setShippingOrderRecycleView()
        setCompleteOrderRecycleView()
        setCancelOrderRecycleView()

        lifecycleScope.launchWhenStarted {
            orderViewModel.confirmOrder.collectLatest {
                when(it){
                    is Resource.Error -> {
                        Toast.makeText(requireContext(), "Lỗi lấy dữ liệu", Toast.LENGTH_SHORT).show()
                    }
                    is Resource.Loading ->{}
                    is Resource.Success -> {
                        if (it.data!!.isEmpty()) {
                            confirm = 0
                        }else{
                            confirm = 1
                            confirmAdapter.differ.submitList(it.data)
                        }
                    }
                }
            }
        }
        lifecycleScope.launchWhenStarted {
            orderViewModel.packOrder.collectLatest {
                when(it){
                    is Resource.Error -> {
                        Toast.makeText(requireContext(), "Lỗi lấy dữ liệu", Toast.LENGTH_SHORT).show()
                    }
                    is Resource.Loading ->{}
                    is Resource.Success -> {
                        if (it.data!!.isEmpty()) {
                            pack = 0
                        }else{
                            pack = 1
                            shippingAdapter.differ.submitList(it.data)
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
                        if (it.data!!.isEmpty()) {
                            shipping = 0
                        } else {
                            shipping = 1
                            shippingAdapter.differ.submitList(it.data)
                        }
                    }
                }
            }
        }
        lifecycleScope.launchWhenStarted {
            orderViewModel.completeOrderList.collectLatest {
                when(it){
                    is Resource.Error -> {
                        Toast.makeText(requireContext(), "Lỗi lấy dữ liệu", Toast.LENGTH_SHORT).show()
                    }
                    is Resource.Loading ->{}
                    is Resource.Success -> {
                        if (it.data!!.isEmpty()) {
                            complete = 0
                        }else{
                            complete = 1
                            completeAdapter.differ.submitList(it.data)
                        }
                    }
                }
            }
        }
        lifecycleScope.launchWhenStarted {
            orderViewModel.cancelOrderList.collectLatest {
                when(it){
                    is Resource.Error -> {
                        Toast.makeText(requireContext(), "Lỗi lấy dữ liệu", Toast.LENGTH_SHORT).show()
                    }
                    is Resource.Loading ->{}
                    is Resource.Success -> {
                        if (it.data!!.isEmpty()) {
                            cancel = 0
                        }else{
                            cancel = 1
                            cancelAdapter.differ.submitList(it.data)
                        }
                    }
                }
            }
        }

        binding.tvConfirm.setOnClickListener { confirm() }
        binding.tvPack.setOnClickListener { pack() }
        binding.tvShipping.setOnClickListener { shipping() }
        binding.tvDelivered.setOnClickListener { delivered() }
        binding.tvCancel.setOnClickListener { cancel() }

        var id = 0
        if (arguments != null) {
            id = requireArguments().getInt("id")
        }
        when (id) {
            1 -> {
                confirm()
            }
            2 -> {
                pack()
            }
            3 -> {
                shipping()
            }
            4 -> {
                delivered()
            }
        }
        binding.imgBack.setOnClickListener {
            controller.popBackStack()
        }
    }

    private fun cancel() {
        if (cancel == 0) {
            binding.tvEmpty5.visibility = View.VISIBLE
        }else{
            binding.tvEmpty5.visibility = View.GONE
        }
        binding.tvEmpty1.visibility = View.GONE
        binding.tvEmpty2.visibility = View.GONE
        binding.tvEmpty3.visibility = View.GONE
        binding.tvEmpty4.visibility = View.GONE
        binding.rcvConfirm.visibility = View.GONE
        binding.rcvShipping.visibility = View.GONE
        binding.rcvPack.visibility = View.GONE
        binding.rcvComplete.visibility = View.GONE
        binding.rcvCancel.visibility = View.VISIBLE
        binding.tvConfirm.setTextColor(Color.parseColor("#FF000000"))
        binding.tvConfirm.typeface = Typeface.DEFAULT
        binding.lineConfirm.visibility = View.GONE
        binding.tvShipping.setTextColor(Color.parseColor("#FF000000"))
        binding.tvShipping.typeface = Typeface.DEFAULT
        binding.lineShipping.visibility = View.GONE
        binding.tvPack.setTextColor(Color.parseColor("#FF000000"))
        binding.tvPack.typeface = Typeface.DEFAULT
        binding.linePack.visibility = View.GONE
        binding.tvDelivered.setTextColor(Color.parseColor("#FF000000"))
        binding.tvDelivered.typeface = Typeface.DEFAULT
        binding.lineDelivered.visibility = View.GONE
        binding.tvCancel.setTextColor(Color.parseColor("#FF5722"))
        binding.tvCancel.typeface = Typeface.DEFAULT_BOLD
        binding.lineCancel.visibility = View.VISIBLE
    }

    private fun pack() {
        if (pack == 0) {
            binding.tvEmpty2.visibility = View.VISIBLE
        } else{
            binding.tvEmpty2.visibility = View.GONE
        }
        binding.tvEmpty1.visibility = View.GONE
        binding.tvEmpty3.visibility = View.GONE
        binding.tvEmpty4.visibility = View.GONE
        binding.tvEmpty5.visibility = View.GONE
        binding.rcvConfirm.visibility = View.GONE
        binding.rcvShipping.visibility = View.GONE
        binding.rcvPack.visibility = View.VISIBLE
        binding.rcvComplete.visibility = View.GONE
        binding.rcvCancel.visibility = View.GONE
        binding.tvConfirm.setTextColor(Color.parseColor("#FF000000"))
        binding.tvConfirm.typeface = Typeface.DEFAULT
        binding.lineConfirm.visibility = View.GONE
        binding.tvShipping.setTextColor(Color.parseColor("#FF000000"))
        binding.tvShipping.typeface = Typeface.DEFAULT
        binding.lineShipping.visibility = View.GONE
        binding.tvPack.setTextColor(Color.parseColor("#FF5722"))
        binding.tvPack.typeface = Typeface.DEFAULT_BOLD
        binding.linePack.visibility = View.VISIBLE
        binding.tvDelivered.setTextColor(Color.parseColor("#FF000000"))
        binding.tvDelivered.typeface = Typeface.DEFAULT
        binding.lineDelivered.visibility = View.GONE
        binding.tvCancel.setTextColor(Color.parseColor("#FF000000"))
        binding.tvCancel.typeface = Typeface.DEFAULT
        binding.lineCancel.visibility = View.GONE
    }

    private fun confirm() {
        if (confirm == 0) {
            binding.tvEmpty1.visibility = View.VISIBLE
        } else{
            binding.tvEmpty1.visibility = View.GONE
        }
        binding.tvEmpty2.visibility = View.GONE
        binding.tvEmpty3.visibility = View.GONE
        binding.tvEmpty4.visibility = View.GONE
        binding.tvEmpty5.visibility = View.GONE
        binding.rcvConfirm.visibility = View.VISIBLE
        binding.rcvShipping.visibility = View.GONE
        binding.rcvPack.visibility = View.GONE
        binding.rcvComplete.visibility = View.GONE
        binding.rcvCancel.visibility = View.GONE
        binding.tvConfirm.setTextColor(Color.parseColor("#FF5722"))
        binding.tvConfirm.typeface = Typeface.DEFAULT_BOLD
        binding.lineConfirm.visibility = View.VISIBLE
        binding.tvShipping.setTextColor(Color.parseColor("#FF000000"))
        binding.tvShipping.typeface = Typeface.DEFAULT
        binding.lineShipping.visibility = View.GONE
        binding.tvPack.setTextColor(Color.parseColor("#FF000000"))
        binding.tvPack.typeface = Typeface.DEFAULT
        binding.linePack.visibility = View.GONE
        binding.tvDelivered.setTextColor(Color.parseColor("#FF000000"))
        binding.tvDelivered.typeface = Typeface.DEFAULT
        binding.lineDelivered.visibility = View.GONE
        binding.tvCancel.setTextColor(Color.parseColor("#FF000000"))
        binding.tvCancel.typeface = Typeface.DEFAULT
        binding.lineCancel.visibility = View.GONE
    }

    private fun shipping() {
        if (shipping == 0) {
            binding.tvEmpty3.visibility = View.VISIBLE
        } else{
            binding.tvEmpty3.visibility = View.GONE
        }
        binding.tvEmpty1.visibility = View.GONE
        binding.tvEmpty2.visibility = View.GONE
        binding.tvEmpty4.visibility = View.GONE
        binding.tvEmpty5.visibility = View.GONE
        binding.rcvConfirm.visibility = View.GONE
        binding.rcvShipping.visibility = View.VISIBLE
        binding.rcvPack.visibility = View.GONE
        binding.rcvComplete.visibility = View.GONE
        binding.rcvCancel.visibility = View.GONE
        binding.tvShipping.setTextColor(Color.parseColor("#FF5722"))
        binding.tvShipping.typeface = Typeface.DEFAULT_BOLD
        binding.lineShipping.visibility = View.VISIBLE
        binding.tvConfirm.setTextColor(Color.parseColor("#FF000000"))
        binding.tvConfirm.typeface = Typeface.DEFAULT
        binding.lineConfirm.visibility = View.GONE
        binding.tvPack.setTextColor(Color.parseColor("#FF000000"))
        binding.tvPack.typeface = Typeface.DEFAULT
        binding.linePack.visibility = View.GONE
        binding.tvDelivered.setTextColor(Color.parseColor("#FF000000"))
        binding.tvDelivered.typeface = Typeface.DEFAULT
        binding.lineDelivered.visibility = View.GONE
        binding.tvCancel.setTextColor(Color.parseColor("#FF000000"))
        binding.tvCancel.typeface = Typeface.DEFAULT
        binding.lineCancel.visibility = View.GONE
    }

    private fun delivered() {
        if (complete == 0) {
            binding.tvEmpty4.visibility = View.VISIBLE
        } else{
            binding.tvEmpty4.visibility = View.GONE
        }
        binding.tvEmpty1.visibility = View.GONE
        binding.tvEmpty2.visibility = View.GONE
        binding.tvEmpty3.visibility = View.GONE
        binding.tvEmpty5.visibility = View.GONE
        binding.rcvConfirm.visibility = View.GONE
        binding.rcvShipping.visibility = View.GONE
        binding.rcvComplete.visibility = View.VISIBLE
        binding.rcvCancel.visibility = View.GONE
        binding.rcvPack.visibility = View.GONE
        binding.tvDelivered.setTextColor(Color.parseColor("#FF5722"))
        binding.tvDelivered.typeface = Typeface.DEFAULT_BOLD
        binding.lineDelivered.visibility = View.VISIBLE
        binding.tvConfirm.setTextColor(Color.parseColor("#FF000000"))
        binding.tvConfirm.typeface = Typeface.DEFAULT
        binding.lineConfirm.visibility = View.GONE
        binding.tvShipping.setTextColor(Color.parseColor("#FF000000"))
        binding.tvShipping.typeface = Typeface.DEFAULT
        binding.lineShipping.visibility = View.GONE
        binding.tvCancel.setTextColor(Color.parseColor("#FF000000"))
        binding.tvCancel.typeface = Typeface.DEFAULT
        binding.lineCancel.visibility = View.GONE
        binding.tvPack.setTextColor(Color.parseColor("#FF000000"))
        binding.tvPack.typeface = Typeface.DEFAULT
        binding.linePack.visibility = View.GONE
    }

    private fun setConfirmOrderRecycleView() {
        confirmAdapter = OrderAdapter(this)
        binding.rcvConfirm.apply {
            layoutManager = LinearLayoutManager(this@OrderProcessFragment.context)
            adapter = confirmAdapter
        }
    }

    private fun setPackOrderRecycleView() {
        packAdapter = OrderAdapter(this)
        binding.rcvPack.apply {
            layoutManager = LinearLayoutManager(this@OrderProcessFragment.context)
            adapter = packAdapter
        }
    }

    private fun setShippingOrderRecycleView() {
        shippingAdapter = OrderAdapter(this)
        binding.rcvShipping.apply {
            layoutManager = LinearLayoutManager(this@OrderProcessFragment.context)
            adapter = shippingAdapter
        }
    }

    private fun setCompleteOrderRecycleView() {
        completeAdapter = OrderAdapter(this)
        binding.rcvComplete.apply {
            layoutManager = LinearLayoutManager(this@OrderProcessFragment.context)
            adapter = completeAdapter
        }
    }
    private fun setCancelOrderRecycleView() {
        cancelAdapter = OrderAdapter(this)
        binding.rcvCancel.apply {
            layoutManager = LinearLayoutManager(this@OrderProcessFragment.context)
            adapter = cancelAdapter
        }
    }
    override fun onClick(order: Order) {
        val bundle = Bundle()
        bundle.putSerializable("Order", order)
        bundle.putString("From", "OrderProcessFragment")
        controller.navigate(R.id.action_orderProcessFragment_to_detailOrderFragment, bundle)
    }
}