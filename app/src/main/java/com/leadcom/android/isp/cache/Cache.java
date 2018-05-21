package com.leadcom.android.isp.cache;

import android.os.Build;

import com.google.gson.reflect.TypeToken;
import com.leadcom.android.isp.BuildConfig;
import com.leadcom.android.isp.R;
import com.leadcom.android.isp.application.App;
import com.leadcom.android.isp.etc.Utils;
import com.leadcom.android.isp.helper.LogHelper;
import com.leadcom.android.isp.helper.PreferenceHelper;
import com.leadcom.android.isp.helper.StringHelper;
import com.leadcom.android.isp.lib.Json;
import com.leadcom.android.isp.model.organization.Organization;
import com.leadcom.android.isp.model.organization.RelateGroup;
import com.leadcom.android.isp.model.organization.Role;
import com.leadcom.android.isp.model.user.User;
import com.leadcom.android.isp.nim.session.NimSessionHelper;

import java.util.ArrayList;
import java.util.List;

import cn.jpush.android.api.JPushInterface;

/**
 * <b>功能描述：</b>全局缓存<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/05/10 20:32 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/05/10 20:32 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class Cache {

    public static boolean isReleasable() {
        return BuildConfig.BUILD_TYPE.equals("beta") || BuildConfig.BUILD_TYPE.equals("release");
    }

    public static int sdk = Build.VERSION.SDK_INT;

    private static Cache cache;

    private Cache() {
        userPhone = PreferenceHelper.get(get(R.string.pf_last_login_user_account, R.string.pf_last_login_user_account_beta), "");
    }

    /**
     * 获取当前全局实例缓存
     */
    public static Cache cache() {
        if (null == cache) {
            cache = new Cache();
        }
        return cache;
    }

    public void clear() {
        saveCurrentUser();
        userId = null;
        userName = null;
        nimToken = null;
        accessToken = null;
        me = null;
        PreferenceHelper.save(get(R.string.pf_last_login_user_id, R.string.pf_last_login_user_id_beta), "");
    }

    public User me;

    @SuppressWarnings("ConstantConditions")
    public void saveCurrentUser() {
        App.app().saveCurrentUser(me);
    }

    private void logUser(User user, String tag) {
        if (null == user) {
            LogHelper.log("CacheLog", tag + " user is null");
        } else {
            LogHelper.log("CacheLog", tag + " user is: " +
                    StringHelper.format("id: %s, accessToken: %s, last login: %s", user.getId(), user.getAccessToken(), user.getLastLoginDate()));
        }
    }

    private static int get(int res, int resBeta) {
        return isReleasable() ? resBeta : res;
    }

    public void setCurrentUser(User user) {
        logUser(me, "Old");
        logUser(user, "New");
        me = user;
        if (null != me && !StringHelper.isEmpty(me.getId())) {
            userId = me.getId();
            nimToken = me.getPassword();
            userName = me.getName();
            accessToken = me.getAccessToken();
            NimSessionHelper.setAccount(userId);
            userPhone = me.getPhone();
            // 保存网易云登录的账户和令牌
            PreferenceHelper.save(get(R.string.pf_last_login_user_id, R.string.pf_last_login_user_id_beta), userId);
            PreferenceHelper.save(get(R.string.pf_last_login_user_token, R.string.pf_last_login_user_token_beta), accessToken);
            PreferenceHelper.save(get(R.string.pf_last_login_user_nim_token, R.string.pf_last_login_user_nim_token_beta), nimToken);
            PreferenceHelper.save(get(R.string.pf_last_login_user_account, R.string.pf_last_login_user_account_beta), userPhone);
        }
    }

    public void restoreCached() {
        userId = PreferenceHelper.get(get(R.string.pf_last_login_user_id, R.string.pf_last_login_user_id_beta));
        accessToken = PreferenceHelper.get(get(R.string.pf_last_login_user_token, R.string.pf_last_login_user_token_beta));
        nimToken = PreferenceHelper.get(get(R.string.pf_last_login_user_nim_token, R.string.pf_last_login_user_nim_token_beta));
        restoreGroups();
        if (!StringHelper.isEmpty(userId)) {
            JPushInterface.setAlias(App.app(), 0, userId);
        }
    }

    public void restoreGroups() {
        String json = PreferenceHelper.get(StringHelper.getString(get(R.string.pf_last_login_user_groups, R.string.pf_last_login_user_groups_beta), userId), "[]");
        groups = Json.gson().fromJson(json, new TypeToken<ArrayList<RelateGroup>>() {
        }.getType());
        if (null == groups) {
            groups = new ArrayList<>();
        }
    }

    /**
     * 查看当前用户是否需要同步基本信息
     */
    public boolean isNeedSync() {
        if (StringHelper.isEmpty(userId) || null == me) {
            return true;
        }

        long then = Utils.parseDate(StringHelper.getString(R.string.ui_base_text_date_time_format), me.getLastLoginDate()).getTime();
        long now = Utils.timestamp();
        return (now - then) >= Utils.DAY * 7;
    }

    public void resetRelatedGroups(List<RelateGroup> list) {
        if (null != list) {
            groups.clear();
            groups.addAll(list);
            saveGroups();
        }
    }

    private void saveGroups() {
        String json = Json.gson().toJson(groups, new TypeToken<ArrayList<RelateGroup>>() {
        }.getType());
        PreferenceHelper.save(StringHelper.getString(get(R.string.pf_last_login_user_groups, R.string.pf_last_login_user_groups_beta), userId), json);
    }

    public void updateGroup(Organization group) {
        boolean exists = false;
        for (RelateGroup grp : groups) {
            if (grp.getGroupId().equals(group.getId())) {
                grp.setGroupName(group.getName());
                grp.setLogo(group.getLogo());
                grp.setIntro(group.getIntro());
                if (null != group.getGroMember()) {
                    grp.setGroRole(group.getGroMember().getGroRole());
                }
                exists = true;
                break;
            }
        }
        if (!exists) {
            RelateGroup relate = new RelateGroup();
            relate.setGroupId(group.getId());
            relate.setGroupName(group.getName());
            relate.setLogo(group.getLogo());
            relate.setIntro(group.getIntro());
            relate.setType(RelateGroup.Type.JOINED);
            relate.setUserId(userId);
            if (null != group.getGroMember()) {
                relate.setGroRole(group.getGroMember().getGroRole());
            }
            groups.add(relate);
        }
        saveGroups();
    }

    /**
     * 获取登录者在指定组织里的角色
     */
    public Role getGroupRole(String groupId) {
        if (StringHelper.isEmpty(groupId, true)) return null;
        for (RelateGroup group : groups) {
            if (group.getGroupId().equals(groupId)) {
                return group.getGroRole();
            }
        }
        return null;
    }

    /**
     * 当前登录用户的id、姓名、网易云信登录的token、api服务器的accessToken
     */
    public String userId, nimToken, accessToken, userName, userPhone;

    public ArrayList<String> groupIds = new ArrayList<>();

    private ArrayList<RelateGroup> groups = new ArrayList<>();

    public ArrayList<RelateGroup> getGroups() {
        return groups;
    }
}
