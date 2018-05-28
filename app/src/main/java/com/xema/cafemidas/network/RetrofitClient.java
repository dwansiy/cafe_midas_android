package com.xema.cafemidas.network;

import com.xema.cafemidas.BuildConfig;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class RetrofitClient {
    //로컬서버
    public static final String BASE_URL = "http://172.17.22.237:8000/";
    //외부서버
    //private static final String BASE_URL = "http://sgc109.pythonanywhere.com/";
    private static Retrofit retrofit = null;

    static Retrofit getClient() {
        if (retrofit == null) {
            Retrofit.Builder builder = new Retrofit.Builder().baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create());
            //to get string response
            //.addConverterFactory(ScalarsConverterFactory.create());
            builder.client(OkHttpGenerator.getInstance(BuildConfig.DEBUG));
            retrofit = builder.build();
        }
        return retrofit;
    }

}
