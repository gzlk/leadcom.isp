package com.leadcom.android.isp.model.activity.vote;

import com.google.gson.reflect.TypeToken;
import com.leadcom.android.isp.lib.Json;
import com.leadcom.android.isp.model.Dao;
import com.leadcom.android.isp.model.Model;
import com.leadcom.android.isp.model.activity.Activity;
import com.leadcom.android.isp.model.archive.Archive;
import com.leadcom.android.isp.model.common.TalkTeam;
import com.litesuits.orm.db.annotation.Column;
import com.litesuits.orm.db.annotation.Ignore;
import com.litesuits.orm.db.annotation.Table;

import java.util.ArrayList;

/**
 * <b>功能描述：</b>投票<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/05/25 23:21 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/05/25 23:21 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */
@Table(Activity.Table.VOTE)
public class AppVote extends Model {

    public interface Field {
        String BeginDate = "beginDate";
        String EndDate = "endDate";
        String CreatorHeadPhoto = "creatorHeadPhoto";
        String End = "end";
        String VoteId = "voteId";
        String VoteSetupId = "voteSetupId";
        String MaxSelectable = "maxSelectable";
        String Num = "num";
        String Archived = "archived";
        String VoteItemId = "voteItemIds";
        String Anonymity = "anonymity";
    }

    /**
     * 投票类型
     */
    public interface Type {
        /**
         * 单选投票
         */
        int SINGLE = 0;
        /**
         * 多选投票且至多只能选择两项
         */
        int TWICE = 1;
        /**
         * 多选投票无限制选择
         */
        int UNLIMITED = 2;
    }

    /**
     * 投票状态
     */
    public interface Status {
        /**
         * 未投票
         */
        int NOT_VOTE = 1;
        /**
         * 已投票
         */
        int HAS_VOTED = 2;
        /**
         * 已弃权
         */
        int REFUSED = 3;
    }

    /**
     * 投票结束状态
     */
    public interface VoteEnd {
        /**
         * 已结束
         */
        int ENDED = 1;
        /**
         * 未结束
         */
        int NOT_END = 0;
    }

    public static String toJson(AppVote appVote) {
        return Json.gson().toJson(appVote, new TypeToken<AppVote>() {
        }.getType());
    }

    public static AppVote fromJson(String json) {
        if (isEmpty(json)) return null;
        return Json.gson().fromJson(json, new TypeToken<AppVote>() {
        }.getType());
    }

    // 群聊相关属性
    //沟通ID
    @Column(TalkTeam.Field.TeamId)
    private String commId;
    //云信高级群ID
    @Column(Activity.Field.NimId)
    private String tid;

    //活动Id
    @Column(Activity.Field.ActivityId)
    private String actId;
    //标题
    @Column(Archive.Field.Title)
    private String title;
    //描述
    @Column(Archive.Field.Content)
    private String content;
    //投票开始时间
    @Column(Field.BeginDate)
    private String beginTime;
    //创建时间
    @Column(Model.Field.CreateDate)
    private String createDate;
    //投票结束时间
    @Column(Field.EndDate)
    private String endDate;
    //创建者id
    @Column(Archive.Field.CreatorId)
    private String creatorId;
    //创建者名字
    @Column(Archive.Field.CreatorName)
    private String creatorName;
    //创建者用户头像
    @Column(Field.CreatorHeadPhoto)
    private String creatorHeadPhoto;
    // 最大可选数量
    @Column(Field.MaxSelectable)
    private int maxSelectable;
    //是否不记名(0.不记名,1.记名)
    @Column(Field.Anonymity)
    private int anonymity;
    //公开范围(0.不公开,1.公开)
    @Column(Archive.Field.AuthPublic)
    private int authPublic;
    //是否已经结束
    @Column(Field.End)
    private int end;
    //是否已存档(0.未存档,1.已存档)
    @Column(Field.Archived)
    private int archive;
    @Ignore
    private int type;
    @Ignore
    private int notifyBeginTime;
    //当前用户的投票记录
    @Ignore
    private AppVoteRecord actVote;
    @Ignore
    private ArrayList<String> itemContentList;
    @Ignore
    private ArrayList<AppVoteItem> actVoteItemList;
    @Ignore
    private ArrayList<AppVoteRecord> actVoteList;
    @Ignore
    private ArrayList<AppVoteItem> commVoteItemList;
    @Ignore
    private ArrayList<AppVoteRecord> commVoteRecordList;

    public void saveVoteItems() {
        if (null != actVoteItemList) {
            new Dao<>(AppVoteItem.class).save(actVoteItemList);
        }
    }

    /**
     * 保存当前用户的投票记录和所有投票列表
     */
    public void saveVoteRecords() {
        Dao<AppVoteRecord> dao = new Dao<>(AppVoteRecord.class);
        if (null != actVote) {
            dao.save(actVote);
        }
        if (null != actVoteList) {
            dao.save(actVoteList);
        }
    }

    /**
     * 投票类型
     *
     * @return <ul>
     * <li>0=单选</li>
     * <li>1=多选（2项）</li>
     * <li>2=多选无限制选项</li>
     * </ul>
     */
    public int getType() {
        switch (maxSelectable) {
            case 1:
                // 单选
                type = Type.SINGLE;
                break;
            case 2:
                // 多选，最多两项
                type = Type.TWICE;
                break;
            default:
                // 多选，无限制
                type = Type.UNLIMITED;
                break;
        }
        return this.type;
    }

    public void setType(int type) {
        this.type = type;
        switch (type) {
            case Type.SINGLE:
                maxSelectable = 1;
                break;
            case Type.TWICE:
                maxSelectable = 2;
                break;
            case Type.UNLIMITED:
                maxSelectable = 0;
                break;
        }
    }

    public String getCommId() {
        return commId;
    }

    public void setCommId(String commId) {
        this.commId = commId;
    }

    public String getTid() {
        return tid;
    }

    public void setTid(String tid) {
        this.tid = tid;
    }

    public String getActId() {
        return actId;
    }

    public void setActId(String actId) {
        this.actId = actId;
    }

    public int getMaxSelectable() {
        if (0 >= maxSelectable) {
            // 默认可选择1项
            maxSelectable = 1;
        }
        return maxSelectable;
    }

    public void setMaxSelectable(int maxSelectable) {
        this.maxSelectable = maxSelectable;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(String beginTime) {
        this.beginTime = beginTime;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }

    public String getCreatorName() {
        if (isEmpty(creatorName)) {
            creatorName = NO_NAME;
        }
        return creatorName;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }

    public String getCreatorHeadPhoto() {
        return creatorHeadPhoto;
    }

    public void setCreatorHeadPhoto(String creatorHeadPhoto) {
        this.creatorHeadPhoto = creatorHeadPhoto;
    }

    public int getAnonymity() {
        return anonymity;
    }

    public void setAnonymity(int anonymity) {
        this.anonymity = anonymity;
    }

    public int getAuthPublic() {
        return authPublic;
    }

    public void setAuthPublic(int authPublic) {
        this.authPublic = authPublic;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    /**
     * 投票是否已结束
     */
    public boolean isEnded() {
        return this.end == VoteEnd.ENDED;
    }

    public int getNotifyBeginTime() {
        return notifyBeginTime;
    }

    public void setNotifyBeginTime(int notifyBeginTime) {
        this.notifyBeginTime = notifyBeginTime;
    }

    public ArrayList<String> getItemContentList() {
        return itemContentList;
    }

    public void setItemContentList(ArrayList<String> itemContentList) {
        this.itemContentList = itemContentList;
    }

    public ArrayList<AppVoteItem> getActVoteItemList() {
        if (null == actVoteItemList) {
            actVoteItemList = getCommVoteItemList();
        }
        return actVoteItemList;
    }

    public void setActVoteItemList(ArrayList<AppVoteItem> actVoteItemList) {
        this.actVoteItemList = actVoteItemList;
    }

    public ArrayList<AppVoteRecord> getActVoteList() {
        if (null == actVoteList) {
            actVoteList = commVoteRecordList;
        }
        return actVoteList;
    }

    public void setActVoteList(ArrayList<AppVoteRecord> actVoteList) {
        this.actVoteList = actVoteList;
    }

    public int getArchive() {
        return archive;
    }

    public void setArchive(int archive) {
        this.archive = archive;
    }

    public AppVoteRecord getActVote() {
        return actVote;
    }

    public void setActVote(AppVoteRecord actVote) {
        this.actVote = actVote;
    }

    public ArrayList<AppVoteItem> getCommVoteItemList() {
        if (null == commVoteItemList) {
            commVoteItemList = new ArrayList<>();
        }
        return commVoteItemList;
    }

    public void setCommVoteItemList(ArrayList<AppVoteItem> commVoteItemList) {
        this.commVoteItemList = commVoteItemList;
    }

    public ArrayList<AppVoteRecord> getCommVoteRecordList() {
        if (null == commVoteRecordList) {
            commVoteRecordList = new ArrayList<>();
        }
        return commVoteRecordList;
    }

    public void setCommVoteRecordList(ArrayList<AppVoteRecord> commVoteRecordList) {
        this.commVoteRecordList = commVoteRecordList;
    }
}
