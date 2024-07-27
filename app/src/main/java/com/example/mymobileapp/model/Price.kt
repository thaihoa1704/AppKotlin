package com.example.mymobileapp.model

class Price(
    var price: String = "",
    var min: Int = 0,
    var max: Int = 0
) {
    constructor(): this("", 0, 0)
}
