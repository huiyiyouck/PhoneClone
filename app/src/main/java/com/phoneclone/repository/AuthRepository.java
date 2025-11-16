package com.phoneclone.repository;

import android.content.Context;
import com.phoneclone.model.AuthResponse;
import com.phoneclone.network.RetrofitClient;
import com.phoneclone.network.api.AuthApi;
import com.phoneclone.util.SharedPreferencesManager;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AuthRepository {
    
    private AuthApi authApi;
    private SharedPreferencesManager prefsManager;
    
    public AuthRepository(Context context) {
        RetrofitClient retrofitClient = RetrofitClient.getInstance(context);
        authApi = retrofitClient.getAuthApi();
        prefsManager = new SharedPreferencesManager(context);
    }
    
    public void register(String username, String email, String password, AuthCallback callback) {
        AuthApi.RegisterRequest request = new AuthApi.RegisterRequest(username, email, password);
        Call<AuthResponse> call = authApi.register(request);
        
        call.enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    AuthResponse authResponse = response.body();
                    prefsManager.saveToken(authResponse.getToken());
                    prefsManager.saveUserInfo(
                            authResponse.getUsername(),
                            authResponse.getEmail(),
                            authResponse.getMembershipLevel()
                    );
                    callback.onSuccess(authResponse);
                } else {
                    callback.onError("注册失败: " + response.message());
                }
            }
            
            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                callback.onError("网络错误: " + t.getMessage());
            }
        });
    }
    
    public void login(String email, String password, AuthCallback callback) {
        AuthApi.LoginRequest request = new AuthApi.LoginRequest(email, password);
        Call<AuthResponse> call = authApi.login(request);
        
        call.enqueue(new Callback<AuthResponse>() {
            @Override
            public void onResponse(Call<AuthResponse> call, Response<AuthResponse> response) {
                if (response.isSuccessful() && response.body() != null) {
                    AuthResponse authResponse = response.body();
                    prefsManager.saveToken(authResponse.getToken());
                    prefsManager.saveUserInfo(
                            authResponse.getUsername(),
                            authResponse.getEmail(),
                            authResponse.getMembershipLevel()
                    );
                    callback.onSuccess(authResponse);
                } else {
                    callback.onError("登录失败: " + response.message());
                }
            }
            
            @Override
            public void onFailure(Call<AuthResponse> call, Throwable t) {
                callback.onError("网络错误: " + t.getMessage());
            }
        });
    }
    
    public void logout() {
        prefsManager.clear();
    }
    
    public boolean isLoggedIn() {
        return prefsManager.isLoggedIn();
    }
    
    public interface AuthCallback {
        void onSuccess(AuthResponse response);
        void onError(String error);
    }
}

