package com.xema.cafemidas.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.RequestManager;
import com.xema.cafemidas.R;
import com.xema.cafemidas.model.Category;

import java.util.ArrayList;

public class OrderCategoryAdapter extends RecyclerView.Adapter<OrderCategoryAdapter.ViewHolder> {
    private ArrayList<Category> list;
    private RequestManager requestManager;
    private onCategoryClickListener onCategoryClickListener;

    public OrderCategoryAdapter(ArrayList<Category> list, RequestManager requestManager, onCategoryClickListener onCategoryClickListener) {
        this.list = list;
        this.requestManager = requestManager;
        this.onCategoryClickListener = onCategoryClickListener;
    }

    public interface onCategoryClickListener {
        public void onClick(View view, int position);
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_category, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.txt_category.setText(list.get(position).getName());
        requestManager.load("http://blog.jinbo.net/attach/615/200937431.jpg").into(holder.im_category);
        holder.linearLayout.setOnClickListener(v -> {
            onCategoryClickListener.onClick(v, position);
        });

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public ImageView im_category;
        public TextView txt_category;
        public LinearLayout linearLayout;

        public ViewHolder(View itemView) {
            super(itemView);
            itemView.setOnClickListener(this);
            im_category = (ImageView) itemView.findViewById(R.id.order_im_category);
            txt_category = (TextView) itemView.findViewById(R.id.order_txt_category);
            linearLayout = (LinearLayout) itemView.findViewById(R.id.order_linear);

        }

        @Override
        public void onClick(View v) {

        }
    }
}
