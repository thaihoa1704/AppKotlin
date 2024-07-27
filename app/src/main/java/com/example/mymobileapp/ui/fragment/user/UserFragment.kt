package com.example.mymobileapp.ui.fragment.user

import android.content.Intent
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
import com.example.mymobileapp.R
import com.example.mymobileapp.databinding.FragmentUserBinding
import com.example.mymobileapp.listener.OnClickChoice
import com.example.mymobileapp.model.User
import com.example.mymobileapp.ui.activity.MainActivity
import com.example.mymobileapp.ui.dialog.ChoiceDialog
import com.example.mymobileapp.util.Resource
import com.example.mymobileapp.viewmodel.AddressViewModel
import com.example.mymobileapp.viewmodel.CartViewModel
import com.example.mymobileapp.viewmodel.OrderViewModel
import com.example.mymobileapp.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@Suppress("DEPRECATION")
@AndroidEntryPoint
class UserFragment : Fragment() {
    private lateinit var binding: FragmentUserBinding
    private lateinit var controller: NavController
    private val cartViewModel by viewModels<CartViewModel>()
    private val userViewModel by viewModels<UserViewModel>()
    private val orderViewModel by viewModels<OrderViewModel>()
    private val addressViewModel by viewModels<AddressViewModel>()
    private lateinit var user: User
    private var position = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentUserBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        controller = Navigation.findNavController(view)

        cartViewModel.selectNoneAllProduct()

        lifecycleScope.launchWhenStarted {
            userViewModel.user.collectLatest {
                when (it) {
                    is Resource.Error -> {}
                    is Resource.Loading -> {}
                    is Resource.Success ->{
                        user = it.data!!
                        binding.tvUserName.text = it.data.name
                    }
                }
            }
        }

        orderViewModel.getConfirmOrder()
        orderViewModel.getShippingOrder()
        orderViewModel.getRateOrder()

        lifecycleScope.launchWhenStarted {
            orderViewModel.confirmOrder.collectLatest {
                when(it){
                    is Resource.Error -> {}
                    is Resource.Loading ->{}
                    is Resource.Success -> {
                        if (it.data!!.isNotEmpty()) {
                            binding.tvQuantityConfirm.text = it.data.size.toString()
                            binding.cvConfirm.visibility = View.VISIBLE
                        }
                    }
                }
            }
        }
        lifecycleScope.launchWhenStarted {
            orderViewModel.shippingOrderList.collectLatest {
                when(it){
                    is Resource.Error -> {}
                    is Resource.Loading ->{}
                    is Resource.Success -> {
                        if (it.data!!.isNotEmpty()) {
                            binding.tvQuantityShipping.text = it.data.size.toString()
                            binding.cvShipping.visibility = View.VISIBLE
                        }
                    }
                }
            }
        }
        lifecycleScope.launchWhenStarted {
            orderViewModel.rateOrderList.collectLatest {
                when(it){
                    is Resource.Error -> {}
                    is Resource.Loading ->{}
                    is Resource.Success -> {
                        if (it.data!!.isNotEmpty()) {
                            binding.tvQuantityRate.text = it.data.size.toString()
                            binding.cvRate.visibility = View.VISIBLE
                        }
                    }
                }
            }
        }
        lifecycleScope.launchWhenStarted {
            addressViewModel.addressList.collectLatest {
                when (it) {
                    is Resource.Error -> {}
                    is Resource.Loading -> {}
                    is Resource.Success -> {
                        if (it.data!!.isEmpty()) {
                            position = -1
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
        binding.imgConfirm.setOnClickListener { moveToNewFragment(1) }
        binding.imgShipping.setOnClickListener { moveToNewFragment(2) }
        binding.imgRate.setOnClickListener { moveToNewFragment(3) }

        binding.tvProfileUser.setOnClickListener {
            val bundle = Bundle()
            bundle.putSerializable("user", user)
            controller.navigate(R.id.action_userFragment_to_profileFragment, bundle)
        }
        binding.tvAddress.setOnClickListener {
            val bundle = Bundle()
            bundle.putInt("position", position)
            controller.navigate(R.id.action_userFragment_to_addressFragment, bundle)
        }
        binding.tvLogout.setOnClickListener {
            val logoutDialog = ChoiceDialog("UserFragment", object : OnClickChoice {
                override fun onClick(choice: Boolean?) {
                    if (choice == true) {
                        userLogout()
                    }
                }
            })
            logoutDialog.show(requireActivity().supportFragmentManager, null)
        }
        binding.tvOrder.setOnClickListener { controller.navigate(R.id.action_userFragment_to_purchaseHistoryFragment) }
    }
    private fun moveToNewFragment(id: Int) {
        val bundle = Bundle()
        bundle.putInt("id", id)
        controller.navigate(R.id.action_userFragment_to_orderProcessFragment, bundle)
    }
    private fun userLogout() {
        userViewModel.userLogout()
        Toast.makeText(requireContext(), "Tài khoản đã đăng xuất!", Toast.LENGTH_SHORT).show()
        val intent = Intent(requireActivity(), MainActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }
}