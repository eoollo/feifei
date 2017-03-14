package com.example.lifeifei.xplayer.content;

import android.app.Activity;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;

import com.example.lifeifei.xplayer.activities.ActivityMediaList;
import com.example.lifeifei.xplayer.application.Log;

import java.util.ArrayList;

/**
 * Created with Android Studio.
 * Description:
 * Author: lifeifei
 * Date: 2017/3/7
 * Version 1.0
 */
public class VideoData {
    private Cursor cursor;
    private ArrayList<VideoInfo> videoList;
    private Activity context;
    private String TAG = "VideoData";
    private static final int VIDEO_NO_INDEX = -1;

    public VideoData(ActivityMediaList activityMediaList) {
        super();
        context = activityMediaList;
    }

    public ArrayList<VideoInfo> getVideoList(){
        String[] thumbColumns = new String[]{
                MediaStore.Video.Thumbnails.DATA,
                MediaStore.Video.Thumbnails.VIDEO_ID
        };

        String[] mediaColumns = new String[]{
                MediaStore.Video.Media.DATA,
                MediaStore.Video.Media._ID,
                MediaStore.Video.Media.TITLE,
                MediaStore.Video.Media.MIME_TYPE
        };

        //首先检索SDcard上所有的video
        cursor = context.managedQuery(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, mediaColumns, null, null, null);

        videoList = new ArrayList<VideoInfo>();

        if(cursor.moveToFirst()){
            do{
                VideoInfo info = new VideoInfo();

                info.filePath = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DATA));
                info.mimeType = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.MIME_TYPE));
                info.title = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.TITLE));
                Bitmap bitmap = ThumbnailUtils.createVideoThumbnail(info.filePath,MediaStore.Images.Thumbnails.MICRO_KIND);
                info.thumb = ThumbnailUtils.extractThumbnail(bitmap,200,150,ThumbnailUtils.OPTIONS_RECYCLE_INPUT);

                //然后将其加入到videoList
                videoList.add(info);

            }while(cursor.moveToNext());
        }
        return videoList;
    }

    public VideoInfo getNextVideo(int index){
        Log.d(TAG,"getNextVideo index = " + index);
        if(index == VIDEO_NO_INDEX || index == videoList.size() - 1){
            return null;
        }
        return videoList.get(index + 1);
    }

    public VideoInfo getPreVideo(int index){
        if(index == VIDEO_NO_INDEX || index == 0){
            return null;
        }
        return videoList.get(index - 1);
    }
}
