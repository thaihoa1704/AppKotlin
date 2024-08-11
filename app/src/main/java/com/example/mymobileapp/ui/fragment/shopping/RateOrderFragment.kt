package com.example.mymobileapp.ui.fragment.shopping

import android.os.Bundle
import android.os.Handler
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RatingBar.OnRatingBarChangeListener
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.example.mymobileapp.databinding.FragmentRateOrderBinding
import com.example.mymobileapp.model.Order
import com.example.mymobileapp.util.Resource
import com.example.mymobileapp.util.constants.RATE_STATUS
import com.example.mymobileapp.viewmodel.OrderViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@Suppress("DEPRECATION")
@AndroidEntryPoint
class RateOrderFragment : Fragment() {
    private lateinit var binding: FragmentRateOrderBinding
    private lateinit var controller: NavController
    private val orderViewModel by viewModels<OrderViewModel>()
    private var order = Order()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentRateOrderBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        controller = Navigation.findNavController(view)

        order = requireArguments().getSerializable("order") as Order
        val rate = requireArguments().getString("rate")
        val type = requireArguments().getString("type")

        if (rate == RATE_STATUS) {
            binding.ratingBar.rating = order.rateStar.toFloat()
            val star = order.rateStar
            when (star) {
                1 -> binding.tvRate.text = "Tệ"
                2 -> binding.tvRate.text = "Không hài lòng"
                3 -> binding.tvRate.text = "Bình thường"
                4 -> binding.tvRate.text = "Hài lòng"
                5 -> binding.tvRate.text = "Tuyệt vời"
                else -> binding.tvRate.text = " "
            }
            binding.tvNote.visibility = View.GONE
            if (order.note.isNotEmpty()) {
                binding.tvNote1.text = order.note
            } else {
                binding.tvNote1.text = "Không có nhận xét"
            }
            binding.btnSend.visibility = View.GONE
            binding.edtNote.visibility = View.GONE
        }

        binding.ratingBar.onRatingBarChangeListener =
            OnRatingBarChangeListener { ratingBar, rating, _ ->
                binding.tvRate.text = rating.toString()
                when (ratingBar.rating.toInt()) {
                    1 -> binding.tvRate.text = "Tệ"
                    2 -> binding.tvRate.text = "Không hài lòng"
                    3 -> binding.tvRate.text = "Bình thường"
                    4 -> binding.tvRate.text = "Hài lòng"
                    5 -> binding.tvRate.text = "Tuyệt vời"
                    else -> binding.tvRate.text = " "
                }
            }
        binding.edtNote.addTextChangedListener(textWatcher)

        binding.btnSend.setOnClickListener {
            val star = binding.ratingBar.rating.toInt()
            val note = binding.edtNote.text.toString()
            orderViewModel.rateOrder(order, star, note)
        }

        lifecycleScope.launchWhenStarted {
            orderViewModel.message.collectLatest {
                when(it){
                    is Resource.Error -> {
                        Toast.makeText(requireContext(), it.message, Toast.LENGTH_SHORT).show()
                        Handler().postDelayed({
                            controller.popBackStack()
                        },2000)
                    }
                    is Resource.Loading -> {}
                    is Resource.Success -> {
                        Toast.makeText(requireContext(), it.data, Toast.LENGTH_SHORT).show()
                        Handler().postDelayed({
                            order.status = "Đã đánh giá"
                            controller.popBackStack()
                        },2000)
                    }
                }
            }
        }

        binding.imgBack.setOnClickListener{
            if (type == "admin") {
                removeFragment()
            } else {
                controller.popBackStack()
            }
        }
    }
    private val textWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
        }

        override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
            val note = binding.edtNote.text.toString()
            if (note.isNotEmpty()) {
                binding.tvNote.visibility = View.GONE
            } else {
                binding.tvNote.visibility = View.VISIBLE
            }
        }

        override fun afterTextChanged(editable: Editable) {
        }
    }
    private fun removeFragment() {
        val fragmentManager = requireActivity().supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.remove(this)
        fragmentTransaction.commit()
    }
}