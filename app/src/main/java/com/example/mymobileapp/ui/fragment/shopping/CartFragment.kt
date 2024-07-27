package com.example.mymobileapp.ui.fragment.shopping

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
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mymobileapp.R
import com.example.mymobileapp.adapter.CartAdapter
import com.example.mymobileapp.databinding.FragmentCartBinding
import com.example.mymobileapp.helper.Convert
import com.example.mymobileapp.listener.ChangeQuantityCartProduct
import com.example.mymobileapp.listener.ChangeSelectProductListener
import com.example.mymobileapp.listener.ClickItemProductListener
import com.example.mymobileapp.model.Product
import com.example.mymobileapp.util.Resource
import com.example.mymobileapp.viewmodel.CartViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@Suppress("DEPRECATION")
@AndroidEntryPoint
class CartFragment : Fragment(), ClickItemProductListener, ChangeQuantityCartProduct,
    ChangeSelectProductListener {
    private lateinit var binding: FragmentCartBinding
    private lateinit var controller: NavController
    private lateinit var cartAdapter: CartAdapter
    private val viewModel by viewModels<CartViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        controller = Navigation.findNavController(view)

        setupCartRecyclerView()

        lifecycleScope.launchWhenStarted {
            viewModel.cartList.collectLatest {
                when(it){
                    is Resource.Error -> {
                        hideLoading()
                        Toast.makeText(requireContext(), "Lỗi lấy dữ liệu", Toast.LENGTH_SHORT).show()
                    }
                    is Resource.Loading ->{
                        showLoading()
                    }
                    is Resource.Success -> {
                        if (it.data!!.isEmpty()) {
                            showEmpty()
                        } else {
                            hideLoading()
                            cartAdapter.differ.submitList(it.data)
                        }
                    }
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.totalPrice.collectLatest { price ->
                price.let {
                    binding.tvTotal.text = Convert.DinhDangTien(it) + " đ"
                }
            }
        }

        binding.btnBuy.setOnClickListener {
            if (binding.tvTotal.text == "0 đ") {
                Toast.makeText(requireContext(), "Chọn sản phẩm bạn muốn thanh toán!", Toast.LENGTH_SHORT).show()
            } else {
                controller.navigate(R.id.action_cartFragment_to_orderFragment)
            }
        }
    }

    private fun showEmpty() {
        binding.processIndicator.visibility = View.INVISIBLE
        binding.rcvCart.visibility = View.INVISIBLE
        binding.tvEmpty.visibility = View.VISIBLE
    }

    private fun showLoading() {
        binding.processIndicator.visibility = View.VISIBLE
        binding.rcvCart.visibility = View.VISIBLE
    }

    private fun hideLoading() {
        binding.processIndicator.visibility = View.INVISIBLE
        binding.rcvCart.visibility = View.VISIBLE
    }

    private fun setupCartRecyclerView() {
        cartAdapter = CartAdapter(this, this, this)
        binding.rcvCart.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@CartFragment.context)
            adapter = cartAdapter
        }
    }

    override fun onClickItemProduct(product: Product) {
        val bundle = Bundle()
        bundle.putSerializable("ProductModel", product)
        controller.navigate(R.id.action_cartFragment_to_detailProductFragment, bundle)
    }

    override fun incrementQuantity(documentId: String) {
        viewModel.incrementQuantityProductInCart(documentId)
    }

    override fun decrementQuantity(documentId: String) {
        viewModel.decrementQuantityProductInCart(documentId)
    }

    override fun deleteProduct(documentId: String) {
        viewModel.deleteProductInCart(documentId)
    }

    override fun onChangeSelect(documentId: String, aBoolean: Boolean) {
        viewModel.selectProduct(documentId, aBoolean)
    }

}