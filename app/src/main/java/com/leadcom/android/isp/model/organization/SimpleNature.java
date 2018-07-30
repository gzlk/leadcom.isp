package com.leadcom.android.isp.model.organization;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;
import com.google.gson.reflect.TypeToken;
import com.leadcom.android.isp.lib.Json;
import com.leadcom.android.isp.model.Model;

import java.util.ArrayList;


/**
 * <b>功能描述：</b>更新成员属性的模板<br />
 * <b>创建作者：</b>SYSTEM <br />
 * <b>创建时间：</b>2018/07/30 12:59 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>version: 1.0.0 <br />
 * <b>修改时间：</b>2017/10/04 18:50 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */
public class SimpleNature extends Model {

    public static String toJson(ArrayList<SimpleNature> list) {
        return null == list ? "[]" : Json.gson(new ExclusionStrategy() {
            @Override
            public boolean shouldSkipField(FieldAttributes f) {
                return !f.getName().equals("id") && !f.getName().equals("templateId") && !f.getName().equals("value");
            }

            @Override
            public boolean shouldSkipClass(Class<?> clazz) {
                return false;
            }
        }).toJson(list, new TypeToken<ArrayList<SimpleNature>>() {
        }.getType());
    }

    public SimpleNature() {
        super();
    }

    public SimpleNature(MemberNature nature) {
        setId(nature.getNatureId());
        setTemplateId(nature.getId());
        setValue(nature.getValue());
    }

    private String templateId;
    private String value;

    public String getTemplateId() {
        return templateId;
    }

    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
