package com.xema.cafemidas.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;

import com.xema.cafemidas.R;

import butterknife.BindView;
import butterknife.ButterKnife;

//
public class UserBuyListActivity extends AppCompatActivity {
    @BindView(R.id.tb_buy_list)
    Toolbar tbBuyList;
    @BindView(R.id.buy_spinner_year)
    Spinner buySpinnerYear;
    @BindView(R.id.buy_spinner_month)
    Spinner buySpinnerMonth;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_buy_list);
        ButterKnife.bind(this);
        initToolbar();
        initSpinner();
        //setRecyclerView();
    }

    private void initSpinner()
    {
        ArrayAdapter yearAdapter = ArrayAdapter.createFromResource(this,
                R.array.date_year,android.R.layout.simple_spinner_item);
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        buySpinnerYear.setAdapter(yearAdapter);
        buySpinnerYear.setSelection(0);
        buySpinnerMonth.setSelection(0);

        ArrayAdapter monthAdapter = ArrayAdapter.createFromResource(this,
                R.array.date_month, android.R.layout.simple_spinner_item);
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        buySpinnerMonth.setAdapter(monthAdapter);

        buySpinnerMonth.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.d("spinner",buySpinnerYear.getSelectedItem().toString() +"!!!"
                + buySpinnerMonth.getSelectedItem().toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    private void initToolbar() {
        setSupportActionBar(tbBuyList);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayShowTitleEnabled(false);
    }
}
