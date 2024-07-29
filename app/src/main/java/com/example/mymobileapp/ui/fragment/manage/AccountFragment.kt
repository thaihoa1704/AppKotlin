package com.example.mymobileapp.ui.fragment.manage

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mymobileapp.R
import com.example.mymobileapp.adapter.AccountAdapter
import com.example.mymobileapp.adapter.SearchAdapter
import com.example.mymobileapp.databinding.FragmentAccountBinding
import com.example.mymobileapp.listener.ClickItemAccountListener
import com.example.mymobileapp.listener.ClickItemProductListener
import com.example.mymobileapp.model.Product
import com.example.mymobileapp.model.User
import com.example.mymobileapp.util.Resource
import com.example.mymobileapp.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@Suppress("DEPRECATION")
@AndroidEntryPoint
class AccountFragment : Fragment(), ClickItemAccountListener {
    private lateinit var binding: FragmentAccountBinding
    private lateinit var controller: NavController
    private lateinit var accountAdapter: AccountAdapter
    private val viewModel by viewModels<UserViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAccountBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        controller = Navigation.findNavController(view)

        setupAccountRecyclerView()

        viewModel.getAllUser()
        lifecycleScope.launchWhenStarted {
            viewModel.userList.collectLatest {
                when (it) {
                    is Resource.Error -> {}
                    is Resource.Loading -> {}
                    is Resource.Success ->{
                        binding.tvQuantity.text = it.data!!.size.toString()
                        accountAdapter.differ.submitList(it.data)
                    }
                }
            }
        }

        binding.imgBack.setOnClickListener { controller.popBackStack() }
    }
    private fun setupAccountRecyclerView() {
        accountAdapter = AccountAdapter(this)
        binding.rcvAccount.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@AccountFragment.context)
            adapter = accountAdapter
        }
    }

    override fun onClickItemAccount(user: User) {
        val bundle = Bundle()
        bundle.putSerializable("UserModel", user)
        controller.navigate(R.id.action_accountFragment_to_detailAccountFragment, bundle)
    }
}