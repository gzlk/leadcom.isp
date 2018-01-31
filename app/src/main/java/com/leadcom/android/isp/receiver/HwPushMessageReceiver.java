package com.leadcom.android.isp.receiver;

import android.content.Context;
import android.os.Bundle;

import com.huawei.hms.support.api.push.PushReceiver;
import com.leadcom.android.isp.helper.LogHelper;
import com.netease.nimlib.sdk.mixpush.HWPushMessageReceiver;


/**
 * <b>功能描述：</b>华为推送log<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2018/01/31 13:14 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>version: 1.0.0 <br />
 * <b>修改时间：</b>2017/10/04 18:50 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */
public class HwPushMessageReceiver extends HWPushMessageReceiver {

    private static final String TAG = "HwPush";

    private void log(String string) {
        LogHelper.log(TAG, string);
    }

    @Override
    public boolean onPushMsg(Context context, byte[] raw, Bundle bundle) {
        try {
            //CP可以自己解析消息内容，然后做相应的处理
            String content = new String(raw, "UTF-8");
            log("onPushMsg(透传): " + content);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return super.onPushMsg(context, raw, bundle);
    }

    @Override
    public void onEvent(Context context, PushReceiver.Event event, Bundle extras) {
        super.onEvent(context, event, extras);
        log("onEvent: " + event);
    }

    @Override
    public void onPushState(Context context, boolean pushState) {
        super.onPushState(context, pushState);
        log("onPushState: " + pushState);
    }

    @Override
    public void onToken(Context context, String token, Bundle extras) {
        super.onToken(context, token, extras);
        log("onToken: " + token);
    }
}
