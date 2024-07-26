package com.example.mymobileapp.model

data class CartProduct(
    var id: String,
    var product: Product,
    var version: Version,
    var quantity: Int,
    var select: Boolean?
){
    constructor(): this("", Product(), Version(), 0, null)
}
