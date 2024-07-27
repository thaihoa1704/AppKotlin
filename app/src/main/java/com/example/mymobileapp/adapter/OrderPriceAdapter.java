package com.example.mymobileapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import com.example.mymobileapp.databinding.ItemOrderByBinding;
import com.example.mymobileapp.databinding.ItemSelectedBinding;

import java.util.List;

public class OrderPriceAdapter extends ArrayAdapter<String> {
    public OrderPriceAdapter(@NonNull Context context, int resource, @NonNull List<String> objects) {
        super(context, resource, objects);
    }
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ItemSelectedBinding binding = ItemSelectedBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        String string = this.getItem(position);
        if (string != null){
            binding.tvSelected.setText(string);
        }
        return binding.getRoot();
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        ItemOrderByBinding binding = ItemOrderByBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        String string = this.getItem(position);
        if (string != null){
            binding.tvName.setText(string);
        }
        return binding.getRoot();
    }
}
