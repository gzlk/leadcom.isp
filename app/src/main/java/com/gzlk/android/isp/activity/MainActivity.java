package com.gzlk.android.isp.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;

import com.gzlk.android.isp.R;
import com.gzlk.android.isp.api.listener.OnSingleRequestListener;
import com.gzlk.android.isp.api.org.GroupJoinRequest;
import com.gzlk.android.isp.api.org.InvitationRequest;
import com.gzlk.android.isp.fragment.main.MainFragment;
import com.gzlk.android.isp.helper.DialogHelper;
import com.gzlk.android.isp.helper.SimpleDialogHelper;
import com.gzlk.android.isp.helper.StringHelper;
import com.gzlk.android.isp.lib.Json;
import com.gzlk.android.isp.model.organization.Invitation;
import com.gzlk.android.isp.model.organization.JoinGroup;
import com.gzlk.android.isp.nim.model.NimMessage;
import com.gzlk.android.isp.nim.session.NimSessionHelper;
import com.netease.nim.uikit.NimUIKit;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.NimIntent;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.msg.MsgServiceObserve;
import com.netease.nimlib.sdk.msg.constant.MsgTypeEnum;
import com.netease.nimlib.sdk.msg.model.CustomNotification;
import com.netease.nimlib.sdk.msg.model.IMMessage;

/**
 * <b>功能描述：</b>主页窗体<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/04/06 16:44 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/04/06 16:44 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class MainActivity extends TitleActivity {

    public static final String EXTRA_NOTIFICATION = "leadcom.extra.notification";

    public static void start(Context context) {
        start(context, 0);
    }

    public static void start(Context context, int selectedIndex) {
        start(context, new Intent().putExtra(MainFragment.PARAM_SELECTED, selectedIndex));
    }

    public static void start(Context context, Intent extras) {
        context.startActivity(getIntent(context, extras));
    }

    public static Intent getIntent(Context context, Intent extras) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        if (null != extras) {
            if (!intent.hasExtra(MainFragment.RESULT_STRING)) {
                // 默认打开第一页
                intent.putExtra(MainFragment.PARAM_SELECTED, 0);
            }
            intent.putExtras(extras);
        }
        return intent;
    }

    private MainFragment mainFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        supportPressAgainToExit = true;
        supportTransparentStatusBar = true;
        isToolbarSupported = false;
        super.onCreate(savedInstanceState);
        // 接收消息
        //NIMClient.getService(MsgServiceObserve.class).observeReceiveMessage(incomingMessageObserver, true);
        // 接收自定义通知
        NIMClient.getService(MsgServiceObserve.class).observeCustomNotification(customNotificationObserver, true);

        if (null == mainFragment) {
            mainFragment = new MainFragment();
        }
        setMainFrameLayout(mainFragment);
        parseIntent();
        //registerUpgradeListener();
    }

    @Override
    protected boolean onBackKeyEvent(int keyCode, KeyEvent event) {
        return mainFragment.onBackKeyEvent();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        parseIntent();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.clear();
    }

    @Override
    protected void onDestroy() {
        NIMClient.getService(MsgServiceObserve.class).observeCustomNotification(customNotificationObserver, false);
        //NIMClient.getService(MsgServiceObserve.class).observeReceiveMessage(incomingMessageObserver, false);
        //PgyUpdateManager.unregister();
        super.onDestroy();
    }

    // 自定义消息接收类
//    Observer<List<IMMessage>> incomingMessageObserver = new Observer<List<IMMessage>>() {
//        @Override
//        public void onEvent(List<IMMessage> messages) {
//            // 处理新收到的消息，为了上传处理方便，SDK 保证参数 messages 全部来自同一个聊天对象。
//            for (IMMessage msg : messages) {
//                if (msg.getMsgType() == MsgTypeEnum.custom) {
//
//                }
//            }
//        }
//    };

    // 自定义系统通知类
    Observer<CustomNotification> customNotificationObserver = new Observer<CustomNotification>() {
        @Override
        public void onEvent(CustomNotification message) {
            // 在这里处理自定义通知。
            String json = message.getContent();
            if (!StringHelper.isEmpty(json)) {
                NimMessage msg = Json.gson().fromJson(json, NimMessage.class);
                if (null != msg) {
                    handleNimMessageDetails(msg);
                }
            }
        }
    };

    private void parseIntent() {
        Intent intent = getIntent();
        if (null != intent) {
            if (intent.hasExtra(NimIntent.EXTRA_NOTIFY_CONTENT)) {
                // 点击通知栏传过来的消息
                IMMessage message = (IMMessage) getIntent().getSerializableExtra(NimIntent.EXTRA_NOTIFY_CONTENT);
                switch (message.getSessionType()) {
                    case P2P:
                        // 点对点聊天
                        NimUIKit.startP2PSession(this, message.getSessionId());
                        break;
                    case Team:
                        // 群聊
                        NimSessionHelper.startTeamSession(this, message.getSessionId());
                        break;
                }
                if (message.getMsgType() == MsgTypeEnum.custom) {
                    NimMessage nim = (NimMessage) message.getAttachment();
                    handleNimMessageDetails(nim);
                }
            } else if (intent.hasExtra(EXTRA_NOTIFICATION)) {
                // 自定义系统通知
                NimMessage msg = (NimMessage) intent.getSerializableExtra(EXTRA_NOTIFICATION);
                handleNimMessageDetails(msg);
            }
        }
    }

    private void handleNimMessageDetails(final NimMessage msg) {
        String yes = "", no = "";
        switch (msg.getType()) {
            case NimMessage.Type.JOIN_TO_GROUP:
                yes = StringHelper.getString(R.string.ui_base_text_ok);
                no = StringHelper.getString(R.string.ui_base_text_reject);
                break;
            case NimMessage.Type.APPROVE_JOIN_GROUP:
                // 组织管理者同意，申请方只有一个按钮“知道了”
                yes = StringHelper.getString(R.string.ui_base_text_i_known);
                break;
            case NimMessage.Type.DISAPPROVE_JOIN_GROUP:
                // 组织管理者不同意，申请方都只有一个按钮“好吧”
                yes = StringHelper.getString(R.string.ui_base_text_ok_ba);
                break;
            case NimMessage.Type.INVITE_TO_GROUP:
                // 受邀者出现的对话框是“好”,“不用了”
                yes = StringHelper.getString(R.string.ui_base_text_ok);
                no = StringHelper.getString(R.string.ui_base_text_no_need);
                break;
            case NimMessage.Type.AGREE_TO_GROUP:
            case NimMessage.Type.DISAGREE_TO_GROUP:
                // 新成员同意或不同意邀请，邀请方都只有一个按钮“知道了”
                yes = StringHelper.getString(R.string.ui_base_text_i_known);
                break;
            case NimMessage.Type.INVITE_TO_SQUAD:
                // 受邀者出现的对话框是“好”,“不用了”
                yes = StringHelper.getString(R.string.ui_base_text_ok);
                no = StringHelper.getString(R.string.ui_base_text_no_need);
                break;
            case NimMessage.Type.AGREE_TO_SQUAD:
            case NimMessage.Type.DISAGREE_TO_SQUAD:
                // 新成员同意或不同意邀请，邀请方都只有一个按钮“知道了”
                yes = StringHelper.getString(R.string.ui_base_text_i_known);
                break;
        }
        if (!StringHelper.isEmpty(yes)) {
            SimpleDialogHelper.init(this).show(msg.getMsgContent(), yes, no, new DialogHelper.OnDialogConfirmListener() {
                @Override
                public boolean onConfirm() {
                    switch (msg.getType()) {
                        case NimMessage.Type.JOIN_TO_GROUP:
                            // 通过别人的入群申请
                            joinIntoGroupPassed(msg);
                            break;
                        case NimMessage.Type.INVITE_TO_GROUP:
                            // 通过别人的入群邀请
                            inviteToGroupPassed(msg);
                            break;
                        case NimMessage.Type.INVITE_TO_SQUAD:
                            // 通过别人的邀请加入组织
                            inviteToSquadPassed(msg);
                            break;
                    }
                    return true;
                }
            }, new DialogHelper.OnDialogCancelListener() {
                @Override
                public void onCancel() {
                    switch (msg.getType()) {
                        case NimMessage.Type.JOIN_TO_GROUP:
                            // 拒绝别人的入群申请
                            joinIntoGroupDenied(msg);
                            break;
                        case NimMessage.Type.INVITE_TO_GROUP:
                            // 拒绝别人的入群邀请
                            inviteToGroupDenied(msg);
                            break;
                        case NimMessage.Type.INVITE_TO_SQUAD:
                            // 拒绝别人加入组织的邀请
                            inviteToSquadDenied(msg);
                            break;
                    }
                }
            });
        }
    }

    /**
     * 处理申请入群的审批操作
     */
    private void joinIntoGroupPassed(NimMessage msg) {
        GroupJoinRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<JoinGroup>() {
            @Override
            public void onResponse(JoinGroup joinGroup, boolean success, String message) {
                super.onResponse(joinGroup, success, message);
            }
        }).approveJoin(msg.getUuid(), "");
    }

    /**
     * 处理申请入群的拒绝操作
     */
    private void joinIntoGroupDenied(NimMessage msg) {
        GroupJoinRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<JoinGroup>() {
            @Override
            public void onResponse(JoinGroup joinGroup, boolean success, String message) {
                super.onResponse(joinGroup, success, message);
            }
        }).rejectJoin(msg.getUuid(), "");
    }

    /**
     * 处理受邀入群的审批操作
     */
    private void inviteToGroupPassed(NimMessage msg) {
        InvitationRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<Invitation>() {
            @Override
            public void onResponse(Invitation invitation, boolean success, String message) {
                super.onResponse(invitation, success, message);
            }
        }).agreeInviteToGroup(msg.getUuid(), "");
    }

    /**
     * 处理受邀入群的拒绝操作
     */
    private void inviteToGroupDenied(NimMessage msg) {
        InvitationRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<Invitation>() {
            @Override
            public void onResponse(Invitation invitation, boolean success, String message) {
                super.onResponse(invitation, success, message);
            }
        }).disagreeInviteToGroup(msg.getUuid(), "");
    }

    /**
     * 接受邀请加入小组
     */
    private void inviteToSquadPassed(NimMessage msg) {
        InvitationRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<Invitation>() {
            @Override
            public void onResponse(Invitation invitation, boolean success, String message) {
                super.onResponse(invitation, success, message);
            }
        }).agreeInviteToSquad(msg.getUuid(), "");
    }

    /**
     * 拒绝加入小组
     */
    private void inviteToSquadDenied(NimMessage msg) {
        InvitationRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<Invitation>() {
            @Override
            public void onResponse(Invitation invitation, boolean success, String message) {
                super.onResponse(invitation, success, message);
            }
        }).disagreeInviteToSquad(msg.getUuid(), "");
    }

    private void registerUpgradeListener() {
        //PgyUpdateManager.register(this, "leadcom_provider_file");
    }
}
