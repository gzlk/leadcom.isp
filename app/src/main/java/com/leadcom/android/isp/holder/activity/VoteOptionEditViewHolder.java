package com.leadcom.android.isp.holder.activity;

import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.leadcom.android.isp.R;
import com.leadcom.android.isp.fragment.base.BaseFragment;
import com.leadcom.android.isp.holder.BaseViewHolder;
import com.leadcom.android.isp.model.activity.vote.AppVoteItem;
import com.hlk.hlklib.lib.inject.Click;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.inject.ViewUtility;
import com.hlk.hlklib.lib.view.ClearEditText;
import com.hlk.hlklib.lib.view.CustomTextView;

/**
 * <b>功能描述：</b>投票选项编辑框<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/06/29 02:59 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/06/29 02:59 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class VoteOptionEditViewHolder extends BaseViewHolder {

    @ViewId(R.id.ui_holder_view_vote_option_editable_icon)
    private CustomTextView iconView;
    @ViewId(R.id.ui_holder_view_vote_option_editable_text)
    private TextView textView;
    @ViewId(R.id.ui_holder_view_vote_option_editable_content)
    private ClearEditText contentView;

    public VoteOptionEditViewHolder(View itemView, BaseFragment fragment) {
        super(itemView, fragment);
        ViewUtility.bind(this, itemView);
        contentView.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (null != mOnHandlerBoundDataListener) {
                    AppVoteItem item = (AppVoteItem) mOnHandlerBoundDataListener.onHandlerBoundData(VoteOptionEditViewHolder.this);
                    if (null != item && !item.getId().equals("+")) {
                        item.setContent(contentView.getValue());
                    }
                }
            }
        });
    }

    public void showContent(AppVoteItem item) {
        // 是否可以显示+或-号
        iconView.setVisibility(item.isLocalDeleted() ? View.GONE : View.VISIBLE);
        resetMargin(item);
        contentView.setValue(item.getContent());
        contentView.setVisibility(item.isSelectable() ? View.GONE : View.VISIBLE);
        textView.setVisibility(item.isSelectable() ? View.VISIBLE : View.GONE);
        textView.setText(item.getContent());
        iconView.setText(item.isSelectable() ? R.string.ui_icon_add_solid : R.string.ui_icon_subtract_solid);
        iconView.setTextColor(getColor(item.isSelectable() ? R.color.colorPrimary : R.color.colorAccent));
    }

    private void resetMargin(AppVoteItem item) {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) contentView.getLayoutParams();
        int margin = getDimension(R.dimen.ui_base_dimen_margin_padding);
        params.leftMargin = item.isLocalDeleted() ? margin : 0;
        contentView.setLayoutParams(params);
    }

    @Click({R.id.ui_holder_view_vote_option_editable_icon,
            R.id.ui_holder_view_vote_option_editable_text})
    private void click(View view) {
        switch (view.getId()) {
            case R.id.ui_holder_view_vote_option_editable_icon:
            case R.id.ui_holder_view_vote_option_editable_text:
                // 添加或删除
                if (null != mOnViewHolderClickListener) {
                    mOnViewHolderClickListener.onClick(getAdapterPosition());
                }
                break;
        }
    }
}
