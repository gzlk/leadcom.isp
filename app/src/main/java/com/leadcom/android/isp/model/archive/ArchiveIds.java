package com.leadcom.android.isp.model.archive;

import com.leadcom.android.isp.model.Model;
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

public class ArchiveIds extends Model {

    @Column(Archive.Field.UserMomentId)
    private String momentId;    //用户动态的ID
    @Column(Archive.Field.ArchiveId)
    private String docId;

    public String getMomentId() {
        return momentId;
    }

    public void setMomentId(String momentId) {
        this.momentId = momentId;
    }

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }
}
