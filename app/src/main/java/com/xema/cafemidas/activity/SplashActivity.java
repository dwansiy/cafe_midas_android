package com.xema.cafemidas.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import com.xema.cafemidas.R;

/**
 * Created by xema0 on 2018-05-26.
 */

public class SplashActivity extends AppCompatActivity {
    private Handler mHandler;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        mHandler = new Handler();
        mHandler.postDelayed(mStartRunnable, 400);
    }

    private Runnable mStartRunnable = () -> {
        // TODO: 2018-05-26 자동로그인 기능 구현
        Intent intent = new Intent(SplashActivity.this, SignInActivity.class);
        startActivity(intent);
        finish();
    };

    @Override
    public void finish() {
        mHandler.removeCallbacks(mStartRunnable);
        super.finish();
    }
}