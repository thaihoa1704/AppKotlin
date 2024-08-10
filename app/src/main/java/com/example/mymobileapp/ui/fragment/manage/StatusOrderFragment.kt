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
import com.example.mymobileapp.model.User
import com.example.mymobileapp.ui.fragment.shopping.DetailOrderFragment
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
    private var user = User()
    private var idStatus = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStatusOrderBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val start = requireArguments().getLong("start")
        val end = requireArguments().getLong("end")
        user = requireArguments().getSerializable("user") as User
        idStatus = requireArguments().getInt("id")

        orderViewModel.getConfirmOrderByTime(start, end)
        orderViewModel.getPackOrderByTime(start, end)
        orderViewModel.getShippingOrderByTime(start, end)
        orderViewModel.getCancelOrderByTime(start, end)

        var confirm = 0
        var pack = 0
        var shipping = 0
        var cancel = 0

        lifecycleScope.launchWhenStarted {
            orderViewModel.confirmOrderList.collectLatest {
                when(it){
                    is Resource.Error -> {}
                    is Resource.Loading ->{
                        binding.linearProgress.visibility = View.VISIBLE
                        binding.rcvConfirm.visibility = View.GONE
                    }
                    is Resource.Success -> {
                        binding.linearProgress.visibility = View.INVISIBLE
                        if (idStatus == 1) {
                            binding.rcvConfirm.visibility = View.VISIBLE
                            if (it.data!!.isEmpty()) {
                                confirm = 0
                            }else{
                                confirm = it.data.size
                                confirmAdapter.differ.submitList(it.data)
                            }
                            setConfirm(confirm)
                            idStatus = 0
                        } else {
                            if (it.data!!.isEmpty()) {
                                confirm = 0
                            }else{
                                confirm = it.data.size
                                confirmAdapter.differ.submitList(it.data)
                            }
                        }
                    }
                }
            }
        }
        lifecycleScope.launchWhenStarted {
            orderViewModel.packOrderList.collectLatest {
                when(it){
                    is Resource.Error -> {}
                    is Resource.Loading ->{
                        binding.linearProgress.visibility = View.VISIBLE
                        binding.rcvPack.visibility = View.GONE
                    }
                    is Resource.Success -> {
                        binding.linearProgress.visibility = View.INVISIBLE
                        if (idStatus == 2){
                            binding.rcvPack.visibility = View.VISIBLE
                            if (it.data!!.isEmpty()) {
                                pack = 0
                            }else{
                                pack = it.data.size
                                packAdapter.differ.submitList(it.data)
                            }
                            setPack(pack)
                            idStatus = 0
                        } else {
                            if (it.data!!.isEmpty()) {
                                pack = 0
                            }else {
                                pack = it.data.size
                                packAdapter.differ.submitList(it.data)
                            }
                        }
                    }
                }
            }
        }
        lifecycleScope.launchWhenStarted {
            orderViewModel.shippingOrderList.collectLatest {
                when(it){
                    is Resource.Error -> {}
                    is Resource.Loading ->{
                        binding.linearProgress.visibility = View.VISIBLE
                        binding.rcvShipping.visibility = View.GONE
                    }
                    is Resource.Success -> {
                        binding.linearProgress.visibility = View.INVISIBLE
                        if (idStatus == 3){
                            binding.rcvShipping.visibility = View.VISIBLE
                            if (it.data!!.isEmpty()) {
                                shipping = 0
                            }else {
                                shipping = it.data.size
                                shippingAdapter.differ.submitList(it.data)
                            }
                            setShipping(shipping)
                            idStatus = 0
                        } else {
                            if (it.data!!.isEmpty()) {
                                shipping = 0
                            } else {
                                shipping = it.data.size
                                shippingAdapter.differ.submitList(it.data)
                            }
                        }
                    }
                }
            }
        }
        lifecycleScope.launchWhenStarted {
            orderViewModel.cancelOrderList.collectLatest {
                when(it){
                    is Resource.Error -> {}
                    is Resource.Loading ->{
                        binding.linearProgress.visibility = View.VISIBLE
                        binding.rcvCancel.visibility = View.GONE
                    }
                    is Resource.Success -> {
                        binding.linearProgress.visibility = View.INVISIBLE
                        if (idStatus == 4){
                            binding.rcvCancel.visibility = View.VISIBLE
                            if (it.data!!.isEmpty()) {
                                cancel = 0
                            }else {
                                cancel = it.data.size
                                cancelAdapter.differ.submitList(it.data)
                            }
                            setCancel(cancel)
                            idStatus = 0
                        } else {
                            if (it.data!!.isEmpty()) {
                                cancel = 0
                            } else {
                                cancel = it.data.size
                                cancelAdapter.differ.submitList(it.data)
                            }
                        }
                    }
                }
            }
        }

        setConfirmOrderRecycleView()
        setPackOrderRecycleView()
        setShippingOrderRecycleView()
        setCancelOrderRecycleView()

        binding.tvConfirm.setOnClickListener { setConfirm(confirm) }
        binding.tvPack.setOnClickListener { setPack(pack) }
        binding.tvShipping.setOnClickListener { setShipping(shipping) }
        binding.tvCancel.setOnClickListener { setCancel(cancel) }

        binding.imgBack.setOnClickListener {
            removeFragment()
        }
    }
    private fun setCancel(cancel: Int) {
        idStatus = 4
        if (cancel == 0) {
            binding.tvEmpty.visibility = View.VISIBLE
        }else{
            binding.tvEmpty.visibility = View.GONE
        }
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
        idStatus = 2
        if (pack == 0) {
            binding.tvEmpty.visibility = View.VISIBLE
        } else{
            binding.tvEmpty.visibility = View.GONE
        }
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
        idStatus = 1
        if (confirm == 0) {
            binding.tvEmpty.visibility = View.VISIBLE
        } else{
            binding.tvEmpty.visibility = View.GONE
        }
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
        idStatus = 3
        if (shipping == 0) {
            binding.tvEmpty.visibility = View.VISIBLE
        } else{
            binding.tvEmpty.visibility = View.GONE
        }
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
        confirmAdapter = OrderAdapter("admin",this)
        binding.rcvConfirm.apply {
            layoutManager = LinearLayoutManager(this@StatusOrderFragment.context)
            adapter = confirmAdapter
        }
    }
    private fun setPackOrderRecycleView() {
        packAdapter = OrderAdapter("admin",this)
        binding.rcvPack.apply {
            layoutManager = LinearLayoutManager(this@StatusOrderFragment.context)
            adapter = packAdapter
        }
    }
    private fun setShippingOrderRecycleView() {
        shippingAdapter = OrderAdapter("admin",this)
        binding.rcvShipping.apply {
            layoutManager = LinearLayoutManager(this@StatusOrderFragment.context)
            adapter = shippingAdapter
        }
    }
    private fun setCancelOrderRecycleView() {
        cancelAdapter = OrderAdapter("admin",this)
        binding.rcvCancel.apply {
            layoutManager = LinearLayoutManager(this@StatusOrderFragment.context)
            adapter = cancelAdapter
        }
    }
    override fun onClick(order: Order) {
        addFragment(DetailOrderFragment(), user, order)
    }
    private fun addFragment(fragment: Fragment, user: User, order: Order) {
        val bundle = Bundle()
        bundle.putSerializable("user", user)
        bundle.putSerializable("order", order)
        bundle.putString("type", "admin")
        bundle.putString("from", "deliveredFragment")
        fragment.arguments = bundle
        val fragmentManager = requireActivity().supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.frame_layout_status, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }
    private fun removeFragment() {
        val fragmentManager = requireActivity().supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.remove(this)
        fragmentTransaction.commit()
    }
}