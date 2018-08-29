package com.leadcom.android.isp.model.user;

import com.google.gson.reflect.TypeToken;
import com.leadcom.android.isp.lib.Json;

import java.util.ArrayList;

/**
 * <b>功能描述：</b>常用中文名双字<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2018/08/29 13:31 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2018/08/29 13:31 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */
public class Name {

    public static ArrayList<Name> fromJson(String json) {
        return Json.gson().fromJson(json, new TypeToken<ArrayList<Name>>() {
        }.getType());
    }

    private int id;
    private String name;
    private int hot;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getHot() {
        return hot;
    }

    public void setHot(int hot) {
        this.hot = hot;
    }
}
