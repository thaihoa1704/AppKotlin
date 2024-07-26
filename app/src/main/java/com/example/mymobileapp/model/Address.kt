package com.example.mymobileapp.model

import java.io.Serializable

data class Address (
    var id: String = "",
    var string: String = "",
    var select: Boolean = false
): Serializable{
    constructor(): this("", "", false)
}
