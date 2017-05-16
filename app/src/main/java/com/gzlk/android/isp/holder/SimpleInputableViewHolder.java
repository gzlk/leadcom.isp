package com.gzlk.android.isp.holder;

import android.view.View;
import android.widget.TextView;

import com.gzlk.android.isp.R;
import com.gzlk.android.isp.fragment.base.BaseFragment;
import com.gzlk.android.isp.helper.StringHelper;
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
        String value = contentView.getValue();
        return StringHelper.escapeToHtml(value);
    }

    public void showContent(String title, String value, String hint, String valueExtract, String valueVerify, int maxLength) {
        titleTextView.setText(title);
        titleTextView.setVisibility(StringHelper.isEmpty(title) ? View.GONE : View.VISIBLE);
        contentView.setValue(value);
        contentView.setTextHint(hint);
        contentView.setValueExtract(valueExtract);
        contentView.setValueVerify(valueVerify);
        contentView.setMaxLength(maxLength);
    }
}
