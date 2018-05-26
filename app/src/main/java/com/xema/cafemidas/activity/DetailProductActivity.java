package com.xema.cafemidas.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.AppBarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.xema.cafemidas.R;
import com.xema.cafemidas.adapter.OrderItemAdapter;
import com.xema.cafemidas.common.PreferenceHelper;
import com.xema.cafemidas.model.OrderItem;
import com.xema.cafemidas.network.ApiUtil;
import com.xema.cafemidas.util.LoadingProgressDialog;
import com.xema.cafemidas.widget.FontTextView;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by xema0 on 2018-05-26.
 */

public class DetailProductActivity extends AppCompatActivity {

    @BindView(R.id.ftv_title)
    FontTextView ftvTitle;
    @BindView(R.id.tv_price)
    TextView tvPrice;
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tb_main)
    Toolbar tbMain;
    @BindView(R.id.abl_main)
    AppBarLayout ablMain;
    @BindView(R.id.rv_main)
    RecyclerView rvMain;
    
    private Context mContext;

    private List<OrderItem> mList;
    private OrderItemAdapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_product);
        ButterKnife.bind(this);
        mContext = this;

        initToolbar();
        initAdapter();
        getServerData();
    }


    private void initToolbar() {
        setSupportActionBar(tbMain);
        //if (getSupportActionBar() != null)
        //    getSupportActionBar().setDisplayShowTitleEnabled(false);
        //tbMain.setTitle("로딩중...");
    }

    private void initAdapter() {
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        rvMain.setLayoutManager(mLayoutManager);
        mList = new ArrayList<>();
        mAdapter = new OrderItemAdapter(this, mList);
        mAdapter.setHasStableIds(true);
        rvMain.setAdapter(mAdapter);
    }

    private void getServerData() {
        LoadingProgressDialog.showProgress(mContext);
        ApiUtil.getOrderService().getDetailOrder(PreferenceHelper.loadId(mContext), PreferenceHelper.loadPw(mContext), 13).enqueue(new Callback<List<OrderItem>>() {
            @Override
            public void onResponse(@NonNull Call<List<OrderItem>> call, @NonNull Response<List<OrderItem>> response) {
                LoadingProgressDialog.hideProgress();
                if (response.code() == 200) {
                    List<OrderItem> profileList = response.body();
                    if (profileList != null && !profileList.isEmpty()) {
                        rvMain.setVisibility(View.VISIBLE);
                        mList.clear();
                        mList.addAll(profileList);
                        mAdapter.notifyDataSetChanged();
                    } else {
                        Toast.makeText(mContext, getString(R.string.error_common), Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(mContext, getString(R.string.error_common), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<OrderItem>> call, @NonNull Throwable t) {
                LoadingProgressDialog.hideProgress();
                Toast.makeText(mContext, getString(R.string.error_network), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
