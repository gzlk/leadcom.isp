package com.gzlk.android.isp.application;

import android.Manifest;

import com.gzlk.android.isp.fragment.base.BasePermissionHandleFragment;
import com.gzlk.android.isp.helper.LogHelper;
import com.gzlk.android.isp.helper.StringHelper;
import com.hlk.hlklib.etc.Cryptography;
import com.litesuits.orm.LiteOrm;
import com.litesuits.orm.db.DataBaseConfig;

/**
 * <b>功能描述：</b>提供SQLite数据库操作的application基类<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/04/10 22:39 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/04/10 22:39 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class OrmApplication extends BaseApplication {

    private static final String TAG = OrmApplication.class.getSimpleName();

    public static LiteOrm Orm;

    protected void closeOrm() {
        if (null != Orm) {
            Orm.close();
        }
    }

    /**
     * 按照指定的文件名初始化数据库
     */
    public void initializeLiteOrm(String dbName) {
        if (StringHelper.isEmpty(dbName)) {
            throw new IllegalArgumentException("could not initialize database with null parameter.");
        }

        String db = getCachePath(DB_DIR) + Cryptography.md5(dbName) + ".db";
        if (null == Orm) {
            if (initialize(db)) {
                LogHelper.log(TAG, "database initialized at: " + db);
            }
        } else {
            if (!Orm.getDataBaseConfig().dbName.equals(db)) {
                Orm.close();
                if (initialize(db)) {
                    LogHelper.log(TAG, "database re-initialized at: " + db);
                }
            }
        }
    }

    private boolean initialize(String db) {
        if (BasePermissionHandleFragment.hasPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            try {
                Orm = LiteOrm.newSingleInstance(getConfig(db));
                return true;
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            // 没有权限时，orm = null
            Orm = null;
        }
        return false;
    }

    private DataBaseConfig getConfig(String dbName) {
        DataBaseConfig config = new DataBaseConfig(this, dbName);
        //config.debugged = BuildConfig.DEBUG;
        config.dbVersion = 1;
        return config;
    }
}
