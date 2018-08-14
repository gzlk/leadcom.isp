package com.leadcom.android.isp.holder.common;

import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.text.Html;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hlk.hlklib.lib.view.CustomTextView;
import com.leadcom.android.isp.R;
import com.leadcom.android.isp.fragment.base.BaseFragment;
import com.leadcom.android.isp.helper.StringHelper;
import com.leadcom.android.isp.holder.BaseViewHolder;
import com.hlk.hlklib.lib.inject.Click;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.inject.ViewUtility;
import com.leadcom.android.isp.model.Model;
import com.leadcom.android.isp.model.archive.Classify;
import com.leadcom.android.isp.model.archive.Dictionary;

/**
 * <b>功能：</b>单纯文字的viewholder<br />
 * <b>作者：</b>Hsiang Leekwok <br />
 * <b>时间：</b>2016/07/06 12:18 <br />
 * <b>邮箱：</b>xiang.l.g@gmail.com <br />
 */
public class TextViewHolder extends BaseViewHolder {

    @ViewId(R.id.ui_holder_view_text_item_container)
    private CardView container;
    @ViewId(R.id.ui_holder_view_text_item_pre_selector)
    private View preSelector;
    @ViewId(R.id.ui_holder_view_text_item_text)
    private TextView text;
    @ViewId(R.id.ui_holder_view_text_item_selector)
    private CustomTextView icon;
    @ViewId(R.id.ui_holder_view_text_item_last_selector)
    private View lastSelector;
    @ViewId(R.id.ui_holder_view_text_item_top_line)
    private View topLine;
    @ViewId(R.id.ui_holder_view_text_item_bottom_line)
    private View bottomLine;

    private String title;
    private int index = -1;
    private boolean showSelectedEffect = false;

    public TextViewHolder(View itemView, BaseFragment fragment) {
        super(itemView, fragment);
        ViewUtility.bind(this, itemView);
    }

    public void showBottomLine(boolean shown) {
        bottomLine.setVisibility(shown ? View.VISIBLE : View.GONE);
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
            text.setTextColor(getColor(selected ? R.color.colorPrimary : R.color.textColor));
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
            text.setText(Html.fromHtml(string));
            container.setBackgroundColor(Color.TRANSPARENT);
        } else {
            text.setText(null);
        }
    }

    public void showContent(Dictionary dictionary) {
        preSelector.setVisibility(View.GONE);
        lastSelector.setVisibility(View.GONE);
        topLine.setVisibility(View.GONE);
        bottomLine.setBackgroundResource(R.color.windowBackground);
        icon.setVisibility(View.VISIBLE);
        icon.setTextColor(getColor(dictionary.isSelected() ? R.color.colorPrimary : R.color.textColorHintLight));
        text.setText(dictionary.getName());
        text.setTextColor(getColor(dictionary.isSelected() ? R.color.colorPrimary : R.color.textColorHint));
        CardView.LayoutParams params = (CardView.LayoutParams) bottomLine.getLayoutParams();
        params.leftMargin = getDimension(R.dimen.ui_base_dimen_margin_padding);
        params.rightMargin = 0;
    }

    public void showContent(Classify classify) {
        icon.setVisibility(classify.getParentId() == 0 ? View.GONE : View.VISIBLE);
        icon.setTextColor(getColor(classify.isSelected() ? R.color.colorPrimary : R.color.textColorHintLight));
        preSelector.setVisibility(classify.getParentId() == 0 ? View.VISIBLE : View.GONE);
        preSelector.setBackgroundResource(classify.getParentId() == 0 && classify.isSelected() ? R.color.colorPrimary : R.color.transparent_00);
        text.setText(classify.getName());
        text.setTextColor(getColor(classify.isSelected() ? R.color.colorPrimary : R.color.textColorHint));
        lastSelector.setVisibility(classify.getParentId() == 0 ? (classify.isSelected() ? View.GONE : View.VISIBLE) : View.GONE);
        topLine.setVisibility(classify.getParentId() == 0 ? (classify.isSelected() ? View.VISIBLE : View.GONE) : (getAdapterPosition() == 0 ? View.VISIBLE : View.GONE));
        bottomLine.setBackgroundResource(classify.getParentId() == 0 ? (classify.isSelected() ? R.color.colorPrimary : R.color.windowBackground) : R.color.windowBackground);
        CardView.LayoutParams params = (CardView.LayoutParams) bottomLine.getLayoutParams();
        params.leftMargin = classify.getParentId() == 0 ? getDimension(classify.isSelected() ? R.dimen.ui_static_dp_5 : R.dimen.ui_base_dimen_margin_padding) : 0;
        params.rightMargin = classify.getParentId() == 0 ? getDimension(classify.isSelected() ? 0 : R.dimen.ui_static_dp_1) : 0;
    }

    public void showContent(Model model) {
        preSelector.setVisibility(View.GONE);
        lastSelector.setVisibility(View.GONE);
        topLine.setVisibility(View.GONE);
        bottomLine.setBackgroundResource(R.color.windowBackground);
        icon.setVisibility(View.VISIBLE);
        icon.setTextColor(getColor(model.isSelected() ? R.color.colorPrimary : R.color.textColorHintLight));
        text.setText(model.getAccessToken());
        text.setTextColor(getColor(model.isSelected() ? R.color.colorPrimary : R.color.textColorHint));
        CardView.LayoutParams params = (CardView.LayoutParams) bottomLine.getLayoutParams();
        params.leftMargin = getDimension(R.dimen.ui_base_dimen_margin_padding);
        params.rightMargin = 0;
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
        if (null != mOnViewHolderClickListener) {
            mOnViewHolderClickListener.onClick(index <= 0 ? getAdapterPosition() : index);
        }
    }
}
