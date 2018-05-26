package com.xema.cafemidas.adapter;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.makeramen.roundedimageview.RoundedImageView;
import com.xema.cafemidas.R;
import com.xema.cafemidas.activity.DetailProductActivity;
import com.xema.cafemidas.activity.ReservationListActivity;
import com.xema.cafemidas.common.Constants;
import com.xema.cafemidas.common.GlideApp;
import com.xema.cafemidas.common.PreferenceHelper;
import com.xema.cafemidas.model.ApiResult;
import com.xema.cafemidas.model.Order;
import com.xema.cafemidas.model.Profile;
import com.xema.cafemidas.network.ApiUtil;
import com.xema.cafemidas.util.CommonUtil;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class OrderAdapter extends RecyclerView.Adapter<OrderAdapter.ListItemViewHolder> {
    private static final String TAG = OrderAdapter.class.getSimpleName();

    private Context mContext = null;
    private List<Order> mDataList = null;

    public OrderAdapter(Context context, List<Order> mDataList) {
        super();
        this.mContext = context;
        this.mDataList = mDataList;
    }

    @NonNull
    @Override
    public ListItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order, parent, false);
        return new ListItemViewHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull ListItemViewHolder holder, int position) {
        final Order order = mDataList.get(position);

        holder.tvComment.setText(TextUtils.isEmpty(order.getComment()) ? "X" : order.getComment());
        holder.tvPrice.setText(CommonUtil.toDecimalFormat(order.getPrice()));
        // TODO: 2018-05-27 포매팅
        holder.tvDate.setText(CommonUtil.formatDate(order.getDate(), mContext));

        holder.llContainer.setOnClickListener(v -> {
            Intent intent = new Intent(mContext, DetailProductActivity.class);
            intent.putExtra("id", order.getId());
            mContext.startActivity(intent);
        });
        if (position == 0 && (mContext instanceof ReservationListActivity)) {
            holder.ivCheck.setVisibility(View.VISIBLE);
            holder.ivCheck.setOnClickListener(v -> {
                attemptCheck(order, position);
            });
        } else {
            holder.ivCheck.setVisibility(View.GONE);
        }

        final Profile profile = order.getProfile();
        holder.tvName.setText(profile.getName() + " 고객님 주문");
        GlideApp.with(mContext).load(Constants.BASE_URL + profile.getProfileImage()).error(R.drawable.ic_profile_default).into(holder.ivProfile);
    }

    public void attemptCheck(Order order, int pos) {
        ApiUtil.getOrderService().changeOrderState(PreferenceHelper.loadId(mContext), PreferenceHelper.loadPw(mContext), order.getId()).enqueue(new Callback<ApiResult>() {
            @Override
            public void onResponse(@NonNull Call<ApiResult> call, @NonNull Response<ApiResult> response) {
                if (response.code() == 200) {
                    mDataList.remove(pos);
                    notifyItemRemoved(pos);
                    notifyItemRangeChanged(pos, getItemCount());
                } else {
                    Toast.makeText(mContext, mContext.getString(R.string.error_common), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResult> call, @NonNull Throwable t) {
                Toast.makeText(mContext, mContext.getString(R.string.error_network), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public long getItemId(int position) {
        return mDataList != null ? mDataList.get(position).getId() : position;
    }

    @Override
    public int getItemCount() {
        return mDataList == null ? 0 : mDataList.size();
    }

    final static class ListItemViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_profile)
        RoundedImageView ivProfile;
        @BindView(R.id.tv_name)
        TextView tvName;
        @BindView(R.id.tv_date)
        TextView tvDate;
        @BindView(R.id.tv_comment)
        TextView tvComment;
        @BindView(R.id.tv_price)
        TextView tvPrice;
        @BindView(R.id.ll_container)
        LinearLayout llContainer;
        @BindView(R.id.iv_check)
        ImageView ivCheck;

        ListItemViewHolder(View itemView, int viewType) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }
}
