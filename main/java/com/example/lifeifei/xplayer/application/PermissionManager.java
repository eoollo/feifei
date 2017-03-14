package com.example.lifeifei.xplayer.application;

import android.Manifest;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;

import com.example.lifeifei.xplayer.activities.ActivityMediaList;
import com.example.lifeifei.xplayer.application.Log;

/**
 * Created with Android Studio.
 * Description:
 * Author: lifeifei
 * Date: 2017/2/22
 * Version 1.0
 */
public class PermissionManager {
    private static final String TAG = "PermissionManager";
    public static final int MY_PERMISSIONS_REQUEST_READ = 100;
    private final ActivityMediaList mActivity;

    public PermissionManager(ActivityMediaList activity){
        mActivity = activity;
    }

    public boolean requestPermissions() {
        Log.d(TAG,"checkPermission");
        //如果没有获取权限，这弹出请求权限的dialog
        if(ContextCompat.checkSelfPermission(mActivity, Manifest.permission.READ_EXTERNAL_STORAGE) !=
                PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(mActivity,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    MY_PERMISSIONS_REQUEST_READ);
            return false;
        }
        return true;
    }
}
