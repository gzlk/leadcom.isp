package com.gzlk.android.isp.fragment.individual;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;

import com.gzlk.android.isp.R;
import com.gzlk.android.isp.api.listener.OnMultipleRequestListener;
import com.gzlk.android.isp.api.listener.OnSingleRequestListener;
import com.gzlk.android.isp.api.archive.UserArchiveCommentRequest;
import com.gzlk.android.isp.api.archive.UserArchiveRequest;
import com.gzlk.android.isp.cache.Cache;
import com.gzlk.android.isp.fragment.base.BaseChatInputSupportFragment;
import com.gzlk.android.isp.helper.DialogHelper;
import com.gzlk.android.isp.helper.SimpleDialogHelper;
import com.gzlk.android.isp.helper.StringHelper;
import com.gzlk.android.isp.helper.ToastHelper;
import com.gzlk.android.isp.listener.OnLiteOrmTaskExecutedListener;
import com.gzlk.android.isp.listener.OnLiteOrmTaskExecutingListener;
import com.gzlk.android.isp.listener.OnTitleButtonClickListener;
import com.gzlk.android.isp.model.archive.Archive;
import com.gzlk.android.isp.model.Dao;
import com.gzlk.android.isp.model.Model;
import com.gzlk.android.isp.model.archive.Comment;
import com.gzlk.android.isp.model.user.UserArchive;
import com.gzlk.android.isp.multitype.adapter.BaseMultiTypeAdapter;
import com.gzlk.android.isp.multitype.binder.user.DocumentCommentViewBinder;
import com.gzlk.android.isp.multitype.binder.user.DocumentDetailsHeaderViewBinder;
import com.gzlk.android.isp.task.OrmTask;
import com.litesuits.orm.db.assit.QueryBuilder;

import java.util.List;

/**
 * <b>功能描述：</b>档案详情页<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/04/27 08:24 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/04/27 08:24 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class DocumentDetailsFragment extends BaseChatInputSupportFragment {

    private static final String TAG = "document_%s";

    public static DocumentDetailsFragment newInstance(String params) {
        DocumentDetailsFragment ddf = new DocumentDetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putString(PARAM_QUERY_ID, params);
        ddf.setArguments(bundle);
        return ddf;
    }

    private DocumentDetailsAdapter mAdapter;

    @Override
    protected void onDelayRefreshComplete(@DelayType int type) {

    }

    @Override
    public int getLayout() {
        return R.layout.fragment_chatable;
    }

    @Override
    public void doingInResume() {
        showAppend = false;
        showRecorder = false;
        super.doingInResume();
        if (StringHelper.isEmpty(mQueryId)) {
            closeWithWarning(R.string.ui_text_document_details_not_exists);
        } else {
            setCustomTitle(R.string.ui_text_document_details_fragment_title);
            initializeAdapter();
        }
    }

    @Override
    protected boolean shouldSetDefaultTitleEvents() {
        return true;
    }

    @Override
    protected void destroyView() {

    }

    @Override
    protected void onSwipeRefreshing() {
        remotePageNumber = 1;
        fetchingRemote();
    }

    @Override
    protected void onLoadingMore() {
        loadingLocalCache();
    }

    @Override
    protected String getLocalPageTag() {
        return format(TAG, mQueryId);
    }

    private void initializeAdapter() {
        if (null == mAdapter) {
            addOnInputCompleteListener(onInputCompleteListener);
            mAdapter = new DocumentDetailsAdapter();
            mAdapter.register(UserArchive.class, new DocumentDetailsHeaderViewBinder().setFragment(this));
            mAdapter.register(Comment.class, new DocumentCommentViewBinder().setFragment(this));
            mRecyclerView.setAdapter(mAdapter);
            loadingDocument();
        }
    }

    private void loadingDocument() {
        UserArchive userArchive = new Dao<>(UserArchive.class).query(mQueryId);
        if (null == userArchive) {
            // 档案不存在则从服务器上拉取
            fetchingDocument();
        } else {
            mAdapter.update(userArchive);
            resetRightTitleButton(userArchive);
        }
    }

    private void resetRightTitleButton(@NonNull UserArchive userArchive) {
        if (userArchive.getUserId().equals(Cache.cache().userId)) {
            //setRightIcon(R.string.ui_icon_more);
            setRightText(R.string.ui_base_text_edit);
            setRightTitleClickListener(new OnTitleButtonClickListener() {
                @Override
                public void onClick() {
                    //openEditSelector();
                    openActivity(DocumentNewFragment.class.getName(), format("%d,%s", Archive.Type.INDIVIDUAL, mQueryId), true, true);
                }
            });
        }
        loadingLocalCache();
    }

    // 尝试拉取远程资源
    private void tryFetchingRemote() {
        if (isNeedRefresh()) {
            refreshing();
            fetchingRemote();
        } else {
            stopRefreshing();
        }
    }

    private void fetchingRemote() {
        UserArchiveCommentRequest.request().setOnMultipleRequestListener(new OnMultipleRequestListener<Comment>() {
            @SuppressWarnings("unchecked")
            @Override
            public void onResponse(List<Comment> list, boolean success, int totalPages, int pageSize, int total, int pageNumber) {
                super.onResponse(list, success, totalPages, pageSize, total, pageNumber);
                if (success) {
                    remotePageSize = pageSize;
                    remoteTotalPages = totalPages;
                    remoteTotalCount = total;
                    remotePageNumber = pageNumber;
                    if (list.size() >= remotePageSize) {
                        // 如果取满了一页，则下次需要拉取下一页
                        remotePageNumber += 1;
                    }
                    if (list.size() > 0) {
                        new Dao<>(Comment.class).save(list);
                        mAdapter.update((List<Model>) (Object) list);
                    }
                }
                stopRefreshing();
                smoothScrollToBottom(mAdapter.getItemCount() - 1);
            }
        }).list(mQueryId, PAGE_SIZE, remotePageNumber);
    }

    // 加载本地缓存
    private void loadingLocalCache() {
        new OrmTask<Comment>().addOnLiteOrmTaskExecutingListener(new OnLiteOrmTaskExecutingListener<Comment>() {
            @Override
            public boolean isModifiable() {
                return false;
            }

            @Override
            public List<Comment> executing(OrmTask<Comment> task) {
                // 分页查找
                QueryBuilder<Comment> builder = new QueryBuilder<>(Comment.class)
                        .whereEquals(Archive.Field.UserArchiveId, mQueryId)
                        .orderBy(Model.Field.CreateDate)
                        .limit(localPageNumber * PAGE_SIZE, PAGE_SIZE);
                return new Dao<>(Comment.class).query(builder);
            }
        }).addOnLiteOrmTaskExecutedListener(new OnLiteOrmTaskExecutedListener<Comment>() {
            @SuppressWarnings("unchecked")
            @Override
            public void onExecuted(boolean modified, List<Comment> result) {
                if (null != result) {
                    if (result.size() >= PAGE_SIZE) {
                        // 取满一页后下一次需要取下一页
                        localPageNumber++;
                    }
                    mAdapter.update((List<Model>) (Object) result);
                    fetchingRemote();
                    isLoadingComplete(result.size() < PAGE_SIZE);
                }
            }
        }).exec();
    }

    private void openEditSelector() {
        DialogHelper.init(Activity()).addOnDialogInitializeListener(new DialogHelper.OnDialogInitializeListener() {
            @Override
            public View onInitializeView() {
                return View.inflate(Activity(), R.layout.popup_dialog_edit_selector, null);
            }

            @Override
            public void onBindData(View dialogView, DialogHelper helper) {

            }
        }).addOnEventHandlerListener(new DialogHelper.OnEventHandlerListener() {
            @Override
            public int[] clickEventHandleIds() {
                return new int[]{R.id.ui_dialog_button_editor_to_change, R.id.ui_dialog_button_editor_to_delete};
            }

            @Override
            public boolean onClick(View view) {
                int id = view.getId();
                switch (id) {
                    case R.id.ui_dialog_button_editor_to_change:
                        openActivity(DocumentNewFragment.class.getName(), format("%d,%s", Archive.Type.INDIVIDUAL, mQueryId), true, true);
                        break;
                    case R.id.ui_dialog_button_editor_to_delete:
                        warningDeleteDocument();
                        break;
                }
                return true;
            }
        }).setPopupType(DialogHelper.TYPE_SLID).setAdjustScreenWidth(true).show();
    }

    private void warningDeleteDocument() {
        SimpleDialogHelper.init(Activity()).show(R.string.ui_text_document_details_delete, R.string.ui_base_text_yes, R.string.ui_base_text_no_need, new DialogHelper.OnDialogConfirmListener() {
            @Override
            public boolean onConfirm() {
                deleteDocument();
                return true;
            }
        }, null);
    }

    private void deleteDocument() {
        UserArchiveRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<UserArchive>() {
            @Override
            public void onResponse(UserArchive userArchive, boolean success, String message) {
                super.onResponse(userArchive, success, message);
                if (success) {
                    ToastHelper.make().showMsg(message);
                    Dao<UserArchive> dao = new Dao<>(UserArchive.class);
                    UserArchive doc = dao.query(mQueryId);
                    if (null != doc) {
                        doc.setLocalDeleted(true);
                        dao.save(doc);
                    }
                    // 返回成功
                    finish();
                }
            }
        }).delete(mQueryId);
    }

    public Comment getFromPosition(int position) {
        return (Comment) mAdapter.get(position);
    }

    public void deleteComment(final int position) {
        final Comment cmt = getFromPosition(position);
        UserArchiveCommentRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<Comment>() {
            @Override
            public void onResponse(Comment comment, boolean success, String message) {
                super.onResponse(comment, success, message);
                if (success) {
                    // 删除成功之后本地评论也删除
                    new Dao<>(Comment.class).delete(cmt);
                    mAdapter.remove(position);
                }
            }
        }).delete(mQueryId, cmt.getId());
    }

    private void fetchingDocument() {
        UserArchiveRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<UserArchive>() {
            @Override
            public void onResponse(UserArchive userArchive, boolean success, String message) {
                super.onResponse(userArchive, success, message);
                if (success && null != message) {
                    new Dao<>(UserArchive.class).save(userArchive);
                    mAdapter.update(userArchive);
                    resetRightTitleButton(userArchive);
                } else {
                    closeWithWarning(R.string.ui_text_document_details_not_exists);
                }
            }
        }).find(mQueryId);
    }

    private OnInputCompleteListener onInputCompleteListener = new OnInputCompleteListener() {
        @Override
        public void onInputComplete(String text, int length, int type) {
            tryComment(text);
        }
    };

    @SuppressWarnings("ConstantConditions")
    private void tryComment(String text) {
        UserArchiveCommentRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<Comment>() {
            @Override
            public void onResponse(Comment comment, boolean success, String message) {
                super.onResponse(comment, success, message);
                if (success) {
                    if (null != comment && !StringHelper.isEmpty(comment.getId())) {
                        new Dao<>(Comment.class).save(comment);
                        mAdapter.update(comment);
                    }
                    refreshing();
                    fetchingRemote();
                }
            }
        }).add(mQueryId, text);
    }

    private class DocumentDetailsAdapter extends BaseMultiTypeAdapter<Model> {
        @Override
        protected int comparator(Model item1, Model item2) {
            if (item1 instanceof Comment && item2 instanceof Comment) {
                // 按照创建时间倒序排序
                int compared = ((Comment) item1).getCreateDate().compareTo(((Comment) item2).getCreateDate());
                return compared == 0 ? 0 : -compared;
            }
            return 0;
        }
    }
}
