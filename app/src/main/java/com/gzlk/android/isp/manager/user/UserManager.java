package com.gzlk.android.isp.manager.user;

import com.gzlk.android.isp.manager.BaseManager;

/**
 * <b>功能描述：</b>用户管理器<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/05/20 17:29 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/05/20 17:29 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class UserManager extends BaseManager {

    public static UserManager manager() {
        return new UserManager();
    }

    @Override
    protected Class requestedType() {
        return null;
    }

    @Override
    public void find(String byId) {

    }
}
