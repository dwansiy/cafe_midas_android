package com.xema.cafemidas.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.request.RequestOptions;
import com.google.firebase.iid.FirebaseInstanceId;
import com.xema.cafemidas.R;
import com.xema.cafemidas.common.Constants;
import com.xema.cafemidas.common.GlideApp;
import com.xema.cafemidas.common.PreferenceHelper;
import com.xema.cafemidas.model.ApiResult;
import com.xema.cafemidas.model.Profile;
import com.xema.cafemidas.network.ApiUtil;
import com.xema.cafemidas.util.LoadingProgressDialog;
import com.xema.cafemidas.widget.FontTextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import jp.wasabeef.glide.transformations.BlurTransformation;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by xema0 on 2018-05-26.
 */

public class SignInActivity extends AppCompatActivity {
    @BindView(R.id.iv_background)
    ImageView ivBackground;
    @BindView(R.id.tv_login)
    FontTextView tvLogin;
    @BindView(R.id.tv_new_account)
    FontTextView tvNewAccount;
    @BindView(R.id.edt_id)
    EditText edtId;
    @BindView(R.id.edt_password)
    EditText edtPassword;
    @BindView(R.id.btn_action)
    Button btnAction;
    @BindView(R.id.tv_help_left)
    TextView tvHelpLeft;
    @BindView(R.id.tv_help_right)
    TextView tvHelpRight;
    @BindView(R.id.ll_bottom)
    LinearLayout llBottom;
    @BindView(R.id.edt_name)
    EditText edtName;
    @BindView(R.id.ll_name)
    LinearLayout llName;

    private enum Mode {
        SIGN_IN, SIGN_UP
    }

    private Mode mode = Mode.SIGN_IN;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        ButterKnife.bind(this);

        GlideApp.with(this).load(R.drawable.img_background).override(150, 450).apply(RequestOptions.bitmapTransform(new BlurTransformation(8))).into(ivBackground);

        initListeners();

    }

    private void initListeners() {
        llBottom.setOnClickListener(this::convertPageMode);
        btnAction.setOnClickListener(this::attemptAction);
        /*
        edtId.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String id = s.toString();
                String password = edtPassword.getText().toString();
                changeSignInButton(CommonUtils.isValidId(id) && CommonUtils.isValidPassword(password));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        edtPassword.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String password = s.toString();
                String id = edtId.getText().toString();
                changeSignInButton(CommonUtils.isValidId(id) && CommonUtils.isValidPassword(password));
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
        */
    }

    /*
    private void changeSignInButton(boolean enable) {
        btnAction.setEnabled(enable);
        if (enable) {
            btnAction.setTextColor(ContextCompat.getColor(this, R.color.colorWhite));
        } else {
            btnAction.setTextColor(ContextCompat.getColor(this, R.color.colorBlue));
        }
    }
    */

    private void attemptAction(View view) {
        String id = edtId.getText().toString();
        String password = edtPassword.getText().toString();
        String name = edtName.getText().toString();

        if (isSignUpMode()) {
            attemptSignUp(id, password, name);
        } else {
            attemptSignIn(id, password);
        }
    }

    private void attemptSignIn(String id, String password) {
        LoadingProgressDialog.showProgress(this);
        ApiUtil.getAccountService().signIn(id, password).enqueue(new Callback<Profile>() {
            @Override
            public void onResponse(@NonNull Call<Profile> call, @NonNull Response<Profile> response) {
                LoadingProgressDialog.hideProgress();
                if (response.code() == 200) {
                    //PreferenceHelper.saveAutoSignInEnabled(SignInActivity.this, cbAutoSignIn.isChecked());
                    PreferenceHelper.saveId(SignInActivity.this, id);
                    PreferenceHelper.savePw(SignInActivity.this, password);

                    Profile profile = response.body();
                    if (profile == null) return;
                    PreferenceHelper.saveMyProfile(SignInActivity.this, profile);

                    if (profile.getType() == Constants.AUTH_SUPER_ADMIN || profile.getType() == Constants.AUTH_SUB_ADMIN) {
                        Intent intent = new Intent(SignInActivity.this, ReservationListActivity.class);
                        startActivity(intent);
                        finish();
                    } else if (profile.getType() == Constants.AUTH_USER) {
                        Intent intent = new Intent(SignInActivity.this, UserMainActivity.class);
                        startActivity(intent);
                        finish();
                    }
                } else {
                    Toast.makeText(SignInActivity.this, "아이디 혹은 패스워드가 다릅니다.", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<Profile> call, @NonNull Throwable t) {
                Toast.makeText(SignInActivity.this, getString(R.string.error_network), Toast.LENGTH_SHORT).show();
                LoadingProgressDialog.hideProgress();
            }
        });
    }

    private void attemptSignUp(String id, String password, String name) {
        String firebaseToken = "000000";
        if (FirebaseInstanceId.getInstance() != null) {
            String token = FirebaseInstanceId.getInstance().getToken();
            if (!TextUtils.isEmpty(token)) firebaseToken = token;
        }

        LoadingProgressDialog.showProgress(this);
        ApiUtil.getAccountService().signUp(id, password, name, firebaseToken).enqueue(new Callback<ApiResult>() {
            @Override
            public void onResponse(@NonNull Call<ApiResult> call, @NonNull Response<ApiResult> response) {
                LoadingProgressDialog.hideProgress();
                if (response.code() == 200) {
                    Toast.makeText(SignInActivity.this, "회원가입 되었습니다.", Toast.LENGTH_SHORT).show();
                    changeSignInPage();
                    edtId.setText(id);
                    edtPassword.setText(password);
                    btnAction.requestFocus();
                } else {
                    Toast.makeText(SignInActivity.this, "이미 존재하는 아이디입니다.\n다른 아이디를 입력해주세요", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(@NonNull Call<ApiResult> call, @NonNull Throwable t) {
                Toast.makeText(SignInActivity.this, getString(R.string.error_network), Toast.LENGTH_SHORT).show();
                LoadingProgressDialog.hideProgress();
            }
        });
    }

    private boolean isSignUpMode() {
        return mode == Mode.SIGN_UP;
    }

    private void convertPageMode(View view) {
        if (isSignUpMode()) {
            changeSignInPage();
        } else {
            changeSignUpPage();
        }
    }

    private void changeSignUpPage() {
        mode = Mode.SIGN_UP;

        edtName.setText("");
        edtPassword.setText("");
        edtId.setText("");
        edtId.requestFocus();

        llName.setVisibility(View.VISIBLE);
        tvLogin.setVisibility(View.GONE);
        tvNewAccount.setVisibility(View.VISIBLE);
        btnAction.setText("SIGN UP");
        tvHelpLeft.setText("이미 계정이 있으신가요?");
        tvHelpRight.setText("로그인하기.");
    }

    private void changeSignInPage() {
        mode = Mode.SIGN_IN;

        edtName.setText("");
        edtPassword.setText("");
        edtId.setText("");
        edtId.requestFocus();

        llName.setVisibility(View.GONE);
        tvNewAccount.setVisibility(View.GONE);
        tvLogin.setVisibility(View.VISIBLE);
        btnAction.setText("GO");
        tvHelpLeft.setText("계정이 없으신가요?");
        tvHelpRight.setText("가입하기.");
    }
}
