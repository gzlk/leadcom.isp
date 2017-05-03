package com.gzlk.android.isp.model.user.document;

import com.gzlk.android.isp.model.user.moment.MomentAdditional;
import com.litesuits.orm.db.annotation.Column;
import com.litesuits.orm.db.annotation.Table;

/**
 * <b>功能描述：</b>个人档案统计信息<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/04/20 00:07 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/04/20 00:07 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

@Table(Document.Table.ADDITIONAL)
public class DocumentAdditional extends SubDocument {

    @Column(MomentAdditional.Field.ReadNum)
    private String readNum;     //阅读次数
    @Column(MomentAdditional.Field.LikeNum)
    private String likeNum;     //点赞次数
    @Column(MomentAdditional.Field.CommentNum)
    private String cmtNum;      //评论次数
    @Column(MomentAdditional.Field.CollectionNum)
    private String colNum;      //收藏次数

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
}
