package com.gzlk.android.isp.nim.viewholder;

import android.widget.TextView;

import com.gzlk.android.isp.R;
import com.gzlk.android.isp.helper.StringHelper;
import com.gzlk.android.isp.nim.model.extension.TopicAttachment;
import com.gzlk.android.isp.nim.session.NimSessionHelper;
import com.netease.nim.uikit.common.ui.recyclerview.adapter.BaseMultiItemFetchLoadAdapter;
import com.netease.nim.uikit.session.viewholder.MsgViewHolderBase;

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
        NimSessionHelper.startTeamSession(context, topic.getCustomId());
    }
}
