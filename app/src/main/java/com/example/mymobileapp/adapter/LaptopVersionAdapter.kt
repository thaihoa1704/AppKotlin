package com.example.mymobileapp.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.mymobileapp.databinding.ItemLaptopAttributeBinding
import com.example.mymobileapp.listener.ClickItemLaptopVersionListener
import com.example.mymobileapp.model.Version

class LaptopVersionAdapter(private val clickItemLaptopVersionListener: ClickItemLaptopVersionListener
): RecyclerView.Adapter<LaptopVersionAdapter.PhoneVersionViewHolder>() {
    private var selectedPosition = -1

    private val diffCallback = object : DiffUtil.ItemCallback<Version>() {
        override fun areItemsTheSame(oldItem: Version, newItem: Version): Boolean {
            return oldItem.id == newItem.id
        }
        override fun areContentsTheSame(oldItem: Version, newItem: Version): Boolean {
            return oldItem == newItem
        }
    }
    val differ = AsyncListDiffer(this, diffCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PhoneVersionViewHolder {
        val binding: ItemLaptopAttributeBinding =
            ItemLaptopAttributeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PhoneVersionViewHolder(binding, clickItemLaptopVersionListener)
    }

    override fun onBindViewHolder(holder: PhoneVersionViewHolder, position: Int) {
        val version = differ.currentList[position]
        holder.bind(version, position)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    inner class PhoneVersionViewHolder(private val binding: ItemLaptopAttributeBinding,
                                private val clickItemLaptopVersionListener: ClickItemLaptopVersionListener
    ): RecyclerView.ViewHolder(binding.root) {
        fun bind(version: Version, position: Int) {
            binding.apply {
                tvCpu.text = version.cpu
                tvMemory.text = version.ram + "-" + version.hardDrive
                if (selectedPosition == position) {
                    card.strokeColor = Color.parseColor("#1835D6")
                    tvCpu.setTextColor(Color.parseColor("#1835D6"))
                    tvCpu.setBackgroundColor(Color.parseColor("#DDEFFD"))
                    tvMemory.setTextColor(Color.parseColor("#1835D6"))
                    tvMemory.setBackgroundColor(Color.parseColor("#DDEFFD"))
                } else {
                    card.strokeColor = Color.parseColor("#FF000000")
                    tvCpu.setTextColor(Color.parseColor("#FF000000"))
                    tvCpu.setBackgroundColor(Color.parseColor("#FFFFFFFF"))
                    tvMemory.setTextColor(Color.parseColor("#FF000000"))
                    tvMemory.setBackgroundColor(Color.parseColor("#FFFFFFFF"))
                }
                itemView.setOnClickListener {
                    clickItemLaptopVersionListener.onClickLaptop(version)
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