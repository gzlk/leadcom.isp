package com.leadcom.android.isp.fragment.archive;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.leadcom.android.isp.R;
import com.leadcom.android.isp.adapter.RecyclerViewAdapter;
import com.leadcom.android.isp.api.archive.ArchiveRequest;
import com.leadcom.android.isp.api.listener.OnMultipleRequestListener;
import com.leadcom.android.isp.api.listener.OnSingleRequestListener;
import com.leadcom.android.isp.fragment.base.BaseFragment;
import com.leadcom.android.isp.fragment.base.BaseSwipeRefreshSupportFragment;
import com.leadcom.android.isp.helper.popup.DeleteDialogHelper;
import com.leadcom.android.isp.helper.popup.DialogHelper;
import com.leadcom.android.isp.holder.BaseViewHolder;
import com.leadcom.android.isp.holder.archive.ArchiveDraftViewHolder;
import com.leadcom.android.isp.holder.common.NothingMoreViewHolder;
import com.leadcom.android.isp.listener.OnTitleButtonClickListener;
import com.leadcom.android.isp.listener.OnViewHolderElementClickListener;
import com.leadcom.android.isp.model.Model;
import com.leadcom.android.isp.model.archive.Archive;
import com.leadcom.android.isp.view.SwipeItemLayout;

import java.util.List;

/**
 * <b>功能描述：</b>草稿档案列表<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/10/23 16:49 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/10/23 16:49 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class ArchiveDraftFragment extends BaseSwipeRefreshSupportFragment {

    public static ArchiveDraftFragment newInstance(String params) {
        ArchiveDraftFragment adf = new ArchiveDraftFragment();
        Bundle bundle = new Bundle();
        // 传过来的组织id
        bundle.putString(PARAM_QUERY_ID, params);
        adf.setArguments(bundle);
        return adf;
    }

    public static void open(BaseFragment fragment) {
        fragment.openActivity(ArchiveDraftFragment.class.getName(), "", REQUEST_DRAFT, true, false);
    }

    private static int selected = -1;
    private DraftAdapter mAdapter;
    private Model noMore;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        selected = -1;
        enableSwipe(false);
        isLoadingComplete(true);
        noMore = Model.getNoMore("");
    }

    @Override
    protected void onDelayRefreshComplete(@DelayType int type) {

    }

    @Override
    public void doingInResume() {
        initializeAdapter();
    }

    private void resultSelected() {
        if (selected >= 0) {
            Archive draft = (Archive) mAdapter.get(selected);
            ArchiveEditorFragment.open(this, draft.getId(), draft.isAttachmentArchive() ? ArchiveEditorFragment.ATTACHABLE : ArchiveEditorFragment.MULTIMEDIA);
        }
        finish();
//        String json = "";
//        boolean selected = false;
//        for (int i = 0, len = mAdapter.getItemCount(); i < len; i++) {
//            Model model = mAdapter.get(i);
//            if (model.isSelected() && model instanceof Archive) {
//                Archive draft = (Archive) model;
//                json = Archive.toJson(draft);
//                selected = true;
//                break;
//            }
//        }
//        if (selected) {
//            ArchiveEditorFragment.open(this, json, );
//            resultData(json);
//        }
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

    @Override
    protected String getLocalPageTag() {
        return null;
    }

    private void initializeAdapter() {
        if (null == mAdapter) {
            setCustomTitle(R.string.ui_base_text_draft);
            mAdapter = new DraftAdapter();
            mRecyclerView.addOnItemTouchListener(new SwipeItemLayout.OnSwipeItemTouchListener(Activity()));
            mRecyclerView.setAdapter(mAdapter);
            fetchingDraft();
        }
    }

    private void resetRightEvent() {
        setRightText(selected >= 0 ? R.string.ui_text_archive_creator_editor_create_draft_right_button_text : 0);
        setRightTitleClickListener(selected >= 0 ? new OnTitleButtonClickListener() {
            @Override
            public void onClick() {
                resultSelected();
            }
        } : null);
    }

    private void fetchingDraft() {
        mAdapter.remove(noMore);
        ArchiveRequest.request().setOnMultipleRequestListener(new OnMultipleRequestListener<Archive>() {
            @Override
            public void onResponse(List<Archive> list, boolean success, int totalPages, int pageSize, int total, int pageNumber) {
                super.onResponse(list, success, totalPages, pageSize, total, pageNumber);
                if (success && null != list && list.size() > 0) {
                    for (Archive archive : list) {
                        mAdapter.add(archive);
                    }
                }
                mAdapter.add(noMore);
            }
        }).listDraft(remotePageNumber);
    }

    private OnViewHolderElementClickListener elementClickListener = new OnViewHolderElementClickListener() {
        @Override
        public void onClick(View view, int index) {
            switch (view.getId()) {
                case R.id.ui_tool_view_archive_draft_layout:
                    finish();
                    Archive draft = (Archive) mAdapter.get(index);
                    ArchiveDetailsWebViewFragment.open(ArchiveDraftFragment.this, draft.getId(), Archive.Type.GROUP);
                    break;
                case R.id.ui_tool_view_archive_draft_selector:
                    Model selected = mAdapter.get(index);
                    selected.setSelected(!selected.isSelected());
                    mAdapter.update(selected);
                    if (selected.isSelected()) {
                        ArchiveDraftFragment.selected = index;
                    } else {
                        ArchiveDraftFragment.selected = -1;
                    }
                    for (int i = 0, len = mAdapter.getItemCount(); i < len; i++) {
                        Model model = mAdapter.get(i);
                        if (!model.getId().equals(selected.getId()) && model.isSelected()) {
                            model.setSelected(false);
                            mAdapter.update(model);
                        }
                    }
                    resetRightEvent();
                    break;
                case R.id.ui_tool_view_archive_draft_delete:
                    warningDraftDelete(index);
                    break;
            }
        }
    };

    private void warningDraftDelete(final int index) {
        DeleteDialogHelper.helper().init(this).setTitleText(R.string.ui_text_archive_creator_editor_create_draft_delete).setOnDialogConfirmListener(new DialogHelper.OnDialogConfirmListener() {
            @Override
            public boolean onConfirm() {
                deleteDraft(index);
                return true;
            }
        }).show();
    }

    private void deleteDraft(final int index) {
        ArchiveRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<Archive>() {
            @Override
            public void onResponse(Archive archive, boolean success, String message) {
                super.onResponse(archive, success, message);
                if (success) {
                    mAdapter.remove(index);
                }
            }
        }).deleteDraft(mAdapter.get(index).getId());
    }

    private class DraftAdapter extends RecyclerViewAdapter<BaseViewHolder, Model> {

        private static final int VT_DRAFT = 0, VT_LAST = 1;

        @Override
        public BaseViewHolder onCreateViewHolder(View itemView, int viewType) {
            if (viewType == VT_DRAFT) {
                ArchiveDraftViewHolder holder = new ArchiveDraftViewHolder(itemView, ArchiveDraftFragment.this);
                holder.setOnViewHolderElementClickListener(elementClickListener);
                return holder;
            } else {
                return new NothingMoreViewHolder(itemView, ArchiveDraftFragment.this);
            }
        }

        @Override
        public int itemLayout(int viewType) {
            if (viewType == VT_DRAFT) {
                return R.layout.holder_view_archive_draft;
            }
            return R.layout.holder_view_nothing_more;
        }

        @Override
        public int getItemViewType(int position) {
            if (get(position) instanceof Archive) {
                return VT_DRAFT;
            }
            return VT_LAST;
        }

        @Override
        public void onBindHolderOfView(BaseViewHolder holder, int position, @Nullable Model item) {
            if (holder instanceof ArchiveDraftViewHolder) {
                ((ArchiveDraftViewHolder) holder).showContent((Archive) item);
            } else if (holder instanceof NothingMoreViewHolder) {
                ((NothingMoreViewHolder) holder).showContent(item);
            }
        }

        @Override
        protected int comparator(Model item1, Model item2) {
            return 0;
        }
    }
}
