package com.gzlk.android.isp.holder.archive;

import android.text.Html;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gzlk.android.isp.R;
import com.gzlk.android.isp.etc.Utils;
import com.gzlk.android.isp.fragment.archive.ArchiveDetailsFragment;
import com.gzlk.android.isp.fragment.base.BaseFragment;
import com.gzlk.android.isp.helper.StringHelper;
import com.gzlk.android.isp.holder.BaseViewHolder;
import com.gzlk.android.isp.lib.view.ExpandableTextView;
import com.gzlk.android.isp.lib.view.ImageDisplayer;
import com.gzlk.android.isp.model.archive.Archive;
import com.hlk.hlklib.lib.inject.Click;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.inject.ViewUtility;

/**
 * <b>功能描述：</b>个人档案<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/04/27 00:15 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/04/27 00:15 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class ArchiveViewHolder extends BaseViewHolder {

    // header
    @ViewId(R.id.ui_tool_view_document_user_header_image)
    private ImageDisplayer userHead;
    @ViewId(R.id.ui_tool_view_document_user_header_name)
    private TextView userName;
    @ViewId(R.id.ui_tool_view_document_user_header_time)
    private TextView createTime;
    // content
    @ViewId(R.id.ui_holder_view_document_title)
    private TextView documentTitle;
    @ViewId(R.id.ui_holder_view_document_content_layout)
    private LinearLayout documentContentLayout;
    @ViewId(R.id.ui_holder_view_document_content_text)
    private ExpandableTextView documentContentText;

    private ArchiveAdditionalViewHolder additionalViewHolder;

    public ArchiveViewHolder(View itemView, BaseFragment fragment) {
        super(itemView, fragment);
        ViewUtility.bind(this, itemView);
        userHead.setImageScaleType(ImageView.ScaleType.CENTER_CROP);
        additionalViewHolder = new ArchiveAdditionalViewHolder(itemView, fragment);
    }

    public void showContent(Archive archive) {
        userHead.displayImage(archive.getHeadPhoto(), getDimension(R.dimen.ui_static_dp_35), false, false);
        userName.setText(archive.getUserName());
        createTime.setText(Utils.format(archive.getCreateDate(), StringHelper.getString(R.string.ui_base_text_date_time_format), StringHelper.getString(R.string.ui_base_text_date_format)));
        documentTitle.setText(Html.fromHtml(archive.getTitle()));
        documentContentText.setText(StringHelper.escapeFromHtml(archive.getContent()));
        documentContentText.makeExpandable();
        additionalViewHolder.showContent(archive);
    }

    @Click({R.id.ui_holder_view_document_content_container})
    private void elementClick(View view) {
        if (null != dataHandlerBoundDataListener) {
            Object object = dataHandlerBoundDataListener.onHandlerBoundData(this);
            if (null != object && object instanceof Archive) {
                Archive archive = (Archive) object;
                int type = isEmpty(archive.getGroupId()) ? Archive.Type.USER : Archive.Type.GROUP;
                openActivity(ArchiveDetailsFragment.class.getName(), format("%d,%s", type, archive.getId()), BaseFragment.REQUEST_CHANGE, true, false);
            }
        } else if (null != mOnViewHolderClickListener) {
            mOnViewHolderClickListener.onClick(getAdapterPosition());
        }
    }
}
