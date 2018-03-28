package com.leadcom.android.isp.fragment.archive;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.leadcom.android.isp.R;
import com.leadcom.android.isp.adapter.RecyclerViewAdapter;
import com.leadcom.android.isp.fragment.base.BaseFragment;
import com.leadcom.android.isp.fragment.base.BaseSwipeRefreshSupportFragment;
import com.leadcom.android.isp.helper.DeleteDialogHelper;
import com.leadcom.android.isp.helper.DialogHelper;
import com.leadcom.android.isp.helper.StringHelper;
import com.leadcom.android.isp.holder.BaseViewHolder;
import com.leadcom.android.isp.holder.archive.ArchiveDraftViewHolder;
import com.leadcom.android.isp.holder.common.NothingMoreViewHolder;
import com.leadcom.android.isp.listener.OnTitleButtonClickListener;
import com.leadcom.android.isp.listener.OnViewHolderElementClickListener;
import com.leadcom.android.isp.model.Model;
import com.leadcom.android.isp.model.archive.ArchiveDraft;
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

    private DraftAdapter mAdapter;
    private Model noMore;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        enableSwipe(false);
        isLoadingComplete(true);
        noMore = Model.getNoMore("");
    }

    @Override
    protected void onDelayRefreshComplete(@DelayType int type) {

    }

    @Override
    public void doingInResume() {
        setCustomTitle(R.string.ui_base_text_draft);
        setRightText(R.string.ui_base_text_confirm);
        setRightTitleClickListener(new OnTitleButtonClickListener() {
            @Override
            public void onClick() {
                resultSelected();
            }
        });
        initializeAdapter();
    }

    private void resultSelected() {
        String json = "";
        boolean selected = false;
        for (int i = 0, len = mAdapter.getItemCount(); i < len; i++) {
            Model model = mAdapter.get(i);
            if (model.isSelected() && model instanceof ArchiveDraft) {
                ArchiveDraft draft = (ArchiveDraft) model;
                json = draft.getArchiveJson();
                selected = true;
                break;
            }
        }
        if (selected) {
            resultData(StringHelper.replaceJson(json, false));
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
            mAdapter = new DraftAdapter();
            mRecyclerView.addOnItemTouchListener(new SwipeItemLayout.OnSwipeItemTouchListener(Activity()));
            mRecyclerView.setAdapter(mAdapter);
            List<ArchiveDraft> drafts = ArchiveDraft.getDraft("");
            if (null != drafts) {
                for (ArchiveDraft draft : drafts) {
                    mAdapter.add(draft);
                }
            }
            mAdapter.add(noMore);
        }
    }

    private OnViewHolderElementClickListener elementClickListener = new OnViewHolderElementClickListener() {
        @Override
        public void onClick(View view, int index) {
            switch (view.getId()) {
                case R.id.ui_tool_view_archive_draft_layout:
                    Model selected = mAdapter.get(index);
                    selected.setSelected(!selected.isSelected());
                    mAdapter.update(selected);
                    for (int i = 0, len = mAdapter.getItemCount(); i < len; i++) {
                        Model model = mAdapter.get(i);
                        if (!model.getId().equals(selected.getId()) && model.isSelected()) {
                            model.setSelected(false);
                            mAdapter.update(model);
                        }
                    }
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
                ArchiveDraft.delete(mAdapter.get(index).getId());
                mAdapter.remove(index);
                mAdapter.notifyItemRemoved(index);
                return true;
            }
        }).show();
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
            if (get(position) instanceof ArchiveDraft) {
                return VT_DRAFT;
            }
            return VT_LAST;
        }

        @Override
        public void onBindHolderOfView(BaseViewHolder holder, int position, @Nullable Model item) {
            if (holder instanceof ArchiveDraftViewHolder) {
                ((ArchiveDraftViewHolder) holder).showContent((ArchiveDraft) item);
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
