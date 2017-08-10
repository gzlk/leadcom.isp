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
        String End = "end";
        String VoteId = "voteId";
        String MaxSelectable = "maxSelectable";
        String Num = "num";
        String VoteItemId = "voteItemId";
        String Anonymity = "anonymity";
        String IMSI = "imsi";
    }

    /**
     * 投票类型
     */
    public interface VoteType {
        /**
         * 单选投票
         */
        int SINGLE = 1;
        /**
         * 多选投票
         */
        int MULTIPLE = 2;
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
    //调查类型：1.单选；2.多选
    @Column(Archive.Field.Type)
    private int type;
    // 最大可选数量
    @Column(Field.MaxSelectable)
    private int maxSelectable;
    //标题
    @Column(Archive.Field.Title)
    private String title;
    //描述
    @Column(Archive.Field.Content)
    private String content;
    //投票开始时间
    @Column(Field.BeginDate)
    private String beginTime;
    //投票结束时间
    @Column(Field.EndDate)
    private String endDate;
    //创建时间
    @Column(Model.Field.CreateDate)
    private String createDate;
    //创建者id
    @Column(Archive.Field.CreatorId)
    private String creatorId;
    //创建者名字
    @Column(Archive.Field.CreatorName)
    private String creatorName;
    //是否不记名(0.不记名,1.记名)
    @Column(Field.Anonymity)
    private int anonymity;
    //公开范围(0.不公开,1.公开)
    @Column(Archive.Field.AuthPublic)
    private int authPublic;
    //是否已经结束
    @Column(Field.End)
    private String end;
    @Ignore
    private int notifyBeginTime;
    @Ignore
    private ArrayList<AppVoteItem> actVoteItemList;
    @Ignore
    private ArrayList<AppVoteRecord> actVoteList;

    /**
     * 投票是否已经结束
     */
    public boolean isEnded() {
        if (isEmpty(endDate)) return true;
        long end = Utils.parseDate(StringHelper.getString(R.string.ui_base_text_date_time_format), endDate).getTime();
        long now = Utils.timestamp();
        return now > end;
    }

    public void saveVoteItems() {
        if (null != actVoteItemList) {
            new Dao<>(AppVoteItem.class).save(actVoteItemList);
        }
    }

    public String getActId() {
        return actId;
    }

    public void setActId(String actId) {
        this.actId = actId;
    }

    public int getType() {
        if (0 == type) {
            type = VoteType.SINGLE;
        }
        return type;
    }

    public void setType(int type) {
        this.type = type;
        // 设置默认的最大选择数量
        maxSelectable = type == VoteType.SINGLE ? 1 : 2;
    }

    public int getMaxSelectable() {
        if (0 == maxSelectable) {
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

    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public int getNotifyBeginTime() {
        return notifyBeginTime;
    }

    public void setNotifyBeginTime(int notifyBeginTime) {
        this.notifyBeginTime = notifyBeginTime;
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
