package com.example.mymobileapp.adapter

import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.mymobileapp.databinding.ItemWatchAttributeBinding
import com.example.mymobileapp.listener.ClickItemWatchVersionListener
import com.example.mymobileapp.model.Version

class WatchVersionAdapter(private val clickItemWatchVersionListener: ClickItemWatchVersionListener
): RecyclerView.Adapter<WatchVersionAdapter.PhoneVersionViewHolder>() {
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
        val binding: ItemWatchAttributeBinding =
            ItemWatchAttributeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PhoneVersionViewHolder(binding, clickItemWatchVersionListener)
    }

    override fun onBindViewHolder(holder: PhoneVersionViewHolder, position: Int) {
        val version = differ.currentList[position]
        holder.bind(version, position)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    inner class PhoneVersionViewHolder(private val binding: ItemWatchAttributeBinding,
                                private val clickItemWatchVersionListener: ClickItemWatchVersionListener
    ): RecyclerView.ViewHolder(binding.root) {
        fun bind(version: Version, position: Int) {
            binding.apply {
                tvDiameter.text = version.diameter.toString() + " mm"
                if (selectedPosition == position) {
                    card.strokeColor = Color.parseColor("#1835D6")
                    tvDiameter.setTextColor(Color.parseColor("#1835D6"))
                    tvDiameter.setBackgroundColor(Color.parseColor("#DDEFFD"))
                } else {
                    card.strokeColor = Color.parseColor("#FF000000")
                    tvDiameter.setTextColor(Color.parseColor("#FF000000"))
                    tvDiameter.setBackgroundColor(Color.parseColor("#FFFFFFFF"))
                }
                itemView.setOnClickListener {
                    clickItemWatchVersionListener.onClickWatch(version)
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