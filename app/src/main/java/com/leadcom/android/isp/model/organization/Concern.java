package com.leadcom.android.isp.model.organization;

import com.leadcom.android.isp.R;
import com.leadcom.android.isp.helper.StringHelper;
import com.leadcom.android.isp.model.archive.Classify;

import java.util.ArrayList;

/**
 * <b>功能描述：</b>关注的组织<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/05/07 09:46 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/05/07 09:46 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */
public class Concern extends Organization {

    /**
     * 关注的类型
     */
    public interface Type {
        /**
         * 当前组织
         */
        int SELF = 0;
        /**
         * 上级组织
         */
        int UPPER = 1;
        /**
         * 下级组织
         */
        int SUBGROUP = 2;
        /**
         * 友好组织
         */
        int FRIEND = 3;
        /**
         * 未关注
         */
        int CONCERNABLE = 4;
    }

    // 关注类型:1.上级组织 2.下级组织 3.友好组织
    private int type;
    private String groupId;
    private String groupName;
    private String reportStr;
    private int authorized;
    private String allowGroupId, allowGroupName, targerResource;
    private ArrayList<Classify> docClassifyList;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
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

    public String getReportStr() {
        return reportStr;
    }

    public void setReportStr(String reportStr) {
        this.reportStr = reportStr;
    }

    public boolean isAuthorized() {
        return authorized == AuthorizeType.AUTHORIZED;
    }

    public int getAuthorized() {
        return authorized;
    }

    public void setAuthorized(int authorized) {
        this.authorized = authorized;
    }

    public String getAllowGroupId() {
        return allowGroupId;
    }

    public void setAllowGroupId(String allowGroupId) {
        this.allowGroupId = allowGroupId;
    }

    public String getAllowGroupName() {
        return allowGroupName;
    }

    public void setAllowGroupName(String allowGroupName) {
        this.allowGroupName = allowGroupName;
    }

    public String getTargerResource() {
        return targerResource;
    }

    public void setTargerResource(String targerResource) {
        this.targerResource = targerResource;
    }

    public ArrayList<Classify> getDocClassifyList() {
        if (null == docClassifyList) {
            docClassifyList = new ArrayList<>();
        }
        return docClassifyList;
    }

    public void setDocClassifyList(ArrayList<Classify> docClassifyList) {
        this.docClassifyList = docClassifyList;
    }

    /**
     * 获取关注的组织类型
     */
    public static String getTypeString(int type) {
        return StringHelper.getStringArray(R.array.ui_organization_concerned_type)[type];
    }

    @Override
    public boolean isConcerned() {
        return type <= Type.FRIEND;
    }
}
