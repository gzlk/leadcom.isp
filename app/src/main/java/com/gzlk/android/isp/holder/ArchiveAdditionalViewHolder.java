package com.gzlk.android.isp.holder;

import android.view.View;
import android.widget.TextView;

import com.gzlk.android.isp.R;
import com.gzlk.android.isp.fragment.base.BaseFragment;
import com.gzlk.android.isp.model.archive.Archive;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.inject.ViewUtility;

/**
 * <b>功能描述：</b>档案附加信息<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/05/23 01:00 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/05/23 01:00 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class ArchiveAdditionalViewHolder extends BaseViewHolder {

    @ViewId(R.id.ui_tool_view_document_additional_read)
    private TextView readNumber;
    @ViewId(R.id.ui_tool_view_document_additional_like)
    private TextView likeNumber;
    @ViewId(R.id.ui_tool_view_document_additional_comment)
    private TextView commentNumber;
    @ViewId(R.id.ui_tool_view_document_additional_favorite)
    private TextView favoriteNumber;

    public ArchiveAdditionalViewHolder(View itemView, BaseFragment fragment) {
        super(itemView, fragment);
        ViewUtility.bind(this, itemView);
    }

    public void showContent(Archive archive) {
        readNumber.setText(String.valueOf(archive.getReadNum()));
        likeNumber.setText(String.valueOf(archive.getLikeNum()));
        commentNumber.setText(String.valueOf(archive.getCmtNum()));
        favoriteNumber.setText(String.valueOf(archive.getColNum()));
    }
}
