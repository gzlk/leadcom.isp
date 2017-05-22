package com.gzlk.android.isp.holder;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gzlk.android.isp.R;
import com.gzlk.android.isp.etc.Utils;
import com.gzlk.android.isp.fragment.base.BaseFragment;
import com.gzlk.android.isp.fragment.individual.DocumentDetailsFragment;
import com.gzlk.android.isp.helper.StringHelper;
import com.gzlk.android.isp.lib.view.ExpandableTextView;
import com.gzlk.android.isp.lib.view.ImageDisplayer;
import com.gzlk.android.isp.model.organization.GroupArchive;
import com.gzlk.android.isp.model.user.UserArchive;
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

public class DocumentViewHolder extends BaseViewHolder {

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
    // additional
    @ViewId(R.id.ui_tool_view_document_additional_read)
    private TextView readNumber;
    @ViewId(R.id.ui_tool_view_document_additional_like)
    private TextView likeNumber;
    @ViewId(R.id.ui_tool_view_document_additional_comment)
    private TextView commentNumber;
    @ViewId(R.id.ui_tool_view_document_additional_favorite)
    private TextView favoriteNumber;

    public DocumentViewHolder(View itemView, BaseFragment fragment) {
        super(itemView, fragment);
        ViewUtility.bind(this, itemView);
    }

    public void showContent(UserArchive userArchive) {
        userName.setText(userArchive.getUserName());
        createTime.setText(Utils.format(userArchive.getCreateDate(), StringHelper.getString(R.string.ui_base_text_date_time_format), StringHelper.getString(R.string.ui_base_text_date_format)));
        documentTitle.setText(userArchive.getTitle());
        documentContentText.setText(StringHelper.escapeFromHtml(userArchive.getContent()));
        documentContentText.makeExpandable();
    }

    public void showContent(GroupArchive groupArchive) {
        userName.setText(groupArchive.getUserName());
        createTime.setText(Utils.format(groupArchive.getCreateDate(), StringHelper.getString(R.string.ui_base_text_date_time_format), StringHelper.getString(R.string.ui_base_text_date_format)));
        documentTitle.setText(groupArchive.getTitle());
        documentContentText.setText(StringHelper.escapeFromHtml(groupArchive.getContent()));
        documentContentText.makeExpandable();
    }

    @Click({R.id.ui_holder_view_document_content_container})
    private void elementClick(View view) {
        if (null != dataHandlerBoundDataListener) {
            Object object = dataHandlerBoundDataListener.onHandlerBoundData(this);
            if (null != object && object instanceof UserArchive) {
                openActivity(DocumentDetailsFragment.class.getName(), ((UserArchive) object).getId(), BaseFragment.REQUEST_CHANGE, true, false);
            }
        } else if (null != mOnViewHolderClickListener) {
            mOnViewHolderClickListener.onClick(getAdapterPosition());
        }
    }
}
