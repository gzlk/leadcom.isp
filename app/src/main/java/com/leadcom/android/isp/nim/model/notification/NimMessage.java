package com.leadcom.android.isp.nim.model.notification;

import com.leadcom.android.isp.model.Dao;
import com.leadcom.android.isp.model.Model;
import com.leadcom.android.isp.model.activity.Activity;
import com.leadcom.android.isp.model.archive.Archive;
import com.leadcom.android.isp.model.common.Message;
import com.leadcom.android.isp.model.organization.Organization;
import com.litesuits.orm.db.annotation.Column;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.assit.QueryBuilder;
import com.litesuits.orm.db.assit.WhereBuilder;
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
        String APPID = "appId";
        String APPTID = "appTid";
        String TOPICS = "appTopics";
    }

    public static void save(NimMessage msg) {
        if (msg.isSavable()) {
            //if (StringHelper.isEmpty(msg.getId(), true)) {
            msg.setId(msg.getUuid());
            //}
            new Dao<>(NimMessage.class).save(msg);
        }
    }

    public static void save(List<NimMessage> msgs) {
        for (NimMessage msg : msgs) {
            //if (StringHelper.isEmpty(msg.getId(), true)) {
            msg.setId(msg.getUuid());
            //}
        }
        new Dao<>(NimMessage.class).save(msgs);
    }

    public static void delete(String msgId) {
        Dao<NimMessage> dao = new Dao<>(NimMessage.class);
        NimMessage msg = dao.querySingle(Model.Field.Id, msgId);
        dao.delete(msg);
    }

    public static void deleteByUuid(String uuid) {
        WhereBuilder builder = new WhereBuilder(NimMessage.class)
                .where(Model.Field.UUID + " = ? ", uuid);
        new Dao<>(NimMessage.class).delete(builder);
    }

    public static NimMessage query(String msgId) {
        return new Dao<>(NimMessage.class).querySingle(Model.Field.Id, msgId);
    }

    public static List<NimMessage> query() {
        QueryBuilder<NimMessage> builder = new QueryBuilder<>(NimMessage.class).appendOrderDescBy(Model.Field.Id);
        return new Dao<>(NimMessage.class).query(builder);
    }

    public static void resetStatus(String tid) {
        Dao<NimMessage> dao = new Dao<>(NimMessage.class);
        List<NimMessage> list = dao.query(Activity.Field.NimId, tid);
        if (null != list && list.size() > 0) {
            for (NimMessage msg : list) {
                if (!msg.isRead()) {
                    // 未读状态设置为已读
                    msg.setStatus(Status.READ);
                }
            }
            dao.save(list);
        }
    }

    /**
     * 查找同一个活动的未处理邀请
     */
    public static List<NimMessage> queryNoHandledByTid(String tid) {
        QueryBuilder<NimMessage> builder = new QueryBuilder<>(NimMessage.class)
                .whereEquals(Activity.Field.NimId, tid)
                .whereAppendAnd()
                .whereLessThan(Activity.Field.Status, Status.HANDLED)
                .whereAppendAnd()
                .whereEquals(Archive.Field.Type, Type.ACTIVITY_INVITE);
        return new Dao<>(NimMessage.class).query(builder);
    }

    public static int getUnRead() {
        QueryBuilder<NimMessage> builder = new QueryBuilder<>(NimMessage.class)
                .whereLessThan(Activity.Field.Status, Status.READ).whereAppendAnd()
                .whereLessThan(Archive.Field.Type, Type.USER_ARCHIVE_LIKE);
        List<NimMessage> list = new Dao<>(NimMessage.class).query(builder);
        return null == list ? 0 : list.size();
    }

    public static void clear() {
        new Dao<>(NimMessage.class).clear();
    }

    // 活动的tid
    @Column(Activity.Field.NimId)
    private String tid;// 原有属性
    // 活动所属的组织
    @Column(Organization.Field.GroupId)
    private String groupId;// 原有属性
    @Column(PARAM.APPID)
    private String appId;// 原有属性
    @Column(PARAM.APPTID)
    private String appTid;// 原有属性
    // 结束的活动的所有议题的tid(用于取消这些议题里的未读消息)
    @Column(PARAM.TOPICS)
    private ArrayList<String> subTidList;// 原有属性

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
