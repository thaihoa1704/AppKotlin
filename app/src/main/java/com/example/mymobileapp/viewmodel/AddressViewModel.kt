package com.example.mymobileapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mymobileapp.model.Address
import com.example.mymobileapp.util.Resource
import com.example.mymobileapp.util.constants.ADDRESS_COLLECTION
import com.example.mymobileapp.util.constants.USER_COLLECTION
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AddressViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val db: FirebaseFirestore
): ViewModel() {
    private val _addressList = MutableStateFlow<Resource<List<Address>>>(Resource.Loading())
    val addressList: StateFlow<Resource<List<Address>>> = _addressList

    init {
        getAddress()
    }

    private fun getAddress() {
        db.collection(USER_COLLECTION).document(firebaseAuth.uid!!).collection(ADDRESS_COLLECTION)
            .addSnapshotListener { value, error ->
                if (error != null || value == null) {
                    viewModelScope.launch {
                        _addressList.emit(Resource.Error(error?.message.toString()))
                    }
                } else {
                    val list = value.toObjects(Address::class.java)
                    viewModelScope.launch {
                        _addressList.emit(Resource.Success(list))
                    }
                }
            }
    }
    fun selectAddress(address: Address){
        db.collection(USER_COLLECTION).document(firebaseAuth.uid!!)
            .collection(ADDRESS_COLLECTION).document(address.id)
            .update("select", true)
            .addOnCompleteListener { }
            .addOnFailureListener { }

        db.collection(USER_COLLECTION).document(firebaseAuth.uid!!)
            .collection(ADDRESS_COLLECTION)
            .whereNotEqualTo("id", address.id)
            .get().addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    val list: MutableList<String> = ArrayList()
                    for (queryDocumentSnapshot in task.result) {
                        list.add(queryDocumentSnapshot.id)
                    }
                    updateData(list)
                }
            }
    }
    private fun updateData(list: List<String>) {
        val batch = db.batch()
        for (i in list.indices) {
            val document: DocumentReference = db.collection(USER_COLLECTION).document(firebaseAuth.uid!!)
                .collection(ADDRESS_COLLECTION).document(list[i])
            batch.update(document, "select", false)
        }
        batch.commit().addOnCompleteListener { }
    }
}