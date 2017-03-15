package com.example.lifeifei.xplayer.application;

import android.app.Application;

import com.example.lifeifei.xplayer.content.VideoData;

/**
 * Created with Android Studio.
 * Description:
 * Author: lifeifei
 * Date: 2017/3/9
 * Version 1.0
 */
public class AppImpl extends Application{
    private String TAG = "AppImpl";
    private VideoData mVideoData;

    public AppImpl() {
        Log.d(TAG,"AppImpl...");
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG,"onCreate...");
    }

    public void setVideoData(VideoData videoData){
        mVideoData = videoData;
    }

    public VideoData getVideoData(){
        return mVideoData;
    }
}
