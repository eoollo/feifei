package com.example.lifeifei.xplayer.application;

/**
 * Created with Android Studio.
 * Description:
 * Author: lifeifei
 * Date: 2017/2/17
 * Version 1.0
 */
public final class Log {
    private static String LOG_TAG = "xplayer_";

    public static void v(String tag,String msg){
        android.util.Log.v(LOG_TAG + tag,msg);
    }

    public static void d(String tag,String msg){
        android.util.Log.d(LOG_TAG + tag,msg);
    }

    public static void e(String tag,String msg){
        android.util.Log.e(LOG_TAG + tag,msg);
    }

    public static void i(String tag,String msg){
        android.util.Log.i(LOG_TAG + tag,msg);
    }
}
