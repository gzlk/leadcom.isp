package com.gzlk.android.isp.model.user;

import com.gzlk.android.isp.model.archive.Additional;
import com.gzlk.android.isp.model.archive.Archive;
import com.gzlk.android.isp.model.Model;
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
    // 我是否已赞
    @Ignore
    private boolean myPraised;
    //当前用户是否收藏(0.未收藏,1.已收藏)
    @Column(Archive.Field.Collected)
    private int collection;
    @Column(Archive.Field.CollectionId)
    private String colId;          //当前用户收藏该动态后的收藏ID

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
        return image;
    }

    public void setImage(ArrayList<String> image) {
        this.image = image;
    }

    public boolean isMyPraised() {
        return myPraised;
    }

    public void setMyPraised(boolean myPraised) {
        this.myPraised = myPraised;
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
    }

    public String getColId() {
        return colId;
    }

    public void setColId(String colId) {
        this.colId = colId;
    }
}
