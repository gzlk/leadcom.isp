package com.leadcom.android.isp.model.organization;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.reflect.TypeToken;
import com.leadcom.android.isp.cache.Cache;
import com.leadcom.android.isp.helper.StringHelper;
import com.leadcom.android.isp.lib.Json;
import com.leadcom.android.isp.model.Dao;
import com.leadcom.android.isp.model.Model;
import com.leadcom.android.isp.model.activity.Activity;
import com.leadcom.android.isp.model.activity.topic.AppTopic;
import com.leadcom.android.isp.model.common.Leaguer;
import com.leadcom.android.isp.model.operation.ACTOperation;
import com.leadcom.android.isp.model.operation.GRPOperation;
import com.leadcom.android.isp.model.user.User;
import com.litesuits.orm.db.annotation.Column;
import com.litesuits.orm.db.annotation.Ignore;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.assit.QueryBuilder;
import com.litesuits.orm.db.assit.WhereBuilder;

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
        /**
         * 组织管理员Id
         */
        String GROUP_MANAGER_ROLE_ID = "592fc0d0cb220a23640a0395";          //组织管理员
        String GROUP_COMMON_MEMBER_ROLE_ID = "592fc373cb220a23640a039a";    //普通成员
        String GROUP_DOC_MANAGER_ROLE_ID = "592fce2dcb220a32d8c88f52";      //档案管理员
        String GROUP_SQUAD_MANAGER_ROLE_ID = "594a0d0031bbf76228c90e62";    //小组管理员
        //组织成员角色编码
        String GROUP_MANAGER_ROLE_CODE = "f43c7aedfe22410ea885e707aa79ac6a";        //组织管理员
        String GROUP_COMMON_MEMBER_ROLE_CODE = "a498fcf70a4c48178ee72726be47ce13";  //普通成员
        String GROUP_DOC_MANAGER_ROLE_CODE = "66a2932a2d5c435bb95a3dc42b435f4e";    //档案管理员
        String GROUP_SQUAD_MANAGER_ROLE_CODE = "b76a597176ba465e8fd306bb91cb7f3c";  //小组管理员
        //组织成员角色名称
        String GROUP_MANAGER_ROLE_NAME = "组织管理员";
        String GROUP_COMMON_MEMBER_ROLE_NAME = "普通成员";
        String GROUP_DOC_MANAGER_ROLE_NAME = "档案管理员";
        String GROUP_SQUAD_MANAGER_ROLE_NAME = "小组管理员";

        // 活动成员角色id
        /**
         * 活动管理员角色ID
         */
        String ACT_MANAGER_ROLE_ID = "58f8640fad41ef4aa0290624";
        /**
         * 活动成员角色ID
         */
        String ACT_MEMBER_ROLE_ID = "58f863fdad41ef4aa0290623";

        // 活动成员角色code
        /**
         * 活动管理员的角色code
         */
        String ACT_MANAGER_ROLE_CODE = "d72e64ece64b4362bba01f43e171319a";
        /**
         * 活动成员角色code
         */
        String ACT_MEMBER_ROLE_CODE = "50d6aaf585e049fd836fc85817c29aac";
        // 活动成员角色name
        /**
         * 活动管理员名称
         */
        String ACT_MANAGER_ROLE_NAME = "活动管理员";
        /**
         * 活动成员名称
         */
        String ACT_MEMBER_ROLE_NAME = "活动参与者";
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
        /**
         * 议题成员
         */
        int TOPIC = 4;
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
     * 查询指定用户是否在本地缓存中的某个组织或小组里
     */
    public static boolean isPhoneMemberOfGroupOrSquad(String phone, String groupId, String squadId) {
        if (isEmpty(phone) || isEmpty(groupId)) return false;
        QueryBuilder<Member> query = new QueryBuilder<>(Member.class)
                .whereEquals(Organization.Field.GroupId, groupId);
        if (isEmpty(squadId)) {
            query = query.whereAppendAnd().whereAppend(Organization.Field.SquadId + " IS NULL");
        } else {
            query = query.whereAppendAnd().whereEquals(Organization.Field.SquadId, squadId);
        }
        query = query.whereAppendAnd().whereEquals(User.Field.Phone, phone);
        List<Member> list = new Dao<>(Member.class).query(query);
        return (null != list && list.size() > 0);
    }

    public static void save(Member member) {
        if (null != member.getActRole()) {
            member.setActRoleId(member.getActRole().getId());
            Role.save(member.getActRole());
        }
        if (null != member.getGroRole()) {
            member.setGroRoleId(member.getGroRole().getId());
            Role.save(member.getGroRole());
        }
        new Dao<>(Member.class).save(member);
    }

    public static Member query(String memberId) {
        return new Dao<>(Member.class).query(memberId);
    }

    public static void remove(String memberId) {
        new Dao<>(Member.class).delete(memberId);
    }

    /**
     * 查询我加入的所有组织的以我为成员的列表
     */
    public static List<Member> getMyMembersOfJoinedGroups() {
        QueryBuilder<Member> query = new QueryBuilder<>(Member.class)
                .whereEquals(Model.Field.UserId, Cache.cache().userId)
                .whereAnd(Organization.Field.GroupId + " IS NOT NULL ")
                .whereAnd(Organization.Field.SquadId + " IS NULL")
                .orderBy(Model.Field.CreateDate);
        return new Dao<>(Member.class).query(query);
    }

    /**
     * 查询组织或小组的本地成员列表
     */
    public static List<Member> getMembersOfGroupOrSquad(String groupId, String squadId) {
        QueryBuilder<Member> query = new QueryBuilder<>(Member.class)
                .whereEquals(Organization.Field.GroupId, groupId);
        if (StringHelper.isEmpty(squadId)) {
            query = query.whereAppendAnd().whereAppend(Organization.Field.SquadId + " IS NULL");
        } else {
            query = query.whereAppendAnd().whereEquals(Organization.Field.SquadId, squadId);
        }
        query = query.whereAnd(Activity.Field.ActivityId + " IS NULL ")
                .whereAnd(AppTopic.Field.TopicId + " IS NULL ");
        return new Dao<>(Member.class).query(query);
    }

    /**
     * 获取指定活动的成员列表
     */
    public static List<Member> getMemberOfActivity(String activityId) {
        return new Dao<>(Member.class).query(Activity.Field.ActivityId, activityId);
    }

    /**
     * 我是否是指定tid的议题中的成员
     */
    public static boolean isMeMemberOfTopic(String tid) {
        AppTopic topic = AppTopic.queryByTid(tid);
        return null != topic && null != getMyMemberOfTopic(topic.getId());
    }

    /**
     * 查询我在指定议题id中的成员信息
     */
    private static Member getMyMemberOfTopic(String topicId) {
        QueryBuilder<Member> builder = new QueryBuilder<>(Member.class)
                .whereEquals(AppTopic.Field.TopicId, topicId)
                .whereAppendAnd()
                .whereEquals(Field.UserId, Cache.cache().userId);
        List<Member> list = new Dao<>(Member.class).query(builder);
        return (null == list || list.size() < 1) ? null : list.get(0);
    }

    /**
     * 从本地议题成员里删除指定议题的所有成员(退出议题、解散议题时用到)
     */
    public static void removeMemberOfTopicId(String topicId) {
        WhereBuilder builder = new WhereBuilder(Member.class)
                .where(AppTopic.Field.TopicId + " = ?", topicId);
        new Dao<>(Member.class).delete(builder);
    }

    @Column(Organization.Field.GroupId)
    private String groupId;        //群体ID

    @Column(Organization.Field.SquadId)
    private String squadId;        //小组ID

    @Column(User.Field.Duty)
    private String duty;

    @Column(Organization.Field.Rank)
    private int rank;

    //活动Id
    @Column(Activity.Field.ActivityId)
    private String actId;

    @Column(AppTopic.Field.TopicId)
    private String actTopicId;          //活动议题ID

    @Ignore
    private Role groRole;
    @Ignore
    private Role actRole;
    @Column(Activity.Field.ActivityRoleId)
    private String actRoleId;
    @Column(Organization.Field.GroupRoleId)
    private String groRoleId;

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

    public String getDuty() {
        return duty;
    }

    public void setDuty(String duty) {
        this.duty = duty;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public String getActId() {
        return actId;
    }

    public void setActId(String actId) {
        this.actId = actId;
    }

    public String getActTopicId() {
        return actTopicId;
    }

    public void setActTopicId(String actTopicId) {
        this.actTopicId = actTopicId;
    }

    public Role getGroRole() {
        if (null == groRole) {
            groRole = Role.getRole(groRoleId);
            if (null != groRole) {
                setRoleName(groRole.getRoleName());
            }
        }
        return groRole;
    }

    public void setGroRole(Role groRole) {
        this.groRole = groRole;
    }

    public String getGroRoleId() {
        return groRoleId;
    }

    public void setGroRoleId(String groRoleId) {
        this.groRoleId = groRoleId;
    }

    public Role getActRole() {
        if (null == actRole) {
            actRole = Role.getRole(actRoleId);
        }
        return actRole;
    }

    public void setActRole(Role actRole) {
        this.actRole = actRole;
    }

    public String getActRoleId() {
        return actRoleId;
    }

    public void setActRoleId(String actRoleId) {
        this.actRoleId = actRoleId;
    }

    /**
     * 是否具有某个操作权限
     */
    private boolean hasOperation(String operation) {
        return null != getGroRole() && getGroRole().hasOperation(operation);
//        if (null == getGroRole().getPerList() || getGroRole().getPerList().size() < 1) return false;
//        for (Permission per : getGroRole().getPerList()) {
//            if (per.getPerCode().equals(operation)) {
//                return true;
//            }
//        }
//        return false;
    }

    private boolean hasActivityOperation(String operation) {
        return null != getActRole() && getActRole().hasOperation(operation);
    }

    /**
     * 是否是群管理员
     */
    public boolean isManager() {
        return null != getGroRole() && getGroRole().getRolCode().equals(Code.GROUP_MANAGER_ROLE_CODE);
    }

    /**
     * 是否小组管理员
     */
    public boolean isSquadManager() {
        return null != getGroRole() && getGroRole().getRolCode().equals(Code.GROUP_SQUAD_MANAGER_ROLE_CODE);
    }

    /**
     * 是否是活动管理员
     */
    public boolean isActivityManager() {
        return null != getActRole() && !isEmpty(getActRole().getRolCode()) && getActRole().getRolCode().equals(Code.ACT_MANAGER_ROLE_CODE);
    }
    /*
     * 是否是群主
     */
//    public boolean isOwner() {
//        return null != getGroRole() && getGroRole().getId().equals(Code.GROUP_OWNER_ROLE_ID);
//    }

    /**
     * 是否普通成员
     */
    public boolean isMember() {
        return null == getGroRole() || getGroRole().getRolCode().equals(Code.GROUP_COMMON_MEMBER_ROLE_CODE);
    }

    /**
     * 是否档案管理员
     */
    public boolean isArchiveManager() {
        return null != getGroRole() && getGroRole().getRolCode().equals(Code.GROUP_DOC_MANAGER_ROLE_CODE);
    }

    /**
     * 是否可以修改组织属性
     */
    public boolean groupPropertyEditable() {
        return hasOperation(GRPOperation.GROUP_PROPERTY);
    }

    /**
     * 是否可以关联组织
     */
    public boolean groupAssociatable() {
        return hasOperation(GRPOperation.GROUP_ASSOCIATION);
    }

    /**
     * 是否可以编辑成员角色属性
     */
    public boolean memberRoleEditable() {
        return hasOperation(GRPOperation.MEMBER_ROLE);
    }

    /**
     * 是否可以删除用户
     */
    public boolean memberDeletable() {
        return hasOperation(GRPOperation.MEMBER_DELETE);
    }

    /**
     * 是否可以添加用户
     */
    public boolean memberInvitable() {
        return hasOperation(GRPOperation.MEMBER_ADD);
    }

    /**
     * 是否可以审批档案
     */
    public boolean archiveApprovable() {
        return hasOperation(GRPOperation.ARCHIVE_APPROVAL);
    }

    /**
     * 是否可以修改档案
     */
    public boolean archiveEditable() {
        return hasOperation(GRPOperation.ARCHIVE_EDIT);
    }

    /**
     * 是否可以删除档案
     */
    public boolean archiveDeletable() {
        return hasOperation(GRPOperation.ARCHIVE_DELETE);
    }

    /**
     * 是否可以添加小组
     */
    public boolean squadAddable() {
        return hasOperation(GRPOperation.SQUAD_ADD);
    }

    /**
     * 是否可以删除小组
     */
    public boolean squadDeletable() {
        return hasOperation(GRPOperation.SQUAD_DELETE);
    }

    /**
     * 是否可以编辑小组属性
     */
    public boolean squadEditable() {
        return hasOperation(GRPOperation.SQUAD_PROPERTY);
    }

    /**
     * 是否可以邀请成员加入小组
     */
    public boolean squadMemberInvitable() {
        return hasOperation(GRPOperation.SQUAD_MEMBER_INVITE);
    }

    /**
     * 是否可以删除小组成员
     */
    public boolean squadMemberDeletable() {
        return hasOperation(GRPOperation.SQUAD_MEMBER_DELETE);
    }

    /**
     * 是否可以编辑活动的属性
     */
    public boolean activeEditable() {
        return hasActivityOperation(ACTOperation.PROPERTY_EDIT);
    }

    /**
     * 是否可以结束活动
     */
    public boolean activityEndable() {
        return hasActivityOperation(ACTOperation.CLOSEABLE);
    }

    /**
     * 是否可以删除活动
     */
    public boolean activityDeletable() {
        return hasActivityOperation(ACTOperation.DELETABLE);
    }

    /**
     * 是否可以查看活动
     */
    public boolean activityCheckable() {
        return hasActivityOperation(ACTOperation.CHECKABLE);
    }

    /**
     * 是否可以添加成员
     */
    public boolean activityMemberAddable() {
        return hasActivityOperation(ACTOperation.MEMBER_ADDABLE);
    }

    /**
     * 是否可以删除成员
     */
    public boolean activityMemberDeletable() {
        return hasActivityOperation(ACTOperation.MEMBER_DELETABLE);
    }
}
