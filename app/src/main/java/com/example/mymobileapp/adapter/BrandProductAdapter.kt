package com.example.mymobileapp.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import com.bumptech.glide.Glide
import com.example.mymobileapp.databinding.ItemBrandBinding
import com.example.mymobileapp.model.Brand

class BrandProductAdapter(context: Context, objects: List<Brand>) :
    ArrayAdapter<Brand?>(context, 0, objects) {
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        return initView(position, convertView, parent)
    }

    override fun getDropDownView(position: Int, convertView: View?, parent: ViewGroup): View {
        return initView(position, convertView, parent)
    }

    private fun initView(position: Int, convertView: View?, parent: ViewGroup): View {
        val brand = getItem(position)
        val binding = ItemBrandBinding.inflate(LayoutInflater.from(context), parent, false)
        Glide.with(context).load(brand!!.logo).into(binding.image)
        return binding.root
    }
}
