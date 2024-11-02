package com.example.mymobileapp.listener

interface ChangeQuantityCartProduct {
    fun incrementQuantity(documentId: String, productId: String, quantity: Int)
    fun decrementQuantity(documentId: String)
    fun deleteProduct(documentId: String)
}
