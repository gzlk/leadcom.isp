package com.leadcom.android.isp.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import com.leadcom.android.isp.helper.LogHelper;
import com.leadcom.android.isp.helper.StringHelper;


/**
 * <b>功能描述：</b>后台服务的基类<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2018/08/30 13:01 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2018/08/30 13:01  <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */
public abstract class BaseService extends Service {

    private final String TAG = DraftService.class.getSimpleName();

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    protected void log(String string) {
        LogHelper.log(TAG, string);
    }

    protected String format(String fmt, Object... args) {
        return StringHelper.format(fmt, args);
    }

    protected boolean isEmpty(String string) {
        return StringHelper.isEmpty(string, true);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (null != intent) {
            String action = intent.getAction();
            if (!StringHelper.isEmpty(action)) {
                onHandleAction(action, intent);
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * 处理收到的action
     *
     * @param action Action 字符串，有可能为空
     * @param intent 传递过来的数据内容集合
     */
    protected abstract void onHandleAction(String action, @NonNull Intent intent);
}
