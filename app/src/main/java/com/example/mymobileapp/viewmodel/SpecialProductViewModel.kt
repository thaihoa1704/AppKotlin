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
class SpecialProductViewModel @Inject constructor(
    private val db: FirebaseFirestore
): ViewModel(){
    private val _specialProducts = MutableStateFlow<Resource<List<Product>>>(Resource.Loading())
    val specialProducts: StateFlow<Resource<List<Product>>> = _specialProducts
    //val specialProducts = _specialProducts.asStateFlow()
    init {
        getSpecialProducts()
    }

    private fun getSpecialProducts(){
        db.collection(PRODUCT_COLLECTION).whereEqualTo("special", true)
            .addSnapshotListener { value, error ->
                if (error != null || value == null) {
                    viewModelScope.launch {
                        _specialProducts.emit(Resource.Error(error?.message.toString()))
                    }
                } else {
                    val list = value.toObjects(Product::class.java)
                    viewModelScope.launch {
                        _specialProducts.emit(Resource.Success(list))
                    }
                }
            }
    }
}
