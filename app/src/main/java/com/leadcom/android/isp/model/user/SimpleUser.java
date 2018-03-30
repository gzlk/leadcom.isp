package com.leadcom.android.isp.model.user;

import com.google.gson.reflect.TypeToken;
import com.leadcom.android.isp.lib.Json;
import com.leadcom.android.isp.model.Model;

import java.util.ArrayList;

/**
 * <b>功能描述：</b><br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/08/14 10:27 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/08/14 10:27 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class SimpleUser extends Model {

    public static String toJson(ArrayList<SimpleUser> list) {
        return null == list ? "[]" : Json.gson().toJson(list, new TypeToken<ArrayList<SimpleUser>>() {
        }.getType());
    }

    public static ArrayList<SimpleUser> fromJson(String json) {
        return Json.gson().fromJson((isEmpty(json) ? "[]" : json), new TypeToken<ArrayList<SimpleUser>>() {
        }.getType());
    }

    private String userName;
    private String userId;
    private String headPhoto;
    private String phone;

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getHeadPhoto() {
        return headPhoto;
    }

    public void setHeadPhoto(String headPhoto) {
        this.headPhoto = headPhoto;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
