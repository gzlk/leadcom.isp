package com.leadcom.android.isp.nim.action;

import android.app.Activity;
import android.content.Intent;

import com.leadcom.android.isp.R;
import com.leadcom.android.isp.fragment.activity.topic.TopicCreatorFragment;
import com.leadcom.android.isp.fragment.activity.topic.TopicListFragment;
import com.leadcom.android.isp.fragment.base.BaseFragment;
import com.leadcom.android.isp.model.activity.topic.AppTopic;
import com.leadcom.android.isp.nim.constant.RequestCode;
import com.leadcom.android.isp.nim.model.extension.TopicAttachment;
import com.netease.nim.uikit.session.actions.BaseAction;
import com.netease.nimlib.sdk.msg.MessageBuilder;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;

/**
 * <b>功能描述：</b>网易云信议题Action<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/06/07 11:00 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/06/07 11:00 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class IssueAction extends BaseAction {

    /**
     * 议题
     */
    public IssueAction() {
        super(R.drawable.nim_action_issue, R.string.ui_nim_action_issue);
    }

    @Override
    public void onClick() {
        // 打开通知列表页面
        int requestCode = makeRequestCode(RequestCode.REQ_TOPIC_LIST);
        TopicListFragment.open(getActivity(), getAccount(), requestCode);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == Activity.RESULT_OK) {
            switch (requestCode) {
                case RequestCode.REQ_TOPIC_LIST:
                    // 到议题创建页面
                    TopicCreatorFragment.open(getActivity(), makeRequestCode(RequestCode.REQ_TOPIC_NEW), getAccount());
                    break;
                case RequestCode.REQ_TOPIC_NEW:
                    String json = BaseFragment.getResultedData(data);
                    AppTopic topic = AppTopic.fromJson(json);
                    TopicAttachment attachment = new TopicAttachment();
                    attachment.setCustomId(topic.getTid());
                    attachment.setActId(topic.getActId());
                    attachment.setTopicId(topic.getId());
                    attachment.setTitle(topic.getTitle());
                    IMMessage message = MessageBuilder.createCustomMessage(getAccount(), SessionTypeEnum.Team, topic.getTitle(), attachment);
                    sendMessage(message);
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
