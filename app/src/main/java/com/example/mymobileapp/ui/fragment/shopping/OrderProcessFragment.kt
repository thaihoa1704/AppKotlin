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
import com.example.mymobileapp.model.User
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
    private var user = User()
    private var from = ""
    private var idStatus = 0

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

        user = requireArguments().getSerializable("user") as User
        from = requireArguments().getString("from").toString()
        idStatus = requireArguments().getInt("id")

        if (from == "detailAccountFragment") {
            binding.tvTitle.text = "Lịch sử mua hàng"
            binding.tvQuantity.visibility = View.VISIBLE
            idStatus = 4
        } else if(from == "userFragment"){
            binding.tvTitle.text = "Lịch sử mua hàng"
        }

        orderViewModel.getConfirmOrder(user.id)
        orderViewModel.getPackOrder(user.id)
        orderViewModel.getShippingOrder(user.id)
        orderViewModel.getCompleteOrder(user.id)
        orderViewModel.getCancelOrder(user.id)

        var confirm = 0
        var pack = 0
        var shipping = 0
        var complete = 0
        var cancel = 0

        lifecycleScope.launchWhenStarted {
            orderViewModel.confirmOrderList.collectLatest {
                when(it){
                    is Resource.Error -> {
                        //Toast.makeText(requireContext(), "Lỗi lấy dữ liệu", Toast.LENGTH_SHORT).show()
                    }
                    is Resource.Loading ->{
                        binding.linearProgress.visibility = View.VISIBLE
                    }
                    is Resource.Success -> {
                        binding.linearProgress.visibility = View.GONE
                        if (idStatus == 1) {
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
                    is Resource.Error -> {
                        //Toast.makeText(requireContext(), "Lỗi lấy dữ liệu", Toast.LENGTH_SHORT).show()
                    }
                    is Resource.Loading ->{
                        binding.linearProgress.visibility = View.VISIBLE
                    }
                    is Resource.Success -> {
                        binding.linearProgress.visibility = View.GONE
                        if (idStatus == 2){
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
                    is Resource.Error -> {
                        //Toast.makeText(requireContext(), "Lỗi lấy dữ liệu", Toast.LENGTH_SHORT).show()
                    }
                    is Resource.Loading ->{
                        binding.linearProgress.visibility = View.VISIBLE
                    }
                    is Resource.Success -> {
                        binding.linearProgress.visibility = View.GONE
                        if (idStatus == 3){
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
            orderViewModel.completeOrderList.collectLatest {
                when(it){
                    is Resource.Error -> {
                        //Toast.makeText(requireContext(), "Lỗi lấy dữ liệu", Toast.LENGTH_SHORT).show()
                    }
                    is Resource.Loading ->{
                        binding.linearProgress.visibility = View.VISIBLE
                    }
                    is Resource.Success -> {
                        binding.linearProgress.visibility = View.GONE
                        if (idStatus == 4) {
                            if (it.data!!.isEmpty()) {
                                complete = 0
                            } else {
                                complete = it.data.size
                                completeAdapter.differ.submitList(it.data)
                            }
                            setDelivered(complete)
                            idStatus = 0
                        } else {
                            if (it.data!!.isEmpty()) {
                                complete = 0
                            } else {
                                complete = it.data.size
                                completeAdapter.differ.submitList(it.data)
                            }
                        }
                    }
                }
            }
        }
        lifecycleScope.launchWhenStarted {
            orderViewModel.cancelOrderList.collectLatest {
                when(it){
                    is Resource.Error -> {
                        //Toast.makeText(requireContext(), "Lỗi lấy dữ liệu", Toast.LENGTH_SHORT).show()
                    }
                    is Resource.Loading ->{
                        binding.linearProgress.visibility = View.VISIBLE
                    }
                    is Resource.Success -> {
                        binding.linearProgress.visibility = View.GONE
                        if (it.data!!.isEmpty()) {
                            cancel = 0
                        }else{
                            cancel = it.data.size
                            cancelAdapter.differ.submitList(it.data)
                        }
                    }
                }
            }
        }
        setConfirmOrderRecycleView()
        setPackOrderRecycleView()
        setShippingOrderRecycleView()
        setCompleteOrderRecycleView()
        setCancelOrderRecycleView()

        binding.tvConfirm.setOnClickListener { setConfirm(confirm) }
        binding.tvPack.setOnClickListener { setPack(pack) }
        binding.tvShipping.setOnClickListener { setShipping(shipping) }
        binding.tvDelivered.setOnClickListener { setDelivered(complete) }
        binding.tvCancel.setOnClickListener { setCancel(cancel) }

        binding.imgBack.setOnClickListener {
            controller.popBackStack()
        }
    }

    private fun setCancel(cancel: Int) {
        if (cancel == 0) {
            binding.tvEmpty.visibility = View.VISIBLE
            binding.tvQuantity.visibility = View.GONE
        }else{
            if (from == "userFragment"){
                binding.tvEmpty.visibility = View.GONE
                binding.tvQuantity.visibility = View.GONE
            } else {
                binding.tvQuantity.text = "Số lượng: $cancel"
                binding.tvQuantity.visibility = View.VISIBLE
                binding.tvEmpty.visibility = View.GONE
            }
        }
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

    private fun setPack(pack: Int) {
        if (pack == 0) {
            binding.tvEmpty.visibility = View.VISIBLE
            binding.tvQuantity.visibility = View.GONE
        } else{
            if (from == "userFragment"){
                binding.tvEmpty.visibility = View.GONE
                binding.tvQuantity.visibility = View.GONE
            } else {
                binding.tvEmpty.visibility = View.GONE
                binding.tvQuantity.text = "Số lượng: $pack"
                binding.tvQuantity.visibility = View.VISIBLE
            }
        }
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

    private fun setConfirm(confirm: Int) {
        if (confirm == 0) {
            binding.tvEmpty.visibility = View.VISIBLE
            binding.tvQuantity.visibility = View.GONE
        } else{
            if (from == "userFragment"){
                binding.tvEmpty.visibility = View.GONE
                binding.tvQuantity.visibility = View.GONE
            } else {
                binding.tvEmpty.visibility = View.GONE
                binding.tvQuantity.text = "Số lượng: $confirm"
                binding.tvQuantity.visibility = View.VISIBLE
            }
        }
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

    private fun setShipping(shipping: Int) {
        if (shipping == 0) {
            binding.tvEmpty.visibility = View.VISIBLE
            binding.tvQuantity.visibility = View.GONE
        } else{
            if (from == "userFragment"){
                binding.tvEmpty.visibility = View.GONE
                binding.tvQuantity.visibility = View.GONE
            } else {
                binding.tvEmpty.visibility = View.GONE
                binding.tvQuantity.text = "Số lượng: $shipping"
                binding.tvQuantity.visibility = View.VISIBLE
            }
        }
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

    private fun setDelivered(complete: Int) {
        if (complete == 0) {
            binding.tvEmpty.visibility = View.VISIBLE
            binding.tvQuantity.visibility = View.GONE
        } else{
            if (from == "userFragment"){
                binding.tvEmpty.visibility = View.GONE
                binding.tvQuantity.visibility = View.GONE
            } else {
                binding.tvEmpty.visibility = View.GONE
                binding.tvQuantity.text = "Số lượng: $complete"
                binding.tvQuantity.visibility = View.VISIBLE
            }
        }
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
        confirmAdapter = OrderAdapter("customer", this)
        binding.rcvConfirm.apply {
            layoutManager = LinearLayoutManager(this@OrderProcessFragment.context)
            adapter = confirmAdapter
        }
    }

    private fun setPackOrderRecycleView() {
        packAdapter = OrderAdapter("customer",this)
        binding.rcvPack.apply {
            layoutManager = LinearLayoutManager(this@OrderProcessFragment.context)
            adapter = packAdapter
        }
    }

    private fun setShippingOrderRecycleView() {
        shippingAdapter = OrderAdapter("customer",this)
        binding.rcvShipping.apply {
            layoutManager = LinearLayoutManager(this@OrderProcessFragment.context)
            adapter = shippingAdapter
        }
    }

    private fun setCompleteOrderRecycleView() {
        completeAdapter = OrderAdapter("customer",this)
        binding.rcvComplete.apply {
            layoutManager = LinearLayoutManager(this@OrderProcessFragment.context)
            adapter = completeAdapter
        }
    }
    private fun setCancelOrderRecycleView() {
        cancelAdapter = OrderAdapter("customer",this)
        binding.rcvCancel.apply {
            layoutManager = LinearLayoutManager(this@OrderProcessFragment.context)
            adapter = cancelAdapter
        }
    }
    override fun onClick(order: Order) {
        if (from == "detailAccountFragment"){
            val bundle = Bundle()
            bundle.putSerializable("order", order)
            bundle.putSerializable("user", user)
            bundle.putString("from", "detailAccountFragment")
            controller.navigate(R.id.action_orderProcessFragment2_to_detailOrderFragment2, bundle)
        } else {
            val bundle = Bundle()
            bundle.putSerializable("order", order)
            bundle.putSerializable("user", user)
            controller.navigate(R.id.action_orderProcessFragment_to_detailOrderFragment, bundle)
        }
    }
}