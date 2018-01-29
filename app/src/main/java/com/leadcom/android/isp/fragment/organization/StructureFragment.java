package com.leadcom.android.isp.fragment.organization;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.google.gson.reflect.TypeToken;
import com.leadcom.android.isp.R;
import com.leadcom.android.isp.adapter.RecyclerViewSwipeAdapter;
import com.leadcom.android.isp.api.listener.OnSingleRequestListener;
import com.leadcom.android.isp.api.org.OrgRequest;
import com.leadcom.android.isp.api.org.SimpleOrgRequest;
import com.leadcom.android.isp.api.org.SimpleOutput;
import com.leadcom.android.isp.api.org.SquadRequest;
import com.leadcom.android.isp.etc.Utils;
import com.leadcom.android.isp.fragment.base.BaseFragment;
import com.leadcom.android.isp.fragment.main.MainFragment;
import com.leadcom.android.isp.fragment.main.OrganizationFragment;
import com.leadcom.android.isp.helper.DialogHelper;
import com.leadcom.android.isp.helper.SimpleDialogHelper;
import com.leadcom.android.isp.helper.StringHelper;
import com.leadcom.android.isp.helper.ToastHelper;
import com.leadcom.android.isp.holder.BaseViewHolder;
import com.leadcom.android.isp.holder.common.SimpleClickableViewHolder;
import com.leadcom.android.isp.holder.common.TextViewHolder;
import com.leadcom.android.isp.holder.organization.OrgStructureViewHolder;
import com.leadcom.android.isp.holder.organization.SquadAddViewHolder;
import com.leadcom.android.isp.lib.DepthViewPager;
import com.leadcom.android.isp.lib.Json;
import com.leadcom.android.isp.listener.OnHandleBoundDataListener;
import com.leadcom.android.isp.listener.OnViewHolderClickListener;
import com.leadcom.android.isp.model.Dao;
import com.leadcom.android.isp.model.Model;
import com.leadcom.android.isp.model.common.SimpleClickableItem;
import com.leadcom.android.isp.model.organization.Concern;
import com.leadcom.android.isp.model.organization.Member;
import com.leadcom.android.isp.model.organization.Organization;
import com.leadcom.android.isp.model.organization.Squad;
import com.hlk.hlklib.lib.inject.Click;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.view.ClearEditText;
import com.hlk.hlklib.lib.view.CorneredView;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * <b>功能描述：</b>组织架构<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/05/05 10:36 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/05/05 10:36 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class StructureFragment extends BaseOrganizationFragment {

    private static final String PARAM_SELECTED_ = "sf_selected_index";
    private static final String PARAM_INITIALIZED = "sf_initialized";

    // View
    @ViewId(R.id.ui_popup_squad_add_layout_background)
    private View popupBackground;
    @ViewId(R.id.ui_popup_squad_add_container)
    private CorneredView popupContainer;
    @ViewId(R.id.ui_popup_squad_add_input)
    private ClearEditText popupName;
    @ViewId(R.id.ui_popup_squad_add_introduction)
    private ClearEditText popupIntroducing;

    // Holder
    private OrgStructureViewHolder concernedViewHolder;

    private String[] items;
    private StructureAdapter mAdapter;
    private int selectedIndex = -1;
    // 是否第一次初始化，此时需要隐藏添加小组的输入框
    private boolean initialized = false;

    public MainFragment mainFragment;
    public OrganizationFragment organizationFragment;

    private OnOrganizationChangedListener organizationChangedListener;

    public void setOnOrganizationChangedListener(OnOrganizationChangedListener l) {
        organizationChangedListener = l;
    }

    @Override
    public void onDestroy() {
        selectedGroupId = "";
        super.onDestroy();
    }

    @Override
    protected void getParamsFromBundle(Bundle bundle) {
        super.getParamsFromBundle(bundle);
        initialized = bundle.getBoolean(PARAM_INITIALIZED, false);
        selectedIndex = bundle.getInt(PARAM_SELECTED_, -1);
        if (null != concernedViewHolder && selectedIndex >= 0) {
            concernedViewHolder.setSelected(selectedIndex);
        }
    }

    @Override
    protected void saveParamsToBundle(Bundle bundle) {
        super.saveParamsToBundle(bundle);
        bundle.putBoolean(PARAM_INITIALIZED, initialized);
        if (null != concernedViewHolder) {
            selectedIndex = concernedViewHolder.getSelected();
        }
        bundle.putInt(PARAM_SELECTED_, selectedIndex);
    }

    @Override
    public int getLayout() {
        return R.layout.fragment_organization_structure;
    }

    @Override
    protected void onSwipeRefreshing() {
        // 刷新我加入的组织列表
        refreshRemoteOrganizations();
        listAllMembers();
    }

    private void listAllMembers() {
        if (!isEmpty(selectedGroupId)) {
            SimpleOrgRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<SimpleOutput>() {
                @Override
                public void onResponse(SimpleOutput simpleOutput, boolean success, String message) {
                    super.onResponse(simpleOutput, success, message);
                }
            }).listAllMember(selectedGroupId);
        }
    }

    @Override
    protected void onLoadingMore() {

    }

    @Override
    protected String getLocalPageTag() {
        return null;
    }

    @Override
    public void doingInResume() {
        setSupportLoadingMore(false);
        if (null == items) {
            items = StringHelper.getStringArray(R.array.ui_organization_structure_items);
            mainFragment.setStructureFragment(this);
        }
        initializeAdapter();
    }

    @Override
    protected boolean shouldSetDefaultTitleEvents() {
        return false;
    }

    @Override
    protected void destroyView() {

    }

    @Override
    protected void onFetchingJoinedRemoteOrganizationsComplete(List<Organization> list) {
        if (null != list) {
            concernedViewHolder.add(list);
        } else {
            // 当前显示本fragment时才提示用户
            if (getUserVisibleHint()) {
                ToastHelper.make().showMsg(R.string.ui_organization_structure_no_group_exist);
            }
        }
        displayLoading(false);
        stopRefreshing();
        setSupportLoadingMore(false);
    }

    public boolean isConcerned(String id) {
        return null != concernedViewHolder && concernedViewHolder.isConcerned(id);
    }

    private void initializeAdapter() {
        if (null == mAdapter) {
            mAdapter = new StructureAdapter();
            mRecyclerView.setAdapter(mAdapter);
            initializeItems();
        }
        refreshRemoteOrganizations();
    }

    private void refreshRemoteOrganizations() {
        displayLoading(true);
        fetchingJoinedRemoteOrganizations(OrgRequest.GROUP_LIST_OPE_JOINED);
    }

    private void initializeItems() {
        for (String string : items) {
            if (string.charAt(0) == '1') {
                // 已关注的组织数量统计
                Organization group = null == concernedViewHolder ? null : concernedViewHolder.get(selectedIndex);
                string = format(string, (null == group ? 0 : (null == group.getConGroup() ? 0 : group.getConGroup().size())));
            } else if (string.charAt(0) == '4') {
                // 下属小组
                string = format(string, 0);
            }
            SimpleClickableItem item = new SimpleClickableItem(string);
            mAdapter.update(item);
        }
    }

    @Override
    protected void onDelayRefreshComplete(@DelayType int type) {

    }

    @Click({R.id.ui_popup_squad_add_layout_background,
            R.id.ui_dialog_button_confirm,
            R.id.ui_tool_image_view_delete_container})
    private void elementClick(View view) {
        switch (view.getId()) {
            case R.id.ui_popup_squad_add_layout_background:
            case R.id.ui_tool_image_view_delete_container:
                Utils.hidingInputBoard(popupName);
                showSquadAddPopup(false);
                break;
            case R.id.ui_dialog_button_confirm:
                String value = popupName.getValue();
                if (StringHelper.isEmpty(value)) {
                    ToastHelper.make().showMsg(R.string.ui_organization_squad_add_name_invalid);
                    return;
                }
                String introduction = popupIntroducing.getValue();
                if (StringHelper.isEmpty(introduction)) {
                    ToastHelper.make().showMsg(R.string.ui_organization_squad_add_introduction_invalid);
                    return;
                }
                Utils.hidingInputBoard(popupName);
                addNewSquad(value, introduction);
                popupName.setValue("");
                popupIntroducing.setValue("");
                showSquadAddPopup(false);
                break;
        }
    }

    private void addNewSquad(String name, String introduction) {
        Organization organization = concernedViewHolder.get(selectedIndex);
        final String id = organization.getId();
        displayLoading(true);
        addNewSquadToOrganization(id, name, introduction);
    }

    @Override
    protected void onAddNewSquadToOrganizationComplete(Squad squad) {
        if (null != squad && !StringHelper.isEmpty(squad.getId())) {
            SimpleClickableItem sci = new SimpleClickableItem(items[4]);
            int index = mAdapter.indexOf(sci);
            mAdapter.add(squad, index + 1);
        }
        displayLoading(false);
    }

    public void showSquadAddPopup(final boolean shown) {
        if (selectedIndex < 0) {
            selectedIndex = concernedViewHolder.getSelected();
        }
        if (null == concernedViewHolder.get(selectedIndex)) {
            ToastHelper.make().showMsg(R.string.ui_organization_structure_no_group_exist);
            return;
        }
        popupBackground.animate().setDuration(duration())
                .alpha(shown ? 1 : 0).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (!shown) {
                    popupBackground.setVisibility(View.GONE);
                }
            }

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
                if (shown) {
                    popupBackground.setVisibility(View.VISIBLE);
                }
            }
        }).withStartAction(new Runnable() {
            @Override
            public void run() {
                showSquadAddInputLayout(shown);
            }
        }).start();
    }

    private void showSquadAddInputLayout(final boolean shown) {
        popupContainer.animate().setDuration(duration())
                .alpha(shown ? 1 : 0).translationY(shown ? 0 : popupContainer.getHeight())
                .setListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        if (!shown) {
                            popupContainer.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onAnimationStart(Animator animation) {
                        super.onAnimationStart(animation);
                        if (shown) {
                            popupContainer.setVisibility(View.VISIBLE);
                        }
                    }
                }).start();
    }

    private static final int REQ_INTEREST = ACTIVITY_BASE_REQUEST + 100;
    private static final int REQ_CONCERNED = ACTIVITY_BASE_REQUEST + 101;
    private OnViewHolderClickListener holderClickListener = new OnViewHolderClickListener() {
        @Override
        public void onClick(int index) {
            Model model = mAdapter.get(index);
            if (model instanceof SimpleClickableItem) {
                switch (((SimpleClickableItem) model).getIndex()) {
                    case 1:
                        if (my.groupAssociatable()) {
                            Organization org = concernedViewHolder.get(selectedIndex);
                            ArrayList<Concern> concerns = org.getConGroup();
                            for (Concern concern : concerns) {
                                concern.setConcerned(true);
                            }
                            String json = Json.gson().toJson(concerns, new TypeToken<ArrayList<Concern>>() {
                            }.getType());
                            // 已关注的组织列表
                            ConcernedOrganizationFragment.open(StructureFragment.this, selectedGroupId, StringHelper.replaceJson(json, false), REQ_CONCERNED);
                        } else {
                            ToastHelper.make().showMsg(R.string.ui_organization_structure_no_permission_concern);
                        }
                        break;
                    case 2:
                        // 可能感兴趣的组织列表
                        if (my.groupAssociatable()) {
                            InterestingOrganizationFragment.open(StructureFragment.this, selectedGroupId, REQ_INTEREST);
                        } else {
                            ToastHelper.make().showMsg(R.string.ui_organization_structure_no_permission_concern);
                        }
                        break;
                }
            } else if (model instanceof Squad) {
                Squad squad = (Squad) model;
                // 当前登录者的角色
                ContactFragment.squadRole = squad.getGroRole();
                openSquadContact(squad.getGroupId(), squad.getId());
            } else if (model instanceof Concern) {
                Concern concern = (Concern) model;
                String title = format("%s(%s)", concern.getName(), Concern.getTypeString(concern.getType()));
                openActivity(OrganizationContactFragment.class.getName(), format("%s,,%s", concern.getId(), title), true, false);
            }
        }
    };

    private void openSquadContact(String groupId, String squadId) {
        openActivity(ContactFragment.class.getName(), format("%d,%s,%s", ContactFragment.TYPE_SQUAD, groupId, squadId), true, false);
    }

    private DepthViewPager.OnPageChangeListener onPageChangeListener = new DepthViewPager.OnPageChangeListener() {
        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            selectedIndex = position;
            Handler().post(new Runnable() {
                @Override
                public void run() {
                    changeSelectedGroup();
                }
            });
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    /**
     * 当前登录用户在本组织内的角色
     */
    public static Member my;
    /**
     * 当前选中的组织
     */
    public static Organization selectedOrganization;
    /**
     * 当前选中的组织的id
     */
    public static String selectedGroupId = "";

    public void changeSelectedGroup() {
        if (selectedIndex < 0) return;
        Organization org = concernedViewHolder.get(selectedIndex);
        selectedOrganization = org;
        if (null != org) {
            my = org.getGroMember();
            selectedGroupId = org.getId();
            log(format("change group to %s, %s", org.getName(), org.getId()));
            // 显示我关注的组织列表
            clearConcerned();
            ArrayList<Concern> concerns = org.getConGroup();
            SimpleClickableItem sciConcerned = new SimpleClickableItem(format(items[1], null == concerns ? 0 : concerns.size()));
            mAdapter.update(sciConcerned);
            if (null != concerns) {
                int index = mAdapter.indexOf(sciConcerned);
                for (Concern concern : concerns) {
                    mAdapter.add(concern, index + 1);
                    index++;
                }
            }
            SimpleClickableItem sci = new SimpleClickableItem(items[2]);
            mAdapter.update(sci);
            // 本组织下的小组列表
            fetchingGroupSquads(org.getId());
            // 如果当前用户是管理员的话，增加推荐档案列表的显示
            if (my.isManager() || my.isArchiveManager()) {
                organizationFragment.addRecommendedArchives(true, selectedGroupId);
            } else {
                organizationFragment.addRecommendedArchives(false, selectedGroupId);
            }
        }
        // 更改标题栏上的文字和icon
        if (null != organizationChangedListener) {
            organizationChangedListener.onChanged(org);
        }
    }

    private void clearConcerned() {
        Iterator<Model> iterator = mAdapter.iterator();
        int index = 0;
        while (iterator.hasNext()) {
            Model model = iterator.next();
            if (model instanceof Concern) {
                iterator.remove();
                mAdapter.notifyItemRemoved(index);
            }
            index++;
        }
    }

    private void clearSquads() {
        Iterator<Model> iterator = mAdapter.iterator();
        int index = 0;
        while (iterator.hasNext()) {
            Model model = iterator.next();
            if (model instanceof Squad) {
                iterator.remove();
                mAdapter.notifyItemRemoved(index);
            }
            index++;
        }
    }

    private void fetchingGroupSquads(String groupId) {
        displayLoading(true);
        // 清空已经显示了的小组列表
        clearSquads();
        // 拉取远程的小组列表
        if (!isEmpty(groupId)) {
            fetchingRemoteSquads(groupId);
        }
    }

    @Override
    protected void onFetchingRemoteSquadsComplete(List<Squad> list) {
        SimpleClickableItem sci = new SimpleClickableItem(format(items[4], null == list ? 0 : list.size()));
        mAdapter.update(sci);
        if (null != list) {
            int index = mAdapter.indexOf(sci);
            for (Squad squad : list) {
                mAdapter.add(squad, index + 1);
                index++;
            }
        }
        notifySquadAddable();
        displayLoading(false);
    }

    private void notifySquadAddable() {
        SimpleClickableItem sci = new SimpleClickableItem(items[5]);
        mAdapter.update(sci);
    }

    private void warningDeleteSquad(final int index) {
        Squad squad = (Squad) mAdapter.get(index);
        String text = getString(R.string.ui_organization_squad_delete_warning, squad.getName());
        SimpleDialogHelper.init(Activity()).show(text, R.string.ui_base_text_yes, R.string.ui_base_text_cancel, new DialogHelper.OnDialogConfirmListener() {
            @Override
            public boolean onConfirm() {
                deleteSquad(mAdapter.get(index).getId());
                return true;
            }
        }, null);
    }

    private void deleteSquad(final String squadId) {
        SquadRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<Squad>() {
            @Override
            public void onResponse(Squad squad, boolean success, String message) {
                super.onResponse(squad, success, message);
                if (success) {
                    Squad s = new Squad();
                    s.setId(squadId);
                    mAdapter.remove(s);
                    new Dao<>(Squad.class).delete(squadId);
                }
            }
        }).delete(squadId);
    }

    // SimpleClickable的删除事件
    private OnHandleBoundDataListener<Squad> handlerBoundDataListener = new OnHandleBoundDataListener<Squad>() {
        @Override
        public Squad onHandlerBoundData(BaseViewHolder holder) {
            warningDeleteSquad(holder.getAdapterPosition());
            return null;
        }
    };

    private class StructureAdapter extends RecyclerViewSwipeAdapter<BaseViewHolder, Model> {

        private static final int VT_HEAD = 0, VT_DIVIDER = 1, VT_CLICK = 2, VT_SQUAD = 3, VT_FOOTER = 4;

        @Override
        public BaseViewHolder onCreateViewHolder(View itemView, int viewType) {
            BaseFragment fragment = StructureFragment.this;
            switch (viewType) {
                case VT_HEAD:
                    if (null == concernedViewHolder) {
                        concernedViewHolder = new OrgStructureViewHolder(itemView, fragment);
                        concernedViewHolder.setPageChangeListener(onPageChangeListener);
                    }
                    return concernedViewHolder;
                case VT_DIVIDER:
                    return new TextViewHolder(itemView, fragment);
                case VT_FOOTER:
                    return new SquadAddViewHolder(itemView, fragment);
                case VT_SQUAD:
                default:
                    SimpleClickableViewHolder holder = new SimpleClickableViewHolder(itemView, fragment);
                    holder.addOnViewHolderClickListener(holderClickListener);
                    holder.addOnHandlerBoundDataListener(handlerBoundDataListener);
                    return holder;
            }
        }

        public int itemLayout(int viewType) {
            switch (viewType) {
                case VT_HEAD:
                    return R.layout.holder_view_organization_concerned;
                case VT_DIVIDER:
                    return R.layout.tool_view_divider_big;
                case VT_FOOTER:
                    return R.layout.holder_view_squad_add_layout;
                case VT_SQUAD:
                    return R.layout.holder_view_simple_clickable_deleteable_gravity_left;
                default:
                    return R.layout.holder_view_simple_clickable_gravity_left;
            }
        }

        @Override
        public void onBindHolderOfView(BaseViewHolder holder, int position, @Nullable Model item) {
            if (holder instanceof SimpleClickableViewHolder) {
                SimpleClickableViewHolder scvh = (SimpleClickableViewHolder) holder;
                if (item instanceof SimpleClickableItem) {
                    scvh.showContent((SimpleClickableItem) item);
                } else if (item instanceof Concern) {
                    scvh.showContent((Concern) item);
                } else if (item instanceof Squad) {
                    scvh.showContent((Squad) item);
                    scvh.showDelete(null != my && my.squadDeletable());
                }
            } else if (holder instanceof SquadAddViewHolder) {
                ((SquadAddViewHolder) holder).showAddContainer(null != my && my.squadAddable());
            }
        }

        @Override
        public int getItemViewType(int position) {
            Model model = get(position);
            switch (model.getId()) {
                case "3":
                    return VT_DIVIDER;
                case "0":
                    return VT_HEAD;
                case "5":
                    return VT_FOOTER;
            }
            if (model instanceof Squad) {
                return VT_SQUAD;
            } else {
                return VT_CLICK;
            }
        }

        @Override
        protected int comparator(Model item1, Model item2) {
            return 0;
        }

        @Override
        public int getSwipeLayoutResourceId(int i) {
            return 0;
        }
    }
}
