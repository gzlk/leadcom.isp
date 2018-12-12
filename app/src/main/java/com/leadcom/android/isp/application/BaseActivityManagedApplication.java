package com.leadcom.android.isp.application;

import android.app.Activity;

import com.leadcom.android.isp.R;
import com.leadcom.android.isp.cache.Cache;
import com.leadcom.android.isp.etc.PressAgainToExit;
import com.leadcom.android.isp.helper.PreferenceHelper;
import com.leadcom.android.isp.helper.StringHelper;
import com.leadcom.android.isp.helper.ToastHelper;
import com.leadcom.android.isp.listener.NotificationChangeHandleCallback;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import me.leolin.shortcutbadger.ShortcutBadger;

/**
 * <b>功能描述：</b><br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/04/19 13:24 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/04/19 13:24 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class BaseActivityManagedApplication extends OrmApplication {

    /**
     * App是否转入后台运行
     */
    protected boolean isAppStayInBackground = false;

    /**
     * 设置app是否转入后台运行
     */
    public void setAppStayInBackground(boolean background) {
        log("app stay in background: " + background);
        isAppStayInBackground = background;
    }

    /**
     * 查看app是否在后台运行
     */
    public boolean isAppStayInBackground() {
        return isAppStayInBackground;
    }

    private Map<String, Activity> activities = new HashMap<>();

    /**
     * 添加Activity到监控列表
     */
    public synchronized void addActivity(Activity activity) {
        String hashCode = Integer.toHexString(activity.hashCode());
        if (!activities.containsKey(hashCode)) {
            activities.put(hashCode, activity);
        }
    }

    private String[] getActivityKeys() {
        int size = activities.size();
        if (size > 0) {
            return activities.keySet().toArray(new String[size]);
        }
        return null;
    }

    /**
     * 移除已经finish的Activity
     */
    public synchronized void removeActivity(String hashCode) {
        if (null != hashCode) {
            if (activities.containsKey(hashCode)) {
                Activity a = activities.remove(hashCode);
                if (null != a && !a.isFinishing()) {
                    a.finish();
                }
            }
        }
    }

    private void clearActivity() {
        String[] keys = getActivityKeys();
        while (null != keys && keys.length > 0) {
            int size = keys.length;
            removeActivity(keys[size - 1]);
            keys = getActivityKeys();
        }
    }

    private static int get(int res, int resBeta) {
        return Cache.isReleasable() ? resBeta : res;
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
        //NIMClient.updateStatusBarNotificationConfig(getNotificationConfig());
    }

    private static int unreadCount = 0;

    /**
     * 获取未读推送消息数量
     */
    public int getUnreadCount() {
        return unreadCount;
    }

    /**
     * 重设未读推送消息数量
     */
    public synchronized void setUnreadCount(int count) {
        unreadCount = count;
        if (unreadCount <= 0) {
            unreadCount = 0;
        }
        resetBadgeNumber();
    }

    protected static void resetBadgeNumber() {
        //int size = NIMClient.getService(MsgService.class).getTotalUnreadCount() + unreadCount;
        ShortcutBadger.applyCount(App.app(), unreadCount);
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

    private PressAgainToExit mPressAgainToExit = new PressAgainToExit();

    /**
     * 再按一次退出程序。
     */
    public void pressAgainExit() {
        if (mPressAgainToExit.isExit()) {
            exitDirectly();
        } else {
            log("press again to exit.");
            String text = getString(R.string.ui_base_text_press_again_to_exit);
            ToastHelper.helper().showMsg(text);
            mPressAgainToExit.doExitInOneSecond();
        }
    }

    /**
     * 退出登录
     */
    public void logout() {
        log("manual exit.");
        // 关闭数据库
        closeOrm();
        exitDirectly();
        // 退出后桌面提醒清零
        ShortcutBadger.applyCount(this, 0);
    }

    /**
     * 直接关闭程序但不关闭后台服务
     */
    public void exitDirectly() {
        clearActivity();
        //CrashHandler.getInstance().clearDebugLog();
    }
}
