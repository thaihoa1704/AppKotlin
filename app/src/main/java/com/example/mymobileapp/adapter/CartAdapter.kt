package com.example.mymobileapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mymobileapp.databinding.ItemCartBinding
import com.example.mymobileapp.helper.Convert
import com.example.mymobileapp.listener.ChangeQuantityCartProduct
import com.example.mymobileapp.listener.ChangeSelectProductListener
import com.example.mymobileapp.listener.ClickItemProductListener
import com.example.mymobileapp.model.CartProduct

class CartAdapter(private val clickItemProductListener: ClickItemProductListener,
                  private var changeQuantityCartProduct: ChangeQuantityCartProduct,
                  private var changeSelectProductListener: ChangeSelectProductListener
): RecyclerView.Adapter<CartAdapter.CartViewHolder>() {

    private val diffCallback = object : DiffUtil.ItemCallback<CartProduct>() {
        override fun areItemsTheSame(oldItem: CartProduct, newItem: CartProduct): Boolean {
            return oldItem.id == newItem.id
        }
        override fun areContentsTheSame(oldItem: CartProduct, newItem: CartProduct): Boolean {
            return oldItem == newItem
        }
    }
    val differ = AsyncListDiffer(this, diffCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartViewHolder {
        val binding: ItemCartBinding =
            ItemCartBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CartViewHolder(binding, clickItemProductListener, changeSelectProductListener, changeQuantityCartProduct)
    }

    override fun onBindViewHolder(holder: CartViewHolder, position: Int) {
        val cartProduct = differ.currentList[position]
        holder.bind(cartProduct)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    inner class CartViewHolder(
        private val binding: ItemCartBinding,
        private val clickItemProductListener: ClickItemProductListener,
        private val changeSelectProductListener: ChangeSelectProductListener,
        private val changeQuantityCartProduct: ChangeQuantityCartProduct
    ): RecyclerView.ViewHolder(binding.root) {
        private lateinit var documentId: String
        private lateinit var category: String

        fun bind(cartProduct: CartProduct) {
            documentId = cartProduct.id
            category = cartProduct.product.category
            if (category == "Điện thoại") {
                setPhoneVersionData(cartProduct)
            } else if (category == "Laptop") {
                setLaptopVersionData(cartProduct)
            } else if (category == "Tai nghe") {
                setHeadPhoneVersionData(cartProduct)
            } else if (category == "Đồng hồ") {
                setWatchVersionData(cartProduct)
            }
            binding.apply {
                Glide.with(itemView).load(cartProduct.product.images[0]).into(imgProduct)
                tvProductName.text = cartProduct.product.name
                tvQuantity.text = cartProduct.quantity.toString()
                checkBox.isChecked = cartProduct.select!!
                tvPrice.text = Convert.DinhDangTien(cartProduct.version.price) + " đ"

                itemView.setOnClickListener {
                    clickItemProductListener.onClickItemProduct(cartProduct.product)
                }
                checkBox.setOnClickListener{
                    changeSelectProductListener.onChangeSelect(documentId, checkBox.isChecked)
                }
                tvAdd.setOnClickListener {
                    changeQuantityCartProduct.incrementQuantity(documentId, cartProduct.product.id, cartProduct.quantity)
                }
                tvMinus.setOnClickListener {
                    if (cartProduct.quantity == 1){
                        changeQuantityCartProduct.deleteProduct(documentId)
                    } else
                        changeQuantityCartProduct.decrementQuantity(documentId)
                }
            }
        }
        private fun setPhoneVersionData(cartProduct: CartProduct) {
            val version: String = ((cartProduct.version.color + " - "
                    + cartProduct.version.ram + " - "
                    + cartProduct.version.storage))
            binding.tvVersion.text = version
        }

        private fun setLaptopVersionData(cartProduct: CartProduct) {
            val version: String = (cartProduct.version.color + " - "
                    + cartProduct.version.cpu + " - "
                    + cartProduct.version.ram + " - "
                    + cartProduct.version.hardDrive)
            binding.tvVersion.text = version
        }

        private fun setWatchVersionData(cartProduct: CartProduct) {
            val version: String = (cartProduct.version.color + " - "
                    + cartProduct.version.diameter + " mm")
            binding.tvVersion.text = version
        }

        private fun setHeadPhoneVersionData(cartProduct: CartProduct) {
            val version: String = cartProduct.version.color
            binding.tvVersion.text = version
        }
    }
}