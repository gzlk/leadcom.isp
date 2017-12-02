package com.leadcom.android.isp.helper;

import android.content.Context;
import android.content.SharedPreferences;

import com.leadcom.android.isp.application.App;

/**
 * 简单的配置数据
 * 作者：Hsiang Leekwok on 2015/08/26 10:56<br />
 * 邮箱：xiang.l.g@gmail.com<br />
 */
public class PreferenceHelper {

    private static final String SHARED_NAME = "setting";

    /**
     * 保存简单的数据到Preference
     */
    public static void save(int key, String value) {
        save(StringHelper.getString(key), value);
    }

    /**
     * 保存简单的数据到Preference
     */
    @SuppressWarnings("ConstantConditions")
    public static void save(String key, String value) {
        SharedPreferences.Editor editor = App.app().getSharedPreferences(name(), Context.MODE_PRIVATE).edit();
        try {
            editor.putString(key, value);
        } finally {
            editor.apply();
        }
    }

    /**
     * 从Preference中获取字符串值
     */
    public static String get(int key) {
        return get(StringHelper.getString(key));
    }

    /**
     * 从Preference中获取字符串值
     */
    public static String get(int key, String defaultValue) {
        return get(StringHelper.getString(key), defaultValue);
    }

    /**
     * 从Preference中获取字符串值
     */
    public static String get(String key) {
        return get(key, null);
    }

    @SuppressWarnings("ConstantConditions")
    private static String name() {
        return SHARED_NAME;//StringHelper.format("%s_%s", SHARED_NAME, Cryptography.md5(App.app().UserId()));
    }

    /**
     * 从Preference中获取字符串值
     */
    @SuppressWarnings("ConstantConditions")
    public static String get(String key, String defaultValue) {
        return App.app().getSharedPreferences(name(), Context.MODE_PRIVATE).getString(key, defaultValue);
    }
}
