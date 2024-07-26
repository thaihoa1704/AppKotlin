package com.example.mymobileapp.model

import java.util.Objects

class PhoneVersion(
    var id: String,
    var color: String,
    var ram: String,
    var storage: String,
    var price: Int,
    var quantity: Int
) {
    constructor(): this("", "", "", "", 0, 0)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other == null || javaClass != other.javaClass) return false
        val version = other as PhoneVersion
        return ram == version.ram && storage == version.storage
    }

    override fun hashCode(): Int {
        return Objects.hash(ram, storage)
    }
}
