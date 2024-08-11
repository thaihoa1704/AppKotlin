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
import com.example.mymobileapp.databinding.FragmentDetailOrderBinding
import com.example.mymobileapp.helper.Convert
import com.example.mymobileapp.listener.OnClickChoice
import com.example.mymobileapp.model.Order
import com.example.mymobileapp.model.User
import com.example.mymobileapp.ui.dialog.ChoiceDialog
import com.example.mymobileapp.util.Resource
import com.example.mymobileapp.util.constants.CANCEL_STATUS
import com.example.mymobileapp.util.constants.CONFIRM_STATUS
import com.example.mymobileapp.util.constants.NOT_RATE_STATUS
import com.example.mymobileapp.util.constants.PACKING_STATUS
import com.example.mymobileapp.util.constants.RATE_STATUS
import com.example.mymobileapp.util.constants.SHIPPING_STATUS
import com.example.mymobileapp.viewmodel.OrderViewModel
import com.example.mymobileapp.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@Suppress("DEPRECATION")
@AndroidEntryPoint
class DetailOrderFragment : Fragment() {
    private lateinit var binding: FragmentDetailOrderBinding
    private lateinit var controller: NavController
    private lateinit var order: Order
    private val orderProductAdapter by lazy { OrderProductAdapter() }
    private val orderViewModel by viewModels<OrderViewModel>()
    private lateinit var user: User
    private var from = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDetailOrderBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        controller = Navigation.findNavController(view)

        order = requireArguments().getSerializable("order") as Order
        user = requireArguments().getSerializable("user") as User
        from = requireArguments().getString("from").toString()

        if (from == "detailAccountFragment" &&
            order.status == CONFIRM_STATUS||
            order.status == PACKING_STATUS||
            order.status == SHIPPING_STATUS||
            order.status == NOT_RATE_STATUS)
        {
            binding.btnCancel.isEnabled = false
            binding.btnProcess.isEnabled = false
        }

        binding.tvContact.text = order.contact
        binding.tvAddress.text = order.address
        binding.tvTime.text = Convert.getDateTime(order.dateTime)

        setupOrderProductRecyclerView()
        orderProductAdapter.differ.submitList(order.listProduct)

        var price = 0
        for (item in order.listProduct) {
            price += item.quantity * item.version.price
        }
        binding.tvPrice.text = Convert.DinhDangTien(price) + " đ"
        binding.tvTotal.text = Convert.DinhDangTien(order.total) + " đ"

        binding.imgDropDown.setOnClickListener {
            binding.tvLabel.visibility = View.VISIBLE
            binding.tvLabel1.visibility = View.VISIBLE
            binding.tvPrice.visibility = View.VISIBLE
            binding.tvPriceShipping.visibility = View.VISIBLE
            binding.constraintLayout2.visibility = View.GONE
        }

        lifecycleScope.launchWhenStarted {
            orderViewModel.message.collectLatest {
                when(it){
                    is Resource.Error -> {}
                    is Resource.Loading -> {}
                    is Resource.Success -> {
                        Toast.makeText(requireContext(), it.data, Toast.LENGTH_SHORT).show()
                        Handler().postDelayed({
                            if (user.type == "admin") {
                                removeFragment()
                            } else {
                                controller.popBackStack()
                            }
                        }, 3000)
                    }
                }
            }
        }

        val status = order.status
        when (status) {
            CONFIRM_STATUS -> {
                if (user.type == "customer") {
                    binding.btnCancel.visibility = View.VISIBLE
                    binding.btnProcess.text = "Đơn hàng đang được xử lý"
                } else {
                    binding.btnCancel.visibility = View.GONE
                    binding.btnProcess.text = "Xác nhận đơn hàng và chuẩn bị đơn"
                }
            }
            PACKING_STATUS -> {
                if (user.type == "customer") {
                    binding.btnCancel.visibility = View.GONE
                    binding.btnProcess.text = "Đơn hàng đang được chuẩn bị"
                } else {
                    binding.btnCancel.visibility = View.GONE
                    binding.btnProcess.isEnabled = true
                    binding.btnProcess.text = "Xác nhận giao đơn hàng"
                }
            }
            SHIPPING_STATUS -> {
                if (user.type == "customer") {
                    if (from == "detailAccountFragment") {
                        binding.btnProcess.isEnabled = false
                        binding.btnCancel.visibility = View.GONE
                        binding.btnProcess.text = SHIPPING_STATUS
                    } else {
                        binding.btnCancel.visibility = View.GONE
                        binding.btnProcess.isEnabled = true
                        binding.btnProcess.text = "Đã nhận được hàng"
                    }
                } else {
                    binding.btnCancel.visibility = View.GONE
                    binding.btnProcess.text = SHIPPING_STATUS
                    binding.btnProcess.isEnabled = false
                }
            }
            NOT_RATE_STATUS -> {
                if (user.type == "customer") {
                    binding.btnCancel.visibility = View.GONE
                    binding.btnProcess.isEnabled = true
                    binding.btnProcess.text = "Đánh giá"
                }else{
                    binding.btnCancel.visibility = View.GONE
                    binding.btnProcess.text = NOT_RATE_STATUS
                }
            }
            RATE_STATUS -> {
                binding.btnCancel.visibility = View.GONE
                binding.btnProcess.text = "Xem đánh giá"
            }
            CANCEL_STATUS -> {
                binding.btnCancel.visibility = View.GONE
                binding.btnProcess.text = "Đơn hàng đã bị huỷ"
            }
        }

        binding.btnCancel.setOnClickListener {
            val cancelDialog = ChoiceDialog("detailOrderFragment", object : OnClickChoice {
                override fun onClick(choice: Boolean?) {
                    if (choice == true) {
                        cancelOrder(order)
                    }
                }
            })
            cancelDialog.show(requireActivity().supportFragmentManager, null)
        }
        binding.btnProcess.setOnClickListener {
            if (status == CONFIRM_STATUS) {
                if (user.type == "customer") {
                    //DO NOTHING
                }else{
                    Handler().postDelayed({
                        removeFragment()
                    }, 3000)
                    orderViewModel.updateOrder(order, PACKING_STATUS)
                }
            }else if (status == PACKING_STATUS) {
                if (user.type == "customer") {
                    //DO NOTHING
                } else {
                    orderViewModel.updateOrder(order, SHIPPING_STATUS)
                    Handler().postDelayed({
                        removeFragment()
                    }, 3000)
                }
            } else if (status == SHIPPING_STATUS) {
                if (from == "detailAccountFragment") {
                    //DO NOTHING
                } else {
                    if (user.type == "customer") {
                        orderViewModel.updateOrder(order, NOT_RATE_STATUS)
                        Handler().postDelayed({
                            controller.popBackStack()
                        }, 3000)
                    } else {
                        //DO NOTHING
                    }
                }
            } else if (status == NOT_RATE_STATUS) {
                if (user.type == "customer") {
                    val bundle = Bundle()
                    bundle.putSerializable("order", order)
                    controller.navigate(R.id.action_detailOrderFragment_to_rateOrderFragment, bundle)
                } else {
                    //DO NOTHING
                }
            } else if (status == RATE_STATUS) {
                if (from == "detailAccountFragment") {
                    val bundle = Bundle()
                    bundle.putSerializable("order", order)
                    bundle.putString("rate", RATE_STATUS)
                    controller.navigate(R.id.action_detailOrderFragment2_to_rateOrderFragment2, bundle)
                } else {
                    if (user.type == "customer") {
                        val bundle = Bundle()
                        bundle.putSerializable("order", order)
                        bundle.putString("rate", RATE_STATUS)
                        controller.navigate(R.id.action_detailOrderFragment_to_rateOrderFragment, bundle)
                    } else {
                        addFragment(RateOrderFragment(), order, RATE_STATUS)
                    }
                }
            } else if (status == CANCEL_STATUS) {
                //DO NOTHING
            }
        }
        binding.imgBack.setOnClickListener{
            if (user.type == "admin") {
                removeFragment()
            } else {
                controller.popBackStack()
            }
        }

    }
    private fun setupOrderProductRecyclerView() {
        binding.rcvOrder.apply {
            layoutManager = LinearLayoutManager(this@DetailOrderFragment.context)
            adapter = orderProductAdapter
        }
    }
    private fun cancelOrder(order: Order) {
        orderViewModel.updateCancelOrder(order)
    }
    private fun removeFragment() {
        val fragmentManager = requireActivity().supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.remove(this)
        fragmentTransaction.commit()
    }
    private fun addFragment(fragment: Fragment, order: Order, status: String) {
        val bundle = Bundle()
        bundle.putSerializable("order", order)
        bundle.putString("rate", status)
        bundle.putString("type", "admin")
        fragment.arguments = bundle
        val fragmentManager = requireActivity().supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.frame_layout_delivered, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }
}