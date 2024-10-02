package com.example.mymobileapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mymobileapp.model.CartProduct
import com.example.mymobileapp.util.Resource
import com.example.mymobileapp.util.constants.CART_COLLECTION
import com.example.mymobileapp.util.constants.USER_COLLECTION
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class CartViewModel @Inject constructor(
    private val db: FirebaseFirestore,
    private val firebaseAuth: FirebaseAuth
): ViewModel() {
    private val _cartList = MutableStateFlow<Resource<List<CartProduct>>>(Resource.Loading())
    val cartList: StateFlow<Resource<List<CartProduct>>> = _cartList

    private val _addToCart = MutableSharedFlow<Resource<String>>()
    val addToCart = _addToCart.asSharedFlow()

    val totalPrice = cartList.map {
        when(it) {
            is Resource.Success ->{
                calculatePrice(it.data!!.filter { cartProduct ->
                    cartProduct.select == true})
            }
            else -> 0
        }
    }
    private fun calculatePrice(list: List<CartProduct>): Int {
        return list.sumOf {
            it.version.price * it.quantity
        }
    }

    init {
        getCartList()
    }

    private fun getCartList() {
        db.collection(USER_COLLECTION).document(firebaseAuth.uid!!).collection(CART_COLLECTION)
            .addSnapshotListener { value, error ->
                if (error != null || value == null) {
                    viewModelScope.launch {
                        _cartList.emit(Resource.Error(error?.message.toString()))
                    }
                }else {
                    val list = value.toObjects(CartProduct::class.java)
                    viewModelScope.launch {
                        _cartList.emit(Resource.Success(list))
                    }
                }
            }
    }
    fun incrementQuantityProductInCart(documentId: String) {
        db.collection(USER_COLLECTION).document(firebaseAuth.uid!!)
            .collection(CART_COLLECTION).document(documentId)
            .update("quantity", FieldValue.increment(1))
            .addOnSuccessListener {

            }
            .addOnFailureListener {

            }
    }
    fun decrementQuantityProductInCart(documentId: String) {
        db.collection(USER_COLLECTION).document(firebaseAuth.uid!!)
            .collection(CART_COLLECTION).document(documentId)
            .update("quantity", FieldValue.increment(-1))
            .addOnSuccessListener {

            }
            .addOnFailureListener {

            }
    }
    fun deleteProductInCart(documentId: String) {
        db.collection(USER_COLLECTION).document(firebaseAuth.uid!!)
            .collection(CART_COLLECTION).document(documentId)
            .delete()
            .addOnSuccessListener {

            }
            .addOnFailureListener {

            }
    }
    fun selectProduct(documentId: String, aBoolean: Boolean) {
        db.collection(USER_COLLECTION).document(firebaseAuth.uid!!)
            .collection(CART_COLLECTION).document(documentId)
            .update("select", aBoolean)
            .addOnSuccessListener {

            }
            .addOnFailureListener {

            }
    }
    private fun addToCart(cartProduct: CartProduct){
        db.collection(USER_COLLECTION).document(firebaseAuth.uid!!)
            .collection(CART_COLLECTION).document(cartProduct.id)
            .set(cartProduct)
            .addOnSuccessListener {
                viewModelScope.launch {
                    _addToCart.emit(Resource.Success("Sản phẩm đã được thêm vào giỏ hàng!"))
                }
            }.addOnFailureListener{
                viewModelScope.launch {
                    _addToCart.emit(Resource.Error("Lỗi hệ thống!"))
                }
            }
    }
    fun checkProductInCart(cartProduct: CartProduct){
        viewModelScope.launch {
            _addToCart.emit(Resource.Loading())
        }
        db.collection(USER_COLLECTION).document(firebaseAuth.uid!!)
            .collection(CART_COLLECTION).document(cartProduct.version.id)
            .get().addOnSuccessListener {document->
                if(document.exists()){
                    viewModelScope.launch {
                        incrementQuantityProductInCart(cartProduct.id)
                        _addToCart.emit(Resource.Success("Sản phẩm đã được thêm vào giỏ hàng!"))
                    }
                }else{
                    addToCart(cartProduct)
                }
            }.addOnFailureListener{
                viewModelScope.launch {
                    _addToCart.emit(Resource.Error("Lỗi hệ thống!"))
                }
            }
    }
    fun selectNoneAllProduct() {
        db.collection(USER_COLLECTION).document(firebaseAuth.uid!!)
            .collection(CART_COLLECTION).get()
            .addOnCompleteListener { task ->
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
                                                .collection(CART_COLLECTION).document(list[i])
            batch.update(document, "select", false)
        }
        batch.commit().addOnCompleteListener { }
    }
    fun deleteProductSelect(){
        db.collection(USER_COLLECTION).document(firebaseAuth.uid!!)
            .collection(CART_COLLECTION)
            .whereEqualTo("select", true)
            .get().addOnCompleteListener{
                val list: MutableList<CartProduct> = ArrayList()
                for (queryDocumentSnapshot in it.result) {
                    val cartProduct = queryDocumentSnapshot.toObject(CartProduct::class.java)
                    list.add(cartProduct)
                }
                for (i in list.indices) {
                    deleteProductInCart(list[i].id)
                }
            }.addOnFailureListener{

            }
    }
}