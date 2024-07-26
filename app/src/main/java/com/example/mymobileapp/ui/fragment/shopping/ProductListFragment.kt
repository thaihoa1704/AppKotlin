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
import androidx.recyclerview.widget.GridLayoutManager
import com.example.mymobileapp.adapter.ProductAdapter
import com.example.mymobileapp.databinding.FragmentProductListBinding
import com.example.mymobileapp.listener.ClickItemProductListener
import com.example.mymobileapp.model.Product
import com.example.mymobileapp.util.Resource
import com.example.mymobileapp.viewmodel.ProductListViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@Suppress("DEPRECATION")
@AndroidEntryPoint
class ProductListFragment : Fragment(), ClickItemProductListener {
    private lateinit var binding: FragmentProductListBinding
    private lateinit var controller: NavController
    private val viewModel by viewModels<ProductListViewModel>()
    private var category: String? = null
    private lateinit var productAdapter: ProductAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentProductListBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        controller = Navigation.findNavController(view)

        category = requireArguments().getString("category")
        binding.tvCategoryName.text = category

        setupProductRecyclerView()
        viewModel.getProducts(category!!)
        lifecycleScope.launchWhenStarted {
            viewModel.productList.collectLatest {
                when(it){
                    is Resource.Error -> {
                        hideLoading()
                        binding.tvEmpty.text = "Lỗi lấy dữ liệu"
                        binding.tvEmpty.visibility = View.VISIBLE
                        Toast.makeText(requireContext(), "Lỗi lấy dữ liệu", Toast.LENGTH_SHORT).show()
                    }
                    is Resource.Loading -> {
                        showLoading()
                    }
                    is Resource.Success ->{
                        if (it.data!!.isEmpty()) {
                            showEmpty()
                        } else {
                            hideLoading()
                            productAdapter.differ.submitList(it.data)
                        }
                    }
                }
            }
        }
        binding.imgBack.setOnClickListener {
            controller.popBackStack()
        }
    }

    private fun showEmpty() {
        binding.processBar.visibility = View.GONE
        binding.rcvProduct.visibility = View.INVISIBLE
        binding.tvEmpty.visibility = View.VISIBLE
    }
    private fun showLoading() {
        binding.processBar.visibility = View.VISIBLE
        binding.rcvProduct.visibility = View.INVISIBLE
    }

    private fun hideLoading() {
        binding.processBar.visibility = View.GONE
        binding.rcvProduct.visibility = View.VISIBLE
    }

    private fun setupProductRecyclerView() {
        productAdapter = ProductAdapter(this)
        binding.rcvProduct.apply {
            setHasFixedSize(true)
            layoutManager = GridLayoutManager(this@ProductListFragment.context, 2)
            adapter = productAdapter
        }
    }

    override fun onClickItemProduct(product: Product) {

    }
}