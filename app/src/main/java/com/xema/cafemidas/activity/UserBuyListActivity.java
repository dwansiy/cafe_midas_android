package com.xema.cafemidas.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.xema.cafemidas.R;
import com.xema.cafemidas.adapter.BuyListAdapter;
import com.xema.cafemidas.adapter.OrderAdapter;
import com.xema.cafemidas.common.PreferenceHelper;
import com.xema.cafemidas.model.Order;
import com.xema.cafemidas.network.ApiUtil;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserBuyListActivity extends AppCompatActivity {
    @BindView(R.id.tb_buy_list)
    Toolbar tbBuyList;
    @BindView(R.id.buy_spinner_year)
    Spinner buySpinnerYear;
    @BindView(R.id.buy_spinner_month)
    Spinner buySpinnerMonth;
    @BindView(R.id.buy_list)
    RecyclerView buyList;

    private ArrayList<Order> buy_list;
    private RecyclerView.Adapter mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_buy_list);
        ButterKnife.bind(this);
        buy_list = new ArrayList<>();

        initToolbar();
        initSpinner();
        setRecyclerView();
    }

    private void initSpinner() {
        ArrayAdapter yearAdapter = ArrayAdapter.createFromResource(this,
                R.array.date_year, android.R.layout.simple_spinner_item);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        buySpinnerYear.setAdapter(yearAdapter);
        buySpinnerYear.setSelection(0);
        buySpinnerMonth.setSelection(4);

        ArrayAdapter monthAdapter = ArrayAdapter.createFromResource(this,
                R.array.date_month, android.R.layout.simple_spinner_item);
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        buySpinnerMonth.setAdapter(monthAdapter);

        buySpinnerMonth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d("spinner", buySpinnerYear.getSelectedItem().toString() + "!!!"
                        + buySpinnerMonth.getSelectedItem().toString());
                setList();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void setRecyclerView() {
        //mAdapter = new BuyListAdapter(buy_list, new BuyListAdapter.onClickListener() {
        //    @Override
        //    public void onClick(View view, int position) {
        //    }
        //});
        mAdapter = new OrderAdapter(this, buy_list);
        buyList.setHasFixedSize(true);
        buyList.setLayoutManager(new LinearLayoutManager(this));
        buyList.setAdapter(mAdapter);
        setList();


    }

    private void setList() {
        buy_list.clear();
        String year = buySpinnerYear.getSelectedItem().toString().replace("년", "");
        String month = buySpinnerMonth.getSelectedItem().toString().replace("월", "");
        Log.d("buy_list", year + " // " + month);
        retrofit2.Call<List<Order>> call = ApiUtil.getOrderService().getOrderList(PreferenceHelper.loadId(this), PreferenceHelper.loadPw(this), 2, Integer.parseInt(year), Integer.parseInt(month));

        call.enqueue(new Callback<List<Order>>() {
            @Override
            public void onResponse(Call<List<Order>> call, Response<List<Order>> response) {
                if (response.body() != null && !response.body().isEmpty()) {
                    buy_list.addAll(response.body());
                    Log.d("ttt", response.toString());
                }
                mAdapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Call<List<Order>> call, Throwable t) {
                Log.d("buy_list", t.getMessage());
            }
        });

    }

    private void initToolbar() {
        setSupportActionBar(tbBuyList);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayShowTitleEnabled(false);
    }
}
