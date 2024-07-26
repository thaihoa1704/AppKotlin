package com.example.mymobileapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mymobileapp.model.Address
import com.example.mymobileapp.model.User
import com.example.mymobileapp.util.Resource
import com.example.mymobileapp.util.constans.ADDRESS_COLLECTION
import com.example.mymobileapp.util.constans.USER_COLLECTION
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val db: FirebaseFirestore
) : ViewModel() {
    private val _forgotPassword = MutableSharedFlow<Resource<String>>()
    val forgotPassword = _forgotPassword.asSharedFlow()

    private val _user = MutableStateFlow<Resource<User>>(Resource.Loading())
    val user = _user.asStateFlow()

    init {
        getUser()
    }

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

    fun sendLinkToEmail(email: String){
        viewModelScope.launch {
            _forgotPassword.emit(Resource.Loading())
        }
        db.collection(USER_COLLECTION).whereEqualTo("email", email)
            .get().addOnSuccessListener {
                if (!it.isEmpty) {
                    firebaseAuth.sendPasswordResetEmail(email)
                        .addOnSuccessListener {
                            viewModelScope.launch {
                                _forgotPassword.emit(Resource.Success("Link đặt lại mật khẩu đã được gửi đến email của bạn!"))
                            }
                        }.addOnFailureListener{
                            viewModelScope.launch {
                                _forgotPassword.emit(Resource.Error("Lỗi hệ thống f!"))
                            }
                        }
                }else {
                    viewModelScope.launch {
                        _forgotPassword.emit(Resource.Error("Email chưa được đăng ký!"))
                    }
                }
            }.addOnFailureListener{
                viewModelScope.launch {
                    _forgotPassword.emit(Resource.Error(it.message.toString()))
                }
            }
    }
}