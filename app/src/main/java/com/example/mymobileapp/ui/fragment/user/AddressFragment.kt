package com.example.mymobileapp.ui.fragment.user

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mymobileapp.R
import com.example.mymobileapp.adapter.AddressAdapter
import com.example.mymobileapp.databinding.FragmentAddressBinding
import com.example.mymobileapp.listener.ClickItemAddressListener
import com.example.mymobileapp.model.Address
import com.example.mymobileapp.model.User
import com.example.mymobileapp.util.Resource
import com.example.mymobileapp.viewmodel.AddressViewModel
import com.example.mymobileapp.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@Suppress("DEPRECATION")
@AndroidEntryPoint
class AddressFragment : Fragment(), ClickItemAddressListener {
    private lateinit var binding: FragmentAddressBinding
    private lateinit var controller: NavController
    private lateinit var addressAdapter: AddressAdapter
    private val addressViewModel by viewModels<AddressViewModel>()
    private var position = 0
    private var check: Boolean = false
    private var user = User()
    private var type = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddressBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        controller = Navigation.findNavController(view)

        position = requireArguments().getInt("position")
        user = requireArguments().getSerializable("user") as User
        type = requireArguments().getString("type").toString()

        if (type == "admin") {
            binding.label.text = "Địa chỉ khách hàng"
            binding.constraintLayout.visibility = View.INVISIBLE
            binding.line2.visibility = View.INVISIBLE
            binding.rcvAddress.isEnabled = false
        } else{
            binding.constraintLayout.visibility = View.VISIBLE
        }

        addressViewModel.getAddressAccount(user)
        lifecycleScope.launchWhenStarted {
            addressViewModel.addressList.collectLatest {
                when (it) {
                    is Resource.Error -> {
                        binding.processIndicator.visibility = View.INVISIBLE
                    }
                    is Resource.Loading -> {
                        binding.processIndicator.visibility = View.VISIBLE
                    }
                    is Resource.Success -> {
                        binding.processIndicator.visibility = View.INVISIBLE
                        if (it.data!!.isEmpty()) {
                            binding.rcvAddress.visibility = View.INVISIBLE
                            check = true
                        } else {
                            addressAdapter.differ.submitList(it.data)
                            check = false
                        }
                    }
                }
            }
        }
        setupAddressRecyclerView()

        binding.constraintLayout.setOnClickListener {
            controller.navigate(R.id.action_addressFragment_to_addAddressFragment)
        }
        binding.imgBack.setOnClickListener{
            controller.popBackStack()
        }
    }
    private fun setupAddressRecyclerView() {
        addressAdapter = AddressAdapter( position, this)
        binding.rcvAddress.apply {
            layoutManager = LinearLayoutManager(this@AddressFragment.context)
            adapter = addressAdapter
        }
    }
    override fun onClick(address: Address) {
        if (type == "admin") {
            return
        } else {
            addressViewModel.selectAddress(address)
        }
    }
}