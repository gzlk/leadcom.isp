package com.gzlk.android.isp.model.organization.archive;

import com.gzlk.android.isp.model.BaseArchive;
import com.gzlk.android.isp.model.Model;
import com.litesuits.orm.db.annotation.Column;

/**
 * <b>功能描述：</b>组织档案<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/05/07 09:58 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/05/07 09:58 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class SubArchive extends Model {

    @Column(BaseArchive.Field.GroupArchiveId)
    private String groDocId;    //组织档案ID
    @Column(Field.CreateDate)
    private String createDate;  //点赞日期

    public String getGroDocId() {
        return groDocId;
    }

    public void setGroDocId(String groDocId) {
        this.groDocId = groDocId;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }
}
