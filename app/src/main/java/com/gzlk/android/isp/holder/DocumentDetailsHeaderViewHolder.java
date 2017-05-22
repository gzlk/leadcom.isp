package com.gzlk.android.isp.holder;

import android.view.View;

import com.gzlk.android.isp.R;
import com.gzlk.android.isp.etc.Utils;
import com.gzlk.android.isp.fragment.base.BaseFragment;
import com.gzlk.android.isp.helper.StringHelper;
import com.gzlk.android.isp.lib.view.ExpandableTextView;
import com.gzlk.android.isp.model.user.UserArchive;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.inject.ViewUtility;

/**
 * <b>功能描述：</b>文档详情头部文档基本信息<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/04/27 07:58 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/04/27 07:58 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class DocumentDetailsHeaderViewHolder extends BaseViewHolder {

    @ViewId(R.id.ui_tool_view_document_details_header_title)
    private View titleView;
    @ViewId(R.id.ui_tool_view_document_details_header_source)
    private View sourceView;
    @ViewId(R.id.ui_tool_view_document_details_header_time)
    private View timeView;
    @ViewId(R.id.ui_tool_view_document_details_header_privacy)
    private View privacyView;
    @ViewId(R.id.ui_holder_view_document_details_content)
    private ExpandableTextView contentView;

    // holder
    private SimpleClickableViewHolder titleHolder;
    private SimpleClickableViewHolder sourceHolder;
    private SimpleClickableViewHolder timeHolder;
    private SimpleClickableViewHolder privacyHolder;

    // items
    private String[] items;

    public DocumentDetailsHeaderViewHolder(View itemView, BaseFragment fragment) {
        super(itemView, fragment);
        ViewUtility.bind(this, itemView);
        initializeHolders();
    }

    private void initializeHolders() {
        if (null == items) {
            items = StringHelper.getStringArray(R.array.ui_document_details_item);
        }
        if (null == titleHolder) {
            titleHolder = new SimpleClickableViewHolder(titleView, fragment());
        }
        if (null == sourceHolder) {
            sourceHolder = new SimpleClickableViewHolder(sourceView, fragment());
        }
        if (null == timeHolder) {
            timeHolder = new SimpleClickableViewHolder(timeView, fragment());
        }
        if (null == privacyHolder) {
            privacyHolder = new SimpleClickableViewHolder(privacyView, fragment());
        }
    }

    public void showContent(UserArchive userArchive) {
        titleHolder.showContent(format(items[0], userArchive.getTitle()));
        sourceHolder.showContent(format(items[1], userArchive.getUserName()));
        timeHolder.showContent(format(items[2], Utils.format(userArchive.getCreateDate(), StringHelper.getString(R.string.ui_base_text_date_time_format), StringHelper.getString(R.string.ui_base_text_date_format_chs))));
        privacyHolder.showContent(format(items[3], ""));
        contentView.setText(StringHelper.escapeFromHtml(userArchive.getContent()));
        contentView.makeExpandable();
    }
}
