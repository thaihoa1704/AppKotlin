package com.example.mymobileapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mymobileapp.helper.ComparatorPrice
import com.example.mymobileapp.model.Brand
import com.example.mymobileapp.model.Price
import com.example.mymobileapp.model.Product
import com.example.mymobileapp.util.Resource
import com.example.mymobileapp.util.constants.BRAND_COLLECTION
import com.example.mymobileapp.util.constants.CATEGORY_COLLECTION
import com.example.mymobileapp.util.constants.PRICE_COLLECTION
import com.example.mymobileapp.util.constants.PRODUCT_COLLECTION
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class FilterViewModel @Inject constructor(
    private val db: FirebaseFirestore
): ViewModel(){
    private val _productList = MutableStateFlow<Resource<List<Product>>>(Resource.Loading())
    val productList: StateFlow<Resource<List<Product>>> = _productList

    private var randomList = emptyList<Product>()

    private val _brandList = MutableStateFlow<Resource<List<Brand>>>(Resource.Loading())
    val brandList: StateFlow<Resource<List<Brand>>> = _brandList

    private val _priceList = MutableStateFlow<Resource<List<Price>>>(Resource.Loading())
    val priceList: StateFlow<Resource<List<Price>>> = _priceList

    fun ascending(){
        val list = productList.value.data
        val ascendingList = list!!.sortedWith(ComparatorPrice.ascending)
        if (ascendingList.isEmpty()) {
            viewModelScope.launch {
                _productList.emit(Resource.Error("Không có sản phẩm nào"))
            }
        } else {
            viewModelScope.launch {
                _productList.emit(Resource.Success(ascendingList))
            }
        }
    }
    fun descending(){
        val list = productList.value.data
        val descendingList = list!!.sortedWith(ComparatorPrice.descending)
        if (descendingList.isEmpty()) {
            viewModelScope.launch {
                _productList.emit(Resource.Error("Không có sản phẩm nào"))
            }
        } else {
            viewModelScope.launch {
                _productList.emit(Resource.Success(descendingList))
            }
        }
    }
    fun random() {
        viewModelScope.launch {
            _productList.emit(Resource.Success(randomList))
        }
    }
    fun getAllProduct(category: String){
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
    fun getBrandList(category: String){
        db.collection(CATEGORY_COLLECTION).document(category)
            .collection(BRAND_COLLECTION)
            .orderBy("name")
            .get().addOnSuccessListener {
                val list = it.toObjects(Brand::class.java)
                viewModelScope.launch {
                    _brandList.emit(Resource.Success(list))
                }
            }.addOnFailureListener {
                viewModelScope.launch {
                    _brandList.emit(Resource.Error(it.message.toString()))
                }
            }
    }
    fun getPriceList(category: String){
        db.collection(CATEGORY_COLLECTION).document(category)
            .collection(PRICE_COLLECTION)
            .orderBy("min")
            .get().addOnSuccessListener {
                val list = it.toObjects(Price::class.java)
                viewModelScope.launch {
                    _priceList.emit(Resource.Success(list))
                }
            }.addOnFailureListener {
                viewModelScope.launch {
                    _priceList.emit(Resource.Error(it.message.toString()))
                }
            }
    }
    fun findProduct(category: String, brandSelected: Brand, priceSelected: Price){
        val query = db.collection(PRODUCT_COLLECTION)
            .whereEqualTo("category", category)
            .whereEqualTo("brand", brandSelected.name)
            .whereGreaterThanOrEqualTo("price", priceSelected.min)
            .whereLessThanOrEqualTo("price", priceSelected.max)
        val queryBrand = db.collection(PRODUCT_COLLECTION)
            .whereEqualTo("category", category)
            .whereEqualTo("brand", brandSelected.name)
        val queryPrice = db.collection(PRODUCT_COLLECTION)
            .whereGreaterThanOrEqualTo("price", priceSelected.min)
            .whereLessThanOrEqualTo("price", priceSelected.max)
        val querySelected = if (brandSelected.name != "" && priceSelected.price != "") {
            query
        } else if (brandSelected.name != "") {
            queryBrand
        } else {
            queryPrice
        }
        querySelected.get().addOnSuccessListener {
            val list = it.toObjects(Product::class.java)
            randomList = list
            viewModelScope.launch {
                _productList.emit(Resource.Success(list))
            }
        }.addOnFailureListener {
            viewModelScope.launch {
                _productList.emit(Resource.Error(it.message.toString()))
            }
        }
    }
}