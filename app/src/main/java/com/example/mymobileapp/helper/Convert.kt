package com.example.mymobileapp.helper

import com.google.type.DateTime
import java.text.NumberFormat
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object Convert {
    //Hàm đổi từ số sang chữ có định dạng
    fun DinhDangTien(tien: Int): String {
        return NumberFormat.getNumberInstance().format((tien * 1000).toLong())
    }

    //Hàm chuyển từ chữ sang số để tính toán
    fun ChuyenTien(tien: String): Int {
        try {
            return NumberFormat.getNumberInstance().parse(tien)!!.toInt()
        } catch (_: ParseException) {
        }
        return 0
    }

    //Hàm chuyển timestamp từ firestore sang ngày tháng năm
    fun getDateTime(timestamp: Long): String {
        try {
            val newDate = (Date(timestamp))
            val sfd = SimpleDateFormat("dd-MM-yyyy HH:mm:ss", Locale.getDefault())
            return sfd.format(newDate)
        } catch (e: Exception) {
            return "date"
        }
    }
}
