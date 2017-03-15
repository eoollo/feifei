package com.example.lifeifei.xplayer.activities;

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.lifeifei.xplayer.application.AppImpl;
import com.example.lifeifei.xplayer.application.Log;
import com.example.lifeifei.xplayer.R;
import com.example.lifeifei.xplayer.content.VideoData;
import com.example.lifeifei.xplayer.content.VideoInfo;

import java.io.IOException;

import tv.danmaku.ijk.media.player.IMediaPlayer;
import tv.danmaku.ijk.media.player.IjkMediaPlayer;

/**
 * Created with Android Studio.
 * Description:使用SurfaceView 和 IjkMediaPlayer播放视频
 * Author: lifeifei
 * Date: 2017/2/21
 * Version 1.0
 */
public class IjkSurfaceViewActivity extends Activity implements IjkMediaPlayer.OnPreparedListener,
        View.OnClickListener,IjkMediaPlayer.OnCompletionListener,
        SeekBar.OnSeekBarChangeListener{
    private static final String TAG = "IjkSurfaceView";
    private static final String VIDEO_PATH = "video_path";
    private static final String VIDEO_INDEX = "video_index";
    private static final int VIDEO_NO_INDEX = -1;
    private SurfaceView mIjkSurfaceView;
    private IjkMediaPlayer mIjkMediaPlayer;
    private String mCurrentVideoPath;
    private int mCurrentVideoIndex;
    private SurfaceHolder mIjkSurfaceHolder;
    private SeekBar mSeekBar;
    private ImageButton mPrevButton,mStartPauseButton,mNextButton,mLockButton,mUnlockButton,mZoomButton,mRotateButton;
    private final int PROGRESS_CHANGED = 1;
    private ProgressThread mProThread;
    private boolean isCompleted = false;
    private long mDuration,mCurrentPosition;
    private TextView mDurText,mPosText;
    private boolean isTrackTouch = false;
    private AppImpl mAppImpl;
    private View mMediaControllerBottom;
    private ActionBar mActionBar;
    private ZOOM_STATE mZoomState;
    private VideoData mVideoData;

    private enum ZOOM_STATE{
        STRETCH,
        CROP,
        ORIGINAL,
        INSIDE,
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG,"onCreate");
        mAppImpl = ((AppImpl)getApplicationContext());
        mVideoData = mAppImpl.getVideoData();

        this.setContentView(R.layout.ijksurface_video_screen);

        Intent intent = this.getIntent();
        mCurrentVideoPath = intent.getStringExtra(VIDEO_PATH);
        mCurrentVideoIndex = intent.getIntExtra(VIDEO_INDEX,VIDEO_NO_INDEX);
        initView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.menu_video_player,menu);
        return true;
    }

    private void initView() {
        initialzeActionBar();

        mIjkSurfaceView = (SurfaceView) this.findViewById(R.id.ijkSurfaceView);
        //设置surfaceHolder
        mIjkSurfaceHolder = mIjkSurfaceView.getHolder();
        //设置Holder类型，该类型表示surfaceView自己不管理缓存区，虽然提示过时，但最好还是要设置
        mIjkSurfaceHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
        //设置surface回调
        mIjkSurfaceHolder.addCallback(new SurfaceHolderBack());

        mSeekBar = (SeekBar) findViewById(R.id.seek_bar);
        mDurText = (TextView) findViewById(R.id.duration_text);
        mPosText = (TextView) findViewById(R.id.position_text);
        mPrevButton = (ImageButton) findViewById(R.id.prev_button);
        mStartPauseButton = (ImageButton) findViewById(R.id.start_pause_button);
        mNextButton = (ImageButton) findViewById(R.id.next_button);
        mLockButton = (ImageButton) findViewById(R.id.lock_button);
        mUnlockButton = (ImageButton) findViewById(R.id.unlock_button);
        mMediaControllerBottom = findViewById(R.id.media_controller_bottom);
        mZoomButton = (ImageButton) findViewById(R.id.zoom_button);
        mRotateButton = (ImageButton) findViewById(R.id.rotate_button);

        mSeekBar.setOnSeekBarChangeListener(this);
        mPrevButton.setOnClickListener(this);
        mStartPauseButton.setOnClickListener(this);
        mNextButton.setOnClickListener(this);
        mLockButton.setOnClickListener(this);
        mUnlockButton.setOnClickListener(this);
        mZoomButton.setOnClickListener(this);
        mRotateButton.setOnClickListener(this);

        mZoomState = ZOOM_STATE.STRETCH;
    }

    private void initialzeActionBar() {
        mActionBar = getActionBar();
        mActionBar.setDisplayOptions(ActionBar.DISPLAY_HOME_AS_UP,ActionBar.DISPLAY_HOME_AS_UP);
        mActionBar.setDisplayOptions(mActionBar.getDisplayOptions()
                | ActionBar.DISPLAY_SHOW_TITLE);
        setActionBarTitle();
    }

    private void setActionBarTitle() {
        VideoInfo mCurrentVideo = mVideoData.getVideo(mCurrentVideoIndex);
        String title;
        if(mCurrentVideo == null){
            title = mCurrentVideoPath;
        }else{
            title = mCurrentVideo.title;
        }
        mActionBar.setTitle(title);
    }

    private void playVideo() {
        Log.d(TAG,"playVideo...");
        //初始化MediaPlayer
        mIjkMediaPlayer = new IjkMediaPlayer();
        Log.d(TAG,"reset...");
        //重置mediaPlayer
        mIjkMediaPlayer.reset();
        Log.d(TAG,"setAudioStreamType...");
        //设置声音效果
        mIjkMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
        // 设置媒体加载完成以后回调函数。
        mIjkMediaPlayer.setOnPreparedListener(this);
        mIjkMediaPlayer.setOnCompletionListener(this);
        //设置播放源
        try {
            mIjkMediaPlayer.setDataSource(mCurrentVideoPath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.d(TAG,"prepareAsync...");
        mIjkMediaPlayer.prepareAsync();
    }

    private void stopVideo(){
        Log.d(TAG,"stopVideo...");
        if(mIjkMediaPlayer != null){
            mIjkMediaPlayer.stop();
            mIjkMediaPlayer.release();
            mIjkMediaPlayer = null;
        }
    }


    Handler mHandler = new Handler(){
        @Override
        public void handleMessage(Message msg) {
            switch (msg.what){
                case PROGRESS_CHANGED:
                    mCurrentPosition = mIjkMediaPlayer.getCurrentPosition();
                    if(mCurrentPosition <= mDuration) {
                        mSeekBar.setProgress((int) mCurrentPosition);
                        mPosText.setText(getTimeString(mCurrentPosition));
                    }else{
                        mSeekBar.setProgress((int) mDuration);
                        mPosText.setText(getTimeString(mDuration));
                    }
                    break;
            }
        }
    };

    @Override
    public void onCompletion(IMediaPlayer mp) {
        Log.d(TAG,"onCompletion...");
        isCompleted = true;
        playNextVideo();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        if(isTrackTouch){
            mIjkMediaPlayer.seekTo(progress);
        }
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {
        Log.d(TAG,"onStartTrackingTouch...");
        isTrackTouch = true;
        if(mIjkMediaPlayer != null && mIjkMediaPlayer.isPlaying()){
            mIjkMediaPlayer.pause();
        }
    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {
        Log.d(TAG,"onStopTrackingTouch...");
        isTrackTouch = false;
        if(mIjkMediaPlayer != null){
            resumeVideo();
        }
    }

    class ProgressThread extends Thread{
        @Override
        public void run() {
            while(mIjkMediaPlayer.isPlaying()){
                Message message = new Message();
                message.what = PROGRESS_CHANGED;
                mHandler.sendMessage(message);
                try{
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    @Override
    public void onPrepared(IMediaPlayer mp) {
        Log.d(TAG,"onPrepared...");
        //更新进度
        mDuration = mIjkMediaPlayer.getDuration();
        mSeekBar.setMax((int)mDuration);
        mDurText.setText(getTimeString(mDuration));
        mProThread = new ProgressThread();
        mProThread.start();
        isCompleted = false;
        //播放视频
        mIjkMediaPlayer.start();
        //设置显示到屏幕
        mIjkMediaPlayer.setDisplay(mIjkSurfaceHolder);
        //设置surfaceView保持在屏幕上
        mIjkMediaPlayer.setScreenOnWhilePlaying(true);
        mIjkSurfaceHolder.setKeepScreenOn(true);
    }

    private String getTimeString(long duration) {
        int minutes = (int) Math.floor(duration / 1000 / 60);
        int seconds = (int) ((duration / 1000) - (minutes * 60));
        return minutes + ":" + String.format("%02d",seconds);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.prev_button:
                playPreVideo();
                break;
            case R.id.start_pause_button:
                if(mIjkMediaPlayer == null || isCompleted){
                    playVideo();
                    mStartPauseButton.setImageResource(R.drawable.ic_button_pause);
                }else if(mIjkMediaPlayer.isPlaying()){
                    mIjkMediaPlayer.pause();
                    mStartPauseButton.setImageResource(R.drawable.ic_button_play);
                }else{
                    resumeVideo();
                    mStartPauseButton.setImageResource(R.drawable.ic_button_pause);
                }
                break;
            case R.id.next_button:
                playNextVideo();
                break;
            case R.id.lock_button:
                lockScreen();
                break;
            case R.id.unlock_button:
                unLockScreen();
                break;
            case R.id.zoom_button:
                setZoom();
                break;
            case R.id.rotate_button:
                rotateScreen();
                break;
            default:

        }
    }

    private void rotateScreen() {
        Log.e(TAG,"rotateScreen...");
    }

    private void setZoom() {
        Log.e(TAG,"setZoom...mZoomState = " + mZoomState);
        String text = "";
        if(mZoomState == ZOOM_STATE.STRETCH){
            mZoomButton.setImageResource(R.drawable.ic_crop_white_24dp);
            mZoomState = ZOOM_STATE.CROP;
            text = "STRETCH";
        }else if(mZoomState == ZOOM_STATE.CROP){
            mZoomButton.setImageResource(R.drawable.ic_zoom_original);
            mZoomState = ZOOM_STATE.ORIGINAL;
            text = "CROP";
        }else if(mZoomState == ZOOM_STATE.ORIGINAL) {
            mZoomButton.setImageResource(R.drawable.ic_zoom_inside);
            mZoomState = ZOOM_STATE.INSIDE;
            text = "100%";
        }else if(mZoomState == ZOOM_STATE.INSIDE) {
            mZoomButton.setImageResource(R.drawable.ic_zoom_stretch);
            mZoomState = ZOOM_STATE.STRETCH;
            text = "FIT TO SCREEN";
        }
        Toast.makeText(this,text,Toast.LENGTH_SHORT).show();
    }

    private void lockScreen() {
        Log.e(TAG,"lockScreen...");
        mUnlockButton.setVisibility(View.VISIBLE);
        mRotateButton.setVisibility(View.GONE);
        mMediaControllerBottom.setVisibility(View.GONE);
        mActionBar.hide();
    }

    private void unLockScreen(){
        Log.e(TAG,"unLockScreen...");
        mUnlockButton.setVisibility(View.GONE);
        mRotateButton.setVisibility(View.VISIBLE);
        mMediaControllerBottom.setVisibility(View.VISIBLE);
        mActionBar.show();
    }

    private void playPreVideo() {
        VideoInfo mPreVideoInfo = mVideoData.getPreVideo(mCurrentVideoIndex);
        if(mPreVideoInfo == null){
            return;
        }
        stopVideo();
        mCurrentVideoPath = mPreVideoInfo.filePath;
        mCurrentVideoIndex = mCurrentVideoIndex - 1;
        playVideo();
        setActionBarTitle();
    }

    private void playNextVideo() {
        VideoInfo mNextVideoInfo = mVideoData.getNextVideo(mCurrentVideoIndex);
        if(mNextVideoInfo == null){
            return;
        }
        stopVideo();
        mCurrentVideoPath = mNextVideoInfo.filePath;
        mCurrentVideoIndex = mCurrentVideoIndex + 1;
        playVideo();
        setActionBarTitle();
    }

    private void resumeVideo() {
        Log.d(TAG,"resumeVideo");
        mIjkMediaPlayer.start();
        mProThread = new ProgressThread();
        mProThread.start();
        isCompleted = false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        Log.e(TAG,"id = " + id);
        switch (id){
            case android.R.id.home:
                finish();
                break;
            default:
        }
        return true;
    }

    private class SurfaceHolderBack implements SurfaceHolder.Callback {
        @Override
        public void surfaceCreated(SurfaceHolder holder) {
            Log.d(TAG,"surfaceCreated...");
            //surfaceView被创建
            //设置播放资源
            if(mIjkMediaPlayer != null){
                mIjkMediaPlayer.setDisplay(mIjkSurfaceHolder);
            }else{
                playVideo();
            }
        }

        @Override
        public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            Log.d(TAG,"surfaceChanged...");
        }

        @Override
        public void surfaceDestroyed(SurfaceHolder holder) {
            Log.d(TAG,"surfaceDestroyed...");
            if(mIjkMediaPlayer != null){
                mIjkMediaPlayer.release();
                mIjkSurfaceHolder = null;
            }
        }
    }
}
