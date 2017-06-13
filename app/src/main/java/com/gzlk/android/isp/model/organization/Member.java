package com.gzlk.android.isp.model.organization;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.reflect.TypeToken;
import com.gzlk.android.isp.cache.Cache;
import com.gzlk.android.isp.lib.Json;
import com.gzlk.android.isp.model.Dao;
import com.gzlk.android.isp.model.Model;
import com.gzlk.android.isp.model.activity.Activity;
import com.gzlk.android.isp.model.common.Leaguer;
import com.gzlk.android.isp.model.user.User;
import com.litesuits.orm.db.annotation.Column;
import com.litesuits.orm.db.annotation.Ignore;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.assit.QueryBuilder;

import java.util.List;

/**
 * <b>功能描述：</b>组织内成员<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/05/07 09:29 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/05/07 09:29 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */
@Table(Organization.Table.MEMBER)
public class Member extends Leaguer {

    public interface Code {
        //组织成员角色ID
        String GROUP_OWNER_ROLE_ID = "592fbf5e311dca041cd64953";            //群主
        String GROUP_MANAGER_ROLE_ID = "592fc0d0cb220a23640a0395";          //群管理员
        String GROUP_COMMON_MEMBER_ROLE_ID = "592fc373cb220a23640a039a";    //普通成员
        String GROUP_DOC_MANAGER_ROLE_ID = "592fce2dcb220a32d8c88f52";      //档案管理员
        //组织成员角色编码
        String GROUP_OWNER_ROLE_CODE = "7490b1dc542e4d4da72c1bfb5da453bc";        //群创建者
        String GROUP_MANAGER_ROLE_CODE = "f43c7aedfe22410ea885e707aa79ac6a";        //群管理员
        String GROUP_COMMON_MEMBER_ROLE_CODE = "a498fcf70a4c48178ee72726be47ce13";  //普通成员
        String GROUP_DOC_MANAGER_ROLE_CODE = "66a2932a2d5c435bb95a3dc42b435f4e";    //档案管理员
        //组织成员角色名称
        String GROUP_OWNER_ROLE_NAME = "群创建者";
        String GROUP_MANAGER_ROLE_NAME = "群管理员";
        String GROUP_COMMON_MEMBER_ROLE_NAME = "普通成员";
        String GROUP_DOC_MANAGER_ROLE_NAME = "档案管理员";
    }

    /**
     * 成员类别
     */
    public interface Type {
        /**
         * 组织成员
         */
        int GROUP = 1;
        /**
         * 小组成员
         */
        int SQUAD = 2;
        /**
         * 活动成员
         */
        int ACTIVITY = 3;
    }

    private static ExclusionStrategy strategy = new ExclusionStrategy() {
        @Override
        public boolean shouldSkipField(FieldAttributes f) {
            return f.getName().contains("groRole") || f.getName().contains("strategy");
        }

        @Override
        public boolean shouldSkipClass(Class<?> clazz) {
            return false;
        }
    };

    public static String toJson(Member member) {
        return Json.gson(strategy).toJson(member);
    }

    public static String toJson(List<Member> members) {
        return Json.gson(strategy).toJson(members, new TypeToken<List<Member>>() {
        }.getType());
    }

    /**
     * 查找我在指定组织里的角色
     */
    public static Member getGroupMemberOfMe(String groupId) {
        QueryBuilder<Member> builder = new QueryBuilder<>(Member.class)
                .whereEquals(Organization.Field.GroupId, groupId)
                .whereAppendAnd()
                .whereEquals(Field.UserId, Cache.cache().userId)
                .whereAppendAnd()
                .whereAppend(Organization.Field.SquadId + " IS NULL")
                .whereAppendAnd()
                .whereAppend(Activity.Field.ActivityId + " IS NULL");
        List<Member> members = new Dao<>(Member.class).query(builder);
        return (null == members || members.size() < 1) ? null : members.get(0);
    }

    /**
     * 查询指定用户是否在本地缓存中的某个组织或小组里
     */
    public static boolean isMemberInLocal(String userId, String groupId, String squadId) {
        if (isEmpty(userId) || isEmpty(groupId)) return false;
        QueryBuilder<Member> query = new QueryBuilder<>(Member.class)
                .whereEquals(Organization.Field.GroupId, groupId);
        if (isEmpty(squadId)) {
            query = query.whereAppendAnd().whereAppend(Organization.Field.SquadId + " IS NULL");
        } else {
            query = query.whereAppendAnd().whereEquals(Organization.Field.SquadId, squadId);
        }
        query = query.whereAppendAnd().whereEquals(Model.Field.UserId, userId);
        List<Member> list = new Dao<>(Member.class).query(query);
        return !(null == list || list.size() < 1);
    }

    /**
     * 查询指定用户是否在本地缓存中的某个活动里
     */
    public static boolean isMemberInLocal(String userId, String activityId) {
        if (isEmpty(userId) || isEmpty(activityId)) return false;

        QueryBuilder<Member> query = new QueryBuilder<>(Member.class)
                .whereEquals(Activity.Field.ActivityId, activityId)
                .whereAppendAnd().whereEquals(Model.Field.UserId, userId);
        List<Member> list = new Dao<>(Member.class).query(query);
        return !(null == list || list.size() < 1);
    }

    @Column(Organization.Field.GroupId)
    private String groupId;        //群体ID

    @Column(Organization.Field.SquadId)
    private String squadId;        //小组ID

    @Column(User.Field.Phone)
    private String phone;          //用户手机

    //活动Id
    @Column(Activity.Field.ActivityId)
    private String actId;
    @Ignore
    private Role groRole;

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getSquadId() {
        return squadId;
    }

    public void setSquadId(String squadId) {
        this.squadId = squadId;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getActId() {
        return actId;
    }

    public void setActId(String actId) {
        this.actId = actId;
    }

    public Role getGroRole() {
        if (null == groRole) {
            groRole = Role.getRole(getRoleId());
            if (null != groRole) {
                setRoleName(groRole.getRoleName());
            }
        }
        return groRole;
    }

    public void setGroRole(Role groRole) {
        this.groRole = groRole;
    }

    /**
     * 是否具有某个操作权限
     */
    public boolean hasOperation(String operation) {
        if (null == getGroRole()) return false;
        if (null == getGroRole().getPerList() || getGroRole().getPerList().size() < 1) return false;
        for (Permission per : getGroRole().getPerList()) {
            if (per.getPerCode().equals(operation)) {
                return true;
            }
        }
        return false;
    }

    /**
     * 是否是群管理员
     */
    public boolean isManager() {
        return null != getGroRole() && getGroRole().getId().equals(Code.GROUP_MANAGER_ROLE_ID);
    }

    /**
     * 是否是群主
     */
    public boolean isOwner() {
        return null != getGroRole() && getGroRole().getId().equals(Code.GROUP_OWNER_ROLE_ID);
    }

    /**
     * 是否普通成员
     */
    public boolean isMember() {
        return null == getGroRole() || getGroRole().getId().equals(Code.GROUP_COMMON_MEMBER_ROLE_ID);
    }

    /**
     * 是否档案管理员
     */
    public boolean isArchiveManager() {
        return null != getGroRole() && getGroRole().getId().equals(Code.GROUP_DOC_MANAGER_ROLE_ID);
    }

    /**
     * 是否可以修改组织属性
     */
    public boolean isGroupEditable() {
        return hasOperation(Operation.GROUP_PROPERTY);
    }

    /**
     * 是否可以审批档案
     */
    public boolean isApprovable() {
        return hasOperation(Operation.ARCHIVE_APPROVE);
    }

    /**
     * 是否可以编辑成员角色属性
     */
    public boolean isRoleEditable() {
        return hasOperation(Operation.MEMBER_ROLE);
    }

    /**
     * 是否可以删除用户
     */
    public boolean isMemberDeletable() {
        return hasOperation(Operation.MEMBER_DELETE);
    }

    /**
     * 是否可以添加小组
     */
    public boolean isSquadAddable() {
        return hasOperation(Operation.SQUAD_ADDABLE);
    }

    /**
     * 是否可以删除小组
     */
    public boolean isSquadDeletable() {
        return hasOperation(Operation.SQUAD_DELETE);
    }
}
