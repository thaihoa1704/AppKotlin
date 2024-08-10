package com.example.mymobileapp.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;
import com.example.mymobileapp.R;
import com.example.mymobileapp.listener.ClickItemColorListener;
import com.example.mymobileapp.listener.OnClickDeleteColor;
import com.example.mymobileapp.model.ProductColor;
import java.util.ArrayList;

public class ColorAdapter1 extends RecyclerView.Adapter<ColorAdapter1.ColorViewHolder> {

    private ArrayList<ProductColor> colors;
    private Context context;
    private OnClickDeleteColor onClickDeleteColor;
    private ClickItemColorListener clickItemColorListener;
    private int itemId;

    public ColorAdapter1(ArrayList<ProductColor> colors,
                         Context context,
                         OnClickDeleteColor onClickDeleteColor,
                         ClickItemColorListener clickItemColorListener) {
        this.colors = colors;
        this.context = context;
        this.onClickDeleteColor = onClickDeleteColor;
        this.clickItemColorListener = clickItemColorListener;
        //notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ColorAdapter1.ColorViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(R.layout.item_color1, parent, false);
        return new ColorViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ColorAdapter1.ColorViewHolder holder, int position) {
        holder.cardColor.setCardBackgroundColor(android.graphics.Color.parseColor(colors.get(position).getColorCode()));
        holder.cardColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemId = holder.getAdapterPosition();
                clickItemColorListener.onClickColor(colors.get(itemId));
            }
        });

        holder.delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                itemId = holder.getAdapterPosition();
                if(itemId != RecyclerView.NO_POSITION){
                    onClickDeleteColor.onClickDeleteColor(itemId);
                }
            }
        });
    }

    @Override
    public int getItemCount() {
        return colors.size();
    }

    public static class ColorViewHolder extends RecyclerView.ViewHolder {
        public ImageView delete;
        public CardView cardColor;
        public ColorViewHolder(@NonNull View itemView) {
            super(itemView);
            cardColor = itemView.findViewById(R.id.color_card);
            delete = itemView.findViewById(R.id.delete_color);
        }
    }
}
