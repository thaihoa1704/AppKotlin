package com.example.mymobileapp.model

data class ProductCount(
    val cartProduct: CartProduct,
    val quantity: Int
) {
    constructor() : this(CartProduct(), 0)
}