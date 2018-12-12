package com.leadcom.android.isp.fragment.common;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.leadcom.android.isp.R;
import com.leadcom.android.isp.adapter.RecyclerViewAdapter;
import com.leadcom.android.isp.api.common.CoverRequest;
import com.leadcom.android.isp.api.listener.OnMultipleRequestListener;
import com.leadcom.android.isp.fragment.base.BaseFragment;
import com.leadcom.android.isp.fragment.base.BaseSwipeRefreshSupportFragment;
import com.leadcom.android.isp.helper.ToastHelper;
import com.leadcom.android.isp.holder.BaseViewHolder;
import com.leadcom.android.isp.holder.common.CoverTemplateViewHolder;
import com.leadcom.android.isp.holder.common.NothingMoreViewHolder;
import com.leadcom.android.isp.listener.OnTitleButtonClickListener;
import com.leadcom.android.isp.listener.OnViewHolderClickListener;
import com.leadcom.android.isp.model.Model;
import com.leadcom.android.isp.model.common.CoverTemplate;

import java.util.Iterator;
import java.util.List;

import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;

/**
 * <b>功能描述：</b>预定义封面选择器<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2018/04/18 15:36 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2018/04/18 15:36 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */
public class CoverTemplateFragment extends BaseSwipeRefreshSupportFragment {

    private static int selectedIndex = -1;

    public static CoverTemplateFragment newInstance(Bundle bundle) {
        CoverTemplateFragment ctf = new CoverTemplateFragment();
        ctf.setArguments(bundle);
        return ctf;
    }

    public static void open(BaseFragment fragment, String selected) {
        Bundle bundle = new Bundle();
        bundle.putString(PARAM_QUERY_ID, selected);
        fragment.openActivity(CoverTemplateFragment.class.getName(), bundle, REQUEST_SELECT, true, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        last = Model.getNoMore();
        setCustomTitle(R.string.ui_activity_cover_picker_fragment_title1);
        setRightText(R.string.ui_base_text_confirm);
        setRightTitleClickListener(new OnTitleButtonClickListener() {
            @Override
            public void onClick() {
                if (selectedIndex >= 0) {
                    CoverTemplate cover = (CoverTemplate) mAdapter.get(selectedIndex);
                    resultData(cover.getUrl());
                } else {
                    ToastHelper.helper().showMsg(R.string.ui_activity_cover_picker_none_image_selected1);
                }
            }
        });
    }

    private Model last;
    private CoverAdapter mAdapter;

    @Override
    protected void onSwipeRefreshing() {

    }

    @Override
    protected void onLoadingMore() {

    }

    @Override
    public int getLayout() {
        return R.layout.tool_view_recycler_view_none_swipe_refreshable;
    }

    @Override
    protected String getLocalPageTag() {
        return null;
    }

    @Override
    protected void onDelayRefreshComplete(int type) {

    }

    @Override
    public void doingInResume() {
        initializeAdapter();
    }

    @Override
    protected boolean shouldSetDefaultTitleEvents() {
        return true;
    }

    @Override
    protected void destroyView() {

    }

    private void fetchingCoverTemplate() {
        mAdapter.remove(last);
        CoverRequest.request().setOnMultipleRequestListener(new OnMultipleRequestListener<CoverTemplate>() {
            @Override
            public void onResponse(List<CoverTemplate> list, boolean success, int totalPages, int pageSize, int total, int pageNumber) {
                super.onResponse(list, success, totalPages, pageSize, total, pageNumber);
                if (success && null != list) {
                    for (CoverTemplate cover : list) {
                        if (cover.getUrl().equals(mQueryId)) {
                            cover.setSelected(true);
                        }
                        mAdapter.update(cover);
                    }
                }
                mAdapter.update(last);
            }
        }).list(remotePageNumber);
    }

    private void initializeAdapter() {
        if (null == mAdapter) {
            mAdapter = new CoverAdapter();
            OverScrollDecoratorHelper.setUpOverScroll(mRecyclerView, OverScrollDecoratorHelper.ORIENTATION_VERTICAL);
            mRecyclerView.setAdapter(mAdapter);
            fetchingCoverTemplate();
        }
    }

    private OnViewHolderClickListener holderClickListener = new OnViewHolderClickListener() {
        @Override
        public void onClick(int index) {
            CoverTemplate cover = (CoverTemplate) mAdapter.get(index);
            cover.setSelected(!cover.isSelected());
            mAdapter.update(cover);
            if (cover.isSelected()) {
                selectedIndex = index;
            } else {
                selectedIndex = -1;
            }
            Iterator<Model> iterator = mAdapter.iterator();
            while (iterator.hasNext()) {
                Model model = iterator.next();
                if (model.isSelected() && !model.getId().equals(cover.getId())) {
                    model.setSelected(false);
                    mAdapter.update(model);
                }
            }
        }
    };

    private class CoverAdapter extends RecyclerViewAdapter<BaseViewHolder, Model> {
        private static final int VT_LAST = 0, VT_COVER = 1;

        @Override
        public BaseViewHolder onCreateViewHolder(View itemView, int viewType) {
            switch (viewType) {
                case VT_COVER:
                    CoverTemplateViewHolder ctvh = new CoverTemplateViewHolder(itemView, CoverTemplateFragment.this);
                    ctvh.addOnViewHolderClickListener(holderClickListener);
                    return ctvh;
                default:
                    return new NothingMoreViewHolder(itemView, CoverTemplateFragment.this);
            }
        }

        @Override
        public int itemLayout(int viewType) {
            return viewType == VT_LAST ? R.layout.holder_view_archive_details_comment_nothing_more : R.layout.holder_view_cover_picker;
        }

        @Override
        public int getItemViewType(int position) {
            return get(position) instanceof CoverTemplate ? VT_COVER : VT_LAST;
        }

        @Override
        public void onBindHolderOfView(BaseViewHolder holder, int position, @Nullable Model item) {
            if (holder instanceof CoverTemplateViewHolder) {
                ((CoverTemplateViewHolder) holder).showContent((CoverTemplate) item);
            }
        }

        @Override
        protected int comparator(Model item1, Model item2) {
            return 0;
        }
    }
}
