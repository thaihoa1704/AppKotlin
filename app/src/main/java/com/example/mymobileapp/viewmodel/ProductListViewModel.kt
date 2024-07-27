package com.example.mymobileapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mymobileapp.model.Product
import com.example.mymobileapp.util.Resource
import com.example.mymobileapp.util.constants.PRODUCT_COLLECTION
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProductListViewModel @Inject constructor(
    private val db: FirebaseFirestore
): ViewModel(){
    private val _productList = MutableStateFlow<Resource<List<Product>>>(Resource.Loading())
    val productList: StateFlow<Resource<List<Product>>> = _productList

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