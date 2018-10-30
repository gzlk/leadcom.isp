package com.leadcom.android.isp.model.common;

import android.graphics.Color;

import com.leadcom.android.isp.R;
import com.leadcom.android.isp.helper.StringHelper;
import com.leadcom.android.isp.model.Model;

import java.util.ArrayList;

/**
 * <b>功能描述：</b>文档分享按钮<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2018/07/19 10:01 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2018/07/19 10:01 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */
public class ShareItem extends Model {

    public static ArrayList<ShareItem> items;

    public static void init() {
        if (null == items) {
            items = new ArrayList<>();
            String[] strings = StringHelper.getStringArray(R.array.ui_text_archive_details_popup_items);
            for (String string : strings) {
                ShareItem item = new ShareItem(string);
                if (!items.contains(item)) {
                    items.add(item);
                }
            }
        }
    }

    // 索引
    private int index;
    // 是否可见
    private int visible;
    // 图标
    private String icon;
    // 图标颜色
    private int iconColor;
    // 图标背景色
    private int iconBackground, iconActiveBackground;
    // 文字
    private String text;

    public ShareItem(String sources) {
        String[] strings = sources.split("\\|", -1);
        setId(strings[0]);
        index = Integer.valueOf(strings[0]);
        visible = Integer.valueOf(strings[1]);
        icon = strings[2];
        iconColor = Color.parseColor(strings[3]);
        iconBackground = Color.parseColor(strings[4]);
        iconActiveBackground = Color.parseColor(strings[5]);
        text = strings[6];
    }

    public int getIndex() {
        return index;
    }

    public void setIndex(int index) {
        this.index = index;
    }

    public int getVisible() {
        return visible;
    }

    public void setVisible(int visible) {
        this.visible = visible;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public int getIconColor() {
        return iconColor;
    }

    public void setIconColor(int iconColor) {
        this.iconColor = iconColor;
    }

    public int getIconBackground() {
        return iconBackground;
    }

    public void setIconBackground(int iconBackground) {
        this.iconBackground = iconBackground;
    }

    public int getIconActiveBackground() {
        return iconActiveBackground;
    }

    public void setIconActiveBackground(int iconActiveBackground) {
        this.iconActiveBackground = iconActiveBackground;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean visible() {
        return visible > 0;
    }

    public boolean wx() {
        return index == 0;
    }

    public boolean timeline() {
        return index == 1;
    }

    public boolean qq() {
        return index == 2;
    }

    public boolean qzone() {
        return index == 3;
    }

    public boolean deletable() {
        return index == 5;
    }

    public boolean forwardable() {
        return index == 6;
    }

    public boolean recommendable() {
        return index == 7;
    }

    public boolean unrecommendable() {
        return index == 8;
    }

    public boolean awardable() {
        return index == 9;
    }

    public boolean unawardable() {
        return index == 10;
    }

    public boolean classfiyable() {
        return index == 11;
    }

    public boolean repliable() {
        return index == 12;
    }

    /**
     * 是否可以下发
     */
    public boolean transformable() {
        return index == 13;
    }

    /**
     * 是否设置档案属性
     */
    public boolean propertyEditable() {
        return index == 14;
    }
}
