package com.gzlk.android.isp.helper.publishable;

import com.gzlk.android.isp.api.archive.CommentRequest;
import com.gzlk.android.isp.api.listener.OnMultipleRequestListener;
import com.gzlk.android.isp.api.listener.OnSingleRequestListener;
import com.gzlk.android.isp.helper.publishable.listener.OnCommentAddListener;
import com.gzlk.android.isp.helper.publishable.listener.OnCommentDeleteListener;
import com.gzlk.android.isp.helper.publishable.listener.OnCommentListListener;
import com.gzlk.android.isp.model.archive.Archive;
import com.gzlk.android.isp.model.archive.Comment;
import com.gzlk.android.isp.model.user.Moment;

import java.util.List;

/**
 * <b>功能描述：</b>评论发布helper<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/10/31 16:04 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/10/31 16:04 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class CommentHelper extends Publishable {

    public static CommentHelper helper() {
        return new CommentHelper();
    }

    @Override
    public CommentHelper setArchive(Archive archive) {
        mArchive = archive;
        return this;
    }

    @Override
    public CommentHelper setMoment(Moment moment) {
        mMoment = moment;
        return this;
    }

    private OnCommentAddListener addListener;

    public CommentHelper setCommentAddListener(OnCommentAddListener l) {
        addListener = l;
        return this;
    }

    private OnCommentListListener listListener;

    public CommentHelper setCommentListListener(OnCommentListListener l) {
        listListener = l;
        return this;
    }

    private OnCommentDeleteListener deleteListener;

    public CommentHelper setCommentDeleteListener(OnCommentDeleteListener l) {
        deleteListener = l;
        return this;
    }

    private String getHostId() {
        if (null != mArchive) {
            return mArchive.getId();
        }
        if (null != mMoment) {
            return mMoment.getId();
        }
        return null;
    }

    public void comment(int commentType, String content, String toUserId) {
        CommentRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<Comment>() {
            @Override
            public void onResponse(Comment comment, boolean success, String message) {
                super.onResponse(comment, success, message);
                if (null != addListener) {
                    if (null != mArchive) {
                        if (success) {
                            mArchive.setCmtNum(mArchive.getCmtNum() + 1);
                        }
                        addListener.onComplete(success, comment, mArchive);
                    } else if (null != mMoment) {
                        if (success) {
                            mMoment.getUserMmtCmtList().add(comment);
                            mMoment.setCmtNum(mMoment.getCmtNum() + 1);
                        }
                        addListener.onComplete(success, comment, mMoment);
                    }
                }
            }
        }).add(commentType, getHostId(), content, toUserId);
    }

    public void delete(int commentType, String archiveId, String commentId) {
        CommentRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<Comment>() {
            @Override
            public void onResponse(Comment comment, boolean success, String message) {
                super.onResponse(comment, success, message);
                if (null != deleteListener) {
                    if (null != mArchive) {
                        if (success) {
                            int cmt = mArchive.getCmtNum() - 1;
                            if (cmt <= 0) {
                                cmt = 0;
                            }
                            mArchive.setCmtNum(cmt);
                        }
                        deleteListener.onDeleted(success, mArchive);
                    } else if (null != mMoment) {
                    }
                }
            }
        }).delete(commentType, archiveId, commentId);
    }

    public void list(int commentType, String archiveId, int pageNumber) {
        CommentRequest.request().setOnMultipleRequestListener(new OnMultipleRequestListener<Comment>() {
            @Override
            public void onResponse(List<Comment> list, boolean success, int totalPages, int pageSize, int total, int pageNumber) {
                super.onResponse(list, success, totalPages, pageSize, total, pageNumber);
                if (null != listListener) {
                    listListener.onList(list, success, pageSize);
                }
            }
        }).list(commentType, archiveId, pageNumber);
    }
}
