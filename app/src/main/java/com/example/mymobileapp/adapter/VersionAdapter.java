package com.example.mymobileapp.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mymobileapp.R;
import com.example.mymobileapp.helper.Convert;
import com.example.mymobileapp.listener.OnClickDeleteVersion;
import com.example.mymobileapp.model.Version;

import java.util.ArrayList;

public class VersionAdapter extends RecyclerView.Adapter<VersionAdapter.VersionViewHolder> {

    private ArrayList<Version> versions;
    private Context context;
    private OnClickDeleteVersion onClickDeleteVersion;

    private int itemId;

    public VersionAdapter(ArrayList<Version> versions, Context context, OnClickDeleteVersion onClickDeleteVersion) {
        this.versions = versions;
        this.context = context;
        this.onClickDeleteVersion = onClickDeleteVersion;
        //notifyDataSetChanged();
    }

    @NonNull
    @Override
    public VersionAdapter.VersionViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_phone_version, parent, false);
        return new VersionViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull VersionAdapter.VersionViewHolder holder, int position) {
        holder.tvColor.setText(versions.get(position).getColor());
        holder.tvRamStorage.setText(versions.get(position).getRam() + "-" + versions.get(position).getStorage());
        holder.tvPrice.setText(Convert.INSTANCE.DinhDangTien(versions.get(position).getPrice()) + " đ");
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemId = holder.getAdapterPosition();
                if(itemId != RecyclerView.NO_POSITION){
                    onClickDeleteVersion.onClickDeleteVersion(itemId);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return versions.size();
    }

    public static class VersionViewHolder extends RecyclerView.ViewHolder {
        public ImageView delete;
        public TextView tvColor, tvRamStorage, tvPrice;;
        public VersionViewHolder(@NonNull View itemView) {
            super(itemView);
            tvColor = itemView.findViewById(R.id.tv_color);
            tvRamStorage = itemView.findViewById(R.id.tv_ram_storage);
            tvPrice = itemView.findViewById(R.id.tv_price);
            delete = itemView.findViewById(R.id.delete_version);
        }
    }
}
