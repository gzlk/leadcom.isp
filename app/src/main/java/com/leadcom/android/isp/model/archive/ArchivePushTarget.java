package com.leadcom.android.isp.model.archive;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.reflect.TypeToken;
import com.leadcom.android.isp.lib.Json;
import com.leadcom.android.isp.model.Model;

import java.util.ArrayList;

/**
 * <b>功能描述：</b>档案推送时的组织对象<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2018/07/25 12:44 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2018/07/25 12:44 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */
public class ArchivePushTarget extends Model {

    public static String toJson(ArrayList<ArchivePushTarget> list) {
        return null == list ? "[]" : Json.gson(new ExclusionStrategy() {
            @Override
            public boolean shouldSkipField(FieldAttributes f) {
                return !f.getName().contains("docClassifyId") && !f.getName().contains("targertGroupId");
            }

            @Override
            public boolean shouldSkipClass(Class<?> clazz) {
                return false;
            }
        }).toJson(list, new TypeToken<ArrayList<ArchivePushTarget>>() {
        }.getType());
    }

    private String docClassifyId;
    private String targertGroupId;

    public String getDocClassifyId() {
        return docClassifyId;
    }

    public void setDocClassifyId(String docClassifyId) {
        this.docClassifyId = docClassifyId;
    }

    public String getTargertGroupId() {
        return targertGroupId;
    }

    public void setTargertGroupId(String targertGroupId) {
        this.targertGroupId = targertGroupId;
    }
}
