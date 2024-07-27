package com.example.mymobileapp.model

data class LaptopVersion(
    var id: String,
    var color: String,
    var hardDrive: String = "",
    var cpu: String = "",
    var price: Int,
    var quantity: Int
) {
    constructor(): this("", "", "", "", 0, 0)
}