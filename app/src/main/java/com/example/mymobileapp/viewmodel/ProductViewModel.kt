package com.example.mymobileapp.viewmodel

import android.net.Uri
import android.widget.Toast
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mymobileapp.model.Brand
import com.example.mymobileapp.model.Order
import com.example.mymobileapp.model.Product
import com.example.mymobileapp.model.ProductColor
import com.example.mymobileapp.model.Version
import com.example.mymobileapp.util.Resource
import com.example.mymobileapp.util.constants.BRAND_COLLECTION
import com.example.mymobileapp.util.constants.CART_COLLECTION
import com.example.mymobileapp.util.constants.CATEGORY_COLLECTION
import com.example.mymobileapp.util.constants.PRODUCT_COLLECTION
import com.example.mymobileapp.util.constants.USER_COLLECTION
import com.example.mymobileapp.util.constants.VERSION_COLLECTION
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import java.util.Objects
import java.util.UUID
import javax.inject.Inject


@Suppress("UNCHECKED_CAST", "DEPRECATION")
@HiltViewModel
class ProductViewModel @Inject constructor(
    private val db: FirebaseFirestore,
    private val storage: FirebaseStorage
): ViewModel(){
    private val _versionList = MutableStateFlow<Resource<List<Version>>>(Resource.Loading())
    val versionList: StateFlow<Resource<List<Version>>> = _versionList

    private val _versionDetail = MutableStateFlow<Resource<List<Version>>>(Resource.Loading())
    val versionDetail: StateFlow<Resource<List<Version>>> = _versionDetail

    private val _brandList = MutableStateFlow<Resource<List<Brand>>>(Resource.Loading())
    val brandList: StateFlow<Resource<List<Brand>>> = _brandList

    private val _product = MutableStateFlow<Resource<Product>>(Resource.Loading())
    val product: StateFlow<Resource<Product>> = _product

    private val _message = MutableSharedFlow<Resource<String>>()
    val message = _message.asSharedFlow()

    private val _idProduct = MutableSharedFlow<Resource<String>>()
    val idProduct = _idProduct.asSharedFlow()

    private val _imageList = MutableStateFlow<Resource<List<String>>>(Resource.Loading())
    val imageList: StateFlow<Resource<List<String>>> = _imageList

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
    fun getBrand(category: String){
        db.collection(CATEGORY_COLLECTION).document(category)
            .collection(BRAND_COLLECTION)
            .orderBy("name")
            .get().addOnSuccessListener {result ->
                val list = result.toObjects(Brand::class.java)
                viewModelScope.launch {
                    _brandList.emit(Resource.Success(list))
                }
            }.addOnFailureListener {
                viewModelScope.launch {
                    _brandList.emit(Resource.Error(it.message.toString()))
                }
            }
    }
    fun saveProduct(name: String, images: ArrayList<String>, price: Int, category: String
                    , brand: String, description: String, colors: ArrayList<ProductColor>){
        viewModelScope.launch {
            _idProduct.emit(Resource.Loading())
        }
        val idProduct = db.collection(PRODUCT_COLLECTION).document().id

        val hashMap = HashMap<String, Any>()
        hashMap["id"] = idProduct
        hashMap["name"] = name
        hashMap["images"] = images
        hashMap["price"] = price
        hashMap["category"] = category
        hashMap["brand"] = brand
        hashMap["description"] = description
        hashMap["colors"] = colors
        hashMap["isSpecial"] = false

        db.collection(PRODUCT_COLLECTION).document(idProduct)
            .set(hashMap)
            .addOnSuccessListener {
                viewModelScope.launch {
                    _idProduct.emit(Resource.Success(idProduct))
                }
            }.addOnFailureListener {
                viewModelScope.launch {
                    _idProduct.emit(Resource.Error(it.message.toString()))
                }
            }
    }
    fun uploadImages(imagesUrl: ArrayList<Uri>, images: ArrayList<String>) {
        viewModelScope.launch {
            _imageList.emit(Resource.Loading())
        }
        //val images = ArrayList<String>()
        val storageReference =
            storage.getReference("images").child(UUID.randomUUID().toString())
        val uri = imagesUrl[images.size]
        storageReference.putFile(uri)
            .addOnSuccessListener {
                storageReference.downloadUrl.addOnCompleteListener { task: Task<Uri> ->
                    val url = Objects.requireNonNull(task.result).toString()
                    images.add(url)
                    if (imagesUrl.size == images.size) {
                        //All images uploaded successfully.
                        //Uploaded images url is stored in imagesUrl ArrayList.
                        viewModelScope.launch {
                            _imageList.emit(Resource.Success(images))
                        }
                    } else {
                        uploadImages(imagesUrl, images)
                    }
                }
            }.addOnFailureListener {
                viewModelScope.launch {
                    _imageList.emit(Resource.Error(it.message.toString()))
                }
            }
    }
    fun saveVersion(idProduct: String, versionList: ArrayList<Version>){
        viewModelScope.launch {
            _message.emit(Resource.Loading())
        }
        val batch = db.batch()
        versionList.forEach {
            val document: DocumentReference = db.collection(PRODUCT_COLLECTION).document(idProduct)
                .collection(VERSION_COLLECTION).document(it.id)
            batch.set(document, it)
        }
        batch.commit().addOnSuccessListener {
            viewModelScope.launch {
                _message.emit(Resource.Success("Success Add Version"))
            }
        }.addOnFailureListener {
            viewModelScope.launch {
                _message.emit(Resource.Error(it.message.toString()))
            }
        }
    }
    fun getInformationProduct(idProduct: String){
        db.collection(PRODUCT_COLLECTION).document(idProduct)
            .addSnapshotListener{ value, error ->
                if (error != null || value == null) {
                    viewModelScope.launch {
                        _product.emit(Resource.Error(error?.message.toString()))
                    }
                }else {
                    val model = value.toObject(Product::class.java)
                    viewModelScope.launch {
                        _product.emit(Resource.Success(model!!))
                    }
                }
            }
    }
    fun updateSpecial(idProduct: String, isSpecial: Boolean){
        viewModelScope.launch {
            _message.emit(Resource.Loading())
        }
        db.collection(PRODUCT_COLLECTION).document(idProduct)
            .update("isSpecial", isSpecial)
            .addOnSuccessListener {
                viewModelScope.launch {
                    _message.emit(Resource.Success("Success"))
                }
            }.addOnFailureListener {
                viewModelScope.launch {
                    _message.emit(Resource.Error(it.message.toString()))
                }
            }
    }
}