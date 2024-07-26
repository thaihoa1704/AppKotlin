package com.example.mymobileapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mymobileapp.model.PhoneVersion
import com.example.mymobileapp.model.Product
import com.example.mymobileapp.model.User
import com.example.mymobileapp.model.Version
import com.example.mymobileapp.util.Resource
import com.example.mymobileapp.util.constans.PRODUCT_COLLECTION
import com.example.mymobileapp.util.constans.VERSION_COLLECTION
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@Suppress("UNCHECKED_CAST")
@HiltViewModel
class ProductViewModel @Inject constructor(
    private val db: FirebaseFirestore
): ViewModel(){
    private val _versionList = MutableStateFlow<Resource<List<Version>>>(Resource.Loading())
    val versionList: StateFlow<Resource<List<Version>>> = _versionList

    private val _versionDetail = MutableStateFlow<Resource<List<Version>>>(Resource.Loading())
    val versionDetail: StateFlow<Resource<List<Version>>> = _versionDetail

    fun getVersion(idProduct: String){
        db.collection(PRODUCT_COLLECTION).document(idProduct).collection(VERSION_COLLECTION)
            .get().addOnSuccessListener {result ->
                val list = result.toObjects(Version::class.java)
                viewModelScope.launch {
                    _versionList.emit(Resource.Success(list))
                }
            }.addOnFailureListener {
                viewModelScope.launch {
                    _versionList.emit(Resource.Error(it.message.toString()))
                }
            }
    }
    fun getDetailPhone(idProduct: String, color: String, ram: String, storage: String) {
        db.collection(PRODUCT_COLLECTION).document(idProduct).collection(VERSION_COLLECTION)
            .whereEqualTo("color", color)
            .whereEqualTo("ram", ram)
            .whereEqualTo("storage", storage)
            .get().addOnSuccessListener {result ->
                val list = result.toObjects(Version::class.java)
                viewModelScope.launch {
                    _versionDetail.emit(Resource.Success(list))
                }
            }.addOnFailureListener {
                viewModelScope.launch {
                    _versionDetail.emit(Resource.Error(it.message.toString()))
                }
            }
    }
}