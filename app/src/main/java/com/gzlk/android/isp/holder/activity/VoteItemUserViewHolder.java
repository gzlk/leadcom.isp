package com.gzlk.android.isp.holder.activity;

import android.view.View;
import android.widget.TextView;

import com.gzlk.android.isp.R;
import com.gzlk.android.isp.fragment.base.BaseFragment;
import com.gzlk.android.isp.holder.BaseViewHolder;
import com.gzlk.android.isp.lib.view.ImageDisplayer;
import com.gzlk.android.isp.model.activity.vote.AppVoteRecord;
import com.gzlk.android.isp.model.organization.Member;
import com.hlk.hlklib.lib.inject.Click;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.inject.ViewUtility;

/**
 * <b>功能描述：</b>投票选项详情中的投票人<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/08/23 14:35 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/08/23 14:35 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class VoteItemUserViewHolder extends BaseViewHolder {

    @ViewId(R.id.ui_holder_view_activity_vote_item_user_header)
    private ImageDisplayer header;
    @ViewId(R.id.ui_holder_view_activity_vote_item_user_name)
    private TextView name;
    @ViewId(R.id.ui_holder_view_activity_vote_item_deleter)
    private View deleter;

    public VoteItemUserViewHolder(View itemView, BaseFragment fragment) {
        super(itemView, fragment);
        ViewUtility.bind(this, itemView);
        header.addOnImageClickListener(new ImageDisplayer.OnImageClickListener() {
            @Override
            public void onImageClick(String url) {
                performClick();
            }
        });
    }

    public void showContent(AppVoteRecord record) {
        header.displayImage(record.getHeadPhoto(), getDimension(R.dimen.ui_base_user_header_image_size), false, false);
        name.setText(record.getUserName());
    }

    public void showContent(Member member) {
        header.displayImage(member.getHeadPhoto(), getDimension(R.dimen.ui_base_user_header_image_size), false, false);
        name.setText(member.getUserName());
        deleter.setVisibility(member.isSelectable() ? View.VISIBLE : View.GONE);
    }

    @Click({R.id.ui_holder_view_activity_vote_item_user_info,
            R.id.ui_holder_view_activity_vote_item_deleter})
    private void elementClick(View view) {
        switch (view.getId()) {
            case R.id.ui_holder_view_activity_vote_item_user_info:
                performClick();
                break;
            case R.id.ui_holder_view_activity_vote_item_deleter:
                if (null != mOnHandlerBoundDataListener) {
                    mOnHandlerBoundDataListener.onHandlerBoundData(VoteItemUserViewHolder.this);
                }
                break;
        }
        performClick();
    }

    private void performClick() {
        if (null != mOnViewHolderClickListener) {
            mOnViewHolderClickListener.onClick(getAdapterPosition());
        }
    }
}
