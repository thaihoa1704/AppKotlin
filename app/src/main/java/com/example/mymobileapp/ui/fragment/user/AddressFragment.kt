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
    private var typeN = ""

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
        typeN = requireArguments().getString("type").toString()

        if (typeN == "admin") {
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
                            binding.rcvAddress.visibility = View.GONE
                            binding.emptyAddress.visibility = View.VISIBLE
                            binding.line3.visibility = View.VISIBLE
                            check = true
                        } else {
                            binding.rcvAddress.visibility = View.VISIBLE
                            binding.emptyAddress.visibility = View.GONE
                            binding.line3.visibility = View.GONE
                            addressAdapter.differ.submitList(it.data)
                            check = false
                        }
                    }
                }
            }
        }
        setupAddressRecyclerView()

        binding.constraintLayout.setOnClickListener {
            addFragment(AddAddressFragment(), check)
            //controller.navigate(R.id.action_addressFragment_to_addAddressFragment)
        }
        binding.imgBack.setOnClickListener {
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
        if (user.type == "customer") {
            addressViewModel.selectAddress(address)
        } else {
            //DO NOTHING
        }
    }
    private fun addFragment(fragment: Fragment, check: Boolean) {
        val bundle = Bundle()
        bundle.putBoolean("check", check)
        fragment.arguments = bundle

        val fragmentManager = requireActivity().supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.frame_layout_address, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }
}