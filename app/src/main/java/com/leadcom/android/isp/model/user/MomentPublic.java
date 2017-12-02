package com.leadcom.android.isp.model.user;

import com.leadcom.android.isp.model.Model;

/**
 * <b>功能描述：</b>公开的动态说说<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/10/20 10:21 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/10/20 10:21 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class MomentPublic extends Model {

    private String userMmtId;   //个人动态ID
    private String userId;      //档案创建者的用户ID
    private String userName;    //档案创建者的用户名称
    private String headPhoto;   //档案创建者的用户头像
    private String createDate;  //创建时间
    private Moment userMmt;    //个人动态

    public String getUserMmtId() {
        return userMmtId;
    }

    public void setUserMmtId(String userMmtId) {
        this.userMmtId = userMmtId;
    }

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
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public Moment getUserMmt() {
        return userMmt;
    }

    public void setUserMmt(Moment userMmt) {
        this.userMmt = userMmt;
    }
}
