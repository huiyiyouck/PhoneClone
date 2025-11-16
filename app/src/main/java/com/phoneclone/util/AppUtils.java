package com.phoneclone.util;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import com.phoneclone.model.AppInfo;
import java.util.ArrayList;
import java.util.List;

public class AppUtils {
    
    public static List<AppInfo> getInstalledApps(Context context) {
        List<AppInfo> appList = new ArrayList<>();
        PackageManager packageManager = context.getPackageManager();
        
        List<ApplicationInfo> installedApps = packageManager.getInstalledApplications(PackageManager.GET_META_DATA);
        
        for (ApplicationInfo appInfo : installedApps) {
            // 过滤系统应用（可选）
            if ((appInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                String appName = packageManager.getApplicationLabel(appInfo).toString();
                String packageName = appInfo.packageName;
                
                // 跳过自己
                if (packageName.equals(context.getPackageName())) {
                    continue;
                }
                
                AppInfo app = new AppInfo();
                app.setPackageName(packageName);
                app.setAppName(appName);
                app.setIcon(packageManager.getApplicationIcon(appInfo));
                app.setCategory("其他"); // 默认分类
                
                appList.add(app);
            }
        }
        
        return appList;
    }
    
    public static String getAppCategory(String packageName) {
        // 简单的分类逻辑，可以根据包名判断
        if (packageName.contains("wechat") || packageName.contains("tencent")) {
            return "社交";
        } else if (packageName.contains("game") || packageName.contains("play")) {
            return "游戏";
        } else if (packageName.contains("music") || packageName.contains("audio")) {
            return "音乐";
        } else if (packageName.contains("video") || packageName.contains("movie")) {
            return "视频";
        } else {
            return "其他";
        }
    }
}

