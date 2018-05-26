package com.xema.cafemidas.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.xema.cafemidas.R;
import com.xema.cafemidas.model.BuyDetail;

import java.util.ArrayList;

public class BuyDetailAdapter extends RecyclerView.Adapter<BuyDetailAdapter.ViewHolder> {
    private ArrayList<BuyDetail> list;
    private onClickListener onClickListener;

    public BuyDetailAdapter(ArrayList<BuyDetail> list , onClickListener onClickListener)
    {
        this.list = list;
        this.onClickListener = onClickListener;
    }

    public interface onClickListener
    {
        public void onClick(View view, int position, int status);
    }

    @NonNull
    @Override
    public BuyDetailAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_buy_detail, parent, false);
        BuyDetailAdapter.ViewHolder viewHolder = new BuyDetailAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(BuyDetailAdapter.ViewHolder holder, int position) {
        holder.txt_num.setText(list.get(position).getNum());
        holder.txt_name.setText(list.get(position).getName());
        holder.txt_price.setText(list.get(position).getPrice());
        holder.btn_plus.setOnClickListener(v ->
        {
            onClickListener.onClick(v,position,1);
        });
        holder.btn_minus.setOnClickListener(v ->
        {
            onClickListener.onClick(v,position,0);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder
    {
        public TextView txt_name;
        public TextView txt_num;
        public TextView txt_price;
        public Button btn_plus;
        public Button btn_minus;

        public ViewHolder(View itemView) {
            super(itemView);
            txt_num = (TextView)itemView.findViewById(R.id.buy_detail_txt_num);
            txt_name = (TextView)itemView.findViewById(R.id.buy_detail_txt_name);
            txt_price = (TextView)itemView.findViewById(R.id.buy_detail_txt_price);
            btn_plus = (Button)itemView.findViewById(R.id.buy_detail_btn_plus);
            btn_minus = (Button)itemView.findViewById(R.id.buy_detail_btn_minus);
        }

    }
}
