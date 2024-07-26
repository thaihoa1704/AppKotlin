package com.example.mymobileapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.mymobileapp.databinding.ItemOrderBinding
import com.example.mymobileapp.helper.Convert
import com.example.mymobileapp.listener.ClickItemOrderListener
import com.example.mymobileapp.model.CartProduct
import com.example.mymobileapp.model.Order

class OrderAdapter(private val clickItemOrderListener: ClickItemOrderListener
): RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    private val diffCallback = object : DiffUtil.ItemCallback<Order>() {
        override fun areItemsTheSame(oldItem: Order, newItem: Order): Boolean {
            return oldItem.dateTime == newItem.dateTime
        }
        override fun areContentsTheSame(oldItem: Order, newItem: Order): Boolean {
            return oldItem == newItem
        }
    }
    val differ = AsyncListDiffer(this, diffCallback)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val binding: ItemOrderBinding =
            ItemOrderBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OrderViewHolder(binding, clickItemOrderListener)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val product = differ.currentList[position]
        holder.bind(product)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    inner class OrderViewHolder(private val binding: ItemOrderBinding,
                                 private val clickItemOrderListener: ClickItemOrderListener
    ): RecyclerView.ViewHolder(binding.root) {
        fun bind(order: Order) {
            val product = order.listProduct!![0].product
            val img = product.images[0]
            Glide.with(itemView).load(img).into(binding.imgProduct)
            binding.tvProductName.text = product.name

            val category: String = product.category
            val cartProduct = order.listProduct!![0]
            if (category == "Điện thoại") {
                setPhoneVersionData(cartProduct)
            } else if (category == "Laptop") {
                setLaptopVersionData(cartProduct)
            } else if (category == "Tai nghe") {
                setWatchVersionData(cartProduct)
            } else if (category == "Đồng hồ") {
                setHeadPhoneVersionData(cartProduct)
            } else {
                setAccessoryVersionData(cartProduct)
            }

            binding.tvQuantity.text = cartProduct.quantity.toString()

            val price: String = Convert.DinhDangTien(cartProduct.version.price) + " đ"
            binding.tvPrice.text = price

            if (order.listProduct!!.size > 1) {
                binding.tvAnother.visibility = View.VISIBLE
                binding.line1.visibility = View.VISIBLE
            } else {
                binding.tvAnother.visibility = View.GONE
                binding.line1.visibility = View.GONE
            }

            var totalQuantity = 0
            for (item in order.listProduct!!) {
                totalQuantity += item.quantity
            }
            binding.tvTotalQuantity.text = totalQuantity.toString()
            val totalPrice: String = Convert.DinhDangTien(order.total) + " đ"
            binding.tvTatolPrice.text = totalPrice

            val process: String = order.status.toString()
            when (process) {
                "Chờ xác nhận" -> {
                    binding.btnProcess.text = "Đang xử lý"
                    binding.tvStatus.visibility = View.GONE
                    //binding.btnProcess.setBackgroundColor(Color.parseColor("#CECFCF"));
                }
                "Đơn hàng đang trên đường giao đến bạn" -> {
                    binding.tvStatus.text = process
                    binding.tvStatus.visibility = View.VISIBLE
                    binding.btnProcess.text = "Đã nhận được hàng"
                    //binding.btnProcess.setBackgroundColor(Color.parseColor("#49d7c8"));
                }
                "Chưa đánh giá" -> {
                    binding.tvStatus.visibility = View.GONE
                    binding.btnProcess.text = "Đánh giá"
                    //binding.btnProcess.setBackgroundColor(Color.parseColor("#49d7c8"));
                }
                "Đã đánh giá" -> {
                    binding.tvStatus.visibility = View.GONE
                    binding.btnProcess.text = process
                    //binding.btnProcess.setBackgroundColor(Color.parseColor("#49d7c8"));
                }
            }

            binding.layoutItemOrder.setOnClickListener { clickItemOrderListener.onClick(order) }
            binding.btnProcess.setOnClickListener { clickItemOrderListener.onClick(order) }
        }

        private fun setPhoneVersionData(cartProduct: CartProduct) {
            val version: String = ((cartProduct.version.color + " - "
                    + cartProduct.version.ram) + " - "
                    + cartProduct.version.storage)
            binding.tvVersion.text = version
        }

        private fun setLaptopVersionData(cartProduct: CartProduct) {
        }

        private fun setWatchVersionData(cartProduct: CartProduct) {
        }

        private fun setHeadPhoneVersionData(cartProduct: CartProduct) {
        }

        private fun setAccessoryVersionData(cartProduct: CartProduct) {
        }
    }
}