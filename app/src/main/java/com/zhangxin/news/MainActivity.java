package com.zhangxin.news;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.zhangxin.news.View.EightFragment;
import com.zhangxin.news.View.FiveFragment;
import com.zhangxin.news.View.FourFragment;
import com.zhangxin.news.View.NineFragment;
import com.zhangxin.news.View.OneFragment;
import com.zhangxin.news.View.SevenFragment;
import com.zhangxin.news.View.SixFragment;
import com.zhangxin.news.View.ThreeFragment;
import com.zhangxin.news.View.TwoFragment;
import com.zhangxin.news.View.WebViewActivity;
import com.zhangxin.news.Webservice.WebAddress;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    private static MainActivity mainActivity;
    private Context context = this;
    private Handler uiHandler;
    private TabLayout mTablayout;
    private ViewPager mViewPager;
    private Fragment currentFragment;
    private Fragment FragmentOne,FragmentTwo,FragmentThree,FragmentFour,FragmentFive,FragmentSix,FragmentSeven,FragmentEight,FragmentNine;
    private TabLayout.Tab one, two, three, four, five, six, seven, eight, nine;

    private Timer timer = null;
    private TimerTask timeTask = null;
    private boolean isExit = false; // 标记是否要退出
    private Intent intent = null;

    public static MainActivity getInstance() {
        if (mainActivity == null) {
            mainActivity = new MainActivity();
        }
        return mainActivity;
    }

    public Context getContext(){
        return context;
    }

    public Handler getUiHandler() {
        return uiHandler;
    }

    public MainActivity() {
        uiHandler = new Handler() {
            public void handleMessage(Message msg) {
                Log.e("uiHandler:", " = new Handler()##############");
                switch (msg.what) {
                    //加载WebView内容
                    case 0: {
                        if (msg.obj == null) {
                            Log.e("Activity:", " startActivity(intent);");
                            Intent intent = new Intent(context, WebViewActivity.class);
                            context.startActivity(intent);
                        } else {
                            Log.e("Activity:", " startActivity(intent);");
                            Intent intent = new Intent(context, WebViewActivity.class);
                            WebAddress.getUrl = WebAddress.picNews[Integer.parseInt(msg.obj + "")];
                            WebAddress.getTitle = "加载中...";
                            context.startActivity(intent);
                        }
                    }
                    break;
                    //切换Fragement
                    case 1:{
                        Log.e("Activity:", " Reload Fragement");
                        initViews();
                        initEvents();
                    }
                    default:
                        break;
                }
            }
        };
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.e("MainActivity:", " onCreate()###############################");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mainActivity = this;
        timer = new Timer();

        initViews();
        initEvents();
    }

    private void initEvents() {

        mTablayout.setOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                if (tab == mTablayout.getTabAt(0)) {
                    mViewPager.setCurrentItem(0);
                } else if (tab == mTablayout.getTabAt(1)) {
                    mViewPager.setCurrentItem(1);
                } else if (tab == mTablayout.getTabAt(2)) {
                    mViewPager.setCurrentItem(2);
                } else if (tab == mTablayout.getTabAt(3)) {
                    mViewPager.setCurrentItem(3);
                } else if (tab == mTablayout.getTabAt(4)) {
                    mViewPager.setCurrentItem(4);
                } else if (tab == mTablayout.getTabAt(5)) {
                    mViewPager.setCurrentItem(5);
                } else if (tab == mTablayout.getTabAt(6)) {
                    mViewPager.setCurrentItem(6);
                } else if (tab == mTablayout.getTabAt(7)) {
                    mViewPager.setCurrentItem(7);
                } else if (tab == mTablayout.getTabAt(8)) {
                    mViewPager.setCurrentItem(8);
                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {
                if (tab == mTablayout.getTabAt(0)) {
                } else if (tab == mTablayout.getTabAt(1)) {
                } else if (tab == mTablayout.getTabAt(2)) {
                } else if (tab == mTablayout.getTabAt(3)) {
                } else if (tab == mTablayout.getTabAt(4)) {
                } else if (tab == mTablayout.getTabAt(5)) {
                } else if (tab == mTablayout.getTabAt(6)) {
                } else if (tab == mTablayout.getTabAt(7)) {
                } else if (tab == mTablayout.getTabAt(8)) {
                }
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

    }

    private void initViews() {

        mTablayout = (TabLayout) findViewById(R.id.tabLayout);
        mViewPager = (ViewPager) findViewById(R.id.viewPager);

        mViewPager.setAdapter(new FragmentPagerAdapter(getSupportFragmentManager()) {

            private String[] mTitles = new String[]{"头条", "娱乐", "热点", "体育", "成都", "财经", "科技", "汽车", "历史"};

            @Override
            public Fragment getItem(int position) {
                if (position == 0) {
                    currentFragment = new OneFragment();
                    return currentFragment;
                } else if (position == 1) {
                    currentFragment = new TwoFragment();
                    return currentFragment;
                } else if (position == 2) {
                    currentFragment = new ThreeFragment();
                    return currentFragment;
                } else if (position == 3) {
                    currentFragment = new FourFragment();
                    return currentFragment;
                } else if (position == 4) {
                    currentFragment = new FiveFragment();
                    return currentFragment;
                } else if (position == 5) {
                    currentFragment = new SixFragment();
                    return currentFragment;
                } else if (position == 6) {
                    currentFragment = new SevenFragment();
                    return currentFragment;
                } else if (position == 7) {
                    currentFragment = new EightFragment();
                    return currentFragment;
                } else if (position == 8) {
                    currentFragment = new NineFragment();
                    return currentFragment;
                }
                if(currentFragment == null)
                    currentFragment = new OneFragment();
                return currentFragment;
            }

            @Override
            public int getCount() {
                return mTitles.length;
            }

            @Override
            public CharSequence getPageTitle(int position) {
                return mTitles[position];
            }

        });

        mTablayout.setupWithViewPager(mViewPager);

        one = mTablayout.getTabAt(0);
        two = mTablayout.getTabAt(1);
        three = mTablayout.getTabAt(2);
        four = mTablayout.getTabAt(3);
        five = mTablayout.getTabAt(4);
        six = mTablayout.getTabAt(5);
        seven = mTablayout.getTabAt(6);
        eight = mTablayout.getTabAt(7);
        nine = mTablayout.getTabAt(8);
    }

    @Override
    public void onBackPressed() {
        if (isExit) {
            finish();
        } else {
            isExit = true;
            Toast.makeText(MainActivity.this, "再按一次退出网易新闻", Toast.LENGTH_SHORT).show();
            timeTask = new TimerTask() {
                @Override
                public void run() {
                    isExit = false;
                }
            };
            timer.schedule(timeTask, 2000);
        }
    }

    @Override
    protected void onDestroy() {
        Log.e("MainActivity:", " onDestroy()###############################");
        super.onDestroy();
    }

    @Override
    protected void onStop() {
        Log.e("MainActivity:", " onStop()###############################");
        super.onStop();
    }

    @Override
    protected void onPause() {
        Log.e("MainActivity:", " onPause()###############################");
        super.onPause();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

}
