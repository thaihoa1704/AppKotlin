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
import com.example.mymobileapp.adapter.ColorAdapter
import com.example.mymobileapp.adapter.ImageProductAdapter
import com.example.mymobileapp.adapter.LaptopVersionAdapter
import com.example.mymobileapp.adapter.PhoneVersionAdapter
import com.example.mymobileapp.adapter.WatchVersionAdapter
import com.example.mymobileapp.databinding.FragmentDetailProductBinding
import com.example.mymobileapp.helper.Convert
import com.example.mymobileapp.listener.ClickItemColorListener
import com.example.mymobileapp.listener.ClickItemLaptopVersionListener
import com.example.mymobileapp.listener.ClickItemPhoneVersionListener
import com.example.mymobileapp.listener.ClickItemWatchVersionListener
import com.example.mymobileapp.model.CartProduct
import com.example.mymobileapp.model.Product
import com.example.mymobileapp.model.ProductColor
import com.example.mymobileapp.model.Version
import com.example.mymobileapp.util.Resource
import com.example.mymobileapp.viewmodel.CartViewModel
import com.example.mymobileapp.viewmodel.ProductViewModel
import com.example.mymobileapp.viewmodel.UserViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@Suppress("DEPRECATION")
@AndroidEntryPoint
class DetailProductFragment : Fragment(), ClickItemColorListener, ClickItemPhoneVersionListener,
    ClickItemLaptopVersionListener, ClickItemWatchVersionListener {
    private lateinit var binding: FragmentDetailProductBinding
    private lateinit var controller: NavController
    private lateinit var versionSelected: Version
    private lateinit var productColorSelected: ProductColor

    private val viewModel by viewModels<ProductViewModel>()
    private val cartViewModel by viewModels<CartViewModel>()
    private val userViewModel by viewModels<UserViewModel>()

    private val imageAdapter by lazy { ImageProductAdapter() }
    private val colorAdapter by lazy { ColorAdapter(this) }
    private val phoneVersionAdapter by lazy { PhoneVersionAdapter(this) }
    private val laptopVersionAdapter by lazy { LaptopVersionAdapter(this) }
    private val watchVersionAdapter by lazy { WatchVersionAdapter(this) }

    private var product: Product? = null
    private var startFragment: String? = null
    private var type = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDetailProductBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        lifecycleScope.launchWhenStarted {
            userViewModel.user.collectLatest {
                when (it) {
                    is Resource.Error -> {}
                    is Resource.Loading -> {}
                    is Resource.Success ->{
                        type = it.data!!.type
                    }
                }
            }
        }

        controller = Navigation.findNavController(view)
        product = requireArguments().getSerializable("product") as Product
        startFragment = requireArguments().getString("from")

        if (product!!.category == "Điện thoại") {

        } else if (product!!.category == "Laptop") {

        } else if (product!!.category == "Tai nghe") {
            binding.tvVersion.visibility = View.GONE
            binding.rcvAttribute.visibility = View.GONE
        } else {

        }

        binding.tvProductName.text = product!!.name
        binding.tvDescription.text = product!!.description

        productColorSelected = ProductColor()
        versionSelected = Version()

        imageAdapter.differ.submitList(product!!.images)
        setSlideImage()
        //Show color
        colorAdapter.differ.submitList(product!!.colors)
        setColorAdapter()

        viewModel.getVersion(product!!.id)
        lifecycleScope.launchWhenStarted {
            viewModel.versionList.collectLatest {
                when (it) {
                    is Resource.Error -> {
                        Toast.makeText(requireContext(), "Lỗi lấy dữ liệu", Toast.LENGTH_SHORT).show()
                    }
                    is Resource.Loading -> {}
                    is Resource.Success -> {
                        if (product!!.category == "Điện thoại") {
                            //List version without duplicate item
                            val list: HashSet<Version> = HashSet(it.data!!)
                            val newList: List<Version> = ArrayList(list)
                            phoneVersionAdapter.differ.submitList(newList)
                            setPhoneVersionAdapter()
                        } else if (product!!.category == "Laptop") {
                            val list: HashSet<Version> = HashSet(it.data!!)
                            val newList: List<Version> = ArrayList(list)
                            laptopVersionAdapter.differ.submitList(newList)
                            setLaptopVersionAdapter()
                        } else if (product!!.category == "Đồng hồ") {
                            val list: HashSet<Version> = HashSet(it.data!!)
                            val newList: List<Version> = ArrayList(list)
                            watchVersionAdapter.differ.submitList(newList)
                            setWatchVersionAdapter()
                        }
                    }
                }
            }
        }

        lifecycleScope.launchWhenStarted {
            viewModel.versionDetail.collectLatest {
                when (it) {
                    is Resource.Error -> {}
                    is Resource.Loading -> {}
                    is Resource.Success -> {
                        if (it.data.isNullOrEmpty()){
                            setAddButtonOff()
                        } else{
                            binding.apply {
                                tvPrice.text = Convert.DinhDangTien(it.data[0].price) + " đ"
                                if (it.data[0].quantity == 0) {
                                    setAddButtonOff()
                                } else {
                                    versionSelected = it.data[0]
                                    setAddButtonOn(type, it.data[0].quantity)
                                }
                            }
                        }
                    }
                }
            }
        }

        binding.btnAdd.setOnClickListener{
            cartViewModel.checkProductInCart(getCartProductObj())
        }
        lifecycleScope.launchWhenStarted {
            cartViewModel.addToCart.collectLatest {
                when(it){
                    is Resource.Error -> {
                        binding.btnAdd.revertAnimation()
                        Toast.makeText(requireContext(), it.data, Toast.LENGTH_SHORT).show()
                    }
                    is Resource.Loading -> {
                        binding.btnAdd.startAnimation()
                    }
                    is Resource.Success -> {
                        binding.btnAdd.revertAnimation()
                        Toast.makeText(requireContext(), it.data, Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }
        binding.tvSeeMore.setOnClickListener{

        }
        binding.imgBack.setOnClickListener{
            if (startFragment == "productListFragment") {
                removeFragment()
            } else {
                controller.popBackStack()
            }
        }
    }

    private fun setAddButtonOn(type: String, quantity: Int) {
        binding.tvPrice.visibility = View.VISIBLE
        binding.btnAdd.visibility = View.VISIBLE

        if (type == "admin") {
            binding.btnAdd.text = "Số lượng: " + quantity.toString()
            binding.btnAdd.isEnabled = false
        } else {
            binding.apply {
                btnAdd.text = "Thêm vào giỏ hàng"
                btnAdd.isEnabled = true
            }
        }
    }
    private fun setAddButtonOff() {
        binding.apply {
            tvPrice.text = "Hết hàng!"
            btnAdd.visibility = View.GONE
        }
    }

    private fun setPhoneVersionAdapter() {
        binding.rcvAttribute.apply {
            visibility = View.VISIBLE
            layoutManager = LinearLayoutManager(this@DetailProductFragment.context, LinearLayoutManager.HORIZONTAL, false)
            adapter = phoneVersionAdapter
        }
    }

    private fun setLaptopVersionAdapter() {
        binding.rcvAttribute.apply {
            visibility = View.VISIBLE
            layoutManager = LinearLayoutManager(this@DetailProductFragment.context, LinearLayoutManager.HORIZONTAL, false)
            adapter = laptopVersionAdapter
        }
    }

    private fun setWatchVersionAdapter() {
        binding.rcvAttribute.apply {
            visibility = View.VISIBLE
            layoutManager = LinearLayoutManager(this@DetailProductFragment.context, LinearLayoutManager.HORIZONTAL, false)
            adapter = watchVersionAdapter
        }
    }

    private fun setColorAdapter() {
        binding.rcvColor.apply {
            visibility = View.VISIBLE
            layoutManager = LinearLayoutManager(this@DetailProductFragment.context, LinearLayoutManager.HORIZONTAL, false)
            adapter = colorAdapter
        }
    }

    private fun setSlideImage() {
        binding.apply {
            viewPagerImage.adapter = imageAdapter
            circleIndicator.setViewPager(viewPagerImage)
        }
    }

    override fun onClickColor(productColor: ProductColor) {
        productColorSelected = productColor

        if (product!!.category == "Điện thoại") {
            binding.tvVersion.visibility = View.VISIBLE

            if (versionSelected.id != "0"){
                viewModel.getDetailPhone(
                    product!!.id,
                    productColorSelected.name,
                    versionSelected.ram,
                    versionSelected.storage
                )
            }
        } else if (product!!.category == "Tai nghe") {
            viewModel.getDetailHeadphone(
                product!!.id,
                productColorSelected.name
            )
        } else if (product!!.category == "Laptop"){
            binding.tvVersion.visibility = View.VISIBLE

            if (versionSelected.id != "0"){
                viewModel.getDetailLaptop(
                    product!!.id,
                    productColorSelected.name,
                    versionSelected.cpu,
                    versionSelected.ram,
                    versionSelected.hardDrive
                )
            }
        } else {
            binding.tvVersion.visibility = View.VISIBLE

            if (versionSelected.id != "0"){
                viewModel.getDetailWatch(
                    product!!.id,
                    productColorSelected.name,
                    versionSelected.diameter
                )
            }
        }
    }

    override fun onClickPhone(version: Version) {
        versionSelected = version

        if (productColorSelected.name != "") {
            viewModel.getDetailPhone(
                product!!.id,
                productColorSelected.name,
                version.ram,
                version.storage
            )
        }
    }

    private fun getCartProductObj(): CartProduct {
        return CartProduct(
            versionSelected.id,
            product!!,
            versionSelected,
            1,
            false
        )
    }
    private fun removeFragment() {
        val fragmentManager = requireActivity().supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.remove(this)
        fragmentTransaction.commit()
    }

    override fun onClickLaptop(version: Version) {
        versionSelected = version

        if (productColorSelected.name != "") {
            viewModel.getDetailLaptop(
                product!!.id,
                productColorSelected.name,
                versionSelected.cpu,
                versionSelected.ram,
                versionSelected.hardDrive
            )
        }
    }

    override fun onClickWatch(version: Version) {
        versionSelected = version

        if (productColorSelected.name != "") {
            viewModel.getDetailWatch(
                product!!.id,
                productColorSelected.name,
                versionSelected.diameter
            )
        }
    }
}