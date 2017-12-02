package com.leadcom.android.isp.model.user;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.reflect.TypeToken;
import com.leadcom.android.isp.lib.Json;
import com.leadcom.android.isp.model.Model;
import com.hlk.hlklib.etc.Cryptography;

import java.util.ArrayList;

/**
 * <b>功能描述：</b>用户的附加属性<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/08/19 12:48 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/08/19 12:48 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class UserExtra extends Model {

    public static String toJson(ArrayList<UserExtra> list) {
        return Json.gson(new ExclusionStrategy() {
            @Override
            public boolean shouldSkipField(FieldAttributes f) {
                return f.getName().equals("id") ||
                        f.getName().startsWith("is") ||
                        f.getName().startsWith("local");
            }

            @Override
            public boolean shouldSkipClass(Class<?> aClass) {
                return false;
            }
        }).toJson(list, new TypeToken<ArrayList<UserExtra>>() {
        }.getType());
    }

    /**
     * 显示类型
     */
    public interface ShownType {
        /**
         * 隐藏
         */
        int HIDE = 0;
        /**
         * 显示
         */
        int SHOWN = 1;
    }

    private String title;   //标题
    private String content; //内容
    private int show;    //是否隐藏(0.隐藏,1.显示)

    @Override
    public String getId() {
        if (isEmpty(super.getId())) {
            if (isEmpty(title)) {
                throw new IllegalArgumentException("User extra field \"title\" is empty.");
            }
            setId(Cryptography.md5(title));
        }
        return super.getId();
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
        getId();
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getShow() {
        if (show != ShownType.HIDE && show != ShownType.SHOWN) {
            show = ShownType.HIDE;
        }
        return show;
    }

    public void setShow(int show) {
        this.show = show;
    }
}
