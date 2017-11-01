package com.gzlk.android.isp.model.user;

import com.gzlk.android.isp.model.archive.Additional;
import com.gzlk.android.isp.model.archive.Archive;
import com.gzlk.android.isp.model.Model;
import com.gzlk.android.isp.model.archive.ArchiveLike;
import com.gzlk.android.isp.model.archive.Comment;
import com.litesuits.orm.db.annotation.Column;
import com.litesuits.orm.db.annotation.Ignore;
import com.litesuits.orm.db.annotation.Table;

import java.util.ArrayList;

/**
 * <b>功能描述：</b>个人动态<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/04/15 14:34 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/04/15 14:34 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

@Table(Archive.Table.USER_MOMENT)
public class Moment extends Additional {

    /**
     * 说说类型
     */
    public interface Type {
        /**
         * 纯文本
         */
        int TEXT = 1;
        /**
         * 图片
         */
        int IMAGE = 2;
        /**
         * 视频
         */
        int VIDEO = 3;
    }

    //用户姓名
    @Column(Model.Field.UserId)
    private String userId;
    //姓名
    @Column(Model.Field.UserName)
    private String userName;
    //用户头像
    @Column(User.Field.HeadPhoto)
    private String headPhoto;
    //创建时间
    @Column(Model.Field.CreateDate)
    private String createDate;
    @Column(Archive.Field.Type)
    private int type;           //类型(1.纯文本,2.图片,3.视频)
    @Column(Archive.Field.AuthPublic)
    private int authPublic;     //授权公开范围(1.公开,2.私密)
    //创建地点
    @Column(Archive.Field.Location)
    private String location;
    //动态内容
    @Column(Archive.Field.Content)
    private String content;
    //档案附加信息
    @Ignore
    private Additional addition;
    @Column(Archive.Field.Video)
    private String video;          //视频地址
    //图片
    @Column(Archive.Field.Image)
    private ArrayList<String> image;
    // 点赞相关
    @Ignore
    private ArrayList<ArchiveLike> userMmtLikeList;
    @Ignore
    private ArrayList<Comment> userMmtCmtList;

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getHeadPhoto() {
        return headPhoto;
    }

    public void setHeadPhoto(String headPhoto) {
        this.headPhoto = headPhoto;
    }

    public String getCreateDate() {
        if (isEmpty(createDate)) {
            createDate = DFT_DATE;
        }
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public int getAuthPublic() {
        return authPublic;
    }

    public void setAuthPublic(int authPublic) {
        this.authPublic = authPublic;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public Additional getAddition() {
        return addition;
    }

    public void setAddition(Additional addition) {
        this.addition = addition;
        resetAdditional(this.addition);
    }

    public String getVideo() {
        return video;
    }

    public void setVideo(String video) {
        this.video = video;
    }

    public ArrayList<String> getImage() {
        if (null == image) {
            image = new ArrayList<>();
        }
        return image;
    }

    public void setImage(ArrayList<String> image) {
        this.image = image;
    }

    public ArrayList<ArchiveLike> getUserMmtLikeList() {
        if (null == userMmtLikeList) {
            userMmtLikeList = new ArrayList<>();
        }
        return userMmtLikeList;
    }

    public void setUserMmtLikeList(ArrayList<ArchiveLike> userMmtLikeList) {
        this.userMmtLikeList = userMmtLikeList;
    }

    public String getLikeNames() {
        if (null != userMmtLikeList) {
            String tmp = "";
            for (ArchiveLike like : userMmtLikeList) {
                tmp += (isEmpty(tmp) ? "" : "、") + like.getUserName();
            }
            return tmp;
        }
        return "";
    }

    public ArrayList<Comment> getUserMmtCmtList() {
        if (null == userMmtCmtList) {
            userMmtCmtList = new ArrayList<>();
        }
        return userMmtCmtList;
    }

    public void setUserMmtCmtList(ArrayList<Comment> userMmtCmtList) {
        this.userMmtCmtList = userMmtCmtList;
    }
}
