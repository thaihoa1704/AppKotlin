package com.example.mymobileapp.ui.dialog

import android.app.Dialog
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.widget.AdapterView
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import com.example.mymobileapp.adapter.AttributeAdapter
import com.example.mymobileapp.adapter.BrandProductAdapter
import com.example.mymobileapp.adapter.ColorProductAdapter
import com.example.mymobileapp.databinding.PhoneVersionDialogBinding
import com.example.mymobileapp.model.Brand
import com.example.mymobileapp.model.ProductColor

class PhoneVersionDialog(private val colors: List<ProductColor>) : DialogFragment() {
    private lateinit var binding: PhoneVersionDialogBinding
    private var listColor = colors
    private var productColor = ProductColor()
    private var ram = ""
    private var storage = ""
    private var price = 0
    private var colorName = ""

    interface GetVersion {
        fun getData(colorName: String, ram: String, storage: String, price: Int)
    }
    private lateinit var getVersion: GetVersion

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = Dialog(requireActivity())
        binding = PhoneVersionDialogBinding.inflate(layoutInflater)
        dialog.setContentView(binding.root)

        val window = dialog.window
        if (window != null) {
            window.setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.WRAP_CONTENT
            )
            window.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        }
        setupColorSpinner(listColor)
        setupRamSpinner(getListRam())
        setupStorageSpinner(getListStorage())

        binding.btnNo.setOnClickListener { dialog.dismiss() }
        binding.btnYes.setOnClickListener {
            var stringPrice = binding.edtPrice.text.toString()
            if (ram == "" || storage == "" || stringPrice == "" || productColor.name == "") {
                Toast.makeText(requireContext(), "Vui lòng nhập đủ thông tin", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            if (ram != "" && storage != "" && stringPrice != "" && productColor.name != "") {
                price = stringPrice.toInt()
                getVersion.getData(productColor.name, ram, storage, price)
            }
            dialog.dismiss()
        }
        return dialog
    }
    private fun setupColorSpinner(list: List<ProductColor>) {
        val adapter = ColorProductAdapter(requireContext(), list)
        binding.spinnerColor.adapter = adapter
        binding.spinnerColor.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                productColor = p0!!.getItemAtPosition(p2) as ProductColor
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }
    }
    private fun setupRamSpinner(list: List<String>) {
        val adapter = AttributeAdapter(requireContext(), list)
        binding.spinnerRam.adapter = adapter
        binding.spinnerRam.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                ram = p0!!.getItemAtPosition(p2) as String
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }
    }
    private fun setupStorageSpinner(list: List<String>) {
        val adapter = AttributeAdapter(requireContext(), list)
        binding.spinnerStorage.adapter = adapter
        binding.spinnerStorage.onItemSelectedListener = object: AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                storage = p0!!.getItemAtPosition(p2) as String
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }
    }
    private fun getListRam(): List<String> {
        val list: MutableList<String> = ArrayList()
        list.add("4GB")
        list.add("6GB")
        list.add("8GB")
        list.add("12GB")
        list.add("16GB")
        return list
    }
    private fun getListStorage(): List<String> {
        val list: MutableList<String> = ArrayList()
        list.add("32GB")
        list.add("64GB")
        list.add("128GB")
        list.add("256GB")
        list.add("512GB")
        list.add("1TB")
        return list
    }
    override fun onAttach(context: Context) {
        super.onAttach(context)
        getVersion = (parentFragment as GetVersion?)!!
    }
}