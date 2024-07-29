package com.example.mymobileapp.ui.fragment.shopping

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mymobileapp.R
import com.example.mymobileapp.adapter.OrderProductAdapter
import com.example.mymobileapp.databinding.FragmentDetailOrderBinding
import com.example.mymobileapp.helper.Convert
import com.example.mymobileapp.listener.OnClickChoice
import com.example.mymobileapp.model.Order
import com.example.mymobileapp.ui.dialog.ChoiceDialog
import com.example.mymobileapp.util.constants.CONFIRM_STATUS
import com.example.mymobileapp.util.constants.NOT_RATE_STATUS
import com.example.mymobileapp.util.constants.PACKING_STATUS
import com.example.mymobileapp.util.constants.RATE_STATUS
import com.example.mymobileapp.util.constants.SHIPPING_STATUS
import com.example.mymobileapp.viewmodel.OrderViewModel
import dagger.hilt.android.AndroidEntryPoint

@Suppress("DEPRECATION")
@AndroidEntryPoint
class DetailOrderFragment : Fragment() {
    private lateinit var binding: FragmentDetailOrderBinding
    private lateinit var controller: NavController
    private lateinit var order: Order
    private val orderProductAdapter by lazy { OrderProductAdapter() }
    private val orderViewModel by viewModels<OrderViewModel>()

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

        order = requireArguments().getSerializable("Order") as Order

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

        val status = order.status
        when (status) {
            CONFIRM_STATUS -> {
                binding.btnCancel.visibility = View.VISIBLE
                binding.btnProcess.text = "Đơn hàng đang được xử lý"
            }
            PACKING_STATUS -> {
                binding.btnCancel.visibility = View.GONE
                binding.btnProcess.text = "Đơn hàng đang được chuẩn bị"
            }
            SHIPPING_STATUS -> {
                binding.btnCancel.visibility = View.GONE
                binding.btnProcess.text = "Đã nhận được hàng"
            }
            NOT_RATE_STATUS -> {
                binding.btnCancel.visibility = View.GONE
                binding.btnProcess.text = "Đánh giá"
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
                        cancelOrder()
                    }
                }
            })
            cancelDialog.show(requireActivity().supportFragmentManager, null)
        }
        binding.btnProcess.setOnClickListener {
            if (status == SHIPPING_STATUS) {
                orderViewModel.updateReceiveOrder(order)
            } else if (status == NOT_RATE_STATUS) {
                val bundle = Bundle()
                bundle.putSerializable("Order", order)
                controller.navigate(R.id.action_detailOrderFragment_to_rateOrderFragment, bundle)
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
    private fun cancelOrder() {
        //huy don hang
        //update so luong san pham
    }
}