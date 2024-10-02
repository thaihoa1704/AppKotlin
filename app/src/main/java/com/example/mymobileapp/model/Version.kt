package com.example.mymobileapp.model

import java.util.Objects

data class Version(
    var id: String = "",
    var color: String = "",
    var storage: String = "",
    var ram: String = "",
    var hardDrive: String = "",
    var cpu: String = "",
    var isBluetooth: Boolean = false,
    var hpType: String = "",
    //var accessoryType: String = "",
    var diameter: Int = 0,
    var price: Int = 0,
    var quantity: Int = 0
) {
    constructor(): this("0", "", "", "", "", "", false, "", 0, 0, 0)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Version

        if (ram != other.ram) return false
        if (storage != other.storage) return false
        if (hardDrive != other.hardDrive) return false
        if (cpu != other.cpu) return false
        if (isBluetooth != other.isBluetooth) return false
        if (hpType != other.hpType) return false
        //if (accessoryType != other.accessoryType) return false
        if (diameter != other.diameter) return false

        return true
    }

    override fun hashCode(): Int {
        return Objects.hash(ram, storage, hardDrive, cpu, isBluetooth, hpType, diameter)
    }
}
