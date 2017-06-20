package com.gzlk.android.isp.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.gzlk.android.isp.activity.MainActivity;
import com.gzlk.android.isp.crash.system.SysInfoUtil;
import com.gzlk.android.isp.helper.LogHelper;
import com.gzlk.android.isp.helper.NotificationHelper;
import com.gzlk.android.isp.lib.Json;
import com.gzlk.android.isp.nim.model.notification.NimMessage;
import com.netease.nimlib.sdk.NimIntent;
import com.netease.nimlib.sdk.msg.model.CustomNotification;

/**
 * <b>功能描述：</b>网易云信自定义通知接收器<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/05/25 16:42 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/05/25 16:42 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class NimNotificationReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = context.getPackageName() + NimIntent.ACTION_RECEIVE_CUSTOM_NOTIFICATION;
        if (action.equals(intent.getAction())) {
            // 从 intent 中取出自定义通知， intent 中只包含了一个 CustomNotification 对象
            CustomNotification notification = (CustomNotification) intent.getSerializableExtra(NimIntent.EXTRA_BROADCAST_MSG);
            // 第三方 APP 在此处理自定义通知：存储，处理，展示给用户等
            LogHelper.log("demo", "receive custom notification: " + notification.getContent() + " from :" + notification.getSessionId() + "/" + notification.getSessionType());
            if (!SysInfoUtil.isAppOnForeground(context)) {
                String json = notification.getContent();
                // 如果app已经隐藏到后台，则需要打开通过系统通知来提醒用户
                NimMessage msg = Json.gson().fromJson(json, NimMessage.class);
                if (null != msg) {
                    Intent extra = new Intent().putExtra(MainActivity.EXTRA_NOTIFICATION, msg);
                    NotificationHelper.helper(context).show("系统通知", msg.getMsgContent(), extra);
                }
            }
        }
    }
}
