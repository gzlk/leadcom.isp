package com.leadcom.android.isp.model.archive;

import com.leadcom.android.isp.model.Dao;
import com.leadcom.android.isp.model.Model;
import com.leadcom.android.isp.model.organization.Organization;
import com.litesuits.orm.db.annotation.Column;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.assit.QueryBuilder;

import java.util.List;

/**
 * <b>功能描述：</b>草稿档案<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/10/23 16:20 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/10/23 16:20 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

@Table(Archive.Table.ARCHIVE_DRAFT)
public class ArchiveDraft extends Model {

    /**
     * 查询指定组织下的草稿，groupId为空时，查询所有草稿
     */
    public static List<ArchiveDraft> getDraft(String groupId) {
        QueryBuilder<ArchiveDraft> builder = new QueryBuilder<>(ArchiveDraft.class);
        if (!isEmpty(groupId)) {
            builder = builder.whereEquals(Organization.Field.GroupId, groupId);
        }
        builder = builder.appendOrderDescBy(Field.CreateDate);
        return new Dao<>(ArchiveDraft.class).query(builder);
    }

    public static void save(ArchiveDraft draft) {
        new Dao<>(ArchiveDraft.class).save(draft);
    }

    public static void delete(String draftId) {
        new Dao<>(ArchiveDraft.class).delete(draftId);
    }

    @Column(Archive.Field.Title)
    private String title;
    @Column(Organization.Field.GroupId)
    private String groupId;
    @Column(Organization.Field.GroupName)
    private String groupName;
    @Column(Archive.Field.DraftJson)
    private String archiveJson;
    @Column(Field.CreateDate)
    private String createDate;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getArchiveJson() {
        return archiveJson;
    }

    public void setArchiveJson(String archiveJson) {
        this.archiveJson = archiveJson;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }
}
