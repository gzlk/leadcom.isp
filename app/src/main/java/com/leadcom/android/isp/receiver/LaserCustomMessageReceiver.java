package com.leadcom.android.isp.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.leadcom.android.isp.helper.LogHelper;

/**
 * <b>功能描述：</b><br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2018/05/16 16:21 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2018/05/16 16:21 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */
public class LaserCustomMessageReceiver extends BroadcastReceiver {

    private static final String TAG = "LaserCustomer";

    private void log(String string) {
        LogHelper.log(TAG, string);
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (null != intent) {
            String action = intent.getAction();
            log("onReceive: " + action);
        } else {
            log("onReceive: null intent");
        }
    }
}
