package com.example.mymobileapp.ui.fragment.manage

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
import com.example.mymobileapp.R
import com.example.mymobileapp.databinding.FragmentDetailAccountBinding
import com.example.mymobileapp.model.Address
import com.example.mymobileapp.model.User
import com.example.mymobileapp.util.Resource
import com.example.mymobileapp.util.constants.CANCEL_STATUS
import com.example.mymobileapp.util.constants.CONFIRM_STATUS
import com.example.mymobileapp.util.constants.NOT_RATE_STATUS
import com.example.mymobileapp.util.constants.RATE_STATUS
import com.example.mymobileapp.util.constants.SHIPPING_STATUS
import com.example.mymobileapp.viewmodel.AddressViewModel
import com.example.mymobileapp.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@Suppress("DEPRECATION")
@AndroidEntryPoint
class DetailAccountFragment : Fragment() {
    private lateinit var binding: FragmentDetailAccountBinding
    private lateinit var controller: NavController
    private val addressViewModel by viewModels<AddressViewModel>()
    private val userViewModel by viewModels<UserViewModel>()
    private var position = 0
    private var empty = -1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDetailAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        controller = Navigation.findNavController(view)

        val user = arguments?.getSerializable("user") as User

        binding.apply {
            tvName.text = user.name
            tvEmail.text = user.email
            tvPhone.text = user.phone
        }

        addressViewModel.getAddressAccount(user)
        lifecycleScope.launchWhenStarted {
            addressViewModel.addressList.collectLatest {
                when (it) {
                    is Resource.Error -> {}
                    is Resource.Loading -> {}
                    is Resource.Success -> {
                        empty = it.data!!.size
                        if (it.data.isEmpty()) {
                            empty = 0
                        } else {
                            for (i in it.data) {
                                if (i.select) {
                                    position = it.data.indexOf(i)
                                }
                            }
                        }
                    }
                }
            }
        }

        userViewModel.getOrderOfUser(user)
        lifecycleScope.launchWhenStarted {
            userViewModel.orderList.collectLatest {
                when (it) {
                    is Resource.Error -> {}
                    is Resource.Loading -> {}
                    is Resource.Success -> {
                        binding.tvQuantity.text = it.data!!.size.toString()
                        var confirm = 0
                        var shipping = 0
                        var notRate = 0
                        var rate = 0
                        var cancel = 0
                        it.data.forEach { order ->
                            when (order.status) {
                                CONFIRM_STATUS -> { confirm++ }
                                SHIPPING_STATUS -> { shipping++ }
                                NOT_RATE_STATUS -> { notRate++ }
                                RATE_STATUS -> { rate++ }
                                CANCEL_STATUS -> { cancel++ }
                            }
                        }
                        binding.tvQuantityConfirm.text = confirm.toString()
                        binding.tvQuantityShipping.text = shipping.toString()
                        binding.tvQuantityNotRate.text = notRate.toString()
                        binding.tvQuantityRate.text = rate.toString()
                        binding.tvQuantityCancel.text = cancel.toString()
                    }
                }
            }
        }

        binding.imgMore.setOnClickListener {
            if (empty != 0) {
                val bundle = Bundle()
                bundle.putInt("position", position)
                bundle.putSerializable("user", user)
                bundle.putString("type", "admin")
                controller.navigate(R.id.action_detailAccountFragment_to_addressFragment2, bundle)
            } else {
                Toast.makeText(requireContext(), "Chưa có địa chỉ giao hàng!", Toast.LENGTH_SHORT).show()
            }
        }
        binding.imgBack.setOnClickListener {
            controller.popBackStack()
        }
    }
}