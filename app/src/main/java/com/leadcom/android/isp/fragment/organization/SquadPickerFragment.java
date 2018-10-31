package com.leadcom.android.isp.fragment.organization;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.leadcom.android.isp.R;
import com.leadcom.android.isp.adapter.RecyclerViewAdapter;
import com.leadcom.android.isp.fragment.base.BaseFragment;
import com.leadcom.android.isp.holder.organization.GroupInterestViewHolder;
import com.leadcom.android.isp.listener.OnTitleButtonClickListener;
import com.leadcom.android.isp.listener.OnViewHolderClickListener;
import com.leadcom.android.isp.model.organization.Squad;

import java.util.Iterator;
import java.util.List;

/**
 * <b>功能描述：</b>小组拾取器<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2018/05/23 13:45 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2018/05/23 13:45 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */
public class SquadPickerFragment extends GroupBaseFragment {

    public static SquadPickerFragment newInstance(Bundle bundle) {
        SquadPickerFragment spf = new SquadPickerFragment();
        spf.setArguments(bundle);
        return spf;
    }

    public static void open(BaseFragment fragment, String groupId, String selectedSquadId) {
        Bundle bundle = new Bundle();
        bundle.putString(PARAM_QUERY_ID, groupId);
        bundle.putString(PARAM_SQUAD_ID, selectedSquadId);
        fragment.openActivity(SquadPickerFragment.class.getName(), bundle, REQUEST_SQUAD, true, false);
    }

    private static int selectedIndex = -1;
    private SquadAdapter mAdapter;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        enableSwipe(false);
        isLoadingComplete(true);
    }

    @Override
    protected void onSwipeRefreshing() {

    }

    @Override
    protected void onLoadingMore() {

    }

    @Override
    public void doingInResume() {
        initializeAdapter();
    }

    @Override
    protected boolean shouldSetDefaultTitleEvents() {
        return true;
    }

    private void initializeAdapter() {
        if (null == mAdapter) {
            setCustomTitle("选择支部");
            setRightText(R.string.ui_base_text_confirm);
            setRightTitleClickListener(new OnTitleButtonClickListener() {
                @Override
                public void onClick() {
                    if (selectedIndex >= 0) {
                        resultData(Squad.toJson(mAdapter.get(selectedIndex)));
                    }
                }
            });
            setLoadingText(R.string.ui_organization_squad_contact_loading_squads);
            setNothingText(R.string.ui_group_squads_loading_nothing);
            displayLoading(true);
            mAdapter = new SquadAdapter();
            mRecyclerView.setAdapter(mAdapter);
            fetchingRemoteSquads(mOrganizationId);
        }
    }

    @Override
    protected void onFetchingRemoteSquadsComplete(List<Squad> list) {
        if (null != list) {
            mAdapter.update(list, true);
        }
        displayLoading(false);
        displayNothing(mAdapter.getItemCount() < 1);
    }

    private OnViewHolderClickListener onViewHolderClickListener = new OnViewHolderClickListener() {
        @Override
        public void onClick(int index) {
            Squad selected = mAdapter.get(index);
            selected.setSelected(!selected.isSelected());
            mAdapter.update(selected);
            if (selected.isSelected()) {
                selectedIndex = index;
            } else {
                selectedIndex = -1;
            }
            Iterator<Squad> iterator = mAdapter.iterator();
            while (iterator.hasNext()) {
                Squad squad = iterator.next();
                if (!squad.getId().equals(selected.getId())) {
                    if (squad.isSelected()) {
                        squad.setSelected(false);
                        mAdapter.update(squad);
                    }
                }
            }
        }
    };

    private class SquadAdapter extends RecyclerViewAdapter<GroupInterestViewHolder, Squad> {

        @Override
        public GroupInterestViewHolder onCreateViewHolder(View itemView, int viewType) {
            GroupInterestViewHolder holder = new GroupInterestViewHolder(itemView, SquadPickerFragment.this);
            holder.addOnViewHolderClickListener(onViewHolderClickListener);
            holder.setSelectable(true);
            return holder;
        }

        @Override
        public int itemLayout(int viewType) {
            return R.layout.holder_view_group_interesting_item;
        }

        @Override
        public void onBindHolderOfView(GroupInterestViewHolder holder, int position, @Nullable Squad item) {
            holder.showContent(item);
        }

        @Override
        protected int comparator(Squad item1, Squad item2) {
            return 0;
        }
    }
}
