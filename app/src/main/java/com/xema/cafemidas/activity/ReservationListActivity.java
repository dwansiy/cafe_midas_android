package com.xema.cafemidas.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.Toast;

import com.xema.cafemidas.R;
import com.xema.cafemidas.common.PreferenceHelper;
import com.xema.cafemidas.dialog.SimpleTextDialog;
import com.xema.cafemidas.model.ApiResult;
import com.xema.cafemidas.model.Category;
import com.xema.cafemidas.network.ApiUtil;
import com.xema.cafemidas.util.LoadingProgressDialog;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReservationListActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    @BindView(R.id.tb_main)
    Toolbar tbMain;
    @BindView(R.id.fab_check)
    FloatingActionButton fabCheck;
    @BindView(R.id.nv_admin)
    NavigationView nvAdmin;
    @BindView(R.id.dl_main)
    DrawerLayout dlMain;
    @BindView(R.id.rv_main)
    RecyclerView rvMain;

    private Context mContext;

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
    }

    private void initToolbar() {
        setSupportActionBar(tbMain);
        //if (getSupportActionBar() != null)
        //    getSupportActionBar().setDisplayShowTitleEnabled(false);
        tbMain.setTitle("로딩중...");
    }

    private void initNavigationDrawer() {
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, dlMain, tbMain, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        dlMain.addDrawerListener(toggle);
        toggle.syncState();
        nvAdmin.setNavigationItemSelectedListener(this);
    }

    private void initListeners() {
        fabCheck.setOnClickListener(v -> {

        });
    }

    private void initAdapter() {

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
            Intent intent = new Intent(this, DetailProductActivity.class);
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
