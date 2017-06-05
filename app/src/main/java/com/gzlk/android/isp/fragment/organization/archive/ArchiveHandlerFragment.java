package com.gzlk.android.isp.fragment.organization.archive;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.gzlk.android.isp.R;
import com.gzlk.android.isp.adapter.RecyclerViewAdapter;
import com.gzlk.android.isp.api.archive.ArchiveRequest;
import com.gzlk.android.isp.api.listener.OnSingleRequestListener;
import com.gzlk.android.isp.fragment.base.BaseSwipeRefreshSupportFragment;
import com.gzlk.android.isp.helper.StringHelper;
import com.gzlk.android.isp.helper.ToastHelper;
import com.gzlk.android.isp.holder.BaseViewHolder;
import com.gzlk.android.isp.holder.archive.ArchiveDetailsHeaderViewHolder;
import com.gzlk.android.isp.holder.attachment.AttachmentViewHolder;
import com.gzlk.android.isp.lib.Json;
import com.gzlk.android.isp.model.Model;
import com.gzlk.android.isp.model.archive.Archive;
import com.gzlk.android.isp.model.common.Attachment;
import com.hlk.hlklib.lib.inject.Click;
import com.hlk.hlklib.lib.inject.ViewId;

import java.util.ArrayList;

/**
 * <b>功能描述：</b>组织档案审批页面<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/06/05 21:13 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/06/05 21:13 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class ArchiveHandlerFragment extends BaseSwipeRefreshSupportFragment {

    /**
     * 审批档案
     */
    public static final int TYPE_APPROVE = 1;
    /**
     * 存档档案
     */
    public static final int TYPE_ARCHIVE = 2;

    private static final String PARAM_TYPE = "ahf_param_type";
    private static final String PARAM_CONTENT = "ahf_param_content";

    public static ArchiveHandlerFragment newInstance(String params) {
        ArchiveHandlerFragment aaf = new ArchiveHandlerFragment();
        String[] strings = splitParameters(params);
        Bundle bundle = new Bundle();
        bundle.putInt(PARAM_TYPE, Integer.valueOf(strings[0]));
        bundle.putString(PARAM_QUERY_ID, strings[1]);
        bundle.putString(PARAM_CONTENT, strings[2]);
        aaf.setArguments(bundle);
        return aaf;
    }

    @ViewId(R.id.ui_tool_chatable_inputbar_container)
    private LinearLayout chatContainer;
    @ViewId(R.id.ui_archive_approve_container)
    private LinearLayout approveContainer;
    @ViewId(R.id.ui_archive_approve_reject_text)
    private TextView rejectTextView;
    @ViewId(R.id.ui_archive_approve_passed_text)
    private TextView passedTextView;

    private int mType = TYPE_APPROVE;
    private String mContent = "";
    private DocumentDetailsAdapter mAdapter;

    @Override
    protected void getParamsFromBundle(Bundle bundle) {
        super.getParamsFromBundle(bundle);
        mType = bundle.getInt(PARAM_TYPE, TYPE_APPROVE);
        mContent = bundle.getString(PARAM_CONTENT, "");
    }

    @Override
    protected void saveParamsToBundle(Bundle bundle) {
        super.saveParamsToBundle(bundle);
        bundle.putInt(PARAM_TYPE, mType);
        bundle.putString(PARAM_CONTENT, mContent);
    }

    @Override
    protected void onDelayRefreshComplete(@DelayType int type) {

    }

    @Override
    public void doingInResume() {
        chatContainer.setVisibility(View.GONE);
        approveContainer.setVisibility(View.VISIBLE);
        rejectTextView.setText(mType == TYPE_APPROVE ? "审核不通过" : "放弃存档");
        passedTextView.setText(mType == TYPE_APPROVE ? "审核通过" : "存为档案");

        if (StringHelper.isEmpty(mQueryId)) {
            closeWithWarning(R.string.ui_text_document_details_not_exists);
        } else {
            setCustomTitle(mType == TYPE_APPROVE ? "档案审核" : "活动档案存档");
            initializeAdapter();
        }
    }

    @Override
    public int getLayout() {
        return R.layout.fragment_archive_details;
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
        fetchingDocument();
    }

    @Override
    protected void onLoadingMore() {

    }

    @Override
    protected String getLocalPageTag() {
        return null;
    }

    @Click({R.id.ui_archive_approve_reject,
            R.id.ui_archive_approve_passed})
    private void elementClick(View view) {
        switch (view.getId()) {
            case R.id.ui_archive_approve_reject:
                // 审核通不过
                reject();
                break;
            case R.id.ui_archive_approve_passed:
                // 审核通过
                passed();
                break;
        }
    }

    private void reject() {
        if (mType == TYPE_APPROVE) {
            passedApprove(Archive.ArchiveStatus.FAILURE);
        } else {
            passedArchive(Archive.ArchiveStatus.FAILURE);
        }
    }

    private void passed() {
        if (mType == TYPE_APPROVE) {
            passedApprove(Archive.ArchiveStatus.APPROVED);
        } else {
            passedArchive(Archive.ArchiveStatus.APPROVED);
        }
    }

    private void passedApprove(int status) {
        ArchiveRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<Archive>() {
            @Override
            public void onResponse(Archive archive, boolean success, String message) {
                super.onResponse(archive, success, message);
                if (success) {
                    // 审核通过了，重新拉取档案信息以便刷新UI
                    ToastHelper.make().showMsg(mType == TYPE_APPROVE ? "已审核" : "已存档");
                    finish();
                }
            }
        }).approve(mQueryId, status);
    }

    private void passedArchive(int status) {
        ArchiveRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<Archive>() {
            @Override
            public void onResponse(Archive archive, boolean success, String message) {
                super.onResponse(archive, success, message);
            }
        }).archive(mQueryId, status);
    }

    // 拉取档案内容
    private void fetchingDocument() {
        if (mType == TYPE_APPROVE) {
            findApproveArchive();
        } else {
            Archive archive = Json.gson().fromJson(replaceJson(mContent, true), new TypeToken<Archive>() {
            }.getType());
            if (null != archive) {
                mAdapter.update(archive);
                loadingAttachments(archive);
            }
        }
    }

    private void findApproveArchive() {
        ArchiveRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<Archive>() {
            @Override
            public void onResponse(Archive archive, boolean success, String message) {
                super.onResponse(archive, success, message);
                if (success) {
                    if (null != archive) {
                        mAdapter.update(archive);
                        loadingAttachments(archive);
                    }
                }
            }
        }).approveFind(mQueryId);
    }

    private void loadingAttachments(final Archive archive) {
        // office 文件列表
        loadingAttachments(archive.getOffice());
        // image
        loadingAttachments(archive.getImage());
        // video
        loadingAttachments(archive.getVideo());
        // other
        loadingAttachments(archive.getAttach());
    }

    private void loadingAttachments(ArrayList<Attachment> list) {
        if (null != list && list.size() > 0) {
            for (Attachment att : list) {
                mAdapter.update(att);
            }
        }
    }

    private void initializeAdapter() {
        if (null == mAdapter) {
            mAdapter = new DocumentDetailsAdapter();
            mRecyclerView.setAdapter(mAdapter);
            fetchingDocument();
        }
    }

    private class DocumentDetailsAdapter extends RecyclerViewAdapter<BaseViewHolder, Model> {
        private static final int VT_HEADER = 0, VT_ATTACHMENT = 3;

        @Override
        public BaseViewHolder onCreateViewHolder(View itemView, int viewType) {
            ArchiveHandlerFragment fragment = ArchiveHandlerFragment.this;
            switch (viewType) {
                case VT_HEADER:
                    return new ArchiveDetailsHeaderViewHolder(itemView, fragment);
                case VT_ATTACHMENT:
                    return new AttachmentViewHolder(itemView, fragment);
                default:
                    return null;
            }
        }

        @Override
        public int itemLayout(int viewType) {
            switch (viewType) {
                case VT_HEADER:
                    return R.layout.tool_view_document_details_header;
                case VT_ATTACHMENT:
                    return R.layout.holder_view_attachment;
                default:
                    return R.layout.holder_view_document_comment;
            }
        }

        @Override
        public int getItemViewType(int position) {
            Model model = get(position);
            if (model instanceof Archive) {
                return VT_HEADER;
            }
            return VT_ATTACHMENT;
        }

        @Override
        public void onBindHolderOfView(BaseViewHolder holder, int position, @Nullable Model item) {
            if (holder instanceof ArchiveDetailsHeaderViewHolder) {
                ((ArchiveDetailsHeaderViewHolder) holder).showContent((Archive) item);
            } else if (holder instanceof AttachmentViewHolder) {
                ((AttachmentViewHolder) holder).setEditable(false);
                ((AttachmentViewHolder) holder).showContent((Attachment) item);
            }
        }

        @Override
        protected int comparator(Model item1, Model item2) {
            return 0;
        }
    }
}
