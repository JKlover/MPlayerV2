package com.example.sheng.mplayerv2.utils;

import android.util.Log;

import com.example.sheng.mplayerv2.domain.NetVideoBean;
import com.example.sheng.mplayerv2.domain.NumUrlBean;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;

import static com.example.sheng.mplayerv2.interfaces.Api.BASE_URL;

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
 * Created by st on 2018/7/16
 */
public class SpiderData {
    private SpiderData() {

    }
    private static SpiderData ourInstance =null;

   public static SpiderData getInstance() {

        if (ourInstance==null){
            synchronized(SpiderData.class){
                if (ourInstance==null){
                    ourInstance=new SpiderData();
                }
            }
        }
        return ourInstance;
    }

    public List<NetVideoBean> spiderSearchResultZiyuanWang(String html) {
        List<NetVideoBean> beans=new ArrayList<>();
        Document document= Jsoup.parse(html);
        Elements getData=document.select("div.xing_vb");
        Elements lis=getData.select("ul li");
        for (Element e:lis){
            if (!e.select("span.xing_vb4").isEmpty()){
                NetVideoBean bean=new NetVideoBean();
                String href=e.select("a").attr("href");
                bean.setHref(BASE_URL+href);
                Log.e("链接地址",BASE_URL+href);

                String title=e.getElementsByClass("xing_vb4").text();
                bean.setTitle(title);
                Log.e("视频名称",title);

                String type=e.getElementsByClass("xing_vb5").text();
                bean.setType(type);
                Log.e("类型",type);

                String date=e.getElementsByClass("xing_vb6").text();
                bean.setTime(date);
                Log.e("日期",date);
                beans.add(bean);
            }
        }
        return beans;
    }


    public List<NetVideoBean> spiderHtmlBTSO(String html) {
        List<NetVideoBean> beans=new ArrayList<>();
        Document doc= Jsoup.parse(html);
        if (!doc.select("div.data-list").isEmpty()){
            Elements getData=doc.select("div.data-list");
            Elements lis=getData.select("div.row");
            for (int i=0;i<lis.size();i++){
                if (i==0){
                    continue;
                }
                NetVideoBean bean=new NetVideoBean();
                String name=lis.get(i).select("div.col-xs-12.col-sm-8.col-lg-9.file").text();
                Log.e("电影名",name);
                bean.setTitle(name);

                String href=lis.get(i).select("a").attr("href");
                Log.e("电影链接",href);
                bean.setHref(href);
                String size=lis.get(i).select("div.col-sm-2.col-lg-1.hidden-xs.text-right.size").text();

                Log.e("大小",size);
                bean.setType(size);

                String date=lis.get(i).select("div.col-sm-2.col-lg-2.hidden-xs.text-right.date").text();
                bean.setTime(date);
                Log.e("日期",date);
                beans.add(bean);
            }
        }
        Log.e("集合的大小为", String.valueOf(beans.size()));
        return beans;
    }
}
