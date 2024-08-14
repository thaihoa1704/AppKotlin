package com.example.mymobileapp.ui.fragment.manage

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.viewModels
import androidx.lifecycle.lifecycleScope
import androidx.navigation.NavController
import androidx.navigation.Navigation
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.mymobileapp.R
import com.example.mymobileapp.adapter.BrandProductAdapter
import com.example.mymobileapp.adapter.ColorAdapter1
import com.example.mymobileapp.adapter.ImageAdapter1
import com.example.mymobileapp.adapter.VersionAdapter
import com.example.mymobileapp.databinding.FragmentAddProductBinding
import com.example.mymobileapp.listener.ClickItemColorListener
import com.example.mymobileapp.listener.OnClickDeleteColor
import com.example.mymobileapp.listener.OnClickDeleteVersion
import com.example.mymobileapp.model.Brand
import com.example.mymobileapp.model.ProductColor
import com.example.mymobileapp.model.Version
import com.example.mymobileapp.ui.dialog.ColorDialog
import com.example.mymobileapp.ui.dialog.PhoneVersionDialog
import com.example.mymobileapp.util.Resource
import com.example.mymobileapp.viewmodel.ProductViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collectLatest

@Suppress("DEPRECATION")
@AndroidEntryPoint
class AddProductFragment : Fragment(), ColorDialog.GetColor, PhoneVersionDialog.GetVersion, ClickItemColorListener,
    OnClickDeleteColor, OnClickDeleteVersion {
    private lateinit var binding: FragmentAddProductBinding
    private lateinit var controller: NavController
    private lateinit var colorAdapter: ColorAdapter1
    private lateinit var versionAdapter: VersionAdapter
    private lateinit var imageAdapter: ImageAdapter1

    private val productViewModel by viewModels<ProductViewModel>()

    private var brand = Brand()
    private var version = Version()
    private var colorProduct = ProductColor()

    private var listImage = ArrayList<Uri>()
    private var images = ArrayList<String>()
    private var colorList = ArrayList<ProductColor>()
    private var versionList = ArrayList<Version>()

    private val Read_Permission = 101
    private var priceMin = 0
    private var idProduct = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentAddProductBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        controller = Navigation.findNavController(view)

        val category = requireArguments().getString("category")
        val type = requireArguments().getString("type")

        binding.tvCategoryName.text = category
        productViewModel.getBrand(changeName(category.toString()))

        lifecycleScope.launchWhenStarted {
            productViewModel.brandList.collectLatest {
                when(it){
                    is Resource.Error -> {}
                    is Resource.Loading -> {}
                    is Resource.Success -> {
                        setupBrandSpinner(it.data!!)
                    }
                }
            }
        }
        lifecycleScope.launchWhenStarted {
            productViewModel.idProduct.collectLatest {
                when(it){
                    is Resource.Error -> {}
                    is Resource.Loading -> {}
                    is Resource.Success -> {
                        idProduct = it.data!!
                        saveVersion(idProduct)
                    }
                }
            }
        }
        lifecycleScope.launchWhenStarted {
            productViewModel.message.collectLatest {
                when(it){
                    is Resource.Error -> {}
                    is Resource.Loading -> {}
                    is Resource.Success -> {
                        if (it.data == "Success Add Version"){
                            Toast.makeText(requireContext(), "Thêm sản phẩm thành công", Toast.LENGTH_SHORT).show()
                            Handler().postDelayed({
                                removeFragment()
                            }, 3000)
                        }
                    }
                }
            }
        }
        lifecycleScope.launchWhenStarted {
            productViewModel.imageList.collectLatest {
                when(it){
                    is Resource.Error -> {}
                    is Resource.Loading -> {}
                    is Resource.Success -> {
                        images = (it.data as ArrayList<String>?)!!
                        //Toast.makeText(requireContext(), images.size.toString(), Toast.LENGTH_SHORT).show()
                        if (images.size == listImage.size){
                            saveProduct(images)
                        }
                    }
                }
            }
        }

        setupImageRecycleView()
        setColorRecycleView()
        setVersionRecycleView()

        binding.imgAddImage.setOnClickListener {
            if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE)
                != android.content.pm.PackageManager.PERMISSION_GRANTED){
                ActivityCompat.requestPermissions(requireActivity(), arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), Read_Permission)
            }
            val intent = Intent()
            intent.type = "image/*"
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "Select Picture"), 1);
        }

        binding.imgAddColor.setOnClickListener {
            val colorDialog = ColorDialog()
            colorDialog.show(childFragmentManager, "ColorDialog")
        }
        binding.imgAddVersion.setOnClickListener {
            val versionDialog = PhoneVersionDialog(colorList)
            versionDialog.show(childFragmentManager, "VersionDialog")
        }
        binding.btnAddProduct.setOnClickListener {
            if (checkProduct()){
                uploadImages()
            } else {
                return@setOnClickListener
            }
        }
        binding.imgBack.setOnClickListener {
            removeFragment()
        }
    }

    private fun setupImageRecycleView() {
        imageAdapter = ImageAdapter1(listImage, requireContext())
        binding.rcvImage.apply {
            visibility = View.VISIBLE
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(this@AddProductFragment.context,LinearLayoutManager.HORIZONTAL, false)
            adapter = imageAdapter
        }
    }

    @Deprecated("Deprecated in Java")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == RESULT_OK){
            if (data?.clipData != null){
                val count = data.clipData!!.itemCount
                (0 until count).forEach {
                    val imageUri = data.clipData!!.getItemAt(it).uri
                    listImage.add(imageUri)
                }
                imageAdapter.notifyDataSetChanged()
            } else if (data?.data != null){
                val imageUri = data.data!!
                listImage.add(imageUri)
            }
            imageAdapter.notifyDataSetChanged()
        } else {
            Toast.makeText(requireContext(), "Không chọn ảnh nào", Toast.LENGTH_SHORT).show()
        }
    }
    private fun setColorRecycleView() {
        colorAdapter = ColorAdapter1(colorList, requireContext(), this, this)
        binding.rcvColor.apply {
            visibility = View.VISIBLE
            layoutManager = LinearLayoutManager(this@AddProductFragment.context, LinearLayoutManager.HORIZONTAL, false)
            adapter = colorAdapter
        }
    }
    private fun setVersionRecycleView() {
        versionAdapter = VersionAdapter(versionList, requireContext(), this)
        binding.rcvVersion.apply {
            visibility = View.VISIBLE
            layoutManager = GridLayoutManager(this@AddProductFragment.context, 2)
            adapter = versionAdapter
        }
    }

    private fun setupBrandSpinner(list: List<Brand>) {
        val adapter = BrandProductAdapter(requireContext(), list)
        binding.spinnerBrand.adapter = adapter
        binding.spinnerBrand.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                brand = p0!!.getItemAtPosition(p2) as Brand
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun changeName(string: String): String {
        val name = when (string) {
            "Điện thoại" -> "Phone"
            "Laptop" -> "Laptop"
            "Đồng hồ" -> "Watch"
            "Tai nghe" -> "Headphone"
            else -> "Accessory"
        }
        return name.trim { it <= ' ' }
    }
    override fun getData(
        name: String,
        colorCode: Int
    ){
        colorProduct = ProductColor(name, "#${Integer.toHexString(colorCode)}")
        colorList.add(this.colorProduct)
        colorAdapter.notifyDataSetChanged()
    }

    override fun onClickColor(productColor: ProductColor) {
        binding.tvColorName.text = productColor.name
    }

    override fun getData(colorName: String, ram: String, storage: String, price: Int) {
        val idVersion = colorName + ram + storage

        if (versionList.isEmpty()){
            version = Version(id = idVersion, color = colorName, ram = ram, storage = storage, price = price/1000)
            versionList.add(version)
            versionAdapter.notifyDataSetChanged()
        } else {
            var count = 0
            for (i in versionList) {
                if (i.id == idVersion) {
                    count++
                }
            }
            if (count == 0) {
                version = Version(id = idVersion, color = colorName, ram = ram, storage = storage, price = price/1000)
                versionList.add(version)
                versionAdapter.notifyDataSetChanged()
            } else {
                Toast.makeText(requireContext(), "Phiên bản đã tồn tại", Toast.LENGTH_SHORT).show()
            }
        }
    }
    private fun checkProduct(): Boolean {
        val name = binding.edtName.text.toString()
        if (name == "") {
            Toast.makeText(requireContext(), "Vui lòng nhập tên sản phẩm", Toast.LENGTH_SHORT).show()
            return false
        }
        if (listImage.isEmpty()) {
            Toast.makeText(requireContext(), "Vui lòng chọn ảnh cho sản phẩm", Toast.LENGTH_SHORT).show()
            return false
        }
        if (colorList.isEmpty()) {
            Toast.makeText(requireContext(), "Vui lòng chọn màu sắc cho sản phẩm", Toast.LENGTH_SHORT).show()
            return false
        }
        val description = binding.edtDescription.text.toString()
        if (description == "") {
            Toast.makeText(requireContext(), "Vui lòng nhập mô tả sản phẩm", Toast.LENGTH_SHORT).show()
            return false
        }
        if (versionList.isEmpty()) {
            Toast.makeText(requireContext(), "Vui lòng thêm phiên bản cho sản phẩm", Toast.LENGTH_SHORT).show()
            return false
        }
        return true
    }
    private fun uploadImages(){
        productViewModel.uploadImages(imagesUrl = listImage, images = images)
    }
    private fun saveProduct(images: ArrayList<String>){
        priceMin = versionList[0].price
        for (i in versionList){
            if (i.price < priceMin){
                priceMin = i.price
            }
        }

        val name = binding.edtName.text.toString()
        val brand = brand.name
        val category = requireArguments().getString("category")
        val colors = colorList
        val description = binding.edtDescription.text.toString()
        val price = priceMin

        productViewModel.saveProduct(name = name, images = images, brand = brand,
                      category = category!!, colors = colors, description = description, price = price)
    }
    private fun saveVersion(idProduct: String){
        if (versionList.isNotEmpty()){
            productViewModel.saveVersion(idProduct, versionList)
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
        versionList.removeAt(position)
        versionAdapter.notifyItemRemoved(position)
    }
    private fun removeFragment() {
        val fragmentManager = requireActivity().supportFragmentManager
        val fragmentTransaction = fragmentManager.beginTransaction()
        fragmentTransaction.remove(this)
        fragmentTransaction.commit()
    }
}