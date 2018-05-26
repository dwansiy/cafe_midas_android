package com.xema.cafemidas.network;

import com.facebook.stetho.okhttp3.StethoInterceptor;

import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;

class OkHttpGenerator {

    static OkHttpClient getInstance(boolean isDebug) {
        OkHttpClient.Builder builder = new OkHttpClient.Builder()
                //.retryOnConnectionFailure(false)
                .readTimeout(60, TimeUnit.SECONDS)
                .connectTimeout(60, TimeUnit.SECONDS);

        if (isDebug) {
            builder.addNetworkInterceptor(new StethoInterceptor());
        }

        // Connection : close 로 설정
        Interceptor interceptor = chain -> {
            Request original = chain.request();
            Request.Builder requestBuilder = original.newBuilder().addHeader("Connection", "close"); // keep-alive 해제
            Request request = requestBuilder.build();
            return chain.proceed(request);
        };
        builder.addInterceptor(interceptor);


        return builder.build();
    }

}
