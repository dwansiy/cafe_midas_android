package com.xema.cafemidas.adapter;

import android.app.Activity;
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
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.makeramen.roundedimageview.RoundedImageView;
import com.xema.cafemidas.R;
import com.xema.cafemidas.activity.EditProfileActivity;
import com.xema.cafemidas.common.Constants;
import com.xema.cafemidas.common.GlideApp;
import com.xema.cafemidas.common.PreferenceHelper;
import com.xema.cafemidas.model.ApiResult;
import com.xema.cafemidas.model.Profile;
import com.xema.cafemidas.network.ApiUtil;
import com.xema.cafemidas.network.RetrofitClient;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

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
        holder.tvComment.setText(TextUtils.isEmpty(profile.getComment()) ? "X" : profile.getComment());
        GlideApp.with(mContext).load(Constants.BASE_URL + profile.getProfileImage()).into(holder.ivProfile);
        if (profile.getType() == Constants.AUTH_SUPER_ADMIN) {
            holder.tvAuth.setText("관리자");
        } else if (profile.getType() == Constants.AUTH_SUB_ADMIN) {
            holder.tvAuth.setText("관리자");
        } else if (profile.getType() == Constants.AUTH_USER) {
            holder.tvAuth.setText("사용자");
        }

        holder.llContainer.setOnLongClickListener(v -> {
            PopupMenu menu = new PopupMenu(mContext, holder.itemView);
            ((Activity) mContext).getMenuInflater().inflate(R.menu.menu_delete_edit, menu.getMenu());
            menu.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.menu_delete) {
                    if (profile.getUid() != PreferenceHelper.loadMyProfile(mContext).getUid()) {
                        attemptDeleteProfile(profile, position);
                    } else {
                        Toast.makeText(mContext, "자기 자신은 삭제할 수 없습니다", Toast.LENGTH_SHORT).show();
                    }
                } else if (item.getItemId() == R.id.menu_edit) {
                    Intent intent = new Intent(mContext, EditProfileActivity.class);
                    intent.putExtra("profile", profile);
                    mContext.startActivity(intent);
                }
                return false;
            });
            menu.show();
            return false;
        });
    }

    private void attemptDeleteProfile(Profile profile, int pos) {
        ApiUtil.getAccountService().dropAccountById(PreferenceHelper.loadId(mContext), PreferenceHelper.loadPw(mContext), profile.getUid()).enqueue(new Callback<ApiResult>() {
            @Override
            public void onResponse(@NonNull Call<ApiResult> call, @NonNull Response<ApiResult> response) {
                if (response.code() == 200) {
                    Toast.makeText(mContext, "삭제되었습니다.", Toast.LENGTH_SHORT).show();
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
        return mDataList != null ? mDataList.get(position).getUid() : position;
    }

    @Override
    public int getItemCount() {
        return mDataList == null ? 0 : mDataList.size();
    }

    final static class ListItemViewHolder extends RecyclerView.ViewHolder {
        @BindView(R.id.iv_profile)
        RoundedImageView ivProfile;
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
