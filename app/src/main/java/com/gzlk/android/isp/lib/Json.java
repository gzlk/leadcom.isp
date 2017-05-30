package com.gzlk.android.isp.lib;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * <b>功能描述：</b>Json对象<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/04/17 21:27 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/04/17 21:27 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class Json {

    private static Gson normal;

    /**
     * 得到一个正常的Gson实例
     */
    public static Gson gson() {
        if (null == normal) {
            normal = new Gson();
        }
        return normal;
    }

    /**
     * 获取一个Gson实例并指定在序列化（反序列化时）需要忽略的父类属性
     *
     * @param skipFieldClass 序列化、反序列化时要忽略的父类名称，如A.class
     */
    public static Gson gson(final Object skipFieldClass) {
        return new GsonBuilder().setExclusionStrategies(new ExclusionStrategy() {
            @Override
            public boolean shouldSkipField(FieldAttributes f) {
                return f.getDeclaringClass().equals(skipFieldClass);
            }

            @Override
            public boolean shouldSkipClass(Class<?> clazz) {
                return false;
            }
        }).create();
    }

    /**
     * 获取一个Gson实例并指定在序列化（反序列化时）的排除策略
     *
     * @param strategy 序列化、反序列化时的自定义排除策略
     */
    public static Gson gson(ExclusionStrategy strategy) {
        return new GsonBuilder().setExclusionStrategies(strategy).create();
    }
}
