package com.gzlk.android.isp.holder.archive;

import android.text.Html;
import android.view.View;
import android.widget.TextView;

import com.gzlk.android.isp.R;
import com.gzlk.android.isp.cache.Cache;
import com.gzlk.android.isp.fragment.base.BaseFragment;
import com.gzlk.android.isp.fragment.organization.StructureFragment;
import com.gzlk.android.isp.helper.StringHelper;
import com.gzlk.android.isp.holder.BaseViewHolder;
import com.gzlk.android.isp.lib.view.ImageDisplayer;
import com.gzlk.android.isp.model.archive.Comment;
import com.hlk.hlklib.lib.inject.Click;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.inject.ViewUtility;
import com.hlk.hlklib.lib.view.CustomTextView;

/**
 * <b>功能描述：</b><br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/10/27 17:07 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/10/27 17:07 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class ArchiveDetailsCommentViewHolder extends BaseViewHolder {

    @ViewId(R.id.ui_holder_view_archive_details_comment_header)
    private ImageDisplayer headerView;
    @ViewId(R.id.ui_holder_view_archive_details_comment_name)
    private TextView nameView;
    @ViewId(R.id.ui_holder_view_archive_details_comment_time)
    private TextView timeView;
    @ViewId(R.id.ui_holder_view_archive_details_comment_delete)
    private CustomTextView deleteView;
    @ViewId(R.id.ui_holder_view_archive_details_comment_content)
    private TextView contentView;

    private int imageSize;
    private boolean deletable = false;

    public ArchiveDetailsCommentViewHolder(View itemView, BaseFragment fragment) {
        super(itemView, fragment);
        ViewUtility.bind(this, itemView);
        imageSize = getDimension(R.dimen.ui_base_user_header_image_size);
        headerView.addOnImageClickListener(new ImageDisplayer.OnImageClickListener() {
            @Override
            public void onImageClick(ImageDisplayer displayer, String url) {
                if (null != mOnViewHolderElementClickListener) {
                    mOnViewHolderElementClickListener.onClick(headerView, getAdapterPosition());
                }
            }
        });
    }

    public void setDeletable(boolean deletable) {
        this.deletable = deletable;
    }

    public void showContent(Comment comment) {
        headerView.displayImage(comment.getHeadPhoto(), imageSize, false, false);
        String text;
        if (!isEmpty(comment.getToUserId())) {
            text = StringHelper.getString(R.string.ui_individual_moment_comment_content_header_name_to, comment.getUserName(), comment.getToUserName());
        } else {
            text = StringHelper.getString(R.string.ui_individual_moment_comment_content_header_name, comment.getUserName());
        }
        nameView.setText(Html.fromHtml(text));
        timeView.setText(fragment().formatTimeAgo(comment.getCreateDate()));
        contentView.setText(comment.getContent());
        // 档案作者可以删除所有评论，评论作者可以删除自己的评论
        deleteView.setVisibility(deletable ? View.VISIBLE : (comment.getUserId().equals(Cache.cache().userId) ? View.VISIBLE : View.GONE));
    }

    @Click({R.id.ui_holder_view_archive_details_comment_layout, R.id.ui_holder_view_archive_details_comment_delete})
    private void elementClick(View view) {
        if (null != mOnViewHolderElementClickListener) {
            mOnViewHolderElementClickListener.onClick(view, getAdapterPosition());
        }
    }
}
