package com.leadcom.android.isp.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.leadcom.android.isp.BuildConfig;
import com.leadcom.android.isp.api.common.PushMsgRequest;
import com.leadcom.android.isp.api.listener.OnSingleRequestListener;
import com.leadcom.android.isp.application.App;
import com.leadcom.android.isp.etc.SysInfoUtil;
import com.leadcom.android.isp.fragment.archive.ArchiveDetailsFragment;
import com.leadcom.android.isp.fragment.individual.moment.MomentDetailsFragment;
import com.leadcom.android.isp.fragment.organization.ActivitiesFragment;
import com.leadcom.android.isp.fragment.organization.GroupAuthorizeFragment;
import com.leadcom.android.isp.helper.LogHelper;
import com.leadcom.android.isp.helper.StringHelper;
import com.leadcom.android.isp.listener.OnTaskCompleteListener;
import com.leadcom.android.isp.model.archive.Archive;
import com.leadcom.android.isp.model.common.PushMessage;
import com.leadcom.android.isp.model.common.PushMessage.Extra;

import cn.jpush.android.api.JPushInterface;

/**
 * <b>功能描述：</b>推送通知接收器<br />
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
        StringBuilder string = new StringBuilder("Bundle {");
        for (String key : bundle.keySet()) {
            string.append("\"").append(key).append("\": \"").append(bundle.get(key)).append("\",");
        }
        string.append("} Bundle");
        log(string.toString());
    }

    public static void switchUI(final Context context, Extra extra) {
        if (null == extra) return;

        final boolean isAppForeground = SysInfoUtil.isAppOnForeground(context, BuildConfig.APPLICATION_ID);
        switch (extra.getMessageCode()) {
            case PushMessage.MsgCode.GROUP_ACTIVITY_DELIVER:
            case PushMessage.MsgCode.GROUP_ACTIVITY_PUBLISH:
            case PushMessage.MsgCode.GROUP_ACTIVITY_REPLY:
                final String activityId = extra.getGroActivityId();
                final String groupId = extra.getGroupId();
                // 活动相关的通知
                if (extra.getMessageCode().equals(PushMessage.MsgCode.GROUP_ACTIVITY_PUBLISH)) {
                    Archive archive = new Archive();
                    archive.setId(activityId);
                    archive.setDocType(Archive.ArchiveType.ACTIVITY);
                    archive.setFromGroupId(extra.getFromGroupId());
                    archive.setGroActivityId(activityId);
                    archive.setGroupId(groupId);
                    archive.setH5(extra.getH5());
                    ArchiveDetailsFragment.open(context, archive, isAppForeground);
                } else {
                    final Archive archive = App.app().getActivity(activityId);
                    if (0 == App.app().getActivities().size() || null == archive) {
                        App.app().fetchingActivities(groupId, new OnTaskCompleteListener() {
                            @Override
                            public void onComplete() {
                                Archive doc = App.app().getActivity(activityId);
                                if (null != doc) {
                                    ArchiveDetailsFragment.open(context, doc, isAppForeground);
                                } else {
                                    ActivitiesFragment.open(context, groupId, "");
                                }
                            }
                        });
                    } else {
                        ArchiveDetailsFragment.open(context, archive, isAppForeground);
                    }
                }
                break;
            case PushMessage.MsgCode.GROUP_ATTENTION:
            case PushMessage.MsgCode.GROUP_ATTENTION_CANCEL:
                //GroupConcernedFragment.open(context, extra.getGroupId(), "", ConcernRequest.CONCERN_FROM);
                break;
            case PushMessage.MsgCode.GROUP_AUTHORIZE:
            case PushMessage.MsgCode.GROUP_AUTHORIZE_CANCEL:
                GroupAuthorizeFragment.open(context, extra.getGroupId());
                break;
            case PushMessage.MsgCode.GROUP_DOC_COMMENT:
            case PushMessage.MsgCode.GROUP_DOC_TRANSPORT:
            case PushMessage.MsgCode.GROUP_DOC_LIKE:
            case PushMessage.MsgCode.GROUP_DOC_REPLY:
                ArchiveDetailsFragment.open(context, extra.getGroupId(), extra.getDocId(), false, isAppForeground, extra.getDocUserId());
                break;
            case PushMessage.MsgCode.USER_DOC_COMMENT:
            case PushMessage.MsgCode.USER_DOC_LIKE:
                ArchiveDetailsFragment.open(context, extra.getGroupId(), extra.getDocId(), false, isAppForeground, extra.getDocUserId());
                break;
            case PushMessage.MsgCode.GROUP_DOC_SHARE:
                ArchiveDetailsFragment.open(context, extra.getGroupId(), extra.getDocId(), true, isAppForeground, extra.getDocUserId());
                break;
            case PushMessage.MsgCode.USER_MMT_COMMENT:
            case PushMessage.MsgCode.USER_MMT_LIKE:
                // 到动态详情页
                MomentDetailsFragment.open(context, extra.getUserMmtId());
                break;
        }
    }

    private void resetStatus(final Context context, final Extra extra) {
        if (null == extra || StringHelper.isEmpty(extra.getMsgId(), true)) {
            return;
        }
        PushMsgRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<PushMessage>() {
            @Override
            public void onResponse(PushMessage pushMessage, boolean success, String message) {
                super.onResponse(pushMessage, success, message);
                if (success) {
                    App.dispatchCallbacks();
                    switchUI(context, extra);
                }
            }
        }).find(extra.getMsgId());
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
                App.dispatchCallbacks();
                //parseBundle(bundle);
                // 在这里可以做些统计，或者做些其他工作
            } else if (JPushInterface.ACTION_NOTIFICATION_OPENED.equals(action)) {
                log("用户点击打开了通知");
                Extra extra = Extra.fromJson(bundle.getString(JPushInterface.EXTRA_EXTRA, "{}"));
                resetStatus(context, extra);
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
