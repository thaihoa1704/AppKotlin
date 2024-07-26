package com.example.mymobileapp.viewmodel

import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mymobileapp.model.Product
import com.example.mymobileapp.util.Resource
import com.example.mymobileapp.util.constans.PRODUCT_COLLECTION
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class ProductListViewModel @Inject constructor(
    private val db: FirebaseFirestore
): ViewModel(){
    private val _productList = MutableStateFlow<Resource<List<Product>>>(Resource.Loading())
    val productList: StateFlow<Resource<List<Product>>> = _productList
    //val productList = _productList.asStateFlow()

    fun getProducts(category: String){
        db.collection(PRODUCT_COLLECTION).whereEqualTo("category", category)
            .addSnapshotListener { value, error ->
                if (error != null || value == null) {
                    viewModelScope.launch {
                        _productList.emit(Resource.Error(error?.message.toString()))
                    }
                } else {
                    val list = value.toObjects(Product::class.java)
                    viewModelScope.launch {
                        _productList.emit(Resource.Success(list))
                    }
                }
            }
    }
    fun getAllProduct(){
        db.collection(PRODUCT_COLLECTION)
            .addSnapshotListener { value, error ->
                if (error != null || value == null) {
                    viewModelScope.launch {
                        _productList.emit(Resource.Error(error?.message.toString()))
                    }
                } else {
                    val list = value.toObjects(Product::class.java)
                    viewModelScope.launch {
                        _productList.emit(Resource.Success(list))
                    }
                }
            }
    }
}