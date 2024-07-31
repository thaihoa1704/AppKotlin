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
                    is Resource.Error -> {

                    }
                    is Resource.Loading -> {}
                    is Resource.Success -> {
                        Toast.makeText(requireContext(), it.data, Toast.LENGTH_SHORT).show()
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
                    binding.btnProcess.text = "Xác nhận giao đơn hàng"
                }
            }
            SHIPPING_STATUS -> {
                if (user.type == "customer") {
                    binding.btnCancel.visibility = View.GONE
                    binding.btnProcess.text = "Đã nhận được hàng"
                } else {
                    binding.btnCancel.visibility = View.GONE
                    binding.btnProcess.text = SHIPPING_STATUS
                }
            }
            NOT_RATE_STATUS -> {
                if (user.type == "customer") {
                    binding.btnCancel.visibility = View.GONE
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
        }

        binding.btnCancel.setOnClickListener {
            val cancelDialog = ChoiceDialog("DetailOrderFragment", object : OnClickChoice {
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
                    orderViewModel.updateOrder(order, PACKING_STATUS)
                }
            }else if (status == PACKING_STATUS) {
                if (user.type == "customer") {
                    //DO NOTHING
                } else {
                    orderViewModel.updateOrder(order, SHIPPING_STATUS)
                }
            } else if (status == SHIPPING_STATUS) {
                if (user.type == "customer") {
                    orderViewModel.updateOrder(order, NOT_RATE_STATUS)
                }else {
                    //DO NOTHING
                }
            } else if (status == NOT_RATE_STATUS) {
                if (user.type == "customer") {
                    val bundle = Bundle()
                    bundle.putSerializable("Order", order)
                    controller.navigate(R.id.action_detailOrderFragment_to_rateOrderFragment, bundle)
                } else {
                    //DO NOTHING
                }
            } else if (status == RATE_STATUS) {
                val bundle = Bundle()
                bundle.putSerializable("Order", order)
                bundle.putString("rate", RATE_STATUS)
                controller.navigate(R.id.action_detailOrderFragment_to_rateOrderFragment, bundle)
            }
        }
        binding.imgBack.setOnClickListener{
            controller.popBackStack()
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
}