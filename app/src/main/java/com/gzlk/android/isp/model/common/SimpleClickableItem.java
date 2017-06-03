package com.gzlk.android.isp.model.common;

import com.gzlk.android.isp.helper.StringHelper;
import com.gzlk.android.isp.model.Model;

/**
 * <b>功能描述：</b><br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/05/08 08:15 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/05/08 08:15 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class SimpleClickableItem extends Model {
    private int index;
    private String title;
    private String value;
    private String source;
    private boolean iconVisible = false;
    private boolean addVisible = false;

    public SimpleClickableItem(String text) {
        source = text;
        reset();
    }

    private void reset() {
        String[] strings = source.split("\\|", -1);
        setId(strings[0]);
        index = Integer.valueOf(strings[0]);
        title = strings[1];
        value = strings[2];
        if (strings.length > 3) {
            if (isEmpty(strings[3])) {
                iconVisible = false;
            } else {
                try {
                    int i = Integer.valueOf(strings[3]);
                    iconVisible = i >= 1;
                    addVisible = i >= 2;
                } catch (Exception e) {
                    //e.printStackTrace();
                }
            }
        }
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public boolean isIconVisible() {
        return iconVisible;
    }

    public void setIconVisible(boolean iconVisible) {
        this.iconVisible = iconVisible;
    }

    public boolean isAddVisible() {
        return addVisible;
    }

    public void setAddVisible(boolean addVisible) {
        this.addVisible = addVisible;
    }
}
