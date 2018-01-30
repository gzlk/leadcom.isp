package com.leadcom.android.isp.nim.viewholder;

import android.widget.TextView;

import com.leadcom.android.isp.R;
import com.leadcom.android.isp.api.activity.AppTopicMemberRequest;
import com.leadcom.android.isp.api.activity.AppTopicRequest;
import com.leadcom.android.isp.api.listener.OnMultipleRequestListener;
import com.leadcom.android.isp.helper.StringHelper;
import com.leadcom.android.isp.helper.ToastHelper;
import com.leadcom.android.isp.model.activity.topic.AppTopic;
import com.leadcom.android.isp.model.organization.Member;
import com.leadcom.android.isp.nim.model.extension.TopicAttachment;
import com.leadcom.android.isp.nim.session.NimSessionHelper;
import com.netease.nim.uikit.business.session.viewholder.MsgViewHolderBase;
import com.netease.nim.uikit.common.ui.recyclerview.adapter.BaseMultiItemFetchLoadAdapter;

import java.util.List;

/**
 * <b>功能描述：</b>网易云信对话列表里显示新增加的议题<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/08/30 10:33 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/08/30 10:33 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class MsgViewHolderTopic extends MsgViewHolderBase {

    private TextView titleView;
    private TextView contentTextView;
    private TopicAttachment topic;

    public MsgViewHolderTopic(BaseMultiItemFetchLoadAdapter adapter) {
        super(adapter);
    }

    @Override
    protected int getContentResId() {
        return R.layout.nim_msg_view_holder_topic;
    }

    @Override
    protected void inflateContentView() {
        titleView = (TextView) view.findViewById(R.id.message_item_topic_title_label);
        contentTextView = (TextView) view.findViewById(R.id.message_item_topic_content_label);
    }

    @Override
    protected void bindContentView() {
        topic = (TopicAttachment) message.getAttachment();
        titleView.setText(StringHelper.getString(R.string.ui_activity_topic_nim_view_holder_title, topic.getTitle()));
        contentTextView.setText(R.string.ui_activity_topic_nim_view_holder_content);
    }

    @Override
    protected void onItemClick() {
        // 如果我已经是议题里的成员则直接打开议题
        if (Member.isMeMemberOfTopic(topic.getCustomId())) {
            NimSessionHelper.startTeamSession(context, topic.getCustomId());
        } else {
            if (StringHelper.isEmpty(topic.getTopicId())) {
                // 兼容之前的版本，先查找议题列表，然后在查找议题的成员
                fetchingTopics();
            } else {
                // 拉取远程议题的成员列表并再次判断是否是议题成员
                fetchingTopicMembers(topic.getTopicId());
            }
        }
    }

    private void fetchingTopics() {
        AppTopicRequest.request().setOnMultipleRequestListener(new OnMultipleRequestListener<AppTopic>() {
            @Override
            public void onResponse(List<AppTopic> list, boolean success, int totalPages, int pageSize, int total, int pageNumber) {
                super.onResponse(list, success, totalPages, pageSize, total, pageNumber);
                if (success) {
                    AppTopic tp = AppTopic.queryByTid(topic.getCustomId());
                    if (null != tp) {
                        topic.setTopicId(tp.getId());
                        fetchingTopicMembers(tp.getId());
                    } else {
                        ToastHelper.make().showMsg(R.string.ui_activity_topic_property_invalid);
                    }
                }
            }
        }).list(topic.getActId(), 1);
    }

    private void fetchingTopicMembers(String topicId) {
        AppTopicMemberRequest.request().setOnMultipleRequestListener(new OnMultipleRequestListener<Member>() {
            @Override
            public void onResponse(List<Member> list, boolean success, int totalPages, int pageSize, int total, int pageNumber) {
                super.onResponse(list, success, totalPages, pageSize, total, pageNumber);
                if (success) {
                    if (Member.isMeMemberOfTopic(topic.getCustomId())) {
                        NimSessionHelper.startTeamSession(context, topic.getCustomId());
                    } else {
                        ToastHelper.make().showMsg(R.string.ui_activity_topic_not_member_of);
                    }
                }
            }
        }).list(topicId, 1);
    }
}
