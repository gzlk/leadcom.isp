package com.leadcom.android.isp.application;

import android.app.Activity;

import com.leadcom.android.isp.R;
import com.leadcom.android.isp.etc.PressAgainToExit;
import com.leadcom.android.isp.helper.ToastHelper;

import java.util.HashMap;
import java.util.Map;

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
            ToastHelper.make(this).showMsg(text);
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
    }

    /**
     * 直接关闭程序但不关闭后台服务
     */
    public void exitDirectly() {
        clearActivity();
        //CrashHandler.getInstance().clearDebugLog();
    }
}
