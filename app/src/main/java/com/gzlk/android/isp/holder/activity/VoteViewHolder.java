package com.gzlk.android.isp.holder.activity;

import android.view.View;
import android.widget.TextView;

import com.gzlk.android.isp.R;
import com.gzlk.android.isp.fragment.base.BaseFragment;
import com.gzlk.android.isp.helper.StringHelper;
import com.gzlk.android.isp.holder.BaseViewHolder;
import com.gzlk.android.isp.lib.view.ExpandableTextView;
import com.gzlk.android.isp.lib.view.ImageDisplayer;
import com.gzlk.android.isp.model.activity.vote.AppVote;
import com.hlk.hlklib.lib.inject.Click;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.inject.ViewUtility;

/**
 * <b>功能描述：</b><br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/06/29 16:43 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/06/29 16:43 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class VoteViewHolder extends BaseViewHolder {

    @ViewId(R.id.ui_individual_header_image)
    private ImageDisplayer headerImage;
    @ViewId(R.id.ui_individual_header_name)
    private TextView headerName;
    @ViewId(R.id.ui_holder_view_activity_vote_item_title)
    private TextView titleView;
    @ViewId(R.id.ui_holder_view_activity_vote_item_desc)
    private ExpandableTextView descView;
    @ViewId(R.id.ui_holder_view_activity_vote_item_status_text)
    private TextView statusTextView;
    @ViewId(R.id.ui_holder_view_activity_vote_item_time)
    private TextView timeView;
    @ViewId(R.id.ui_holder_view_activity_vote_item_status_flag)
    private TextView statusFlagView;

    public VoteViewHolder(View itemView, BaseFragment fragment) {
        super(itemView, fragment);
        ViewUtility.bind(this, itemView);
    }

    public void showContent(AppVote appVote) {
        headerName.setText(appVote.getCreatorName());
        titleView.setText(appVote.getTitle());
        titleView.setVisibility(isEmpty(appVote.getTitle()) ? View.GONE : View.VISIBLE);
        descView.setText(appVote.getContent());
        descView.makeExpandable();
        timeView.setText(fragment().formatTimeAgo(appVote.getCreateDate()));
        boolean ended = appVote.isEnded();
        statusTextView.setVisibility(ended ? View.VISIBLE : View.INVISIBLE);
        statusFlagView.setText(ended ? R.string.ui_activity_vote_details_status_ended : R.string.ui_activity_vote_details_status_voting);
        statusFlagView.setBackgroundColor(getColor(ended ? R.color.textColorHintLight : R.color.colorPrimary));
    }

    public void showVoteType(AppVote appVote) {
        statusTextView.setVisibility(View.GONE);
        int i = appVote.getType();
        i = i <= 1 ? 1 : 2;
        timeView.setText(StringHelper.getStringArray(R.array.ui_activity_vote_types)[i]);
    }

    @Click({R.id.ui_holder_view_activity_vote_item})
    private void onClick(View view) {
        switch (view.getId()) {
            case R.id.ui_holder_view_activity_vote_item:
                if (null != mOnViewHolderClickListener) {
                    mOnViewHolderClickListener.onClick(getAdapterPosition());
                }
                break;
        }
    }
}
