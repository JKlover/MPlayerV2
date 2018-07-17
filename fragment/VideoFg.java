package com.example.sheng.mplayerv2.fragment;

import android.Manifest;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.sheng.mplayerv2.MainActivity;
import com.example.sheng.mplayerv2.R;
import com.example.sheng.mplayerv2.activity.JcPlayer;
import com.example.sheng.mplayerv2.adapter.VideoAdapter;
import com.example.sheng.mplayerv2.domain.VideoBean;
import com.example.sheng.mplayerv2.utils.MessageEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import pub.devrel.easypermissions.EasyPermissions;


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
public class VideoFg extends Fragment implements EasyPermissions.PermissionCallbacks{
    private Context mContext;
    private int READ_EXTERNAL_STORAGE_PERM=122;

    private RecyclerView mRecyclerView;
    private VideoAdapter mAdapter;
    private List<VideoBean> mList;

    private View notDataView;
    private boolean mNoData;
    private Handler handler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 0:
                    mAdapter.setNewData(mList);
                    break;
                case 1:

                    break;
            }
        }
    };

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mContext=getActivity();
        //注册接收消息
        EventBus.getDefault().register(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fg_video,container,false);
        initView(view);
        return view;
    }



    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (EasyPermissions.hasPermissions(getContext(), Manifest.permission.READ_EXTERNAL_STORAGE)) {
            // Have permission, do the thing!
            getData();
            Log.e("我被执行","onActivityCreated，if");
//            Toast.makeText(getActivity(), "TODO: SMS things", Toast.LENGTH_LONG).show();
        } else {
            // Request one permission
            Log.e("我被执行","onActivityCreated，else");
            EasyPermissions.requestPermissions(this, null,
                    READ_EXTERNAL_STORAGE_PERM, Manifest.permission.READ_EXTERNAL_STORAGE);
        }
    }

    private void getData() {
        mList.clear();
        new Thread(){
            @Override
            public void run() {
                super.run();
                ContentResolver resolver =mContext.getContentResolver();
                Uri uri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                String[] objs = {
                        MediaStore.Video.Media.DISPLAY_NAME,//视频文件在sdcard的名称
                        MediaStore.Video.Media.DURATION,//视频总时长
                        MediaStore.Video.Media.SIZE,//视频的文件大小
                        MediaStore.Video.Media.DATA,//视频的绝对地址
                        MediaStore.Video.Media.ARTIST,//歌曲的演唱者

                };
                Cursor cursor = resolver.query(uri, objs, null, null, null);
                if(cursor != null){
                    while (cursor.moveToNext()){
                        VideoBean videoBean=new VideoBean();

                        String name = cursor.getString(0);//视频的名称
                        videoBean.setName(name);

                        long duration = cursor.getLong(1);//视频的时长
                        videoBean.setDuration(duration);
                        Log.e("时长", String.valueOf(videoBean.getDuration()));

                        long size = cursor.getLong(2);//视频的文件大小
                        videoBean.setSize(size);
                        String playuri = cursor.getString(3);//视频的播放地址
                        videoBean.setData(playuri);
                        mList.add(videoBean);//写在上面
                    }

                    cursor.close();
//                    if (mList.size()==0){
//                        mNoData=true;
////                        onRefresh();
//                    }else {
//                        mNoData=false;
//                    }
                }
                Log.e("我被执行", String.valueOf(mList.size()));
                handler.sendEmptyMessage(0);
            }

        }.start();


        if (mList.size()==0){
            mNoData=true;
            onRefresh();
        }else {
            mNoData=false;
        }
    }

    private void onRefresh() {
        mAdapter.setEmptyView(R.layout.loading_view, (ViewGroup) mRecyclerView.getParent());
        new Handler().postDelayed(() -> {
            if (mNoData) {
                mAdapter.setEmptyView(notDataView);
                mNoData = false;
                Log.e("我被执行","onRefresh if");
            } else {
                Log.e("我被执行","onRefresh else");
                getData();
            }

        }, 1000);
    }

    private void initView(View view) {

        mRecyclerView=view.findViewById(R.id.rlv_video);
        mRecyclerView.setHasFixedSize(true);

        notDataView = getLayoutInflater().inflate(R.layout.empty_view, (ViewGroup) mRecyclerView.getParent(), false);

        notDataView.setOnClickListener(v -> onRefresh());
//
        mAdapter=new VideoAdapter(mContext);
//        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        GridLayoutManager gridLayoutManager=new GridLayoutManager(getContext(),2,GridLayoutManager.VERTICAL,false);
        mRecyclerView.setLayoutManager( gridLayoutManager);
        mRecyclerView.setAdapter(mAdapter);
        mList=new ArrayList<>();
        mAdapter.setOnItemClickListener((adapter, view1, position) -> {
            VideoBean bean= (VideoBean) adapter.getItem(position);
            Intent intent=new Intent(getContext(),JcPlayer.class);
            intent.putExtra("VIDEO",bean.getData());
            intent.putExtra("TITLE",bean.getName());
            startActivity(intent);
        });
        //初始化控件

    }



    /**
     *  //这个方法必须先执行后面的两个重写onPermissionsGranted和onPermissionsDenied才会回调
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this);
    }

    /**
     * 请求成功的操作
     * @param requestCode
     * @param perms
     */
    @Override
    public void onPermissionsGranted(int requestCode, @NonNull List<String> perms) {
        getData();
    }

    /**
     * 请求失败的操作
     * @param requestCode
     * @param perms
     */
    @Override
    public void onPermissionsDenied(int requestCode, @NonNull List<String> perms) {

    }

    /**
     * 用于接收消息
     * @param event
     */
    @Subscribe(threadMode = ThreadMode.MAIN)
    public   void onEventMessages(MessageEvent event){
        Log.e("传值为", String.valueOf(event.type));
         if (event.type==1){
//             Toast.makeText(mContext,"传过来消息了"+event.type,Toast.LENGTH_SHORT).show();
             getData();
         }
    }

    /**
     * 取消消息
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        EventBus.getDefault().unregister(this);
    }
}
