package com.gzlk.android.isp.fragment.individual;

import com.gzlk.android.isp.api.archive.CommentRequest;
import com.gzlk.android.isp.api.archive.LikeRequest;
import com.gzlk.android.isp.api.listener.OnMultipleRequestListener;
import com.gzlk.android.isp.api.listener.OnSingleRequestListener;
import com.gzlk.android.isp.api.user.MomentRequest;
import com.gzlk.android.isp.fragment.base.BaseChatInputSupportFragment;
import com.gzlk.android.isp.model.archive.ArchiveLike;
import com.gzlk.android.isp.model.archive.Comment;
import com.gzlk.android.isp.model.user.Moment;

import java.util.List;

/**
 * <b>功能描述：</b>单个说说<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/07/26 10:35 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/07/26 10:35 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public abstract class BaseMomentFragment extends BaseChatInputSupportFragment {

    @Override
    protected void onSwipeRefreshing() {

    }

    @Override
    protected void onLoadingMore() {

    }

    @Override
    protected void onDelayRefreshComplete(@DelayType int type) {

    }

    @Override
    protected String getLocalPageTag() {
        return null;
    }

    @Override
    protected void destroyView() {

    }

    protected Moment mMoment;

    /**
     * 拉取远程服务器上的说说
     */
    protected void fetchingMoment() {
        MomentRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<Moment>() {
            @Override
            public void onResponse(Moment moment, boolean success, String message) {
                super.onResponse(moment, success, message);
                onFetchingMomentComplete(moment, success);
            }
        }).find(mQueryId);
    }

    /**
     * 拉取远程服务器上的说说完毕
     */
    protected void onFetchingMomentComplete(Moment moment, boolean success) {
    }

    protected void deleteMoment() {
        MomentRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<Moment>() {
            @Override
            public void onResponse(Moment moment, boolean success, String message) {
                super.onResponse(moment, success, message);
                onDeleteMomentComplete(moment, success, message);
            }
        }).delete(mQueryId);
    }

    protected void onDeleteMomentComplete(Moment moment, boolean success, String message) {
    }

    /**
     * 拉取评论列表
     */
    protected void fetchingComments() {
        CommentRequest.request().setOnMultipleRequestListener(new OnMultipleRequestListener<Comment>() {
            @Override
            public void onResponse(List<Comment> list, boolean success, int totalPages, int pageSize, int total, int pageNumber) {
                super.onResponse(list, success, totalPages, pageSize, total, pageNumber);
                onFetchingCommentsComplete(list, success, pageSize);
            }
        }).list(Comment.Type.MOMENT, mQueryId, remotePageNumber);
    }

    protected void onFetchingCommentsComplete(List<Comment> list, boolean success, int pageSize) {
    }

    /**
     * 拉取赞列表
     */
    protected void fetchingPraises() {
        LikeRequest.request().setOnMultipleRequestListener(new OnMultipleRequestListener<ArchiveLike>() {
            @Override
            public void onResponse(List<ArchiveLike> list, boolean success, int totalPages, int pageSize, int total, int pageNumber) {
                super.onResponse(list, success, totalPages, pageSize, total, pageNumber);
                onFetchingPraisesComplete(list, success, pageSize);
            }
        }).list(Comment.Type.MOMENT, mQueryId, 1);
    }

    protected void onFetchingPraisesComplete(List<ArchiveLike> list, boolean success, int pageSize) {
    }
}
