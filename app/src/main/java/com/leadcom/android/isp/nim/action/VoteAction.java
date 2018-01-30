package com.leadcom.android.isp.nim.action;

import android.app.Activity;
import android.content.Intent;

import com.leadcom.android.isp.R;
import com.leadcom.android.isp.fragment.activity.vote.VoteCreatorFragment;
import com.leadcom.android.isp.fragment.activity.vote.VoteListFragment;
import com.leadcom.android.isp.fragment.base.BaseFragment;
import com.leadcom.android.isp.model.activity.vote.AppVote;
import com.leadcom.android.isp.nim.constant.RequestCode;
import com.leadcom.android.isp.nim.model.extension.VoteAttachment;
import com.netease.nim.uikit.business.session.actions.BaseAction;
import com.netease.nimlib.sdk.msg.MessageBuilder;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;

/**
 * <b>功能描述：</b>网易云信投票Action<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/06/07 11:01 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/06/07 11:01 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class VoteAction extends BaseAction {

    /**
     * 投票
     */
    public VoteAction() {
        super(R.drawable.nim_action_vote, R.string.ui_nim_action_vote);
    }

    @Override
    public void onClick() {
        // 打开投票列表页面
        int requestCode = makeRequestCode(RequestCode.REQ_VOTE_LIST);
        VoteListFragment.open(getActivity(), requestCode, getAccount());
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case RequestCode.REQ_VOTE_LIST:
                    // 创建新的投票
                    int code = makeRequestCode(RequestCode.REQ_VOTE_NEW);
                    VoteCreatorFragment.open(getActivity(), code, getAccount());
                    break;
                case RequestCode.REQ_VOTE_NEW:
                    // 投票创建完毕，要转发到群里
                    String result = BaseFragment.getResultedData(data);
                    AppVote appVote = AppVote.fromJson(result);
                    VoteAttachment vote = new VoteAttachment();
                    assert appVote != null;
                    vote.setTitle(appVote.getTitle());
                    vote.setVoteId(appVote.getId());
                    vote.setCustomId(appVote.getId());
                    vote.setMaxVote(appVote.getMaxSelectable());
                    for (String item : appVote.getItemContentList()) {
                        vote.getVoteItems().add(item);
                    }

                    IMMessage message;
                    message = MessageBuilder.createCustomMessage(getAccount(), SessionTypeEnum.Team, vote.getTitle(), vote);
                    sendMessage(message);
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
