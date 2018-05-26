package com.xema.cafemidas.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.Toast;

import com.xema.cafemidas.R;
import com.xema.cafemidas.adapter.OrderAdapter;
import com.xema.cafemidas.common.PreferenceHelper;
import com.xema.cafemidas.model.Order;
import com.xema.cafemidas.network.ApiUtil;
import com.xema.cafemidas.util.LoadingProgressDialog;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PendingReservationListActivity extends AppCompatActivity {
    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.tb_main)
    Toolbar tbMain;
    @BindView(R.id.rv_main)
    RecyclerView rvMain;
    @BindView(R.id.srl_main)
    SwipeRefreshLayout srlMain;
    private Context mContext;
    private List<Order> mList;
    private OrderAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pending_reservation_list);
        mContext = this;
        ButterKnife.bind(this);

        initToolbar();
        initListeners();
        initAdapter();

        getServerData();
    }

    private void initToolbar() {
        setSupportActionBar(tbMain);
        //if (getSupportActionBar() != null)
        //    getSupportActionBar().setDisplayShowTitleEnabled(false);
    }


    private void initListeners() {
        ivBack.setOnClickListener(v -> {
            finish();
        });
    }

    private void initAdapter() {
        LinearLayoutManager mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);

        rvMain.setLayoutManager(mLayoutManager);
        mList = new ArrayList<>();
        mAdapter = new OrderAdapter(this, mList);
        mAdapter.setHasStableIds(true);
        rvMain.setAdapter(mAdapter);
        srlMain.setOnRefreshListener(this::getServerData);
    }

    private void getServerData() {
        LoadingProgressDialog.showProgress(mContext);
        ApiUtil.getOrderService().getOrderList(PreferenceHelper.loadId(mContext), PreferenceHelper.loadPw(mContext), 0).enqueue(new Callback<List<Order>>() {
            @Override
            public void onResponse(@NonNull Call<List<Order>> call, @NonNull Response<List<Order>> response) {
                LoadingProgressDialog.hideProgress();
                srlMain.setRefreshing(false);
                if (response.code() == 200) {
                    List<Order> orderList = response.body();
                    if (orderList != null) {
                        mList.clear();
                        mList.addAll(orderList);
                        mAdapter.notifyDataSetChanged();
                    }
                } else {
                    Toast.makeText(mContext, getString(R.string.error_common), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Order>> call, @NonNull Throwable t) {
                LoadingProgressDialog.hideProgress();
                srlMain.setRefreshing(false);
                Toast.makeText(mContext, getString(R.string.error_network), Toast.LENGTH_SHORT).show();
            }
        });
    }

}
