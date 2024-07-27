package com.example.mymobileapp.listener

import com.example.mymobileapp.model.Price

interface ClickItemPriceListener {
    fun onPriceClick(price: Price, position: Int)
}
