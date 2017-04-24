package com.gzlk.android.isp.model.user.document;

import com.gzlk.android.isp.model.Model;
import com.litesuits.orm.db.annotation.Column;

/**
 * <b>功能描述：</b>个人档案信息基类<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/04/19 23:58 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/04/19 23:58 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class SubDocument extends Model {

    public static class Field {
        public static final String UserDocumentId = "userDocId";
    }

    //组织档案的ID
    @Column(Field.UserDocumentId)
    private String userDocId;

    public String getUserDocId() {
        return userDocId;
    }

    public void setUserDocId(String userDocId) {
        this.userDocId = userDocId;
    }
}
