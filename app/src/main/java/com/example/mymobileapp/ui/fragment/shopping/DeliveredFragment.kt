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
import com.example.mymobileapp.model.User
import com.example.mymobileapp.util.Resource
import com.example.mymobileapp.viewmodel.OrderViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@Suppress("DEPRECATION")
@AndroidEntryPoint
class DeliveredFragment : Fragment(), ClickItemOrderListener {
    private lateinit var binding: FragmentDeliveredBinding
    private lateinit var controller: NavController
    private lateinit var rateAdapter: OrderAdapter
    private lateinit var notRateAdapter: OrderAdapter
    private val orderViewModel by viewModels<OrderViewModel>()
    private var from = ""
    private var user = User()
    private var idStatus = 0

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

        //type = requireArguments().getString("type").toString()
        from = requireArguments().getString("from").toString()
        val start = requireArguments().getLong("start")
        val end = requireArguments().getLong("end")
        user = requireArguments().getSerializable("user") as User
        idStatus = requireArguments().getInt("id")

        if(user.type == "admin"){
            orderViewModel.getRateOrderByTime(start, end)
            orderViewModel.getNotRateOrderByTime(start, end)
        }
        if (user.type == "customer"){
            orderViewModel.getRateOrder(user.id)
            orderViewModel.getNotRateOrder(user.id)
        }

        setRateOrderRecycleView()
        setNotRateOrderRecycleView()

        var rate = 0
        lifecycleScope.launchWhenStarted {
            orderViewModel.rateOrderList.collectLatest {
                when(it){
                    is Resource.Error -> {}
                    is Resource.Loading ->{
                        binding.linearProgress.visibility = View.VISIBLE
                        binding.rcvRate.visibility = View.GONE
                    }
                    is Resource.Success -> {
                        binding.linearProgress.visibility = View.INVISIBLE
                        if (idStatus == 2){
                            binding.rcvRate.visibility = View.VISIBLE
                            if (it.data!!.isEmpty()) {
                                rate = 0
                            }else{
                                rate = 1
                                rateAdapter.differ.submitList(it.data)
                            }
                            setRate(rate)
                            idStatus = 0
                        }else{
                            if (it.data!!.isEmpty()) {
                                rate = 0
                            }else {
                                rate = 1
                                rateAdapter.differ.submitList(it.data)
                            }
                        }
                    }
                }
            }
        }

        var notRate = 0
        lifecycleScope.launchWhenStarted {
            orderViewModel.notRateOrderList.collectLatest {
                when(it){
                    is Resource.Error -> {}
                    is Resource.Loading ->{
                        binding.linearProgress.visibility = View.VISIBLE
                        binding.rcvNotRate.visibility = View.GONE
                    }
                    is Resource.Success -> {
                        binding.linearProgress.visibility = View.INVISIBLE
                        if (idStatus == 1){
                            binding.rcvNotRate.visibility = View.VISIBLE
                            if (it.data!!.isEmpty()) {
                                notRate = 0
                            }else{
                                notRate = 1
                                notRateAdapter.differ.submitList(it.data)
                            }
                            setNotRate(notRate)
                            idStatus = 0
                        } else{
                            if (it.data!!.isEmpty()) {
                                notRate = 0
                            }else {
                                notRate = 1
                                notRateAdapter.differ.submitList(it.data)
                            }
                        }
                    }
                }
            }
        }

        binding.tvNotRate.setOnClickListener { setNotRate(notRate) }
        binding.tvRate.setOnClickListener { setRate(rate) }

        binding.imgBack.setOnClickListener {
            if (user.type == "admin") {
                removeFragment()
            } else {
                controller.popBackStack()
            }
        }
    }
    private fun setNotRate(notRate: Int){
        idStatus = 1
        if (notRate == 0) {
            binding.tvEmpty.visibility = View.VISIBLE
        } else{
            binding.tvEmpty.visibility = View.GONE
        }
        binding.rcvNotRate.visibility = View.VISIBLE
        binding.rcvRate.visibility = View.GONE
        binding.tvNotRate.setTextColor(Color.parseColor("#FF5722"))
        binding.tvNotRate.typeface = Typeface.DEFAULT_BOLD
        binding.lineNotRate.visibility = View.VISIBLE
        binding.tvRate.setTextColor(Color.parseColor("#FF000000"))
        binding.tvRate.typeface = Typeface.DEFAULT
        binding.lineRate.visibility = View.GONE
    }
    private fun setRate(rate: Int){
        idStatus = 2
        if (rate == 0) {
            binding.tvEmpty.visibility = View.VISIBLE
        } else{
            binding.tvEmpty.visibility = View.GONE
        }
        binding.rcvRate.visibility = View.VISIBLE
        binding.rcvNotRate.visibility = View.GONE
        binding.tvRate.setTextColor(Color.parseColor("#FF5722"))
        binding.tvRate.typeface = Typeface.DEFAULT_BOLD
        binding.lineRate.visibility = View.VISIBLE
        binding.tvNotRate.setTextColor(Color.parseColor("#FF000000"))
        binding.tvNotRate.typeface = Typeface.DEFAULT
        binding.lineNotRate.visibility = View.GONE
    }

    private fun setNotRateOrderRecycleView() {
        if (from == "orderListFragment"){
            rateAdapter = OrderAdapter("admin",this)
        } else{
            rateAdapter = OrderAdapter("customer",this)
        }
        binding.rcvRate.apply {
            layoutManager = LinearLayoutManager(this@DeliveredFragment.context)
            adapter = rateAdapter
        }
    }

    private fun setRateOrderRecycleView() {
        if (from == "orderListFragment"){
            notRateAdapter = OrderAdapter("admin",this)
        } else{
            notRateAdapter = OrderAdapter("customer",this)
        }
        binding.rcvNotRate.apply {
            layoutManager = LinearLayoutManager(this@DeliveredFragment.context)
            adapter = notRateAdapter
        }
    }

    override fun onClick(order: Order) {
        if (user.type == "admin") {
            addFragment(DetailOrderFragment(), user, order)
        } else {
            val bundle = Bundle()
            bundle.putSerializable("order", order)
            bundle.putSerializable("user", user)
            bundle.putString("from", "deliveredFragment")
            controller.navigate(R.id.action_deliveredFragment_to_detailOrderFragment, bundle)
        }
    }
    private fun removeFragment() {
        val fragmentManager = requireActivity().supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.remove(this)
        fragmentTransaction.commit()
    }
    private fun addFragment(fragment: Fragment, user: User, order: Order) {
        val bundle = Bundle()
        bundle.putSerializable("user", user)
        bundle.putSerializable("order", order)
        bundle.putString("type", "admin")
        bundle.putString("from", from)
        fragment.arguments = bundle
        val fragmentManager = requireActivity().supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.frame_layout_delivered, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }
}