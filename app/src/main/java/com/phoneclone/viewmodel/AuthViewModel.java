package com.phoneclone.viewmodel;

import android.app.Application;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.phoneclone.model.AuthResponse;
import com.phoneclone.repository.AuthRepository;

public class AuthViewModel extends AndroidViewModel {
    
    private AuthRepository authRepository;
    private MutableLiveData<AuthResponse> authResponseLiveData = new MutableLiveData<>();
    private MutableLiveData<String> errorLiveData = new MutableLiveData<>();
    private MutableLiveData<Boolean> isLoadingLiveData = new MutableLiveData<>();
    
    public AuthViewModel(Application application) {
        super(application);
        authRepository = new AuthRepository(application);
    }
    
    public void register(String username, String email, String password) {
        isLoadingLiveData.setValue(true);
        authRepository.register(username, email, password, new AuthRepository.AuthCallback() {
            @Override
            public void onSuccess(AuthResponse response) {
                isLoadingLiveData.setValue(false);
                authResponseLiveData.setValue(response);
            }
            
            @Override
            public void onError(String error) {
                isLoadingLiveData.setValue(false);
                errorLiveData.setValue(error);
            }
        });
    }
    
    public void login(String email, String password) {
        isLoadingLiveData.setValue(true);
        authRepository.login(email, password, new AuthRepository.AuthCallback() {
            @Override
            public void onSuccess(AuthResponse response) {
                isLoadingLiveData.setValue(false);
                authResponseLiveData.setValue(response);
            }
            
            @Override
            public void onError(String error) {
                isLoadingLiveData.setValue(false);
                errorLiveData.setValue(error);
            }
        });
    }
    
    public void logout() {
        authRepository.logout();
    }
    
    public boolean isLoggedIn() {
        return authRepository.isLoggedIn();
    }
    
    public LiveData<AuthResponse> getAuthResponse() {
        return authResponseLiveData;
    }
    
    public LiveData<String> getError() {
        return errorLiveData;
    }
    
    public LiveData<Boolean> getIsLoading() {
        return isLoadingLiveData;
    }
}

