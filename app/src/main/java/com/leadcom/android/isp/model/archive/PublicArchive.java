package com.leadcom.android.isp.model.archive;

import com.leadcom.android.isp.model.Model;
import com.leadcom.android.isp.model.user.User;
import com.litesuits.orm.db.annotation.Column;
import com.litesuits.orm.db.annotation.Ignore;
import com.litesuits.orm.db.annotation.Table;

/**
 * <b>功能描述：</b>组织内成员公开的档案<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/10/17 14:05 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/10/17 14:05 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

@Table(Archive.Table.PUBLIC_ARCHIVE)
public class PublicArchive extends Model {

    @Column(Archive.Field.UserArchiveId)
    private String userDocId;   //个人档案ID
    @Column(Field.UserId)
    private String userId;      //档案创建者的用户ID
    @Column(Field.UserName)
    private String userName;    //档案创建者的用户名称
    @Column(User.Field.HeadPhoto)
    private String headPhoto;   //档案创建者的用户头像
    @Column(Field.CreateDate)
    private String createDate;  //创建时间
    @Ignore
    private Archive userDoc;    //个人档案

    public String getUserDocId() {
        return userDocId;
    }

    public void setUserDocId(String userDocId) {
        this.userDocId = userDocId;
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

    public Archive getUserDoc() {
        if (null == userDoc) {
            userDoc = Archive.get(userDocId);
        }
        return userDoc;
    }

    public void setUserDoc(Archive userDoc) {
        this.userDoc = userDoc;
    }
}
