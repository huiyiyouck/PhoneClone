package com.phoneclone.ui.applist;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.phoneclone.R;
import com.phoneclone.model.AppInfo;
import java.util.ArrayList;
import java.util.List;

public class AppListAdapter extends RecyclerView.Adapter<AppListAdapter.AppViewHolder> {
    
    private List<AppInfo> apps;
    private OnAppSelectedListener listener;
    private List<String> selectedPackageNames = new ArrayList<>();
    
    public AppListAdapter(List<AppInfo> apps, OnAppSelectedListener listener) {
        this.apps = apps;
        this.listener = listener;
    }
    
    @NonNull
    @Override
    public AppViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_app, parent, false);
        return new AppViewHolder(view);
    }
    
    @Override
    public void onBindViewHolder(@NonNull AppViewHolder holder, int position) {
        AppInfo app = apps.get(position);
        holder.bind(app);
    }
    
    @Override
    public int getItemCount() {
        return apps.size();
    }
    
    class AppViewHolder extends RecyclerView.ViewHolder {
        private ImageView iconImageView;
        private TextView nameTextView;
        private TextView packageTextView;
        private CheckBox checkBox;
        
        AppViewHolder(@NonNull View itemView) {
            super(itemView);
            iconImageView = itemView.findViewById(R.id.iconImageView);
            nameTextView = itemView.findViewById(R.id.nameTextView);
            packageTextView = itemView.findViewById(R.id.packageTextView);
            checkBox = itemView.findViewById(R.id.checkBox);
            
            itemView.setOnClickListener(v -> {
                checkBox.setChecked(!checkBox.isChecked());
            });
            
            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    AppInfo app = apps.get(position);
                    if (isChecked) {
                        if (!selectedPackageNames.contains(app.getPackageName())) {
                            selectedPackageNames.add(app.getPackageName());
                        }
                    } else {
                        selectedPackageNames.remove(app.getPackageName());
                    }
                    listener.onAppSelected(app, isChecked);
                }
            });
        }
        
        void bind(AppInfo app) {
            iconImageView.setImageDrawable(app.getIcon());
            nameTextView.setText(app.getAppName());
            packageTextView.setText(app.getPackageName());
            // 移除监听器避免触发回调
            checkBox.setOnCheckedChangeListener(null);
            checkBox.setChecked(selectedPackageNames.contains(app.getPackageName()));
            // 重新设置监听器
            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && listener != null) {
                    AppInfo appInfo = apps.get(position);
                    if (isChecked) {
                        if (!selectedPackageNames.contains(appInfo.getPackageName())) {
                            selectedPackageNames.add(appInfo.getPackageName());
                        }
                    } else {
                        selectedPackageNames.remove(appInfo.getPackageName());
                    }
                    listener.onAppSelected(appInfo, isChecked);
                }
            });
        }
    }
    
    public interface OnAppSelectedListener {
        void onAppSelected(AppInfo app, boolean isSelected);
    }
}

