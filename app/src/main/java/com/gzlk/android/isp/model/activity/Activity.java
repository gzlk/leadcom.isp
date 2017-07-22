package com.gzlk.android.isp.model.activity;

import com.gzlk.android.isp.model.Dao;
import com.gzlk.android.isp.model.Model;
import com.gzlk.android.isp.model.archive.Archive;
import com.gzlk.android.isp.model.common.Attachment;
import com.gzlk.android.isp.model.organization.Member;
import com.gzlk.android.isp.model.organization.Organization;
import com.litesuits.orm.db.annotation.Column;
import com.litesuits.orm.db.annotation.Ignore;
import com.litesuits.orm.db.annotation.Table;

import java.util.ArrayList;
import java.util.List;

/**
 * <b>功能描述：</b>活动<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/05/25 22:21 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/05/25 22:21 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */
@Table(Activity.Table.ACTIVITY)
public class Activity extends Model {

    public interface Table {
        String ACTIVITY = "activity";
        String ACTIVITY_ARCHIVE = "activityArchive";
        String ASSOCIATOR = "associator";
        String CHARACTER = "character";
        String LABEL = "activityLabel";
        String NOTICE = "activityNotice";
        String SIGN = "activitySign";
        String SIGN_RECORD = "activitySignRecord";
        String VOTE = "activityVote";
        String VOTE_ITEM = "activityVoteItem";
        String VOTE_RECORD = "activityVoteRecord";
        String SURVEY = "activitySurvey";
        String SURVEY_ITEM = "activitySurveyItem";
    }

    public interface Field {
        String UseMessage = "useMessage";
        String Status = "status";
        String OpenStatus = "authPublic";
        String NimId = "nimId";
        String BeginDate = "beginDate";
        String Site = "site";
        String Sequence = "sequence";
        String ActivityId = "activityId";
        String ActivityName = "activityName";
        String ActivityImage = "activityImage";
        String IsLocalStorage="isLocalStorage";
        String UsedTimes="usedTimes";
    }

    /**
     * 活动状态
     */
    public interface Status {
        /**
         * 开放中
         */
        int ACTIVE = 1;
        /**
         * 已结束
         */
        int ENDED = 2;
    }

    /**
     * 开放形式
     */
    public interface OpenStatus {
        /**
         * 未设置
         */
        int NONE = 0;
        /**
         * 向所有人开放
         */
        int OPEN = 1;
        /**
         * 只向组织内开放
         */
        int GROUP = 2;
    }

    /**
     * 从本地缓存中查找指定活动成员列表
     */
    public static List<Member> getMembers(String activityId) {
        return new Dao<>(Member.class).query(Field.ActivityId, activityId);
    }

    /**
     * 根据tid反查活动详情
     */
    public static Activity getByTid(String tid) {
        return new Dao<>(Activity.class).querySingle(Field.NimId, tid);
    }

    //标题
    @Column(Archive.Field.Title)
    private String title;
    //内容
    @Column(Organization.Field.Introduction)
    private String intro;
    //组织id
    @Column(Organization.Field.GroupId)
    private String groupId;
    //创建时间
    @Column(Model.Field.CreateDate)
    private String createDate;
    //最后修改时间
    @Column(Organization.Field.ModifyDate)
    private String modifyDate;
    //是否使用短信通知
    @Column(Field.UseMessage)
    private boolean useMsg;
    //宣传照h
    @Column(Archive.Field.Cover)
    private String cover;
    //状态：1.活跃 2.结束
    @Column(Field.Status)
    private int status;
    //活动公开状态 1.向所有人公开 2。关闭（只向组织内公开） 参见ActivityConstant中的状态定义
    @Column(Archive.Field.AuthPublic)
    private int authPublic;
    //标签
    @Column(Archive.Field.Label)
    private ArrayList<String> label;
    @Ignore
    private ArrayList<Label> labels;
    //创建者
    @Column(Archive.Field.CreatorId)
    private String creatorId;
    //创建者名字
    @Column(Archive.Field.CreatorName)
    private String creatorName;
    //历康活动对应于网易云的聊天群的tid
    @Column(Field.NimId)
    private String tid;
    //邀请的成员id列表（可能是尚未加入活动）,内容格式如["aaa","bbb"]
    //@Column(Field.MemberIdArray)
    //private ArrayList<String> memberIdArray;
    //邀请的成员姓名列表（可能是尚未加入活动）,内容格式如["aaa","bbb"]
    //@Column(Field.MemberNameArray)
    //private ArrayList<String> memberNameArray;
    //所属组织
    @Ignore
    private Organization group;

    //活动开始时间
    @Column(Field.BeginDate)
    private String beginDate;
    //活动地点
    @Column(Field.Site)
    private String site;
    @Ignore
    private int unreadNum;
    //活动附件
    @Ignore
    private ArrayList<Attachment> attachList;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getCreateDate() {
        if (isEmpty(createDate)) {
            createDate = DFT_DATE;
        }
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getModifyDate() {
        return modifyDate;
    }

    public void setModifyDate(String modifyDate) {
        this.modifyDate = modifyDate;
    }

    public boolean isUseMsg() {
        return useMsg;
    }

    public void setUseMsg(boolean useMsg) {
        this.useMsg = useMsg;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getAuthPublic() {
        return authPublic;
    }

    public void setAuthPublic(int authPublic) {
        this.authPublic = authPublic;
    }

    public ArrayList<String> getLabel() {
        fetchingLabels();
        return label;
    }

    public void setLabel(ArrayList<String> label) {
        this.label = label;
    }

    private void fetchingLabels() {
        if (null == labels) {
            labels = (ArrayList<Label>) Label.getLabelsById(label);
        }
    }

    public ArrayList<Label> getLabels() {
        fetchingLabels();
        return labels;
    }

    public void setLabels(ArrayList<Label> labels) {
        this.labels = labels;
    }

    public String getCreatorId() {
        if (isEmpty(creatorId)) {
            creatorId = "";
        }
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public Organization getGroup() {
        if (null == group) {
            group = new Dao<>(Organization.class).query(groupId);
        }
        return group;
    }

    public void setGroup(Organization group) {
        this.group = group;
    }

    public String getBeginDate() {
        if (isEmpty(beginDate)) {
            beginDate = DFT_DATE;
        }
        return beginDate;
    }

    public void setBeginDate(String beginDate) {
        this.beginDate = beginDate;
    }

    public String getSite() {
        if (isEmpty(site)) {
            site = "未设置地址";
        }
        return site;
    }

    public void setSite(String site) {
        this.site = site;
    }

    public int getUnreadNum() {
        return unreadNum;
    }

    public void setUnreadNum(int unreadNum) {
        this.unreadNum = unreadNum;
    }

    public ArrayList<Attachment> getAttachList() {
        if (null == attachList) {
            attachList = new ArrayList<>();
            List<Attachment> list = Attachment.getAttachments(getId());
            if (null != list) {
                attachList.addAll(list);
            }
        }
        return attachList;
    }

    public void setAttachList(ArrayList<Attachment> attachList) {
        this.attachList = attachList;
    }
}
