package com.zhangxin.news.Model;

import android.content.Context;
import android.graphics.Bitmap;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.android.volley.toolbox.Volley;
import com.zhangxin.news.R;
import com.zhangxin.news.Webservice.BitmapCache;

/**
 * Created by Administrator on 2016/5/25.
 */
public class RequestManager {
    private static RequestManager requestManager = new RequestManager();
    public static RequestQueue mQueue;
    private static ImageLoader imageLoader;
    private String retStr = null;

    public RequestManager() {
    }

    public static RequestManager getInstance() {
        return requestManager;
    }

    public static void init(Context context) {
        mQueue = Volley.newRequestQueue(context);
        imageLoader = new ImageLoader(mQueue, new BitmapCache() {
            @Override
            public void putBitmap(String url, Bitmap bitmap) {
            }

            @Override
            public Bitmap getBitmap(String url) {
                return null;
            }
        });
    }

    public void loadImgUrl(NetworkImageView networkImageView, String url) {
        networkImageView.setDefaultImageResId(R.mipmap.github_loading_outer);
        networkImageView.setErrorImageResId(R.mipmap.github_loading_inner); //failed
        networkImageView.setImageUrl(url, imageLoader);
    }




}
