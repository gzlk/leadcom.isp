package com.leadcom.android.isp.application;

import android.Manifest;

import com.leadcom.android.isp.fragment.base.BasePermissionHandleSupportFragment;
import com.leadcom.android.isp.helper.StringHelper;
import com.leadcom.android.isp.listener.OnLiteOrmTaskExecutingListener;
import com.leadcom.android.isp.model.Model;
import com.leadcom.android.isp.task.OrmTask;
import com.hlk.hlklib.etc.Cryptography;
import com.litesuits.orm.LiteOrm;
import com.litesuits.orm.db.DataBaseConfig;

import java.util.List;
import java.util.concurrent.ExecutionException;

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

    public static LiteOrm Orm;

    protected void closeOrm() {
        if (null != Orm) {
            Orm.close();
            Orm = null;
        }
    }

    /**
     * 按照指定的文件名初始化数据库
     */
    public void initializeLiteOrm(final String dbName) {
        if (StringHelper.isEmpty(dbName)) {
            throw new IllegalArgumentException("could not init database with null parameter.");
        }

        try {
            new OrmTask<>().addOnLiteOrmTaskExecutingListener(new OnLiteOrmTaskExecutingListener<Model>() {
                @Override
                public boolean isModifiable() {
                    return false;
                }

                @Override
                public List<Model> executing(OrmTask<Model> task) {

                    String db = getCachePath(DB_DIR) + Cryptography.md5(dbName) + ".db";
                    if (null == Orm) {
                        if (initialize(db)) {
                            log("database initialized at: " + db);
                        }
                    } else {
                        if (!Orm.getDataBaseConfig().dbName.equals(db)) {
                            Orm.close();
                            if (initialize(db)) {
                                log("database re-initialized at: " + db);
                            }
                        }
                    }

                    return null;
                }

                private boolean initialize(String db) {
                    if (BasePermissionHandleSupportFragment.hasPermission(OrmApplication.this, Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                        try {
                            Orm = LiteOrm.newSingleInstance(getConfig(db));
                            Orm.openOrCreateDatabase();
                            return true;
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        Orm = null;
                    } else {
                        // 没有权限时，orm = null
                        Orm = null;
                    }
                    return false;
                }

                private DataBaseConfig getConfig(String dbName) {
                    DataBaseConfig config = new DataBaseConfig(OrmApplication.this, dbName);
                    //config.debugged = BuildConfig.DEBUG;
                    config.dbVersion = 1;
                    return config;
                }
            }).exec().get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }
}
