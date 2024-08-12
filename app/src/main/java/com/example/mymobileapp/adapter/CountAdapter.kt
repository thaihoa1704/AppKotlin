package com.example.mymobileapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mymobileapp.databinding.ItemOrderBinding
import com.example.mymobileapp.databinding.ItemOrderProductBinding
import com.example.mymobileapp.helper.Convert
import com.example.mymobileapp.listener.ClickItemOrderListener
import com.example.mymobileapp.model.CartProduct
import com.example.mymobileapp.model.Order
import com.example.mymobileapp.model.ProductCount

class CountAdapter(): RecyclerView.Adapter<CountAdapter.CountViewHolder>() {

    private val diffCallback = object : DiffUtil.ItemCallback<ProductCount>() {
        override fun areItemsTheSame(oldItem: ProductCount, newItem: ProductCount): Boolean {
            return oldItem.cartProduct.id == newItem.cartProduct.id
        }
        override fun areContentsTheSame(oldItem: ProductCount, newItem: ProductCount): Boolean {
            return oldItem == newItem
        }
    }
    val differ = AsyncListDiffer(this, diffCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CountViewHolder {
        val binding: ItemOrderProductBinding =
            ItemOrderProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CountViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CountViewHolder, position: Int) {
        val product = differ.currentList[position]
        holder.bind(product)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    inner class CountViewHolder(private val binding: ItemOrderProductBinding
    ): RecyclerView.ViewHolder(binding.root) {
        fun bind(productCount: ProductCount) {
            val img = productCount.cartProduct.product.images[0]
            Glide.with(itemView).load(img).into(binding.imageProduct)
            binding.tvProductName.text = productCount.cartProduct.product.name

            val category: String = productCount.cartProduct.product.category
            val cartProduct = productCount.cartProduct
            if (category == "Điện thoại") {
                setPhoneVersionData(cartProduct)
            } else if (category == "Laptop") {
                setLaptopVersionData(cartProduct)
            } else if (category == "Tai nghe") {
                setHeadPhoneVersionData(cartProduct)
            } else if (category == "Đồng hồ") {
                setWatchVersionData(cartProduct)
            } else {
                setAccessoryVersionData(cartProduct)
            }

            val price: String = Convert.DinhDangTien(cartProduct.version.price) + " đ"
            binding.tvPrice.text = price

            binding.tvQuantity.text = productCount.quantity.toString()
        }

        private fun setPhoneVersionData(cartProduct: CartProduct) {
            val version: String = ((cartProduct.version.color + " - "
                    + cartProduct.version.ram) + " - "
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

        private fun setAccessoryVersionData(cartProduct: CartProduct) {
        }
    }
}