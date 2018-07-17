package com.example.sheng.mplayerv2.utils;

import android.support.annotation.NonNull;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.example.sheng.mplayerv2.R;
import com.example.sheng.mplayerv2.application.MyApp;

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
 * Created by st on 2018/1/16
 */
public class ImageLoaderUtils {
    private ImageLoaderUtils() {
    }
    private static ImageLoaderUtils ourInstance = null;

    public static ImageLoaderUtils getInstance() {
        if (ourInstance==null){
            synchronized (ImageLoaderUtils.class){
                if (ourInstance==null){
                    ourInstance=new ImageLoaderUtils();
                }
            }
        }
        return ourInstance;
    }

    public void display(@NonNull ImageView imageView, String url) {
        try {
            if (imageView.getContext()!=null){
                Glide.with(MyApp.getGlobalApplication()).load(url).placeholder(R.mipmap.shop_photo_frame).crossFade().into(imageView);
            }else {
                return ; }

        }catch (Exception e){
            return;
        }

    }

}
