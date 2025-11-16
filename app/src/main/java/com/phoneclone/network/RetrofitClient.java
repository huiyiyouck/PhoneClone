package com.phoneclone.network;

import android.content.Context;
import android.content.SharedPreferences;
import com.phoneclone.network.api.AuthApi;
import com.phoneclone.network.interceptor.AuthInterceptor;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import java.util.concurrent.TimeUnit;

public class RetrofitClient {
    
    // 阿里云服务器地址 - 请替换为你的实际服务器IP或域名
    // 格式：http://你的公网IP:8080/api/ 或 https://your-domain.com/api/
    private static final String BASE_URL = "http://47.94.122.137:8080/api/";
    private static RetrofitClient instance;
    private Retrofit retrofit;
    
    private RetrofitClient(Context context) {
        SharedPreferences sharedPreferences = context.getSharedPreferences("app_prefs", Context.MODE_PRIVATE);
        
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .addInterceptor(new AuthInterceptor(sharedPreferences))
                .addInterceptor(loggingInterceptor)
                .connectTimeout(30, TimeUnit.SECONDS)
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .build();
        
        retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .client(okHttpClient)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }
    
    public static synchronized RetrofitClient getInstance(Context context) {
        if (instance == null) {
            instance = new RetrofitClient(context);
        }
        return instance;
    }
    
    public AuthApi getAuthApi() {
        return retrofit.create(AuthApi.class);
    }
}

