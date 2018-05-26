package com.xema.cafemidas.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.widget.Button;
import android.widget.EditText;

import com.xema.cafemidas.R;
import com.xema.cafemidas.adapter.BuyDetailAdapter;
import com.xema.cafemidas.model.BuyDetail;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;

//todo
public class UserBuyDetailActivity extends AppCompatActivity {
    private RecyclerView.Adapter mAdapter;
    private ArrayList<BuyDetail> buy_detail_list;
    private ArrayList<BuyDetail> intent_list;

    @BindView(R.id.buy_detail_btn_order)
    Button buyDetailBtnOrder;
    @BindView(R.id.tb_buy_detail)
    Toolbar tbBuyDetail;
    @BindView(R.id.buy_detail_list)
    RecyclerView buyDetailList;
    @BindView(R.id.buy_detail_edit)
    EditText buyDetailEdit;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_buy_detail);
        ButterKnife.bind(this);
        buy_detail_list = new ArrayList<>();
        initToolbar();
        //setRecyclerView();
        //Intent intent = getIntent();

        buyDetailBtnOrder.setOnClickListener(v ->
        {

        });
    }

    private void initToolbar() {
        setSupportActionBar(tbBuyDetail);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    /*
    private void setRecyclerView() {
        mAdapter = new BuyDetailAdapter(buy_detail_list);
        buyDetailList.setHasFixedSize(true);
        buyDetailList.setLayoutManager(new LinearLayoutManager(this));
        buyDetailList.setAdapter(mAdapter);
        setList();
    }
    */

    private void setList() {
        buy_detail_list.clear();
        Intent intent = getIntent();
        intent_list = (ArrayList<BuyDetail>) intent.getSerializableExtra("list");
        buy_detail_list.addAll(intent_list);
        mAdapter.notifyDataSetChanged();
    }
}
