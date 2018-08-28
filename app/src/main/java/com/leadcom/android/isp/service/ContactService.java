package com.leadcom.android.isp.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.leadcom.android.isp.application.App;
import com.leadcom.android.isp.etc.SysInfoUtil;
import com.leadcom.android.isp.helper.LogHelper;
import com.leadcom.android.isp.helper.StringHelper;
import com.leadcom.android.isp.model.Dao;
import com.leadcom.android.isp.model.common.Contact;

import java.util.ArrayList;

/**
 * <b>功能描述：</b><br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2018/08/28 10:12 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2018/08/28 10:12 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */
public class ContactService extends Service {
    private static final String TAG = "ContactService";
    public static final String ACTION_CONTACT = "com.leadcom.android.isp.service.CONTACTS";
    private static final String EXTRA_CONTACTS = "cs_contacts";

    public static void start(ArrayList<Contact> contacts) {
        Intent intent = new Intent(App.app(), ContactService.class);
        intent.setAction(ACTION_CONTACT);
        intent.putExtra(EXTRA_CONTACTS, contacts);
        App.app().startService(intent);
    }

    private void log(String string) {
        LogHelper.log(TAG, string);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        log("onCreate");
        super.onCreate();
    }

    @Override
    public void onDestroy() {
        log("onDestroy");
        super.onDestroy();
    }

    @SuppressWarnings("unchecked")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (null != intent) {
            String action = intent.getAction();
            if (ACTION_CONTACT.equals(action)) {
                ArrayList<Contact> contacts = (ArrayList<Contact>) intent.getSerializableExtra(EXTRA_CONTACTS);
                if (null != contacts && contacts.size() > 0) {
                    long start = System.currentTimeMillis();
                    new Dao<>(Contact.class).save(contacts);
                    long end = System.currentTimeMillis();
                    log("save " + contacts.size() + " contacts used " + (end - start) + " milliseconds");
                    stopSelf();
                }
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }
}
