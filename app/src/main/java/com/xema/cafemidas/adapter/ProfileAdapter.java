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
import com.xema.cafemidas.model.Profile;
import com.xema.cafemidas.network.RetrofitClient;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProfileAdapter extends RecyclerView.Adapter<ProfileAdapter.ListItemViewHolder> {
    private static final String TAG = ProfileAdapter.class.getSimpleName();

    private Context mContext = null;
    private List<Profile> mDataList = null;

    public ProfileAdapter(Context context, List<Profile> mDataList) {
        super();
        this.mContext = context;
        this.mDataList = mDataList;
    }

    @NonNull
    @Override
    public ListItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_profile, parent, false);
        return new ListItemViewHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(@NonNull ListItemViewHolder holder, int position) {
        final Profile profile = mDataList.get(position);

        holder.tvName.setText(profile.getName());
        holder.tvComment.setText(profile.getComment());
        GlideApp.with(mContext).load(Constants.BASE_URL + profile.getProfileImage()).into(holder.ivProfile);
        if (profile.getType() == 2){
            holder.tvAuth.setText("최고 관리자");
        }else if (profile.getType() == 1){
            holder.tvAuth.setText("서브 관리자");
        }else if (profile.getType() == 0){
            holder.tvAuth.setText("사용자");
        }
    }

    @Override
    public long getItemId(int position) {
        return mDataList != null ? mDataList.get(0).getUid() : position;
    }

    @Override
    public int getItemCount() {
        return mDataList == null ? 0 : mDataList.size();
    }

    final static class ListItemViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_profile)
        ImageView ivProfile;
        @BindView(R.id.tv_profile)
        TextView tvProfile;
        @BindView(R.id.tv_name)
        TextView tvName;
        @BindView(R.id.tv_comment)
        TextView tvComment;
        @BindView(R.id.tv_auth)
        TextView tvAuth;
        @BindView(R.id.ll_container)
        LinearLayout llContainer;

        ListItemViewHolder(View itemView, int viewType) {
            super(itemView);
            ButterKnife.bind(this, itemView);
        }

    }
}
