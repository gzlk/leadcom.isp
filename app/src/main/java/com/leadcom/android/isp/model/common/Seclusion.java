package com.leadcom.android.isp.model.common;

import com.leadcom.android.isp.R;
import com.leadcom.android.isp.helper.StringHelper;
import com.leadcom.android.isp.model.Model;

import java.util.ArrayList;
import java.util.List;

/**
 * <b>功能描述：</b>隐私选项内容<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/05/28 00:13 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/05/28 00:13 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class Seclusion extends Model {
    /**
     * 隐私设置类型
     */
    public interface Type {
        /**
         * 私密，只能自己看
         */
        int Private = 4;
        /**
         * 公开，对所有人公开
         */
        int Public = 1;
        /**
         * 组织内可见
         */
        int Group = 2;
        /**
         * 指定部分人可查看
         */
        int Specify = 3;
    }

    private static String getSecurityNames(List<String> list) {
        if (null == list || list.size() < 1) {
            return "";
        }
        String ret = "";
        for (String string : list) {
            ret += (isEmpty(ret) ? "" : ",") + string;
        }
        return ret;
    }

    public static String getPrivacy(Seclusion seclusion) {
        String names = getSecurityNames(seclusion.getUserNames());
        switch (seclusion.getStatus()) {
            case Seclusion.Type.Private:
                // 只能自己可见
                return StringHelper.getString(R.string.ui_base_text_private);
            case Seclusion.Type.Public:
                // 对所有人公开
                return StringHelper.getString(R.string.ui_base_text_public);
            case Seclusion.Type.Group:
                // 组织内公开
                return StringHelper.getString(R.string.ui_security_force_to_group);
            case Seclusion.Type.Specify:
                // 对指定人公开
                return StringHelper.getString(R.string.ui_security_force_to_user, names);
        }
        return StringHelper.getString(R.string.ui_security_fragment_title);
    }

    public Seclusion() {
    }

    private int status;
    private String archiveId;
    private ArrayList<String> userIds;
    private ArrayList<String> userNames;
    private ArrayList<String> groupIds;
    private ArrayList<String> groupNames;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getArchiveId() {
        return archiveId;
    }

    public void setArchiveId(String archiveId) {
        this.archiveId = archiveId;
    }

    public ArrayList<String> getUserIds() {
        return userIds;
    }

    public void setUserIds(ArrayList<String> userIds) {
        this.userIds = userIds;
        // 用户id列表为空或者长度为0时表示对组织公开
        status = (null == userIds || userIds.size() < 1) ? Type.Public : Type.Specify;
    }

    public ArrayList<String> getUserNames() {
        return userNames;
    }

    public void setUserNames(ArrayList<String> userNames) {
        this.userNames = userNames;
    }

    public ArrayList<String> getGroupIds() {
        return groupIds;
    }

    public void setGroupIds(ArrayList<String> groupIds) {
        this.groupIds = groupIds;
    }

    public ArrayList<String> getGroupNames() {
        return groupNames;
    }

    public void setGroupNames(ArrayList<String> groupNames) {
        this.groupNames = groupNames;
    }
}
