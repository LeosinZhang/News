package com.zhangxin.news.View;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.zhangxin.news.R;
import com.zhangxin.news.Util.CommonUtil;
import com.zhangxin.news.Webservice.WebAddress;

/**
 * Created by Administrator on 2016/5/17.
 */
public class WebViewActivity extends AppCompatActivity {
    private WebView mWebView;
    private Context context;
    TextView newsTitle;
    private ImageButton clickLoad;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = this;

        if (CommonUtil.isNetworkAvailable(context)) {
            setContentView(R.layout.webview);
            init();
        } else {
            setContentView(R.layout.network_error);
            clickLoad = (ImageButton) findViewById(R.id.network_error);
            clickLoad.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (CommonUtil.isNetworkAvailable(context)) {
                        setContentView(R.layout.webview);
                        init();
                    } else {
                        Log.d("OneFragement", "请检测网络后再重试");
                        Toast.makeText(context, "网络未连接，请检测网络后再重试", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }


    }

    private void init(){
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);  //AppCompatActivity

        newsTitle = (TextView) findViewById(R.id.webView_title);
        newsTitle.setText(WebAddress.getTitle);
        mWebView = (WebView) findViewById(R.id.webView);
        Runner runner = new Runner();
        Thread thread = new Thread(runner);
        thread.run();
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case 16908332:
                if (mWebView.canGoBack()) {
                    mWebView.goBack(); //goBack()表示返回WebView的上一页面
                } else {
                    this.finish();
                }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    //设置回退
    //覆盖Activity类的onKeyDown(int keyCoder,KeyEvent event)方法
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && mWebView.canGoBack()) {
            mWebView.goBack(); //goBack()表示返回WebView的上一页面
            return true;
        } else {
            this.finish();
        }
        return false;
    }

    class Runner implements Runnable { // 实现了Runnable接口，jdk就知道这个类是一个线程
        public void run() {

            WebSettings settings = mWebView.getSettings();
            //适应屏幕大小
            settings.setUseWideViewPort(true);
            settings.setLoadWithOverviewMode(true);
            mWebView.getSettings().setJavaScriptEnabled(true); //允许加载JS

            //WebView保留缩放功能但隐藏缩放控件:
            mWebView.getSettings().setSupportZoom(true);
            mWebView.getSettings().setBuiltInZoomControls(true);
            settings.setDisplayZoomControls(false);//不显示webview缩放按钮

            /*LayoutAlgorithm是一个枚举，用来控制html的布局，总共有三种类型：
            * NORMAL：正常显示，没有渲染变化。
            * SINGLE_COLUMN：把所有内容放到WebView组件等宽的一列中。
            * NARROW_COLUMNS：可能的话，使所有列的宽度不超过屏幕宽度。
            */
            mWebView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.SINGLE_COLUMN);


            //WebView加载web资源
            mWebView.loadUrl(WebAddress.getUrl);


            //覆盖WebView默认使用第三方或系统默认浏览器打开网页的行为，使网页用WebView打开
            mWebView.setWebViewClient(new WebViewClient() {
                @Override
                public boolean shouldOverrideUrlLoading(WebView view, String url) {
                    // TODO Auto-generated method stub
                    //返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
                    view.loadUrl(url);
                    return true;
                }
            });

            //通过如下方式获取webview内页面的加载进度：
            mWebView.setWebChromeClient(new WebChromeClient() {
                @Override
                public void onProgressChanged(WebView view, int newProgress) {
                    //get the newProgress and refresh progress bar
                }
            });

            //webview正在加载的页面的title：
            mWebView.setWebChromeClient(new WebChromeClient() {
                @Override
                public void onReceivedTitle(WebView view, String title) {
                    newsTitle.setText(title);
                }
            });

            mWebView.setWebViewClient(new WebViewClient() {

                @Override
                public void onReceivedError(WebView view, int errorCode, String description, String failingUrl) {
                    switch (errorCode) {
                        case 404:
                            view.loadUrl("file:///android_assets/error_handle.html");
                            break;
                    }
                }
            });

        }
    }



}
