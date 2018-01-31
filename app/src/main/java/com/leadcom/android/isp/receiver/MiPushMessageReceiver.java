package com.leadcom.android.isp.receiver;

import android.content.Context;

import com.leadcom.android.isp.helper.LogHelper;
import com.xiaomi.mipush.sdk.MiPushCommandMessage;
import com.xiaomi.mipush.sdk.MiPushMessage;

/**
 * <b>功能描述：</b>小米推送本地log显示<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2018/01/31 12:02 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>version: 1.0.0 <br />
 * <b>修改时间：</b>2017/10/04 18:50 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */
public class MiPushMessageReceiver extends com.netease.nimlib.sdk.mixpush.MiPushMessageReceiver {

    private static final String TAG = "MiPush";

    private void log(String string) {
        LogHelper.log(TAG, string);
    }

    @Override
    public void onCommandResult(Context context, MiPushCommandMessage message) {
        super.onCommandResult(context, message);
        log("onCommandResult: " + message.toString());
    }

    @Override
    public void onNotificationMessageArrived(Context context, MiPushMessage message) {
        super.onNotificationMessageArrived(context, message);
        log("onNotificationMessageArrived: " + message.toString());
    }

    @Override
    public void onNotificationMessageClicked(Context context, MiPushMessage message) {
        super.onNotificationMessageClicked(context, message);
        log("onNotificationMessageClicked: " + message.toString());
    }

    @Override
    public void onReceivePassThroughMessage(Context context, MiPushMessage message) {
        super.onReceivePassThroughMessage(context, message);
        log("onReceivePassThroughMessage: " + message.toString());
    }

    @Override
    public void onReceiveRegisterResult(Context context, MiPushCommandMessage message) {
        super.onReceiveRegisterResult(context, message);
        log("onReceiveRegisterResult: " + message.toString());
    }
}
