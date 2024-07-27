package com.example.mymobileapp.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.mymobileapp.databinding.ItemPriceBinding;
import com.example.mymobileapp.listener.ClickItemPriceListener;
import com.example.mymobileapp.model.Price;

import java.util.List;

public class PriceAdapter extends RecyclerView.Adapter<PriceAdapter.PriceViewHolder> {
    private Context context;
    private List<Price> priceList;
    private int selectedPosition;
    private ClickItemPriceListener clickItemPriceListener;

    public void setData(Context context, List<Price> priceList, int position) {
        this.context = context;
        this.priceList = priceList;
        this.selectedPosition = position;
    }

    public PriceAdapter(ClickItemPriceListener clickItemPriceListener) {
        this.clickItemPriceListener = clickItemPriceListener;
    }

    @NonNull
    @Override
    public PriceViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemPriceBinding binding = ItemPriceBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new PriceViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull PriceViewHolder holder, int position) {
        Price price = priceList.get(position);
        if (price == null) {
            return;
        }
        holder.bind(price, position);
    }

    @Override
    public int getItemCount() {
        if (priceList != null) {
            return priceList.size();
        }
        return 0;
    }

    public class PriceViewHolder extends RecyclerView.ViewHolder {
        private ItemPriceBinding binding;

        public PriceViewHolder(ItemPriceBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }

        public void bind(Price price, int position) {
            binding.tvPrice.setText(price.getPrice());

            if (selectedPosition == position) {
                binding.card.setStrokeColor(Color.parseColor("#1835D6"));
                binding.tvPrice.setTextColor(Color.parseColor("#1835D6"));
                binding.tvPrice.setBackgroundColor(Color.parseColor("#DDEFFD"));
            } else {
                binding.card.setStrokeColor(Color.parseColor("#FF000000"));
                binding.tvPrice.setTextColor(Color.parseColor("#FF000000"));
                binding.tvPrice.setBackgroundColor(Color.parseColor("#FFFFFFFF"));
            }
            binding.tvPrice.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    clickItemPriceListener.onPriceClick(price, position);
                    setSingleSelection(getBindingAdapterPosition());
                }
            });
        }

        private void setSingleSelection(int bindingAdapterPosition) {
            if (bindingAdapterPosition == RecyclerView.NO_POSITION) return;

            notifyItemChanged(selectedPosition);
            selectedPosition = bindingAdapterPosition;
            notifyItemChanged(selectedPosition);
        }
    }
}
