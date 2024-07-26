package com.example.mymobileapp.model

import java.io.Serializable

data class Product (
    var id: String = "",
    var name: String = "",
    var images: List<String> =  emptyList(),
    var price: Int = 0,
    var category: String = "",
    var brand: String = "",
    var description: String = "",
    var colors: List<ProductColor> = emptyList(),
    var isSpecial: Boolean = false
) : Serializable {
    constructor(): this("", "", emptyList(), 0, "", "", "", emptyList(), false)
}
