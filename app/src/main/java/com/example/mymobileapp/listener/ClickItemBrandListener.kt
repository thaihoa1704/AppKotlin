package com.example.mymobileapp.listener

import com.example.mymobileapp.model.Brand

interface ClickItemBrandListener {
    fun onClickItemBrand(brand: Brand, position: Int)
}
