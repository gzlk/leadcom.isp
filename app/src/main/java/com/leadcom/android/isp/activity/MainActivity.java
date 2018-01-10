package com.leadcom.android.isp.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.TextView;

import com.leadcom.android.isp.BuildConfig;
import com.leadcom.android.isp.R;
import com.leadcom.android.isp.api.common.UpdateRequest;
import com.leadcom.android.isp.api.listener.OnSingleRequestListener;
import com.leadcom.android.isp.api.org.InvitationRequest;
import com.leadcom.android.isp.application.App;
import com.leadcom.android.isp.application.NimApplication;
import com.leadcom.android.isp.etc.Utils;
import com.leadcom.android.isp.fragment.activity.ActivityEntranceFragment;
import com.leadcom.android.isp.fragment.main.MainFragment;
import com.leadcom.android.isp.fragment.organization.OrganizationPropertiesFragment;
import com.leadcom.android.isp.helper.DialogHelper;
import com.leadcom.android.isp.helper.SimpleDialogHelper;
import com.leadcom.android.isp.helper.StringHelper;
import com.leadcom.android.isp.helper.ToastHelper;
import com.leadcom.android.isp.listener.OnNimMessageEvent;
import com.leadcom.android.isp.model.activity.Activity;
import com.leadcom.android.isp.model.common.Message;
import com.leadcom.android.isp.model.common.SystemUpdate;
import com.leadcom.android.isp.model.organization.Invitation;
import com.leadcom.android.isp.nim.file.FilePreviewHelper;
import com.leadcom.android.isp.nim.model.notification.NimMessage;
import com.leadcom.android.isp.nim.session.NimSessionHelper;
import com.leadcom.android.isp.service.DownloadingService;
import com.netease.nim.uikit.NimUIKit;
import com.netease.nimlib.sdk.NimIntent;
import com.netease.nimlib.sdk.msg.attachment.MsgAttachment;
import com.netease.nimlib.sdk.msg.constant.MsgTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;

import me.zhanghai.android.materialprogressbar.MaterialProgressBar;

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

    /**
     * app内部打开
     */
    public static boolean innerOpen = true;

    public static void start(Context context) {
        start(context, 0);
    }

    public static void start(Context context, int selectedIndex) {
        start(context, new Intent().putExtra(MainFragment.PARAM_SELECTED, selectedIndex));
    }

    public static void start(Context context, Intent extras) {
        innerOpen = true;
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
    private static String downloadingUrl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        supportPressAgainToExit = true;
        supportTransparentStatusBar = true;
        isToolbarSupported = false;
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            // 从堆栈恢复时，设置一个新的空白intent，不再重复解析之前的intent
            setIntent(new Intent());
        }
        // 接收消息
        //NIMClient.getService(MsgServiceObserve.class).observeReceiveMessage(incomingMessageObserver, true);
        // 接收自定义通知
        NimApplication.addNimMessageEvent(nimMessageEvent);
        //NIMClient.getService(MsgServiceObserve.class).observeCustomNotification(customNotificationObserver, true);

        if (null == mainFragment) {
            mainFragment = new MainFragment();
            checkClientVersion();
        }
        setMainFrameLayout(mainFragment);
        parseIntent();
        //registerUpgradeListener();
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void onStart() {
        super.onStart();
        App.app().setAppStayInBackground(false);
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    protected void onStop() {
        App.app().setAppStayInBackground(true);
        super.onStop();
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
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        downloadingUrl = "";
        innerOpen = false;
        NimApplication.removeNimMessageEvent(nimMessageEvent);
        //NIMClient.getService(MsgServiceObserve.class).observeCustomNotification(customNotificationObserver, false);
        //NIMClient.getService(MsgServiceObserve.class).observeReceiveMessage(incomingMessageObserver, false);
        //PgyUpdateManager.unregister();
        super.onDestroy();
    }

    /**
     * 检测服务器上的最新客户端版本并提示用户更新
     */
    private void checkClientVersion() {
        UpdateRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<SystemUpdate>() {
            @Override
            public void onResponse(SystemUpdate systemUpdate, boolean success, String message) {
                super.onResponse(systemUpdate, success, message);
                if (success) {
                    String ver = systemUpdate.getVersion();
                    //warningUpdatable("http://file.ws.126.net/3g/client/netease_newsreader_android.apk");
                    if (!StringHelper.isEmpty(ver) && ver.compareTo(BuildConfig.VERSION_NAME) > 0) {
                        String url = systemUpdate.getResourceURI();
                        if (StringHelper.isEmpty(url) || !Utils.isUrl(url)) {
                            SimpleDialogHelper.init(MainActivity.this).show(R.string.ui_system_updatable_url_invalid);
                        } else {
                            warningUpdatable(url);
                        }
                    }
                }
            }
        }).getClientVersion();
    }

    private void warningUpdatable(String url) {
        downloadingUrl = url;
        String text = StringHelper.getString(R.string.ui_system_updatable, StringHelper.getString(R.string.app_name_default));
        SimpleDialogHelper.init(this).show(text, R.string.ui_base_text_ok, R.string.ui_base_text_cancel, new DialogHelper.OnDialogConfirmListener() {
            @Override
            public boolean onConfirm() {
                // 打开下载对话框，并开始下载（下载对话框可以隐藏）
                showUpgradeDownloadingDialog();
                return true;
            }
        }, null);
    }

    private View upgradeDialogView;
    private MaterialProgressBar upgradeProgress;
    private TextView upgradePercentage, upgradePercentageSize;
    private DialogHelper upgradeDialog;

    private void showUpgradeDownloadingDialog() {
        upgradeDialog = DialogHelper.init(this).addOnDialogInitializeListener(new DialogHelper.OnDialogInitializeListener() {
            @Override
            public View onInitializeView() {
                if (null == upgradeDialogView) {
                    upgradeDialogView = View.inflate(MainActivity.this, R.layout.popup_dialog_upgrade, null);
                    upgradeProgress = upgradeDialogView.findViewById(R.id.ui_popup_upgrade_progressbar);
                    upgradePercentage = upgradeDialogView.findViewById(R.id.ui_popup_upgrade_percentage);
                    upgradePercentageSize = upgradeDialogView.findViewById(R.id.ui_popup_upgrade_percentage_size);
                }
                return upgradeDialogView;
            }

            @Override
            public void onBindData(View dialogView, DialogHelper helper) {
                upgradeProgress.setProgress(0);
                upgradePercentage.setText("0%");
                upgradePercentageSize.setText("");
            }
        }).addOnDialogConfirmListener(new DialogHelper.OnDialogConfirmListener() {
            @Override
            public boolean onConfirm() {
                DownloadingService.background(MainActivity.this);
                return true;
            }
        }).setConfirmText(R.string.ui_system_updating_background).setPopupType(DialogHelper.SLID_IN_BOTTOM);
        upgradeDialog.show();
        // 注册下载进度监听
        DownloadingService.setOnProgressListener(progressListener);
        DownloadingService.start(this, downloadingUrl);
    }

    private DownloadingService.OnProgressListener progressListener = new DownloadingService.OnProgressListener() {
        @Override
        public void onStart() {
            upgradeProgress.setProgress(0);
            upgradePercentage.setText("0%");
            upgradePercentageSize.setText("");
        }

        @Override
        public void onProgressing(int current, int total) {
            if (null != upgradePercentage) {
                if (upgradeProgress.getMax() != total) {
                    upgradeProgress.setMax(total);
                }
                upgradeProgress.setProgress(current);
                upgradePercentage.setText(StringHelper.format("%d%%", (int) (current * 1.0 / total * 100)));
                upgradePercentageSize.setText(getString(R.string.ui_system_updating_percentage_size, Utils.formatSize(current), Utils.formatSize(total)));
            }
        }

        @Override
        public void onSuccess(String path) {
            FilePreviewHelper.previewFile(MainActivity.this, path, "new_version", "apk");
        }

        @Override
        public void onFailure() {
            // 关闭下载对话框
            if (null != upgradeDialog) {
                upgradeDialog.dismiss();
            }
            // 提示下载失败，需要重试或者放弃
            warningDownloadFailure();
        }

        @Override
        public void onStop() {
            if (null != upgradeDialog) {
                upgradeDialog.dismiss();
            }
        }
    };

    private void warningDownloadFailure() {
        SimpleDialogHelper.init(this).show(R.string.ui_system_updating_failure, R.string.ui_base_text_retry, R.string.ui_base_text_abandon, new DialogHelper.OnDialogConfirmListener() {
            @Override
            public boolean onConfirm() {
                upgradeDialogView.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        showUpgradeDownloadingDialog();
                    }
                }, 300);
                return true;
            }
        }, null);
    }

    private OnNimMessageEvent nimMessageEvent = new OnNimMessageEvent() {
        @Override
        public void onMessageEvent(NimMessage message) {
            if (message.isSavable()) {
                handleNimMessageDetails(MainActivity.this, message);
            }
        }
    };

    // 自定义系统通知类
//    Observer<CustomNotification> customNotificationObserver = new Observer<CustomNotification>() {
//        @SuppressWarnings("ConstantConditions")
//        @Override
//        public void onEvent(CustomNotification message) {
//            // 在这里处理自定义通知。
//            String json = message.getContent();
//            if (!StringHelper.isEmpty(json)) {
//                NimMessage msg = Json.gson().fromJson(json, NimMessage.class);
//                if (null != msg) {
//                    if (!App.app().isAppStayInBackground()) {
//                        handleNimMessageDetails(MainActivity.this, msg);
//                    }
//                }
//            }
//        }
//    };

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
                    MsgAttachment attachment = message.getAttachment();
                    if (attachment instanceof NimMessage) {
                        NimMessage nim = (NimMessage) attachment;
                        handleNimMessageDetails(this, nim);
                    }
                }
            } else if (intent.hasExtra(EXTRA_NOTIFICATION)) {
                // 自定义系统通知
                NimMessage msg = (NimMessage) intent.getSerializableExtra(EXTRA_NOTIFICATION);
                handleNimMessageDetails(this, msg);
            }
        }
    }

    public static void handleNimMessageDetails(final AppCompatActivity activity, final NimMessage msg) {
        String yes = "", no = "";
        switch (msg.getMsgType()) {
            case NimMessage.Type.GROUP_JOIN:
                yes = StringHelper.getString(R.string.ui_base_text_ok);
                no = StringHelper.getString(R.string.ui_base_text_reject);
                break;
            case NimMessage.Type.GROUP_JOIN_APPROVE:
                // 组织管理者同意，申请方只有一个按钮“知道了”
                yes = StringHelper.getString(R.string.ui_base_text_i_known);
                break;
            case NimMessage.Type.GROUP_JOIN_DISAPPROVE:
                // 组织管理者不同意，申请方都只有一个按钮“好吧”
                yes = StringHelper.getString(R.string.ui_base_text_ok_ba);
                break;
            case NimMessage.Type.GROUP_INVITE:
                // 受邀者出现的对话框是“好”,“不用了”
                if (msg.isHandled()) {
                    openActivity(activity, OrganizationPropertiesFragment.class.getName(), msg.getGroupId(), false, false, true);
                } else {
                    yes = StringHelper.getString(R.string.ui_base_text_ok);
                    no = StringHelper.getString(R.string.ui_base_text_no_need);
                }
                break;
            case NimMessage.Type.GROUP_INVITE_AGREE:
            case NimMessage.Type.GROUP_INVITE_DISAGREE:
                // 新成员同意或不同意邀请，邀请方都只有一个按钮“知道了”
                yes = StringHelper.getString(R.string.ui_base_text_i_known);
                break;
            case NimMessage.Type.SQUAD_INVITE:
                // 受邀者出现的对话框是“好”,“不用了”
                yes = StringHelper.getString(R.string.ui_base_text_ok);
                no = StringHelper.getString(R.string.ui_base_text_no_need);
                break;
            case NimMessage.Type.SQUAD_INVITE_AGREE:
            case NimMessage.Type.SQUAD_INVITE_DISAGREE:
                // 新成员同意或不同意邀请，邀请方都只有一个按钮“知道了”
                yes = StringHelper.getString(R.string.ui_base_text_i_known);
                break;
            case NimMessage.Type.ACTIVITY_INVITE:
                if (msg.isRead()) {
                    if (msg.isHandled()) {
                        // 直接打开活动群聊页面
                        NimUIKit.startTeamSession(activity, msg.getTid());
                    } else {
                        // 消息已处理过且属于暂不参加则打开加入活动页面
                        openActivity(activity, ActivityEntranceFragment.class.getName(), StringHelper.format(",%s,%s", msg.getTid(), msg.getId()), true, false);
                    }
                } else {
                    // 活动邀请，下一步打开未处理活动页面
                    yes = StringHelper.getString(R.string.ui_base_text_have_a_look);
                    no = StringHelper.getString(R.string.ui_base_text_i_known);
                }
                break;
            case NimMessage.Type.ACTIVITY_ALERT_SELECTED:
                // 系统通知，只提醒就可以了
                yes = StringHelper.getString(R.string.ui_base_text_i_known);
                break;
            case NimMessage.Type.SQUAD_INVITE_ALERT:
//                if (msg.isHandled()) {
//                    if (msg.isHandleState()) {
//                        // 直接打开小组成员
//                        //openActivity(activity, ContactFragment.class.getName(),
//                        //        StringHelper.format("%d,,%s", ContactFragment.TYPE_SQUAD, msg.getGroupId()), true, false);
//                    }
//                } else {
                // 提醒加入小组
                yes = StringHelper.getString(R.string.ui_base_text_i_known);
//                }
                break;
            case NimMessage.Type.TOPIC_INVITE:
                if (msg.isHandled()) {
                    NimUIKit.startTeamSession(activity, msg.getTid());
                } else {
                    yes = StringHelper.getString(R.string.ui_base_text_have_a_look);
                    no = StringHelper.getString(R.string.ui_base_text_i_known);
                }
                break;
            default:
                yes = StringHelper.getString(R.string.ui_base_text_i_known);
                break;
        }
        if (!StringHelper.isEmpty(yes)) {
            SimpleDialogHelper.init(activity).show(msg.getMsgContent(), yes, no, new DialogHelper.OnDialogConfirmListener() {
                @Override
                public boolean onConfirm() {
                    switch (msg.getMsgType()) {
                        case NimMessage.Type.GROUP_JOIN:
                            // 通过别人的入群申请
                            joinIntoGroupPassed(msg);
                            break;
                        case NimMessage.Type.GROUP_INVITE:
                            // 通过别人的入群邀请
                            inviteToGroupPassed(msg);
                            break;
                        case NimMessage.Type.SQUAD_INVITE:
                            // 通过别人的邀请加入组织
                            inviteToSquadPassed(msg);
                            break;
                        case NimMessage.Type.ACTIVITY_INVITE:
                            if (msg.isHandled()) {
                                // 如果消息已经处理过了，则直接打开群聊页面
                                NimUIKit.startTeamSession(activity, msg.getTid());
                            } else {
                                // 消息没有处理过则打开加入活动页面
                                openActivity(activity, ActivityEntranceFragment.class.getName(), StringHelper.format(",%s,%s", msg.getTid(), msg.getId()), true, false);
                            }
                            break;
                        case NimMessage.Type.ACTIVITY_ALERT_SELECTED:
                            // 系统通知的话，点击按钮设置已读标记
                            saveMessage(msg, true, true);
                            break;
                        case NimMessage.Type.SQUAD_INVITE_ALERT:
                            saveMessage(msg, true, true);
                            //openActivity(activity, ContactFragment.class.getName(),
                            //        StringHelper.format("%d,,%s", ContactFragment.TYPE_SQUAD, msg.getGroupId()), true, false);
                            break;
                        case NimMessage.Type.TOPIC_INVITE:
                            saveMessage(msg, true, true);
                            NimUIKit.startTeamSession(activity, msg.getTid());
                            break;
                        default:
                            saveMessage(msg, true, true);
                            break;
                    }
                    return true;
                }
            }, new DialogHelper.OnDialogCancelListener() {
                @Override
                public void onCancel() {
                    switch (msg.getMsgType()) {
                        case NimMessage.Type.GROUP_JOIN:
                            // 拒绝别人的入群申请
                            joinIntoGroupDenied(msg);
                            break;
                        case NimMessage.Type.GROUP_INVITE:
                            // 拒绝别人的入群邀请
                            inviteToGroupDenied(msg);
                            break;
                        case NimMessage.Type.SQUAD_INVITE:
                            // 拒绝别人加入组织的邀请
                            inviteToSquadDenied(msg);
                            break;
                        case NimMessage.Type.TOPIC_INVITE:
                            saveMessage(msg, true, true);
                            break;
                    }
                }
            });
        }
    }

    private static void saveMessage(NimMessage msg, boolean handled, boolean state) {
        // 已处理
        msg.setStatus(Message.Status.HANDLED);
        NimMessage.save(msg);
        NimMessage.resetStatus(msg.getTid());
        NimApplication.dispatchCallbacks();
    }

    /**
     * 处理申请入群的审批操作
     */
    private static void joinIntoGroupPassed(final NimMessage msg) {
        throw new IllegalArgumentException("Cannot support join into group now.");
//        GroupJoinRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<JoinGroup>() {
//            @Override
//            public void onResponse(JoinGroup joinGroup, boolean success, String message) {
//                super.onResponse(joinGroup, success, message);
//                if (success) {
//                    // 处理成功之后保存当前消息状态
//                    saveMessage(msg, true, true);
//                }
//            }
//        }).approveJoin(msg.getUuid(), "");
    }

    /**
     * 处理申请入群的拒绝操作
     */
    private static void joinIntoGroupDenied(final NimMessage msg) {
        throw new IllegalArgumentException("Cannot support join into group now.");
//        GroupJoinRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<JoinGroup>() {
//            @Override
//            public void onResponse(JoinGroup joinGroup, boolean success, String message) {
//                super.onResponse(joinGroup, success, message);
//                if (success) {
//                    // 处理成功之后保存当前消息状态
//                    saveMessage(msg, true, false);
//                }
//            }
//        }).rejectJoin(msg.getUuid(), "");
    }

    /**
     * 处理受邀入群的审批操作
     */
    private static void inviteToGroupPassed(final NimMessage msg) {
        InvitationRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<Invitation>() {
            @Override
            public void onResponse(Invitation invitation, boolean success, String message) {
                super.onResponse(invitation, success, message);
                if (success) {
                    // 处理成功之后保存当前消息状态
                    saveMessage(msg, true, true);
                }
            }
        }).agreeInviteToGroup(msg.getUuid(), "");
    }

    /**
     * 处理受邀入群的拒绝操作
     */
    private static void inviteToGroupDenied(final NimMessage msg) {
        InvitationRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<Invitation>() {
            @Override
            public void onResponse(Invitation invitation, boolean success, String message) {
                super.onResponse(invitation, success, message);
                if (success) {
                    // 处理成功之后保存当前消息状态
                    saveMessage(msg, true, false);
                }
            }
        }).disagreeInviteToGroup(msg.getUuid(), "");
    }

    /**
     * 接受邀请加入小组
     */
    private static void inviteToSquadPassed(final NimMessage msg) {
        InvitationRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<Invitation>() {
            @Override
            public void onResponse(Invitation invitation, boolean success, String message) {
                super.onResponse(invitation, success, message);
                if (success) {
                    // 处理成功之后保存当前消息状态
                    saveMessage(msg, true, true);
                }
            }
        }).agreeInviteToSquad(msg.getUuid(), "");
    }

    /**
     * 拒绝加入小组
     */
    private static void inviteToSquadDenied(final NimMessage msg) {
        InvitationRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<Invitation>() {
            @Override
            public void onResponse(Invitation invitation, boolean success, String message) {
                super.onResponse(invitation, success, message);
                if (success) {
                    // 处理成功之后保存当前消息状态
                    saveMessage(msg, true, false);
                }
            }
        }).disagreeInviteToSquad(msg.getUuid(), "");
    }

    private void registerUpgradeListener() {
        //PgyUpdateManager.register(this, "leadcom_provider_file");
    }
}
