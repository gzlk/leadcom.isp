package com.gzlk.android.isp.model.user.document;

import com.gzlk.android.isp.model.Model;
import com.litesuits.orm.db.annotation.Column;

/**
 * <b>功能描述：</b>个人档案的点赞<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/04/20 00:04 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/04/20 00:04 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class DocumentLike extends SubDocument {

    @Column(Model.Field.UserId)
    private String userId;      //用户ID
    @Column(Model.Field.UserName)
    private String userName;    //用户名称
    @Column(Model.Field.CreateDate)
    private String createDate;  //点赞日期

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

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }
}
