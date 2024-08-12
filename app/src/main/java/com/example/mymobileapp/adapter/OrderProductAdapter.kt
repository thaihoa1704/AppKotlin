package com.example.mymobileapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mymobileapp.databinding.ItemOrderProductBinding
import com.example.mymobileapp.helper.Convert
import com.example.mymobileapp.model.CartProduct

class OrderProductAdapter(): RecyclerView.Adapter<OrderProductAdapter.OrderViewHolder>() {

    private val diffCallback = object : DiffUtil.ItemCallback<CartProduct>() {
        override fun areItemsTheSame(oldItem: CartProduct, newItem: CartProduct): Boolean {
            return oldItem.id == newItem.id
        }
        override fun areContentsTheSame(oldItem: CartProduct, newItem: CartProduct): Boolean {
            return oldItem == newItem
        }
    }
    val differ = AsyncListDiffer(this, diffCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val binding: ItemOrderProductBinding =
            ItemOrderProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OrderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val cartProduct = differ.currentList[position]
        holder.bind(cartProduct)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    inner class OrderViewHolder(private val binding: ItemOrderProductBinding
    ): RecyclerView.ViewHolder(binding.root) {
        private lateinit var category: String

        fun bind(cartProduct: CartProduct) {
            category = cartProduct.product.category
            when (category) {
                "Điện thoại" -> {
                    setPhoneVersionData(cartProduct)
                }
                "Laptop" -> {
                    setLaptopVersionData(cartProduct)
                }
                "Tai nghe" -> {
                    setHeadPhoneVersionData(cartProduct)
                }
                "Đồng hồ" -> {
                    setWatchVersionData(cartProduct)
                }
            }
            binding.apply {
                Glide.with(itemView).load(cartProduct.product.images[0]).into(imageProduct)
                tvProductName.text = cartProduct.product.name
                tvQuantity.text = cartProduct.quantity.toString()
                tvPrice.text = Convert.DinhDangTien(cartProduct.version.price) + " đ"
            }
        }
        private fun setPhoneVersionData(cartProduct: CartProduct) {
            val version: String = (cartProduct.version.color + " - "
                    + cartProduct.version.ram + " - "
                    + cartProduct.version.storage)
            binding.tvVersion.text = version
        }

        private fun setLaptopVersionData(cartProduct: CartProduct) {
            val version: String = (cartProduct.version.color + "-"
                    + cartProduct.version.cpu + "-"
                    + cartProduct.version.ram + "-"
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
