package com.example.mymobileapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mymobileapp.model.User
import com.example.mymobileapp.util.Resource
import com.example.mymobileapp.util.constants.USER_COLLECTION
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val db: FirebaseFirestore
): ViewModel() {
    private val _login = MutableSharedFlow<Resource<FirebaseUser>>()
    val login = _login.asSharedFlow()

    private val _user = MutableStateFlow<Resource<User>>(Resource.Loading())
    val user = _user.asStateFlow()

    private fun getUser(){
        if(firebaseAuth.uid == null){
            viewModelScope.launch {
                _user.emit(Resource.Error("User not found"))
            }
        } else{
            db.collection(USER_COLLECTION).document(firebaseAuth.uid!!).get()
                .addOnSuccessListener {
                    viewModelScope.launch {
                        val user = it.toObject(User::class.java)
                        user?.let {
                            _user.emit(Resource.Success(it))
                        }
                    }
                }.addOnFailureListener{
                    viewModelScope.launch {
                        _user.emit(Resource.Error(it.message.toString()))
                    }
                }
        }
    }
    fun userLogin(email: String, password: String){
        viewModelScope.launch {
            _login.emit(Resource.Loading())
        }
        firebaseAuth.signInWithEmailAndPassword(email, password)
            .addOnSuccessListener {
                viewModelScope.launch {
                    it.user?.let {
                        _login.emit(Resource.Success(it))
                        getUser()
                    }
                }
            }.addOnFailureListener {
                viewModelScope.launch {
                    _login.emit(Resource.Error("Lỗi hệ thống!"))
                }
            }
    }
}