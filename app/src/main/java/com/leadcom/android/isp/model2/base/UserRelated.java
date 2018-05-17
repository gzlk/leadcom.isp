package com.leadcom.android.isp.model2.base;

import com.leadcom.android.isp.model.Model;
import com.litesuits.orm.db.annotation.Column;


/**
 * <b>功能描述：</b>用户创建相关的类<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2018/05/17 22:04 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>version: 1.0.0 <br />
 * <b>修改时间：</b>2017/10/04 18:50 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */
public class UserRelated extends Model {

    @Column(TableFields.UserRelated.UserId)
    private String userId;
    @Column(TableFields.UserRelated.UserName)
    private String userName;
    @Column(TableFields.UserRelated.HeadPhoto)
    private String headPhoto;
    @Column(TableFields.UserRelated.CreateDate)
    private String createDate;

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
}
