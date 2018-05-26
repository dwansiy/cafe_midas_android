package com.xema.cafemidas.activity;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.xema.cafemidas.R;
import com.xema.cafemidas.model.ApiResult;
import com.xema.cafemidas.network.ApiUtil;
import com.xema.cafemidas.util.LoadingProgressDialog;

import butterknife.BindView;
import butterknife.ButterKnife;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class AddProfileActivity extends AppCompatActivity {

    @BindView(R.id.iv_back)
    ImageView ivBack;
    @BindView(R.id.iv_done)
    ImageView ivDone;
    @BindView(R.id.tb_main)
    Toolbar tbMain;
    @BindView(R.id.edt_id)
    EditText edtId;
    @BindView(R.id.edt_pw)
    EditText edtPw;
    @BindView(R.id.edt_name)
    EditText edtName;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_profile);
        ButterKnife.bind(this);

        initToolbar();
        initListeners();
    }

    private void initToolbar() {
        setSupportActionBar(tbMain);
        if (getSupportActionBar() != null)
            getSupportActionBar().setDisplayShowTitleEnabled(false);
    }

    private void initListeners() {
        ivBack.setOnClickListener(v -> finish());
        ivDone.setOnClickListener(this::attemptRegister);
    }

    private void attemptRegister(View view) {
        final String id = edtId.getText().toString();
        final String pw = edtPw.getText().toString();
        final String name = edtName.getText().toString();

        if (TextUtils.isEmpty(name) && TextUtils.isEmpty(id) && TextUtils.isEmpty(pw)) {
            Snackbar.make(edtName, "형식에 맞게 입력해주세요", Snackbar.LENGTH_SHORT).show();
            return;
        }

        LoadingProgressDialog.showProgress(this);
        ApiUtil.getAccountService().signUp(id, pw, name, "000000").enqueue(new Callback<ApiResult>() {
            @Override
            public void onResponse(@NonNull Call<ApiResult> call, @NonNull Response<ApiResult> response) {
                LoadingProgressDialog.hideProgress();
                if (response.code() == 200) {
                    Toast.makeText(AddProfileActivity.this, "추가되었습니다.", Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                } else {
                    Toast.makeText(AddProfileActivity.this, getString(R.string.error_common), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResult> call, @NonNull Throwable t) {
                LoadingProgressDialog.hideProgress();
                Toast.makeText(AddProfileActivity.this, getString(R.string.error_network), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
