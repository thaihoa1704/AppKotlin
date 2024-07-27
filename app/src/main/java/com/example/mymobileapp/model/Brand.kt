package com.example.mymobileapp.model

data class Brand(
    var name: String = "",
    var logo: String = ""
) {
    constructor(): this("", "")
}
