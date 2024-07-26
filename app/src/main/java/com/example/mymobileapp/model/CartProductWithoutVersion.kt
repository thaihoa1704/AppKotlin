package com.example.mymobileapp.model

data class CartProductWithoutVersion(
    open var product: Product? = null,
    open var quantity: Int = 0,
    open var isSelect: Boolean = false
) {
    constructor(): this(null, 0, false)
}
