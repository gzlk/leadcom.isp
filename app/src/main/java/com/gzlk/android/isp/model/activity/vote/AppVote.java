package com.gzlk.android.isp.model.activity.vote;

import com.google.gson.reflect.TypeToken;
import com.gzlk.android.isp.R;
import com.gzlk.android.isp.etc.Utils;
import com.gzlk.android.isp.helper.StringHelper;
import com.gzlk.android.isp.lib.Json;
import com.gzlk.android.isp.model.Dao;
import com.gzlk.android.isp.model.Model;
import com.gzlk.android.isp.model.activity.Activity;
import com.gzlk.android.isp.model.archive.Archive;
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
        String Description = "description";
        String BeginDate = "beginDate";
        String EndDate = "endDate";
        String CreatorHeadPhoto = "creatorHeadPhoto";
        String End = "end";
        String VoteId = "voteId";
        String MaxSelectable = "maxSelectable";
        String Num = "num";
        String VoteItemId = "voteItemId";
        String Anonymity = "anonymity";
        String IMSI = "imsi";
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
    @Ignore
    private int notifyBeginTime;
    @Ignore
    private ArrayList<String> itemContentList;
    @Ignore
    private ArrayList<AppVoteItem> actVoteItemList;
    @Ignore
    private ArrayList<AppVoteRecord> actVoteList;

    public void saveVoteItems() {
        if (null != actVoteItemList) {
            new Dao<>(AppVoteItem.class).save(actVoteItemList);
        }
    }

    public void saveVoteRecords() {
        if (null != actVoteList) {
            new Dao<>(AppVoteRecord.class).save(actVoteList);
        }
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
            actVoteItemList = new ArrayList<>();
        }
        return actVoteItemList;
    }

    public void setActVoteItemList(ArrayList<AppVoteItem> actVoteItemList) {
        this.actVoteItemList = actVoteItemList;
    }

    public ArrayList<AppVoteRecord> getActVoteList() {
        return actVoteList;
    }

    public void setActVoteList(ArrayList<AppVoteRecord> actVoteList) {
        this.actVoteList = actVoteList;
    }
}
