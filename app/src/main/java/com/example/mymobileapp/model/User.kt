package com.example.mymobileapp.model

import android.text.TextUtils
import android.util.Patterns
import java.io.Serializable

data class User(
    var id: String = "",
    var name: String = "",
    var email: String = "",
    var phone: String = "",
    var password: String = "",
    var type: String = ""
) : Serializable {
    constructor(): this("", "", "", "", "", "")

    fun validateEmail(): Boolean {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }

    fun validatePassword(): Boolean {
        return !TextUtils.isEmpty(password) && password!!.length >= 6
    }
}