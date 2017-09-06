package com.gzlk.android.isp.model.activity.vote;

import com.gzlk.android.isp.R;
import com.gzlk.android.isp.helper.StringHelper;
import com.gzlk.android.isp.model.Model;
import com.gzlk.android.isp.model.activity.Activity;
import com.gzlk.android.isp.model.archive.Archive;
import com.litesuits.orm.db.annotation.Column;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.enums.Strategy;

/**
 * <b>功能描述：</b>投票的选项<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/05/25 23:26 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/05/25 23:26 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */
@Table(Activity.Table.VOTE_ITEM)
public class AppVoteItem extends Model {

    public static final String REFUSED_ID = StringHelper.getString(R.string.ui_activity_vote_details_rejected_id);

    public static AppVoteItem getRefuseItem() {
        AppVoteItem item = new AppVoteItem();
        item.setId(REFUSED_ID);
        item.setContent(StringHelper.getString(R.string.ui_activity_vote_details_rejected));
        return item;
    }

    //投票设置对象的id
    @Column(AppVote.Field.VoteId)
    private String setupId;
    //投票选项的描述
    @Column(Archive.Field.Content)
    private String content;
    //序号
    @Column(Activity.Field.Sequence)
    private int seq;
    //该投票选项的投票得票数
    @Column(AppVote.Field.Num)
    private int num = 0;
    //该投票项的创建时间
    @Column(Field.CreateDate)
    private String createDate;

    public String getSetupId() {
        return setupId;
    }

    public void setSetupId(String setupId) {
        this.setupId = setupId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getSeq() {
        return seq;
    }

    public void setSeq(int seq) {
        this.seq = seq;
    }

    public int getNum() {
        return num;
    }

    public void setNum(int num) {
        this.num = num;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }
}
