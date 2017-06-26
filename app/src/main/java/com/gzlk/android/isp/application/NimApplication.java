package com.gzlk.android.isp.application;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Environment;
import android.text.TextUtils;

import com.gzlk.android.isp.BuildConfig;
import com.gzlk.android.isp.R;
import com.gzlk.android.isp.activity.MainActivity;
import com.gzlk.android.isp.activity.WelcomeActivity;
import com.gzlk.android.isp.cache.Cache;
import com.gzlk.android.isp.crash.system.SysInfoUtil;
import com.gzlk.android.isp.helper.LogHelper;
import com.gzlk.android.isp.helper.NotificationHelper;
import com.gzlk.android.isp.helper.PreferenceHelper;
import com.gzlk.android.isp.helper.StringHelper;
import com.gzlk.android.isp.helper.ToastHelper;
import com.gzlk.android.isp.lib.Json;
import com.gzlk.android.isp.model.Dao;
import com.gzlk.android.isp.nim.model.notification.NimMessage;
import com.gzlk.android.isp.nim.session.NimSessionHelper;
import com.netease.nim.uikit.NimUIKit;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.SDKOptions;
import com.netease.nimlib.sdk.StatusBarNotificationConfig;
import com.netease.nimlib.sdk.StatusCode;
import com.netease.nimlib.sdk.auth.AuthServiceObserver;
import com.netease.nimlib.sdk.auth.LoginInfo;
import com.netease.nimlib.sdk.msg.MsgServiceObserve;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.CustomNotification;
import com.netease.nimlib.sdk.uinfo.UserInfoProvider;

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
     * 初始化网易云
     */
    protected void initializeNim() {
        // SDK初始化（启动后台服务，若已经存在用户登录信息， SDK 将完成自动登录）
        NIMClient.init(this, loginInfo(), options());
        // 注册自定义网易云消息解析器，必须在主进程中。
        if (shouldInit()) {
            // 初始化，使用 uikit 默认的用户信息提供者
            NimUIKit.init(this);

            NimSessionHelper.init();

            handleUserOnlineStatus();

            observeCustomNotification();
        }
    }

    private String getAppKey() {
        if (isForTest) {
            // 我的测试key
            return getString(R.string.netease_nim_app_key_test);
        }
        if (BuildConfig.RELEASEABLE) {
            // release 版的 app key
            return getString(R.string.netease_nim_app_key);
        }
        // beta 版的 app key
        return getString(R.string.netease_nim_app_key_beta);
    }

    // 如果返回值为 null，则全部使用默认参数。
    private SDKOptions options() {
        SDKOptions options = new SDKOptions();
        options.appKey = getAppKey();
        log(format("app key: %s, isForTest: %s, is release: %s", options.appKey, isForTest, BuildConfig.RELEASEABLE));
        // 如果将新消息通知提醒托管给 SDK 完成，需要添加以下配置。否则无需设置。
        StatusBarNotificationConfig config = new StatusBarNotificationConfig();
        config.notificationEntrance = WelcomeActivity.class; // 点击通知栏跳转到该Activity
        config.notificationSmallIconId = R.mipmap.ic_launcher;
        // 呼吸灯配置
        config.ledARGB = Color.GREEN;
        config.ledOnMs = 1000;
        config.ledOffMs = 1500;
        // 通知铃声的uri字符串
        config.notificationSound = "android.resource://com.leadcom.android.isp/raw/msg";
        options.statusBarNotificationConfig = config;

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

        // 用户资料提供者, 目前主要用于提供用户资料，用于新消息通知栏中显示消息来源的头像和昵称
        options.userInfoProvider = new UserInfoProvider() {
            @Override
            public UserInfo getUserInfo(String account) {
                return null;
            }

            @Override
            public int getDefaultIconResId() {
                return R.mipmap.img_default_user_header;
            }

            @Override
            public Bitmap getTeamIcon(String tid) {
                return null;
            }

            @Override
            public Bitmap getAvatarForMessageNotifier(String account) {
                return null;
            }

            @Override
            public String getDisplayNameForMessageNotifier(String account, String sessionId, SessionTypeEnum sessionType) {
                return null;
            }
        };
        return options;
    }

    private int getImageMaxEdge() {
        return getResources().getDisplayMetrics().widthPixels / 2;
    }

    // 如果已经存在用户登录信息，返回LoginInfo，否则返回null即可
    public LoginInfo loginInfo() {
        String account = isForTest ? "xfeiffer" : PreferenceHelper.get(R.string.pf_last_login_user_id, "");
        String token = isForTest ? "111111" : PreferenceHelper.get(R.string.pf_last_login_user_nim_token, "");
        if (!TextUtils.isEmpty(account) && !TextUtils.isEmpty(token)) {
            if (account.equals(Cache.cache().userId)) {
                // 设置当前登录者信息
                NimSessionHelper.setAccount(account);
            }
            return new LoginInfo(account, token);
        } else {
            return null;
        }
    }

    private void handleUserOnlineStatus() {
        NIMClient.getService(AuthServiceObserver.class).observeOnlineStatus(new Observer<StatusCode>() {
            public void onEvent(StatusCode status) {
                log("User status changed to: " + status);
                if (status.wontAutoLogin()) {
                    if (StatusCode.typeOfValue(status.getValue()) == StatusCode.PWD_ERROR) {
                        ToastHelper.make(NimApplication.this).showMsg(R.string.ui_text_nim_pwd_error);
                    } else {
                        // 被踢出、账号被禁用、密码错误等情况，自动登录失败，需要返回到登录界面进行重新登录操作
                        ToastHelper.make(NimApplication.this).showMsg(R.string.ui_text_nim_kick_out);
                    }
                } else if (status.shouldReLogin()) {
                    if (StatusCode.typeOfValue(status.getValue()) == StatusCode.NET_BROKEN) {
                        ToastHelper.make(NimApplication.this).showMsg(R.string.ui_text_nim_net_broken);
                    }
                }
            }
        }, true);
    }

    private void observeCustomNotification() {
        // 如果有自定义通知是作用于全局的，不依赖某个特定的 Activity，那么这段代码应该在 Application 的 onCreate 中就调用
        NIMClient.getService(MsgServiceObserve.class).observeCustomNotification(new Observer<CustomNotification>() {
            @Override
            public void onEvent(CustomNotification message) {
                LogHelper.log("demo", "custom notification: " + message.getContent() + " from :" + message.getSessionId() + "/" + message.getSessionType());
                // 在这里处理自定义通知。
                String json = message.getContent();
                if (!StringHelper.isEmpty(json)) {
                    NimMessage msg = Json.gson().fromJson(json, NimMessage.class);
                    if (null != msg) {
                        new Dao<>(NimMessage.class).save(msg);
                        if (isAppStayInBackground || !SysInfoUtil.isAppOnForeground(NimApplication.this)) {
                            // 如果app已经隐藏到后台，则需要打开通过系统通知来提醒用户
                            Intent extra = new Intent().putExtra(MainActivity.EXTRA_NOTIFICATION, msg);
                            NotificationHelper.helper(NimApplication.this).show("通知", msg.getMsgContent(), extra);
                        }
                    }
                }
            }
        }, true);
    }
}
