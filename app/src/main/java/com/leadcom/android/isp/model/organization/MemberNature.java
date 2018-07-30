package com.leadcom.android.isp.model.organization;

import com.leadcom.android.isp.model.Model;


/**
 * <b>功能描述：</b>成员属性<br />
 * <b>创建作者：</b>SYSTEM <br />
 * <b>创建时间：</b>2018/07/29 23:34 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>version: 1.0.0 <br />
 * <b>修改时间：</b>2017/10/04 18:50 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */
public class MemberNature extends Model {

    /**
     * 属性类型
     */
    public interface NatureType {
        /**
         * 文本类
         */
        String TEXT = "text";
        /**
         * 时间填写类
         */
        String TIME = "time";
    }

    // 模板名称
    private String name;
    // 模板值
    private String value;
    // 属性类型:text(标签类),time(时间类)
    private String type;
    // 成员数
    private long memberNum;
    // 是否选择
    private boolean choose;
    // 个人信息属性ID
    private String natureId;
    // 更新时需要设置的id
    private String templateId;
    // 父级classify的id
    private String parentId;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public boolean isText() {
        return !isEmpty(type) && type.equals(NatureType.TEXT);
    }

    public boolean isTime() {
        return !isEmpty(type) && type.equals(NatureType.TIME);
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public long getMemberNum() {
        return memberNum;
    }

    public void setMemberNum(long memberNum) {
        this.memberNum = memberNum;
    }

    public boolean isChoose() {
        return choose;
    }

    public void setChoose(boolean choose) {
        this.choose = choose;
    }

    public String getNatureId() {
        return natureId;
    }

    public void setNatureId(String natureId) {
        this.natureId = natureId;
    }

    public String getTemplateId() {
        return templateId;
    }

    public void setTemplateId(String templateId) {
        this.templateId = templateId;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }
}
