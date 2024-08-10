package com.example.mymobileapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.PopupMenu
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mymobileapp.R
import com.example.mymobileapp.databinding.ItemProductBinding
import com.example.mymobileapp.helper.Convert
import com.example.mymobileapp.listener.ClickItemProductListener
import com.example.mymobileapp.listener.ClickItemProductListener1
import com.example.mymobileapp.model.Product

class ProductAdapter(
    private val userType: String,
    private val clickItemProductListener: ClickItemProductListener,
    private val clickItemProductListener1: ClickItemProductListener1
): RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {
    private var type = userType

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
        return ProductViewHolder(binding, clickItemProductListener, clickItemProductListener1)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        val product = differ.currentList[position]
        holder.bind(product)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    inner class ProductViewHolder(private val binding: ItemProductBinding,
                                  private val clickItemProductListener: ClickItemProductListener,
                                  private val clickItemProductListener1: ClickItemProductListener1
    ): RecyclerView.ViewHolder(binding.root), View.OnClickListener {
        fun bind(product: Product) {
            binding.apply {
                if(type == "admin"){
                    imgPopup.visibility = View.VISIBLE
                }else{
                    imgPopup.visibility = View.GONE
                }
                Glide.with(itemView).load(product.images[0]).into(image)
                tvProductName.text = product.name
                tvPrice.text = "đ " + Convert.DinhDangTien(product.price)
                binding.image.setOnClickListener {
                    clickItemProductListener.onClickItemProduct(product)
                }
                binding.imgPopup.setOnClickListener(this@ProductViewHolder)
            }
        }
        private fun setupPopUpMenu(view: View, product: Product){
            val popupMenu = PopupMenu(view.context, view)
            popupMenu.inflate(R.menu.pop_up_menu)
            popupMenu.setOnMenuItemClickListener(PopupMenu.OnMenuItemClickListener{ item ->
                when(item.itemId){
                    R.id.edit_product -> {
                        clickItemProductListener1.onClickItemProduct1(product)
                    }
                }
                true
            })
            popupMenu.show()
        }

        override fun onClick(v: View) {
            setupPopUpMenu(v, differ.currentList[bindingAdapterPosition])
        }
    }
}