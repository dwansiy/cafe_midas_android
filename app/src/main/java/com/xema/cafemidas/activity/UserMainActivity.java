package com.xema.cafemidas.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.xema.cafemidas.R;

import butterknife.BindView;
import butterknife.ButterKnife;

public class UserMainActivity extends AppCompatActivity {
    @BindView(R.id.tb_user_main)
    Toolbar tbUserMain;
    @BindView(R.id.user_im_order)
    ImageView userImOrder;
    @BindView(R.id.user_txt_buy_list)
    TextView userTxtBuyList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_main);
        ButterKnife.bind(this);

        initToolbar();
        initListener();
    }


    private void initListener() {
        userImOrder.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(v.getContext(), UserOrderActivity.class));
            }
        });
        userTxtBuyList.setOnClickListener(v ->
        {
            startActivity(new Intent(v.getContext(),UserBuyListActivity.class));
        });

    }

    private void initToolbar() {
        setSupportActionBar(tbUserMain);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayShowTitleEnabled(false);
    }
}
