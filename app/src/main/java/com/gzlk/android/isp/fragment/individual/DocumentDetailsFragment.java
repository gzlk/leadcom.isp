package com.gzlk.android.isp.fragment.individual;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.MenuItem;
import android.view.View;

import com.gzlk.android.isp.R;
import com.gzlk.android.isp.api.listener.OnRequestListListener;
import com.gzlk.android.isp.api.listener.OnRequestListener;
import com.gzlk.android.isp.api.user.DocCommentRequest;
import com.gzlk.android.isp.api.user.DocumentRequest;
import com.gzlk.android.isp.application.App;
import com.gzlk.android.isp.fragment.base.BaseChatInputSupportFragment;
import com.gzlk.android.isp.helper.DialogHelper;
import com.gzlk.android.isp.helper.SimpleDialogHelper;
import com.gzlk.android.isp.helper.StringHelper;
import com.gzlk.android.isp.helper.ToastHelper;
import com.gzlk.android.isp.listener.OnLiteOrmTaskExecutedListener;
import com.gzlk.android.isp.listener.OnLiteOrmTaskExecutingListener;
import com.gzlk.android.isp.listener.OnTitleButtonClickListener;
import com.gzlk.android.isp.model.Dao;
import com.gzlk.android.isp.model.Model;
import com.gzlk.android.isp.model.user.document.Document;
import com.gzlk.android.isp.model.user.document.DocumentComment;
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

    private void closeWithWarning(int text) {
        ToastHelper.make().showMsg(text);
        finish();
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
            mAdapter.register(Document.class, new DocumentDetailsHeaderViewBinder().setFragment(this));
            mAdapter.register(DocumentComment.class, new DocumentCommentViewBinder().setFragment(this));
            mRecyclerView.setAdapter(mAdapter);
            loadingDocument();
        }
    }

    private void loadingDocument() {
        Document document = new Dao<>(Document.class).query(mQueryId);
        if (null == document) {
            // 档案不存在则从服务器上拉取
            fetchingDocument();
        } else {
            mAdapter.update(document);
            resetRightTitleButton(document);
        }
    }

    @SuppressWarnings("ConstantConditions")
    private void resetRightTitleButton(@NonNull Document document) {
        if (document.getUserId().equals(App.app().UserId())) {
            setRightIcon(R.string.ui_icon_more);
            setRightTitleClickListener(new OnTitleButtonClickListener() {
                @Override
                public void onClick() {
                    openEditSelector();
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
        DocCommentRequest.request().setOnRequestListListener(new OnRequestListListener<DocumentComment>() {
            @SuppressWarnings("unchecked")
            @Override
            public void onResponse(List<DocumentComment> list, boolean success, int totalPages, int pageSize, int total, int pageNumber) {
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
                        new Dao<>(DocumentComment.class).save(list);
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
        new OrmTask<DocumentComment>().addOnLiteOrmTaskExecutingListener(new OnLiteOrmTaskExecutingListener<DocumentComment>() {
            @Override
            public boolean isModifiable() {
                return false;
            }

            @Override
            public List<DocumentComment> executing(OrmTask<DocumentComment> task) {
                // 分页查找
                QueryBuilder<DocumentComment> builder = new QueryBuilder<>(DocumentComment.class)
                        .whereEquals(DocumentComment.Field.UserDocumentId, mQueryId)
                        .orderBy(Model.Field.CreateDate)
                        .limit(localPageNumber * PAGE_SIZE, PAGE_SIZE);
                return new Dao<>(DocumentComment.class).query(builder);
            }
        }).addOnLiteOrmTaskExecutedListener(new OnLiteOrmTaskExecutedListener<DocumentComment>() {
            @SuppressWarnings("unchecked")
            @Override
            public void onExecuted(boolean modified, List<DocumentComment> result) {
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
                        openActivity(DocumentNewFragment.class.getName(), mQueryId, true, true);
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
        DocumentRequest.request().setOnRequestListener(new OnRequestListener<Document>() {
            @Override
            public void onResponse(Document document, boolean success, String message) {
                super.onResponse(document, success, message);
                if (success) {
                    ToastHelper.make().showMsg(message);
                    Dao<Document> dao = new Dao<>(Document.class);
                    Document doc = dao.query(mQueryId);
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

    public DocumentComment getFromPosition(int position) {
        return (DocumentComment) mAdapter.get(position);
    }

    public void deleteComment(final int position) {
        final DocumentComment cmt = getFromPosition(position);
        DocCommentRequest.request().setOnRequestListener(new OnRequestListener<DocumentComment>() {
            @Override
            public void onResponse(DocumentComment comment, boolean success, String message) {
                super.onResponse(comment, success, message);
                if (success) {
                    // 删除成功之后本地评论也删除
                    new Dao<>(DocumentComment.class).delete(cmt);
                    mAdapter.remove(position);
                }
            }
        }).delete(mQueryId, cmt.getId());
    }

    private void fetchingDocument() {
        DocumentRequest.request().setOnRequestListener(new OnRequestListener<Document>() {
            @Override
            public void onResponse(Document document, boolean success, String message) {
                super.onResponse(document, success, message);
                if (success && null != message) {
                    new Dao<>(Document.class).save(document);
                    mAdapter.update(document);
                    resetRightTitleButton(document);
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
        DocCommentRequest.request().setOnRequestListener(new OnRequestListener<DocumentComment>() {
            @Override
            public void onResponse(DocumentComment comment, boolean success, String message) {
                super.onResponse(comment, success, message);
                if (success) {
                    if (null != comment && !StringHelper.isEmpty(comment.getId())) {
                        new Dao<>(DocumentComment.class).save(comment);
                        mAdapter.update(comment);
                    }
                    refreshing();
                    fetchingRemote();
                }
            }
        }).add(mQueryId, text, App.app().UserId(), App.app().Me().getName());
    }

    private class DocumentDetailsAdapter extends BaseMultiTypeAdapter<Model> {
        @Override
        protected int comparator(Model item1, Model item2) {
            if (item1 instanceof DocumentComment && item2 instanceof DocumentComment) {
                // 按照创建时间倒序排序
                int compared = ((DocumentComment) item1).getCreateDate().compareTo(((DocumentComment) item2).getCreateDate());
                return compared == 0 ? 0 : -compared;
            }
            return 0;
        }
    }
}
