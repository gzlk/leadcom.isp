package com.leadcom.android.isp.nim.model.notification;

import com.leadcom.android.isp.helper.StringHelper;
import com.leadcom.android.isp.model.Dao;
import com.leadcom.android.isp.model.Model;
import com.leadcom.android.isp.model.activity.Activity;
import com.leadcom.android.isp.model.archive.Archive;
import com.leadcom.android.isp.model.common.Message;
import com.leadcom.android.isp.model.organization.Organization;
import com.litesuits.orm.db.annotation.Column;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.assit.QueryBuilder;
import com.netease.nimlib.sdk.msg.attachment.MsgAttachment;

import java.util.ArrayList;
import java.util.List;

/**
 * <b>功能描述：</b>自定义网易云信消息<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/05/18 23:18 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/05/18 23:18 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

@Table(NimMessage.PARAM.TABLE)
public class NimMessage extends Message implements MsgAttachment {

    public interface PARAM {
        String TABLE = "notification";
        String HANDLED = "handled";
        String HANDLE_STATE = "handleState";
        String APPID = "appId";
        String APPTID = "appTid";
        String TOPICS = "appTopics";
    }

    @Override
    public boolean equals(Object object) {
        return null != object && (getClass() == object.getClass()) && (object instanceof NimMessage) && equals((NimMessage) object);
    }

    public boolean equals(NimMessage msg) {
        return null != msg && !isEmpty(msg.getId()) && msg.getId().equals(getId());
    }

    public static void save(NimMessage msg) {
        if (msg.isSavable()) {
            if (StringHelper.isEmpty(msg.getId(), true)) {
                msg.setId(msg.getUuid());
            }
            new Dao<>(NimMessage.class).save(msg);
        }
    }

    public static void save(List<NimMessage> msgs) {
        for (NimMessage msg : msgs) {
            if (StringHelper.isEmpty(msg.getId(), true)) {
                msg.setId(msg.getUuid());
            }
        }
        new Dao<>(NimMessage.class).save(msgs);
    }

    public static void delete(String msgId) {
        Dao<NimMessage> dao = new Dao<>(NimMessage.class);
        NimMessage msg = dao.querySingle(Model.Field.Id, msgId);
        dao.delete(msg);
    }

    public static NimMessage query(String msgId) {
        return new Dao<>(NimMessage.class).querySingle(Model.Field.Id, msgId);
    }

    public static List<NimMessage> query() {
        QueryBuilder<NimMessage> builder = new QueryBuilder<>(NimMessage.class).appendOrderDescBy(Model.Field.Id);
        return new Dao<>(NimMessage.class).query(builder);
    }

    /**
     * 查找同一个活动的未处理邀请
     */
    public static List<NimMessage> queryNoHandledByTid(String tid) {
        QueryBuilder<NimMessage> builder = new QueryBuilder<>(NimMessage.class)
                .whereEquals(Activity.Field.NimId, tid)
                .whereAppendAnd()
                .whereEquals(PARAM.HANDLED, false)
                .whereAppendAnd()
                .whereEquals(Archive.Field.Type, Type.ACTIVITY_INVITE);
        return new Dao<>(NimMessage.class).query(builder);
    }

    public static int getUnHandled() {
        List<NimMessage> msgs = new Dao<>(NimMessage.class).query(NimMessage.PARAM.HANDLED, false);
        return null == msgs ? 0 : msgs.size();
    }

    public static void clear() {
        new Dao<>(NimMessage.class).clear();
    }

    /**
     * 查看本条消息是否可以保存
     */
    public boolean isSavable() {
        return type < Type.USER_ARCHIVE_LIKE || type > Type.MOMENT_COMMENT;
    }

    /**
     * 自定义消息类型
     */
    public interface Type {
        /**
         * 用户聊天短消息
         */
        int USER_CHAT = 1;
        /**
         * 活动回复消息
         */
        int ACTIVITY_REPLY = 2;
        /**
         * 事件回复消息
         */
        int EVENT_REPLY = 3;
        /**
         * 新成员申请加入组织
         */
        int GROUP_JOIN = 4;
        /**
         * 批准新成员加入
         */
        int GROUP_JOIN_APPROVE = 6;
        /**
         * 不批准新成员加入
         */
        int GROUP_JOIN_DISAPPROVE = 7;
        /**
         * 邀请新成员加入组织
         */
        int GROUP_INVITE = 8;
        /**
         * 新成员同意加入组织
         */
        int GROUP_INVITE_AGREE = 9;
        /**
         * 新成员不同意加入组织
         */
        int GROUP_INVITE_DISAGREE = 10;
        /**
         * 被踢出组织
         */
        int GROUP_KICK_OUT = 11;
        /**
         * 主动加入活动
         */
        int ACTIVITY_JOIN = 14;
        /**
         * 主动加入活动的回复
         */
        int ACTIVITY_JOIN_REPLY = 15;
        /**
         * 活动成员邀请
         */
        int ACTIVITY_INVITE = 16;
        /**
         * 活动邀请的回复
         */
        int ACTIVITY_INVITE_REPLY = 17;
        /**
         * 被踢出活动
         */
        int ACTIVITY_KICK_OUT = 18;
        /**
         * 小组成员邀请小组外人员加入小组
         */
        int SQUAD_INVITE = 19;
        /**
         * 被邀请者同意加入小组
         */
        int SQUAD_INVITE_AGREE = 20;
        /**
         * 被邀请者拒绝加入小组
         */
        int SQUAD_INVITE_DISAGREE = 21;
        /**
         * 活动通知(对所有活动成员)
         */
        int ACTIVITY_ALERT_ALL = 22;
        /**
         * 活动通知(对选定成员)
         */
        int ACTIVITY_ALERT_SELECTED = 23;
        /**
         * 邀请到小组（仅通知）
         */
        int SQUAD_INVITE_ALERT = 24;
        /**
         * 成员退出活动
         */
        int ACTIVITY_EXIT = 25;
        /**
         * 活动结束通知
         */
        int ACTIVITY_END = 26;
        /**
         * 邀请活动议题成员
         */
        int TOPIC_INVITE = 27;
        /**
         * 退出活动议题
         */
        int TOPIC_EXIT = 28;
        /**
         * 踢出活动议题成员
         */
        int TOPIC_KICK_OUT = 29;
        /**
         * 结束活动议题
         */
        int TOPIC_END = 30;
        /**
         * 个人档案点赞
         */
        int USER_ARCHIVE_LIKE = 31;
        /**
         * 个人档案评论
         */
        int USER_ARCHIVE_COMMENT = 32;
        /**
         * 组织档案点赞
         */
        int GROUP_ARCHIVE_LIKE = 33;
        /**
         * 组织档案评论
         */
        int GROUP_ARCHIVE_COMMENT = 34;
        /**
         * 个人动态点赞
         */
        int MOMENT_LIKE = 35;
        /**
         * 个人动态评论
         */
        int MOMENT_COMMENT = 36;
    }

    /**
     * 获取类型文字
     */
    public static String getType(int type) {
        switch (type) {
            case Type.GROUP_JOIN:
                return "申请加入组织";
            case Type.GROUP_INVITE:
                return "邀请您加入组织";
            case Type.ACTIVITY_INVITE:
                return "邀请您加入活动";
            case Type.ACTIVITY_END:
                return "活动结束";
            case Type.SQUAD_INVITE:
                return "邀请您加入小组";
            case Type.ACTIVITY_ALERT_ALL:
                return "活动通知";
            case Type.ACTIVITY_ALERT_SELECTED:
                return "系统通知";
            case Type.SQUAD_INVITE_ALERT:
                return "加入小组";
            case Type.TOPIC_INVITE:
                return "邀请您加入议题";
            case Type.TOPIC_EXIT:
                return "退出议题";
            case Type.TOPIC_KICK_OUT:
                return "踢出议题";
            case Type.TOPIC_END:
                return "结束议题";
            default:
                return StringHelper.format("不晓得是什么通知(%d)", type);
        }
    }

    // 推送类型(0.点对点自定义推送,1.群消息自定义推送)
    @Column(Archive.Field.Type)
    private int type;// 原有属性
    // 活动的tid
    @Column(Activity.Field.NimId)
    private String tid;// 原有属性
    // 活动所属的组织
    @Column(Organization.Field.GroupId)
    private String groupId;// 原有属性
    @Column(PARAM.APPID)
    private String appId;
    @Column(PARAM.APPTID)
    private String appTid;
    // 是否已处理
    @Column(PARAM.HANDLED)
    private boolean handled;
    // 处理状态，true=已处理，false=已拒绝
    @Column(PARAM.HANDLE_STATE)
    private boolean handleState;
    // 结束的活动的所有议题的tid(用于取消这些议题里的未读消息)
    @Column(PARAM.TOPICS)
    private ArrayList<String> subTidList;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getAppId() {
        return appId;
    }

    public void setAppId(String appId) {
        this.appId = appId;
    }

    public String getAppTid() {
        return appTid;
    }

    public void setAppTid(String appTid) {
        this.appTid = appTid;
    }

    public boolean isHandled() {
        return handled;
    }

    public void setHandled(boolean handled) {
        this.handled = handled;
    }

    public boolean isHandleState() {
        return handleState;
    }

    public void setHandleState(boolean handleState) {
        this.handleState = handleState;
    }

    public ArrayList<String> getSubTidList() {
        if (null == subTidList) {
            subTidList = new ArrayList<>();
        }
        return subTidList;
    }

    public void setSubTidList(ArrayList<String> subTidList) {
        this.subTidList = subTidList;
    }

    @Override
    public String toJson(boolean b) {
        return NimMessageParser.packData(this);
    }
}
