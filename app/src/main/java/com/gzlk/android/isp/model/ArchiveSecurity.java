package com.gzlk.android.isp.model;

import com.gzlk.android.isp.helper.StringHelper;

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

public class ArchiveSecurity extends Model {

    private int index;
    private String text;
    private String description;
    private boolean selected;

    public ArchiveSecurity(String text) {
        String[] strings = text.split("\\|", -1);
        index = Integer.valueOf(strings[0]);
        setId(String.valueOf(index));
        this.text = strings[1];
        description = strings[2];
        selected = !StringHelper.isEmpty(strings[3]) && strings[3].equals("1");
        setLocalDeleted(selected);
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

    public boolean isSelected() {
        return selected;
    }

    public void setSelected(boolean selected) {
        this.selected = selected;
    }
}
