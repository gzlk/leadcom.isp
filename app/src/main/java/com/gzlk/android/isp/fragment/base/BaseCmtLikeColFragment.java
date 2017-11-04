package com.gzlk.android.isp.fragment.base;

import android.view.View;

import com.gzlk.android.isp.R;
import com.gzlk.android.isp.helper.DialogHelper;
import com.gzlk.android.isp.helper.publishable.CollectHelper;
import com.gzlk.android.isp.helper.publishable.CommentHelper;
import com.gzlk.android.isp.helper.publishable.LikeHelper;
import com.gzlk.android.isp.helper.publishable.listener.OnCollectedListener;
import com.gzlk.android.isp.helper.publishable.listener.OnCommentAddListener;
import com.gzlk.android.isp.helper.publishable.listener.OnCommentDeleteListener;
import com.gzlk.android.isp.helper.publishable.listener.OnCommentListListener;
import com.gzlk.android.isp.helper.publishable.listener.OnLikeListListener;
import com.gzlk.android.isp.helper.publishable.listener.OnLikeListener;
import com.gzlk.android.isp.helper.publishable.listener.OnUncollectedListener;
import com.gzlk.android.isp.helper.publishable.listener.OnUnlikeListener;
import com.gzlk.android.isp.model.Model;
import com.gzlk.android.isp.model.archive.Archive;
import com.gzlk.android.isp.model.archive.ArchiveLike;
import com.gzlk.android.isp.model.archive.Comment;
import com.gzlk.android.isp.model.user.Collection;
import com.gzlk.android.isp.model.user.Moment;

import java.util.List;

/**
 * <b>功能描述：</b>提供评论、赞、收藏接口访问的fragment基类<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/11/03 21:38 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>version: 1.0.0 <br />
 * <b>修改时间：</b>2017/11/03 21:38 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */
public abstract class BaseCmtLikeColFragment extends BaseChatInputSupportFragment {

    // *****************************************************************************************点赞

    /**
     * 点赞
     */
    protected void like(Model model) {
        if (model instanceof Archive) {
            likeArchive((Archive) model);
        } else if (model instanceof Moment) {
            likeMoment((Moment) model);
        }
    }

    private void likeArchive(Archive archive) {
        if (archive.isLiked()) {
            unlike(archive);
        } else {
            liking(archive);
        }
    }

    private void likeMoment(Moment moment) {
        if (moment.isLiked()) {
            unlike(moment);
        } else {
            liking(moment);
        }
    }

    private void liking(Model model) {
        // 赞
        setLoadingText(R.string.ui_text_archive_details_liking);
        displayLoading(true);
        LikeHelper.helper().setModel(model).setLikeListener(new OnLikeListener() {
            @Override
            public void onLiked(boolean success, Model model) {
                displayLoading(false);
                onLikeComplete(success, model);
            }
        }).like();
    }

    private void unlike(Model model) {
        // 取消赞
        setLoadingText(R.string.ui_text_archive_details_unliking);
        displayLoading(true);
        LikeHelper.helper().setModel(model).setUnlikeListener(new OnUnlikeListener() {
            @Override
            public void onUnlike(boolean success, Model model) {
                displayLoading(false);
                onLikeComplete(success, model);
            }
        }).unlike();
    }

    protected abstract void onLikeComplete(boolean success, Model model);

    // ************************************************************************************拉取赞列表

    /**
     * 拉取赞列表
     */
    protected void loadingLike(Model model) {
        setLoadingText(R.string.ui_base_text_loading);
        displayLoading(true);
        LikeHelper.helper().setModel(model).setOnLikeListListener(new OnLikeListListener() {
            @Override
            public void onList(List<ArchiveLike> list, boolean success, int pageSize) {
                displayLoading(false);
                onLoadingLikeComplete(success, list);
            }
        }).list();
    }

    protected void onLoadingLikeComplete(boolean success, List<ArchiveLike> list) {
    }

    // *****************************************************************************************收藏

    /**
     * 收藏
     */
    protected void collect(Model model) {
        if (model instanceof Archive) {
            collectArchive((Archive) model);
        } else if (model instanceof Moment) {
            collectMoment((Moment) model);
        }
    }

    private void collectArchive(Archive archive) {
        if (archive.isCollected()) {
            unCollect(archive.getColId(), archive);
        } else {
            collecting(archive);
        }
    }

    private void collectMoment(Moment moment) {
        if (moment.isCollected()) {
            unCollect(moment.getColId(), moment);
        } else {
            collecting(moment);
        }
    }

    private void collecting(Model model) {
        setLoadingText(R.string.ui_text_archive_details_collecting);
        displayLoading(true);
        CollectHelper.helper().setModel(model).setCollectedListener(new OnCollectedListener() {
            @Override
            public void onCollected(boolean success, Model model) {
                displayLoading(false);
                onCollectComplete(success, model);
            }
        }).collect(Collection.get(model));
    }

    private void unCollect(String collectId, Model model) {
        // 取消收藏
        setLoadingText(R.string.ui_text_archive_details_uncollecting);
        displayLoading(true);
        CollectHelper.helper().setModel(model).setUncollectedListener(new OnUncollectedListener() {
            @Override
            public void onUncollected(boolean success, Model model) {
                displayLoading(false);
                onCollectComplete(success, model);
            }
        }).uncollect(collectId);
    }

    /**
     * 收藏或取消收藏完毕
     */
    protected abstract void onCollectComplete(boolean success, Model model);

    // **********************************************************************************拉取评论列表

    /**
     * 拉取评论列表
     */
    protected void loadingComments(Model model) {
        setLoadingText(R.string.ui_text_document_details_loading_comments);
        displayLoading(true);
        CommentHelper.helper().setModel(model).setCommentListListener(new OnCommentListListener() {
            @Override
            public void onList(List<Comment> list, boolean success, int pageSize) {
                displayLoading(false);
                int size = null == list ? 0 : list.size();
                isLoadingComplete(size < pageSize);
                onLoadingCommentComplete(success, list);
                if (success && null != list) {
                    // 下一页
                    remotePageNumber += size >= pageSize ? 1 : 0;
                }
                stopRefreshing();
            }
        }).list(remotePageNumber);
    }

    protected void onLoadingCommentComplete(boolean success, List<Comment> list) {
    }

    // **************************************************************************************发布评论

    /**
     * 发送评论
     */
    protected void comment(Model model, String content, String toUserId) {
        setLoadingText(R.string.ui_text_archive_details_comment_sending);
        displayLoading(true);
        CommentHelper.helper().setModel(model).setCommentAddListener(new OnCommentAddListener() {
            @Override
            public void onComplete(boolean success, Comment comment, Model model) {
                displayLoading(false);
                onCommentComplete(success, comment, model);
            }
        }).comment(content, toUserId);
    }

    /**
     * 评论发送成功
     */
    protected void onCommentComplete(boolean success, Comment comment, Model model) {
    }

    // **************************************************************************************删除评论

    private View commentDeleteDialog;

    protected void openCommentDeleteDialog() {
        DialogHelper.init(Activity()).addOnDialogInitializeListener(new DialogHelper.OnDialogInitializeListener() {
            @Override
            public View onInitializeView() {
                if (null == commentDeleteDialog) {
                    commentDeleteDialog = View.inflate(Activity(), R.layout.popup_dialog_comment_delete, null);
                }
                return commentDeleteDialog;
            }

            @Override
            public void onBindData(View dialogView, DialogHelper helper) {

            }
        }).addOnDialogCancelListener(new DialogHelper.OnDialogCancelListener() {
            @Override
            public void onCancel() {
                onCommentDeleteDialogCanceled();
            }
        }).addOnDialogConfirmListener(new DialogHelper.OnDialogConfirmListener() {
            @Override
            public boolean onConfirm() {
                onCommentDeleteDialogConfirmed();
                return true;
            }
        }).setAdjustScreenWidth(true).setPopupType(DialogHelper.SLID_IN_BOTTOM).show();
    }

    /**
     * 删除评论对话框取消
     */
    protected void onCommentDeleteDialogCanceled() {
    }

    /**
     * 删除评论对话框确认
     */
    protected void onCommentDeleteDialogConfirmed() {
    }

    /**
     * 删除评论
     */
    protected void deleteComment(Model model, String commentId) {
        setLoadingText(R.string.ui_text_archive_details_comment_deleting);
        displayLoading(true);
        CommentHelper.helper().setModel(model).setCommentDeleteListener(new OnCommentDeleteListener() {
            @Override
            public void onDeleted(boolean success, Model model) {
                displayLoading(false);
                onDeleteCommentComplete(success, model);
            }
        }).delete(commentId);
    }

    protected void onDeleteCommentComplete(boolean success, Model model) {
    }
}
