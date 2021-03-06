package com.leadcom.android.isp.model.organization;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.reflect.TypeToken;
import com.hlk.hlklib.etc.Cryptography;
import com.leadcom.android.isp.R;
import com.leadcom.android.isp.helper.StringHelper;
import com.leadcom.android.isp.lib.Json;
import com.leadcom.android.isp.model.user.SimpleUser;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    public static String toJson(ArrayList<SubMember> list, String[] ignoreFields) {
        final List<String> ids = Arrays.asList(ignoreFields);
        return Json.gson(new ExclusionStrategy() {
            @Override
            public boolean shouldSkipField(FieldAttributes f) {
                return ids.contains(f.getName());
            }

            @Override
            public boolean shouldSkipClass(Class<?> clazz) {
                return false;
            }
        }).toJson((null == list ? new ArrayList<>() : list), new TypeToken<ArrayList<SubMember>>() {
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

    public static String getMemberNames(ArrayList<SubMember> members) {
        String name = "";
        int count = 0;
        if (null != members && members.size() > 0) {
            for (SubMember member : members) {
                if (count < 3) {
                    name += (StringHelper.isEmpty(name) ? "" : "、") + member.getUserName();
                }
                count++;
            }
            name = "[" + name + "]";
            if (members.size() > 1) {
                name += "等";
            }
        }
        return name;
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

    /**
     * 成员类别
     */
    public interface MemberType {
        /**
         * 用户
         */
        int USER = 1;
        /**
         * 组织
         */
        int GROUP = 2;
    }

    private String userId;
    private String userName;
    private String squadId;
    private int type;

    public SubMember() {
    }

    public SubMember(Member member) {
        userId = member.getUserId();
        userName = member.getUserName();
        squadId = member.getSquadId();
        type = MemberType.USER;
    }

    public SubMember(SimpleUser user) {
        userId = user.getUserId();
        userName = user.getUserName();
        type = MemberType.USER;
    }

    public SubMember(RelateGroup group) {
        userId = group.getGroupId();
        userName = group.getGroupName();
        type = MemberType.GROUP;
    }

    /**
     * 本节点用户是否在列表中存在
     */
    public boolean isUserExistedIn(ArrayList<SubMember> members) {
        if (null == members || members.size() < 1) return false;
        for (SubMember member : members) {
            if (!isEmpty(member.getUserId()) && !isEmpty(userId) && member.getUserId().equals(userId)) {
                return true;
            }
        }
        return false;
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

    public String getSquadId() {
        return squadId;
    }

    public void setSquadId(String squadId) {
        this.squadId = squadId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean isMember() {
        return type == MemberType.USER;
    }

    public boolean isGroup() {
        return type == MemberType.GROUP;
    }

    private static boolean isEmpty(String string) {
        return StringHelper.isEmpty(string);
    }

    @Override
    public boolean equals(Object object) {
        if (null == object) return false;
        if (object instanceof SubMember) {
            SubMember member = (SubMember) object;
            boolean equals = !isEmpty(userId) &&
                    !isEmpty(member.getUserId()) &&
                    member.getUserId().equals(userId);
            if (!isEmpty(member.getSquadId())) {
                return equals && !isEmpty(squadId) && squadId.equals(member.getSquadId());
            }
            return equals;
        }
        return false;
    }
}
