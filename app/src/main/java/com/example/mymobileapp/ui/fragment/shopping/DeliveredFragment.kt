package com.example.mymobileapp.ui.fragment.shopping

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
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mymobileapp.R
import com.example.mymobileapp.adapter.OrderAdapter
import com.example.mymobileapp.databinding.FragmentDeliveredBinding
import com.example.mymobileapp.listener.ClickItemOrderListener
import com.example.mymobileapp.model.Order
import com.example.mymobileapp.util.Resource
import com.example.mymobileapp.viewmodel.OrderViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@Suppress("DEPRECATION")
@AndroidEntryPoint
class DeliveredFragment : Fragment(), ClickItemOrderListener {
    private lateinit var binding: FragmentDeliveredBinding
    private lateinit var controller: NavController
    private val orderViewModel by viewModels<OrderViewModel>()
    private lateinit var rateAdapter: OrderAdapter
    private lateinit var notRateAdapter: OrderAdapter
    private var notRate = -1
    private var rate = -1
    private var id = 1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDeliveredBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        controller = Navigation.findNavController(view)

        orderViewModel.getRateOrder()
        orderViewModel.getNotRateOrder()

        setRateOrderRecycleView()
        setNotRateOrderRecycleView()

        lifecycleScope.launchWhenStarted {
            orderViewModel.rateOrderList.collectLatest {
                when(it){
                    is Resource.Error -> {
                        Toast.makeText(requireContext(), "Lỗi lấy dữ liệu", Toast.LENGTH_SHORT).show()
                    }
                    is Resource.Loading ->{}
                    is Resource.Success -> {
                        if (it.data!!.isEmpty()) {
                            rate = 0
                        }else{
                            rate = 1
                            rateAdapter.differ.submitList(it.data)
                        }
                    }
                }
            }
        }
        lifecycleScope.launchWhenStarted {
            orderViewModel.notRateOrderList.collectLatest {
                when(it){
                    is Resource.Error -> {
                        Toast.makeText(requireContext(), "Lỗi lấy dữ liệu", Toast.LENGTH_SHORT).show()
                    }
                    is Resource.Loading ->{}
                    is Resource.Success -> {
                        if (it.data!!.isEmpty()) {
                            notRate = 0
                        }else{
                            notRate = 1
                            notRateAdapter.differ.submitList(it.data)
                        }
                    }
                }
            }
        }

        binding.tvNotRate.setOnClickListener { notRate() }
        binding.tvRate.setOnClickListener { rate() }

        if (arguments != null) {
            id = requireArguments().getInt("id")
        }
        when (id) {
            1 -> {
                notRate()
            }
            2 -> {
                rate()
            }
        }

        binding.imgBack.setOnClickListener {
            controller.popBackStack()
        }

    }
    private fun notRate(){
        if (notRate == 0) {
            binding.tvEmpty1.visibility = View.VISIBLE
            binding.rcvRate.visibility = View.GONE
        } else{
            binding.tvEmpty1.visibility = View.GONE
            binding.rcvNotRate.visibility = View.VISIBLE
        }
        binding.rcvRate.visibility = View.GONE
        binding.tvEmpty2.visibility = View.GONE
        binding.tvNotRate.setTextColor(Color.parseColor("#FF5722"))
        binding.tvNotRate.typeface = Typeface.DEFAULT_BOLD
        binding.lineNotRate.visibility = View.VISIBLE
        binding.tvRate.setTextColor(Color.parseColor("#FF000000"))
        binding.tvRate.typeface = Typeface.DEFAULT
        binding.lineRate.visibility = View.GONE
    }
    private fun rate(){
        if (rate == 0) {
            binding.tvEmpty2.visibility = View.VISIBLE
            binding.rcvRate.visibility = View.GONE
        } else{
            binding.tvEmpty2.visibility = View.GONE
            binding.rcvRate.visibility = View.VISIBLE
        }
        binding.rcvNotRate.visibility = View.GONE
        binding.tvEmpty1.visibility = View.GONE
        binding.tvRate.setTextColor(Color.parseColor("#FF5722"))
        binding.tvRate.typeface = Typeface.DEFAULT_BOLD
        binding.lineRate.visibility = View.VISIBLE
        binding.tvNotRate.setTextColor(Color.parseColor("#FF000000"))
        binding.tvNotRate.typeface = Typeface.DEFAULT
        binding.lineNotRate.visibility = View.GONE
    }

    private fun setNotRateOrderRecycleView() {
        rateAdapter = OrderAdapter(this)
        binding.rcvRate.apply {
            layoutManager = LinearLayoutManager(this@DeliveredFragment.context)
            adapter = rateAdapter
        }
    }

    private fun setRateOrderRecycleView() {
        notRateAdapter = OrderAdapter(this)
        binding.rcvNotRate.apply {
            layoutManager = LinearLayoutManager(this@DeliveredFragment.context)
            adapter = notRateAdapter
        }
    }

    override fun onClick(order: Order) {
        val bundle = Bundle()
        bundle.putSerializable("Order", order)
        bundle.putString("From", "DeliveredFragment")
        controller.navigate(R.id.action_deliveredFragment_to_detailOrderFragment, bundle)
    }
}