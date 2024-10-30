package com.example.mymobileapp.ui.fragment.shopping

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
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mymobileapp.R
import com.example.mymobileapp.adapter.ImageAdapter
import com.example.mymobileapp.adapter.ProductAdapter
import com.example.mymobileapp.databinding.FragmentHomeBinding
import com.example.mymobileapp.listener.ClickItemProductListener
import com.example.mymobileapp.listener.ClickItemProductListener1
import com.example.mymobileapp.model.Product
import com.example.mymobileapp.util.Resource
import com.example.mymobileapp.viewmodel.CartViewModel
import com.example.mymobileapp.viewmodel.SpecialProductViewModel
import com.example.mymobileapp.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@Suppress("DEPRECATION")
@AndroidEntryPoint
class HomeFragment : Fragment(), ClickItemProductListener, ClickItemProductListener1 {
    private lateinit var binding: FragmentHomeBinding
    private lateinit var controller: NavController
    private val viewModel by viewModels<UserViewModel>()
    private val productViewModel by viewModels<SpecialProductViewModel>()
    private val cartViewModel by viewModels<CartViewModel>()
    private lateinit var productAdapter: ProductAdapter
    private val imageAdapter by lazy { ImageAdapter() }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        controller = Navigation.findNavController(view)

        cartViewModel.selectNoneAllProduct()

        lifecycleScope.launchWhenStarted {
            viewModel.user.collectLatest {
                when (it) {
                    is Resource.Error -> {}
                    is Resource.Loading -> {}
                    is Resource.Success ->{
                        binding.txtName.text = it.data?.name
                    }
                }
            }
        }

        viewModel.getBanner()
        lifecycleScope.launchWhenStarted {
            viewModel.banner.collectLatest {
                when (it) {
                    is Resource.Error -> {}
                    is Resource.Loading -> {}
                    is Resource.Success ->{
                        val list = it.data
                        imageAdapter.differ.submitList(list!![0].str)
                    }
                }
            }
        }
        setSlideImage()

        setupProductRecyclerView()

        lifecycleScope.launchWhenStarted {
            productViewModel.specialProducts.collectLatest {
                when (it) {
                    is Resource.Error -> {
                        hideLoading()
                        Toast.makeText(requireContext(), "Lỗi lấy dữ liệu", Toast.LENGTH_SHORT).show()
                    }
                    is Resource.Loading -> {
                        showLoading()
                    }
                    is Resource.Success -> {
                        hideLoading()
                        productAdapter.differ.submitList(it.data)
                    }
                }
            }
        }

        binding.tvSearch.setOnClickListener {
            controller.navigate(R.id.action_homeFragment_to_searchFragment)
        }
        binding.imgPhone.setOnClickListener { moveToNewFragment("Điện thoại") }
        binding.imgHeadPhone.setOnClickListener { moveToNewFragment("Tai nghe") }
        binding.imgLaptop.setOnClickListener { moveToNewFragment("Laptop") }
        binding.imgWatch.setOnClickListener { moveToNewFragment("Đồng hồ") }
        //binding.imageAccessory.setOnClickListener { moveToNewFragment("Phụ kiện") }
    }
    private fun moveToNewFragment(category: String) {
        val bundle = Bundle()
        bundle.putString("category", category)
        bundle.putString("type", "customer")
        controller.navigate(R.id.action_homeFragment_to_productListFragment, bundle)
    }
    private fun setupProductRecyclerView() {
        productAdapter = ProductAdapter("customer",this, this)
        binding.rcvProduct.apply {
            layoutManager = LinearLayoutManager(this@HomeFragment.context, LinearLayoutManager.HORIZONTAL, false)
            adapter = productAdapter
        }
    }

    private fun showLoading() {
        binding.processBar.visibility = View.VISIBLE
        binding.rcvProduct.visibility = View.GONE
    }

    private fun hideLoading() {
        binding.processBar.visibility = View.GONE
        binding.rcvProduct.visibility = View.VISIBLE
    }
    override fun onClickItemProduct(product: Product) {
        val bundle = Bundle()
        bundle.putSerializable("product", product)
        controller.navigate(R.id.action_homeFragment_to_detailProductFragment, bundle)
    }
    private fun setSlideImage() {
        binding.apply {
            viewPagerImage.adapter = imageAdapter
            circleIndicator.setViewPager(viewPagerImage)
        }
    }

    override fun onClickItemProduct1(product: Product) {
        //Do nothing
    }
}