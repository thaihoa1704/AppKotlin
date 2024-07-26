package com.example.mymobileapp.model

 data class ProductColor(
     var id: String = "",
     var color: String = "",
     var colorCode: String = ""
 ){
    constructor(): this("", "", "")
}
