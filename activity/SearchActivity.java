package com.example.sheng.mplayerv2.activity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.TypedValue;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.TextView;


import com.example.sheng.mplayerv2.R;
import com.example.sheng.mplayerv2.adapter.SearchitemAdapter;
import com.example.sheng.mplayerv2.callback.StringDialogCallback;
import com.example.sheng.mplayerv2.domain.NetVideoBean;
import com.example.sheng.mplayerv2.net.myNetWork;
import com.example.sheng.mplayerv2.utils.SpiderData;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.model.Response;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.example.sheng.mplayerv2.interfaces.Api.BASE_URL;
import static com.example.sheng.mplayerv2.interfaces.Api.SEARCHURL_1977;

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
 * Created by st on 2018/1/30
 */
public  class SearchActivity extends AppCompatActivity {
    private SearchView searchView;
    private RecyclerView recyclerView;
    private SearchitemAdapter adapter;
    private List<NetVideoBean> lists;
    private boolean otherSearchStatus;
    private TextView relative_no_result;

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
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

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
    adapter.setOnItemClickListener((adapter, view, position) -> {
        NetVideoBean bean= (NetVideoBean) adapter.getItem(position);
        Intent intent = new Intent(SearchActivity.this, ContenView.class);
        intent.putExtra("NET_VIDEO_BEAN",bean);
        startActivity(intent);
    });
    }

    private void customSearchView() {
        searchView.setIconified(false);
        searchView.onActionViewExpanded();
        if (searchView!=null){
            try{
                Class<?> argClass = searchView.getClass();
                //--指定某个私有属性,mSearchPlate是搜索框父布局的名字
                Field ownField = argClass.getDeclaredField("mSearchPlate");
                //--暴力反射,只有暴力反射才能拿到私有属性
                ownField.setAccessible(true);
                View mView = (View) ownField.get(searchView);
                //--设置背景
                mView.setBackgroundColor(Color.TRANSPARENT);

            }catch (Exception e){
                e.printStackTrace();
            }
        }
        //获取ImageView的id
        int imgId = searchView.getContext().getResources().getIdentifier("android:id/search_mag_icon",null,null);
        //获取ImageView
        ImageView searchButton = searchView.findViewById(imgId);
        //设置图片
        searchButton.setImageResource(R.mipmap.search);
        //不使用默认
        searchView.setIconifiedByDefault(false);


        int id = searchView.getContext().getResources().getIdentifier("android:id/search_src_text",null,null);
        TextView textView = searchView.findViewById(id);
        //设置字体大小为14sp
        textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);//14sp
        //设置提示字体颜色
        textView.setTextColor(this.getResources().getColor(R.color.gray_color));
        //设置输入文字颜色
        textView.setHintTextColor(this.getResources().getColor(R.color.black_overlay));
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

    /**
     * 注意网站的前台提交给后台的处理的一般是POST要抓取网站的搜索引擎最好用POST模拟网站前台提交数据，有的网站用GET模拟请求也可以的
     * @param keyWord
     */
    private void getResponse(String keyWord) {
        Map<String,String> map=new HashMap<>();
        map.put("wd",keyWord);
        myNetWork.getInstance().netWorkPost(SearchActivity.this, SEARCHURL_1977, map, new myNetWork.CallBackListener() {
            @Override
            public void onSuccess(String html) {
                System.out.println(html);
//                lists=spiderHtml(html);
                lists= SpiderData.getInstance().spiderSearchResultZiyuanWang(html);
                if (lists.size()==0){
                    handler.sendEmptyMessageDelayed(0,100);//不发延迟处理的消息无法更新视图
                }
                if (lists.size()>=0&&lists!=null){
                    if (otherSearchStatus){
                        //第二次搜索清空原来的的集合重新加载
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
//        Document document= Jsoup.parse(html);
//        Elements getData=document.select("div.xing_vb");
//        Elements lis=getData.select("ul li");
//            for (Element e:lis){
//                if (!e.select("span.xing_vb4").isEmpty()){
//                    NetVideoBean bean=new NetVideoBean();
//                    String href=e.select("a").attr("href");
//                    bean.setHref(BASE_URL+href);
//                    Log.e("链接地址",BASE_URL+href);
//
//                    String title=e.getElementsByClass("xing_vb4").text();
//                    bean.setTitle(title);
//                    Log.e("视频名称",title);
//
//                    String type=e.getElementsByClass("xing_vb5").text();
//                    bean.setType(type);
//                    Log.e("类型",type);
//
//                    String date=e.getElementsByClass("xing_vb6").text();
//                    bean.setTime(date);
//                    Log.e("日期",date);
//                    beans.add(bean);
//                }
//            }
//        if (beans.size()==0){
//            handler.sendEmptyMessageDelayed(0,100);//不发延迟处理的消息无法更新视图
//        }
//
//            Log.e("集合的大小为", String.valueOf(beans.size()));
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

}
