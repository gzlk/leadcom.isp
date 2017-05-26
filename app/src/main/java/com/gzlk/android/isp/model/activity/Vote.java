package com.gzlk.android.isp.model.activity;

import com.gzlk.android.isp.model.Model;
import com.gzlk.android.isp.model.archive.Archive;
import com.litesuits.orm.db.annotation.Column;
import com.litesuits.orm.db.annotation.Table;

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
public class Vote extends Model {

    public interface Field {
        String Description = "description";
        String BeginTime = "beginTime";
        String EndTime = "endTime";
        String IsEnd = "isEnd";
        String VoteId="voteId";
        String Num="num";
        String VoteItemId="voteItemId";
        String IMSI="imsi";
    }

    //活动Id
    @Column(Activity.Field.ActivityId)
    private String actId;
    //调查类型：1.单选；2.多选
    @Column(Archive.Field.Type)
    private int type;
    //标题
    @Column(Archive.Field.Title)
    private String title;
    //描述
    @Column(Field.Description)
    private String desc;
    //投票开始时间
    @Column(Field.BeginTime)
    private String beginTime;
    //投票结束时间
    @Column(Field.EndTime)
    private String endTime;
    //创建时间
    @Column(Model.Field.CreateDate)
    private String createDate;
    //创建者id
    @Column(Archive.Field.CreatorId)
    private String creatorId;
    //创建者名字
    @Column(Archive.Field.CreatorName)
    private String creatorName;
    //是否已经结束
    @Column(Field.IsEnd)
    private String isEnd;

    public String getActId() {
        return actId;
    }

    public void setActId(String actId) {
        this.actId = actId;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getBeginTime() {
        return beginTime;
    }

    public void setBeginTime(String beginTime) {
        this.beginTime = beginTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
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
        return creatorName;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }

    public String getIsEnd() {
        return isEnd;
    }

    public void setIsEnd(String isEnd) {
        this.isEnd = isEnd;
    }
}
