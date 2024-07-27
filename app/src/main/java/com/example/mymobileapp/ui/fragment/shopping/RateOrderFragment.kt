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
import com.example.mymobileapp.viewmodel.OrderViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@Suppress("DEPRECATION")
@AndroidEntryPoint
class RateOrderFragment : Fragment() {
    private lateinit var binding: FragmentRateOrderBinding
    private lateinit var controller: NavController
    private val orderViewModel by viewModels<OrderViewModel>()

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

        val order = requireArguments().getSerializable("Order") as Order

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
            val note = binding.edtNote.text.toString().trim()
            orderViewModel.rateOrder(order,star, note)
        }

        lifecycleScope.launchWhenStarted {
            orderViewModel.rate.collectLatest {
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
            controller.popBackStack()
        }
    }
    private val textWatcher: TextWatcher = object : TextWatcher {
        override fun beforeTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
        }

        override fun onTextChanged(charSequence: CharSequence, i: Int, i1: Int, i2: Int) {
            val note = binding.edtNote.text.toString().trim()
            if (note.isNotEmpty()) {
                binding.tvNote.visibility = View.GONE
                binding.tvNote1.visibility = View.GONE
            }
        }

        override fun afterTextChanged(editable: Editable) {
        }
    }
}