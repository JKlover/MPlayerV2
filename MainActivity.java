package com.example.sheng.mplayerv2;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.Gravity;
import android.view.MenuItem;

import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;

import android.widget.RelativeLayout;

import android.widget.TextView;

import com.example.sheng.mplayerv2.activity.BtSoActivity;
import com.example.sheng.mplayerv2.activity.SearchActivity;

import com.example.sheng.mplayerv2.fragment.NetVideoFg;
import com.example.sheng.mplayerv2.fragment.VideoFg;
import com.example.sheng.mplayerv2.utils.DataCleanManager;
import com.example.sheng.mplayerv2.utils.MessageEvent;
import com.roughike.bottombar.BottomBar;


import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import static com.example.sheng.mplayerv2.utils.DataCleanManager.getTotalCacheSize;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private List<Fragment> mFragments;
    private int mPosition;
    private Fragment mContent;
    private BottomBar mBottomBar;
    private MenuItem mItemSetting;
    private MenuItem mItemSearch;
    private int mTabClickStatus=0;
    private TextView mTbarTitle;
    private TextView mTvCacheSize;   // NavigationView上的缓存垃圾大小
    private DrawerLayout drawer;
    private NavigationView navigationView;

    private   Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch(msg.what){
                case 0:
                    try {
                        mTvCacheSize.setText(getTotalCacheSize(MainActivity.this));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    drawer.openDrawer(Gravity.LEFT);//保持抽屉菜单打开
                    break;
                case 1:
                    try {
                        mTvCacheSize.setText(getTotalCacheSize(MainActivity.this));
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }
    };




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initFragment();
        initBottomBar();
        initTbarAndDraw();
    }


    private void initTbarAndDraw() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        drawer = findViewById(R.id.drawer_layout);

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        navigationView = findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);
        RelativeLayout mNavHead= (RelativeLayout) navigationView.getHeaderView(0);
        mTvCacheSize=mNavHead.findViewById(R.id.tv_cache_size);
        try {
            mTvCacheSize.setText(getTotalCacheSize(this));
        } catch (Exception e) {
            e.printStackTrace();
        }
        initTimeTask();
    }

    private void initTimeTask() {
        new Timer().schedule(new TimerTask() {
            @Override
            public void run() {
                handler.sendEmptyMessage(1);
            }
        },0,2000);
    }

    private void initBottomBar() {
        mTbarTitle=findViewById(R.id.toolbar_title);
        mBottomBar=findViewById(R.id.bottomBar);
        mBottomBar.setOnTabSelectListener(tabId -> {
            switch (tabId) {
                case R.id.tab_a:
                    mPosition=0;
                    mTbarTitle.setText("本地");
                    if (mTabClickStatus==1){
                        mItemSearch.setVisible(false);
                        mItemSetting.setVisible(true);
                    }
                    break;
                case R.id.tab_b:
                    mPosition=1;
                    mTbarTitle.setText("网络");
                    mTabClickStatus=1;
                    mItemSetting.setVisible(false);
                    mItemSearch.setVisible(true);
                    break;
            }
            //根据位置获取Fragment
            Fragment to=getFragment();
            //替换
            switchFragment(mContent,to);

        });

    }

    private void initFragment() {
        mFragments=new ArrayList<>();
        mFragments.add(new VideoFg());
        mFragments.add(new NetVideoFg());
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    /**
     * 只执行一次
     * @param menu
     * @return
     */
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        mItemSetting=menu.findItem(R.id.menu_refresh);
        mItemSearch=menu.findItem(R.id.menu_search);
        mItemSearch.setVisible(false);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.menu_search) {
            startActivity(new Intent(getApplicationContext(), SearchActivity.class));
            return true;
        }else if (id==R.id.menu_refresh){
//            RecyclerView mRlv=getSupportFragmentManager().findFragmentById(R.id.fl_tab_container).getView().findViewById(R.id.rlv_video);
//            mRlv.smoothScrollToPosition(0);
            EventBus.getDefault().post(new MessageEvent(1));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private void switchFragment(Fragment from, Fragment to) {
     if (from!=to){
         mContent=to;
         FragmentTransaction ft=getSupportFragmentManager().beginTransaction();
         if (!to.isAdded()){

             if (from!=null){
                 ft.hide(from);
             }
             if (to!=null){
                 ft.add(R.id.fl_tab_container,to).commit();
             }

         }else {
             if (from!=null){
                 ft.hide(from);
             }
             if (to!=null){
                 ft.show(to).commit();
             }

         }

     }

    }
    private Fragment getFragment() {
        Fragment fragment=mFragments.get(mPosition);
        return fragment;
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        // Handle navigation view item clicks here.
        int id = item.getItemId();
        if (id==R.id.nav_bt_down){
            startActivity(new Intent(MainActivity.this,BtSoActivity.class));
        }
        if (id == R.id.nav_git) {
          Intent intent=new Intent(MainActivity.this, WebActivity.class);
          intent.putExtra("GITHUB_URL","https://github.com/JKlover/MPlayerV1");
          startActivity(intent);
            // Handle the camera action
        } else if (id == R.id.nav_about) {
            DataCleanManager.clearAllCache(MainActivity.this);
            handler.sendEmptyMessage(0);

        }
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
    }


}
