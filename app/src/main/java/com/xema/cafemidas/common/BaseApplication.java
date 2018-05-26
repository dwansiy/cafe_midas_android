package com.xema.cafemidas.common;

import android.app.Application;
import android.support.v7.app.AppCompatDelegate;

import com.facebook.stetho.Stetho;
import com.xema.cafemidas.BuildConfig;

public class BaseApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        //벡터 이미지 활성화
        AppCompatDelegate.setCompatVectorFromResourcesEnabled(true);

        //디버그 (chrome://inspect/#devices)
        if (BuildConfig.DEBUG)
            Stetho.initializeWithDefaults(this);
    }

}
