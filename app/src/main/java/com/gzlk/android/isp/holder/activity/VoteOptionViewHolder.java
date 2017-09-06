package com.gzlk.android.isp.holder.activity;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.android.flexbox.FlexboxLayout;
import com.gzlk.android.isp.R;
import com.gzlk.android.isp.fragment.base.BaseFragment;
import com.gzlk.android.isp.helper.StringHelper;
import com.gzlk.android.isp.holder.BaseViewHolder;
import com.gzlk.android.isp.lib.view.ImageDisplayer;
import com.gzlk.android.isp.model.activity.vote.AppVote;
import com.gzlk.android.isp.model.activity.vote.AppVoteItem;
import com.gzlk.android.isp.model.activity.vote.AppVoteRecord;
import com.hlk.hlklib.lib.inject.Click;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.inject.ViewUtility;
import com.hlk.hlklib.lib.view.ChatBalloon;
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
    @ViewId(R.id.ui_holder_view_vote_option_extra)
    private LinearLayout extraView;
    @ViewId(R.id.ui_holder_view_vote_option_chart_color)
    private View chartColor;
    @ViewId(R.id.ui_holder_view_vote_option_chart_blank)
    private View chartBlank;
    @ViewId(R.id.ui_holder_view_vote_option_chart_count)
    private TextView chartCount;
    @ViewId(R.id.ui_holder_view_vote_option_chart_to_details)
    private TextView chartToDetails;
    @ViewId(R.id.ui_holder_view_vote_option_users_view)
    private ChatBalloon headersView;
    @ViewId(R.id.ui_holder_view_vote_option_users)
    private FlexboxLayout headersLayout;

    private int imageSize, marginEnd, totalVote = 0;

    public VoteOptionViewHolder(View itemView, BaseFragment fragment) {
        super(itemView, fragment);
        ViewUtility.bind(this, itemView);
        imageSize = getDimension(R.dimen.ui_base_user_header_image_size_small);
        marginEnd = getDimension(R.dimen.ui_static_dp_5);
    }

    public void showContent(AppVoteItem item, AppVote vote) {
        boolean multi = vote.getMaxSelectable() > 1;
        if (multi) {
            showMultiChooseIcon(item);
        } else {
            showSingleChooseIcon(item);
        }
        textView.setText(item.getContent());
        showVoteCount(item, vote);
        showVotedHeaders(item.getId(), vote);
        //showEnded(vote.isEnded());
    }

    private void resetTotalVoteCount(AppVote vote) {
        totalVote = 0;
        if (null != vote.getActVoteList()) {
            for (AppVoteRecord record : vote.getActVoteList()) {
                if (record.getStatus() == AppVote.Status.REFUSED) {
                    totalVote += 1;
                } else {
                    totalVote += null == record.getItemIdList() ? 0 : record.getItemIdList().size();
                }
            }
        }
    }

    private void showVoteCount(AppVoteItem item, AppVote vote) {
        resetTotalVoteCount(vote);
        numberView.setText(fragment().getString(R.string.ui_activity_vote_details_count, item.getNum()));
        if (null != vote.getActVoteList()) {
            float percentage = (float) ((item.getNum() * 1.0) / totalVote);
            chartCount.setText(StringHelper.getString(R.string.ui_activity_vote_details_number, item.getNum(), format("%d", (int) (percentage * 100)) + "%"));
            chartCount.setTag(R.id.hlklib_ids_custom_view_click_tag, item.getNum());
            chartToDetails.setVisibility(item.getNum() > 0 ? View.VISIBLE : View.INVISIBLE);
            showWeight(chartColor, percentage);
            showWeight(chartBlank, 1.0F - percentage);
        }
    }

    private void showWeight(View view, float weight) {
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) view.getLayoutParams();
        params.weight = weight;
        view.setLayoutParams(params);
    }

    private void showVotedHeaders(String voteItemId, AppVote vote) {
        headersLayout.removeAllViews();
        if (null != vote.getActVoteList()) {
            for (AppVoteRecord record : vote.getActVoteList()) {
                if (voteItemId.equals(AppVoteItem.REFUSED_ID)) {
                    if (record.getStatus() == AppVote.Status.REFUSED) {
                        addHeader(record.getHeadPhoto());
                    }
                } else {
                    if (record.getStatus() == AppVote.Status.HAS_VOTED) {
                        if (null != record.getItemIdList() && record.getItemIdList().contains(voteItemId)) {
                            addHeader(record.getHeadPhoto());
                        }
                    }
                }
            }
        }
    }

    private void addHeader(String url) {
        ImageDisplayer displayer = (ImageDisplayer) View.inflate(extraView.getContext(), R.layout.tool_view_user_header, null);
        displayer.displayImage(url, imageSize, false, false);
        headersLayout.addView(displayer);
        FlexboxLayout.LayoutParams params = (FlexboxLayout.LayoutParams) displayer.getLayoutParams();
        params.width = imageSize;
        params.height = imageSize;
        params.rightMargin = marginEnd;
        displayer.setLayoutParams(params);
        displayer.addOnImageClickListener(onImageClickListener);
    }

    public void showVoted(boolean voted) {
        showEnded(voted);
        // 已投过票时，显示投票结果，否则不显示
        numberView.setVisibility(voted ? View.VISIBLE : View.GONE);
    }

    private void showEnded(boolean ended) {
        iconView.setVisibility(ended ? View.GONE : View.VISIBLE);
        extraView.setVisibility(ended ? View.VISIBLE : View.GONE);
    }

    private void showMultiChooseIcon(AppVoteItem item) {
        iconView.setText(item.isSelected() ? R.string.ui_icon_select_solid : R.string.ui_icon_radio_unselected);
        iconView.setTextColor(getColor(!item.isSelected() ? R.color.textColorHint : R.color.colorPrimary));
    }

    private void showSingleChooseIcon(AppVoteItem item) {
        iconView.setText(item.isSelected() ? R.string.ui_icon_radio_selected : R.string.ui_icon_radio_unselected);
        iconView.setTextColor(getColor(!item.isSelected() ? R.color.textColorHint : R.color.colorPrimary));
    }

    @Click({R.id.ui_holder_view_vote_option,
            R.id.ui_holder_view_vote_option_users,
            R.id.ui_holder_view_vote_option_chart_to_details})
    private void click(View view) {
        switch (view.getId()) {
            case R.id.ui_holder_view_vote_option:
                if (null != mOnViewHolderClickListener) {
                    mOnViewHolderClickListener.onClick(getAdapterPosition());
                }
                break;
            case R.id.ui_holder_view_vote_option_users:
                // 打开当前选项的投票详情
                handleToVoteItemDetails();
                break;
            case R.id.ui_holder_view_vote_option_chart_to_details:
                int num = (int) chartCount.getTag(R.id.hlklib_ids_custom_view_click_tag);
                if (num > 0) {
                    headersView.setVisibility(headersView.getVisibility() == View.VISIBLE ? View.GONE : View.VISIBLE);
                }
                break;
        }
    }

    private ImageDisplayer.OnImageClickListener onImageClickListener = new ImageDisplayer.OnImageClickListener() {
        @Override
        public void onImageClick(String url) {
            handleToVoteItemDetails();
        }
    };

    private void handleToVoteItemDetails() {
        if (null != mOnHandlerBoundDataListener) {
            mOnHandlerBoundDataListener.onHandlerBoundData(this);
        }
    }
}
