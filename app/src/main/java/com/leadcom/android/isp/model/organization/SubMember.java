package com.leadcom.android.isp.model.organization;

import com.google.gson.reflect.TypeToken;
import com.hlk.hlklib.etc.Cryptography;
import com.hlk.hlklib.etc.Utility;
import com.leadcom.android.isp.R;
import com.leadcom.android.isp.etc.Utils;
import com.leadcom.android.isp.helper.StringHelper;
import com.leadcom.android.isp.lib.Json;
import com.leadcom.android.isp.model.user.SimpleUser;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * <b>功能描述：</b>只包含userId和userName两个属性的简单member对象<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/06/09 21:52 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/06/09 21:52 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class SubMember implements Serializable {

    public static String toJson(ArrayList<SubMember> list) {
        return Json.gson().toJson((null == list ? new ArrayList<>() : list), new TypeToken<ArrayList<SubMember>>() {
        }.getType());
    }

    public static String toJson(SubMember member) {
        return null == member ? "{}" : Json.gson().toJson(member, new TypeToken<SubMember>() {
        }.getType());
    }

    public static ArrayList<SubMember> fromJson(String json) {
        return Json.gson().fromJson((StringHelper.isEmptyJsonArray(json) ? "[]" : json), new TypeToken<ArrayList<SubMember>>() {
        }.getType());
    }

    public static SubMember fromJsonOne(String json) {
        return Json.gson().fromJson((StringHelper.isEmptyJsonArray(json) ? "{}" : json), new TypeToken<SubMember>() {
        }.getType());
    }

    public static String getMemberInfo(ArrayList<SubMember> list) {
        String string = "";
        if (list.size() < 1) {
            string = StringHelper.getString(R.string.ui_activity_create_member_select_title);
        } else {
            int i = 0;
            for (SubMember member : list) {
                String name = member.getUserName();
                string += (StringHelper.isEmpty(string) ? "" : "、") + (StringHelper.isEmpty(name) ? "" : name);
                if (i >= 1) {
                    break;
                }
                i++;
            }
            int size = list.size();
            string += StringHelper.format("%s共%d人", (StringHelper.isEmpty(string) ? "" : (size > 2 ? "等，" : "，")), list.size());
        }
        return string;
    }

    public static ArrayList<String> getUserIds(ArrayList<SubMember> list) {
        ArrayList<String> ids = new ArrayList<>();
        if (null != list && list.size() > 0) {
            for (SubMember member : list) {
                if (!ids.contains(member.getUserId())) {
                    ids.add(member.getUserId());
                }
            }
        }
        return ids;
    }

    public static ArrayList<SubMember> getMember(ArrayList<SimpleUser> users) {
        ArrayList<SubMember> members = new ArrayList<>();
        if (null != users && users.size() > 0) {
            for (SimpleUser user : users) {
                members.add(new SubMember(user));
            }
        }
        return members;
    }

    private String userId;
    private String userName;

    public SubMember() {
    }

    public SubMember(Member member) {
        userId = member.getUserId();
        userName = member.getUserName();
    }

    public SubMember(SimpleUser user) {
        userId = user.getUserId();
        userName = user.getUserName();
    }

    public String getUserId() {
        if (StringHelper.isEmpty(userId, true)) {
            userId = Cryptography.md5(userName);
        }
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public boolean equals(Object object) {
        return null != object && getClass() == object.getClass() && equals((SubMember) object);
    }

    public boolean equals(SubMember member) {
        return null != member &&
                !StringHelper.isEmpty(getUserId()) &&
                !StringHelper.isEmpty(member.getUserId()) &&
                member.getUserId().equals(getUserId());
    }
}
