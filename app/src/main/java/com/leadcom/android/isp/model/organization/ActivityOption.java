package com.leadcom.android.isp.model.organization;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.reflect.TypeToken;
import com.leadcom.android.isp.lib.Json;
import com.leadcom.android.isp.model.Model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * <b>功能描述：</b>活动附加选项<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2018/11/29 12:14 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2018/11/29 12:14  <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */
public class ActivityOption extends Model {

    public static String toJSON(ArrayList<ActivityOption> options, String[] savedFields) {
        final List<String> ids = Arrays.asList(savedFields);
        return Json.gson(new ExclusionStrategy() {
            @Override
            public boolean shouldSkipField(FieldAttributes f) {
                return !ids.contains(f.getName());
            }

            @Override
            public boolean shouldSkipClass(Class<?> clazz) {
                return false;
            }
        }).toJson(options, new TypeToken<ArrayList<ActivityOption>>() {
        }.getType());
    }

    private String groActivityId;
    private String additionalOptionName;

    public String getGroActivityId() {
        return groActivityId;
    }

    public void setGroActivityId(String groActivityId) {
        this.groActivityId = groActivityId;
    }

    public String getAdditionalOptionName() {
        return additionalOptionName;
    }

    public void setAdditionalOptionName(String additionalOptionName) {
        this.additionalOptionName = additionalOptionName;
    }
}
