package com.gzlk.android.isp.holder.individual;

import android.view.View;
import android.widget.TextView;

import com.gzlk.android.isp.R;
import com.gzlk.android.isp.fragment.base.BaseFragment;
import com.gzlk.android.isp.holder.BaseViewHolder;
import com.gzlk.android.isp.lib.view.ExpandableTextView;
import com.gzlk.android.isp.lib.view.ImageDisplayer;
import com.gzlk.android.isp.model.archive.Comment;
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

public class MomentCommentViewHolder extends BaseViewHolder {

    @ViewId(R.id.ui_holder_view_moment_comment_icon)
    private CustomTextView icon;
    @ViewId(R.id.ui_holder_view_moment_comment_header)
    private ImageDisplayer header;
    @ViewId(R.id.ui_holder_view_moment_comment_name)
    private TextView nameView;
    @ViewId(R.id.ui_holder_view_moment_comment_time)
    private TextView timeView;
    @ViewId(R.id.ui_holder_view_moment_comment_content)
    private ExpandableTextView contentView;

    private int imageSize;

    public MomentCommentViewHolder(View itemView, BaseFragment fragment) {
        super(itemView, fragment);
        ViewUtility.bind(this, itemView);
        imageSize = getDimension(R.dimen.ui_base_user_header_image_size_small);
    }

    public void showIcon(boolean shown) {
        icon.setVisibility(shown ? View.VISIBLE : View.INVISIBLE);
    }

    public void showContent(Comment comment) {
        header.displayImage("", imageSize, false, false);
        nameView.setText(comment.getUserName());
        timeView.setText(fragment().formatTimeAgo(comment.getCreateDate()));
        contentView.setText(EmojiUtility.getEmojiString(contentView.getContext(), comment.getContent(), true));
        contentView.makeExpandable();
    }

    @Click({R.id.ui_holder_view_moment_comment_container})
    private void elementClick(View view){

    }
}
