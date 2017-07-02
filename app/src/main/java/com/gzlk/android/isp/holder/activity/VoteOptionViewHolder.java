package com.gzlk.android.isp.holder.activity;

import android.view.View;
import android.widget.TextView;

import com.gzlk.android.isp.R;
import com.gzlk.android.isp.fragment.base.BaseFragment;
import com.gzlk.android.isp.helper.StringHelper;
import com.gzlk.android.isp.holder.BaseViewHolder;
import com.gzlk.android.isp.model.activity.vote.AppVoteItem;
import com.hlk.hlklib.lib.inject.Click;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.inject.ViewUtility;
import com.hlk.hlklib.lib.view.CustomTextView;

/**
 * <b>功能描述：</b>投票选项<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/06/30 16:58 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/06/30 16:58 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class VoteOptionViewHolder extends BaseViewHolder {

    @ViewId(R.id.ui_holder_view_vote_option_icon)
    private CustomTextView iconView;
    @ViewId(R.id.ui_holder_view_vote_option_text)
    private TextView textView;
    @ViewId(R.id.ui_holder_view_vote_option_number)
    private TextView numberView;

    public VoteOptionViewHolder(View itemView, BaseFragment fragment) {
        super(itemView, fragment);
        ViewUtility.bind(this, itemView);
    }

    public void showContent(AppVoteItem item, boolean multiChoose) {
        if (multiChoose) {
            showMultiChooseIcon(item);
        } else {
            showSingleChooseIcon(item);
        }
        textView.setText(item.getDesc());
        numberView.setText(StringHelper.getString(R.string.ui_activity_vote_details_number, item.getNum()));
    }

    public void showEnded(boolean ended) {
        iconView.setVisibility(ended ? View.GONE : View.VISIBLE);
    }

    private void showMultiChooseIcon(AppVoteItem item) {
        iconView.setText(item.isSelected() ? R.string.ui_icon_checkbox_checked : R.string.ui_icon_checkbox_unchecked);
        iconView.setTextColor(getColor(!item.isSelected() ? R.color.textColorHint : R.color.colorPrimary));
    }

    private void showSingleChooseIcon(AppVoteItem item) {
        iconView.setText(item.isSelected() ? R.string.ui_icon_radio_selected : R.string.ui_icon_radio_unselected);
        iconView.setTextColor(getColor(!item.isSelected() ? R.color.textColorHint : R.color.colorPrimary));
    }

    @Click({R.id.ui_holder_view_vote_option})
    private void click(View view) {
        if (null != mOnViewHolderClickListener) {
            mOnViewHolderClickListener.onClick(getAdapterPosition());
        }
    }
}
