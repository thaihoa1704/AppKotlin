package com.example.mymobileapp.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.mymobileapp.databinding.ItemColorBinding
import com.example.mymobileapp.listener.ClickItemColorListener
import com.example.mymobileapp.model.ProductColor

class ColorAdapter(private val clickItemColorListener: ClickItemColorListener
): RecyclerView.Adapter<ColorAdapter.ColorViewHolder>() {
    private var selectedPosition = -1

    private val diffCallback = object : DiffUtil.ItemCallback<ProductColor>() {
        override fun areItemsTheSame(oldItem: ProductColor, newItem: ProductColor): Boolean {
            return oldItem.id == newItem.id
        }
        override fun areContentsTheSame(oldItem: ProductColor, newItem: ProductColor): Boolean {
            return oldItem == newItem
        }
    }
    val differ = AsyncListDiffer(this, diffCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ColorViewHolder {
        val binding: ItemColorBinding =
            ItemColorBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ColorViewHolder(binding, clickItemColorListener)
    }

    override fun onBindViewHolder(holder: ColorViewHolder, position: Int) {
        val productColor = differ.currentList[position]
        holder.bind(productColor, position)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    inner class ColorViewHolder(private val binding: ItemColorBinding,
                                  private val clickItemColorListener: ClickItemColorListener
    ): RecyclerView.ViewHolder(binding.root) {
        fun bind(productColor: ProductColor, position: Int) {
            binding.apply {
                colorCard.setCardBackgroundColor(Color.parseColor(productColor.colorCode))
                if (selectedPosition == position) {
                    selected.visibility = View.VISIBLE
                } else {
                    selected.visibility = View.INVISIBLE
                }
                itemView.setOnClickListener {
                    clickItemColorListener.onClickColor(productColor)
                    setSingleSelection(bindingAdapterPosition)
                }
            }
        }
    }

    private fun setSingleSelection(bindingAdapterPosition: Int) {
        if (bindingAdapterPosition == selectedPosition) return
        //notifyItemChanged(position): Update item at position
        notifyItemChanged(selectedPosition)
        selectedPosition = bindingAdapterPosition
        notifyItemChanged(selectedPosition)
    }
}