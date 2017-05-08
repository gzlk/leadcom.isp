package com.gzlk.android.isp.fragment.organization;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.gzlk.android.isp.R;
import com.gzlk.android.isp.adapter.RecyclerViewAdapter;
import com.gzlk.android.isp.etc.Utils;
import com.gzlk.android.isp.fragment.base.BaseFragment;
import com.gzlk.android.isp.fragment.base.BaseSwipeRefreshSupportFragment;
import com.gzlk.android.isp.helper.StringHelper;
import com.gzlk.android.isp.helper.ToastHelper;
import com.gzlk.android.isp.holder.BaseViewHolder;
import com.gzlk.android.isp.holder.OrganizationStructureConcernedViewHolder;
import com.gzlk.android.isp.holder.SimpleClickableViewHolder;
import com.gzlk.android.isp.holder.SquadAddViewHolder;
import com.gzlk.android.isp.holder.TextViewHolder;
import com.gzlk.android.isp.listener.OnViewHolderClickListener;
import com.gzlk.android.isp.model.ListItem;
import com.hlk.hlklib.lib.inject.Click;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.view.ClearEditText;
import com.hlk.hlklib.lib.view.CorneredView;

import java.util.ArrayList;
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

public class StructureFragment extends BaseSwipeRefreshSupportFragment {

    private static final String PARAM_SELECTED_ = "sf_selected_index";

    // View
    @ViewId(R.id.ui_popup_squad_add_layout_background)
    private View popupBackground;
    @ViewId(R.id.ui_popup_squad_add_container)
    private CorneredView popupContainer;
    @ViewId(R.id.ui_popup_squad_add_input)
    private ClearEditText popupName;

    // Holder
    private OrganizationStructureConcernedViewHolder concernedViewHolder;

    private String[] items;
    private StructureAdapter mAdapter;
    private int selectedIndex = 0;

    @Override
    protected void getParamsFromBundle(Bundle bundle) {
        super.getParamsFromBundle(bundle);
        selectedIndex = bundle.getInt(PARAM_SELECTED_, 0);
        if (null != concernedViewHolder) {
            concernedViewHolder.setSelected(selectedIndex);
        }
    }

    @Override
    protected void saveParamsToBundle(Bundle bundle) {
        super.saveParamsToBundle(bundle);
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
        enableSwipe(false);
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

    private void initializeAdapter() {
        if (null == mAdapter) {
            mAdapter = new StructureAdapter();
            mRecyclerView.setAdapter(mAdapter);
        }
        initializeItems();
    }

    private void initializeItems() {
        for (String string : items) {
            String text = "";
            if (string.contains("%s")) {
                if (string.startsWith("6|")) {
                    if (squads.size() < 1) {
                        continue;
                    } else {
                        for (String squad : squads) {
                            ListItem item = new ListItem(format(string, squad));
                            item.setId(squad);
                            if (mAdapter.exist(item)) {
                                mAdapter.update(item);
                            } else {
                                mAdapter.add(item, mAdapter.getItemCount() - 1);
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
                ListItem item = new ListItem(text);
                mAdapter.update(item);
            }
        }
    }

    private List<String> squads = new ArrayList<>();

    public void addSquad(String name) {
        squads.add(name);
        initializeItems();
    }

    @Override
    protected void onDelayRefreshComplete(@DelayType int type) {

    }

    @Click({R.id.ui_popup_squad_add_layout_background, R.id.ui_dialog_button_confirm})
    private void elementClick(View view) {
        switch (view.getId()) {
            case R.id.ui_popup_squad_add_layout_background:
                showSquadAddPopup(false);
                break;
            case R.id.ui_dialog_button_confirm:
                String value = popupName.getValue();
                if (StringHelper.isEmpty(value)) {
                    ToastHelper.make().showMsg("输入不符合要求");
                    return;
                }
                Utils.hidingInputBoard(popupName);
                addSquad(value);
                popupName.setValue("");
                showSquadAddPopup(false);
                break;
        }
    }

    public void showSquadAddPopup(final boolean shown) {
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
                openActivity(SquadContactFragment.class.getName(), "", true, false);
            }
        }
    };

    private class StructureAdapter extends RecyclerViewAdapter<BaseViewHolder, ListItem> {

        private static final int VT_HEAD = 0, VT_DIVIDER = 1, VT_CLICK = 2, VT_FOOTER = 3;

        @Override
        public BaseViewHolder onCreateViewHolder(View itemView, int viewType) {
            BaseFragment fragment = StructureFragment.this;
            switch (viewType) {
                case VT_HEAD:
                    if (null == concernedViewHolder) {
                        concernedViewHolder = new OrganizationStructureConcernedViewHolder(itemView, fragment);
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
        public void onBindHolderOfView(BaseViewHolder holder, int position, @Nullable ListItem item) {
            if (holder instanceof SimpleClickableViewHolder) {
                ((SimpleClickableViewHolder) holder).showContent(item);
            }
        }

        @Override
        protected int comparator(ListItem item1, ListItem item2) {
            return 0;
        }
    }
}
