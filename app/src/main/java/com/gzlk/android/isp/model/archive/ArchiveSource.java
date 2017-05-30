package com.gzlk.android.isp.model.archive;

import com.gzlk.android.isp.lib.Json;

/**
 * <b>功能描述：</b><br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/05/30 23:45 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/05/30 23:45 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class ArchiveSource {

    //模块类型(1.个人档案,2.组织档案,3.活动聊天,4.议题聊天,5.个人动态)
    private int module;
    //模块ID(表的ID主键)
    private String id;

    public int getModule() {
        return module;
    }

    public void setModule(int module) {
        this.module = module;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Override
    public String toString() {
        return Json.gson().toJson(this);
    }
}
