package com.example.mymobileapp.ui.fragment.manage

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mymobileapp.R
import com.example.mymobileapp.adapter.OrderAdapter
import com.example.mymobileapp.databinding.FragmentStatusOrderBinding
import com.example.mymobileapp.listener.ClickItemOrderListener
import com.example.mymobileapp.model.Order
import com.example.mymobileapp.util.Resource
import com.example.mymobileapp.viewmodel.OrderViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@Suppress("DEPRECATION")
@AndroidEntryPoint
class StatusOrderFragment : Fragment(), ClickItemOrderListener {
    private lateinit var binding: FragmentStatusOrderBinding
    private lateinit var confirmAdapter: OrderAdapter
    private lateinit var packAdapter: OrderAdapter
    private lateinit var shippingAdapter: OrderAdapter
    private lateinit var cancelAdapter: OrderAdapter
    private val orderViewModel by viewModels<OrderViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStatusOrderBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        orderViewModel.getConfirmOrder()
        orderViewModel.getPackOrder()
        orderViewModel.getShippingOrder()
        orderViewModel.getCancelOrder()

        setConfirmOrderRecycleView()
        setPackOrderRecycleView()
        setShippingOrderRecycleView()
        setCancelOrderRecycleView()

        var confirm = -1
        var pack = -1
        var shipping = -1
        var cancel = -1
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

        binding.tvConfirm.setOnClickListener { setConfirm(confirm) }
        binding.tvPack.setOnClickListener { setPack(pack) }
        binding.tvShipping.setOnClickListener { setShipping(shipping) }
        binding.tvCancel.setOnClickListener { setCancel(cancel) }

        var id = 0
        if (arguments != null) {
            id = requireArguments().getInt("id")
        }
        when (id) {
            1 -> {
                setConfirm(confirm)
            }
            2 -> {
                setPack(pack)
            }
            3 -> {
                setShipping(shipping)
            }
            4 -> {
                setCancel(cancel)
            }
        }
        binding.imgBack.setOnClickListener {

        }
    }
    private fun setCancel(cancel: Int) {
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
        binding.tvCancel.setTextColor(Color.parseColor("#FF5722"))
        binding.tvCancel.typeface = Typeface.DEFAULT_BOLD
        binding.lineCancel.visibility = View.VISIBLE
    }

    private fun setPack(pack: Int) {
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
        binding.tvCancel.setTextColor(Color.parseColor("#FF000000"))
        binding.tvCancel.typeface = Typeface.DEFAULT
        binding.lineCancel.visibility = View.GONE
    }

    private fun setConfirm(confirm: Int) {
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
        binding.tvCancel.setTextColor(Color.parseColor("#FF000000"))
        binding.tvCancel.typeface = Typeface.DEFAULT
        binding.lineCancel.visibility = View.GONE
    }

    private fun setShipping(shipping: Int) {
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
        binding.tvCancel.setTextColor(Color.parseColor("#FF000000"))
        binding.tvCancel.typeface = Typeface.DEFAULT
        binding.lineCancel.visibility = View.GONE
    }

    private fun setConfirmOrderRecycleView() {
        confirmAdapter = OrderAdapter(this)
        binding.rcvConfirm.apply {
            layoutManager = LinearLayoutManager(this@StatusOrderFragment.context)
            adapter = confirmAdapter
        }
    }

    private fun setPackOrderRecycleView() {
        packAdapter = OrderAdapter(this)
        binding.rcvPack.apply {
            layoutManager = LinearLayoutManager(this@StatusOrderFragment.context)
            adapter = packAdapter
        }
    }

    private fun setShippingOrderRecycleView() {
        shippingAdapter = OrderAdapter(this)
        binding.rcvShipping.apply {
            layoutManager = LinearLayoutManager(this@StatusOrderFragment.context)
            adapter = shippingAdapter
        }
    }
    private fun setCancelOrderRecycleView() {
        cancelAdapter = OrderAdapter(this)
        binding.rcvCancel.apply {
            layoutManager = LinearLayoutManager(this@StatusOrderFragment.context)
            adapter = cancelAdapter
        }
    }

    override fun onClick(order: Order) {

    }
}