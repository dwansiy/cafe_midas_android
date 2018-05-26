package com.xema.cafemidas.adapter;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xema.cafemidas.R;
import com.xema.cafemidas.model.Order;
import com.xema.cafemidas.util.CommonUtil;

import java.util.ArrayList;

public class BuyListAdapter extends RecyclerView.Adapter<BuyListAdapter.ViewHolder> {
    private ArrayList<Order> list;
    private onClickListener onClickListener;

    public interface onClickListener {
        public void onClick(View view, int position);
    }

    public BuyListAdapter(ArrayList<Order> list, onClickListener onClickListener) {
        this.onClickListener = onClickListener;
        this.list = list;
    }

    @NonNull
    @Override
    public BuyListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_buy_list, parent, false);
        BuyListAdapter.ViewHolder viewHolder = new BuyListAdapter.ViewHolder(view);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(BuyListAdapter.ViewHolder holder, int position) {
        final Order order = list.get(position);

        holder.txt_date.setText(list.get(position).getDate().substring(0, 10));
        holder.txt_name.setText(TextUtils.isEmpty(order.getComment()) ? "X" : order.getComment());
        holder.txt_price.setText(CommonUtil.toDecimalFormat(order.getPrice()));
        holder.linearLayout.setOnClickListener(v ->
        {
            onClickListener.onClick(v, position);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView txt_date;
        public TextView txt_name;
        public TextView txt_price;
        public LinearLayout linearLayout;


        public ViewHolder(View itemView) {
            super(itemView);
            txt_date = (TextView) itemView.findViewById(R.id.buy_list_txt_date);
            txt_name = (TextView) itemView.findViewById(R.id.buy_list_txt_name);
            txt_price = (TextView) itemView.findViewById(R.id.buy_list_txt_price);
            linearLayout = (LinearLayout) itemView.findViewById(R.id.buy_list_layout);
        }

    }
}
