package com.example.mymobileapp.ui.fragment.manage

import android.net.Uri
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
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.example.mymobileapp.R
import com.example.mymobileapp.adapter.ColorAdapter1
import com.example.mymobileapp.adapter.ImageAdapter1
import com.example.mymobileapp.adapter.ImageAdapter2
import com.example.mymobileapp.adapter.VersionAdapter
import com.example.mymobileapp.databinding.FragmentEditProductBinding
import com.example.mymobileapp.listener.ClickItemColorListener
import com.example.mymobileapp.listener.OnClickDeleteColor
import com.example.mymobileapp.listener.OnClickDeleteVersion
import com.example.mymobileapp.model.Product
import com.example.mymobileapp.model.ProductColor
import com.example.mymobileapp.model.Version
import com.example.mymobileapp.util.Resource
import com.example.mymobileapp.viewmodel.ProductViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@Suppress("DEPRECATION")
@AndroidEntryPoint
class EditProductFragment : Fragment(), OnClickDeleteColor, OnClickDeleteVersion,
    ClickItemColorListener {
    private lateinit var binding: FragmentEditProductBinding
    private val viewModel by viewModels<ProductViewModel>()
    private var product =  Product()
    private var versionList = ArrayList<Version>()
    private lateinit var imageAdapter: ImageAdapter2
    private lateinit var colorAdapter: ColorAdapter1
    private lateinit var versionAdapter: VersionAdapter
    private lateinit var controller: NavController
    private var colorList = ArrayList<ProductColor>()
    private var listImage = ArrayList<Uri>()
    private var images = ArrayList<String>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditProductBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        controller = Navigation.findNavController(view)

        product = requireArguments().getSerializable("product") as Product
        val category = requireArguments().getString("category")
        val type = requireArguments().getString("type")

        viewModel.getVersion(product.id)
        lifecycleScope.launchWhenStarted {
            viewModel.versionList.collectLatest {
                when (it) {
                    is Resource.Error -> {
                        Toast.makeText(requireContext(), "Lỗi lấy dữ liệu", Toast.LENGTH_SHORT).show()
                    }
                    is Resource.Loading -> {}
                    is Resource.Success -> {
                        if (product.category == "Điện thoại") {
                            //List version without duplicate item
                            val list: HashSet<Version> = HashSet(it.data!!)
                            versionList = ArrayList(list)
                        }
                        setVersionRecycleView(versionList)
                    }
                }
            }
        }

        binding.tvCategoryName.text = product.category
        binding.edtName.setText(product.name)
        binding.tvBrandName.text = product.brand
        colorList = product.colors as ArrayList<ProductColor>
        images = product.images as ArrayList<String>
        binding.edtDescription.setText(product.description)
        setupImageRecycleView(images)
        setColorRecycleView(colorList)

        binding.imgBack.setOnClickListener{
            removeFragment()
        }
    }
    private fun setupImageRecycleView(listImage: ArrayList<String>) {
        imageAdapter = ImageAdapter2(listImage, requireContext())
        binding.rcvImage.apply {
            visibility = View.VISIBLE
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@EditProductFragment.context,LinearLayoutManager.HORIZONTAL, false)
            adapter = imageAdapter
        }
    }
    private fun setColorRecycleView(colorList: ArrayList<ProductColor>) {
        colorAdapter = ColorAdapter1(colorList, requireContext(), this, this)
        binding.rcvColor.apply {
            visibility = View.VISIBLE
            layoutManager = LinearLayoutManager(this@EditProductFragment.context, LinearLayoutManager.HORIZONTAL, false)
            adapter = colorAdapter
        }
    }
    private fun setVersionRecycleView(versionList: ArrayList<Version>) {
        versionAdapter = VersionAdapter(versionList, requireContext(), this)
        binding.rcvVersion.apply {
            visibility = View.VISIBLE
            layoutManager = GridLayoutManager(this@EditProductFragment.context, 3)
            adapter = versionAdapter
        }
    }

    override fun onClickDeleteColor(position: Int) {
        val colorName = colorList[position].name
        var quantity = 0
        for (i in versionList){
            if (i.color == colorName){
                quantity++
            }
        }
        if (quantity != 0){
            Toast.makeText(requireContext(), "Không thể xóa màu này", Toast.LENGTH_SHORT).show()
        }else {
            colorList.removeAt(position)
            colorAdapter.notifyItemRemoved(position)
        }
    }

    override fun onClickDeleteVersion(position: Int) {

    }

    override fun onClickColor(productColor: ProductColor) {
        binding.tvColorName.text = productColor.name
    }
    private fun removeFragment() {
        val fragmentManager = requireActivity().supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.remove(this)
        fragmentTransaction.commit()
    }
}