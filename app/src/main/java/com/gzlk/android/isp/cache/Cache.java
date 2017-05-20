package com.gzlk.android.isp.cache;

import com.gzlk.android.isp.R;
import com.gzlk.android.isp.helper.PreferenceHelper;
import com.gzlk.android.isp.helper.StringHelper;
import com.gzlk.android.isp.model.Dao;
import com.gzlk.android.isp.model.user.User;

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
        userId = null;
        userName = null;
        nimToken = null;
        accessToken = null;
        me = null;
    }

    public User me;

    public void saveCurrentUser() {
        new Dao<>(User.class).save(me);
    }

    public void setCurrentUser(User user) {
        me = user;
        if (null != me && !StringHelper.isEmpty(me.getId())) {
            saveCurrentUser();
            userId = me.getId();
            nimToken = me.getPassword();
            userName = me.getName();
            accessToken = me.getAccessToken();
            // 保存网易云登录的账户和令牌
            PreferenceHelper.save(R.string.pf_last_login_user_id, me.getId());
            PreferenceHelper.save(R.string.pf_last_login_user_token, me.getPassword());
        }
    }

    /**
     * 当前登录用户的id、姓名、网易云信登录的token、api服务器的accessToken
     */
    public String userId, nimToken, accessToken, userName;
}
