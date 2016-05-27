package com.zhangxin.news;

import android.app.Application;

/**
 * Created by Administrator on 2016/5/18.
 */
public class MyApplication extends Application {
    private static MyApplication instance;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
    }

    public static MyApplication getInstance(){
        // 这里不用判断instance是否为空
        return instance;
    }
}
