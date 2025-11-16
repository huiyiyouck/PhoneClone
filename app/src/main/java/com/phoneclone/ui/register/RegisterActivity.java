package com.phoneclone.ui.register;

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
import com.phoneclone.ui.login.LoginActivity;
import com.phoneclone.viewmodel.AuthViewModel;

public class RegisterActivity extends AppCompatActivity {
    
    private TextInputEditText usernameEditText;
    private TextInputEditText emailEditText;
    private TextInputEditText passwordEditText;
    private TextInputEditText confirmPasswordEditText;
    private MaterialButton registerButton;
    private MaterialButton loginButton;
    private View progressBar;
    private View errorTextView;
    
    private AuthViewModel authViewModel;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        
        initViews();
        initViewModel();
        setupObservers();
        setupClickListeners();
    }
    
    private void initViews() {
        usernameEditText = findViewById(R.id.usernameEditText);
        emailEditText = findViewById(R.id.emailEditText);
        passwordEditText = findViewById(R.id.passwordEditText);
        confirmPasswordEditText = findViewById(R.id.confirmPasswordEditText);
        registerButton = findViewById(R.id.registerButton);
        loginButton = findViewById(R.id.loginButton);
        progressBar = findViewById(R.id.progressBar);
        errorTextView = findViewById(R.id.errorTextView);
    }
    
    private void initViewModel() {
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
    }
    
    private void setupObservers() {
        authViewModel.getAuthResponse().observe(this, authResponse -> {
            if (authResponse != null) {
                Toast.makeText(this, "注册成功", Toast.LENGTH_SHORT).show();
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
            registerButton.setEnabled(!isLoading);
        });
    }
    
    private void setupClickListeners() {
        registerButton.setOnClickListener(v -> {
            String username = usernameEditText.getText().toString().trim();
            String email = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();
            String confirmPassword = confirmPasswordEditText.getText().toString().trim();
            
            if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(this, "请填写所有字段", Toast.LENGTH_SHORT).show();
                return;
            }
            
            if (!password.equals(confirmPassword)) {
                Toast.makeText(this, "密码不匹配", Toast.LENGTH_SHORT).show();
                return;
            }
            
            if (password.length() < 8) {
                Toast.makeText(this, "密码长度至少8个字符", Toast.LENGTH_SHORT).show();
                return;
            }
            
            errorTextView.setVisibility(View.GONE);
            authViewModel.register(username, email, password);
        });
        
        loginButton.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }
    
    private void navigateToHome() {
        Intent intent = new Intent(RegisterActivity.this, HomeActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}

