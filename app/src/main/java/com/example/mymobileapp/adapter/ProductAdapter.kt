package com.example.mymobileapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mymobileapp.databinding.ItemProductBinding
import com.example.mymobileapp.helper.Convert
import com.example.mymobileapp.listener.ClickItemProductListener
import com.example.mymobileapp.model.Product

class ProductAdapter(private val clickItemProductListener: ClickItemProductListener
): RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    private val diffCallback = object : DiffUtil.ItemCallback<Product>() {
        override fun areItemsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem.id == newItem.id
        }
        override fun areContentsTheSame(oldItem: Product, newItem: Product): Boolean {
            return oldItem == newItem
        }
    }
    val differ = AsyncListDiffer(this, diffCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val binding: ItemProductBinding =
            ItemProductBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ProductViewHolder(binding, clickItemProductListener)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = differ.currentList[position]
        holder.bind(product)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    inner class ProductViewHolder(private val binding: ItemProductBinding,
                            private val clickItemProductListener: ClickItemProductListener
    ): RecyclerView.ViewHolder(binding.root) {
        fun bind(product: Product) {
            binding.apply {
                Glide.with(itemView).load(product.images[0]).into(image)
                tvProductName.text = product.name
                tvPrice.text = "đ " + Convert.DinhDangTien(product.price)

                itemView.setOnClickListener {
                    clickItemProductListener.onClickItemProduct(product)
                }
            }
        }
    }
}