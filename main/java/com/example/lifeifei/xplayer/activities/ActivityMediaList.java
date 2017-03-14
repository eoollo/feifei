package com.example.lifeifei.xplayer.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.lifeifei.xplayer.application.AppImpl;
import com.example.lifeifei.xplayer.application.Log;
import com.example.lifeifei.xplayer.application.PermissionManager;
import com.example.lifeifei.xplayer.R;
import com.example.lifeifei.xplayer.content.VideoAdapter;
import com.example.lifeifei.xplayer.content.VideoData;
import com.example.lifeifei.xplayer.content.VideoInfo;

import java.util.ArrayList;

/**
 * Created with Android Studio.
 * Description:显示本地所有的视频文件
 * Author: lifeifei
 * Date: 2017/2/15
 * Version 1.0
 */
public class ActivityMediaList extends AppCompatActivity implements ListView.OnItemClickListener{

    private static final String VIDEO_PATH = "video_path";
    private static final String VIDEO_INDEX = "video_index";
    private static final int VIDEO_NO_INDEX = -1;
    private ListView mediaListView;
    private String TAG = "ActivityMediaList";
    private ArrayList<VideoInfo> videoList;
    private VideoAdapter mediaAdapter;
    private PermissionManager mPermissionManager;
    private AppImpl mAppImpl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG,"onCreate...");
        mAppImpl = (AppImpl)getApplication();
        mPermissionManager = new PermissionManager(this);
        
        setContentView(R.layout.activity_media_list);
        mediaListView = (ListView) findViewById(R.id.media_list);

        if(mPermissionManager.requestPermissions()){

            initView();
        }
    }

    public void initView(){
        Log.d(TAG,"initView...");
        VideoData mVideoData = new VideoData(this);
        videoList = mVideoData.getVideoList();
        mAppImpl.setVideoList(mVideoData);

        //设置ListView的Adapter，使用我们自定义的Adatper
        mediaAdapter = new VideoAdapter(this, videoList);
        mediaListView.setAdapter(mediaAdapter);
        mediaListView.setOnItemClickListener(this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        switch(requestCode){
            case PermissionManager.MY_PERMISSIONS_REQUEST_READ:{
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    //同意获取权限，则刷新view
                    initView();
                } else {
                    //不同意获取权限,则退出程序
                    Toast.makeText(this,R.string.denied_required_permission,Toast.LENGTH_SHORT).show();
                    finish();
                }
                return;
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG,"onResume...");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_activity_media_list, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        switch (id){
            case R.id.menu_update:
                updateData();
                break;
            case R.id.menu_search:
                break;
            case R.id.action_network_stream:
                showStreamDialog(this);
                break;
            case R.id.action_view:
                break;
            case R.id.action_select:
                break;
            case R.id.action_settings:
                break;
            case R.id.action_help:
                break;
            default:
        }

        return super.onOptionsItemSelected(item);
    }

    private void updateData() {

    }

    private void showStreamDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);

        View layout = getLayoutInflater().inflate(R.layout.stream_dialog,(ViewGroup)findViewById(R.id.dialog));
        builder.setView(layout);
        final EditText editText = (EditText)layout.findViewById(R.id.stream_path);
        ImageView image = (ImageView) layout.findViewById(R.id.stream_clear);
        image.setImageResource(android.R.drawable.ic_delete);
        image.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO
                Log.e(TAG,"clear");
                editText.setText(null);
            }
        });

        builder.setTitle(R.string.stream_dialog_title);
        builder.setMessage(R.string.stream_dialog_message);
        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String path = editText.getText().toString();
                Log.d(TAG,"ok path = " + path);
                sentToVideo(path);
            }
        });
        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //TODO
                Log.d(TAG,"cancel");
            }
        });
        builder.show();
    }



    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Log.d(TAG,"onItemClick position = " + position);
        VideoInfo info = (VideoInfo) mediaAdapter.getItem(position);
        String path = info.filePath;
        Log.d(TAG,"path = " + path);

        sentToVideo(path,position);
    }

    //转到播放video界面
    private void sentToVideo(String path) {
        sentToVideo(path,VIDEO_NO_INDEX);
    }

    //转到播放video界面
    private void sentToVideo(String path,int index) {
        Intent intent = new Intent(this,IjkSurfaceViewActivity.class);
        intent.putExtra(VIDEO_PATH,path);
        intent.putExtra(VIDEO_INDEX,index);
        this.startActivity(intent);
    }
}
