package com.leadcom.android.isp.model.activity.sign;

import com.leadcom.android.isp.R;
import com.leadcom.android.isp.etc.Utils;
import com.leadcom.android.isp.helper.StringHelper;
import com.leadcom.android.isp.helper.ToastHelper;
import com.leadcom.android.isp.model.Model;
import com.leadcom.android.isp.model.activity.Activity;
import com.leadcom.android.isp.model.activity.vote.AppVote;
import com.leadcom.android.isp.model.archive.Archive;
import com.leadcom.android.isp.model.common.TalkTeam;
import com.litesuits.orm.db.annotation.Column;
import com.litesuits.orm.db.annotation.Ignore;
import com.litesuits.orm.db.annotation.Table;

import java.util.ArrayList;

/**
 * <b>功能描述：</b>活动应用：签到<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/05/26 00:18 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/05/26 00:18 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */
@Table(Activity.Table.SIGN)
public class AppSigning extends Sign {

    //活动Id
    @Column(Activity.Field.ActivityId)
    private String actId;
    //标题
    @Column(Archive.Field.Title)
    private String title;
    //描述
    @Column(Archive.Field.Content)
    private String content;
    //创建者的id
    @Column(Model.Field.CreatorId)
    private String creatorId;
    //创建者名称
    @Column(Model.Field.CreatorName)
    private String creatorName;
    //签到开始时间
    @Column(AppVote.Field.BeginDate)
    private String beginDate;
    //签到结束时间
    @Column(AppVote.Field.EndDate)
    private String endDate;
    //签到人数(按user对象的id过滤，避免一个用户多次签到后被重复计数)
    @Column(Field.SignInNum)
    private int signInNum;
    //是否已经结束(0.结束,1.进行中)
    @Column(Field.End)
    private int end;
    //是否已存档(0.未存档,1.已存档)
    @Column(Field.Archived)
    private int archive;
    @Ignore
    private ArrayList<AppSignRecord> actSignInList;
    @Ignore
    private int notifyBeginTime;

    // 群聊沟通相关字段
    @Column(TalkTeam.Field.TeamId)
    private String commId;
    @Column(Activity.Field.NimId)
    private String tid;
    @Column(AppVote.Field.CreatorHeadPhoto)
    private String creatorHeadPhoto;
    @Ignore
    private ArrayList<AppSignRecord> recordList;

    /**
     * 是否可以签到
     */
    public boolean couldSignable(String date) {
        String fmt = StringHelper.getString(R.string.ui_base_text_date_time_format);
        long postTime = Utils.parseDate(fmt, date).getTime();
        long beginTime = Utils.parseDate(fmt, getBeginDate()).getTime();
        long endTime = Utils.parseDate(fmt, getEndDate()).getTime();
        if (postTime < beginTime) {
            ToastHelper.make().showMsg(R.string.ui_activity_sign_not_start);
            return false;
        } else if (postTime > endTime) {
            ToastHelper.make().showMsg(R.string.ui_activity_sign_has_been_ended);
            return false;
        }
        return true;
    }

    public String getActId() {
        return actId;
    }

    public void setActId(String actId) {
        this.actId = actId;
    }

    public String getTitle() {
        if (isEmpty(title)) {
            title = "";
        }
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

    public String getBeginDate() {
        return beginDate;
    }

    public void setBeginDate(String beginDate) {
        this.beginDate = beginDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public int getSignInNum() {
        return signInNum;
    }

    public void setSignInNum(int signInNum) {
        this.signInNum = signInNum;
    }

    public int getEnd() {
        return end;
    }

    public void setEnd(int end) {
        this.end = end;
    }

    public int getArchive() {
        return archive;
    }

    public void setArchive(int archive) {
        this.archive = archive;
    }

    public ArrayList<AppSignRecord> getActSignInList() {
        return actSignInList;
    }

    public void setActSignInList(ArrayList<AppSignRecord> actSignInList) {
        this.actSignInList = actSignInList;
    }

    public int getNotifyBeginTime() {
        return notifyBeginTime;
    }

    public void setNotifyBeginTime(int notifyBeginTime) {
        this.notifyBeginTime = notifyBeginTime;
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

    public String getCreatorHeadPhoto() {
        return creatorHeadPhoto;
    }

    public void setCreatorHeadPhoto(String creatorHeadPhoto) {
        this.creatorHeadPhoto = creatorHeadPhoto;
    }

    public ArrayList<AppSignRecord> getRecordList() {
        if (null == recordList) {
            recordList = new ArrayList<>();
        }
        return recordList;
    }

    public void setRecordList(ArrayList<AppSignRecord> recordList) {
        this.recordList = recordList;
    }
}
