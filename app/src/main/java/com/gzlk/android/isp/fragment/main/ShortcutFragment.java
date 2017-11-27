package com.gzlk.android.isp.fragment.main;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.gzlk.android.isp.R;
import com.gzlk.android.isp.adapter.RecyclerViewAdapter;
import com.gzlk.android.isp.api.org.OrgRequest;
import com.gzlk.android.isp.application.App;
import com.gzlk.android.isp.fragment.activity.ActivityCreatorFragment;
import com.gzlk.android.isp.fragment.archive.ArchiveCreateSelectorFragment;
import com.gzlk.android.isp.fragment.archive.ArchiveEditorFragment;
import com.gzlk.android.isp.fragment.base.BaseFragment;
import com.gzlk.android.isp.fragment.individual.MomentCreatorFragment;
import com.gzlk.android.isp.fragment.organization.BaseOrganizationFragment;
import com.gzlk.android.isp.holder.home.ShortcutGroupViewHolder;
import com.gzlk.android.isp.listener.OnViewHolderElementClickListener;
import com.gzlk.android.isp.model.organization.Organization;
import com.hlk.hlklib.layoutmanager.CustomLinearLayoutManager;
import com.hlk.hlklib.lib.inject.Click;
import com.hlk.hlklib.lib.inject.ViewId;

import java.util.ArrayList;
import java.util.List;

/**
 * <b>功能描述：</b>主页快捷方式页面<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/11/24 15:14 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/11/24 15:14 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class ShortcutFragment extends BaseOrganizationFragment {

    private static final int TYPE_ACTIVITY = 1, TYPE_ARCHIVE = 2;

    public static void open(BaseFragment fragment) {
        fragment.openActivity(ShortcutFragment.class.getName(), "", true, false);
    }

    @ViewId(R.id.ui_shortcut_to_group_activity)
    private RecyclerView groupActivity;
    @ViewId(R.id.ui_shortcut_to_group_activity_nothing)
    private TextView groupActivityNothing;
    @ViewId(R.id.ui_shortcut_to_group_archive)
    private RecyclerView groupArchive;
    @ViewId(R.id.ui_shortcut_to_group_archive_nothing)
    private TextView groupArchiveNothing;

    private GroupAdapter actAdapter, arcAdapter;
    private List<Organization> groups;

    @Override
    public int getLayout() {
        return R.layout.fragment_shortcut;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        CustomLinearLayoutManager cllmActivity = new CustomLinearLayoutManager(groupActivity.getContext());
        cllmActivity.setOrientation(CustomLinearLayoutManager.HORIZONTAL);
        groupActivity.setLayoutManager(cllmActivity);
        CustomLinearLayoutManager cllmArchive = new CustomLinearLayoutManager(groupArchive.getContext());
        cllmArchive.setOrientation(CustomLinearLayoutManager.HORIZONTAL);
        groupArchive.setLayoutManager(cllmArchive);
    }

    @Override
    protected void getParamsFromBundle(Bundle bundle) {

    }

    @Override
    public void doingInResume() {
        setCustomTitle(R.string.ui_main_shortcut_fragment_title);
        if (null == groups) {
            fetchingJoinedRemoteOrganizations(OrgRequest.GROUP_LIST_OPE_JOINED);
        }
    }

    @Override
    protected boolean shouldSetDefaultTitleEvents() {
        return true;
    }

    @Override
    protected void saveParamsToBundle(Bundle bundle) {

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

    @Override
    protected void destroyView() {

    }

    @Click({R.id.ui_archive_creator_selector_rich_text, R.id.ui_archive_creator_selector_attachment,
            R.id.ui_shortcut_to_moment, R.id.ui_shortcut_closer})
    private void elementClick(View view) {
        view.startAnimation(App.clickAnimation());
        switch (view.getId()) {
            case R.id.ui_archive_creator_selector_rich_text:
                ArchiveEditorFragment.open(ShortcutFragment.this, "", ArchiveEditorFragment.MULTIMEDIA);
                break;
            case R.id.ui_archive_creator_selector_attachment:
                ArchiveEditorFragment.open(ShortcutFragment.this, "", ArchiveEditorFragment.ATTACHABLE);
                break;
            case R.id.ui_shortcut_to_moment:
                MomentCreatorFragment.open(ShortcutFragment.this, "[]");
                break;
            case R.id.ui_shortcut_closer:
                finish();
                break;
        }
    }

    @Override
    protected void onDelayRefreshComplete(int type) {

    }

    @Override
    protected void onFetchingJoinedRemoteOrganizationsComplete(List<Organization> list) {
        groups = list;
        if (null == groups) {
            groups = new ArrayList<>();
        }
        groupActivityNothing.setVisibility(groups.size() < 1 ? View.VISIBLE : View.GONE);
        groupArchiveNothing.setVisibility(groups.size() < 1 ? View.VISIBLE : View.GONE);
        if (null == actAdapter) {
            actAdapter = new GroupAdapter(TYPE_ACTIVITY);
            groupActivity.setAdapter(actAdapter);
        }
        if (null == arcAdapter) {
            arcAdapter = new GroupAdapter(TYPE_ARCHIVE);
            groupArchive.setAdapter(arcAdapter);
        }
        for (Organization group : list) {
            actAdapter.update(group);
            arcAdapter.update(group);
        }
    }

    private OnViewHolderElementClickListener elementClickListener = new OnViewHolderElementClickListener() {
        @Override
        public void onClick(View view, int index) {
            int type = (int) view.getTag(R.id.hlklib_ids_custom_view_click_tag);
            if (type == TYPE_ACTIVITY) {
                // 新建活动
                ActivityCreatorFragment.open(ShortcutFragment.this, actAdapter.get(index).getId(), "");
            } else if (type == TYPE_ARCHIVE) {
                // 新建组织档案
                ArchiveCreateSelectorFragment.open(ShortcutFragment.this, arcAdapter.get(index).getId());
            }
        }
    };

    private class GroupAdapter extends RecyclerViewAdapter<ShortcutGroupViewHolder, Organization> {
        private int type;

        GroupAdapter(int type) {
            super();
            this.type = type;
        }

        @Override
        public ShortcutGroupViewHolder onCreateViewHolder(View itemView, int viewType) {
            ShortcutGroupViewHolder holder = new ShortcutGroupViewHolder(itemView, ShortcutFragment.this);
            holder.setOnViewHolderElementClickListener(elementClickListener);
            return holder;
        }

        @Override
        public int itemLayout(int viewType) {
            return R.layout.tool_view_organization_item;
        }

        @Override
        public void onBindHolderOfView(ShortcutGroupViewHolder holder, int position, @Nullable Organization item) {
            holder.showContent(item, type);
        }

        @Override
        protected int comparator(Organization item1, Organization item2) {
            return 0;
        }
    }
}
