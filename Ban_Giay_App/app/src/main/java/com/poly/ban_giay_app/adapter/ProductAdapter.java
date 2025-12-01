package com.poly.ban_giay_app.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.poly.ban_giay_app.ProductDetailActivity;
import com.poly.ban_giay_app.R;
import com.poly.ban_giay_app.models.Product;

import java.util.List;

public class ProductAdapter extends RecyclerView.Adapter<ProductAdapter.VH> {
    private final List<Product> items;

    public ProductAdapter(List<Product> items) {
        this.items = items;
    }

    public static class VH extends RecyclerView.ViewHolder {
        ImageView img;
        TextView name, priceOld, priceNew;

        public VH(@NonNull View itemView) {
            super(itemView);
            img = itemView.findViewById(R.id.imgProduct);
            name = itemView.findViewById(R.id.txtName);
            priceOld = itemView.findViewById(R.id.txtPriceOld);
            priceNew = itemView.findViewById(R.id.txtPriceNew);
        }
    }

    @NonNull
    @Override
    public VH onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_product, parent, false);
        return new VH(v);
    }

    @Override
    public void onBindViewHolder(@NonNull VH holder, int position) {
        Product p = items.get(position);

        if (p.imageUrl != null && !p.imageUrl.isEmpty()) {
            if (p.imageUrl.startsWith("http://") || p.imageUrl.startsWith("https://")) {
                Glide.with(holder.itemView.getContext())
                        .load(p.imageUrl)
                        .placeholder(R.drawable.giaymau)
                        .error(R.drawable.giaymau)
                        .into(holder.img);
            } else {
                int imageResId = getImageResourceId(holder.itemView.getContext(), p.imageUrl);
                if (imageResId != 0) {
                    holder.img.setImageResource(imageResId);
                } else {
                    holder.img.setImageResource(R.drawable.giaymau);
                }
            }
        } else if (p.imageRes != 0) {
            holder.img.setImageResource(p.imageRes);
        } else {
            holder.img.setImageResource(R.drawable.giaymau);
        }

        holder.name.setText(p.name);

        holder.img.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), ProductDetailActivity.class);
            intent.putExtra("product", p);
            v.getContext().startActivity(intent);
        });

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(v.getContext(), ProductDetailActivity.class);
            intent.putExtra("product", p);
            v.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return items.size();
    }

    private int getImageResourceId(Context context, String imageName) {
        String name = imageName;
        if (name.contains("/")) {
            name = name.substring(name.lastIndexOf("/") + 1);
        }
        if (name.contains(".")) {
            name = name.substring(0, name.lastIndexOf("."));
        }
        return context.getResources().getIdentifier(name, "drawable", context.getPackageName());
    }
}

