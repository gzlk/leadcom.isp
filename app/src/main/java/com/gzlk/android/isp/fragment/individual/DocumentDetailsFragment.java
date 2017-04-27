package com.gzlk.android.isp.fragment.individual;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;

import com.gzlk.android.isp.R;
import com.gzlk.android.isp.api.listener.OnRequestListener;
import com.gzlk.android.isp.api.user.DocumentRequest;
import com.gzlk.android.isp.application.App;
import com.gzlk.android.isp.fragment.base.BaseSwipeRefreshSupportFragment;
import com.gzlk.android.isp.helper.DialogHelper;
import com.gzlk.android.isp.helper.SimpleDialogHelper;
import com.gzlk.android.isp.helper.StringHelper;
import com.gzlk.android.isp.helper.ToastHelper;
import com.gzlk.android.isp.listener.OnTitleButtonClickListener;
import com.gzlk.android.isp.model.Dao;
import com.gzlk.android.isp.model.Model;
import com.gzlk.android.isp.model.user.document.Document;
import com.gzlk.android.isp.model.user.document.DocumentComment;
import com.gzlk.android.isp.multitype.adapter.BaseMultiTypeAdapter;
import com.gzlk.android.isp.multitype.binder.user.DocumentDetailsHeaderViewBinder;

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

public class DocumentDetailsFragment extends BaseSwipeRefreshSupportFragment {

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
    public void doingInResume() {
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

    }

    @Override
    protected void onLoadingMore() {

    }

    private void initializeAdapter() {
        if (null == mAdapter) {
            mAdapter = new DocumentDetailsAdapter();
            mAdapter.register(Document.class, new DocumentDetailsHeaderViewBinder().setFragment(this));
            mRecyclerView.setAdapter(mAdapter);
        }
        Document document = new Dao<>(Document.class).query(mQueryId);
        if (null == document) {
            // 档案不存在则从服务器上拉取
            fetchingDocument();
        } else {
            resetRightTitleButton(document);
            mAdapter.update(document);
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
                    new Dao<>(Document.class).delete(mQueryId);
                    finish();
                }
            }
        }).delete(mQueryId);
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
