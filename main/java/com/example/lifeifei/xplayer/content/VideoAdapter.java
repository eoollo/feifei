package com.example.lifeifei.xplayer.content;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.lifeifei.xplayer.R;
import com.example.lifeifei.xplayer.application.Log;

import java.util.List;

/**
 * Created with Android Studio.
 * Description:定义一个Adapter来显示缩略图和视频title信息
 * Author: lifeifei
 * Date: 2017/2/17
 * Version 1.0
 */
public class VideoAdapter extends BaseAdapter {

    private Context context;
    private List<VideoInfo> videoItems;
    private String TAG = "VideoAdapter";

    public VideoAdapter(Context context, List<VideoInfo> data){
        this.context = context;
        this.videoItems = data;
    }
    @Override
    public int getCount() {
        return videoItems.size();
    }
    @Override
    public Object getItem(int p) {
        return videoItems.get(p);
    }
    @Override
    public long getItemId(int p) {
        return p;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if(convertView == null){
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.media_item, null);
            holder.thumbImage = (ImageView)convertView.findViewById(R.id.media_thumbnail);
            holder.titleText = (TextView)convertView.findViewById(R.id.media_title);
            convertView.setTag(holder);
        }else{
            holder = (ViewHolder)convertView.getTag();
        }

        holder.thumbImage.setImageBitmap(videoItems.get(position).thumb);
        holder.titleText.setText(videoItems.get(position).title);

        return convertView;
    }

    class ViewHolder{
        ImageView thumbImage;
        TextView titleText;
    }
}
