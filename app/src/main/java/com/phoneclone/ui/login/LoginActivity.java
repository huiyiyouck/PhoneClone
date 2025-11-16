package com.phoneclone.ui.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;
import com.phoneclone.R;
import com.phoneclone.ui.home.HomeActivity;
import com.phoneclone.ui.register.RegisterActivity;
import com.phoneclone.viewmodel.AuthViewModel;

public class LoginActivity extends AppCompatActivity {
    
    private TextInputEditText emailEditText;
    private TextInputEditText passwordEditText;
    private MaterialButton loginButton;
    private MaterialButton registerButton;
    private View progressBar;
    private View errorTextView;
    
    private AuthViewModel authViewModel;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        
        initViews();
        initViewModel();
        setupObservers();
        setupClickListeners();
        
        // 检查是否已登录
        if (authViewModel.isLoggedIn()) {
            navigateToHome();
        }
    }
    
    private void initViews() {
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        loginButton = findViewById(R.id.loginButton);
        registerButton = findViewById(R.id.registerButton);
        progressBar = findViewById(R.id.progressBar);
        errorTextView = findViewById(R.id.errorTextView);
    }
    
    private void initViewModel() {
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
    }
    
    private void setupObservers() {
        authViewModel.getAuthResponse().observe(this, authResponse -> {
            if (authResponse != null) {
                Toast.makeText(this, "登录成功", Toast.LENGTH_SHORT).show();
                navigateToHome();
            }
        });
        
        authViewModel.getError().observe(this, error -> {
            if (error != null) {
                errorTextView.setVisibility(View.VISIBLE);
                ((android.widget.TextView) errorTextView).setText(error);
                Toast.makeText(this, error, Toast.LENGTH_LONG).show();
            }
        });
        
        authViewModel.getIsLoading().observe(this, isLoading -> {
            progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
            loginButton.setEnabled(!isLoading);
        });
    }
    
    private void setupClickListeners() {
        loginButton.setOnClickListener(v -> {
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();
            
            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "请填写邮箱和密码", Toast.LENGTH_SHORT).show();
                return;
            }
            
            errorTextView.setVisibility(View.GONE);
            authViewModel.login(email, password);
        });
        
        registerButton.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }
    
    private void navigateToHome() {
        Intent intent = new Intent(LoginActivity.this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}

