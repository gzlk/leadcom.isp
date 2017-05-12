package com.gzlk.android.isp.fragment.organization;

import android.support.annotation.Nullable;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.gzlk.android.isp.R;
import com.gzlk.android.isp.adapter.RecyclerViewAdapter;
import com.gzlk.android.isp.fragment.base.BaseFragment;
import com.gzlk.android.isp.fragment.base.BaseSwipeRefreshSupportFragment;
import com.gzlk.android.isp.helper.StringHelper;
import com.gzlk.android.isp.holder.BaseViewHolder;
import com.gzlk.android.isp.holder.UserHeaderBigViewHolder;
import com.gzlk.android.isp.holder.SimpleClickableViewHolder;
import com.gzlk.android.isp.holder.SimpleMemberViewHolder;
import com.gzlk.android.isp.holder.ToggleableViewHolder;
import com.gzlk.android.isp.model.SimpleClickableItem;
import com.hlk.hlklib.lib.inject.Click;
import com.hlk.hlklib.lib.inject.ViewId;

/**
 * <b>功能描述：</b>机构详情页<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/05/08 07:58 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/05/08 07:58 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class OrganizationDetailsFragment extends BaseSwipeRefreshSupportFragment {

    // View
    @ViewId(R.id.ui_transparent_title_container)
    private LinearLayout titleContainer;
    @ViewId(R.id.ui_ui_custom_title_text)
    private TextView titleTextView;

    private DetailsAdapter mAdapter;
    private String[] items;

    @Override
    protected void onDelayRefreshComplete(@DelayType int type) {

    }

    @Override
    public void doingInResume() {
        enableSwipe(false);
        setSupportLoadingMore(false);
        tryPaddingContent(titleContainer, false);
        titleTextView.setText(null);
        initializeAdapter();
    }

    @Override
    public int getLayout() {
        return R.layout.fragment_transparent_title_supported;
    }

    @Override
    protected boolean shouldSetDefaultTitleEvents() {
        return false;
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

    @Click({R.id.ui_ui_custom_title_left_container})
    private void elementClick(View view) {
        switch (view.getId()) {
            case R.id.ui_ui_custom_title_left_container:
                finish();
                break;
        }
    }

    private void initializeAdapter() {
        if (null == items) {
            items = StringHelper.getStringArray(R.array.ui_organization_details_items);
        }
        if (null == mAdapter) {
            mAdapter = new DetailsAdapter();
            mRecyclerView.setAdapter(mAdapter);
        }
        updateAdapter();
    }

    private void updateAdapter() {
        int index = 0;
        for (String string : items) {
            String text;
            switch (index) {
                case 1:
                    text = format(string, 12);
                    break;
                case 2:
                    text = format(string, "未知");
                    break;
                default:
                    text = string;
                    break;
            }
            SimpleClickableItem item = new SimpleClickableItem(text);
            mAdapter.update(item);
            index++;
        }
    }

    private class DetailsAdapter extends RecyclerViewAdapter<BaseViewHolder, SimpleClickableItem> {

        private static final int VT_HEADER = 0, VT_MEMBER = 1, VT_TOGGLE = 2, VT_NORMAL = 3;

        @Override
        public BaseViewHolder onCreateViewHolder(View itemView, int viewType) {
            BaseFragment fragment = OrganizationDetailsFragment.this;
            switch (viewType) {
                case VT_HEADER:
                    return new UserHeaderBigViewHolder(itemView, fragment);
                case VT_MEMBER:
                    return new SimpleMemberViewHolder(itemView, fragment);
                case VT_TOGGLE:
                    return new ToggleableViewHolder(itemView, fragment);
                default:
                    return new SimpleClickableViewHolder(itemView, fragment);
            }
        }

        @Override
        public int itemLayout(int viewType) {
            switch (viewType) {
                case VT_HEADER:
                    return R.layout.holder_view_individual_header_big;
                case VT_MEMBER:
                    return R.layout.holder_view_organization_simple_member;
                case VT_TOGGLE:
                    return R.layout.holder_view_toggle;
                default:
                    return R.layout.holder_view_simple_clickable;
            }
        }

        @Override
        public int getItemViewType(int position) {
            switch (position) {
                case 0:
                    return VT_HEADER;
                case 1:
                    return VT_MEMBER;
                case 5:
                case 6:
                    return VT_TOGGLE;
                default:
                    return VT_NORMAL;
            }
        }

        @Override
        public void onBindHolderOfView(BaseViewHolder holder, int position, @Nullable SimpleClickableItem item) {
            if (holder instanceof SimpleClickableViewHolder) {
                ((SimpleClickableViewHolder) holder).showContent(item);
            } else if (holder instanceof ToggleableViewHolder) {
                ((ToggleableViewHolder) holder).showContent(item);
            }
        }

        @Override
        protected int comparator(SimpleClickableItem item1, SimpleClickableItem item2) {
            return 0;
        }
    }
}
