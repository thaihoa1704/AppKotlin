package com.example.mymobileapp.ui.fragment.manage

import android.app.DatePickerDialog
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.mymobileapp.R
import com.example.mymobileapp.databinding.FragmentOrderListBinding
import com.example.mymobileapp.model.Product
import com.example.mymobileapp.model.User
import com.example.mymobileapp.ui.fragment.shopping.DeliveredFragment
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
import java.text.SimpleDateFormat
import java.util.Locale

@Suppress("DEPRECATION", "UNREACHABLE_CODE")
@AndroidEntryPoint
class OrderListFragment : Fragment() {
    private lateinit var binding: FragmentOrderListBinding
    private lateinit var controller: NavController
    private val orderViewModel by viewModels<OrderViewModel>()
    private val userViewModel by viewModels<UserViewModel>()
    private val dateFormat1 = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault())
    private var dateString: String = ""
    private var dateString1: String = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentOrderListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        controller = Navigation.findNavController(view)

//        if (savedInstanceState != null) {
//            binding.tvDate.text = savedInstanceState.getString("start")
//            binding.tvDate1.text = savedInstanceState.getString("end")
//            binding.layoutSearch.visibility = View.VISIBLE
//        }

        var user = User()
        lifecycleScope.launchWhenStarted {
            userViewModel.user.collectLatest {
                when (it) {
                    is Resource.Error -> {}
                    is Resource.Loading -> {}
                    is Resource.Success ->{
                        user = it.data!!
                    }
                }
            }
        }

        orderViewModel.getAll()
        lifecycleScope.launchWhenStarted {
            orderViewModel.time.collectLatest {
                when(it){
                    is Resource.Error -> {
                        Toast.makeText(requireContext(), "Lỗi lấy dữ liệu", Toast.LENGTH_SHORT).show()
                    }
                    is Resource.Loading ->{}
                    is Resource.Success -> {
                        if (it.data!!.isEmpty()) {
                            binding.tvTotal.text = "Tổng số đơn hàng: 0"
                            setQuantity(0, 0, 0, 0, 0, 0)
                        }else{
                            var confirm = 0
                            var pack = 0
                            var shipping = 0
                            var notRate = 0
                            var rate = 0
                            var cancel = 0
                            binding.tvTotal.text = "Tổng số đơn hàng: " + it.data.size.toString()
                            for (i in it.data) {
                                when (i.status) {
                                    CONFIRM_STATUS -> { confirm++ }
                                    PACKING_STATUS -> { pack++ }
                                    SHIPPING_STATUS -> { shipping++ }
                                    NOT_RATE_STATUS -> { notRate++ }
                                    RATE_STATUS -> { rate++ }
                                    CANCEL_STATUS -> { cancel++ }
                                }
                                setQuantity(confirm, pack, shipping, notRate, rate, cancel)
                            }
                        }
                    }
                }
            }
        }

        binding.tvDate.addTextChangedListener(textWatcher)
        binding.tvDate1.addTextChangedListener(textWatcher)

        binding.btnSearch.setOnClickListener {
            binding.layoutSearch.visibility = View.VISIBLE
            binding.btnSearch.visibility = View.GONE
            binding.btnFind.visibility = View.VISIBLE
            binding.btnFind.isEnabled = true
        }
        binding.imgFrom.setOnClickListener {
            DatePickerDialog(requireContext(), { _, year, month, day ->
                binding.tvDate.text = "$day-${month + 1}-$year"
                dateString = "$day-${month + 1}-$year 00:00:00"
            }, 2024, 6, 29).show()
        }
        binding.imgTo.setOnClickListener {
            DatePickerDialog(requireContext(), { _, year, month, day ->
                binding.tvDate1.text = "$day-${month + 1}-$year"
                dateString1 = "$day-${month + 1}-$year 23:59:59"
            }, 2024, 6, 29).show()
        }
//        dateString = binding.tvDate.text.toString().trim() + " 00:00:00"
//        dateString1 = binding.tvDate1.text.toString().trim() + " 23:59:59"

        var start: Long = 0
        var end: Long = 0
        binding.btnFind.setOnClickListener {
            start = dateFormat1.parse(dateString)!!.time
            end = dateFormat1.parse(dateString1)!!.time
            orderViewModel.getOrderListByTime(start, end)
        }
        binding.btnAll.setOnClickListener {
            orderViewModel.getAll()
            binding.btnSearch.visibility = View.VISIBLE
            binding.layoutSearch.visibility = View.GONE
            binding.btnFind.visibility = View.GONE
        }
        binding.layoutConfirm.setOnClickListener {
            if (binding.tvQuantity.text.toString() == "0"){
                Toast.makeText(requireContext(), "Không có đơn hàng nào", Toast.LENGTH_SHORT).show()
            } else{
                moveToNewFragment(user, start, end)
            }
        }
        binding.layoutPack.setOnClickListener {
            if (binding.tvQuantity.text.toString() == "0"){
                Toast.makeText(requireContext(), "Không có đơn hàng nào", Toast.LENGTH_SHORT).show()
            } else{
                moveToNewFragment(user, start, end)
            }
        }
        binding.layoutShipping.setOnClickListener {
            if (binding.tvQuantity.text.toString() == "0"){
                Toast.makeText(requireContext(), "Không có đơn hàng nào", Toast.LENGTH_SHORT).show()
            } else{
                moveToNewFragment(user, start, end)
            }
        }
        binding.layoutNotRate.setOnClickListener {
            if (binding.tvQuantity.text.toString() == "0"){
                Toast.makeText(requireContext(), "Không có đơn hàng nào", Toast.LENGTH_SHORT).show()
            } else{
                addFragment(DeliveredFragment(), user, start, end, 1)
            }
        }
        binding.layoutRate.setOnClickListener {
            if (binding.tvQuantity.text.toString() == "0"){
                Toast.makeText(requireContext(), "Không có đơn hàng nào", Toast.LENGTH_SHORT).show()
            } else{
                addFragment(DeliveredFragment(), user, start, end, 2)
            }
        }
        binding.layoutCancel.setOnClickListener {
            if (binding.tvQuantity.text.toString() == "0"){
                Toast.makeText(requireContext(), "Không có đơn hàng nào", Toast.LENGTH_SHORT).show()
            } else{
                moveToNewFragment(user, start, end)
            }
        }

        binding.imgBack.setOnClickListener {
            controller.popBackStack()
        }
    }

    private fun moveToNewFragment(user: User, start: Long, end: Long) {
        val bundle = Bundle()
        bundle.putSerializable("user", user)
        bundle.putString("type", "admin")
        bundle.putString("from", "orderListFragment")
        bundle.putLong("start", start)
        bundle.putLong("end", end)
        controller.navigate(R.id.action_orderListFragment_to_statusOrderFragment, bundle)
    }
    private fun moveToCompleteFragment(user: User, start: Long, end: Long) {
        val bundle = Bundle()
        bundle.putSerializable("user", user)
        bundle.putString("type", "admin")
        bundle.putString("from", "orderListFragment")
        bundle.putLong("start", start)
        bundle.putLong("end", end)
        controller.navigate(R.id.action_orderListFragment_to_deliveredFragment2, bundle)
    }

    private fun setQuantity(confirm: Int, pack: Int, shipping: Int, notRate: Int, rate: Int, cancel: Int) {
        binding.apply {
            tvConfirm.text = confirm.toString()
            tvPack.text = pack.toString()
            tvShipping.text = shipping.toString()
            tvNotRate.text = notRate.toString()
            tvRate.text = rate.toString()
            tvCancel.text = cancel.toString()
        }
    }

    private val textWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
        }

        override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
            val date = binding.tvDate.text.toString().trim()
            val date1 = binding.tvDate1.text.toString().trim()
            if (date != "" && date1 != "") {
                binding.btnFind.isEnabled = true
            }
        }

        override fun afterTextChanged(s: Editable?) {

        }
    }

    override fun onSaveInstanceState(outState: Bundle) {
        outState.putString("start", binding.tvDate.text.toString().trim())
        outState.putString("end", binding.tvDate1.text.toString().trim())

        super.onSaveInstanceState(outState)
    }

//    override fun onViewStateRestored(savedInstanceState: Bundle?) {
//        super.onViewStateRestored(savedInstanceState)
//        dateString = savedInstanceState?.getString("start", "").toString()
//        dateString1 = savedInstanceState?.getString("end", "").toString()
//        if (dateString != "null" && dateString1 != "null") {
//            binding.tvDate.text = dateString
//            binding.tvDate1.text = dateString1
//            binding.layoutSearch.visibility = View.VISIBLE
//        }
//    }
    private fun addFragment(fragment: Fragment, user: User, start: Long, end: Long, id: Int) {
        val bundle = Bundle()
        bundle.putSerializable("user", user)
        bundle.putString("type", "admin")
        bundle.putString("from", "orderListFragment")
        bundle.putLong("start", start)
        bundle.putLong("end", end)
        bundle.putInt("id", id)
        fragment.arguments = bundle
        val fragmentManager = requireActivity().supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.frame_layout_order_list, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }
}