package com.example.mymobileapp.listener

interface ChangeQuantityCartProduct {
    fun incrementQuantity(documentId: String)
    fun decrementQuantity(documentId: String)
    fun deleteProduct(documentId: String)
}
