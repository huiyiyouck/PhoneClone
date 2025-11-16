package com.phoneclone.network.interceptor;

import android.content.SharedPreferences;
import okhttp3.Interceptor;
import okhttp3.Request;
import okhttp3.Response;
import java.io.IOException;

public class AuthInterceptor implements Interceptor {
    
    private SharedPreferences sharedPreferences;
    private static final String TOKEN_KEY = "auth_token";
    
    public AuthInterceptor(SharedPreferences sharedPreferences) {
        this.sharedPreferences = sharedPreferences;
    }
    
    @Override
    public Response intercept(Chain chain) throws IOException {
        Request original = chain.request();
        
        String token = sharedPreferences.getString(TOKEN_KEY, null);
        
        if (token != null) {
            Request.Builder requestBuilder = original.newBuilder()
                    .header("Authorization", "Bearer " + token);
            Request request = requestBuilder.build();
            return chain.proceed(request);
        }
        
        return chain.proceed(original);
    }
}

