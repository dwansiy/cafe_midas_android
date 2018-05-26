package com.xema.cafemidas.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.xema.cafemidas.R;
import com.xema.cafemidas.adapter.OrderAdapter;
import com.xema.cafemidas.common.Constants;
import com.xema.cafemidas.common.GlideApp;
import com.xema.cafemidas.common.PreferenceHelper;
import com.xema.cafemidas.dialog.SimpleTextDialog;
import com.xema.cafemidas.model.ApiResult;
import com.xema.cafemidas.model.Order;
import com.xema.cafemidas.model.Profile;
import com.xema.cafemidas.network.ApiUtil;
import com.xema.cafemidas.util.LoadingProgressDialog;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReservationListActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    @BindView(R.id.tb_main)
    Toolbar tbMain;
    @BindView(R.id.nv_admin)
    NavigationView nvAdmin;
    @BindView(R.id.dl_main)
    DrawerLayout dlMain;
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
        setContentView(R.layout.activity_reservation_list);
        mContext = this;
        ButterKnife.bind(this);

        initToolbar();
        initNavigationDrawer();
        initListeners();
        initAdapter();

        getServerData();
    }

    @Override
    protected void onResume() {
        super.onResume();
        View view = nvAdmin.getHeaderView(0);
        Profile profile = PreferenceHelper.loadMyProfile(mContext);
        if (profile == null) return;
        GlideApp.with(mContext).load(Constants.BASE_URL + profile.getProfileImage()).into((ImageView) view.findViewById(R.id.iv_profile));
        ((TextView) view.findViewById(R.id.tv_name)).setText(profile.getName());
        ((TextView) view.findViewById(R.id.tv_comment)).setText(profile.getComment());
    }

    private void initToolbar() {
        setSupportActionBar(tbMain);
        //if (getSupportActionBar() != null)
        //    getSupportActionBar().setDisplayShowTitleEnabled(false);
        tbMain.setTitle("주문 목록");
    }

    private void initNavigationDrawer() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, dlMain, tbMain, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        dlMain.addDrawerListener(toggle);
        toggle.syncState();
        nvAdmin.setNavigationItemSelectedListener(this);
    }

    private void initListeners() {
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

    @Override
    public void onBackPressed() {
        if (dlMain.isDrawerOpen(GravityCompat.START)) {
            dlMain.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.nav_menu) {
            Intent intent = new Intent(this, CategoryActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_customer) {
            Intent intent = new Intent(this, ProfileListActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_order) {
            Intent intent = new Intent(this, CompleteReservationListActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_sign_out) {
            Toast.makeText(mContext, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show();
            PreferenceHelper.resetAll(mContext);
            Intent intent = new Intent(mContext, SignInActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(intent);
        } else if (id == R.id.nav_drop_out) {
            showDropOutDialog();
        } else if (id == R.id.nav_edit_my_profile) {
            Intent intent = new Intent(mContext, EditProfileActivity.class);
            intent.putExtra("profile", PreferenceHelper.loadMyProfile(mContext));
            mContext.startActivity(intent);
        }
        dlMain.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showDropOutDialog() {
        SimpleTextDialog dialog = new SimpleTextDialog(mContext, "회원 탈퇴" + "\n\n" + "정말 탈퇴하시겠습니까?" + "\n\n" + "지워진 정보는 복구되지 않습니다");
        dialog.setOnPositiveListener("삭제", () -> {
            LoadingProgressDialog.showProgress(mContext);
            ApiUtil.getAccountService().dropAccount(PreferenceHelper.loadId(mContext), PreferenceHelper.loadPw(mContext)).enqueue(new Callback<ApiResult>() {
                @Override
                public void onResponse(@NonNull Call<ApiResult> call, @NonNull Response<ApiResult> response) {
                    LoadingProgressDialog.hideProgress();
                    if (response.code() == 200) {
                        Toast.makeText(mContext, "탈퇴 되었습니다.", Toast.LENGTH_SHORT).show();
                        PreferenceHelper.resetAll(mContext);
                        Intent intent = new Intent(mContext, SignInActivity.class);
                        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    } else {
                        Toast.makeText(mContext, getString(R.string.error_common), Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(@NonNull Call<ApiResult> call, @NonNull Throwable t) {
                    LoadingProgressDialog.hideProgress();
                    Toast.makeText(mContext, getString(R.string.error_network), Toast.LENGTH_SHORT).show();
                }
            });
            dialog.show();
        });
    }
}
