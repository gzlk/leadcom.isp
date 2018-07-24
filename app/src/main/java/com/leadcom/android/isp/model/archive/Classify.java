package com.leadcom.android.isp.model.archive;

import com.leadcom.android.isp.model.Model;

/**
 * <b>功能描述：</b>档案分类<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2018/07/24 15:41 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2018/07/24 15:41 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */
public class Classify extends Model {
    /**父类ID*/
    private long parentId;
    /**所属组织，考虑到每个组织的档案分类不一样*/
    private String groupId;
    /**分类*/
    private String name;

    public long getParentId() {
        return parentId;
    }

    public void setParentId(long parentId) {
        this.parentId = parentId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
