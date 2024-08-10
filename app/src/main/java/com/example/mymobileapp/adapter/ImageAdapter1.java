package com.example.mymobileapp.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.mymobileapp.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ImageAdapter1 extends RecyclerView.Adapter<ImageAdapter1.ImageViewHolder> {

    private ArrayList<Uri> images;
    private Context context;

    private int itemId;

    public ImageAdapter1(ArrayList<Uri> images, Context context) {
        this.images = images;
        this.context = context;
        //notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ImageAdapter1.ImageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_image1, parent, false);
        return new ImageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageAdapter1.ImageViewHolder holder, int position) {
        Glide.with(context).load(images.get(position)).into(holder.imageView);
        //holder.imageView.setImageURI(images.get(position));
        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemId = holder.getAdapterPosition();
                deleteImage(itemId);
            }
        });
    }

    private void deleteImage(int itemId) {
        images.remove(itemId);
        notifyItemRemoved(itemId);
    }

    @Override
    public int getItemCount() {
        return images.size();
    }

    public static class ImageViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageView, delete;
        public ImageViewHolder(@NonNull View itemView) {
            super(itemView);
            imageView = itemView.findViewById(R.id.image);
            delete = itemView.findViewById(R.id.delete);
        }
    }
}
