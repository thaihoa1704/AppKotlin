package com.example.mymobileapp.model

import java.io.Serializable

data class Order(
    var listProduct: List<CartProduct> = emptyList(),
    var dateTime: Long = 0,
    var contact: String = "",
    var address: String = "",
    var status: String = "",
    var total: Int = 0,
    var rateStar: Int = 0,
    var note: String = ""
) : Serializable {
    constructor() : this(emptyList(), 0, "", "", "", 0, 0, "")
}
