package com.phoneclone.ui.applist;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ProgressBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.phoneclone.R;
import com.phoneclone.model.AppInfo;
import com.phoneclone.util.AppUtils;
import java.util.ArrayList;
import java.util.List;

public class AppListActivity extends AppCompatActivity {
    
    private RecyclerView recyclerView;
    private AppListAdapter adapter;
    private ProgressBar progressBar;
    private FloatingActionButton fab;
    private List<AppInfo> allApps = new ArrayList<>();
    private List<AppInfo> selectedApps = new ArrayList<>();
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_app_list);
        
        initViews();
        loadApps();
    }
    
    private void initViews() {
        recyclerView = findViewById(R.id.recyclerView);
        progressBar = findViewById(R.id.progressBar);
        fab = findViewById(R.id.fab);
        
        adapter = new AppListAdapter(allApps, new AppListAdapter.OnAppSelectedListener() {
            @Override
            public void onAppSelected(AppInfo app, boolean isSelected) {
                if (isSelected) {
                    selectedApps.add(app);
                } else {
                    selectedApps.remove(app);
                }
                updateFabVisibility();
            }
        });
        
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
        
        fab.setOnClickListener(v -> {
            // 保存选中的应用
            saveSelectedApps();
            finish();
        });
        
        updateFabVisibility();
    }
    
    private void loadApps() {
        progressBar.setVisibility(View.VISIBLE);
        
        new Thread(() -> {
            List<AppInfo> apps = AppUtils.getInstalledApps(this);
            
            runOnUiThread(() -> {
                allApps.clear();
                allApps.addAll(apps);
                adapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);
            });
        }).start();
    }
    
    private void updateFabVisibility() {
        fab.setVisibility(selectedApps.isEmpty() ? View.GONE : View.VISIBLE);
    }
    
    private void saveSelectedApps() {
        Intent resultIntent = new Intent();
        ArrayList<String> packageNames = new ArrayList<>();
        for (AppInfo app : selectedApps) {
            packageNames.add(app.getPackageName());
        }
        resultIntent.putStringArrayListExtra("selected_package_names", packageNames);
        setResult(RESULT_OK, resultIntent);
    }
}

