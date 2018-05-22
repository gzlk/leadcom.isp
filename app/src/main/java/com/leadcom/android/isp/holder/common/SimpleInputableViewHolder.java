package com.leadcom.android.isp.holder.common;

import android.view.View;
import android.widget.TextView;

import com.hlk.hlklib.lib.inject.Click;
import com.leadcom.android.isp.R;
import com.leadcom.android.isp.fragment.base.BaseFragment;
import com.leadcom.android.isp.helper.StringHelper;
import com.leadcom.android.isp.holder.BaseViewHolder;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.inject.ViewUtility;
import com.hlk.hlklib.lib.view.ClearEditText;

/**
 * <b>功能描述：</b><br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/04/25 08:58 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/04/25 08:58 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class SimpleInputableViewHolder extends BaseViewHolder {

    @ViewId(R.id.ui_holder_view_simple_inputable_title)
    private TextView titleTextView;
    @ViewId(R.id.ui_holder_view_simple_inputable_content)
    private ClearEditText contentView;
    @ViewId(R.id.ui_holder_view_simple_clickable_right_icon_add)
    private View rightAddIcon;
    @ViewId(R.id.ui_holder_view_simple_clickable_right_icon)
    private View rightIcon;

    public SimpleInputableViewHolder(View itemView, BaseFragment fragment) {
        super(itemView, fragment);
        ViewUtility.bind(this, itemView);
    }

    /**
     * 显示内容
     * 标题，内容，hint，内容过滤，内容验证，最大长度
     */
    public void showContent(String string) {
        String[] strings = string.split("\\|");
        if (strings.length < 6) {
            throw new IllegalArgumentException("cannot display contents less than 6 items");
        }
        if (strings.length > 6) {
            int icon = Integer.valueOf(strings[6]);
            rightIcon.setVisibility(icon >= 1 ? View.VISIBLE : View.GONE);
            rightAddIcon.setVisibility(icon >= 2 ? View.VISIBLE : View.GONE);
        }
        showContent(strings[0], strings[1], strings[2], strings[3], strings[4], Integer.valueOf(strings[5]));
    }

    public void setMaximumLines(int maxLines) {
        contentView.setMaxLines(maxLines);
    }

    public void focusEnd() {
        contentView.focusEnd();
    }

    public void setMinimumHeight(int height) {
        contentView.setMinimumHeightLimit(height);
    }

    public void setMaximumHeight(int height) {
        contentView.setMaximumHeightLimit(height);
    }

    /**
     * 获取输入内容
     */
    public String getValue() {
        return contentView.getValue();
    }

    public void showContent(String title, String value, String hint, String valueExtract, String valueVerify, int maxLength) {
        titleTextView.setText(title);
        titleTextView.setVisibility(isEmpty(title) ? View.GONE : View.VISIBLE);
        contentView.setValue(value);
        contentView.setTextHint(hint);
        contentView.setValueExtract(valueExtract);
        contentView.setValueVerify(valueVerify);
        contentView.setMaxLength(maxLength);
    }

    @Click({R.id.ui_holder_view_simple_clickable_right_icon_clickable})
    private void viewClick(View view) {
        if (null != mOnViewHolderElementClickListener) {
            mOnViewHolderElementClickListener.onClick(view, getAdapterPosition());
        }
    }
}
