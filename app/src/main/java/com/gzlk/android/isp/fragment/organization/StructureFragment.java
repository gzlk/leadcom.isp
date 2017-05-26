package com.gzlk.android.isp.fragment.organization;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.view.ViewPager;
import android.view.View;

import com.gzlk.android.isp.R;
import com.gzlk.android.isp.adapter.RecyclerViewAdapter;
import com.gzlk.android.isp.api.listener.OnSingleRequestListener;
import com.gzlk.android.isp.api.org.SquadRequest;
import com.gzlk.android.isp.cache.Cache;
import com.gzlk.android.isp.etc.Utils;
import com.gzlk.android.isp.fragment.base.BaseFragment;
import com.gzlk.android.isp.fragment.main.MainFragment;
import com.gzlk.android.isp.helper.DialogHelper;
import com.gzlk.android.isp.helper.SimpleDialogHelper;
import com.gzlk.android.isp.helper.StringHelper;
import com.gzlk.android.isp.helper.ToastHelper;
import com.gzlk.android.isp.holder.BaseViewHolder;
import com.gzlk.android.isp.holder.OrganizationStructureConcernedViewHolder;
import com.gzlk.android.isp.holder.SimpleClickableViewHolder;
import com.gzlk.android.isp.holder.SquadAddViewHolder;
import com.gzlk.android.isp.holder.TextViewHolder;
import com.gzlk.android.isp.listener.OnViewHolderClickListener;
import com.gzlk.android.isp.model.Dao;
import com.gzlk.android.isp.model.Model;
import com.gzlk.android.isp.model.SimpleClickableItem;
import com.gzlk.android.isp.model.organization.Organization;
import com.gzlk.android.isp.model.organization.Squad;
import com.hlk.hlklib.lib.inject.Click;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.view.ClearEditText;
import com.hlk.hlklib.lib.view.CorneredView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
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
    private OrganizationStructureConcernedViewHolder concernedViewHolder;

    private String[] items;
    private StructureAdapter mAdapter;
    private int selectedIndex = -1;
    // 是否第一次初始化，此时需要隐藏添加小组的输入框
    private boolean initialized = false;

    public MainFragment mainFragment;

    private OnOrganizationChangedListener organizationChangedListener;

    public void setOnOrganizationChangedListener(OnOrganizationChangedListener l) {
        organizationChangedListener = l;
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
        fetchingJoinedRemoteOrganizations();
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
        if (null != list && list.size() > 0) {
            concernedViewHolder.add(list);
        } else {
            // 当前显示本fragment时才提示用户
            if (getUserVisibleHint()) {
                ToastHelper.make().showMsg(R.string.ui_organization_structure_no_group_exist);
            }
        }
        stopRefreshing();
        setSupportLoadingMore(false);
    }

    private void initializeAdapter() {
        if (null == mAdapter) {
            mAdapter = new StructureAdapter();
            mRecyclerView.setAdapter(mAdapter);
        }
        initializeItems();
        fetchingJoinedRemoteOrganizations();
    }

    private void initializeItems() {
        for (String string : items) {
            String text = "";
            if (string.contains("%s")) {
                if (string.startsWith("6|")) {
                    if (squads.size() < 1) {
                        continue;
                    } else {
                        for (Squad squad : squads) {
                            if (mAdapter.exist(squad)) {
                                mAdapter.update(squad);
                            } else {
                                mAdapter.add(squad, mAdapter.getItemCount() - 1);
                            }
                        }
                    }
                } else {
                    text = format(string, "");
                }
            } else if (string.contains("%d")) {
                text = format(string, squads.size());
            } else {
                text = string;
            }
            if (!StringHelper.isEmpty(text)) {
                SimpleClickableItem item = new SimpleClickableItem(text);
                mAdapter.update(item);
            }
        }
    }

    private List<Squad> squads = new ArrayList<>();

    @Override
    protected void onDelayRefreshComplete(@DelayType int type) {

    }

    @Click({R.id.ui_popup_squad_add_layout_background, R.id.ui_dialog_button_confirm, R.id.ui_tool_image_view_delete_container})
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
        addNewSquadToOrganization(id, name, introduction);
    }

    @Override
    protected void onAddNewSquadToOrganizationComplete(Squad squad) {
        if (null != squad && !StringHelper.isEmpty(squad.getId())) {
            if (!squads.contains(squad)) {
                squads.add(squad);
            }
            initializeItems();
        }
        // 重新拉取小组列表
        changeSelectedGroup();
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

    private OnViewHolderClickListener holderClickListener = new OnViewHolderClickListener() {
        @Override
        public void onClick(int index) {
            if (index > 5 && index < mAdapter.getItemCount()) {
                Squad squad = squads.get(index - 6);
                if (isMember(Cache.cache().userId, squad.getGroupId(), squad.getId())) {
                    openActivity(ContactFragment.class.getName(), format("%d,%s", ContactFragment.TYPE_SQUAD, squads.get(index - 6).getId()), true, false);
                } else {
                    warningJoinIntoSquad(squad.getId(), squad.getName());
                }
            }
        }
    };

    private void warningJoinIntoSquad(String squadId, String squadName) {
        SimpleDialogHelper.init(Activity()).show(StringHelper.getString(R.string.ui_organization_squad_not_member, squadName), StringHelper.getString(R.string.ui_base_text_yes), StringHelper.getString(R.string.ui_base_text_no_need), new DialogHelper.OnDialogConfirmListener() {
            @Override
            public boolean onConfirm() {
                //joinIntoSquad();
                ToastHelper.make().showMsg("暂时不能申请加入小组（无api支持）");
                return true;
            }
        }, null);
    }

    private void joinIntoSquad() {
        SquadRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<Squad>() {
            @Override
            public void onResponse(Squad squad, boolean success, String message) {
                super.onResponse(squad, success, message);
            }
        });
    }

    private ViewPager.OnPageChangeListener onPageChangeListener = new ViewPager.OnPageChangeListener() {
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

    public void changeSelectedGroup() {
        if (selectedIndex < 0) return;
        Organization organization = concernedViewHolder.get(selectedIndex);
        // 更改标题栏上的文字和icon
        if (null != organizationChangedListener) {
            organizationChangedListener.onChanged(organization);
        }
        // 本组织下的小组列表
        fetchingGroupSquads(organization.getId());
    }

    private void fetchingGroupSquads(String groupId) {
        // 清空已经显示了的小组列表
        for (Squad squad : squads) {
            mAdapter.remove(squad);
        }
        squads.clear();
        initializeItems();
        // 查询本地小组列表
        List<Squad> temp = new Dao<>(Squad.class).query(Organization.Field.GroupId, groupId);
        if (null != temp && temp.size() > 0) {
            squads.addAll(temp);
            sortSquads();
            initializeItems();
        }
        // 拉取远程的小组列表
        fetchingRemoteSquads(groupId);
    }

    @Override
    protected void onFetchingRemoteSquadsComplete(List<Squad> list) {
        if (null != list && list.size() > 0) {
            for (Squad squad : list) {
                if (!squads.contains(squad)) {
                    squads.add(squad);
                }
            }
            sortSquads();
            initializeItems();
        }
    }

    private void sortSquads() {
        // 根据创建日期排序
        Collections.sort(squads, new Comparator<Squad>() {
            @Override
            public int compare(Squad o1, Squad o2) {
                return o1.getCreateDate().compareTo(o2.getCreateDate());
            }
        });
    }

    private class StructureAdapter extends RecyclerViewAdapter<BaseViewHolder, Model> {

        private static final int VT_HEAD = 0, VT_DIVIDER = 1, VT_CLICK = 2, VT_FOOTER = 3;

        @Override
        public BaseViewHolder onCreateViewHolder(View itemView, int viewType) {
            BaseFragment fragment = StructureFragment.this;
            switch (viewType) {
                case VT_HEAD:
                    if (null == concernedViewHolder) {
                        concernedViewHolder = new OrganizationStructureConcernedViewHolder(itemView, fragment);
                        concernedViewHolder.setPageChangeListener(onPageChangeListener);
                        concernedViewHolder.loadingLocal();
                    }
                    return concernedViewHolder;
                case VT_DIVIDER:
                    return new TextViewHolder(itemView, fragment);
                case VT_FOOTER:
                    return new SquadAddViewHolder(itemView, fragment);
                default:
                    SimpleClickableViewHolder holder = new SimpleClickableViewHolder(itemView, fragment);
                    holder.addOnViewHolderClickListener(holderClickListener);
                    return holder;
            }
        }

        @Override
        public int itemLayout(int viewType) {
            switch (viewType) {
                case VT_HEAD:
                    return R.layout.holder_view_organization_concerned;
                case VT_DIVIDER:
                    return R.layout.tool_view_divider_big;
                case VT_FOOTER:
                    return R.layout.holder_view_squad_add_layout;
                default:
                    return R.layout.holder_view_simple_clickable_gravity_left;
            }
        }

        @Override
        public int getItemViewType(int position) {
            if (position == getItemCount() - 1) {
                return VT_FOOTER;
            }
            switch (position) {
                case 0:
                    return VT_HEAD;
                case 1:
                case 4:
                    return VT_DIVIDER;
                default:
                    return VT_CLICK;
            }
        }

        @Override
        public void onBindHolderOfView(BaseViewHolder holder, int position, @Nullable Model item) {
            if (holder instanceof SimpleClickableViewHolder) {
                SimpleClickableViewHolder scvh = (SimpleClickableViewHolder) holder;
                if (item instanceof SimpleClickableItem) {
                    scvh.showContent((SimpleClickableItem) item);
                } else if (item instanceof Squad) {
                    scvh.showContent((Squad) item);
                }
            }
        }

        @Override
        protected int comparator(Model item1, Model item2) {
            return item1.getId().compareTo(item2.getId());
        }
    }
}
