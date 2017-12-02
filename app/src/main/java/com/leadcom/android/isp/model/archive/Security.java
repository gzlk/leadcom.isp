package com.leadcom.android.isp.model.archive;

import com.leadcom.android.isp.helper.StringHelper;
import com.leadcom.android.isp.model.Model;

/**
 * <b>功能描述：</b>档案隐私选项<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/05/16 22:05 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/05/16 22:05 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class Security extends Model {

    private int index;
    private String text;
    private String description;
    // 是否个人可见选项
    private boolean userVisible;
    // 是否组织可见选项
    private boolean groupVisible;

    public Security(String text) {
        String[] strings = text.split("\\|", -1);
        index = Integer.valueOf(strings[0]);
        setId(String.valueOf(index));
        this.text = strings[1];
        description = strings[2];
        boolean selected = !StringHelper.isEmpty(strings[3]) && strings[3].equals("1");
        setLocalDeleted(selected);
        setSelected(selected);
        userVisible = !StringHelper.isEmpty(strings[4]) && strings[4].equals("1");
        groupVisible = !StringHelper.isEmpty(strings[5]) && strings[5].equals("1");
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isUserVisible() {
        return userVisible;
    }

    public void setUserVisible(boolean userVisible) {
        this.userVisible = userVisible;
    }

    public boolean isGroupVisible() {
        return groupVisible;
    }

    public void setGroupVisible(boolean groupVisible) {
        this.groupVisible = groupVisible;
    }
}
