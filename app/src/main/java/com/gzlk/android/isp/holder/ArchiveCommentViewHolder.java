package com.gzlk.android.isp.holder;

import android.graphics.Color;
import android.view.View;

import com.gzlk.android.isp.R;
import com.gzlk.android.isp.fragment.base.BaseFragment;
import com.gzlk.android.isp.fragment.individual.ArchiveDetailsFragment;
import com.gzlk.android.isp.helper.ClipboardHelper;
import com.gzlk.android.isp.helper.StringHelper;
import com.gzlk.android.isp.helper.TooltipHelper;
import com.gzlk.android.isp.lib.view.ExpandableTextView;
import com.gzlk.android.isp.model.archive.Comment;
import com.hlk.hlklib.lib.emoji.EmojiUtility;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.inject.ViewUtility;
import com.hlk.hlklib.lib.view.CorneredView;

/**
 * <b>功能描述：</b>评论ViewHolder<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/05/01 22:22 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/05/01 22:22 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class ArchiveCommentViewHolder extends BaseViewHolder {

    @ViewId(R.id.ui_holder_view_document_comment_container)
    private CorneredView container;
    @ViewId(R.id.ui_holder_view_document_comment_content)
    private ExpandableTextView contentTextView;

    public ArchiveCommentViewHolder(final View itemView, final BaseFragment fragment) {
        super(itemView, fragment);
        ViewUtility.bind(this, itemView);
        container.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                container.setNormalColor(getColor(R.color.textColorHintLightLight));
                fragment.showTooltip(v, R.id.ui_tool_view_tooltip_menu_edit, false, TooltipHelper.TYPE_CENTER, onClickListener);
                return true;
            }
        });
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            container.setNormalColor(Color.WHITE);
            log("clicked: " + v);
            switch (v.getId()) {
                case R.id.ui_tool_popup_menu_document_comment_copy:
                    Comment comment = ((ArchiveDetailsFragment) fragment()).getFromPosition(getAdapterPosition());
                    ClipboardHelper.copyToClipboard(v.getContext(), comment.getContent());
                    break;
                case R.id.ui_tool_popup_menu_document_comment_delete:
                    // 删除本条评论
                    ((ArchiveDetailsFragment) fragment()).deleteComment(getAdapterPosition());
                    break;
            }
        }
    };

    public void showContent(Comment comment) {
        String content = StringHelper.getString(R.string.ui_text_document_comment_content, comment.getUserName(), comment.getContent());
        contentTextView.setText(EmojiUtility.getEmojiString(itemView.getContext(), content, true));
    }
}
