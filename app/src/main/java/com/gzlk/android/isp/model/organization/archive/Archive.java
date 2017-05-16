package com.gzlk.android.isp.model.organization.archive;

import com.gzlk.android.isp.model.BaseArchive;
import com.gzlk.android.isp.model.organization.Organization;
import com.litesuits.orm.db.annotation.Column;
import com.litesuits.orm.db.annotation.Table;

/**
 * <b>功能描述：</b>组织内档案<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/05/07 09:51 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/05/07 09:51 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

@Table(Archive.Table.ARCHIVE)
public class Archive extends BaseArchive {

    public static class Table {
        public static final String ARCHIVE = "archive";
        public static final String ADDITIONAL = "archiveAdditional";
        public static final String COMMENT = "archiveComment";
        public static final String LIKE = "archiveLike";
    }

    @Column(Organization.Field.GroupId)
    private String groupId;            //群体ID


    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

}