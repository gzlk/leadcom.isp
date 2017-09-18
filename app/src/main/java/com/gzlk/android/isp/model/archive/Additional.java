package com.gzlk.android.isp.model.archive;

import com.gzlk.android.isp.model.Model;
import com.litesuits.orm.db.annotation.Column;

/**
 * <b>功能描述：</b>档案、说说的附加信息：查看次数、点赞次数、评论次数、收藏次数<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/05/22 18:28 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/05/22 18:28 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class Additional extends Model {

    @Column(Archive.Field.ReadNumber)
    private int readNum;     //阅读次数
    @Column(Archive.Field.LikeNumber)
    private int likeNum;     //点赞次数
    @Column(Archive.Field.CommentNumber)
    private int cmtNum;      //评论次数
    @Column(Archive.Field.CollectNumber)
    private int colNum;      //收藏次数

    public void resetAdditional(Additional additional) {
        if (null != additional) {
            readNum = additional.getReadNum();
            likeNum = additional.getLikeNum();
            cmtNum = additional.getCmtNum();
            colNum = additional.getColNum();
        }
    }

    public int getReadNum() {
        return readNum;
    }

    public void setReadNum(int readNum) {
        this.readNum = readNum;
    }

    public int getLikeNum() {
        return likeNum;
    }

    public void setLikeNum(int likeNum) {
        this.likeNum = likeNum;
    }

    public int getCmtNum() {
        return cmtNum;
    }

    public void setCmtNum(int cmtNum) {
        this.cmtNum = cmtNum;
    }

    public int getColNum() {
        return colNum;
    }

    public void setColNum(int colNum) {
        this.colNum = colNum;
    }
}
