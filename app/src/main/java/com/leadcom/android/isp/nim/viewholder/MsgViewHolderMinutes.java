package com.leadcom.android.isp.nim.viewholder;

import android.widget.TextView;

import com.leadcom.android.isp.R;
import com.leadcom.android.isp.fragment.base.BaseFragment;
import com.leadcom.android.isp.fragment.common.OfficeOnlinePreviewFragment;
import com.leadcom.android.isp.helper.StringHelper;
import com.leadcom.android.isp.model.common.Attachment;
import com.leadcom.android.isp.nim.model.extension.MinutesAttachment;
import com.netease.nim.uikit.common.ui.recyclerview.adapter.BaseMultiItemFetchLoadAdapter;
import com.netease.nim.uikit.session.viewholder.MsgViewHolderBase;

/**
 * <b>功能描述：</b>网易云信对话列表里显示会议纪要<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/08/30 11:22 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/08/30 11:22 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class MsgViewHolderMinutes extends MsgViewHolderBase {

    private TextView titleTextView;
    private TextView contentTextView;

    private MinutesAttachment minutes;

    public MsgViewHolderMinutes(BaseMultiItemFetchLoadAdapter adapter) {
        super(adapter);
    }

    @Override
    protected int getContentResId() {
        return R.layout.nim_msg_view_holder_minutes;
    }

    @Override
    protected void inflateContentView() {
        titleTextView = (TextView) view.findViewById(R.id.message_item_minutes_title_label);
        contentTextView = (TextView) view.findViewById(R.id.message_item_minutes_content_label);
    }

    @Override
    protected void bindContentView() {
        minutes = (MinutesAttachment) message.getAttachment();
        titleTextView.setText(StringHelper.getString(R.string.ui_activity_minutes_nim_view_holder_title, minutes.getTitle()));
        contentTextView.setText(message.getPushContent());
    }

    @Override
    protected void onItemClick() {
        String url = minutes.getUrl();
        String title = StringHelper.getString(R.string.ui_nim_action_minutes);
        OfficeOnlinePreviewFragment.open(context, BaseFragment.ACTIVITY_BASE_REQUEST, url, title, Attachment.getExtension(url), false);
    }
}
