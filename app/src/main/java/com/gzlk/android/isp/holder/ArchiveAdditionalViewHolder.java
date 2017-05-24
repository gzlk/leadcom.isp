package com.gzlk.android.isp.holder;

import android.view.View;
import android.widget.TextView;

import com.gzlk.android.isp.R;
import com.gzlk.android.isp.api.archive.LikeRequest;
import com.gzlk.android.isp.api.listener.OnSingleRequestListener;
import com.gzlk.android.isp.cache.Cache;
import com.gzlk.android.isp.fragment.base.BaseFragment;
import com.gzlk.android.isp.helper.StringHelper;
import com.gzlk.android.isp.model.Dao;
import com.gzlk.android.isp.model.Model;
import com.gzlk.android.isp.model.archive.Archive;
import com.gzlk.android.isp.model.archive.ArchiveLike;
import com.gzlk.android.isp.model.archive.Comment;
import com.gzlk.android.isp.model.user.Moment;
import com.hlk.hlklib.lib.inject.Click;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.inject.ViewUtility;
import com.hlk.hlklib.lib.view.CustomTextView;
import com.litesuits.orm.db.assit.QueryBuilder;
import com.litesuits.orm.db.assit.WhereBuilder;

import java.util.List;

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
    @ViewId(R.id.ui_tool_view_document_additional_like_icon)
    private CustomTextView likeIcon;
    @ViewId(R.id.ui_tool_view_document_additional_comment)
    private TextView commentNumber;
    @ViewId(R.id.ui_tool_view_document_additional_favorite)
    private TextView favoriteNumber;
    @ViewId(R.id.ui_tool_view_document_additional_favorite_icon)
    private CustomTextView favoriteIcon;

    public ArchiveAdditionalViewHolder(View itemView, BaseFragment fragment) {
        super(itemView, fragment);
        ViewUtility.bind(this, itemView);
    }

    public void showContent(Archive archive) {
        readNumber.setText(String.valueOf(archive.getReadNum()));
        likeNumber.setText(String.valueOf(archive.getLikeNum()));
        ArchiveLike like = getLike(type(archive), archive.getId());
        resetLikeIcon(like != null);
        commentNumber.setText(String.valueOf(archive.getCmtNum()));
        favoriteNumber.setText(String.valueOf(archive.getColNum()));
    }

    private void resetLikeIcon(boolean liked) {
        likeIcon.setText(liked ? R.string.ui_icon_like_solid : R.string.ui_icon_like_hollow);
        likeIcon.setTextColor(getColor(liked ? R.color.colorPrimary : R.color.textColorHint));
    }

    @Click({R.id.ui_tool_view_document_additional_like_container,
            R.id.ui_tool_view_document_additional_favorite_container})
    private void click(View view) {
        switch (view.getId()) {
            case R.id.ui_tool_view_document_additional_like_container:
                tryLike();
                break;
            case R.id.ui_tool_view_document_additional_favorite_container:
                collectArchive();
                break;
        }
    }

    private void tryLike() {
        if (null != dataHandlerBoundDataListener) {
            Model model = (Model) dataHandlerBoundDataListener.onHandlerBoundData(this);
            if (null != model) {
                likeModel(model);
            }
        }
    }

    private void likeModel(Model model) {
        if (model instanceof Archive) {
            likeArchive((Archive) model);
        } else if (model instanceof Moment) {
            // 点赞说说
        }
    }

    private int type(Model model) {
        if (model instanceof Moment) return Comment.Type.MOMENT;
        if (model instanceof Archive) {
            Archive archive = (Archive) model;
            return StringHelper.isEmpty(archive.getGroupId()) ? Comment.Type.USER : Comment.Type.GROUP;
        }
        // 默认组织档案点赞
        return Comment.Type.GROUP;
    }

    private String field(int type) {
        switch (type) {
            case Comment.Type.GROUP:
                return Archive.Field.GroupArchiveId;
            case Comment.Type.USER:
                return Archive.Field.UserArchiveId;
            default:
                return Archive.Field.UserMomentId;
        }
    }

    private ArchiveLike getLike(int type, String archiveId) {
        QueryBuilder<ArchiveLike> builder = new QueryBuilder<>(ArchiveLike.class)
                .whereEquals(Model.Field.UserId, Cache.cache().userId)
                .whereAppendAnd()
                .whereEquals(field(type), archiveId);
        List<ArchiveLike> list = new Dao<>(ArchiveLike.class).query(builder);
        return (null != list && list.size() > 0) ? list.get(0) : null;
    }

    private void likeArchive(Archive archive) {
        ArchiveLike like = getLike(type(archive), archive.getId());
        int type = type(archive);
        if (null == like) {
            likeArchive(type, archive.getId());
        } else {
            unlikeArchive(type, archive.getId());
        }
    }

    private void likeArchive(int type, String archiveId) {
        LikeRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<ArchiveLike>() {
            @Override
            public void onResponse(ArchiveLike archiveLike, boolean success, String message) {
                super.onResponse(archiveLike, success, message);
                if (success) {
                    resetLikeIcon(null != archiveLike);
                }
            }
        }).add(type, archiveId);
    }

    private void unlikeArchive(final int type, final String archiveId) {
        LikeRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<ArchiveLike>() {
            @Override
            public void onResponse(ArchiveLike archiveLike, boolean success, String message) {
                super.onResponse(archiveLike, success, message);
                if (success) {
                    // 删除本地点赞记录
                    deleteLike(type, archiveId);
                    resetLikeIcon(false);
                }
            }
        }).delete(type, archiveId);
    }

    private void deleteLike(int type, String archiveId) {
        String filed = field(type);
        WhereBuilder builder = new WhereBuilder(ArchiveLike.class)
                .where(filed + " = ?", archiveId).and(Model.Field.UserId + " = ?", Cache.cache().userId);
        new Dao<>(ArchiveLike.class).delete(builder);
    }

    private void collectArchive() {
        if (null != dataHandlerBoundDataListener) {
            Model model = (Archive) dataHandlerBoundDataListener.onHandlerBoundData(this);
            if (null != model && model instanceof Archive) {
                collect((Archive) model);
            }
        }
    }

    private void collect(Archive archive) {
    }
}
