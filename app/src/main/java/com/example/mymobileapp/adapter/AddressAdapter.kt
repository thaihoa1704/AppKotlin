package com.example.mymobileapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.mymobileapp.databinding.ItemAddressBinding
import com.example.mymobileapp.listener.ClickItemAddressListener
import com.example.mymobileapp.model.Address

class AddressAdapter(position: Int, private val clickItemAddressListener: ClickItemAddressListener
): RecyclerView.Adapter<AddressAdapter.AddressViewHolder>() {
    private var selectedPosition = position

    private val diffCallback = object : DiffUtil.ItemCallback<Address>() {
        override fun areItemsTheSame(oldItem: Address, newItem: Address): Boolean {
            return oldItem.id == newItem.id
        }
        override fun areContentsTheSame(oldItem: Address, newItem: Address): Boolean {
            return oldItem == newItem
        }
    }
    val differ = AsyncListDiffer(this, diffCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AddressViewHolder {
        val binding: ItemAddressBinding =
            ItemAddressBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AddressViewHolder(binding, clickItemAddressListener)
    }

    override fun onBindViewHolder(holder: AddressViewHolder, position: Int) {
        val address = differ.currentList[position]
        holder.bind(address, position)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    inner class AddressViewHolder(private val binding: ItemAddressBinding,
                                private val clickItemAddressListener: ClickItemAddressListener
    ): RecyclerView.ViewHolder(binding.root) {
        fun bind(address: Address, position: Int) {
            binding.apply {
                radioButton.isChecked = address.select
                tvAddress.text = address.string
                radioButton.isChecked = selectedPosition == position
                radioButton.setOnClickListener {
                    clickItemAddressListener.onClick(address)
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