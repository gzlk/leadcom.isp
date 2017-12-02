package com.leadcom.android.isp.api.org;

import com.leadcom.android.isp.model.organization.SimpleGroup;

import java.util.ArrayList;

/**
 * <b>功能描述：</b>简化的组织列表<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/07/13 17:40 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/07/13 17:40 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class SimpleOutput {
    private SimpleGroup ownGroup;
    private ArrayList<SimpleGroup> supGroup;
    private ArrayList<SimpleGroup> subGroup;
    private ArrayList<SimpleGroup> frdGroup;

    public SimpleGroup getOwnGroup() {
        return ownGroup;
    }

    public void setOwnGroup(SimpleGroup ownGroup) {
        this.ownGroup = ownGroup;
    }

    public ArrayList<SimpleGroup> getSupGroup() {
        return supGroup;
    }

    public void setSupGroup(ArrayList<SimpleGroup> supGroup) {
        this.supGroup = supGroup;
    }

    public ArrayList<SimpleGroup> getSubGroup() {
        return subGroup;
    }

    public void setSubGroup(ArrayList<SimpleGroup> subGroup) {
        this.subGroup = subGroup;
    }

    public ArrayList<SimpleGroup> getFrdGroup() {
        return frdGroup;
    }

    public void setFrdGroup(ArrayList<SimpleGroup> frdGroup) {
        this.frdGroup = frdGroup;
    }
}
