package com.example.mymobileapp.model

data class Banner(
    var str: List<String> =  emptyList()
) {
    constructor(): this(emptyList())
}