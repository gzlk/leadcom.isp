package com.leadcom.android.isp.fragment.organization;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.View;

import com.leadcom.android.isp.R;
import com.leadcom.android.isp.adapter.RecyclerViewAdapter;
import com.leadcom.android.isp.api.listener.OnMultipleRequestListener;
import com.leadcom.android.isp.api.listener.OnSingleRequestListener;
import com.leadcom.android.isp.api.org.ConcernRequest;
import com.leadcom.android.isp.fragment.base.BaseFragment;
import com.leadcom.android.isp.fragment.base.BaseSwipeRefreshSupportFragment;
import com.leadcom.android.isp.helper.StringHelper;
import com.leadcom.android.isp.helper.popup.DeleteDialogHelper;
import com.leadcom.android.isp.helper.popup.DialogHelper;
import com.leadcom.android.isp.holder.BaseViewHolder;
import com.leadcom.android.isp.holder.common.LabelViewHolder;
import com.leadcom.android.isp.holder.common.NothingMoreViewHolder;
import com.leadcom.android.isp.holder.common.TextViewHolder;
import com.leadcom.android.isp.holder.organization.GroupInterestViewHolder;
import com.leadcom.android.isp.listener.OnViewHolderClickListener;
import com.leadcom.android.isp.listener.OnViewHolderElementClickListener;
import com.leadcom.android.isp.model.Model;
import com.leadcom.android.isp.model.organization.Concern;
import com.leadcom.android.isp.model.organization.Organization;

import java.util.Iterator;
import java.util.List;


/**
 * <b>功能描述：</b>组织资料授权管理页面<br />
 * <b>创建作者：</b>SYSTEM <br />
 * <b>创建时间：</b>2018/07/29 16:59 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>version: 1.0.0 <br />
 * <b>修改时间：</b>2017/10/04 18:50 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */
public class GroupAuthorizeFragment extends BaseSwipeRefreshSupportFragment {

    public static GroupAuthorizeFragment newInstance(Bundle bundle) {
        GroupAuthorizeFragment gaf = new GroupAuthorizeFragment();
        gaf.setArguments(bundle);
        return gaf;
    }

    public static void open(BaseFragment fragment, String groupId) {
        Bundle bundle = new Bundle();
        bundle.putString(PARAM_QUERY_ID, groupId);
        fragment.openActivity(GroupAuthorizeFragment.class.getName(), bundle, true, false);
    }

    private static String authorizedId = String.valueOf(R.string.ui_group_authorize_authorized),
            authorizingId = String.valueOf(R.string.ui_group_authorize_authorizing);
    private AuthorizeAdapter mAdapter;
    private int selectedIndex = -1;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        layoutType = TYPE_FLEX;
        super.onActivityCreated(savedInstanceState);
        isLoadingComplete(true);
        setCustomTitle(R.string.ui_group_authorize_fragment_title);
    }

    @Override
    protected void onSwipeRefreshing() {
        fetchingAuthorized();
        fetchingAuthorizing();
    }

    @Override
    protected void onLoadingMore() {

    }

    @Override
    protected String getLocalPageTag() {
        return null;
    }

    @Override
    protected void onDelayRefreshComplete(int type) {

    }

    @Override
    public void doingInResume() {
        initializeAdapter();
    }

    @Override
    protected boolean shouldSetDefaultTitleEvents() {
        return true;
    }

    @Override
    protected void destroyView() {

    }

    private void clearItems(boolean authorized) {
        Iterator<Model> iterator = mAdapter.iterator();
        while (iterator.hasNext()) {
            Model model = iterator.next();
            if (model instanceof Concern) {
                Concern concern = (Concern) model;
                if (authorized && concern.isAuthorized()) {
                    iterator.remove();
                } else if (!authorized && !concern.isAuthorized()) {
                    iterator.remove();
                }
            } else if (model.getId().contains("nothing")) {
                iterator.remove();
            }
        }
        mAdapter.notifyDataSetChanged();
    }

    private void fetchingAuthorized() {
        clearItems(true);
        ConcernRequest.request().setOnMultipleRequestListener(new OnMultipleRequestListener<Concern>() {
            @Override
            public void onResponse(List<Concern> list, boolean success, int totalPages, int pageSize, int total, int pageNumber) {
                super.onResponse(list, success, totalPages, pageSize, total, pageNumber);
                if (success && null != list) {
                    int count = 0;
                    for (Concern concern : list) {
                        count++;
                        if (isEmpty(concern.getId())) {
                            concern.setId(concern.getGroupId());
                        }
                        mAdapter.add(concern, count);
                    }
                }
                if (!success || null == list || list.size() <= 0) {
                    Model model = new Model();
                    model.setId(StringHelper.getString(R.string.ui_group_authorize_authorized_nothing));
                    mAdapter.add(model, 1);
                }
                stopRefreshing();
            }
        }).listAuthorized(mQueryId);
    }

    private void fetchingAuthorizing() {
        clearItems(false);
        ConcernRequest.request().setOnMultipleRequestListener(new OnMultipleRequestListener<Concern>() {
            @Override
            public void onResponse(List<Concern> list, boolean success, int totalPages, int pageSize, int total, int pageNumber) {
                super.onResponse(list, success, totalPages, pageSize, total, pageNumber);
                if (success && null != list) {
                    int index = mAdapter.indexOf(mAdapter.get("warning"));
                    int count = 0;
                    for (Concern concern : list) {
                        count++;
                        if (isEmpty(concern.getId())) {
                            concern.setId(concern.getGroupId());
                        }
                        mAdapter.add(concern, count + index);
                    }
                }
                stopRefreshing();
            }
        }).listAuthorizing(mQueryId);
    }

    private void initializeAdapter() {
        if (null == mAdapter) {
            mAdapter = new AuthorizeAdapter();
            mRecyclerView.setAdapter(mAdapter);
            Model model = new Model();
            model.setId(authorizedId);
            mAdapter.add(model);
            model = new Model();
            model.setId("line1");
            mAdapter.add(model);
            model = new Model();
            model.setId(authorizingId);
            mAdapter.add(model);
            model = new Model();
            model.setId("warning");
            mAdapter.add(model);
            fetchingAuthorized();
            fetchingAuthorizing();
        }
    }

    private OnViewHolderElementClickListener elementClickListener = new OnViewHolderElementClickListener() {
        @Override
        public void onClick(View view, int index) {
            if (view.getId() == R.id.ui_holder_view_group_interest_button) {
                // 授权或取消授权
                selectedIndex = index;
                warningAuthorize();
            }
        }
    };

    private void warningAuthorize() {
        Concern concern = (Concern) mAdapter.get(selectedIndex);
        String title = StringHelper.getString(concern.isAuthorized() ? R.string.ui_group_authorize_title_authorized : R.string.ui_group_authorize_title_authorizing, concern.getGroupName());
        DeleteDialogHelper.helper().setTitleText(title).setOnDialogConfirmListener(new DialogHelper.OnDialogConfirmListener() {
            @Override
            public boolean onConfirm() {
                authorize();
                return true;
            }
        }).init(this).setConfirmText(R.string.ui_base_text_confirm).show();
    }

    private void authorize() {
        Concern waiting = (Concern) mAdapter.get(selectedIndex);
        if (waiting.isAuthorized()) {
            ConcernRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<Concern>() {
                @Override
                public void onResponse(Concern concern, boolean success, String message) {
                    super.onResponse(concern, success, message);
                    if (success) {
                        Concern c = (Concern) mAdapter.get(selectedIndex);
                        c.setAuthorized(Organization.AuthorizeType.NONE);
                        mAdapter.update(c);
                    }
                    selectedIndex = -1;
                }
            }).removeAuthorize(waiting.getId());
        } else {
            ConcernRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<Concern>() {
                @Override
                public void onResponse(Concern concern, boolean success, String message) {
                    super.onResponse(concern, success, message);
                    if (success) {
                        Concern c = (Concern) mAdapter.get(selectedIndex);
                        c.setAuthorized(Organization.AuthorizeType.AUTHORIZED);
                        c.setId(concern.getId());
                        mAdapter.replace(c, selectedIndex);
                    }
                    selectedIndex = -1;
                }
            }).addAuthorize(mQueryId, waiting.getGroupId());
        }
    }

    private OnViewHolderClickListener clickListener = new OnViewHolderClickListener() {
        @Override
        public void onClick(int index) {
            Concern concern = (Concern) mAdapter.get(index);
            if (!isEmpty(concern.getAllowGroupId())) {
                MemberNatureMainFragment.open(GroupAuthorizeFragment.this, concern.getGroupId(), concern.getGroupName(), false, "");
            }
        }
    };

    private class AuthorizeAdapter extends RecyclerViewAdapter<BaseViewHolder, Model> {
        private static final int VT_TITLE = 0, VT_AUTHORIZED = 1, VT_LINE = 2, VT_WARNING = 3, VT_AUTHORIZING = 4;

        @Override
        public BaseViewHolder onCreateViewHolder(View itemView, int viewType) {
            switch (viewType) {
                case VT_TITLE:
                    TextViewHolder holder = new TextViewHolder(itemView, GroupAuthorizeFragment.this);
                    holder.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
                    holder.showBottomLine(false);
                    return holder;
                case VT_AUTHORIZED:
                    LabelViewHolder lvh = new LabelViewHolder(itemView, GroupAuthorizeFragment.this);
                    lvh.addOnViewHolderClickListener(clickListener);
                    return lvh;
                case VT_AUTHORIZING:
                    GroupInterestViewHolder givh = new GroupInterestViewHolder(itemView, GroupAuthorizeFragment.this);
                    givh.setOnViewHolderElementClickListener(elementClickListener);
                    return givh;
                default:
                    return new NothingMoreViewHolder(itemView, GroupAuthorizeFragment.this);
            }
        }

        @Override
        public int itemLayout(int viewType) {
            switch (viewType) {
                case VT_TITLE:
                    return R.layout.holder_view_text_olny;
                case VT_AUTHORIZED:
                    return R.layout.holder_view_activity_label;
                case VT_LINE:
                    return R.layout.tool_view_half_line_horizontal;
                case VT_WARNING:
                    return R.layout.holder_view_group_authorize_warning;
                case VT_AUTHORIZING:
                    return R.layout.holder_view_group_interesting_item;
            }
            return 0;
        }

        @Override
        public int getItemViewType(int position) {
            Model model = get(position);
            if (model.getId().contains("line"))
                return VT_LINE;
            if (model.getId().contains("warning"))
                return VT_WARNING;
            if (model instanceof Concern) {
                Concern concern = (Concern) model;
                // 收到的授权
                if (!isEmpty(concern.getAllowGroupId()))
                    return VT_AUTHORIZED;
                // 请求授权的组织
                return VT_AUTHORIZING;
            }
            return VT_TITLE;
        }

        @Override
        public void onBindHolderOfView(BaseViewHolder holder, int position, @Nullable Model item) {
            if (holder instanceof LabelViewHolder) {
                ((LabelViewHolder) holder).showContent((Concern) item);
            } else if (holder instanceof TextViewHolder) {
                assert item != null;
                String id = item.getId();
                if (id.contains("nothing")) {
                    ((TextViewHolder) holder).showContent(id.replace("nothing", ""));
                } else {
                    int string = Integer.valueOf(item.getId());
                    String text = "<b>" + StringHelper.getString(string) + "</b>";
                    ((TextViewHolder) holder).showContent(text);
                }
            } else if (holder instanceof GroupInterestViewHolder) {
                ((GroupInterestViewHolder) holder).showContent((Concern) item, true);
            }
        }

        @Override
        protected int comparator(Model item1, Model item2) {
            return 0;
        }
    }
}
