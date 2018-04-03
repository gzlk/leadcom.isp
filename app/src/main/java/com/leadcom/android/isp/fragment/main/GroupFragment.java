package com.leadcom.android.isp.fragment.main;

import android.animation.Animator;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.hlk.hlklib.layoutmanager.CustomLinearLayoutManager;
import com.hlk.hlklib.lib.inject.Click;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.view.CustomTextView;
import com.leadcom.android.isp.R;
import com.leadcom.android.isp.adapter.RecyclerViewAdapter;
import com.leadcom.android.isp.api.common.QuantityRequest;
import com.leadcom.android.isp.api.listener.OnSingleRequestListener;
import com.leadcom.android.isp.api.org.OrgRequest;
import com.leadcom.android.isp.application.App;
import com.leadcom.android.isp.cache.Cache;
import com.leadcom.android.isp.fragment.individual.UserIntroductionFragment;
import com.leadcom.android.isp.fragment.organization.ArchivesFragment;
import com.leadcom.android.isp.fragment.organization.BaseOrganizationFragment;
import com.leadcom.android.isp.fragment.organization.ConcernedOrganizationFragment;
import com.leadcom.android.isp.fragment.organization.ContactFragment;
import com.leadcom.android.isp.fragment.organization.CreateOrganizationFragment;
import com.leadcom.android.isp.fragment.organization.SquadsFragment;
import com.leadcom.android.isp.helper.StringHelper;
import com.leadcom.android.isp.holder.BaseViewHolder;
import com.leadcom.android.isp.holder.home.GroupDetailsViewHolder;
import com.leadcom.android.isp.holder.home.GroupHeaderViewHolder;
import com.leadcom.android.isp.holder.organization.GroupInterestViewHolder;
import com.leadcom.android.isp.lib.Json;
import com.leadcom.android.isp.listener.OnViewHolderClickListener;
import com.leadcom.android.isp.listener.OnViewHolderElementClickListener;
import com.leadcom.android.isp.model.Model;
import com.leadcom.android.isp.model.common.Attachment;
import com.leadcom.android.isp.model.common.Quantity;
import com.leadcom.android.isp.model.common.SimpleClickableItem;
import com.leadcom.android.isp.model.operation.GRPOperation;
import com.leadcom.android.isp.model.organization.Concern;
import com.leadcom.android.isp.model.organization.Organization;
import com.leadcom.android.isp.model.organization.Role;

import java.util.ArrayList;
import java.util.List;

import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;

/**
 * <b>功能描述：</b>首页 - 组织<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2018/03/15 09:52 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2018/03/15 09:52 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class GroupFragment extends BaseOrganizationFragment {

    private static boolean isFirst = true;
    @ViewId(R.id.ui_main_tool_bar_container)
    private View toolBar;
    @ViewId(R.id.ui_main_group_title_text)
    private TextView titleTextView;
    @ViewId(R.id.ui_main_group_title_allow)
    private CustomTextView titleAllow;
    @ViewId(R.id.ui_main_group_mine_background)
    private RelativeLayout groupsBkg;
    @ViewId(R.id.ui_main_group_mine_list_bg)
    private LinearLayout groupListBg;
    @ViewId(R.id.ui_main_group_mine_list)
    private RecyclerView groupList;

    private GroupAdapter gAdapter;
    private DetailsAdapter dAdapter;
    private String[] items;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isFirst = true;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //tryPaddingContent(toolBar, false);
        isLoadingComplete(true);

        // 头像选择是需要剪切的
        isChooseImageForCrop = true;
        // 头像是需要压缩的
        isSupportCompress = true;
        // 图片选择后的回调
        addOnImageSelectedListener(albumImageSelectedListener);
        // 文件上传完毕后的回调处理
        setOnFileUploadingListener(mOnFileUploadingListener);
    }

    // 相册选择返回了
    private OnImageSelectedListener albumImageSelectedListener = new OnImageSelectedListener() {
        @Override
        public void onImageSelected(ArrayList<String> selected) {
            // 图片选择完毕之后立即压缩图片并且自动上传
            compressImage();
        }
    };

    private OnFileUploadingListener mOnFileUploadingListener = new OnFileUploadingListener() {
        @Override
        public void onUploading(int all, int current, String file, long size, long uploaded) {

        }

        @Override
        public void onUploadingComplete(ArrayList<Attachment> uploaded) {
            Organization group = (Organization) dAdapter.get(0);
            Role role = Cache.cache().getGroupRole(group.getId());
            final String groupId = group.getId(), url = uploaded.get(0).getUrl();
            if (null != role && role.hasOperation(GRPOperation.GROUP_PROPERTY)) {
                // 重新检查一遍更改权限之后更改组织的logo
                OrgRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<Organization>() {
                    @Override
                    public void onResponse(Organization organization, boolean success, String message) {
                        super.onResponse(organization, success, message);
                        if (success) {
                            Organization org = (Organization) dAdapter.get(0);
                            org.setLogo(url);
                            dAdapter.update(org);
                        }
                    }
                }).update(groupId, "", url, "");
            }
        }
    };

    @Override
    protected void onDelayRefreshComplete(int type) {

    }

    @Override
    public void doingInResume() {
        initializeDetailsAdapter();
        initializeGroupsAdapter();
    }

    @Override
    protected boolean shouldSetDefaultTitleEvents() {
        return false;
    }

    @Override
    protected void destroyView() {

    }

    @Override
    public int getLayout() {
        return R.layout.fragment_main_group;
    }

    @Override
    protected void onSwipeRefreshing() {
        // 拉取我已经加入的组织列表
        fetchingJoinedRemoteOrganizations(OrgRequest.GROUP_LIST_OPE_JOINED);
    }

    @Override
    protected void onLoadingMore() {

    }

    @Override
    protected String getLocalPageTag() {
        return null;
    }

    private String changedId = "";

    @Override
    public void onActivityResult(int requestCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CREATE:
            case REQUEST_CHANGE:
                changedId = getResultedData(data);
                // 组织创建成功，需要重新刷新组织列表
                fetchingJoinedRemoteOrganizations(OrgRequest.GROUP_LIST_OPE_JOINED);
                break;
        }
        super.onActivityResult(requestCode, data);
    }

    @Click({R.id.ui_main_group_title_container, R.id.ui_main_group_mine_background,
            R.id.ui_main_group_create})
    private void viewClick(View view) {
        switch (view.getId()) {
            case R.id.ui_main_group_title_container:
                // 打开组织选择内容
                showGroupSelector(groupsBkg.getVisibility() == View.GONE);
                break;
            case R.id.ui_main_group_mine_background:
                showGroupSelector(false);
                break;
            case R.id.ui_main_group_create:
                view.startAnimation(App.clickAnimation());
                CreateOrganizationFragment.open(GroupFragment.this);
                break;
        }
    }

    private void initializeGroupsPosition() {
        Handler().post(new Runnable() {
            @Override
            public void run() {
                showGroupList(false, duration());
            }
        });
    }

    private void showGroupSelector(boolean shown) {
        titleAllow.animate()
                .rotation(shown ? -90 : 90)
                .setDuration(duration())
                .start();
        showGroupList(shown, duration());
    }

    private void showGroupList(final boolean shown, long duration) {
        groupsBkg.animate()
                .alpha(shown ? 1.0f : 0.0f)
                .setDuration(duration)
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        if (shown) {
                            groupsBkg.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if (!shown) {
                            groupsBkg.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                })
                .start();
        groupListBg.animate()
                .alpha(shown ? 1.0f : 0.0f)
                .translationY(shown ? 0 : -groupListBg.getMeasuredHeight() * 1.1f)
                .setDuration(duration)
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        if (shown) {
                            groupListBg.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if (!shown) {
                            groupListBg.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                })
                .start();
    }

    @Override
    protected void onFetchingJoinedRemoteOrganizationsComplete(List<Organization> list) {
        if (null != list) {
            for (Organization group : list) {
                group.setSelectable(true);
                gAdapter.update(group);
                Cache.cache().updateGroup(group);
            }
            if (isFirst) {
                isFirst = false;
                initializeGroupsPosition();
                // 初始化第一个组织
                if (gAdapter.getItemCount() > 0) {
                    if (isEmpty(changedId)) {
                        onGroupChange(gAdapter.get(0));
                    }
                }
            } else if (!isEmpty(changedId)) {
                onGroupChange(gAdapter.get(changedId));
            }
        }
        displayNothing(gAdapter.getItemCount() <= 0);
        if (gAdapter.getItemCount() <= 0) {
            titleTextView.setText(null);
        }
    }

    private void initializeGroupsAdapter() {
        if (null == gAdapter) {
            setNothingText(R.string.ui_organization_structure_no_group_exist);
            gAdapter = new GroupAdapter();
            groupList.setLayoutManager(new CustomLinearLayoutManager(groupList.getContext()));
            groupList.setAdapter(gAdapter);
            Handler().post(new Runnable() {
                @Override
                public void run() {
                    fetchingJoinedRemoteOrganizations(OrgRequest.GROUP_LIST_OPE_JOINED);
                }
            });
        }
    }

    private OnViewHolderClickListener onViewHolderClickListener = new OnViewHolderClickListener() {
        @Override
        public void onClick(int index) {
            onGroupChange(gAdapter.get(index));
            showGroupSelector(false);
        }
    };

    private void onGroupChange(Organization group) {
        titleTextView.setText(group.getName());
        for (int i = 0, len = gAdapter.getItemCount(); i < len; i++) {
            Organization org = gAdapter.get(i);
            if (org.isSelected()) {
                if (!org.getId().equals(group.getId())) {
                    org.setSelected(false);
                    gAdapter.update(org);
                }
            } else if (org.getId().equals(group.getId())) {
                org.setSelected(true);
                gAdapter.update(org);
            }
        }
        if (isEmpty(dAdapter.get(0).getId()) || !isEmpty(changedId) || !dAdapter.get(0).getId().equals(group.getId())) {
            changedId = "";
            dAdapter.replace(group, 0);
            fetchingQuantity(group.getId());
        }
    }

    private void fetchingQuantity(String groupId) {
        QuantityRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<Quantity>() {
            @Override
            public void onResponse(Quantity quantity, boolean success, String message) {
                super.onResponse(quantity, success, message);
                if (success && null != quantity) {
                    for (int i = 1, len = dAdapter.getItemCount(); i < len; i++) {
                        SimpleClickableItem item = (SimpleClickableItem) dAdapter.get(i);
                        int index = item.getIndex();
                        switch (index) {
                            case 1:
                                item.setSource(format(items[index - 1], quantity.getMemberNum()));
                                break;
                            case 2:
                                item.setSource(format(items[index - 1], quantity.getSquadNum()));
                                break;
                            case 3:
                                item.setSource(format(items[index - 1], quantity.getDocNum()));
                                break;
                            case 4:
                                item.setSource(format(items[index - 1], quantity.getConGroupNum()));
                                break;
                        }
                        item.reset();
                        dAdapter.update(item);
                    }
                }
            }
        }).findGroup(groupId);
    }

    private class GroupAdapter extends RecyclerViewAdapter<GroupInterestViewHolder, Organization> {

        @Override
        public GroupInterestViewHolder onCreateViewHolder(View itemView, int viewType) {
            GroupInterestViewHolder holder = new GroupInterestViewHolder(itemView, GroupFragment.this);
            holder.addOnViewHolderClickListener(onViewHolderClickListener);
            return holder;
        }

        @Override
        public int itemLayout(int viewType) {
            return R.layout.holder_view_group_interesting_item;
        }

        @Override
        public void onBindHolderOfView(GroupInterestViewHolder holder, int position, @Nullable Organization item) {
            holder.showContent(item);
        }

        @Override
        protected int comparator(Organization item1, Organization item2) {
            return 0;
        }

        Organization get(String groupId) {
            for (int i = 0, len = getItemCount(); i < len; i++) {
                Organization group = get(i);
                if (group.getId().equals(groupId)) {
                    return group;
                }
            }
            return null;
        }
    }

    private void initializeDetailsAdapter() {
        if (null == items || items.length < 1) {
            items = StringHelper.getStringArray(R.array.ui_group_details_items);
        }
        if (null == dAdapter) {
            dAdapter = new DetailsAdapter();
            mRecyclerView.setAdapter(dAdapter);
            // ios style
            OverScrollDecoratorHelper.setUpOverScroll(mRecyclerView, OverScrollDecoratorHelper.ORIENTATION_VERTICAL);

            dAdapter.add(new Organization());
            for (String string : items) {
                SimpleClickableItem item = new SimpleClickableItem(format(string, 0));
                dAdapter.add(item);
            }
        }
    }

    private OnViewHolderElementClickListener detailsElementClickListener = new OnViewHolderElementClickListener() {

        @Override
        public void onClick(View view, int index) {
            switch (view.getId()) {
                case R.id.ui_holder_view_simple_clickable:
                    handleItemClick(index);
                    break;
                case R.id.ui_holder_view_group_header_container:
                case R.id.ui_holder_view_group_header_logo:
                    // 打开组织编辑
                    Organization group = (Organization) dAdapter.get(0);
                    Role role = Cache.cache().getGroupRole(group.getId());
                    if (null != role && role.hasOperation(GRPOperation.GROUP_PROPERTY)) {
                        if (view.getId() == R.id.ui_holder_view_group_header_logo) {
                            // 选择图片上传更改组织的logo
                            openImageSelector(true);
                        } else {
                            // 登录者有组织属性编辑权限时，打开组织属性编辑页面
                            CreateOrganizationFragment.open(GroupFragment.this, (Organization) dAdapter.get(0));
                        }
                    } else {
                        // 查看组织简介
                        UserIntroductionFragment.open(GroupFragment.this, group.getId(), group.getName(), group.getLogo(), group.getCreateDate(), group.getIntro());
                    }
                    break;
            }
        }
    };

    private void handleItemClick(int index) {
        Organization group = (Organization) dAdapter.get(0);
        switch (index) {
            case 1:
                // 组织成员
                ContactFragment.open(this, group.getId());
                break;
            case 2:
                // 下属小组
                SquadsFragment.open(this, group.getId());
                break;
            case 3:
                // 组织档案
                ArchivesFragment.open(this, group.getId(), getString(R.string.ui_group_archive_fragment_title));
                break;
            case 4:
                // 上下级
                ArrayList<Concern> concerns = group.getConGroup();
                for (Concern concern : concerns) {
                    concern.setConcernType(getConcerned(concern.getConGroup(), group.getId()));
                }
                String json = Json.gson().toJson(concerns, new TypeToken<ArrayList<Concern>>() {
                }.getType());
                ConcernedOrganizationFragment.open(this, group.getId(), StringHelper.replaceJson(json, false), REQUEST_CONCERNED);
                break;
        }
    }

    /**
     * 检查组织是否互相关注
     */
    private int getConcerned(ArrayList<Concern> list, String hostGroupId) {
        if (null == list || list.size() <= 0) return Concern.ConcernType.CONCERNED;
        for (Concern concern : list) {
            if (concern.getId().equals(hostGroupId)) {
                return Concern.ConcernType.EACH;
            }
        }
        // 对方的已关注列表里没有本组织，则说明是本组织已关注
        return Concern.ConcernType.CONCERNED;
    }

    /**
     * 组织详细内容
     */
    private class DetailsAdapter extends RecyclerViewAdapter<BaseViewHolder, Model> {

        private static final int VT_HEAD = 0, VT_DETAILS = 1;

        @Override
        public BaseViewHolder onCreateViewHolder(View itemView, int viewType) {
            if (viewType == VT_HEAD) {
                GroupHeaderViewHolder ghv = new GroupHeaderViewHolder(itemView, GroupFragment.this);
                ghv.setOnViewHolderElementClickListener(detailsElementClickListener);
                return ghv;
            }
            GroupDetailsViewHolder gdv = new GroupDetailsViewHolder(itemView, GroupFragment.this);
            gdv.setOnViewHolderElementClickListener(detailsElementClickListener);
            return gdv;
        }

        @Override
        public int itemLayout(int viewType) {
            return viewType == VT_HEAD ? R.layout.holder_view_group_header : R.layout.holder_view_group_details;
        }

        @Override
        public int getItemViewType(int position) {
            Model model = get(position);
            return model instanceof Organization ? VT_HEAD : VT_DETAILS;
        }

        @Override
        public void onBindHolderOfView(BaseViewHolder holder, int position, @Nullable Model item) {
            if (holder instanceof GroupHeaderViewHolder) {
                ((GroupHeaderViewHolder) holder).showContent((Organization) item);
            } else if (holder instanceof GroupDetailsViewHolder) {
                ((GroupDetailsViewHolder) holder).showContent((SimpleClickableItem) item);
            }
        }

        @Override
        protected int comparator(Model item1, Model item2) {
            return 0;
        }
    }
}