package com.example.mymobileapp.adapter

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.example.mymobileapp.databinding.ItemColorProductBinding
import com.example.mymobileapp.model.ProductColor

class ColorProductAdapter(context: Context, objects: List<ProductColor>) :
    ArrayAdapter<ProductColor?>(context, 0, objects) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return initView(position, convertView, parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return initView(position, convertView, parent)
    }

    private fun initView(position: Int, convertView: View?, parent: ViewGroup): View {
        val colorProduct = getItem(position)
        val binding = ItemColorProductBinding.inflate(LayoutInflater.from(context), parent, false)
        binding.tvColor.text = colorProduct!!.name
        binding.colorCard.setCardBackgroundColor(Color.parseColor(colorProduct.colorCode))
        return binding.root
    }
}
