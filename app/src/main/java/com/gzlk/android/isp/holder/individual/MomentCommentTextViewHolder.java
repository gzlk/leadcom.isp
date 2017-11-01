package com.gzlk.android.isp.holder.individual;

import android.view.View;
import android.widget.TextView;

import com.gzlk.android.isp.R;
import com.gzlk.android.isp.fragment.base.BaseFragment;
import com.gzlk.android.isp.helper.StringHelper;
import com.gzlk.android.isp.holder.BaseViewHolder;
import com.gzlk.android.isp.model.archive.Comment;
import com.hlk.hlklib.lib.emoji.EmojiUtility;
import com.hlk.hlklib.lib.inject.Click;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.inject.ViewUtility;

/**
 * <b>功能描述：</b>朋友圈（首页 - 个人 - 动态）中说说的评论，只有名字没有头像<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/10/20 14:51 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/10/20 14:51 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class MomentCommentTextViewHolder extends BaseViewHolder {

    @ViewId(R.id.ui_holder_view_individual_moment_comment_name_comment)
    private TextView commentView;
    @ViewId(R.id.ui_holder_view_individual_moment_comment_name_last)
    private View lastPadding;

    public MomentCommentTextViewHolder(View itemView, BaseFragment fragment) {
        super(itemView, fragment);
        ViewUtility.bind(this, itemView);
    }

    public void showContent(Comment comment) {
        String text;
        if (!isEmpty(comment.getToUserId())) {
            text = StringHelper.getString(R.string.ui_individual_moment_comment_content_only_name_to, comment.getUserName(), comment.getToUserName(), comment.getContent());
        } else {
            text = StringHelper.getString(R.string.ui_individual_moment_comment_content_only_name, comment.getUserName(), comment.getContent());
        }
        commentView.setText(EmojiUtility.getEmojiString(commentView.getContext(), text, true));
        lastPadding.setVisibility(comment.isLast() ? View.VISIBLE : View.GONE);
    }

    @Click({R.id.ui_holder_view_individual_moment_comment_name_container})
    private void elementClick(View view) {
        if (null != mOnViewHolderElementClickListener) {
            mOnViewHolderElementClickListener.onClick(view, getAdapterPosition());
        }
    }
}
