package com.gzlk.android.isp.nim.model.notification;

import com.gzlk.android.isp.etc.Utils;
import com.gzlk.android.isp.helper.StringHelper;
import com.gzlk.android.isp.model.Dao;
import com.gzlk.android.isp.model.Model;
import com.gzlk.android.isp.model.activity.Activity;
import com.gzlk.android.isp.model.archive.Archive;
import com.gzlk.android.isp.model.organization.Organization;
import com.litesuits.orm.db.annotation.Column;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.assit.QueryBuilder;
import com.netease.nimlib.sdk.msg.attachment.MsgAttachment;

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
public class NimMessage implements MsgAttachment {

    public interface PARAM {
        String TABLE = "notification";
        String HANDLED = "handled";
        String HANDLE_STATE = "handleState";
        String APPID = "appId";
        String APPTID = "appTid";
    }

    @Override
    public boolean equals(Object object) {
        return null != object && (getClass() == object.getClass()) && (object instanceof NimMessage) && equals((NimMessage) object);
    }

    public boolean equals(NimMessage msg) {
        return null != msg && msg.getId() == getId();
    }

    public static void save(NimMessage msg) {
        new Dao<>(NimMessage.class).save(msg);
    }

    public static void save(List<NimMessage> msgs) {
        new Dao<>(NimMessage.class).save(msgs);
    }

    public static void delete(long msgId) {
        Dao<NimMessage> dao = new Dao<>(NimMessage.class);
        NimMessage msg = dao.querySingle(Model.Field.Id, msgId);
        dao.delete(msg);
    }

    public static NimMessage query(long msgId) {
        return new Dao<>(NimMessage.class).querySingle(Model.Field.Id, msgId);
    }

    public static List<NimMessage> query() {
        QueryBuilder<NimMessage> builder = new QueryBuilder<>(NimMessage.class)
                .appendOrderDescBy(Model.Field.Id);
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

    /**
     * 自定义消息类型
     */
    public interface Type {
        /**
         * 新成员申请加入组织
         */
        int JOIN_TO_GROUP = 4;
        /**
         * 批准新成员加入
         */
        int APPROVE_JOIN_GROUP = 6;
        /**
         * 不批准新成员加入
         */
        int DISAPPROVE_JOIN_GROUP = 7;
        /**
         * 邀请新成员加入组织
         */
        int INVITE_TO_GROUP = 8;
        /**
         * 新成员同意加入组织
         */
        int AGREE_TO_GROUP = 9;
        /**
         * 新成员不同意加入组织
         */
        int DISAGREE_TO_GROUP = 10;
        /**
         * 活动成员邀请
         */
        int ACTIVITY_INVITE = 16;
        /**
         * 小组成员邀请小组外人员加入小组
         */
        int INVITE_TO_SQUAD = 19;
        /**
         * 被邀请者同意加入小组
         */
        int AGREE_TO_SQUAD = 20;
        /**
         * 被邀请者拒绝加入小组
         */
        int DISAGREE_TO_SQUAD = 21;
        /**
         * 活动通知
         */
        int ACTIVITY_NOTIFICATION = 22;
        /**
         * 自定义系统通知
         */
        int SYSTEM_NOTIFICATION = 23;
        /**
         * 邀请到小组（仅通知）
         */
        int INVITE_TO_SQUAD_ALERT = 24;
        /**
         * 邀请活动议题成员
         */
        int INVITE_TO_TOPIC = 27;
        /**
         * 退出活动议题
         */
        int EXIT_TOPIC = 28;
        /**
         * 踢出活动议题成员
         */
        int KICK_OUT_TOPIC = 29;
        /**
         * 结束活动议题
         */
        int END_TOPIC = 30;
    }

    /**
     * 获取类型文字
     */
    public static String getType(int type) {
        switch (type) {
            case Type.JOIN_TO_GROUP:
                return "申请加入组织";
            case Type.INVITE_TO_GROUP:
                return "邀请您加入组织";
            case Type.ACTIVITY_INVITE:
                return "邀请您加入活动";
            case Type.INVITE_TO_SQUAD:
                return "邀请您加入小组";
            case Type.ACTIVITY_NOTIFICATION:
                return "活动通知";
            case Type.SYSTEM_NOTIFICATION:
                return "系统通知";
            case Type.INVITE_TO_SQUAD_ALERT:
                return "加入小组";
            case Type.INVITE_TO_TOPIC:
                return "邀请您加入议题";
            case Type.EXIT_TOPIC:
                return "退出议题";
            case Type.KICK_OUT_TOPIC:
                return "踢出议题";
            case Type.END_TOPIC:
                return "结束议题";
            default:
                return StringHelper.format("不晓得是什么通知(%d)", type);
        }
    }

    @Column(Model.Field.Id)
    private long id = Utils.timestamp();
    // 自定义消息类型
    @Column(Archive.Field.Type)
    private int type;// 原有属性
    // 自定义消息标题
    @Column(Archive.Field.Title)
    private String msgTitle;// 原有属性
    // 自定义消息内容
    @Column(Archive.Field.Content)
    private String msgContent;// 原有属性
    // 自定义消息id
    @Column(Model.Field.UUID)
    private String uuid;// 原有属性
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

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getMsgTitle() {
        return msgTitle;
    }

    public void setMsgTitle(String msgTitle) {
        this.msgTitle = msgTitle;
    }

    public String getMsgContent() {
        return msgContent;
    }

    public void setMsgContent(String msgContent) {
        this.msgContent = msgContent;
    }

    public String getUuid() {
        return uuid;
    }

    public void setUuid(String uuid) {
        this.uuid = uuid;
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

    @Override
    public String toJson(boolean b) {
        return NimMessageParser.packData(this);
    }
}
