package com.leadcom.android.isp.nim.viewholder;

import android.text.Html;
import android.view.View;
import android.widget.TextView;

import com.leadcom.android.isp.R;
import com.leadcom.android.isp.activity.BaseActivity;
import com.leadcom.android.isp.fragment.archive.ArchiveDetailsWebViewFragment;
import com.leadcom.android.isp.helper.StringHelper;
import com.leadcom.android.isp.lib.view.ImageDisplayer;
import com.leadcom.android.isp.nim.model.extension.ArchiveAttachment;
import com.netease.nim.uikit.common.ui.recyclerview.adapter.BaseMultiItemFetchLoadAdapter;
import com.netease.nim.uikit.session.viewholder.MsgViewHolderBase;

/**
 * <b>功能描述：</b>群聊里档案分享的显示Holder<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/12/22 09:27 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>version: 1.0.0 <br />
 * <b>修改时间：</b>2017/10/04 18:50 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */
public class MsgViewHolderArchive extends MsgViewHolderBase {

    public MsgViewHolderArchive(BaseMultiItemFetchLoadAdapter adapter) {
        super(adapter);
    }

    private TextView titleView, summaryView;
    private ImageDisplayer imageView;
    private ArchiveAttachment archive;

    @Override
    protected int getContentResId() {
        return R.layout.nim_msg_view_holder_archive;
    }

    @Override
    protected void inflateContentView() {
        titleView = findViewById(R.id.message_item_archive_title_label);
        summaryView = findViewById(R.id.message_item_archive_summary_label);
        imageView = findViewById(R.id.message_item_archive_image_control);
    }

    @Override
    protected void bindContentView() {
        archive = (ArchiveAttachment) message.getAttachment();
        titleView.setText(archive.getTitle());
        summaryView.setText(Html.fromHtml(archive.getSummary()));
        imageView.setVisibility(StringHelper.isEmpty(archive.getImage(), true) ? View.GONE : View.VISIBLE);
        imageView.displayImage(archive.getImage(), context.getResources().getDimensionPixelOffset(R.dimen.ui_static_dp_50), false, false);
    }

    @Override
    protected void onItemClick() {
        String params = StringHelper.format("%s,%d", archive.getCustomId(), archive.getArchiveType() - 1);
        BaseActivity.openActivity(context, ArchiveDetailsWebViewFragment.class.getName(), params, true, false);
    }
}
