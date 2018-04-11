package com.leadcom.android.isp.fragment.main;

import android.animation.Animator;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hlk.hlklib.layoutmanager.CustomLinearLayoutManager;
import com.hlk.hlklib.lib.inject.Click;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.view.CustomTextView;
import com.leadcom.android.isp.R;
import com.leadcom.android.isp.activity.BaseActivity;
import com.leadcom.android.isp.adapter.RecyclerViewAdapter;
import com.leadcom.android.isp.api.common.QuantityRequest;
import com.leadcom.android.isp.api.listener.OnSingleRequestListener;
import com.leadcom.android.isp.api.org.OrgRequest;
import com.leadcom.android.isp.application.App;
import com.leadcom.android.isp.cache.Cache;
import com.leadcom.android.isp.fragment.base.BaseFragment;
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
import com.leadcom.android.isp.listener.OnNimMessageEvent;
import com.leadcom.android.isp.listener.OnViewHolderClickListener;
import com.leadcom.android.isp.listener.OnViewHolderElementClickListener;
import com.leadcom.android.isp.model.Model;
import com.leadcom.android.isp.model.common.Attachment;
import com.leadcom.android.isp.model.common.Quantity;
import com.leadcom.android.isp.model.common.SimpleClickableItem;
import com.leadcom.android.isp.model.operation.GRPOperation;
import com.leadcom.android.isp.model.organization.Organization;
import com.leadcom.android.isp.model.organization.Role;
import com.leadcom.android.isp.nim.model.notification.NimMessage;

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

    private static final String PARAM_SINGLE = "gf_single";
    private static boolean isFirst = true;

    public static GroupFragment newInstance(Bundle bundle) {
        GroupFragment gf = new GroupFragment();
        gf.setArguments(bundle);
        return gf;
    }

    private static Bundle getBundle(String groupId) {
        Bundle bundle = new Bundle();
        bundle.putString(PARAM_QUERY_ID, groupId);
        bundle.putBoolean(PARAM_SINGLE, true);
        return bundle;
    }

    public static void open(BaseFragment fragment, String groupId) {
        fragment.openActivity(GroupFragment.class.getName(), getBundle(groupId), false, false);
    }

    public static void open(Context context, String groupId) {
        BaseActivity.openActivity(context, GroupFragment.class.getName(), getBundle(groupId), false, false);
    }

    @Override
    protected void getParamsFromBundle(Bundle bundle) {
        super.getParamsFromBundle(bundle);
        isSingle = bundle.getBoolean(PARAM_SINGLE, false);
    }

    @Override
    protected void saveParamsToBundle(Bundle bundle) {
        super.saveParamsToBundle(bundle);
        bundle.putBoolean(PARAM_SINGLE, isSingle);
    }

    @ViewId(R.id.ui_main_tool_bar_container)
    private View toolBar;
    @ViewId(R.id.ui_ui_custom_title_left_container)
    private View leftContainer;
    @ViewId(R.id.ui_main_group_title_text)
    private TextView titleTextView;
    @ViewId(R.id.ui_main_group_title_allow)
    private CustomTextView titleAllow;
    @ViewId(R.id.ui_main_group_create)
    private View createView;
    @ViewId(R.id.ui_main_group_mine_background)
    private RelativeLayout groupsBkg;
    @ViewId(R.id.ui_main_group_mine_list_bg)
    private LinearLayout groupListBg;
    @ViewId(R.id.ui_main_group_mine_list)
    private RecyclerView groupList;

    private GroupAdapter gAdapter;
    private DetailsAdapter dAdapter;
    private String[] items;
    private boolean isSingle = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isFirst = true;
        App.addNimMessageEvent(nimMessageEvent);
    }

    @Override
    public void onDestroy() {
        App.removeNimMessageEvent(nimMessageEvent);
        super.onDestroy();
    }

    private OnNimMessageEvent nimMessageEvent = new OnNimMessageEvent() {
        @Override
        public void onMessageEvent(NimMessage message) {
            // 如果是组织相关的推送，则重新拉取组织列表
            if (null != message && message.isGroupMsg()) {
                fetchingJoinedRemoteOrganizations(OrgRequest.GROUP_LIST_OPE_JOINED);
            }
        }
    };

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //tryPaddingContent(toolBar, false);
        isLoadingComplete(true);
        // 单个组织查询时，需要显示左侧的返回
        leftContainer.setVisibility(isSingle ? View.VISIBLE : View.GONE);
        createView.setVisibility(isSingle ? View.GONE : View.VISIBLE);
        titleAllow.setVisibility(isSingle ? View.GONE : View.VISIBLE);
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
            final String groupId = group.getId(), url = uploaded.get(0).getUrl();
            if (hasOperation(groupId, GRPOperation.GROUP_PROPERTY)) {
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

    private boolean hasOperation(String groupId, String operation) {
        Role role = Cache.cache().getGroupRole(groupId);
        return null != role && role.hasOperation(operation);
    }

    @Override
    protected void onDelayRefreshComplete(int type) {

    }

    @Override
    public void doingInResume() {
        initializeDetailsAdapter();
        if (!isSingle) {
            initializeGroupsAdapter();
        }
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

    @Override
    public void onActivityResult(int requestCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CREATE:
            case REQUEST_CHANGE:
                mQueryId = getResultedData(data);
                // 组织创建成功，需要重新刷新组织列表
                onSwipeRefreshing();
                break;
            case REQUEST_CONCERNED:
                onSwipeRefreshing();
                break;
            case REQUEST_EDIT:
                // 可以编辑组织简介
                Organization group = (Organization) dAdapter.get(0);
                if (hasOperation(group.getId(), GRPOperation.GROUP_PROPERTY)) {
                    // 登录者有组织属性编辑权限时，打开组织属性编辑页面
                    CreateOrganizationFragment.open(GroupFragment.this, (Organization) dAdapter.get(0));
                }
                break;
        }
        super.onActivityResult(requestCode, data);
    }

    @Click({R.id.ui_main_group_title_container, R.id.ui_main_group_mine_background,
            R.id.ui_main_group_create, R.id.ui_ui_custom_title_left_container})
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
            case R.id.ui_ui_custom_title_left_container:
                finish();
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
                Cache.cache().updateGroup(group);
            }
            gAdapter.update(list, true);
            if (isFirst) {
                isFirst = false;
                initializeGroupsPosition();
                // 初始化第一个组织
                if (gAdapter.getItemCount() > 0) {
                    if (isEmpty(mQueryId)) {
                        onGroupChange(gAdapter.get(0));
                    }
                }
            } else if (!isEmpty(mQueryId)) {
                onGroupChange(gAdapter.get(mQueryId));
            }
        }
        displayNothing(gAdapter.getItemCount() <= 0);
        if (gAdapter.getItemCount() <= 0) {
            titleTextView.setText(null);
        }
        // 重新拉取我的权限列表
        App.app().fetchPermissions();
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
                    onSwipeRefreshing();
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
        if (null != gAdapter) {
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
        }
        if (isEmpty(dAdapter.get(0).getId()) || !isEmpty(mQueryId) || !dAdapter.get(0).getId().equals(group.getId())) {
            mQueryId = "";
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
            if (isSingle) {
                fetchingRemoteOrganization(mQueryId);
            }
        }
    }

    @Override
    protected void onFetchingRemoteOrganizationComplete(Organization organization) {
        if (null != organization) {
            onGroupChange(organization);
        }
    }

    private OnViewHolderElementClickListener detailsElementClickListener = new OnViewHolderElementClickListener() {

        @Override
        public void onClick(View view, int index) {
            switch (view.getId()) {
                case R.id.ui_holder_view_simple_clickable:
                    handleItemClick(index);
                    break;
                case R.id.ui_holder_view_group_header_edit_icon:
                case R.id.ui_holder_view_group_header_container:
                case R.id.ui_holder_view_group_header_logo:
                    // 打开组织编辑
                    Organization group = (Organization) dAdapter.get(0);
                    if (hasOperation(group.getId(), GRPOperation.GROUP_PROPERTY)) {
                        if (view.getId() == R.id.ui_holder_view_group_header_logo) {
                            // 选择图片上传更改组织的logo
                            openImageSelector(true);
                        } else if (view.getId() == R.id.ui_holder_view_group_header_edit_icon) {
                            // 登录者有组织属性编辑权限时，打开组织属性编辑页面
                            CreateOrganizationFragment.open(GroupFragment.this, (Organization) dAdapter.get(0));
                        } else {
                            // 查看组织简介
                            UserIntroductionFragment.open(GroupFragment.this, group);
                        }
                    } else {
                        // 查看组织简介
                        UserIntroductionFragment.open(GroupFragment.this, group);
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
//                ArrayList<Concern> concerns = group.getConGroup();
//                if (null != concerns) {
//                    for (Concern concern : concerns) {
//                        concern.setConcernType(getConcerned(concern.getConGroup(), group.getId()));
//                    }
//                }
//                String json = Json.gson().toJson(concerns, new TypeToken<ArrayList<Concern>>() {
//                }.getType());
                //if (hasOperation(group.getId(), GRPOperation.GROUP_ASSOCIATION)) {
                // 每个人都可以打开查看关注的组织列表？
                ConcernedOrganizationFragment.open(this, group.getId());
                //}
                break;
        }
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
