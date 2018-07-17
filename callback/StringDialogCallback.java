
package com.example.sheng.mplayerv2.callback;

import android.app.Activity;
import android.app.ProgressDialog;

import com.afollestad.materialdialogs.MaterialDialog;
import com.lzy.okgo.callback.StringCallback;
import com.lzy.okgo.request.base.Request;

/**
 *st
 */
public abstract class StringDialogCallback extends StringCallback {
    private MaterialDialog materialDialog;
    public StringDialogCallback(Activity activity) {
        materialDialog=new MaterialDialog.Builder(activity)
                .title("稍等片刻")
                .content("正在卖力获取中...")
                .progress(true, 0)
                .progressIndeterminateStyle(false)
                .show();


    }

    @Override
    public void onStart(Request<String, ? extends Request> request) {

    }

    @Override
    public void onFinish() {
      materialDialog.dismiss();
    }
}
