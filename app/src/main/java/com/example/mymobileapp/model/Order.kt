package com.example.mymobileapp.model

import java.io.Serializable

data class Order(
    var listProduct: List<CartProduct>? = null,
    var dateTime: Long = 0,
    var contact: String? = null,
    var address: String? = null,
    var status: String? = null,
    var total: Int = 0,
    var rateStar: Int = 0,
    var note: String? = null
) : Serializable {
    constructor(): this(null, 0, null, null, null, 0, 0, null)
}
