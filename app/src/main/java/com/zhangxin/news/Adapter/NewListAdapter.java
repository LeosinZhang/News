package com.zhangxin.news.Adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.NetworkImageView;
import com.zhangxin.news.MainActivity;
import com.zhangxin.news.Model.RequestManager;
import com.zhangxin.news.R;
import com.zhangxin.news.Webservice.WebAddress;

import java.util.HashMap;
import java.util.List;


public class NewListAdapter extends BaseAdapter {
    private static Bitmap bitmap;
    private MainActivity mMainActivity;
    private Handler mHandler;
    private RequestManager requestManager;

    static class ViewHolder {
        NetworkImageView ivPreview, pic_one, pic_two, pic_three;
        TextView tvTitle;
        TextView tvContent;
        TextView tvReview;
    }

    private Context context;
    private List<HashMap<String, String>> news;

    public NewListAdapter(Context context, List<HashMap<String, String>> news) {
        this.context = context;
        this.news = news;
        mMainActivity = MainActivity.getInstance();
        mHandler = mMainActivity.getUiHandler();
        requestManager = RequestManager.getInstance();
        requestManager.init(this.context);
    }


    @Override
    public int getCount() {
        return news.size();
    }

    @Override
    public HashMap<String, String> getItem(int position) {
        return news.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if ("".equals(getItem(position).get("digest"))) {//多图模式
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.picnews_item, null);
            holder.pic_one = (NetworkImageView) convertView.findViewById(R.id.pic_one);
            holder.pic_two = (NetworkImageView) convertView.findViewById(R.id.pic_two);
            holder.pic_three = (NetworkImageView) convertView.findViewById(R.id.pic_three);
            holder.tvTitle = (TextView) convertView.findViewById(R.id.picnews_title);
            holder.tvReview = (TextView) convertView.findViewById(R.id.picnews_Review);
        } else {//单图模式
            holder = new ViewHolder();
            convertView = LayoutInflater.from(context).inflate(R.layout.wordnews_item, null);
            holder.ivPreview = (NetworkImageView) convertView.findViewById(R.id.ivPreview);
            holder.tvTitle = (TextView) convertView.findViewById(R.id.tvTitle);
            holder.tvContent = (TextView) convertView.findViewById(R.id.tvContent);
            holder.tvReview = (TextView) convertView.findViewById(R.id.tvReview);
        }
        convertView.setTag(holder);

        if ("".equals(getItem(position).get("digest"))) {
            String img1 = getItem(position).get("imgsrc");
            String img2 = getItem(position).get("imgsrc0");
            String img3 = getItem(position).get("imgsrc1");
            String url = getItem(position).get("newsURL");
            requestManager.loadImgUrl(holder.pic_one, img1);
            requestManager.loadImgUrl(holder.pic_two, img2);
            requestManager.loadImgUrl(holder.pic_three, img3);
            holder.tvTitle.setText(getItem(position).get("title"));
            holder.tvReview.setText(getItem(position).get("replyCount") + "跟帖");

            //给标题绑定事件
            holder.pic_one.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    sendMsg(position);
                }
            });

            //给标题绑定事件
            holder.pic_two.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    sendMsg(position);
                }
            });

            //给标题绑定事件
            holder.pic_three.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    sendMsg(position);
                }
            });

        } else {
            String img = getItem(position).get("imgsrc");
            String url = getItem(position).get("newsURL");
            requestManager.loadImgUrl(holder.ivPreview,img);
            holder.tvTitle.setText(getItem(position).get("title"));
            holder.tvContent.setText(getItem(position).get("digest"));
            holder.tvReview.setText(getItem(position).get("replyCount") + "跟帖");

            //给标题绑定事件
            holder.ivPreview.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    sendMsg(position);
                    ;
                }
            });

            //给标题绑定事件
            holder.tvContent.setOnClickListener(new View.OnClickListener() {
                public void onClick(View v) {
                    sendMsg(position);
                }
            });
        }

        //给标题绑定事件
        holder.tvTitle.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sendMsg(position);
            }
        });

        //给标题绑定事件
        holder.tvReview.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                sendMsg(position);
            }
        });

        return convertView;
    }

    public void addNews(List<HashMap<String, String>> addNews) {
        for (HashMap<String, String> hm : addNews) {
            news.add(hm);
        }
    }

    private void sendMsg(int position) {
        WebAddress.getUrl = getItem(position).get("newsURL");
        WebAddress.getTitle = getItem(position).get("title");
        Message msg = new Message();
        msg.what = 0;
        mHandler.sendMessage(msg);
    }

}
