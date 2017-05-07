package com.gzlk.android.isp.model.user.moment;

import com.gzlk.android.isp.model.Model;
import com.litesuits.orm.db.annotation.Column;
import com.litesuits.orm.db.annotation.Table;

/**
 * <b>功能描述：</b>个人动态统计信息<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/04/19 22:35 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/04/19 22:35 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */
@Table(Moment.Table.ADDITIONAL)
public class MomentAdditional extends SubMoment {

    public static class Field {
        public static final String ReadNum = "readNum";
        public static final String LikeNum = "likeNum";
        public static final String CommentNum = "cmtNum";
        public static final String CollectionNum = "colNum";
    }

    //动态发起者用户ID
    @Column(Model.Field.UserId)
    private String userId;
    //阅读次数
    @Column(Field.ReadNum)
    private String readNum;
    //点赞次数
    @Column(Field.LikeNum)
    private String likeNum;
    //评论次数
    @Column(Field.CommentNum)
    private String cmtNum;
    //收藏次数
    @Column(Field.CollectionNum)
    private String colNum;
    //创建时间
    @Column(Model.Field.CreateDate)
    private String createDate;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getReadNum() {
        return readNum;
    }

    public void setReadNum(String readNum) {
        this.readNum = readNum;
    }

    public String getLikeNum() {
        return likeNum;
    }

    public void setLikeNum(String likeNum) {
        this.likeNum = likeNum;
    }

    public String getCmtNum() {
        return cmtNum;
    }

    public void setCmtNum(String cmtNum) {
        this.cmtNum = cmtNum;
    }

    public String getColNum() {
        return colNum;
    }

    public void setColNum(String colNum) {
        this.colNum = colNum;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }
}
