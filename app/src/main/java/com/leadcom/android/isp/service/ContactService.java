package com.leadcom.android.isp.service;

import android.content.ContentResolver;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;

import com.hlk.hlklib.etc.Cryptography;
import com.hlk.hlklib.etc.Utility;
import com.leadcom.android.isp.R;
import com.leadcom.android.isp.application.App;
import com.leadcom.android.isp.cache.Cache;
import com.leadcom.android.isp.etc.Utils;
import com.leadcom.android.isp.helper.PreferenceHelper;
import com.leadcom.android.isp.helper.StringHelper;
import com.leadcom.android.isp.model.Dao;
import com.leadcom.android.isp.model.common.Contact;
import com.litesuits.orm.db.assit.QueryBuilder;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * <b>功能描述：</b>联系人缓存服务<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2018/08/28 10:12 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2018/08/28 10:12 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */
public class ContactService extends BaseService {

    public static final String ACTION_WRITE_CONTACT = "com.leadcom.android.isp.service.WRITE_CONTACTS";
    public static final String ACTION_READ_CONTACT = "com.leadcom.android.isp.service.READ_CONTACTS";
    public static final String ACTION_REFRESH_CONTACT = "com.leadcom.android.isp.service.REFRESH_CONTACTS";
    public static final String ACTION_REFRESH_COMPLETE = "com.leadcom.android.isp.service.REFRESH_COMPLETE";

    public static void start(boolean writable) {
        Intent intent = new Intent(App.app(), ContactService.class);
        intent.setAction(writable ? ACTION_WRITE_CONTACT : ACTION_READ_CONTACT);
        App.app().startService(intent);
    }

    public static void refresh() {
        Intent intent = new Intent(App.app(), ContactService.class);
        intent.setAction(ACTION_REFRESH_CONTACT);
        App.app().startService(intent);
    }

    @Override
    protected void onHandleAction(String action, @NonNull Intent intent) {
        log(action);
        long start = System.currentTimeMillis(), end;
        String msg = "";
        if (ACTION_WRITE_CONTACT.equals(action)) {
            saveContacts();
            msg = format("save %d contacts", App.app().getContacts().size());
        } else if (ACTION_READ_CONTACT.equals(action)) {
            readContactsFromSQLite();
            msg = format("read total %d contacts", App.app().getContacts().size());
        } else if (ACTION_REFRESH_CONTACT.equals(action)) {
            // 重新读取联系人列表并刷新缓存（当有新的联系人添加进来时）
            checkContactsIsChanged();
        }
        end = System.currentTimeMillis();
        if (!StringHelper.isEmpty(msg)) {
            log(format("%s used %d milliseconds", msg, (end - start)));
        }
        stopSelf();
    }

    private void saveContacts() {
        ArrayList<Contact> contacts = App.app().getContacts();
        if (null != contacts && contacts.size() > 0) {
            Dao<Contact> dao = new Dao<>(Contact.class);
            dao.clear();
            dao.save(contacts);
        }
    }

    private void readContactsFromSQLite() {
        App.app().getContacts().clear();
        Dao<Contact> dao = new Dao<>(Contact.class);
        long max = dao.getCount();
        int PAGE_SIZE = 500;
        long pages = max / PAGE_SIZE + (max % PAGE_SIZE > 0 ? 1 : 0);
        for (int i = 0; i < pages; i++) {
            long start = System.currentTimeMillis(), end;
            List<Contact> page = dao.query(new QueryBuilder<>(Contact.class).limit(i * PAGE_SIZE, PAGE_SIZE));
            if (null != page && page.size() > 0) {
                App.app().getContacts().addAll(page);
                end = System.currentTimeMillis();
                log(format("read contacts page %d/%d(size: %d) from SQLite used %d milliseconds", (i + 1), pages, page.size(), (end - start)));
            }
        }
    }

    private String[] FIELDS = new String[]{
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER
    };

    private void gotContacts() {

        ArrayList<String[]> nameList = new ArrayList<>();

        try {
            long start = System.currentTimeMillis();
            ContentResolver resolver = App.app().getContentResolver();
            Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
            Cursor cursor = resolver.query(uri, FIELDS, null, null, null);
            int max = 0;
            if (null != cursor) {
                try {
                    int index = 0;
                    max = cursor.getCount();
                    //publishProgress(0, index, max);
                    while (cursor.moveToNext()) {
                        String name = cursor.getString(cursor.getColumnIndex(FIELDS[0]));
                        String phone = cursor.getString(cursor.getColumnIndex(FIELDS[1]));
                        if (!StringHelper.isEmpty(phone)) {
                            phone = Utility.filterNumbers(phone);
                        }
                        // 名字不为空且号码为手机号码时才加入缓存
                        if (!StringHelper.isEmpty(name) && Utils.isItMobilePhone(phone)) {
                            nameList.add(new String[]{name, phone});
                        }
                        index++;
                        //log(format("read progress, index: %d, name: %s, phone: %s", index, name, phone));
                        //publishProgress(0, index, max);
                    }
                } finally {
                    cursor.close();
                }
            }
            long end = System.currentTimeMillis();
            log(format("reading contact(%d) cost: %d milliseconds.", max, end - start));
        } catch (Exception ignore) {
            ignore.printStackTrace();
        }
        handleContacts(nameList);
    }

    private void handleContacts(ArrayList<String[]> names) {
        long start = System.currentTimeMillis();
        int index = 0, max = names.size();
        //publishProgress(1, index, max);
        try {
            ArrayList<Contact> save = new ArrayList<>();
            for (String[] strings : names) {
                String name = strings[0];
                String phone = strings[1];
                // contact 主键id为 phone 的 md5 值
                Contact contact = new Contact();
                contact.setName(name);
                contact.setPhone(phone);
                //contact.setMember(isMemberExists(contact.getPhone()));
                contact.setInvited(false);
                save.add(contact);
                index++;
                //log(format("handle progress, index: %d, name: %s, phone: %s", index, name, phone));
                //publishProgress(1, index, max);
            }
            App.app().getContacts().clear();
            App.app().getContacts().addAll(save);
            Collections.sort(App.app().getContacts(), new Comparator<Contact>() {
                @Override
                public int compare(Contact o1, Contact o2) {
                    String first1 = o1.getSpell().split(" ")[0];
                    String first2 = o2.getSpell().split(" ")[0];
                    int compare = first1.compareTo(first2);
                    if (compare == 0) {
                        return o1.getName().compareTo(o2.getName());
                    }
                    return compare;
                }
            });
        } catch (Exception ignore) {
            ignore.printStackTrace();
        }
        long end = System.currentTimeMillis();
        log(format("handing contact(%d) cost: %d milliseconds.", max, end - start));
        // 保存
        saveContacts();

        // 通知已经读取完毕
        Intent intent = new Intent();
        intent.setAction(ACTION_REFRESH_COMPLETE);
        sendBroadcast(intent);
        //ContactService.start(true);
        //stopSelf();
    }

    private void checkContactsIsChanged() {
        int res = Cache.get(R.string.pf_last_login_user_contact_changed, R.string.pf_last_login_user_contact_changed_beta);
        String old = PreferenceHelper.get(res, "");
        String ids = gotContactsIds();
        PreferenceHelper.save(res, ids);
        if (isEmpty(old)) {
            log("no cached contact change flag exists, now refresh contacts.");
            gotContacts();
        } else {
            log(format("old flag: %s, new flag %s", old, ids));
            if (!old.equals(ids)) {
                gotContacts();
            }
        }
    }

    private String gotContactsIds() {
        try {
            StringBuilder builder = new StringBuilder();
            ContentResolver resolver = App.app().getContentResolver();
            Uri uri = ContactsContract.RawContacts.CONTENT_URI;
            Cursor cursor = resolver.query(uri, null, null, null, null);
            if (null != cursor) {
                try {
                    while (cursor.moveToNext()) {
                        long version = cursor.getLong(cursor.getColumnIndex(ContactsContract.RawContacts.VERSION));
                        builder.append(version);
                    }
                } finally {
                    cursor.close();
                }
            }
            return Cryptography.md5(builder.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
