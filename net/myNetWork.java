package com.example.sheng.mplayerv2.net;

import android.app.Activity;

import com.example.sheng.mplayerv2.activity.BtSoActivity;
import com.example.sheng.mplayerv2.callback.StringDialogCallback;
import com.lzy.okgo.OkGo;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.model.Response;

import java.util.Map;

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
 * Created by st on 2018/1/14
 */
public class myNetWork {
    private myNetWork() {
    }
    private static myNetWork ourInstance =null;

    public static myNetWork getInstance() {
        if (ourInstance==null){
            synchronized (myNetWork.class){
                if (ourInstance==null){
                    ourInstance=new myNetWork();
                }
            }
        }
        return ourInstance;
    }
    public void commonNetWork(String url,CallBackListener listener){
        OkGo.<String>get(url)//
                .tag(this)//
                .headers("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/66.0.3359.139 Safari/537.36")//
                .headers("Referer","http://www.go1977.com/")
                .execute(new StringCallback() {
                    @Override
                    public void onError(Response<String> response) {
                                listener.onError(response.body());
                    }

                    @Override
                    public void onSuccess(Response<String> response) {
                                listener.onSuccess(response.body());
                    }
                });
    }
    //有等待dialog的OkGo的get请求
    public void netWorkGet(Activity activity,String href,CallBackListener listener){
        OkGo.<String>get(href)//
                .tag(this)//
                .headers("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/66.0.3359.139 Safari/537.36")//
                .execute(new StringDialogCallback(activity) {
                    @Override
                    public void onError(Response<String> response) {
                        listener.onError(response.body());
                    }

                    @Override
                    public void onSuccess(Response<String> response) {
                        listener.onSuccess(response.body());
                        System.out.println(response.body());
                    }
                });

    }
    //oKgoPost请求
    public void netWorkPost(Activity activity, String href, Map<String,String> map,CallBackListener listener){
        OkGo.<String>get(href)//
                .tag(this)//
                .params(map)
                .headers("User-Agent", "Mozilla/5.0 (Windows NT 10.0; WOW64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/66.0.3359.139 Safari/537.36")//
                .headers("Referer","http://www.go1977.com/")
                .execute(new StringDialogCallback(activity) {
                    @Override
                    public void onError(Response<String> response) {
                        listener.onError(response.body());
                    }

                    @Override
                    public void onSuccess(Response<String> response) {
                        listener.onSuccess(response.body());
                        System.out.println(response.body());
                    }
                });

    }



 public    interface CallBackListener{
        void onSuccess(String html);
        void onError(String error);
    }



}
