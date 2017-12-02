package com.leadcom.android.isp.holder.individual;

import android.text.Html;
import android.view.View;
import android.widget.TextView;

import com.leadcom.android.isp.R;
import com.leadcom.android.isp.fragment.base.BaseFragment;
import com.leadcom.android.isp.helper.StringHelper;
import com.leadcom.android.isp.holder.BaseViewHolder;
import com.leadcom.android.isp.lib.view.ImageDisplayer;
import com.leadcom.android.isp.model.archive.Comment;
import com.hlk.hlklib.lib.emoji.EmojiUtility;
import com.hlk.hlklib.lib.inject.Click;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.inject.ViewUtility;
import com.hlk.hlklib.lib.view.CustomTextView;

/**
 * <b>功能描述：</b>说说中的评论<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/07/25 10:14 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/07/25 10:14 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class MomentCommentHeaderViewHolder extends BaseViewHolder {

    @ViewId(R.id.ui_holder_view_moment_comment_icon)
    private CustomTextView icon;
    @ViewId(R.id.ui_holder_view_moment_comment_header)
    private ImageDisplayer header;
    @ViewId(R.id.ui_holder_view_moment_comment_name)
    private TextView nameView;
    @ViewId(R.id.ui_holder_view_moment_comment_time)
    private TextView timeView;
    @ViewId(R.id.ui_holder_view_moment_comment_delete)
    private CustomTextView deleteView;
    @ViewId(R.id.ui_holder_view_moment_comment_content)
    private TextView contentView;

    private int imageSize;
    private boolean showDelete;

    public MomentCommentHeaderViewHolder(View itemView, BaseFragment fragment) {
        super(itemView, fragment);
        ViewUtility.bind(this, itemView);
        imageSize = getDimension(R.dimen.ui_base_user_header_image_size_small);
        header.addOnImageClickListener(new ImageDisplayer.OnImageClickListener() {
            @Override
            public void onImageClick(ImageDisplayer displayer, String url) {
                if (null != mOnViewHolderClickListener) {
                    mOnViewHolderClickListener.onClick(getAdapterPosition());
                }
            }
        });
    }

    public void setDeletable(boolean deletable) {
        showDelete = deletable;
    }

    public void showIcon(boolean shown) {
        icon.setVisibility(shown ? View.VISIBLE : View.INVISIBLE);
    }

    public void showContent(Comment comment) {
        header.displayImage(comment.getHeadPhoto(), imageSize, false, false);
        String text;
        if (!isEmpty(comment.getToUserId())) {
            text = StringHelper.getString(R.string.ui_individual_moment_comment_content_header_name_to, comment.getUserName(), comment.getToUserName());
        } else {
            text = StringHelper.getString(R.string.ui_individual_moment_comment_content_header_name, comment.getUserName());
        }
        nameView.setText(Html.fromHtml(text));
        timeView.setText(fragment().formatTimeAgo(comment.getCreateDate()));
        contentView.setText(EmojiUtility.getEmojiString(contentView.getContext(), comment.getContent(), true));
        // 我发布的评论可以删除
        deleteView.setVisibility(showDelete ? View.VISIBLE : (comment.isMine() ? View.VISIBLE : View.GONE));
    }

    @Click({R.id.ui_holder_view_moment_comment_container, R.id.ui_holder_view_moment_comment_delete})
    private void elementClick(View view) {
        if (null != mOnViewHolderElementClickListener) {
            mOnViewHolderElementClickListener.onClick(view, getAdapterPosition());
        }
    }
}
