package com.example.sheng.mplayerv2.activity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.LongDef;
import android.support.annotation.Nullable;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.sheng.mplayerv2.R;
import com.example.sheng.mplayerv2.adapter.NumUrlAdapter;
import com.example.sheng.mplayerv2.domain.NetVideoBean;
import com.example.sheng.mplayerv2.domain.NumUrlBean;
import com.example.sheng.mplayerv2.net.myNetWork;
import com.example.sheng.mplayerv2.utils.ImageLoaderUtils;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;


/**
 * //                            _ooOoo_
 * //                           o8888888o
 * //                           88" . "88
 * //                           (| -_- |)
 * //                           O\  =  /O
 * //                        ____/`---'\____
 * //                      .'  \\|     |//  `.
 * //                     /  \\|||  :  |||//  \
 * //                    /  _||||| -:- |||||-  \
 * //                    |   | \\\  -  /// |   |
 * //                    | \_|  ''\---/''  |   |
 * //                    \  .-\__  `-`  ___/-. /
 * //                  ___`. .'  /--.--\  `. . __
 * //               ."" '<  `.___\_<|>_/___.'  >'"".
 * //              | | :  `- \`.;`\ _ /`;.`/ - ` : | |
 * //              \  \ `-.   \_ __\ /__ _/   .-` /  /
 * //         ======`-.____`-.___\_____/___.-`____.-'======
 * //                            `=---='
 * //        ^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^
 * //                      Buddha Bless, No Bug !
 * /**
 * Created by st on 2018/1/3
 */
public  class ContenView  extends AppCompatActivity {
    private Toolbar mTbar;
    private CollapsingToolbarLayout mCTbarL;
    private ImageView mIv;
    private TextView mTv;
    private RecyclerView mRlv;
    private NetVideoBean bean;
    private NumUrlAdapter mAdapter;
    private List<NumUrlBean>beans;
    private int playStatus; //设置视频的类型是m3u8的可以用播放器播放,是网页的可以用游览器播放
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0:
                    mAdapter.setNewData(beans);
                    break;
                case 1: ImageLoaderUtils.getInstance().display(mIv,img);
                    break;
            }
        }
    };
    private String img;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content_view);
        bean= (NetVideoBean) getIntent().getSerializableExtra("NET_VIDEO_BEAN");
        initView();
    }

    private void initView() {
        mTbar=findViewById(R.id.toolbar);
        mCTbarL=findViewById(R.id.toolbar_layout);
        mIv=findViewById(R.id.iv_icon);
        mTv=findViewById(R.id.tv_context);
        mRlv=findViewById(R.id.rlv_context);
        setSupportActionBar(mTbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        mTbar.setNavigationOnClickListener(v -> onBackPressed());
        mTbar.setTitle(bean.getTitle());
        mCTbarL.setTitle(bean.getTitle());
        mRlv.setFocusableInTouchMode(false);
        mAdapter=new NumUrlAdapter(this);
        mRlv.setLayoutManager(new GridLayoutManager(this,3, GridLayoutManager.VERTICAL,false));
        mRlv.setAdapter(mAdapter);
        beans=new ArrayList<>();
        initData(bean.getHref());
        mAdapter.setOnItemClickListener((adapter, view, position) -> {
            NumUrlBean bean= (NumUrlBean) adapter.getItem(position);
            if (playStatus==0){
                Intent intent=new Intent(ContenView.this,WebVideoPlayer.class);
                intent.putExtra("VIDEO",bean.getUrl());
                intent.putExtra("TITLE",bean.getTitle());
                startActivity(intent);
            }else {
                Intent intent=new Intent(ContenView.this,JcPlayer.class);
                intent.putExtra("VIDEO",bean.getUrl());
                intent.putExtra("TITLE",bean.getTitle());
                startActivity(intent);
            }



        });
    }

    private void initData(String url) {
        myNetWork.getInstance().commonNetWork( url, new myNetWork.CallBackListener() {
            @Override
            public void onSuccess(String html) {
                parseHtml(html);
            }

            @Override
            public void onError(String error) {

            }
        });
    }

    private void parseHtml(String html) {
        Document doc= Jsoup.parse(html);
        img=doc.getElementsByClass("lazy").attr("src");
        Log.e("图片",img);
//        Glide.with(this).load(img).error(R.mipmap.error_pic).into(mIv);//为滑动的ToolBar加载图片
        handler.sendEmptyMessage(1);
        String desc=doc.select("div.ibox.playBox").first().text();
        Log.e("简介",desc);
        mTv.setText(Html.fromHtml(desc));
        Elements getDatas=doc.select("div.ibox.playBox").last().getAllElements();
        Elements lis=getDatas.select("ul").last().select("li");
        for (Element e :lis){
            NumUrlBean bean=new NumUrlBean();
            String num=e.text();
            if (num.contains(".m3u8")){
                playStatus=1;//有
            }else {
                playStatus=0;//没有m3u8视频用浏览器播放
            }

            String n[] = num.split("\\$");//从"$"字符分割成两个部分取出集数
            Log.e("集数", n[0]);
            bean.setTitle(n[0]);
            String href=e.select("input").attr("value");
            Log.e("集数链接",href);
            bean.setUrl(href);
            beans.add(bean);
        }

        handler.sendEmptyMessage(0);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Glide.get(ContenView.this).clearMemory();
        System.gc();
        Log.e("TAG"," onDestroy() ");
        handler.removeCallbacksAndMessages(null);
    }
}
