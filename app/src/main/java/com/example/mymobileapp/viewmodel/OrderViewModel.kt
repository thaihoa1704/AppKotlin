package com.example.mymobileapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mymobileapp.model.CartProduct
import com.example.mymobileapp.model.Order
import com.example.mymobileapp.util.Resource
import com.example.mymobileapp.util.constans.CART_COLLECTION
import com.example.mymobileapp.util.constans.ORDER_COLLECTION
import com.example.mymobileapp.util.constans.PRODUCT_COLLECTION
import com.example.mymobileapp.util.constans.USER_COLLECTION
import com.example.mymobileapp.util.constans.VERSION_COLLECTION
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.QuerySnapshot
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OrderViewModel @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val db: FirebaseFirestore
): ViewModel(){
    private val _createOrder = MutableSharedFlow<Resource<String>>()
    val createOrder = _createOrder.asSharedFlow()

    private val _confirmOrder = MutableStateFlow<Resource<List<Order>>>(Resource.Loading())
    val confirmOrder: StateFlow<Resource<List<Order>>> = _confirmOrder

    private val _shippingOrderList = MutableStateFlow<Resource<List<Order>>>(Resource.Loading())
    val shippingOrderList: StateFlow<Resource<List<Order>>> = _shippingOrderList

    private val _rateOrderList = MutableStateFlow<Resource<List<Order>>>(Resource.Loading())
    val rateOrderList: StateFlow<Resource<List<Order>>> = _rateOrderList

    private val _orderList = MutableStateFlow<Resource<List<Order>>>(Resource.Loading())
    val orderList: StateFlow<Resource<List<Order>>> = _orderList

    private val _rate = MutableSharedFlow<Resource<String>>()
    val rate = _rate.asSharedFlow()

    fun createOrder(list: List<CartProduct>,contact: String, address: String, total: Int){
        viewModelScope.launch {
            _createOrder.emit(Resource.Loading())
        }
        val timestamp = System.currentTimeMillis()

        val hashMap = HashMap<String, Any>()
        hashMap["dateTime"] = timestamp
        hashMap["contact"] = contact
        hashMap["address"] = address
        hashMap["listProduct"] = list
        hashMap["total"] = total
        hashMap["status"] = "Chờ xác nhận"
        hashMap["rateStar"] = 0
        hashMap["note"] = ""

        db.collection(USER_COLLECTION).document(firebaseAuth.uid!!)
            .collection(ORDER_COLLECTION).document(timestamp.toString())
            .set(hashMap)
            .addOnSuccessListener {
                viewModelScope.launch {
                    _createOrder.emit(Resource.Success("Success"))
                    deleteProductInCart(list)
                    updateQuantityProduct(list)
                }
            }.addOnFailureListener {
                viewModelScope.launch {
                    _createOrder.emit(Resource.Error("Error"))
                }
            }
        db.collection(ORDER_COLLECTION).document(timestamp.toString())
            .set(hashMap)
            .addOnSuccessListener {}
            .addOnFailureListener {}
    }

    private fun deleteProductInCart(list: List<CartProduct>){
        for (item in list) {
            db.collection(USER_COLLECTION).document(firebaseAuth.uid!!)
                .collection(CART_COLLECTION).document(item.version.id)
                .delete()
                .addOnSuccessListener {}
                .addOnFailureListener {}
        }
    }
    private fun updateQuantityProduct(list: List<CartProduct>){
        for (item in list) {
            val quantity: Int = item.quantity
            db.collection(PRODUCT_COLLECTION).document(item.product.id)
                .collection(VERSION_COLLECTION).document(item.version.id)
                .update("quantity", FieldValue.increment(-quantity.toLong()))
                .addOnSuccessListener {}
                .addOnFailureListener {}
        }
    }

    fun getConfirmOrder(){
        db.collection(USER_COLLECTION).document(firebaseAuth.uid!!)
            .collection(ORDER_COLLECTION)
            .whereEqualTo("status", "Chờ xác nhận")
            .orderBy("dateTime", Query.Direction.DESCENDING)
            .addSnapshotListener { value, error ->
                if (error != null || value == null) {
                    viewModelScope.launch {
                        _confirmOrder.emit(Resource.Error(error?.message.toString()))
                    }
                }else {
                    val list = value.toObjects(Order::class.java)
                    viewModelScope.launch {
                        _confirmOrder.emit(Resource.Success(list))
                    }
                }
            }
    }
    fun getShippingOrder(){
        db.collection(USER_COLLECTION).document(firebaseAuth.uid!!)
            .collection(ORDER_COLLECTION)
            .whereEqualTo("status", "Đơn hàng đang trên đường giao đến bạn")
            .orderBy("dateTime", Query.Direction.DESCENDING)
            .addSnapshotListener { value, error ->
                if (error != null || value == null) {
                    viewModelScope.launch {
                        _shippingOrderList.emit(Resource.Error(error?.message.toString()))
                    }
                }else {
                    val list = value.toObjects(Order::class.java)
                    viewModelScope.launch {
                        _shippingOrderList.emit(Resource.Success(list))
                    }
                }
            }
    }
    fun getRateOrder(){
        db.collection(USER_COLLECTION).document(firebaseAuth.uid!!)
            .collection(ORDER_COLLECTION)
            .whereEqualTo("status", "Chưa đánh giá")
            .orderBy("dateTime", Query.Direction.DESCENDING)
            .addSnapshotListener { value, error ->
                if (error != null || value == null) {
                    viewModelScope.launch {
                        _rateOrderList.emit(Resource.Error(error?.message.toString()))
                    }
                }else {
                    val list = value.toObjects(Order::class.java)
                    viewModelScope.launch {
                        _rateOrderList.emit(Resource.Success(list))
                    }
                }
            }
    }
    fun updateReceiveOrder(order: Order) {
        db.collection(USER_COLLECTION).document(firebaseAuth.uid!!)
            .collection(ORDER_COLLECTION).document(order.dateTime.toString())
            .update("status", "Chưa đánh giá")
            .addOnSuccessListener{ }
            .addOnFailureListener{}
        db.collection(ORDER_COLLECTION).document(order.dateTime.toString())
            .update("status", "Chưa đánh giá")
            .addOnSuccessListener {}
            .addOnFailureListener {}
    }
    fun updateCancelOrder(order: Order) {
        db.collection(USER_COLLECTION).document(firebaseAuth.uid!!)
            .collection(ORDER_COLLECTION).document(order.dateTime.toString())
            .update("status", "Đơn hàng đã huỷ")
            .addOnSuccessListener{
                updateQuantityProductAfterCancel(order.listProduct!!)
            }
            .addOnFailureListener{}
        db.collection(ORDER_COLLECTION).document(order.dateTime.toString())
            .update("status", "Đơn hàng đã huỷ")
            .addOnSuccessListener {}
            .addOnFailureListener {}
    }
    private fun updateQuantityProductAfterCancel(list: List<CartProduct>){
        for (item in list) {
            val quantity: Int = item.quantity
            db.collection(PRODUCT_COLLECTION).document(item.product.id)
                .collection(VERSION_COLLECTION).document(item.version.id)
                .update("quantity", FieldValue.increment(quantity.toLong()))
                .addOnSuccessListener {}
                .addOnFailureListener {}
        }
    }
    fun getPurchaseHistory() {
        db.collection(USER_COLLECTION).document(firebaseAuth.uid!!)
            .collection(ORDER_COLLECTION)
            .whereIn("status", mutableListOf("Chưa đánh giá", "Đã đánh giá"))
            .orderBy("dateTime", Query.Direction.DESCENDING)
            .get().addOnSuccessListener{
                val list = it.toObjects(Order::class.java)
                viewModelScope.launch {
                    _orderList.emit(Resource.Success(list))
                }
            }
            .addOnFailureListener{
                viewModelScope.launch {
                    _orderList.emit(Resource.Error(it.message.toString()))
                }
            }
    }
    fun rateOrder(order: Order, star: Int, note: String){
        viewModelScope.launch {
            _rate.emit(Resource.Loading())
        }
        db.collection(USER_COLLECTION).document(firebaseAuth.uid!!)
            .collection(ORDER_COLLECTION).document(order.dateTime.toString())
            .update("status", "Đã đánh giá", "rateStar", star, "note", note)
            .addOnSuccessListener{
                viewModelScope.launch {
                    _rate.emit(Resource.Success("Đánh giá của bạn đã được gửi đi!"))
                }
            }.addOnFailureListener{
                viewModelScope.launch {
                    _rate.emit(Resource.Error("Lỗi hệ thống"))
                }
            }
        db.collection(ORDER_COLLECTION).document(order.dateTime.toString())
            .update("status", "Đã đánh giá", "rateStar", star, "note", note)
            .addOnSuccessListener {}
            .addOnFailureListener {}
    }
}