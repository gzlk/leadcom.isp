package com.leadcom.android.isp.nim.viewholder;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.leadcom.android.isp.R;
import com.leadcom.android.isp.activity.BaseActivity;
import com.leadcom.android.isp.fragment.activity.vote.VoteDetailsFragment;
import com.leadcom.android.isp.helper.StringHelper;
import com.leadcom.android.isp.nim.model.extension.VoteAttachment;
import com.hlk.hlklib.lib.view.CustomTextView;
import com.netease.nim.uikit.common.ui.recyclerview.adapter.BaseMultiItemFetchLoadAdapter;
import com.netease.nim.uikit.session.viewholder.MsgViewHolderBase;

/**
 * <b>功能描述：</b>网易云信对话列表里显示投票<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/06/30 23:45 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/06/30 23:45 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class MsgViewHolderVote extends MsgViewHolderBase {

    private TextView titleView;
    private LinearLayout item1;
    private CustomTextView item1Icon;
    private TextView item1Text;
    private LinearLayout item2;
    private CustomTextView item2Icon;
    private TextView item2Text;
    private LinearLayout item3;
    private CustomTextView item3Icon;
    private TextView item3Text;

    private VoteAttachment vote;

    public MsgViewHolderVote(BaseMultiItemFetchLoadAdapter adapter) {
        super(adapter);
    }

    @Override
    protected int getContentResId() {
        return R.layout.nim_msg_view_holder_vote;
    }

    @Override
    protected void inflateContentView() {
        titleView = (TextView) view.findViewById(R.id.message_item_vote_title_label);
        item1 = (LinearLayout) view.findViewById(R.id.message_item_vote_item_1);
        item1Icon = (CustomTextView) view.findViewById(R.id.message_item_vote_item_1_icon);
        item1Text = (TextView) view.findViewById(R.id.message_item_vote_item_1_text);

        item2 = (LinearLayout) view.findViewById(R.id.message_item_vote_item_2);
        item2Icon = (CustomTextView) view.findViewById(R.id.message_item_vote_item_2_icon);
        item2Text = (TextView) view.findViewById(R.id.message_item_vote_item_2_text);

        item3 = (LinearLayout) view.findViewById(R.id.message_item_vote_item_3);
        item3Icon = (CustomTextView) view.findViewById(R.id.message_item_vote_item_3_icon);
        item3Text = (TextView) view.findViewById(R.id.message_item_vote_item_3_text);
    }

    @Override
    protected void bindContentView() {
        vote = (VoteAttachment) message.getAttachment();
        titleView.setText(vote.getTitle());
        boolean multi = vote.getMaxVote() > 1;
        // 单选或多选
        item1Icon.setText(multi ? R.string.ui_icon_checkbox_checked : R.string.ui_icon_radio_unselected);
        item2Icon.setText(multi ? R.string.ui_icon_checkbox_checked : R.string.ui_icon_radio_unselected);
        item3Icon.setText(multi ? R.string.ui_icon_checkbox_checked : R.string.ui_icon_radio_unselected);
        int size = vote.getVoteItems().size();

        item1.setVisibility(size >= 1 ? View.VISIBLE : View.GONE);
        item1Text.setText(size >= 1 ? vote.getVoteItems().get(0) : "");

        item2.setVisibility(size >= 2 ? View.VISIBLE : View.GONE);
        item2Text.setText(size >= 2 ? vote.getVoteItems().get(1) : "");

        item3.setVisibility(size >= 3 ? View.VISIBLE : View.GONE);
        item3Text.setText(size >= 3 ? vote.getVoteItems().get(2) : "");
    }

    @Override
    protected void onItemClick() {
        String params = StringHelper.format("%s,%s", vote.getVoteId(), message.getSessionId());
        BaseActivity.openActivity(context, VoteDetailsFragment.class.getName(), params, true, false);
    }
}
