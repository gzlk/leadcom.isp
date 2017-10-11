package com.gzlk.android.isp.cache;

import com.gzlk.android.isp.BuildConfig;
import com.gzlk.android.isp.R;
import com.gzlk.android.isp.application.App;
import com.gzlk.android.isp.etc.Utils;
import com.gzlk.android.isp.helper.LogHelper;
import com.gzlk.android.isp.helper.PreferenceHelper;
import com.gzlk.android.isp.helper.StringHelper;
import com.gzlk.android.isp.model.user.User;
import com.gzlk.android.isp.nim.session.NimSessionHelper;

import java.util.ArrayList;
import java.util.Date;

/**
 * <b>功能描述：</b>全局缓存<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/05/10 20:32 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/05/10 20:32 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class Cache {

    private static Cache cache;

    /**
     * 获取当前全局实例缓存
     */
    public static Cache cache() {
        if (null == cache) {
            cache = new Cache();
        }
        return cache;
    }

    public void clear() {
        saveCurrentUser();
        userId = null;
        userName = null;
        nimToken = null;
        accessToken = null;
        me = null;
    }

    public User me;

    @SuppressWarnings("ConstantConditions")
    public void saveCurrentUser() {
        App.app().saveCurrentUser(me);
    }

    private void logUser(User user, String tag) {
        if (null == user) {
            LogHelper.log("CacheLog", tag + " user is null");
        } else {
            LogHelper.log("CacheLog", tag + " user is: " +
                    StringHelper.format("id: %s, accessToken: %s, last login: %s", user.getId(), user.getAccessToken(), user.getLastLoginDate()));
        }
    }

    private static int get(int res, int resBeta) {
        return BuildConfig.RELEASEABLE ? resBeta : res;
    }

    public void setCurrentUser(User user) {
        logUser(me, "Old");
        logUser(user, "New");
        me = user;
        if (null != me && !StringHelper.isEmpty(me.getId())) {
            userId = me.getId();
            nimToken = me.getPassword();
            userName = me.getName();
            accessToken = me.getAccessToken();
            NimSessionHelper.setAccount(userId);
            // 保存网易云登录的账户和令牌
            PreferenceHelper.save(get(R.string.pf_last_login_user_id, R.string.pf_last_login_user_id_beta), userId);
            PreferenceHelper.save(get(R.string.pf_last_login_user_token, R.string.pf_last_login_user_token_beta), accessToken);
            PreferenceHelper.save(get(R.string.pf_last_login_user_nim_token, R.string.pf_last_login_user_nim_token_beta), nimToken);
        }
    }

    public void restoreCached() {
        userId = PreferenceHelper.get(get(R.string.pf_last_login_user_id, R.string.pf_last_login_user_id_beta));
        accessToken = PreferenceHelper.get(get(R.string.pf_last_login_user_token, R.string.pf_last_login_user_token_beta));
        nimToken = PreferenceHelper.get(get(R.string.pf_last_login_user_nim_token, R.string.pf_last_login_user_nim_token_beta));
    }

    /**
     * 查看当前用户是否需要同步基本信息
     */
    public boolean isNeedSync() {
        if (StringHelper.isEmpty(userId) || null == me) {
            return true;
        }

        long then = Utils.parseDate(StringHelper.getString(R.string.ui_base_text_date_time_format), me.getLastLoginDate()).getTime();
        long now = Utils.timestamp();
        return (now - then) >= Utils.DAY * 7;
    }

    /**
     * 当前登录用户的id、姓名、网易云信登录的token、api服务器的accessToken
     */
    public String userId, nimToken, accessToken, userName;

    public ArrayList<String> groupIds = new ArrayList<>();
}
