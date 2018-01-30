package com.leadcom.android.isp.application;

import android.graphics.Color;
import android.os.Environment;
import android.text.TextUtils;

import com.leadcom.android.isp.R;
import com.leadcom.android.isp.activity.WelcomeActivity;
import com.leadcom.android.isp.cache.Cache;
import com.leadcom.android.isp.helper.LogHelper;
import com.leadcom.android.isp.helper.PreferenceHelper;
import com.leadcom.android.isp.helper.StringHelper;
import com.leadcom.android.isp.helper.ToastHelper;
import com.leadcom.android.isp.lib.Json;
import com.leadcom.android.isp.listener.NotificationChangeHandleCallback;
import com.leadcom.android.isp.listener.OnNimMessageEvent;
import com.leadcom.android.isp.model.activity.topic.AppTopic;
import com.leadcom.android.isp.model.organization.Member;
import com.leadcom.android.isp.nim.model.notification.NimMessage;
import com.leadcom.android.isp.nim.session.NimSessionHelper;
import com.netease.nim.uikit.api.NimUIKit;
import com.netease.nim.uikit.api.UIKitOptions;
import com.netease.nim.uikit.api.wrapper.NimUserInfoProvider;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.SDKOptions;
import com.netease.nimlib.sdk.StatusBarNotificationConfig;
import com.netease.nimlib.sdk.StatusCode;
import com.netease.nimlib.sdk.auth.AuthServiceObserver;
import com.netease.nimlib.sdk.auth.LoginInfo;
import com.netease.nimlib.sdk.mixpush.MixPushConfig;
import com.netease.nimlib.sdk.mixpush.NIMPushClient;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.MsgServiceObserve;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.CustomNotification;
import com.netease.nimlib.sdk.msg.model.RecentContact;

import java.util.ArrayList;
import java.util.List;

import me.leolin.shortcutbadger.ShortcutBadger;

/**
 * <b>功能描述：</b>提供网易云接口<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/05/10 19:43 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/05/10 19:43 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class NimApplication extends BaseActivityManagedApplication {

    // 是否用测试的 app key
    private static boolean isForTest = false;

    /**
     * 小米系列
     */
    private static final String XIAOMI = "xiaomi";
    /**
     * 华为系列
     */
    private static final String HUAWEI = "huawei";

    /**
     * 是否是小米手机
     */
    private boolean isXiaomiDevice() {
        return manufacturer().equals(XIAOMI);
    }

    /**
     * 是否是华为手机
     */
    private boolean isHuaweiDevice() {
        return manufacturer().equals(HUAWEI);
    }

    /**
     * 初始化网易云
     */
    protected void initializeNim() {
        // 判断是否为小米手机
//        if (isXiaomiDevice()) {
//            // 此处 certificate 请传入为开发者配置好的小米证书名称
//            NIMPushClient.registerMiPush(this, "leadcomMiPush", "2882303761517709778", "5141770974778");
//        }
//        // 判断是否为华为手机
//        if (isHuaweiDevice()) {
//
//        }
        // SDK初始化（启动后台服务，若已经存在用户登录信息， SDK 将完成自动登录）
        NIMClient.init(this, loginInfo(), options());
        // 注册自定义网易云消息解析器，必须在主进程中。
        if (shouldInit()) {
            // 初始化，使用 uikit 默认的用户信息提供者
            NimUIKit.init(this, buildUIKitOptions());

            NimSessionHelper.init();

            handleUserOnlineStatus();

            observeCustomNotification();

            observerRecentContact(true);
        }
    }

    private UIKitOptions buildUIKitOptions() {
        UIKitOptions options = new UIKitOptions();
        // 设置app图片/音频/日志等缓存目录
        options.appCacheDir = Environment.getExternalStorageDirectory() + "/" + ROOT_DIR + "/nim";//NimSDKOptionConfig.getAppCacheDir(this) + "/app";
        return options;
    }

    @Override
    public void logout() {
        //  注册/注销观察者
        observerRecentContact(false);
        // 退出后桌面提醒清零
        ShortcutBadger.applyCount(this, 0);
        super.logout();
    }

    private String getAppKey() {
        if (isForTest) {
            // 我的测试key
            return getString(R.string.netease_nim_app_key_test);
        }
        if (Cache.isReleasable()) {
            // release 版的 app key
            return getString(R.string.netease_nim_app_key_release);
        }
        // beta 版的 app key
        return getString(R.string.netease_nim_app_key_alpha);
    }

    private static StatusBarNotificationConfig getNotificationConfig() {
        // 如果将新消息通知提醒托管给 SDK 完成，需要添加以下配置。否则无需设置。
        StatusBarNotificationConfig config = new StatusBarNotificationConfig();
        config.notificationEntrance = WelcomeActivity.class; // 点击通知栏跳转到该Activity
        config.notificationSmallIconId = R.mipmap.ic_launcher;
        // 呼吸灯配置
        config.ledARGB = Color.GREEN;
        config.ledOnMs = 1000;
        config.ledOffMs = 1500;
        // 是否需要震动
        config.vibrate = nimVibrate;
        // 是否需要响铃
        config.ring = nimSound;
        // 通知铃声的uri字符串
        config.notificationSound = "android.resource://com.leadcom.android.isp/raw/msg";
        return config;
    }

    // 如果返回值为 null，则全部使用默认参数。
    private SDKOptions options() {
        SDKOptions options = new SDKOptions();
        options.appKey = getAppKey();
        log(format("app key: %s, isForTest: %s, is release: %s", options.appKey, isForTest, Cache.isReleasable()));

        options.statusBarNotificationConfig = getNotificationConfig();

        // 配置数据库加密秘钥
        options.databaseEncryptKey = "NETEASE";
        // 配置保存图片，文件，log 等数据的目录
        // 如果 options 中没有设置这个值，SDK 会使用下面代码示例中的位置作为 SDK 的数据目录。
        // 该目录目前包含 log, file, image, audio, video, thumb 这6个目录。
        // 如果第三方 APP 需要缓存清理功能， 清理这个目录下面个子目录的内容即可。
        options.sdkStorageRootPath = Environment.getExternalStorageDirectory() + "/" + ROOT_DIR + "/nim";

        // 配置是否需要预下载附件缩略图，默认为 true
        options.preloadAttach = true;

        // 配置附件缩略图的尺寸大小。表示向服务器请求缩略图文件的大小
        // 该值一般应根据屏幕尺寸来确定， 默认值为 Screen.width / 2
        options.thumbnailSize = getImageMaxEdge();

        // 设置同步已读回执到各端
        options.sessionReadAck = true;

        // 动图的缩略图直接下载原图
        options.animatedImageThumbnailEnabled = true;

        // 采用异步加载SDK
        options.asyncInitSDK = true;

        // 是否是弱IM场景
        options.reducedIM = false;

        // 是否检查manifest 配置，调试阶段打开，调试通过之后请关掉
        options.checkManifestConfig = !Cache.isReleasable();

        // 配置第三方推送
        options.mixPushConfig = buildMixPushConfig();

        // 用户资料提供者, 目前主要用于提供用户资料，用于新消息通知栏中显示消息来源的头像和昵称
        options.userInfoProvider = new NimUserInfoProvider(this);
        return options;
    }

    private static MixPushConfig buildMixPushConfig() {

        // 第三方推送配置
        MixPushConfig config = new MixPushConfig();

        // 小米推送
        config.xmAppId = "2882303761517709778";
        config.xmAppKey = "5141770974778";
        config.xmCertificateName = "leadcomMiPush";

        // 华为推送
        config.hwCertificateName = "leadcomHwPush";

        // 魅族推送
        //config.mzAppId = "111710";
        //config.mzAppKey = "282bdd3a37ec4f898f47c5bbbf9d2369";
        //config.mzCertificateName = "DEMO_MZ_PUSH";

        // fcm 推送，适用于海外用户
        //config.fcmCertificateName = "DEMO_FCM_PUSH";

        return config;
    }

    private int getImageMaxEdge() {
        return getResources().getDisplayMetrics().widthPixels / 2;
    }

    private static int get(int res, int resBeta) {
        return Cache.isReleasable() ? resBeta : res;
    }

    // 如果已经存在用户登录信息，返回LoginInfo，否则返回null即可
    public LoginInfo loginInfo() {
        String account = isForTest ? "xfeiffer" : PreferenceHelper.get(get(R.string.pf_last_login_user_id, R.string.pf_last_login_user_id_beta), "");
        String token = isForTest ? "111111" : PreferenceHelper.get(get(R.string.pf_last_login_user_nim_token, R.string.pf_last_login_user_nim_token_beta), "");
        if (!TextUtils.isEmpty(account) && !TextUtils.isEmpty(token)) {
            if (account.equals(Cache.cache().userId)) {
                // 设置当前登录者信息
                NimSessionHelper.setAccount(account);
            }
            readNimMessageNotify(account);
            return new LoginInfo(account, token);
        } else {
            return null;
        }
    }

    /**
     * Nim 声音、震动开关
     */
    public static boolean nimSound = false, nimVibrate = false;

    /**
     * 读取本地设置的消息通知方式
     */
    public static void readNimMessageNotify(String account) {
        if (isEmpty(account)) {
            nimSound = true;
            nimVibrate = true;
            return;
        }
        String sound = PreferenceHelper.get(StringHelper.getString(get(R.string.pf_last_login_user_sound, R.string.pf_last_login_user_sound_beta), account), "");
        String vibrate = PreferenceHelper.get(StringHelper.getString(get(R.string.pf_last_login_user_vibrate, R.string.pf_last_login_user_vibrate_beta), account), "");
        nimSound = isEmpty(sound) || sound.equals("1");
        nimVibrate = isEmpty(vibrate) || vibrate.equals("1");
    }

    /**
     * 重置 Nim 消息通知方式
     */
    public static void resetNimMessageNotify(boolean sound, boolean vibrate) {
        nimSound = sound;
        nimVibrate = vibrate;
        PreferenceHelper.save(StringHelper.getString(get(R.string.pf_last_login_user_sound, R.string.pf_last_login_user_sound_beta), Cache.cache().userId), (sound ? "1" : "0"));
        PreferenceHelper.save(StringHelper.getString(get(R.string.pf_last_login_user_vibrate, R.string.pf_last_login_user_vibrate_beta), Cache.cache().userId), (vibrate ? "1" : "0"));
        NIMClient.updateStatusBarNotificationConfig(getNotificationConfig());
    }

    private void handleUserOnlineStatus() {
        NIMClient.getService(AuthServiceObserver.class).observeOnlineStatus(new Observer<StatusCode>() {
            public void onEvent(StatusCode status) {
                log("User status changed to: " + status);
                if (status.wontAutoLogin()) {
                    if (StatusCode.typeOfValue(status.getValue()) == StatusCode.PWD_ERROR) {
                        ToastHelper.make(NimApplication.this).showMsg(R.string.ui_text_nim_pwd_error);
                    } else if (StatusCode.typeOfValue(status.getValue()) == StatusCode.FORBIDDEN) {
                        ToastHelper.make(NimApplication.this).showMsg(R.string.ui_text_nim_forbidden);
                    } else {
                        // 被踢出、账号被禁用、密码错误等情况，自动登录失败，需要返回到登录界面进行重新登录操作
                        ToastHelper.make(NimApplication.this).showMsg(R.string.ui_text_nim_kick_out);
                    }
                } else if (status.shouldReLogin()) {
                    if (StatusCode.typeOfValue(status.getValue()) == StatusCode.NET_BROKEN) {
                        ToastHelper.make(NimApplication.this).showMsg(R.string.ui_text_nim_net_broken);
                    }
                }
                if (StatusCode.typeOfValue(status.getValue()) == StatusCode.LOGINED) {
                    observerRecentContact(true);
                }
            }
        }, true);
    }

    private void observeCustomNotification() {
        // 如果有自定义通知是作用于全局的，不依赖某个特定的 Activity，那么这段代码应该在 Application 的 onCreate 中就调用
        NIMClient.getService(MsgServiceObserve.class).observeCustomNotification(new Observer<CustomNotification>() {
            @Override
            public void onEvent(CustomNotification message) {
                LogHelper.log("NimApp", "notification: " + message.getContent() + " from :" + message.getSessionId() + "/" + message.getSessionType());
                // 在这里处理自定义通知。
                String json = message.getContent();
                if (!StringHelper.isEmpty(json)) {
                    NimMessage msg = Json.gson().fromJson(json, NimMessage.class);
                    if (null != msg) {
                        if (isEmpty(msg.getId())) {
                            msg.setId(msg.getUuid());
                        }
                        switch (msg.getMsgType()) {
                            case NimMessage.Type.ACTIVITY_END:
                            case NimMessage.Type.ACTIVITY_EXIT:
                            case NimMessage.Type.ACTIVITY_KICK_OUT:
                                // 活动结束、退出活动、被踢出活动时，清理活动中的未读消息条数
                                clearUnreadCount(msg.getTid());
                                if (msg.getMsgType() == NimMessage.Type.ACTIVITY_END && null != msg.getSubTidList()) {
                                    for (String tid : msg.getSubTidList()) {
                                        clearUnreadCount(tid);
                                    }
                                }
                                break;
                            case NimMessage.Type.TOPIC_END:
                            case NimMessage.Type.TOPIC_EXIT:
                            case NimMessage.Type.TOPIC_KICK_OUT:
                                // 议题结束、退出议题、被踢出议题时，清理议题中的未读消息条数
                                if (!isEmpty(msg.getTid())) {
                                    clearUnreadCount(msg.getTid());
                                    // 删除本地议题成员
                                    AppTopic topic = AppTopic.queryByTid(msg.getTid());
                                    if (null != topic) {
                                        Member.removeMemberOfTopicId(topic.getId());
                                    }
                                }
                                break;
                        }
                        NimMessage.save(msg);
                        if (msg.isSavable()) {
//                            if (isAppStayInBackground || !SysInfoUtil.isAppOnForeground(NimApplication.this)) {
//                                // 如果app已经隐藏到后台，则需要打开通过系统通知来提醒用户
//                                Intent extra = new Intent().putExtra(MainActivity.EXTRA_NOTIFICATION, msg);
//                                NotificationHelper.helper(NimApplication.this).show(getString(R.string.ui_nim_action_notice), msg.getMsgContent(), extra);
//                            }
                            dispatchCallbacks();
                        }
                        dispatchEvents(msg);
//                        if (isAppStayInBackground || !SysInfoUtil.isAppOnForeground(NimApplication.this)) {
//                            // 如果app已经隐藏到后台，则需要打开通过系统通知来提醒用户
//                            Intent extra = new Intent().putExtra(MainActivity.EXTRA_NOTIFICATION, msg);
//                            NotificationHelper.helper(NimApplication.this).show(getString(R.string.ui_nim_action_notice), msg.getMsgContent(), extra);
//                        } else {
//                            // 转到MainActivity处理消息
//                            if (null != messageEvent) {
//                                messageEvent.onMessageEvent(msg);
//                            }
//                        }
//                        dispatchCallbacks();
                    }
                }
            }
        }, true);
    }

    private void clearUnreadCount(String tid) {
        if (!isEmpty(tid)) {
            NIMClient.getService(MsgService.class).clearUnreadCount(tid, SessionTypeEnum.Team);
        }
    }

    public void observerRecentContact(boolean setting) {
        //  注册/注销观察者
        NIMClient.getService(MsgServiceObserve.class).observeRecentContact(messageObserver, setting);
        if (setting) {
            resetBadgeNumber();
        }
    }

    //  创建观察者对象
    private Observer<List<RecentContact>> messageObserver = new Observer<List<RecentContact>>() {
        @Override
        public void onEvent(List<RecentContact> contacts) {
            // 桌面未读消息数字提醒
            resetBadgeNumber();
        }
    };

    private static ArrayList<OnNimMessageEvent> messageEvents = new ArrayList<>();

    /**
     * 增加推送消息处理回调
     */
    public static void addNimMessageEvent(OnNimMessageEvent event) {
        if (!messageEvents.contains(event)) {
            messageEvents.add(event);
        }
    }

    /**
     * 删除推送消息处理回调
     */
    public static void removeNimMessageEvent(OnNimMessageEvent event) {
        messageEvents.remove(event);
    }

    /**
     * 按照列表挨个通知回调处理消息到达事件
     */
    private static void dispatchEvents(NimMessage message) {
        for (OnNimMessageEvent event : messageEvents) {
            event.onMessageEvent(message);
        }
    }

    private static ArrayList<NotificationChangeHandleCallback> callbacks = new ArrayList<>();

    public static void addNotificationChangeCallback(NotificationChangeHandleCallback callback) {
        if (!callbacks.contains(callback)) {
            callbacks.add(callback);
        }
    }

    public static void removeNotificationChangeCallback(NotificationChangeHandleCallback callback) {
        callbacks.remove(callback);
    }

    public static void dispatchCallbacks() {
        for (int i = callbacks.size() - 1; i >= 0; i--) {
            callbacks.get(i).onChanged();
        }
        resetBadgeNumber();
    }

    private static void resetBadgeNumber() {
        int size = NimMessage.getUnRead();
        size += NIMClient.getService(MsgService.class).getTotalUnreadCount();
        ShortcutBadger.applyCount(App.app(), size);
    }
}
