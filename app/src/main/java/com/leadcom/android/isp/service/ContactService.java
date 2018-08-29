package com.leadcom.android.isp.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.support.annotation.Nullable;

import com.leadcom.android.isp.application.App;
import com.leadcom.android.isp.helper.LogHelper;
import com.leadcom.android.isp.model.Dao;
import com.leadcom.android.isp.model.common.Contact;
import com.litesuits.orm.db.assit.QueryBuilder;

import java.util.ArrayList;
import java.util.List;

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
    public static final String ACTION_WRITE_CONTACT = "com.leadcom.android.isp.service.WRITE_CONTACTS";
    public static final String ACTION_READ_CONTACT = "com.leadcom.android.isp.service.READ_CONTACTS";

    public static void start(boolean writable) {
        Intent intent = new Intent(App.app(), ContactService.class);
        intent.setAction(writable ? ACTION_WRITE_CONTACT : ACTION_READ_CONTACT);
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
            long start = System.currentTimeMillis(), end;
            String action = intent.getAction();
            if (ACTION_WRITE_CONTACT.equals(action)) {
                ArrayList<Contact> contacts = App.app().getContacts();
                if (null != contacts && contacts.size() > 0) {
                    new Dao<>(Contact.class).save(contacts);
                    end = System.currentTimeMillis();
                    log("save " + contacts.size() + " contacts used " + (end - start) + " milliseconds");
                }
                stopSelf();
            } else if (ACTION_READ_CONTACT.equals(action)) {
                App.app().getContacts().clear();
                Dao<Contact> dao = new Dao(Contact.class);
                long max = dao.getCount();
                int PAGE_SIZE = 200;
                long pages = max / PAGE_SIZE + (max % PAGE_SIZE > 0 ? 1 : 0);
                for (int i = 0; i < pages; i++) {
                    List<Contact> page = dao.query(new QueryBuilder<>(Contact.class).limit(i * PAGE_SIZE, PAGE_SIZE));
                    if (null != page && page.size() > 0) {
                        App.app().getContacts().addAll(page);
                        end = System.currentTimeMillis();
                        log("read " + page.size() + " contacts from SQLite used " + (end - start) + " milliseconds");
                    }
                }
                end = System.currentTimeMillis();
                log("read " + App.app().getContacts().size() + " contacts used " + (end - start) + " milliseconds");
                stopSelf();
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }
}
