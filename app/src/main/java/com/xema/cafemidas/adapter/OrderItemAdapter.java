package com.xema.cafemidas.adapter;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.xema.cafemidas.R;
import com.xema.cafemidas.common.Constants;
import com.xema.cafemidas.common.GlideApp;
import com.xema.cafemidas.model.OrderItem;
import com.xema.cafemidas.model.Product;
import com.xema.cafemidas.util.CommonUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class OrderItemAdapter extends RecyclerView.Adapter<OrderItemAdapter.ListItemViewHolder> {
    private static final String TAG = OrderItemAdapter.class.getSimpleName();

    private Context mContext = null;
    private List<OrderItem> mDataList = null;

    public OrderItemAdapter(Context context, List<OrderItem> mDataList) {
        super();
        this.mContext = context;
        this.mDataList = mDataList;
    }

    @NonNull
    @Override
    public ListItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_item, parent, false);
        return new ListItemViewHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull ListItemViewHolder holder, int position) {
        final OrderItem orderItem = mDataList.get(position);

        holder.tvCnt.setText(orderItem.getCnt() + " 개");

        final Product product = orderItem.getMenu();
        holder.tvName.setText(product.getName());
        holder.tvPrice.setText(CommonUtil.toDecimalFormat(product.getPrice()) + " 원");
        GlideApp.with(mContext).load(Constants.BASE_URL + product.getImage()).into(holder.ivImage);
    }


    @Override
    public long getItemId(int position) {
        return mDataList != null ? mDataList.get(0).getId() : position;
    }

    @Override
    public int getItemCount() {
        return mDataList == null ? 0 : mDataList.size();
    }

    final static class ListItemViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_image)
        ImageView ivImage;
        @BindView(R.id.tv_name)
        TextView tvName;
        @BindView(R.id.tv_price)
        TextView tvPrice;
        @BindView(R.id.tv_cnt)
        TextView tvCnt;
        @BindView(R.id.tv_taking_time)
        TextView tvTakingTime;
        @BindView(R.id.ll_container)
        LinearLayout llContainer;

        ListItemViewHolder(View itemView, int viewType) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }
}
