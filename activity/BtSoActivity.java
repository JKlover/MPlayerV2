package com.example.sheng.mplayerv2.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.util.TypedValue;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.GravityEnum;
import com.afollestad.materialdialogs.MaterialDialog;
import com.afollestad.materialdialogs.Theme;
import com.chad.library.adapter.base.BaseQuickAdapter;
import com.example.sheng.mplayerv2.R;
import com.example.sheng.mplayerv2.adapter.SearchitemAdapter;
import com.example.sheng.mplayerv2.callback.StringDialogCallback;
import com.example.sheng.mplayerv2.domain.NetVideoBean;
import com.example.sheng.mplayerv2.net.myNetWork;
import com.example.sheng.mplayerv2.utils.ApkUtils;
import com.example.sheng.mplayerv2.utils.SpiderData;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
public class BtSoActivity extends AppCompatActivity {
    private SearchView searchView;
    private RecyclerView recyclerView;
    private SearchitemAdapter adapter;
    private List<NetVideoBean> lists;
    private boolean otherSearchStatus;
    private boolean noSearchData;
    private TextView relative_no_result;
    private Uri downLoadUri=Uri.parse("https://mobile.xunlei.com/");
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0:
                    //更新UI
                    Log.e("我已经被执行", "更新失效");
                    relative_no_result.setVisibility(View.VISIBLE);
                    break;
                case 1:
                    relative_no_result.setVisibility(View.GONE);
                    break;
            }
        }
    };



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bt_so);
        initView();
        customSearchView();
    }

    private void initView() {
        searchView=findViewById(R.id.search_view);
        recyclerView=findViewById(R.id.recyclerView);
        relative_no_result=findViewById(R.id.tv_no_result);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter=new SearchitemAdapter(this);
        recyclerView.setAdapter(adapter);
        adapter.openLoadAnimation(BaseQuickAdapter.SCALEIN);
        adapter.setOnItemClickListener((adapter, view, position) -> {
            NetVideoBean bean= (NetVideoBean) adapter.getItem(position);
            initContentData(bean.getHref() );
        });
    }

    private void customSearchView() {
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                otherSearchStatus=true;
                System.out.println("我收到了" + query);
                getResponse(query);
                searchView.clearFocus();
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                handler.sendEmptyMessage(1);
                adapter.getData().clear();
                return false;
            }
        });
    }

    private void getResponse(String keyWord) {
//        OkGo.<String>get("https://btso.pw/search/"+keyWord)//
//                .tag(this)//
//                .headers("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/66.0.3359.139 Safari/537.36")//
//                .execute(new StringDialogCallback(this) {
//                    @Override
//                    public void onError(Response<String> response) {
//
//                    }
//
//                    @Override
//                    public void onSuccess(Response<String> response) {
//                        String html= response.body();
//                        System.out.println(html);
//                        lists=spiderHtml(html);
//                        if (lists.size()>=0&&lists!=null){
//                            if (otherSearchStatus){
//                                //第二次搜索清空原来的的集合重新加载
//                                noSearchData=false;
//                                adapter.getData().clear();
//                                recyclerView.setVisibility(View.VISIBLE);
//                                adapter.setNewData(lists);
//                                handler.sendEmptyMessage(1);
//                            }else  {
//                                recyclerView.setVisibility(View.VISIBLE);
//                                adapter.setNewData(lists);
//                                handler.sendEmptyMessage(1);
//                            }
//
//                        }
//
//
//                    }
//                });
        myNetWork.getInstance().netWorkGet(BtSoActivity.this, "https://btso.pw/search/" + keyWord, new myNetWork.CallBackListener() {
            @Override
            public void onSuccess(String html) {
//                System.out.println(html);
//                lists=spiderHtml(html);
                lists= SpiderData.getInstance().spiderHtmlBTSO(html);
                if (lists.size()==0){
                    handler.sendEmptyMessageDelayed(0,100);//不发延迟处理的消息无法更新视图
                }
                if (lists.size()>=0&&lists!=null){
                    if (otherSearchStatus){
                        //第二次搜索清空原来的的集合重新加载
                        noSearchData=false;
                        adapter.getData().clear();
                        recyclerView.setVisibility(View.VISIBLE);
                        adapter.setNewData(lists);
                        handler.sendEmptyMessage(1);
                    }else  {
                        recyclerView.setVisibility(View.VISIBLE);
                        adapter.setNewData(lists);
                        handler.sendEmptyMessage(1);
                    }

                }
            }

            @Override
            public void onError(String error) {

            }
        });
    }
//    private List<NetVideoBean> spiderHtml(String html) {
//        List<NetVideoBean> beans=new ArrayList<>();
//        Document doc= Jsoup.parse(html);
//        if (!doc.select("div.data-list").isEmpty()){
//            Elements getData=doc.select("div.data-list");
//            Elements lis=getData.select("div.row");
//            for (int i=0;i<lis.size();i++){
//                if (i==0){
//                    continue;
//                }
//                NetVideoBean bean=new NetVideoBean();
//                String name=lis.get(i).select("div.col-xs-12.col-sm-8.col-lg-9.file").text();
//                Log.e("电影名",name);
//                bean.setTitle(name);
//
//                String href=lis.get(i).select("a").attr("href");
//                Log.e("电影链接",href);
//                bean.setHref(href);
//                String size=lis.get(i).select("div.col-sm-2.col-lg-1.hidden-xs.text-right.size").text();
//
//                Log.e("大小",size);
//                bean.setType(size);
//
//                String date=lis.get(i).select("div.col-sm-2.col-lg-2.hidden-xs.text-right.date").text();
//                bean.setTime(date);
//                Log.e("日期",date);
//                beans.add(bean);
//            }
//        }else {
//            noSearchData=true;
//
//        }
//
//        if (noSearchData){
//            handler.sendEmptyMessageDelayed(0,100);//不发延迟处理的消息无法更新视图
//        }
//
//        Log.e("集合的大小为", String.valueOf(beans.size()));
//        return beans;
//    }

    /**
     * 解决跳转点击返回时键盘自动唤出
     */
    @Override
    protected void onStart() {
        super.onStart();
        searchView.clearFocus();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);

    }
    private void initContentData(String href) {
//        OkGo.<String>get(href)//
//                .tag(this)//
//                .headers("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/66.0.3359.139 Safari/537.36")//
//                .execute(new StringDialogCallback(BtSoActivity.this) {
//                    @Override
//                    public void onError(Response<String> response) {
//
//                    }
//
//                    @Override
//                    public void onSuccess(Response<String> response) {
//
//                    }
//                });
       myNetWork.getInstance().netWorkGet(BtSoActivity.this, href, new myNetWork.CallBackListener() {
           @Override
           public void onSuccess(String html) {
               System.out.println( html);
               spiderContentData( html);
           }

           @Override
           public void onError(String error) {

           }
       });
    }

    private void spiderContentData(String html) {
        Document doc=Jsoup.parse(html);
        String mBT=doc.select("#magnetLink").text();
        Log.e("磁力链接为",mBT);
        new MaterialDialog.Builder(BtSoActivity.this)
                    .title("获取链接")
                    .titleGravity(GravityEnum.CENTER)
                    .content(mBT)
                    .contentColorRes(android.R.color.white)
                    .backgroundColorRes(R.color.material_blue_grey_800)
                    .btnSelector(R.drawable.md_btn_selector_custom, DialogAction.POSITIVE)
                    .positiveColor(Color.WHITE)
                    .positiveText("下载")
                    .negativeText("取消")
                    .theme(Theme.DARK)
                    .onPositive((dialog, which) -> {
                        if (which==DialogAction.POSITIVE){
//                            Toast.makeText(BtSoActivity.this,"我被点击了成功了",Toast.LENGTH_SHORT).show();

                            if (ApkUtils.getXLIntent() != null){
                                Uri uri = Uri.parse(mBT);
                                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                                startActivity(intent);
                            }else {
                                new AlertDialog.Builder(BtSoActivity.this)
                                        .setTitle("注意")
                                        .setMessage("下载美剧需要迅雷，请先安装迅雷或者其他BT下载软件,是否前往下载迅雷")
                                        .setPositiveButton("是", (dialogInterface, i) -> {
                                            if (i==-1){
                                                Intent intent = new Intent(Intent.ACTION_VIEW, downLoadUri);
                                                startActivity(intent);
                                            }
                                        })
                                        .setNegativeButton("否", null)
                                        .show();


                            }
                        }
                    })
                    .show();
        
       





    }

}
