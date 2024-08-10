package com.example.mymobileapp.model

import java.io.Serializable

data class Temp(
    var list: List<ProductCount>
) : Serializable {
    constructor(): this(emptyList())
}