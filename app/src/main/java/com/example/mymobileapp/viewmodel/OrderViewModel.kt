package com.example.mymobileapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mymobileapp.model.CartProduct
import com.example.mymobileapp.model.Order
import com.example.mymobileapp.util.Resource
import com.example.mymobileapp.util.constants.CANCEL_STATUS
import com.example.mymobileapp.util.constants.CART_COLLECTION
import com.example.mymobileapp.util.constants.CONFIRM_STATUS
import com.example.mymobileapp.util.constants.NOT_RATE_STATUS
import com.example.mymobileapp.util.constants.ORDER_COLLECTION
import com.example.mymobileapp.util.constants.PACKING_STATUS
import com.example.mymobileapp.util.constants.PRODUCT_COLLECTION
import com.example.mymobileapp.util.constants.RATE_STATUS
import com.example.mymobileapp.util.constants.SHIPPING_STATUS
import com.example.mymobileapp.util.constants.USER_COLLECTION
import com.example.mymobileapp.util.constants.VERSION_COLLECTION
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
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

    private val _packOrder = MutableStateFlow<Resource<List<Order>>>(Resource.Loading())
    val packOrder: StateFlow<Resource<List<Order>>> = _packOrder

    private val _shippingOrderList = MutableStateFlow<Resource<List<Order>>>(Resource.Loading())
    val shippingOrderList: StateFlow<Resource<List<Order>>> = _shippingOrderList

    private val _rateOrderList = MutableStateFlow<Resource<List<Order>>>(Resource.Loading())
    val rateOrderList: StateFlow<Resource<List<Order>>> = _rateOrderList

    private val _notRateOrderList = MutableStateFlow<Resource<List<Order>>>(Resource.Loading())
    val notRateOrderList: StateFlow<Resource<List<Order>>> = _notRateOrderList

    private val _cancelOrderList = MutableStateFlow<Resource<List<Order>>>(Resource.Loading())
    val cancelOrderList: StateFlow<Resource<List<Order>>> = _cancelOrderList

    private val _completeOrderList = MutableStateFlow<Resource<List<Order>>>(Resource.Loading())
    val completeOrderList: StateFlow<Resource<List<Order>>> = _completeOrderList

    private val _orderList = MutableStateFlow<Resource<List<Order>>>(Resource.Loading())
    val orderList: StateFlow<Resource<List<Order>>> = _orderList

    private val _time = MutableStateFlow<Resource<List<Order>>>(Resource.Loading())
    val time: StateFlow<Resource<List<Order>>> = _time

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
        hashMap["status"] = CONFIRM_STATUS
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
            .whereEqualTo("status", CONFIRM_STATUS)
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
    fun getPackOrder(){
        db.collection(USER_COLLECTION).document(firebaseAuth.uid!!)
            .collection(ORDER_COLLECTION)
            .whereEqualTo("status", PACKING_STATUS)
            .orderBy("dateTime", Query.Direction.DESCENDING)
            .addSnapshotListener { value, error ->
                if (error != null || value == null) {
                    viewModelScope.launch {
                        _packOrder.emit(Resource.Error(error?.message.toString()))
                    }
                }else {
                    val list = value.toObjects(Order::class.java)
                    viewModelScope.launch {
                        _packOrder.emit(Resource.Success(list))
                    }
                }
            }
    }
    fun getShippingOrder(){
        db.collection(USER_COLLECTION).document(firebaseAuth.uid!!)
            .collection(ORDER_COLLECTION)
            .whereEqualTo("status", SHIPPING_STATUS)
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
            .whereEqualTo("status", RATE_STATUS)
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
    fun getNotRateOrder(){
        db.collection(USER_COLLECTION).document(firebaseAuth.uid!!)
            .collection(ORDER_COLLECTION)
            .whereEqualTo("status", NOT_RATE_STATUS)
            .orderBy("dateTime", Query.Direction.DESCENDING)
            .addSnapshotListener { value, error ->
                if (error != null || value == null) {
                    viewModelScope.launch {
                        _notRateOrderList.emit(Resource.Error(error?.message.toString()))
                    }
                }else {
                    val list = value.toObjects(Order::class.java)
                    viewModelScope.launch {
                        _notRateOrderList.emit(Resource.Success(list))
                    }
                }
            }
    }
    fun getCancelOrder(){
        db.collection(USER_COLLECTION).document(firebaseAuth.uid!!)
            .collection(ORDER_COLLECTION)
            .whereEqualTo("status", CANCEL_STATUS)
            .orderBy("dateTime", Query.Direction.DESCENDING)
            .addSnapshotListener { value, error ->
                if (error != null || value == null) {
                    viewModelScope.launch {
                        _cancelOrderList.emit(Resource.Error(error?.message.toString()))
                    }
                }else {
                    val list = value.toObjects(Order::class.java)
                    viewModelScope.launch {
                        _cancelOrderList.emit(Resource.Success(list))
                    }
                }
            }
    }
    fun updateReceiveOrder(order: Order) {
        db.collection(USER_COLLECTION).document(firebaseAuth.uid!!)
            .collection(ORDER_COLLECTION).document(order.dateTime.toString())
            .update("status", NOT_RATE_STATUS)
            .addOnSuccessListener{ }
            .addOnFailureListener{}
        db.collection(ORDER_COLLECTION).document(order.dateTime.toString())
            .update("status", NOT_RATE_STATUS)
            .addOnSuccessListener {}
            .addOnFailureListener {}
    }
    fun updateCancelOrder(order: Order) {
        db.collection(USER_COLLECTION).document(firebaseAuth.uid!!)
            .collection(ORDER_COLLECTION).document(order.dateTime.toString())
            .update("status", CANCEL_STATUS)
            .addOnSuccessListener{
                updateQuantityProductAfterCancel(order.listProduct!!)
            }
            .addOnFailureListener{}
        db.collection(ORDER_COLLECTION).document(order.dateTime.toString())
            .update("status", CANCEL_STATUS)
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
    fun getCompleteOrder() {
        db.collection(USER_COLLECTION).document(firebaseAuth.uid!!)
            .collection(ORDER_COLLECTION)
            .whereIn("status", mutableListOf(NOT_RATE_STATUS, RATE_STATUS))
            .orderBy("dateTime", Query.Direction.DESCENDING)
            .addSnapshotListener { value, error ->
                if (error != null || value == null) {
                    viewModelScope.launch {
                        _completeOrderList.emit(Resource.Error(error?.message.toString()))
                    }
                }else {
                    val list = value.toObjects(Order::class.java)
                    viewModelScope.launch {
                        _completeOrderList.emit(Resource.Success(list))
                    }
                }
            }
    }
    fun rateOrder(order: Order, star: Int, note: String){
        viewModelScope.launch {
            _rate.emit(Resource.Loading())
        }
        db.collection(USER_COLLECTION).document(firebaseAuth.uid!!)
            .collection(ORDER_COLLECTION).document(order.dateTime.toString())
            .update("status", RATE_STATUS, "rateStar", star, "note", note)
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
            .update("status", RATE_STATUS, "rateStar", star, "note", note)
            .addOnSuccessListener {}
            .addOnFailureListener {}
    }
    fun getOrderListByTime(time: Long, time1: Long){
        db.collection(ORDER_COLLECTION).orderBy("dateTime", Query.Direction.ASCENDING)
            .startAt(time).endAt(time1)
            .get().addOnSuccessListener {
                val list = it.toObjects(Order::class.java)
                viewModelScope.launch {
                    _time.emit(Resource.Success(list))
                }
            }.addOnFailureListener{
                viewModelScope.launch {
                    _time.emit(Resource.Error(it.message.toString()))
                }
            }
    }
    fun getOrderCompleteListByTime(time: Long, time1: Long){
        db.collection(ORDER_COLLECTION)
            .whereIn("status", mutableListOf(NOT_RATE_STATUS, RATE_STATUS))
            .startAt(time).endAt(time1)
            .get().addOnSuccessListener {
                val list = it.toObjects(Order::class.java)
                viewModelScope.launch {
                    _completeOrderList.emit(Resource.Success(list))
                }
            }.addOnFailureListener{
                viewModelScope.launch {
                    _completeOrderList.emit(Resource.Error(it.message.toString()))
                }
            }
    }
}