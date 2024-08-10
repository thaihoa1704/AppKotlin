package com.example.mymobileapp.ui.fragment.manage

import android.app.DatePickerDialog
import android.graphics.Color
import android.graphics.Typeface
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
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mymobileapp.R
import com.example.mymobileapp.adapter.CountAdapter
import com.example.mymobileapp.databinding.FragmentStatisticalBinding
import com.example.mymobileapp.helper.Convert
import com.example.mymobileapp.model.CartProduct
import com.example.mymobileapp.model.Product
import com.example.mymobileapp.model.ProductCount
import com.example.mymobileapp.model.Temp
import com.example.mymobileapp.util.Resource
import com.example.mymobileapp.util.constants.CANCEL_STATUS
import com.example.mymobileapp.util.constants.CONFIRM_STATUS
import com.example.mymobileapp.util.constants.NOT_RATE_STATUS
import com.example.mymobileapp.util.constants.PACKING_STATUS
import com.example.mymobileapp.util.constants.RATE_STATUS
import com.example.mymobileapp.util.constants.SHIPPING_STATUS
import com.example.mymobileapp.viewmodel.OrderViewModel
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.formatter.PercentFormatter
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import java.text.SimpleDateFormat
import java.util.Collections
import java.util.Locale
import java.util.function.BinaryOperator
import java.util.function.Function
import java.util.function.Supplier
import java.util.stream.Collectors


@Suppress("DEPRECATION")
@AndroidEntryPoint
class StatisticalFragment : Fragment() {
    private lateinit var binding: FragmentStatisticalBinding
    private lateinit var controller: NavController
    private val orderViewModel by viewModels<OrderViewModel>()
    private val dateFormat1 = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault())
    private var dateString: String = ""
    private var dateString1: String = ""
    private var confirm = 0
    private var pack = 0
    private var shipping = 0
    private var notRate = 0
    private var rate = 0
    private var cancel = 0
    private val list = mutableListOf<CartProduct>()
    private val listCount = mutableListOf<ProductCount>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentStatisticalBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        controller = Navigation.findNavController(view)

        binding.tvDate.addTextChangedListener(textWatcher)
        binding.tvDate1.addTextChangedListener(textWatcher)
        binding.layoutSearch.visibility = View.GONE

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

        if (binding.tvDate.text.isEmpty() || binding.tvDate1.text.isEmpty()) {
            binding.layoutSearch.visibility = View.GONE
        } else {
            binding.layoutSearch.visibility = View.VISIBLE
        }

//        setupProductRecyclerView()
        lifecycleScope.launchWhenStarted {
            orderViewModel.time.collectLatest {
                when(it){
                    is Resource.Error -> {
                        Toast.makeText(requireContext(), "Lỗi lấy dữ liệu", Toast.LENGTH_SHORT).show()
                    }
                    is Resource.Loading ->{}
                    is Resource.Success -> {
                        if (it.data!!.isEmpty()) {
                            binding.tvEmpty.visibility = View.VISIBLE
                        }else{
                            var total = 0
                            binding.tvEmpty.visibility = View.GONE
                            binding.tvQuantity.text = it.data.size.toString()
                            for (i in it.data) {
                                when (i.status) {
                                    CONFIRM_STATUS -> { confirm++ }
                                    PACKING_STATUS -> { pack++ }
                                    SHIPPING_STATUS -> { shipping++ }
                                    NOT_RATE_STATUS -> {
                                        notRate++
                                        list.addAll(i.listProduct)
                                        total += i.total
                                    }
                                    RATE_STATUS -> {
                                        rate++
                                        list.addAll(i.listProduct)
                                        total += i.total
                                    }
                                    CANCEL_STATUS -> { cancel++ }
                                }
                            }
                            binding.tvPrice.text = Convert.DinhDangTien(total) + " đ"
                            val frequencyMap: MutableMap<CartProduct, Int> = HashMap()
                            for (s in list) {
                                var count = frequencyMap[s]
                                if (count == null) count = 0
                                frequencyMap[s] = count + 1
                            }
                            val sortedMap = frequencyMap.toList().sortedByDescending { (_, value) -> value }.toMap()
                            for ((key, value) in sortedMap) {
                                val productCount = ProductCount(key, value)
                                listCount.add(productCount)
                            }
                            setPieCharOrder()
                            setQuantity()
                        }
                    }
                }
            }
        }
        binding.imgSearch.setOnClickListener {
            val start = dateFormat1.parse(dateString)!!.time
            val end = dateFormat1.parse(dateString1)!!.time
            orderViewModel.getOrderListByTime(start, end)
            binding.scrollView.visibility = View.VISIBLE
        }
        binding.btnProduct.setOnClickListener {
            addFragment(ProductOrderFragment(), listCount)
        }

        binding.imgBack.setOnClickListener {
            controller.popBackStack()
        }
    }

    private fun setQuantity() {
        binding.apply {
            tvConfirm.text = confirm.toString()
            tvPack.text = pack.toString()
            tvShipping.text = shipping.toString()
            tvNotRate.text = notRate.toString()
            tvRate.text = rate.toString()
            tvCancel.text = cancel.toString()
        }
    }

    private fun setPieCharOrder() {
        val entries = listOf(
            PieEntry(confirm.toFloat(), ""),
            PieEntry(pack.toFloat(), ""),
            PieEntry(shipping.toFloat(), ""),
            PieEntry(notRate.toFloat(), ""),
            PieEntry(rate.toFloat(), ""),
            PieEntry(cancel.toFloat(), "")
        )
        val pieDataSet = PieDataSet(entries, "")
        pieDataSet.colors = listOf(Color.GRAY, Color.GREEN, Color.MAGENTA
                                    , Color.BLUE, Color.YELLOW, Color.RED)
        val data = PieData(pieDataSet)
        data.setValueFormatter(PercentFormatter())
        data.setValueTextSize(20f)
        data.setValueTypeface(Typeface.DEFAULT_BOLD)
        data.setValueTextColor(Color.BLACK)

        binding.apply {
            pieChart.data = data
            pieChart.setUsePercentValues(true)
            pieChart.setTransparentCircleColor(Color.WHITE)
            pieChart.legend.isEnabled = false
            pieChart.setEntryLabelColor(Color.WHITE)
            pieChart.description.isEnabled = false
            pieChart.centerText = "Đơn hàng"
            pieChart.setCenterTextSize(22f)
            pieChart.setDrawCenterText(true)
            pieChart.invalidate()
        }
    }

    private val textWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
        }

        override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
            val date = binding.tvDate.text.toString().trim()
            val date1 = binding.tvDate1.text.toString().trim()
            if (date != "" && date1 != "") {
                binding.layoutSearch.visibility = View.VISIBLE
            } else {
                binding.layoutSearch.visibility = View.GONE
            }
        }

        override fun afterTextChanged(s: Editable?) {

        }
    }
    private fun addFragment(fragment: Fragment, list: List<ProductCount>) {
        val bundle = Bundle()
        val temp = Temp(list)
        bundle.putSerializable("list", temp)
        fragment.arguments = bundle
        val fragmentManager = requireActivity().supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.frame_layout_statistical, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }
}
