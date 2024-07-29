package com.example.mymobileapp.ui.fragment.shopping

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.SearchView
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mymobileapp.R
import com.example.mymobileapp.adapter.SearchAdapter
import com.example.mymobileapp.databinding.FragmentSearchBinding
import com.example.mymobileapp.listener.ClickItemProductListener
import com.example.mymobileapp.model.Product
import com.example.mymobileapp.util.Resource
import com.example.mymobileapp.viewmodel.ProductListViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest
import java.util.Locale

@Suppress("DEPRECATION")
@AndroidEntryPoint
class SearchFragment : Fragment(), ClickItemProductListener {
    private lateinit var binding: FragmentSearchBinding
    private lateinit var searchAdapter: SearchAdapter
    private val viewModel by viewModels<ProductListViewModel>()
    private lateinit var controller: NavController
    private lateinit var productList: List<Product>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        controller = Navigation.findNavController(view)

        setupProductRecyclerView()

        viewModel.getAllProduct()
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
                            searchAdapter.differ.submitList(it.data)
                            productList = it.data
                        }
                    }
                }
            }
        }

        binding.searchView.apply {
            clearFocus()
            setOnQueryTextListener(object : SearchView.OnQueryTextListener {
                override fun onQueryTextSubmit(query: String): Boolean {
                    return false
                }

                override fun onQueryTextChange(newText: String): Boolean {
                    filterList(newText)
                    return true
                }
            })
        }
        binding.imgBack.setOnClickListener {
            controller.popBackStack()
        }
    }
    private fun showLoading() {
        binding.processBar.visibility = View.VISIBLE
        binding.rvProduct.visibility = View.GONE
    }

    private fun hideLoading() {
        binding.processBar.visibility = View.GONE
        binding.rvProduct.visibility = View.VISIBLE
    }
    private fun showEmpty() {
        binding.processBar.visibility = View.GONE
        binding.rvProduct.visibility = View.INVISIBLE
        binding.tvEmpty.visibility = View.VISIBLE
    }
    private fun show() {
        binding.processBar.visibility = View.GONE
        binding.rvProduct.visibility = View.VISIBLE
        binding.tvEmpty.visibility = View.GONE
    }
    private fun setupProductRecyclerView() {
        searchAdapter = SearchAdapter(this)
        binding.rvProduct.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@SearchFragment.context)
            adapter = searchAdapter
        }
    }

    override fun onClickItemProduct(product: Product) {
        val bundle = Bundle()
        bundle.putSerializable("ProductModel", product)
        controller.navigate(R.id.action_searchFragment_to_detailProductFragment, bundle)
    }
    private fun filterList(text: String) {
        val list: MutableList<Product> = ArrayList()
        for (product in productList) {
            if (product.name.lowercase(Locale.getDefault()).contains(text.lowercase(Locale.getDefault()))) {
                list.add(product)
            }
        }
        if (list.isEmpty()) {
            showEmpty()
        } else {
            searchAdapter.differ.submitList(list)
            show()
        }
    }

    override fun onResume() {
        super.onResume()
        binding.searchView.clearFocus()
    }
}