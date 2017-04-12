package com.gzlk.android.isp.holder;

import android.graphics.Color;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gzlk.android.isp.R;
import com.gzlk.android.isp.fragment.base.BaseFragment;
import com.gzlk.android.isp.helper.StringHelper;
import com.hlk.hlklib.lib.inject.Click;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.inject.ViewUtility;

/**
 * <b>功能：</b>单纯文字的viewholder<br />
 * <b>作者：</b>Hsiang Leekwok <br />
 * <b>时间：</b>2016/07/06 12:18 <br />
 * <b>邮箱：</b>xiang.l.g@gmail.com <br />
 */
public class TextViewHolder extends BaseViewHolder {

    @ViewId(R.id.ui_holder_view_text_item_container)
    private LinearLayout container;
    @ViewId(R.id.ui_holder_view_text_item_text)
    private TextView text;

    private String itemCode, title;
    private int index;
    private boolean showSelectedEffect = false;

    public TextViewHolder(View itemView, BaseFragment fragment) {
        super(itemView, fragment);
        ViewUtility.bind(this, itemView);
    }

    /**
     * 设置是否显示选中效果（背景更换）
     */
    public void showSelectedEffect(boolean show) {
        showSelectedEffect = show;
    }

    public void showContent(String string, boolean selected) {
        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, getDimension(R.dimen.ui_base_dimen_button_height));
        container.setLayoutParams(params);
        if (showSelectedEffect) {
            //container.setBackgroundResource(selected ? R.drawable.ui_background_bottom_border : R.drawable.ui_normal_bg);
            text.setTextColor(getColor(selected ? R.color.colorPrimaryDark : R.color.textColor));
        } else {
            //container.setBackgroundResource(R.drawable.ui_normal_bg);
            text.setTextColor(getColor(R.color.textColor));
        }
        String[] strings = string.split("\\|", -1);
        index = Integer.valueOf(strings[0]);
        text.setText(strings[1]);
        int padding = getDimension(R.dimen.ui_base_dimen_margin_padding);
        text.setPadding(padding, 0, padding, 0);
    }

    public void showContent(String string) {
        if (!StringHelper.isEmpty(string)) {
            text.setText(string);
            container.setBackgroundColor(Color.TRANSPARENT);
        }
    }

    /**
     * 设置文字的对齐方式
     */
    public void setGravity(int gravity) {
        text.setGravity(gravity);
    }

    private void showContent() {
        if (StringHelper.isEmpty(title)) {
            title = "他什么也没写";
        }
        text.setText(Html.fromHtml(title));
    }

    @Click({R.id.ui_holder_view_text_item_container})
    private void click(View view) {
        if (null != mOnItemClickListener) {
            mOnItemClickListener.onItemClick(index);
        }
    }

    private OnItemClickListener mOnItemClickListener;

    /**
     * 添加点击处理
     */
    public void addOnItemClickListener(OnItemClickListener l) {
        mOnItemClickListener = l;
    }

    /**
     * 子项目点击事件接口
     */
    public interface OnItemClickListener {
        /**
         * 子项目点击
         *
         * @param index 当前点击项的文字
         */
        void onItemClick(int index);
    }
}
