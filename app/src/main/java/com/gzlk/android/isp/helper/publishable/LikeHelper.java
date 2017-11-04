package com.gzlk.android.isp.helper.publishable;

import com.gzlk.android.isp.api.archive.LikeRequest;
import com.gzlk.android.isp.api.listener.OnMultipleRequestListener;
import com.gzlk.android.isp.api.listener.OnSingleRequestListener;
import com.gzlk.android.isp.helper.publishable.listener.OnLikeListListener;
import com.gzlk.android.isp.helper.publishable.listener.OnLikeListener;
import com.gzlk.android.isp.helper.publishable.listener.OnUnlikeListener;
import com.gzlk.android.isp.model.Model;
import com.gzlk.android.isp.model.archive.Archive;
import com.gzlk.android.isp.model.archive.ArchiveLike;
import com.gzlk.android.isp.model.user.Moment;

import java.util.List;

/**
 * <b>功能描述：</b>点赞相关helper<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/10/31 18:46 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>version: 1.0.0 <br />
 * <b>修改时间：</b>2017/10/31 18:46 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */
public class LikeHelper extends Publishable {

    public static LikeHelper helper() {
        return new LikeHelper();
    }

    @Override
    public LikeHelper setModel(Model model) {
        if (null == model) {
            throw new IllegalArgumentException("Cannot set null object to like helper.");
        }
        super.setModel(model);
        return this;
    }

    @Override
    public LikeHelper setArchive(Archive archive) {
        mArchive = archive;
        return this;
    }

    @Override
    public LikeHelper setMoment(Moment moment) {
        mMoment = moment;
        return this;
    }

    private OnLikeListener likeListener;

    public LikeHelper setLikeListener(OnLikeListener l) {
        likeListener = l;
        return this;
    }

    private OnUnlikeListener unlikeListener;

    public LikeHelper setUnlikeListener(OnUnlikeListener l) {
        unlikeListener = l;
        return this;
    }

    private OnLikeListListener listListener;

    public LikeHelper setOnLikeListListener(OnLikeListListener l) {
        listListener = l;
        return this;
    }

    public void like() {
        LikeRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<ArchiveLike>() {
            @Override
            public void onResponse(ArchiveLike like, boolean success, String message) {
                super.onResponse(like, success, message);
                if (null != likeListener) {
                    if (null != mArchive) {
                        if (success) {
                            mArchive.setLike(Archive.LikeType.LIKED);
                            mArchive.setLikeId(like.getId());
                            mArchive.setLikeNum(mArchive.getLikeNum() + 1);
                        }
                        likeListener.onLiked(success, mArchive);
                    } else if (null != mMoment) {
                        if (success) {
                            mMoment.getUserMmtLikeList().add(like);
                            mMoment.setLikeNum(mMoment.getLikeNum() + 1);
                            mMoment.setLikeId(like.getId());
                            mMoment.setLike(Archive.LikeType.LIKED);
                        }
                        likeListener.onLiked(success, mMoment);
                    }
                }
            }
        }).add(getMethodType(), getHostId());
    }

    public void unlike() {
        LikeRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<ArchiveLike>() {
            @Override
            public void onResponse(ArchiveLike like, boolean success, String message) {
                super.onResponse(like, success, message);
                if (null != unlikeListener) {
                    if (null != mArchive) {
                        if (success) {
                            int num = mArchive.getLikeNum() - 1;
                            mArchive.setLikeNum(num >= 0 ? num : 0);
                            mArchive.setLikeId("");
                            mArchive.setLike(Archive.LikeType.UN_LIKE);
                        }
                        unlikeListener.onUnlike(success, mArchive);
                    } else if (null != mMoment) {
                        if (success) {
                            int num = mMoment.getLikeNum() - 1;
                            mMoment.setLikeNum(num >= 0 ? num : 0);
                            mMoment.setLike(Archive.LikeType.UN_LIKE);
                            ArchiveLike del = new ArchiveLike();
                            del.setId(mMoment.getLikeId());
                            mMoment.getUserMmtLikeList().remove(del);
                            mMoment.setLikeId("");
                        }
                        unlikeListener.onUnlike(success, mMoment);
                    }
                }
            }
        }).delete(getMethodType(), getHostId());
    }

    public void list() {
        LikeRequest.request().setOnMultipleRequestListener(new OnMultipleRequestListener<ArchiveLike>() {
            @Override
            public void onResponse(List<ArchiveLike> list, boolean success, int totalPages, int pageSize, int total, int pageNumber) {
                super.onResponse(list, success, totalPages, pageSize, total, pageNumber);
                if (null != listListener) {
                    listListener.onList(list, success, pageSize);
                }
            }
        }).list(getMethodType(), getHostId(), 1);
    }
}
