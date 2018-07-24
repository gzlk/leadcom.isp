package com.leadcom.android.isp.fragment.main;

import android.animation.Animator;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.Html;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.hlk.hlklib.layoutmanager.CustomLinearLayoutManager;
import com.hlk.hlklib.lib.inject.Click;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.view.ClearEditText;
import com.hlk.hlklib.lib.view.CustomTextView;
import com.leadcom.android.isp.R;
import com.leadcom.android.isp.activity.BaseActivity;
import com.leadcom.android.isp.adapter.RecyclerViewAdapter;
import com.leadcom.android.isp.api.archive.ClassifyRequest;
import com.leadcom.android.isp.api.listener.OnMultipleRequestListener;
import com.leadcom.android.isp.api.listener.OnSingleRequestListener;
import com.leadcom.android.isp.api.org.ConcernRequest;
import com.leadcom.android.isp.api.org.OrgRequest;
import com.leadcom.android.isp.application.App;
import com.leadcom.android.isp.cache.Cache;
import com.leadcom.android.isp.etc.Utils;
import com.leadcom.android.isp.fragment.base.BaseFragment;
import com.leadcom.android.isp.fragment.individual.UserIntroductionFragment;
import com.leadcom.android.isp.fragment.organization.ArchivesFragment;
import com.leadcom.android.isp.fragment.organization.BaseOrganizationFragment;
import com.leadcom.android.isp.fragment.organization.ConcernedOrganizationFragment;
import com.leadcom.android.isp.fragment.organization.ContactFragment;
import com.leadcom.android.isp.fragment.organization.CreateOrganizationFragment;
import com.leadcom.android.isp.fragment.organization.SquadsFragment;
import com.leadcom.android.isp.helper.StringHelper;
import com.leadcom.android.isp.helper.ToastHelper;
import com.leadcom.android.isp.helper.popup.DeleteDialogHelper;
import com.leadcom.android.isp.helper.popup.DialogHelper;
import com.leadcom.android.isp.holder.BaseViewHolder;
import com.leadcom.android.isp.holder.home.GroupDetailsViewHolder;
import com.leadcom.android.isp.holder.home.GroupHeaderViewHolder;
import com.leadcom.android.isp.holder.organization.GroupInterestViewHolder;
import com.leadcom.android.isp.listener.OnViewHolderClickListener;
import com.leadcom.android.isp.listener.OnViewHolderElementClickListener;
import com.leadcom.android.isp.model.Model;
import com.leadcom.android.isp.model.archive.Classify;
import com.leadcom.android.isp.model.common.Attachment;
import com.leadcom.android.isp.model.common.Quantity;
import com.leadcom.android.isp.model.common.SimpleClickableItem;
import com.leadcom.android.isp.model.operation.GRPOperation;
import com.leadcom.android.isp.model.organization.Organization;
import com.leadcom.android.isp.model.organization.Role;
import com.leadcom.android.isp.view.SwipeItemLayout;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

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
    private static final String PARAM_SELECTED = "gf_selected";
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
        selectedIndex = bundle.getInt(PARAM_SELECTED, -1);
    }

    @Override
    protected void saveParamsToBundle(Bundle bundle) {
        super.saveParamsToBundle(bundle);
        bundle.putBoolean(PARAM_SINGLE, isSingle);
        bundle.putInt(PARAM_SELECTED, selectedIndex);
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
    private TextView createView;
    @ViewId(R.id.ui_main_group_mine_background)
    private RelativeLayout groupsBkg;
    @ViewId(R.id.ui_main_group_mine_list_bg)
    private LinearLayout groupListBg;
    @ViewId(R.id.ui_main_group_mine_list)
    private RecyclerView groupList;
    @ViewId(R.id.ui_main_group_self_define)
    private View selfDefine;

    private GroupAdapter gAdapter;
    private DetailsAdapter dAdapter;
    private String[] items;
    private boolean isSingle = false;
    private int selectedIndex = -1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isFirst = true;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //tryPaddingContent(toolBar, false);
        int color = getColor(Cache.sdk >= 23 ? R.color.textColor : R.color.textColorLight);
        titleTextView.setTextColor(color);
        titleAllow.setTextColor(color);
        createView.setTextColor(color);
        isLoadingComplete(true);
        // 单个组织查询时，需要显示左侧的返回
        leftContainer.setVisibility(isSingle ? View.VISIBLE : View.GONE);
        createView.setVisibility(isSingle ? View.GONE : View.VISIBLE);
        titleAllow.setVisibility(isSingle ? View.GONE : View.VISIBLE);
        groupsBkg.setVisibility(isSingle ? View.GONE : View.VISIBLE);
        //selfDefine.setVisibility(isSingle ? View.GONE : View.VISIBLE);
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
            final String groupId = dAdapter.get(0).getId(), url = uploaded.get(0).getUrl();
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
            R.id.ui_main_group_create, R.id.ui_ui_custom_title_left_container,
            R.id.ui_main_group_self_define})
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
            case R.id.ui_main_group_self_define:
                // 自定义栏目创建对话框
                openSelfDefineDialog();
                break;
        }
    }

    private View dialogView;
    private TextView titleText;
    private ClearEditText titleView;

    private void openSelfDefineDialog() {
        DialogHelper.init(Activity()).addOnDialogInitializeListener(new DialogHelper.OnDialogInitializeListener() {
            @Override
            public View onInitializeView() {
                if (null == dialogView) {
                    dialogView = View.inflate(Activity(), R.layout.popup_dialog_squad_add, null);
                    titleView = dialogView.findViewById(R.id.ui_popup_squad_add_input);
                    titleText = dialogView.findViewById(R.id.ui_popup_squad_add_title);
                    titleText.setText(R.string.ui_group_details_self_define);
                    titleView.setTextHint(R.string.ui_group_details_self_define_hint);
                }
                return dialogView;
            }

            @Override
            public void onBindData(View dialogView, DialogHelper helper) {

            }
        }).addOnDialogConfirmListener(new DialogHelper.OnDialogConfirmListener() {
            @Override
            public boolean onConfirm() {
                String name = titleView.getValue();
                if (isEmpty(name)) {
                    ToastHelper.make().showMsg(R.string.ui_group_details_self_define_name_blank);
                    return false;
                }
                addClassify(name);
                Utils.hidingInputBoard(titleView);
                return true;
            }
        }).setConfirmText(selectedIndex >= 7 ? R.string.ui_base_text_change : R.string.ui_base_text_confirm).setPopupType(DialogHelper.SLID_IN_BOTTOM).show();
    }

    private void addClassify(String name) {
        ClassifyRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<Classify>() {
            @Override
            public void onResponse(Classify classify, boolean success, String message) {
                super.onResponse(classify, success, message);
                if (success) {
                    dAdapter.add(classify);
                }
            }
        }).add(dAdapter.get(0).getId(), name, 0);
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
        if (null == gAdapter) {
            initGroupsAdapter();
        }
        if (null != list) {
            for (Organization group : list) {
                group.setSelectable(true);
                Cache.cache().updateGroup(group);
            }
            gAdapter.update(list, true);
            if (isFirst) {
                isFirst = false;
                if (!isSingle) {
                    // 如果是首页里的组织页面，则初始化组织列表的位置
                    initializeGroupsPosition();
                }
                // 初始化第一个组织
                if (gAdapter.getItemCount() > 0) {
                    if (isEmpty(mQueryId)) {
                        onGroupChange(gAdapter.get(0));
                    }
                }
            } else if (gAdapter.getItemCount() > 0) {
                onGroupChange(!isEmpty(mQueryId) ? gAdapter.get(mQueryId) : gAdapter.get(0));
            }
        }
        displayNothing(gAdapter.getItemCount() <= 0);
        if (gAdapter.getItemCount() <= 0) {
            titleTextView.setText(null);
        }
        // 重新拉取我的权限列表
        App.app().fetchPermissions();
        stopRefreshing();
    }

    private void initializeGroupsAdapter() {
        if (null == gAdapter) {
            setNothingText(R.string.ui_organization_structure_no_group_exist);
            initGroupsAdapter();
            Handler().post(new Runnable() {
                @Override
                public void run() {
                    onSwipeRefreshing();
                }
            });
        }
    }

    private void initGroupsAdapter() {
        if (null == gAdapter) {
            gAdapter = new GroupAdapter();
            groupList.setLayoutManager(new CustomLinearLayoutManager(groupList.getContext()));
            groupList.setAdapter(gAdapter);
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
        titleTextView.setText(Html.fromHtml(group.getName()));
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
        }
        resetQuantity(group.getCalculate());
        // 是否有查看成员资料统计权限
        SimpleClickableItem item = new SimpleClickableItem(items[5]);
        if (hasOperation(group.getId(), GRPOperation.MEMBER_COUNT)) {
            dAdapter.update(item);
            if (isSingle) {
                dAdapter.remove(item);
            }
        } else {
            dAdapter.remove(item);
        }
        // 是否有授权管理权限
        item = new SimpleClickableItem(items[6]);
        if (hasOperation(group.getId(), GRPOperation.GROUP_PERMISSION)) {
            dAdapter.update(item);
            if (isSingle) {
                dAdapter.remove(item);
            }
        } else {
            dAdapter.remove(item);
        }
        removeClassify();
        loadGroupSelfDefined();
        if (isSingle) {
            return;
        }
        selfDefine.setVisibility(hasOperation(group.getId(), GRPOperation.GROUP_DEFINE) ? View.VISIBLE : View.GONE);
    }

    private void removeClassify() {
        Iterator<Model> iterator = dAdapter.iterator();
        while (iterator.hasNext()) {
            Model model = iterator.next();
            if (model instanceof Classify) {
                iterator.remove();
            }
        }
        dAdapter.notifyDataSetChanged();
    }

    private void loadGroupSelfDefined() {
        ClassifyRequest.request().setOnMultipleRequestListener(new OnMultipleRequestListener<Classify>() {
            @Override
            public void onResponse(List<Classify> list, boolean success, int totalPages, int pageSize, int total, int pageNumber) {
                super.onResponse(list, success, totalPages, pageSize, total, pageNumber);
                if (success && null != list) {
                    for (Classify classify : list) {
                        dAdapter.add(classify);
                    }
                }
            }
        }).list(dAdapter.get(0).getId());
    }

    private void resetQuantity(Quantity quantity) {
        if (null == quantity) return;
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
                case 5:
                    item.setSource(format(items[index - 1], quantity.getConMeNum()));
                    break;
            }
            item.reset();
            dAdapter.update(item);
        }
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
            mRecyclerView.addOnItemTouchListener(new SwipeItemLayout.OnSwipeItemTouchListener(Activity()));
            mRecyclerView.setAdapter(dAdapter);

            dAdapter.add(new Organization());
            for (String string : items) {
                if (string.contains("6|") || string.contains("7|")) {
                    continue;
                }
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
            if (null == gAdapter || gAdapter.getItemCount() <= 0) {
                return;
            }
            switch (view.getId()) {
                case R.id.ui_holder_view_simple_clickable:
                    Role role = Cache.cache().getGroupRole(dAdapter.get(0).getId());
                    if (null != role) {
                        // 当前登录用户在这个组织里时才能打开相应的选项内容
                        handleItemClick(index);
                    }
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
                case R.id.ui_tool_view_contact_button2:
                    // 删除自定义栏目
                    selectedIndex = index;
                    warningDeleteSelfDefine();
                    break;
            }
        }
    };

    private void warningDeleteSelfDefine() {
        DeleteDialogHelper.helper().init(this).setOnDialogConfirmListener(new DialogHelper.OnDialogConfirmListener() {
            @Override
            public boolean onConfirm() {
                deleteSelfDefine();
                return true;
            }
        }).setTitleText(R.string.ui_group_details_self_define_delete).setConfirmText(R.string.ui_base_text_delete).show();
    }

    private void deleteSelfDefine() {
        Classify classify = (Classify) dAdapter.get(selectedIndex);
        ClassifyRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<Classify>() {
            @Override
            public void onResponse(Classify classify, boolean success, String message) {
                super.onResponse(classify, success, message);
                if (success) {
                    dAdapter.remove(selectedIndex);
                    selectedIndex = -1;
                }
            }
        }).delete(classify.getId());
    }

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
            case 5:
                //if (hasOperation(group.getId(), GRPOperation.GROUP_ASSOCIATION)) {
                // 每个人都可以打开查看关注的组织列表？
                ConcernedOrganizationFragment.open(this, group.getId(), (index == 4 ? ConcernRequest.CONCERN_TO : ConcernRequest.CONCERN_FROM));
                //}
                break;
            case 6:
                // 成员资料统计
                break;
            case 7:
                // 授权管理
                break;
        }
    }

    /**
     * 组织详细内容
     */
    private class DetailsAdapter extends RecyclerViewAdapter<BaseViewHolder, Model> {

        private static final int VT_HEAD = 0, VT_DETAILS = 1, VT_CLASSIFY = 2;

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
            switch (viewType) {
                case VT_HEAD:
                    return R.layout.holder_view_group_header;
                case VT_DETAILS:
                    return R.layout.holder_view_group_details;
                case VT_CLASSIFY:
                    return R.layout.holder_view_group_details_deletable;
                default:
                    return R.layout.holder_view_group_details_deletable;
            }
        }

        @Override
        public int getItemViewType(int position) {
            Model model = get(position);
            return model instanceof Organization ? VT_HEAD : (model instanceof Classify ? VT_CLASSIFY : VT_DETAILS);
        }

        @Override
        public void onBindHolderOfView(BaseViewHolder holder, int position, @Nullable Model item) {
            if (holder instanceof GroupHeaderViewHolder) {
                ((GroupHeaderViewHolder) holder).showContent((Organization) item);
            } else if (holder instanceof GroupDetailsViewHolder) {
                if (item instanceof Classify) {
                    ((GroupDetailsViewHolder) holder).showContent((Classify) item);
                } else {
                    ((GroupDetailsViewHolder) holder).showContent((SimpleClickableItem) item);
                }
            }
        }

        @Override
        protected int comparator(Model item1, Model item2) {
            return 0;
        }
    }
}
