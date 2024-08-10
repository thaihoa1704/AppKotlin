package com.example.mymobileapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.bumptech.glide.Glide
import com.example.mymobileapp.databinding.ItemPriceBinding

class AttributeAdapter(context: Context, objects: List<String>) :
    ArrayAdapter<String?>(context, 0, objects) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return initView(position, convertView, parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return initView(position, convertView, parent)
    }

    private fun initView(position: Int, convertView: View?, parent: ViewGroup): View {
        val attribute = getItem(position)
        val binding = ItemPriceBinding.inflate(LayoutInflater.from(context), parent, false)
        binding.tvPrice.text = attribute
        return binding.root
    }
}
