package com.gzlk.android.isp.fragment.main;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.google.android.flexbox.FlexboxLayout;
import com.gzlk.android.isp.R;
import com.gzlk.android.isp.api.org.OrgRequest;
import com.gzlk.android.isp.application.App;
import com.gzlk.android.isp.fragment.activity.ActivityCreatorFragment;
import com.gzlk.android.isp.fragment.archive.ArchiveCreateSelectorFragment;
import com.gzlk.android.isp.fragment.archive.ArchiveEditorFragment;
import com.gzlk.android.isp.fragment.base.BaseFragment;
import com.gzlk.android.isp.fragment.individual.MomentCreatorFragment;
import com.gzlk.android.isp.fragment.organization.BaseOrganizationFragment;
import com.gzlk.android.isp.model.organization.Organization;
import com.gzlk.android.isp.view.OrganizationConcerned;
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

    public static void open(BaseFragment fragment) {
        fragment.openActivity(ShortcutFragment.class.getName(), "", false, false, true);
    }

    @ViewId(R.id.ui_shortcut_to_group_activity)
    private FlexboxLayout groupActivity;
    @ViewId(R.id.ui_shortcut_to_group_activity_nothing)
    private TextView groupActivityNothing;
    @ViewId(R.id.ui_shortcut_to_group_archive)
    private FlexboxLayout groupArchive;
    @ViewId(R.id.ui_shortcut_to_group_archive_nothing)
    private TextView groupArchiveNothing;

    private List<Organization> groups;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        OrganizationConcerned.initDirect = false;
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onDestroy() {
        OrganizationConcerned.initDirect = true;
        super.onDestroy();
    }

    @Override
    public int getLayout() {
        return R.layout.fragment_shortcut;
    }

    @Override
    protected void getParamsFromBundle(Bundle bundle) {

    }

    @Override
    public void doingInResume() {
        setCustomTitle("");
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
        showGroups();
    }

    private void showGroups() {
        groupArchive.removeAllViews();
        groupActivity.removeAllViews();
        for (Organization group : groups) {
            OrganizationConcerned concerned = new OrganizationConcerned(Activity(), R.layout.tool_view_organization_item);
            concerned.showOrganization(group);
            concerned.setOnContainerClickListener(clickListener);
            concerned.setTag(R.id.hlklib_ids_custom_view_click_tag, groupArchive);
            groupArchive.addView(concerned);
            OrganizationConcerned concerned1 = new OrganizationConcerned(Activity(), R.layout.tool_view_organization_item);
            concerned1.showOrganization(group);
            concerned1.setOnContainerClickListener(clickListener);
            concerned.setTag(R.id.hlklib_ids_custom_view_click_tag, groupActivity);
            groupActivity.addView(concerned1);
        }
    }

    private OrganizationConcerned.OnContainerClickListener clickListener = new OrganizationConcerned.OnContainerClickListener() {
        @Override
        public void onClick(OrganizationConcerned concerned, String id) {
            concerned.startAnimation(App.clickAnimation());
            View parent = (View) concerned.getTag(R.id.hlklib_ids_custom_view_click_tag);
            if (parent.getId() == R.id.ui_shortcut_to_group_activity) {
                // 新建活动
                ActivityCreatorFragment.open(ShortcutFragment.this, id, "");
            } else {
                // 新建组织档案
                ArchiveCreateSelectorFragment.open(ShortcutFragment.this, id);
            }
        }
    };
}
