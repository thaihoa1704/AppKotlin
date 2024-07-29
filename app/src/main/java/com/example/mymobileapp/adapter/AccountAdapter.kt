package com.example.mymobileapp.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mymobileapp.databinding.ItemAccountBinding
import com.example.mymobileapp.databinding.ItemSearchBinding
import com.example.mymobileapp.listener.ClickItemAccountListener
import com.example.mymobileapp.listener.ClickItemProductListener
import com.example.mymobileapp.model.Product
import com.example.mymobileapp.model.User

class AccountAdapter(private val clickItemAccountListener: ClickItemAccountListener
): RecyclerView.Adapter<AccountAdapter.AccountViewHolder>() {

    private val diffCallback = object : DiffUtil.ItemCallback<User>() {
        override fun areItemsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem.id == newItem.id
        }
        override fun areContentsTheSame(oldItem: User, newItem: User): Boolean {
            return oldItem == newItem
        }
    }
    val differ = AsyncListDiffer(this, diffCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AccountViewHolder {
        val binding: ItemAccountBinding =
            ItemAccountBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AccountViewHolder(binding, clickItemAccountListener)
    }

    override fun onBindViewHolder(holder: AccountViewHolder, position: Int) {
        val product = differ.currentList[position]
        holder.bind(product)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    inner class AccountViewHolder(private val binding: ItemAccountBinding,
                                  private val clickItemAccountListener: ClickItemAccountListener
    ): RecyclerView.ViewHolder(binding.root) {
        fun bind(user: User) {
            binding.apply {
                //Glide.with(itemView).load(user.avatar).into(avatar)
                tvName.text = user.name
                tvEmail.text = user.email
                itemView.setOnClickListener {
                    clickItemAccountListener.onClickItemAccount(user)
                }
            }
        }
    }
}