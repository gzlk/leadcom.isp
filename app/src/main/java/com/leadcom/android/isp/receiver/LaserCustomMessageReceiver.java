package com.leadcom.android.isp.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.leadcom.android.isp.R;
import com.leadcom.android.isp.fragment.archive.ArchiveDetailsWebViewFragment;
import com.leadcom.android.isp.helper.LogHelper;
import com.leadcom.android.isp.helper.StringHelper;
import com.leadcom.android.isp.model.archive.Archive;
import com.leadcom.android.isp.model.common.PushMessage;

import cn.jpush.android.api.JPushInterface;

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

    private void displayBundle(Bundle bundle) {
        if (bundle == null) {
            return;
        }
        String string = "Bundle{";
        for (String key : bundle.keySet()) {
            string += " " + key + " => " + bundle.get(key) + ";";
        }
        string += " } Bundle";
        log(string);
    }

    private void parseBundle(Bundle bundle) {
        if (null == bundle) return;
        PushMessage push = PushMessage.fromJson(bundle.getString(JPushInterface.EXTRA_EXTRA, "{}"));
        if (null != push) {
            push.setId(bundle.getString(JPushInterface.EXTRA_MSG_ID, ""));
            push.setTitle(bundle.getString(JPushInterface.EXTRA_NOTIFICATION_TITLE, ""));
            push.setContent(bundle.getString(JPushInterface.EXTRA_ALERT, ""));
            PushMessage.save(push);
        }
    }

    private void switchUI(Context context, Bundle bundle) {
        if (null == bundle) return;
        PushMessage push = PushMessage.fromJson(bundle.getString(JPushInterface.EXTRA_EXTRA, "{}"));
        assert push != null;
        switch (push.getMessageCode()) {
            case PushMessage.MsgCode.GROUP_ATTENTION:
                break;
            case PushMessage.MsgCode.GROUP_DOC_COMMENT:
            case PushMessage.MsgCode.GROUP_DOC_TRANSPORT:
                ArchiveDetailsWebViewFragment.open(context, StringHelper.getString(R.string.ui_text_archive_details_fragment_title), push.getDocId(), Archive.Type.GROUP, false);
                break;
            case PushMessage.MsgCode.USER_DOC_COMMENT:
                ArchiveDetailsWebViewFragment.open(context, StringHelper.getString(R.string.ui_text_archive_details_fragment_title), push.getDocId(), Archive.Type.USER, false);
                break;
            case PushMessage.MsgCode.GROUP_DOC_SHARE:
                ArchiveDetailsWebViewFragment.open(context, StringHelper.getString(R.string.ui_text_archive_details_fragment_title_draft), push.getDocId(), Archive.Type.GROUP, true);
                break;
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        if (null != intent) {
            String action = intent.getAction();
            Bundle bundle = intent.getExtras();
            log("onReceive: " + action);
            displayBundle(bundle);
            assert bundle != null;
            if (JPushInterface.ACTION_REGISTRATION_ID.equals(action)) {
                String regId = bundle.getString(JPushInterface.EXTRA_REGISTRATION_ID);
                log("[MyReceiver] 接收Registration Id : " + regId);
            } else if (JPushInterface.ACTION_MESSAGE_RECEIVED.equals(action)) {
                log("收到了自定义消息。消息内容是：" + bundle.getString(JPushInterface.EXTRA_MESSAGE));
                // 自定义消息不会展示在通知栏，完全要开发者写代码去处理
            } else if (JPushInterface.ACTION_NOTIFICATION_RECEIVED.equals(action)) {
                log("收到了通知");
                parseBundle(bundle);
                // 在这里可以做些统计，或者做些其他工作
            } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(action)) {
                log("用户点击打开了通知");
                switchUI(context, bundle);
                // 在这里可以自己写代码去定义用户点击后的行为
                //Intent i = new Intent(context, WelcomeActivity.class);  //自定义打开的界面
                //i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                //context.startActivity(i);
            } else {
                log("Unhandled intent - " + action);
            }
        } else {
            log("onReceive: null intent");
        }
    }
}
