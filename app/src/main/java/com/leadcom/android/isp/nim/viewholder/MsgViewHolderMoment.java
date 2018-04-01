package com.leadcom.android.isp.nim.viewholder;

import android.text.Html;
import android.view.View;
import android.widget.TextView;

import com.leadcom.android.isp.R;
import com.leadcom.android.isp.activity.BaseActivity;
import com.leadcom.android.isp.fragment.individual.moment.MomentDetailsFragment;
import com.leadcom.android.isp.fragment.individual.moment.MomentImagesFragment;
import com.leadcom.android.isp.helper.StringHelper;
import com.leadcom.android.isp.lib.view.ImageDisplayer;
import com.leadcom.android.isp.nim.model.extension.MomentAttachment;
import com.netease.nim.uikit.business.session.viewholder.MsgViewHolderBase;
import com.netease.nim.uikit.common.ui.recyclerview.adapter.BaseMultiItemFetchLoadAdapter;


/**
 * <b>功能描述：</b>群聊里动态分享的显示Holder<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2018/04/01 09:31 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>version: 1.0.0 <br />
 * <b>修改时间：</b>2017/10/04 18:50 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */
public class MsgViewHolderMoment extends MsgViewHolderBase {

    public MsgViewHolderMoment(BaseMultiItemFetchLoadAdapter adapter) {
        super(adapter);
    }

    TextView titleView, summaryView;
    protected ImageDisplayer imageView;
    private MomentAttachment moment;

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
        moment = (MomentAttachment) message.getAttachment();
        titleView.setText(moment.getTitle());
        summaryView.setText(Html.fromHtml(moment.getSummary()));
        imageView.setVisibility(StringHelper.isEmpty(moment.getImage(), true) ? View.GONE : View.VISIBLE);
        imageView.displayImage(moment.getImage(), context.getResources().getDimensionPixelOffset(R.dimen.ui_static_dp_50), false, false);
    }

    @Override
    protected void onItemClick() {
        boolean hasImage = StringHelper.isEmpty(moment.getImage());
        String clazz = !hasImage ? MomentDetailsFragment.class.getName() : MomentImagesFragment.class.getName();
        String params = StringHelper.format((hasImage ? "%s,0" : "%s"), moment.getCustomId());
        BaseActivity.openActivity(context, clazz, params, true, false);
    }
}
