package com.zhangxin.news.View;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.format.DateUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.StringRequest;
import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshListView;
import com.zhangxin.news.Adapter.NewListAdapter;
import com.zhangxin.news.MainActivity;
import com.zhangxin.news.Model.RequestManager;
import com.zhangxin.news.R;
import com.zhangxin.news.Util.CommonUtil;
import com.zhangxin.news.ViewPager.AdvViewPager;
import com.zhangxin.news.Webservice.AnalyticJson;
import com.zhangxin.news.Webservice.BitmapCache;
import com.zhangxin.news.Webservice.WebAddress;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by Administrator on 2016/5/12.
 */
public class OneFragment extends Fragment {
    private Context context;
    public static final int HTTP_REQUEST_SUCCESS = -1;
    public static final int HTTP_REQUEST_ERROR = 0;
    private MainActivity mMainActivity;
    private RequestManager requestManager;
    private Handler mHandler;
    private View mView;

    private PullToRefreshListView HeadLineNews = null;
    private ImageLoader imageLoader = null;
    private List<View> views = null;
    private ViewPager vpViewPager = null;
    private NewListAdapter newAdapter = null;
    private List<HashMap<String, String>> list_item = new ArrayList<HashMap<String, String>>();
    private List<Map<String, Object>> ad_list = null;
    private List<View> advs = null;

    private static String receivedStr = null, cacheStr = null;
    private String paperTitle[] = new String[5];
    private AdvViewPager vpAdv = null;
    private ViewGroup vg = null;
    private TextView title;
    private ImageView[] imageViews = null;
    private int currentPage = 0;
    private boolean isNeedAdViewPager = true;
    private ImageButton clickLoad;
    private boolean isFirstLoad = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.e("OneFragement", "onCreateView *********************");
        mMainActivity = MainActivity.getInstance();
        mHandler = mMainActivity.getUiHandler();
        context = getContext();
        requestManager = RequestManager.getInstance();
        requestManager.init(context);

        //直接获取缓存 再判断缓存是否为空
        cacheStr = new String(requestManager.mQueue.getCache().get(WebAddress.URL).data);
        if (CommonUtil.isNetworkAvailable(context) || cacheStr != null) {
            init(inflater, container);
        } else {
            mView = inflater.inflate(R.layout.network_error, container, false);
            clickLoad = (ImageButton) mView.findViewById(R.id.network_error);
            reload(inflater, container);
        }
        return mView;
    }


    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        Log.e("OneFragement", "onViewCreated #############");
        super.onViewCreated(view, savedInstanceState);
    }


    private void init(LayoutInflater inflater, ViewGroup container) {
        Log.e("OneFragement", "init #############");
        mView = inflater.inflate(R.layout.fragment_one, container, false);
        ad_list = new ArrayList<Map<String, Object>>();
        HeadLineNews = (PullToRefreshListView) mView.findViewById(R.id.ptrlvHeadLineNews);

        if (CommonUtil.isNetworkAvailable(context)) {
            //请求http，获取json
            StringRequest stringRequest = new StringRequest(WebAddress.URL, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    Log.d("TAG", response);
                    receivedStr = response;
                    //解析json 并将结果储存到list_item
                    AnalyticJson.parseJsonMultiImg_Order(receivedStr, list_item, ad_list, WebAddress.JSONArray, isNeedAdViewPager);
                    newAdapter = new NewListAdapter(context, list_item);
                    initPullToRefreshListView(HeadLineNews, newAdapter);

                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.e("TAG", error.getMessage(), error);
                }
            });
            requestManager.mQueue.add(stringRequest);
        } else {
            if (isFirstLoad) {
                AnalyticJson.parseJsonMultiImg_Order(cacheStr, list_item, ad_list, WebAddress.JSONArray, isNeedAdViewPager);
                newAdapter = new NewListAdapter(context, list_item);
                isFirstLoad = !isFirstLoad;
            }
            initPullToRefreshListView(HeadLineNews, newAdapter);
        }

    }


    private void reload(final LayoutInflater inflater, final ViewGroup container) {
        clickLoad.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (CommonUtil.isNetworkAvailable(context)) {
                    Message msg = new Message();
                    msg.what = 1;
                    mHandler.sendMessage(msg);
                } else {
                    Log.d("OneFragement", "请检测网络后再重试");
                    Toast.makeText(context, "网络未连接，请检测网络后再重试", Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    /**
     * 初始化PullToRefreshListView<br>
     * 初始化在PullToRefreshListView中的ViewPager广告栏
     *
     * @param rtflv
     * @param adapter
     */
    public void initPullToRefreshListView(PullToRefreshListView rtflv, NewListAdapter adapter) {
        rtflv.setMode(PullToRefreshBase.Mode.BOTH);
        rtflv.setOnRefreshListener(new MyOnRefreshListener2(rtflv));
        rtflv.setAdapter(adapter);

        if (rtflv.getId() == R.id.ptrlvHeadLineNews) {
            advs = new ArrayList<View>();
            ImageView iv;
            String imgsrc = "", title = "";
            int num = 0;
            for (Map<String, Object> m : ad_list) {
                iv = new ImageView(context);
                int i = 0;
                for (String k : m.keySet()) {
                    if (i == 0) {
                        imgsrc = k;
                    } else {
                        title = k;
                    }
                    i++;
                }
                imageLoader = new ImageLoader(RequestManager.mQueue, new BitmapCache() {
                    @Override
                    public void putBitmap(String url, Bitmap bitmap) {
                    }

                    @Override
                    public Bitmap getBitmap(String url) {
                        return null;
                    }
                });
                ImageLoader.ImageListener listener = ImageLoader.getImageListener(iv, R.mipmap.github_loading_outer, R.mipmap.github_loading_inner);
                imageLoader.get(m.get(imgsrc).toString(), listener, 2000, 2000);
                paperTitle[num] = m.get(title).toString();

                advs.add(iv);
                num++;
            }
            setViewPager(rtflv);
        }
    }


    private void setViewPager(PullToRefreshListView rtflv) {
        RelativeLayout rlAdv = (RelativeLayout) LayoutInflater.from(context).inflate(R.layout.sliding_advertisement, null);
        vpAdv = (AdvViewPager) rlAdv.findViewById(R.id.vpAdv);
        vg = (ViewGroup) rlAdv.findViewById(R.id.viewGroup);
        title = (TextView) rlAdv.findViewById(R.id.pager_title);

        vpAdv.setAdapter(new AdvAdapter());
        vpAdv.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageSelected(int arg0) {
                currentPage = arg0;
                for (int i = 0; i < advs.size(); i++) {
                    if (i == arg0) {
                        title.setText(paperTitle[i]);
                        imageViews[i].setBackgroundResource(R.mipmap.icon_point_pre);
                    } else {
                        imageViews[i].setBackgroundResource(R.mipmap.icon_point);
                    }
                }
            }


            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {

            }

            @Override
            public void onPageScrollStateChanged(int arg0) {

            }
        });

        imageViews = new ImageView[advs.size()];
        ImageView imageView;
        for (int i = 0; i < advs.size(); i++) {
            imageView = new ImageView(context);
            imageView.setLayoutParams(new ViewGroup.LayoutParams(20, 20));
            imageViews[i] = imageView;
            if (i == 0) {
                title.setText(paperTitle[i]);
                imageViews[i].setBackgroundResource(R.mipmap.icon_point_pre);
            } else {
                imageViews[i].setBackgroundResource(R.mipmap.icon_point);
            }
            vg.addView(imageViews[i]);
        }

        rtflv.getRefreshableView().addHeaderView(rlAdv, null, false);


        final Handler handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                vpAdv.setCurrentItem(msg.what);
                super.handleMessage(msg);
            }
        };

        new Thread(new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(5000);
                        currentPage++;
                        if (currentPage > advs.size() - 1) {
                            currentPage = 0;
                        }
                        handler.sendEmptyMessage(currentPage);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }).start();
    }


    class MyOnRefreshListener2 implements PullToRefreshBase.OnRefreshListener2<ListView> {

        private PullToRefreshListView mPtflv;

        public MyOnRefreshListener2(PullToRefreshListView ptflv) {
            this.mPtflv = ptflv;
        }

        @Override
        public void onPullDownToRefresh(PullToRefreshBase<ListView> refreshView) {
            isNeedAdViewPager = true;
            // 下拉刷新
            String label = DateUtils.formatDateTime(getContext(),//getApplicationContext(),
                    System.currentTimeMillis(), DateUtils.FORMAT_SHOW_TIME
                            | DateUtils.FORMAT_SHOW_DATE
                            | DateUtils.FORMAT_ABBREV_ALL);
            refreshView.getLoadingLayoutProxy().setLastUpdatedLabel(label);
            new GetNewsTask(mPtflv).execute();
        }

        @Override
        public void onPullUpToRefresh(PullToRefreshBase<ListView> refreshView) {
            isNeedAdViewPager = false;
            // 上拉加载
            new GetNewsTask(mPtflv).execute();
        }

    }

    /**
     * 获取N条模拟的新闻数据<br>
     * 打包成ArrayList返回
     *
     * @return
     */
    public ArrayList<HashMap<String, String>> getSimulationNews(int n) {
        ArrayList<HashMap<String, String>> ret = new ArrayList<HashMap<String, String>>();
        HashMap<String, String> hm;
        for (int i = 0; i < n; i++) {
            hm = new HashMap<String, String>();
            hm.put("uri", "http://images.china.cn/attachement/jpg/site1000/20131029/001fd04cfc4813d9af0118.jpg");
            hm.put("title", "国内成品油价两连跌几成定局");
            hm.put("content", "国内成品油今日迎调价窗口，机构预计每升降价0.1元。");
            hm.put("review", i + "跟帖");
            ret.add(hm);
        }
        return ret;
    }

    /**
     * 请求网络获得新闻信息
     *
     * @author Louis
     */
    class GetNewsTask extends AsyncTask<String, Void, Integer> {
        private PullToRefreshListView mPtrlv;

        public GetNewsTask(PullToRefreshListView ptrlv) {
            this.mPtrlv = ptrlv;
        }

        @Override
        protected Integer doInBackground(String... params) {
            if (CommonUtil.isNetworkAvailable(context)) {
                return HTTP_REQUEST_SUCCESS;
            }
            return HTTP_REQUEST_ERROR;
        }

        @Override
        protected void onPostExecute(Integer result) {
            super.onPostExecute(result);
            switch (result) {
                case HTTP_REQUEST_SUCCESS:
                    newAdapter.addNews(getSimulationNews(10));
                    newAdapter.notifyDataSetChanged();
                    break;
                case HTTP_REQUEST_ERROR:
                    Toast.makeText(context, "请检查网络", Toast.LENGTH_SHORT).show();
                    break;
            }
            mPtrlv.onRefreshComplete();
        }

    }

    private void sendMsg(int position) {
        Message msg = new Message();
        msg.what = 0;
        msg.obj = position;
        mHandler.sendMessage(msg);
    }

    class AdvAdapter extends PagerAdapter {
        @Override
        public int getCount() {
            return advs.size();
        }

        @Override
        public boolean isViewFromObject(View arg0, Object arg1) {
            return arg0 == arg1;
        }

        @Override
        public void destroyItem(View container, int position, Object object) {
            ((ViewPager) container).removeView(advs.get(position));
        }

        @Override
        public Object instantiateItem(View container, int position) {
            ((ViewPager) container).addView(advs.get(position));
            switch (position) {
                case 0:
                    advs.get(0).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            sendMsg(0);
                            Log.e("log", "你当前选择的是页面一");
                        }
                    });
                    break;

                case 1:
                    advs.get(1).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            sendMsg(1);
                            Log.e("log", "你当前选择的是页面二");
                        }
                    });
                    break;

                case 2:
                    advs.get(2).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            sendMsg(2);
                            Log.e("log", "你当前选择的是页面三");
                        }
                    });
                    break;

                case 3:
                    advs.get(3).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            sendMsg(3);
                            Log.e("log", "你当前选择的是页面四");
                        }
                    });
                    break;

                case 4:
                    advs.get(4).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            sendMsg(4);
                            Log.e("log", "你当前选择的是页面五");
                        }
                    });
                    break;

            }
            return advs.get(position);
        }

    }


    @Override
    public void onAttach(Activity activity) {
        Log.e("OneFragement", "onAttach");
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        Log.e("OneFragement", "onDetach");
        super.onDetach();
    }

    @Override
    public void onStop() {
        Log.e("OneFragement", "onStop");
        super.onStop();
    }

    @Override
    public void onPause() {
        Log.e("OneFragement", "onPause");
        super.onPause();
    }

    @Override
    public void onDestroy() {
        Log.e("OneFragement", "onDestroy");
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        Log.e("OneFragement", "onDestroyView");
        super.onDestroyView();
    }
}
