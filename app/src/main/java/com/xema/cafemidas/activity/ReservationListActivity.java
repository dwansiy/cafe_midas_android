package com.xema.cafemidas.activity;

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

import com.xema.cafemidas.R;
import com.xema.cafemidas.model.Category;

import butterknife.BindView;
import butterknife.ButterKnife;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation_list);
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
        }
        dlMain.closeDrawer(GravityCompat.START);
        return true;
    }
}
