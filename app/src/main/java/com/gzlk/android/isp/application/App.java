package com.gzlk.android.isp.application;

import android.app.Activity;

import com.gzlk.android.isp.etc.PressAgainToExit;

import java.util.HashMap;
import java.util.Map;

/**
 * <b>功能描述：</b>Application类<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/04/04 19:21 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/04/04 19:21 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class App extends OrmApplication {

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

    private PressAgainToExit mPressAgainToExit = new PressAgainToExit();

    /**
     * 再按一次退出程序。
     */
    public void pressAgainExit() {
        if (mPressAgainToExit.isExit()) {
            exitDirectly();
        } else {
            //LogHelper.log(TAG, "press again to exit.");
            //String app_name = StringHelper.getString(R.string.app_name_lxbg_default);
            //String text = StringHelper.getString(R.string.press_again_exit, app_name);
            //ToastHelper.showMsg(text);
            mPressAgainToExit.doExitInOneSecond();
        }
    }

    /**
     * 直接关闭程序但不关闭后台服务
     */
    public void exitDirectly() {
        // 关闭数据库
        closeOrm();
        // 清空已登录信息
        //PreferenceHelper.save(R.string.app_login_state, "0");
        // 退出程序关闭数据库连接
        //Utils.clearDBHelpers();
        clearActivity();
        //LogHelper.log(TAG, "manual exit.");
        //CrashHandler.getInstance().clearDebugLog();
    }
}
