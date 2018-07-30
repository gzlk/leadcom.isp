package com.leadcom.android.isp.model.organization;

import com.leadcom.android.isp.model.Model;

import java.util.ArrayList;


/**
 * <b>功能描述：</b>成员分类<br />
 * <b>创建作者：</b>SYSTEM <br />
 * <b>创建时间：</b>2018/07/29 23:28 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>version: 1.0.0 <br />
 * <b>修改时间：</b>2017/10/04 18:50 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */
public class MemberClassify extends Model {

    // 分类名称
    private String name;
    // 个人信息属性模板列表
    private ArrayList<MemberNature> appUserNatureTemplateList;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public ArrayList<MemberNature> getAppUserNatureTemplateList() {
        if (null == appUserNatureTemplateList) {
            appUserNatureTemplateList = new ArrayList<>();
        }
        return appUserNatureTemplateList;
    }

    public void setAppUserNatureTemplateList(ArrayList<MemberNature> appUserNatureTemplateList) {
        this.appUserNatureTemplateList = appUserNatureTemplateList;
    }
}
