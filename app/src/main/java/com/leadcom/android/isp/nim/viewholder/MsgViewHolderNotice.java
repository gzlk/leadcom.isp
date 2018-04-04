package com.leadcom.android.isp.nim.viewholder;

import android.widget.TextView;

import com.leadcom.android.isp.R;
import com.leadcom.android.isp.activity.BaseActivity;
import com.leadcom.android.isp.fragment.activity.notice.NoticeDetailsFragment;
import com.leadcom.android.isp.helper.StringHelper;
import com.leadcom.android.isp.nim.model.extension.NoticeAttachment;
import com.netease.nim.uikit.business.session.viewholder.MsgViewHolderBase;
import com.netease.nim.uikit.common.ui.recyclerview.adapter.BaseMultiItemFetchLoadAdapter;

/**
 * <b>功能描述：</b>网易云信对话列表里显示通知<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/06/16 07:50 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/06/16 07:50 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class MsgViewHolderNotice extends MsgViewHolderBase {

    private TextView titleTextView;
    private TextView contentTextView;

    private NoticeAttachment notice;

    public MsgViewHolderNotice(BaseMultiItemFetchLoadAdapter adapter) {
        super(adapter);
    }

    @Override
    protected int getContentResId() {
        return R.layout.nim_msg_view_holder_notice;
    }

    @Override
    protected void inflateContentView() {
        titleTextView = view.findViewById(R.id.message_item_notice_title_label);
        contentTextView = view.findViewById(R.id.message_item_notice_content_label);
    }

    @Override
    protected void bindContentView() {
        notice = (NoticeAttachment) message.getAttachment();
        titleTextView.setText(StringHelper.getString(R.string.ui_activity_notice_nim_view_holder_title, notice.getTitle()));
        contentTextView.setText(notice.getContent());
    }

    @Override
    protected void onItemClick() {
        // 打开通知详情页
        NoticeDetailsFragment.open(context, 0, notice.getId());
    }
}
