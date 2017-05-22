package com.gzlk.android.isp.model.archive;

import com.gzlk.android.isp.model.Model;
import com.litesuits.orm.db.annotation.Column;

/**
 * <b>功能描述：</b>各类档案id信息<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/05/22 23:56 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/05/22 23:56 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class ArchiveInfo extends Model {

    @Column(Archive.Field.UserMomentId)
    private String momentId;    //用户动态的ID
    @Column(Archive.Field.UserArchiveId)
    private String userDocId;   //用户档案的ID
    @Column(Archive.Field.GroupArchiveId)
    private String groDocId;    //组织档案的ID

    public String getMomentId() {
        return momentId;
    }

    public void setMomentId(String momentId) {
        this.momentId = momentId;
    }

    public String getUserDocId() {
        return userDocId;
    }

    public void setUserDocId(String userDocId) {
        this.userDocId = userDocId;
    }

    public String getGroDocId() {
        return groDocId;
    }

    public void setGroDocId(String groDocId) {
        this.groDocId = groDocId;
    }
}
