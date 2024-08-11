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

        var user = User()
        lifecycleScope.launchWhenStarted {
            userViewModel.user.collectLatest {
                when (it) {
                    is Resource.Error -> {}
                    is Resource.Loading -> {}
                    is Resource.Success ->{
                        user = it.data!!
                        binding.tvUserName.text = it.data.name
                        orderViewModel.getConfirmOrder(it.data.id)
                        orderViewModel.getPackOrder(it.data.id)
                        orderViewModel.getShippingOrder(it.data.id)
                        orderViewModel.getNotRateOrder(it.data.id)
                    }
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            orderViewModel.confirmOrderList.collectLatest {
                when(it){
                    is Resource.Error -> {}
                    is Resource.Loading ->{
                        binding.linearProgress.visibility = View.VISIBLE
                        binding.cvConfirm.visibility = View.GONE
                    }
                    is Resource.Success -> {
                        binding.linearProgress.visibility = View.INVISIBLE
                        if (it.data!!.isNotEmpty()) {
                            binding.tvQuantityConfirm.text = it.data.size.toString()
                            binding.cvConfirm.visibility = View.VISIBLE
                        }
                    }
                }
            }
        }
        lifecycleScope.launchWhenStarted {
            orderViewModel.packOrderList.collectLatest {
                when(it){
                    is Resource.Error -> {}
                    is Resource.Loading ->{
                        binding.linearProgress.visibility = View.VISIBLE
                        binding.cvBox.visibility = View.GONE
                    }
                    is Resource.Success -> {
                        binding.linearProgress.visibility = View.INVISIBLE
                        if (it.data!!.isNotEmpty()) {
                            binding.tvQuantityBox.text = it.data.size.toString()
                            binding.cvBox.visibility = View.VISIBLE
                        }
                    }
                }
            }
        }
        lifecycleScope.launchWhenStarted {
            orderViewModel.shippingOrderList.collectLatest {
                when(it){
                    is Resource.Error -> {}
                    is Resource.Loading ->{
                        binding.linearProgress.visibility = View.VISIBLE
                        binding.cvShipping.visibility = View.GONE
                    }
                    is Resource.Success -> {
                        binding.linearProgress.visibility = View.INVISIBLE
                        if (it.data!!.isNotEmpty()) {
                            binding.tvQuantityShipping.text = it.data.size.toString()
                            binding.cvShipping.visibility = View.VISIBLE
                        }
                    }
                }
            }
        }
        lifecycleScope.launchWhenStarted {
            orderViewModel.notRateOrderList.collectLatest {
                when(it){
                    is Resource.Error -> {}
                    is Resource.Loading ->{
                        binding.linearProgress.visibility = View.VISIBLE
                        binding.cvRate.visibility = View.GONE
                    }
                    is Resource.Success -> {
                        binding.linearProgress.visibility = View.INVISIBLE
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
        binding.imgConfirm.setOnClickListener { moveToNewFragment(1, user) }
        binding.imgBoxPack.setOnClickListener { moveToNewFragment(2, user) }
        binding.imgShipping.setOnClickListener { moveToNewFragment(3, user) }
        binding.tvOrder.setOnClickListener { moveToNewFragment(4, user) }

        binding.imgRate.setOnClickListener {
            val bundle = Bundle()
            bundle.putInt("id", 1)
            bundle.putSerializable("user", user)
            bundle.putString("type", user.type)
            controller.navigate(R.id.action_userFragment_to_deliveredFragment, bundle)
        }

        binding.tvProfileUser.setOnClickListener {
            val bundle = Bundle()
            bundle.putSerializable("user", user)
            controller.navigate(R.id.action_userFragment_to_profileFragment, bundle)
        }
        binding.tvAddress.setOnClickListener {
            val bundle = Bundle()
            bundle.putInt("position", position)
            bundle.putSerializable("user", user)
            controller.navigate(R.id.action_userFragment_to_addressFragment, bundle)
        }
        binding.tvLogout.setOnClickListener {
            val logoutDialog = ChoiceDialog("userFragment", object : OnClickChoice {
            override fun onClick(choice: Boolean?) {
                if (choice == true) {
                    userLogout()
                }
            }
        })
            logoutDialog.show(requireActivity().supportFragmentManager, null)
        }
    }
    private fun moveToNewFragment(id: Int, user: User) {
        val bundle = Bundle()
        bundle.putInt("id", id)
        bundle.putSerializable("user", user)
        bundle.putString("from", "userFragment")
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