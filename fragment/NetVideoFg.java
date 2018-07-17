package com.example.sheng.mplayerv2.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ExpandableListView;

import com.example.sheng.mplayerv2.R;
import com.example.sheng.mplayerv2.activity.NetVideoList;
import com.example.sheng.mplayerv2.adapter.IndicatorExpandableListAdapter;
import com.example.sheng.mplayerv2.domain.Group;
import com.example.sheng.mplayerv2.domain.Item;

import java.util.ArrayList;

import static com.example.sheng.mplayerv2.interfaces.Api.ACTION_MOVIE_URL;
import static com.example.sheng.mplayerv2.interfaces.Api.ANIMATION_URL;
import static com.example.sheng.mplayerv2.interfaces.Api.CHINA_TV_URL;
import static com.example.sheng.mplayerv2.interfaces.Api.COMEDY_MOVIE_URL;
import static com.example.sheng.mplayerv2.interfaces.Api.DOCUMENTARY_URL;
//import static com.example.sheng.mplayerv2.interfaces.Api.ETHIC_MOVIE_URL;
import static com.example.sheng.mplayerv2.interfaces.Api.EURAMERICAN_TY_URL;
import static com.example.sheng.mplayerv2.interfaces.Api.FEATURE_MOVIE_URL;
import static com.example.sheng.mplayerv2.interfaces.Api.GANGTAI_TV_URL;
import static com.example.sheng.mplayerv2.interfaces.Api.HORROR_MOVIE_URL;
import static com.example.sheng.mplayerv2.interfaces.Api.LOVE_MOVIE_URL;
import static com.example.sheng.mplayerv2.interfaces.Api.RIHAN_TY_URL;
import static com.example.sheng.mplayerv2.interfaces.Api.SCIENCE_MOVIE_URL;
import static com.example.sheng.mplayerv2.interfaces.Api.VARIETY_URL;
import static com.example.sheng.mplayerv2.interfaces.Api.WAR_MOVIE_URL;


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
 * Created by st on 2018/1/8
 */
public class NetVideoFg extends Fragment {
    private Context mContext;

    private ArrayList<Group> gData ;
    private ArrayList<ArrayList<Item>> iData ;
    private ArrayList<Item> lData ;

    private ExpandableListView myExlist;
    private IndicatorExpandableListAdapter myAdapter ;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext=getActivity();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fg_net_video,container,false);
        initView(view);
        return view;
    }



    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initData();
    }

    private void initData() {

        //数据准备
        gData = new ArrayList<>();
        iData = new ArrayList<>();
        gData.add(new Group("电影",""));
        gData.add(new Group("连续剧",""));
        gData.add(new Group("综艺",VARIETY_URL));
        gData.add(new Group("动漫",ANIMATION_URL));
        gData.add(new Group("记录片",DOCUMENTARY_URL));

        lData = new ArrayList<>();

        //电影组
        lData.add(new Item(ACTION_MOVIE_URL,"动作片"));
        lData.add(new Item(COMEDY_MOVIE_URL,"喜剧片"));
        lData.add(new Item(LOVE_MOVIE_URL,"爱情片"));
        lData.add(new Item( SCIENCE_MOVIE_URL,"科幻片"));
        lData.add(new Item(HORROR_MOVIE_URL,"恐怖片"));
        lData.add(new Item(FEATURE_MOVIE_URL,"剧情片"));
        lData.add(new Item(WAR_MOVIE_URL,"战争片"));
//        lData.add(new Item(ETHIC_MOVIE_URL,"伦理片"));
        iData.add(lData);
        //连续剧组
        lData = new ArrayList<>();
        lData.add(new Item(CHINA_TV_URL, "国产剧"));
        lData.add(new Item(GANGTAI_TV_URL, "港台剧"));
        lData.add(new Item(EURAMERICAN_TY_URL, "日韩剧"));
        lData.add(new Item(RIHAN_TY_URL, "欧美剧"));
        iData.add(lData);
        //综艺组
        lData = new ArrayList<>();
        iData.add(lData);
        //动漫组
        lData = new ArrayList<>();
        iData.add(lData);
        //记录片组
        lData = new ArrayList<>();
        iData.add(lData);

        myAdapter = new IndicatorExpandableListAdapter(gData,iData);
        myExlist.setAdapter(myAdapter);
        //Lambda表达式
        myExlist.setOnChildClickListener( (ExpandableListView parent, View v, int groupPosition, int childPosition, long id) ->{
                    Intent intent = new Intent(getContext(), NetVideoList.class);
                    intent.putExtra("NET_URL",iData.get(groupPosition).get(childPosition).getiUrl());
                    intent.putExtra("NET_VIDEO_TYPE",iData.get(groupPosition).get(childPosition).getiName());
                    startActivity(intent);

                    return true;
                }
        );
        myExlist.setOnGroupClickListener((ExpandableListView parent, View v, int groupPosition, long id)->{
            if (iData.get(groupPosition).isEmpty()) {// isEmpty没有

                Intent intent = new Intent(getContext(), NetVideoList.class);
                intent.putExtra("NET_URL",gData.get(groupPosition).getgUrl());
                intent.putExtra("NET_VIDEO_TYPE",gData.get(groupPosition).getgName());
                startActivity(intent);


                return true;
            } else {
                return false;
            }
        });

    }

    private void initView(View view) {
        myExlist = view.findViewById(R.id.exlist);
        // 清除默认的 Indicator
        myExlist.setGroupIndicator(null);
    }
}
