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
import com.xema.cafemidas.model.WaitingOrder;
import com.xema.cafemidas.model.WaitingTime;
import com.xema.cafemidas.network.ApiUtil;
import com.xema.cafemidas.util.LoadingProgressDialog;
import com.xema.cafemidas.widget.FontTextView;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserNavigationActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {


    @BindView(R.id.tb_main)
    Toolbar tbMain;
    @BindView(R.id.tv_waiting_queue)
    FontTextView tvWaitingQueue;
    @BindView(R.id.tv_waiting_time)
    FontTextView tvWaitingTime;
    @BindView(R.id.tv_point)
    FontTextView tvPoint;
    @BindView(R.id.srl_main)
    SwipeRefreshLayout srlMain;
    @BindView(R.id.fab_order)
    FloatingActionButton fabOrder;
    @BindView(R.id.nv_admin)
    NavigationView nvAdmin;
    @BindView(R.id.dl_main)
    DrawerLayout dlMain;
    private Context mContext;
    private List<Order> mList;
    private OrderAdapter mAdapter;

    private int counter1 = 0;
    private int counter2 = 0;
    private int counter3 = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_navigation);
        mContext = this;
        ButterKnife.bind(this);

        initToolbar();
        initNavigationDrawer();
        initListeners();

    }

    private boolean flag1 = false;
    private boolean flag2 = false;
    private boolean flag3 = false;

    private void getServerData() {
        flag1 = false;
        flag2 = false;
        flag3 = false;
        new Thread() {
            @Override
            public void run() {
                while (!(flag1 && flag2 && flag3)) {

                }
                srlMain.setRefreshing(false);
            }
        }.start();
        ApiUtil.getAccountService().signIn(PreferenceHelper.loadId(this), PreferenceHelper.loadPw(this)).enqueue(new Callback<Profile>() {
            @Override
            public void onResponse(Call<Profile> call, Response<Profile> response) {
                flag1 = true;
                if (response.code() == 200) {
                    Profile profile = response.body();
                    if (profile == null) return;
                    PreferenceHelper.saveMyProfile(UserNavigationActivity.this, profile);

                    counter1 = 0;
                    new Thread(() -> {
                        while (counter1 < profile.getPoint()) {
                            try {
                                Thread.sleep(5);
                            } catch (InterruptedException e) {
                            }
                            tvPoint.post(() -> tvPoint.setText("" + counter1));
                            counter1++;
                        }
                    }).start();

                } else {
                    Toast.makeText(UserNavigationActivity.this, "아이디 혹은 패스워드가 다릅니다.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<Profile> call, Throwable t) {
                flag1 = true;
            }
        });
        ApiUtil.getOrderService().getWaitingOrder().enqueue(new Callback<WaitingOrder>() {
            @Override
            public void onResponse(Call<WaitingOrder> call, Response<WaitingOrder> response) {
                flag2 = true;
                if (response.code() == 200) {
                    WaitingOrder waitingOrder = response.body();
                    if (waitingOrder != null) {
                        counter2 = 0;
                        new Thread(() -> {
                            while (counter2 < waitingOrder.getWaitingOrder()) {
                                try {
                                    Thread.sleep(20);
                                } catch (InterruptedException e) {
                                }
                                tvWaitingQueue.post(() -> tvWaitingQueue.setText("" + counter2));
                                counter2++;
                            }
                        }).start();
                    }
                }
            }

            @Override
            public void onFailure(Call<WaitingOrder> call, Throwable t) {
                flag2 = true;

            }
        });
        ApiUtil.getOrderService().getWaitingTime().enqueue(new Callback<WaitingTime>() {
            @Override
            public void onResponse(@NonNull Call<WaitingTime> call, @NonNull Response<WaitingTime> response) {
                flag3 = true;
                if (response.code() == 200) {
                    WaitingTime body = response.body();
                    if (body != null) {
                        counter3 = 0;
                        new Thread(() -> {
                            while (counter3 < body.getWaitingTime()) {
                                try {
                                    Thread.sleep(10);
                                } catch (InterruptedException e) {
                                }
                                tvWaitingTime.post(() -> tvWaitingTime.setText(counter3 + "분"));
                                counter3++;
                            }
                        }).start();
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<WaitingTime> call, @NonNull Throwable t) {
                flag3 = true;
            }
        });
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

        getServerData();
    }

    private void initToolbar() {
        setSupportActionBar(tbMain);
        //if (getSupportActionBar() != null)
        //    getSupportActionBar().setDisplayShowTitleEnabled(false);
        tbMain.setTitle("환영합니다! " + PreferenceHelper.loadMyProfile(this).getName() + "님");
    }

    private void initNavigationDrawer() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, dlMain, tbMain, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        dlMain.addDrawerListener(toggle);
        toggle.syncState();
        nvAdmin.setNavigationItemSelectedListener(this);
    }

    private void initListeners() {
        fabOrder.setOnClickListener(v -> {
            startActivity(new Intent(this, UserOrderActivity.class));
        });
        srlMain.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getServerData();
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

        if (id == R.id.nav_reservation) {
            Intent intent = new Intent(this, PendingReservationListActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_monthly) {
            Intent intent = new Intent(this, UserBuyListActivity.class);
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
