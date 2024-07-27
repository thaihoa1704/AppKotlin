package com.example.mymobileapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mymobileapp.model.User
import com.example.mymobileapp.util.Resource
import com.example.mymobileapp.util.constants.USER_COLLECTION
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegisterViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val db: FirebaseFirestore
): ViewModel(){
    private val _register = MutableSharedFlow<Resource<User>>()
    val register = _register.asSharedFlow()

    fun userRegister(user: User){
        viewModelScope.launch {
            _register.emit(Resource.Loading())
        }
        firebaseAuth.createUserWithEmailAndPassword(user.email, user.password)
            .addOnSuccessListener {
                viewModelScope.launch {
                    it.user?.let {
                        updateUserInfo(it.uid, user)
                    }
                }
            }.addOnFailureListener{
                viewModelScope.launch {
                    _register.emit(Resource.Error("Lỗi hệ thống r!"))
                }
            }
    }

    private fun updateUserInfo(userId: String, user: User) {
        user.id = userId
        db.collection(USER_COLLECTION).document(userId).set(user)
            .addOnSuccessListener {
                viewModelScope.launch {
                    _register.emit(Resource.Success(user))
                }
            }.addOnFailureListener {
                viewModelScope.launch {
                    _register.emit(Resource.Error("Không thể thêm tài khoản người dùng vào cơ sở dữ liệu"))
                }
            }
    }
}