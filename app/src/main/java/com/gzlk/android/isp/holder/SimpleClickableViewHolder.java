package com.gzlk.android.isp.holder;

import android.view.View;
import android.widget.TextView;

import com.gzlk.android.isp.R;
import com.gzlk.android.isp.fragment.base.BaseFragment;
import com.gzlk.android.isp.helper.StringHelper;
import com.gzlk.android.isp.model.Model;
import com.hlk.hlklib.lib.inject.Click;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.inject.ViewUtility;
import com.hlk.hlklib.lib.view.CustomTextView;

/**
 * <b>功能描述：</b>简单的可点击的viewholder<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/04/15 23:48 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/04/15 23:48 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class SimpleClickableViewHolder extends BaseViewHolder {

    @ViewId(R.id.ui_holder_view_simple_clickable_title)
    private TextView titleTextView;
    @ViewId(R.id.ui_holder_view_simple_clickable_value)
    private TextView valueTextView;
    @ViewId(R.id.ui_holder_view_simple_clickable_right_icon)
    private CustomTextView rightIcon;

    public SimpleClickableViewHolder(View itemView, BaseFragment fragment) {
        super(itemView, fragment);
        ViewUtility.bind(this, itemView);
    }

    /**
     * 是否需要最左侧的空白距离
     */
    public void isNeedLeftPadding(boolean need) {
        int top = itemView.getPaddingTop();
        int right = itemView.getPaddingRight();
        int bottom = itemView.getPaddingBottom();
        itemView.setPadding(need ? right : 0, top, right, bottom);
    }

    public void showContent(Model model) {
        showContent(model.getId());
    }

    public void showContent(String string) {
        String[] strings = string.split("\\|", -1);
        showContent(Integer.valueOf(strings[0]), strings[1], strings[2]);
        if (strings.length > 3) {
            boolean invisible = StringHelper.isEmpty(strings[3]) || strings[3].equals("0");
            rightIcon.setVisibility(invisible ? View.GONE : View.VISIBLE);
        }
    }

    public void showContent(int index, String title, String value) {
        this.index = index;
        titleTextView.setText(title);
        valueTextView.setText(value);
    }

    private int index;

    @Click({R.id.ui_holder_view_simple_clickable})
    private void click(View view) {
        if (null != mOnViewHolderClickListener) {
            int pos = getAdapterPosition();
            mOnViewHolderClickListener.onClick(pos < 0 ? index : pos);
        }
    }
}
