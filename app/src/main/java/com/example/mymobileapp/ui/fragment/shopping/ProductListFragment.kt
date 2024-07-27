package com.example.mymobileapp.ui.fragment.shopping

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import com.example.mymobileapp.R
import com.example.mymobileapp.adapter.OrderPriceAdapter
import com.example.mymobileapp.adapter.ProductAdapter
import com.example.mymobileapp.databinding.FragmentProductListBinding
import com.example.mymobileapp.listener.ClickItemProductListener
import com.example.mymobileapp.model.Product
import com.example.mymobileapp.util.Resource
import com.example.mymobileapp.viewmodel.FilterViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@Suppress("DEPRECATION")
@AndroidEntryPoint
class ProductListFragment : Fragment(), ClickItemProductListener {
    private lateinit var binding: FragmentProductListBinding
    private lateinit var controller: NavController
    private var category: String? = null
    private lateinit var productAdapter: ProductAdapter
    private lateinit var orderPriceAdapter: OrderPriceAdapter
    private val filterViewModel by viewModels<FilterViewModel>()

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
        filterViewModel.getAllProduct(category!!)
        lifecycleScope.launchWhenStarted {
            filterViewModel.productList.collectLatest {
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

        orderPriceAdapter = OrderPriceAdapter(requireActivity(), R.layout.item_selected, getList())
        binding.spinner.adapter = orderPriceAdapter
        binding.spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(adapterView: AdapterView<*>?, view: View, i: Int, l: Long) {
                val choice = orderPriceAdapter.getItem(i)
                if (choice != null) {
                    when (choice) {
                        "Ngẫu nhiên" -> {
                            if (binding.imgFilter.visibility == View.VISIBLE
                                && binding.imgFilter1.visibility == View.GONE) {
                                filterViewModel.getAllProduct(category!!)
                            }
                        }
                        "Giá tăng dần" -> {
                            if (binding.imgFilter.visibility == View.VISIBLE
                                && binding.imgFilter1.visibility == View.GONE) {
                                filterViewModel.allAscending(category!!)
                            }
                        }
                        "Giá giảm dần" -> {
                            if (binding.imgFilter.visibility == View.VISIBLE
                                && binding.imgFilter1.visibility == View.GONE) {
                                filterViewModel.allDescending(category!!)
                            }
                        }
                    }
                }
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {
            }
        }
        binding.imgBack.setOnClickListener {
            controller.popBackStack()
        }
    }

    private fun showEmpty() {
        binding.processIndicator.visibility = View.GONE
        binding.rcvProduct.visibility = View.INVISIBLE
        binding.tvEmpty.visibility = View.VISIBLE
    }
    private fun showLoading() {
        binding.processIndicator.visibility = View.VISIBLE
        binding.rcvProduct.visibility = View.INVISIBLE
    }

    private fun hideLoading() {
        binding.processIndicator.visibility = View.GONE
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
        //Creat DetailProductFragment overlaps this fragment
        //Purpose: Don't reload view of this fragment when close DetailProductFragmen
        addFragment(DetailProductFragment(), product)
        //hideFragment()
    }
    private fun getList(): List<String> {
        val list: MutableList<String> = ArrayList()
        list.add("Ngẫu nhiên")
        list.add("Giá tăng dần")
        list.add("Giá giảm dần")
        return list
    }
    private fun addFragment(fragment: Fragment, product: Product) {
        val bundle = Bundle()
        bundle.putSerializable("ProductModel", product)
        bundle.putString("StartFragment", "ProductListFragment");
        fragment.arguments = bundle
        val fragmentManager = requireActivity().supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.frame_layout_product_list, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
    }
    private fun hideFragment(){
        binding.constraintLayoutProductList.visibility = View.GONE
    }
}