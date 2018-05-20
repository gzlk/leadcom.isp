package com.leadcom.android.isp.model.archive;

import com.leadcom.android.isp.model.Model;
import com.leadcom.android.isp.model.user.Collection;
import com.litesuits.orm.db.annotation.Column;
import com.litesuits.orm.db.annotation.Ignore;

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

    //档案附加信息
    @Ignore
    private Additional addition;
    @Column(Archive.Field.ReadNumber)
    private int readNum;     //阅读次数
    @Column(Archive.Field.LikeNumber)
    private int likeNum;     //点赞次数
    @Column(Archive.Field.CommentNumber)
    private int cmtNum;      //评论次数
    @Column(Archive.Field.CollectNumber)
    private int colNum;      //收藏次数

    //当前用户是否收藏(0.未收藏,1.已收藏)
    @Column(Archive.Field.Collected)
    private int collection;
    @Column(Archive.Field.CollectionId)
    private String colId;          //当前用户收藏该动态后的收藏ID

    @Column(Archive.Field.LikeId)
    private String likeId;
    @Column(Archive.Field.Liked)
    private int like;
    @Ignore
    private String fileIds;  //文件ID，多个ID用逗号隔开

    // 当前组织是否推荐：0.未推荐，1.已推荐
    @Column(Archive.Field.Recommend)
    private int recommend;
    // 当前组织推荐该档案后的组织档案推荐ID
    @Column(Archive.Field.RecommendId)
    private String rcmdId;

    /**
     * 档案是否已经推荐到首页
     */
    public boolean isRecommend() {
        return recommend == RecommendArchive.RecommendStatus.RECOMMENDED;
    }

    public boolean isVisible() {
        return likeNum > 0 || cmtNum > 0 || colNum > 0;
    }

    public void resetAdditional(Additional additional) {
        if (null != additional) {
            readNum = additional.getReadNum();
            likeNum = additional.getLikeNum();
            cmtNum = additional.getCmtNum();
            colNum = additional.getColNum();
        }
    }

    public void resetInfo(ArchiveInfo info) {
        if (null != info) {
            recommend = info.getRecommend();
            rcmdId = info.getRcmdId();
            collection = info.getCollection();
            colId = info.getColId();
            like = info.getLike();
            likeId = info.getLikeId();
        }
    }

    public Additional getAddition() {
        return addition;
    }

    public void setAddition(Additional addition) {
        this.addition = addition;
        // 重置附加信息
        resetAdditional(this.addition);
    }

    public int getReadNum() {
        return readNum;
    }

    public void setReadNum(int readNum) {
        this.readNum = readNum;
        if (null != addition) {
            addition.setReadNum(readNum);
        }
    }

    public int getLikeNum() {
        return likeNum;
    }

    public void setLikeNum(int likeNum) {
        this.likeNum = likeNum;
        if (null != addition) {
            addition.setLikeNum(likeNum);
        }
    }

    public int getCmtNum() {
        return cmtNum;
    }

    public void setCmtNum(int cmtNum) {
        this.cmtNum = cmtNum;
        if (null != addition) {
            addition.setCmtNum(cmtNum);
        }
    }

    public int getColNum() {
        return colNum;
    }

    public void setColNum(int colNum) {
        this.colNum = colNum;
        if (null != addition) {
            addition.setColNum(colNum);
        }
    }

    public boolean isCollected() {
        return collection == Collection.CollectionType.COLLECTED;
    }

    /**
     * 当前用户是否收藏(0.未收藏,1.已收藏)
     */
    public int getCollection() {
        return collection;
    }

    /**
     * 当前用户是否收藏(0.未收藏,1.已收藏)
     */
    public void setCollection(int collection) {
        this.collection = collection;
        if (null != addition) {
            addition.setCollection(collection);
        }
    }

    public String getColId() {
        return colId;
    }

    public void setColId(String colId) {
        this.colId = colId;
        if (null != addition) {
            addition.setColId(colId);
        }
    }

    public boolean isLiked() {
        return like == Archive.LikeType.LIKED;
    }

    public String getLikeId() {
        return likeId;
    }

    public void setLikeId(String likeId) {
        this.likeId = likeId;
        if (null != addition) {
            addition.setLikeId(likeId);
        }
    }

    public int getLike() {
        return like;
    }

    public void setLike(int like) {
        this.like = like;
        if (null != addition) {
            addition.setLike(like);
        }
    }

    public void addFileId(String fileId) {
        if (isEmpty(fileIds)) {
            fileIds = fileId;
        } else {
            if (!fileIds.contains(fileId)) {
                fileIds += "," + fileId;
            }
        }
    }

    public String getFileIds() {
        return fileIds;
    }

    public void setFileIds(String fileIds) {
        this.fileIds = fileIds;
    }

    public int getRecommend() {
        return recommend;
    }

    public void setRecommend(int recommend) {
        this.recommend = recommend;
    }

    public String getRcmdId() {
        return rcmdId;
    }

    public void setRcmdId(String rcmdId) {
        this.rcmdId = rcmdId;
    }
}
