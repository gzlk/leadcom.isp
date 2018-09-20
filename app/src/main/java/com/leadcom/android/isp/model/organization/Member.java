package com.leadcom.android.isp.model.organization;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.reflect.TypeToken;
import com.leadcom.android.isp.cache.Cache;
import com.leadcom.android.isp.helper.StringHelper;
import com.leadcom.android.isp.lib.Json;
import com.leadcom.android.isp.model.Dao;
import com.leadcom.android.isp.model.Model;
import com.leadcom.android.isp.model.common.Leaguer;
import com.leadcom.android.isp.model.operation.GRPOperation;
import com.leadcom.android.isp.model.user.User;
import com.litesuits.orm.db.annotation.Column;
import com.litesuits.orm.db.annotation.Ignore;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.assit.QueryBuilder;

import java.util.ArrayList;
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

        //组织成员角色编码
        String GROUP_ROLE_CODE_MANAGER = "f43c7aedfe22410ea885e707aa79ac6a";        //组织管理员
        String GROUP_ROLE_CODE_COMMON_MEMBER = "a498fcf70a4c48178ee72726be47ce13";  //普通成员
        String GROUP_ROLE_CODE_DOC_MANAGER = "66a2932a2d5c435bb95a3dc42b435f4e";    //档案管理员
        String GROUP_ROLE_CODE_SQUAD_MANAGER = "b76a597176ba465e8fd306bb91cb7f3c";  //小组管理员

        // 活动成员角色code
        /**
         * 活动管理员的角色code
         */
        String ACT_MANAGER_ROLE_CODE = "d72e64ece64b4362bba01f43e171319a";
        /**
         * 活动成员角色code
         */
        String ACT_MEMBER_ROLE_CODE = "50d6aaf585e049fd836fc85817c29aac";

        /**
         * 群聊管理员角色code
         */
        String TEAM_MANAGER_ROLE_CODE = "380d01a50c0b4e2683d3f10035bd556e";
        /**
         * 群聊普通成员角色code
         */
        String TEAM_MEMBER_ROLE_CODE = "f93686b676b04a9aa4dced9a6efbe9d9";
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
        /**
         * 群聊成员
         */
        int TEAM = 5;
    }

    /**
     * 成员的活动参加状态
     */
    public interface ActivityStatus {
        /**
         * 已报名参加
         */
        int JOINED = 1;
        /**
         * 已报名请假
         */
        int LEAVE = 2;
        /**
         * 缺席
         */
        int ABSENT = 3;
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
        return Json.gson().toJson(members, new TypeToken<List<Member>>() {
        }.getType());
    }

    public static ArrayList<Member> fromJsonArray(String array) {
        return Json.gson().fromJson(array, new TypeToken<ArrayList<Member>>() {
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
        return new Dao<>(Member.class).query(query);
    }

    @Column(Organization.Field.GroupId)
    private String groupId;        //群体ID

    @Column(Organization.Field.SquadId)
    private String squadId;        //小组ID

    @Column(User.Field.Duty)
    private String duty;

    @Column(Organization.Field.Rank)
    private int rank;

    @Ignore
    private Role groRole;
    @Ignore
    private Role actRole;
    @Column(Organization.Field.GroupRoleId)
    private String groRoleId;

    @Ignore
    private Role commRole;// 角色

    // 组织活动相关
    private String groActivityId;
    private String status;

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

    public Role getGroRole() {
        if (null == groRole) {
            groRole = Role.getRoleById(groRoleId);
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

    public String getGroActivityId() {
        return groActivityId;
    }

    public void setGroActivityId(String groActivityId) {
        this.groActivityId = groActivityId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    /**
     * 是否参加了活动
     */
//    public boolean isActJoined() {
//        return status == ActivityStatus.JOINED;
//    }

    /**
     * 是否请假了
     */
//    public boolean isActLeaved() {
//        return status == ActivityStatus.LEAVE;
//    }

    /**
     * 是否缺席了活动
     */
//    public boolean isActAbsent() {
//        return status == ActivityStatus.ABSENT;
//    }

    public static ExclusionStrategy getStrategy() {
        return strategy;
    }

    public static void setStrategy(ExclusionStrategy strategy) {
        Member.strategy = strategy;
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

    /**
     * 是否是群管理员
     */
    public boolean isGroupManager() {
        return null != getGroRole() && getGroRole().isManager();
    }

    /**
     * 是否小组管理员
     */
    public boolean isSquadManager() {
        return null != getGroRole() && getGroRole().isSquadManager();
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
    public boolean isGroupMember() {
        return null == getGroRole() || getGroRole().isCommonMember();
    }

    /**
     * 是否档案管理员
     */
    public boolean isArchiveManager() {
        return null != getGroRole() && getGroRole().isArchiveManager();
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
}
