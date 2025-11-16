package com.phoneclone.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import androidx.appcompat.app.AppCompatActivity;
import com.phoneclone.R;
import com.phoneclone.ui.applist.AppListActivity;

public class HomeActivity extends AppCompatActivity {
    
    private Button addAppButton;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        
        initViews();
        setupClickListeners();
    }
    
    private void initViews() {
        addAppButton = findViewById(R.id.addAppButton);
    }
    
    private void setupClickListeners() {
        addAppButton.setOnClickListener(v -> {
            Intent intent = new Intent(HomeActivity.this, AppListActivity.class);
            startActivity(intent);
        });
    }
}

