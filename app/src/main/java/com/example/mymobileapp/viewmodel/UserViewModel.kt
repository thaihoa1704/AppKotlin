package com.example.mymobileapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mymobileapp.model.Address
import com.example.mymobileapp.model.Banner
import com.example.mymobileapp.model.Order
import com.example.mymobileapp.model.Product
import com.example.mymobileapp.model.User
import com.example.mymobileapp.util.Resource
import com.example.mymobileapp.util.constants.ADDRESS_COLLECTION
import com.example.mymobileapp.util.constants.ORDER_COLLECTION
import com.example.mymobileapp.util.constants.PRODUCT_COLLECTION
import com.example.mymobileapp.util.constants.USER_COLLECTION
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
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

    private val _addressList = MutableSharedFlow<Resource<String>>()
    val addressList = _addressList.asSharedFlow()

    private val _changeName = MutableSharedFlow<Resource<String>>()
    val changeName = _changeName.asSharedFlow()

    private val _changePassword = MutableSharedFlow<Resource<String>>()
    val changePassword = _changePassword.asSharedFlow()

    private val _user = MutableStateFlow<Resource<User>>(Resource.Loading())
    val user = _user.asStateFlow()

    private val _userList = MutableStateFlow<Resource<List<User>>>(Resource.Loading())
    val userList = _userList.asStateFlow()

    private val _orderList = MutableStateFlow<Resource<List<Order>>>(Resource.Loading())
    val orderList = _orderList.asStateFlow()

    private val _banner = MutableStateFlow<Resource<List<Banner>>>(Resource.Loading())
    val banner = _banner.asStateFlow()

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
    fun addAddress(string: String, boolean: Boolean){
        viewModelScope.launch {
            _addressList.emit(Resource.Loading())
        }
        val id = db.collection(USER_COLLECTION).document(firebaseAuth.uid!!)
                    .collection(ADDRESS_COLLECTION).document().id
        val address = Address(id, string, boolean)
        db.collection(USER_COLLECTION).document(firebaseAuth.uid!!)
            .collection(ADDRESS_COLLECTION).document(id)
            .set(address)
            .addOnSuccessListener {
                viewModelScope.launch {
                    _addressList.emit(Resource.Success("Thêm địa chỉ thành công!"))
                }
            }.addOnFailureListener{
                viewModelScope.launch {
                    _addressList.emit(Resource.Error(it.message.toString()))
                }
            }
    }
    fun changeName(name: String){
        viewModelScope.launch {
            _changeName.emit(Resource.Loading())
        }
        db.collection(USER_COLLECTION).document(firebaseAuth.uid!!)
            .update("name", name)
            .addOnSuccessListener {
                viewModelScope.launch {
                    _changeName.emit(Resource.Success("Đổi tên thành công!"))
                }
            }.addOnFailureListener{
                viewModelScope.launch {
                    _changeName.emit(Resource.Error(it.message.toString()))
                }
            }
    }

    fun checkPassword(user: User, oldPass: String, newPass: String){
        viewModelScope.launch {
            _changePassword.emit(Resource.Loading())
        }
        db.collection(USER_COLLECTION)
            .whereEqualTo("id", user.id)
            .whereEqualTo("password", oldPass)
            .get().addOnSuccessListener {
                if (!it.isEmpty) {
                    viewModelScope.launch {
                        _changePassword.emit(Resource.Error("Mật khẩu cũ không đúng!"))
                    }
                }else {
                    changePassword(oldPass, newPass)
                }
            }.addOnFailureListener{
                viewModelScope.launch {
                    _changePassword.emit(Resource.Error("Lỗi hệ thống"))
                }
            }
    }
    private fun changePassword(oldPass: String, newPass: String){
        viewModelScope.launch {
            _changePassword.emit(Resource.Loading())
        }
        val user = firebaseAuth.currentUser
        val credential = EmailAuthProvider.getCredential(user!!.email!!, oldPass)
        user.reauthenticate(credential)
            .addOnSuccessListener {
                user.updatePassword(newPass).addOnSuccessListener {
                    viewModelScope.launch {
                        _changePassword.emit(Resource.Success("Đổi mật khẩu thành công!"))
                    }
                    updatePassword(newPass)
                }.addOnFailureListener{
                    viewModelScope.launch {
                        _changePassword.emit(Resource.Error("Lỗi hệ thống"))
                    }
                }
            }.addOnFailureListener{
                viewModelScope.launch {
                    _changePassword.emit(Resource.Error("Lỗi hệ thống"))
                }
            }
    }

    private fun updatePassword(password: String){
        db.collection(USER_COLLECTION).document(firebaseAuth.uid!!)
            .update("password", password)
            .addOnSuccessListener {}
            .addOnFailureListener{}
    }

    fun getAllUser(){
        db.collection(USER_COLLECTION).whereNotEqualTo("type", "admin")
            .addSnapshotListener { value, error ->
                if (error != null || value == null) {
                    viewModelScope.launch {
                        _userList.emit(Resource.Error(error?.message.toString()))
                    }
                } else {
                    val list = value.toObjects(User::class.java)
                    viewModelScope.launch {
                        _userList.emit(Resource.Success(list))
                    }
                }
            }
    }
    fun getOrderOfUser(user: User){
        db.collection(USER_COLLECTION).document(user.id)
            .collection(ORDER_COLLECTION)
            .get().addOnSuccessListener {
                val list = it.toObjects(Order::class.java)
                viewModelScope.launch {
                    _orderList.emit(Resource.Success(list))
                }
            }.addOnFailureListener{
                viewModelScope.launch {
                    _orderList.emit(Resource.Error(it.message.toString()))
                }
            }
    }
    fun getBanner(){
        db.collection("Banner")
            .get().addOnSuccessListener {
                viewModelScope.launch {
                    val list = it.toObjects(Banner::class.java)
                    _banner.emit(Resource.Success(list))
                }
            }.addOnFailureListener{
                viewModelScope.launch {
                    _banner.emit(Resource.Error(it.message.toString()))
                }
            }
    }

    fun userLogout(){
        firebaseAuth.signOut()
    }
}