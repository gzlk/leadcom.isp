package com.leadcom.android.isp.fragment.organization;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.leadcom.android.isp.R;
import com.leadcom.android.isp.adapter.RecyclerViewAdapter;
import com.leadcom.android.isp.fragment.base.BaseFragment;
import com.leadcom.android.isp.fragment.individual.UserPropertyFragment;
import com.leadcom.android.isp.helper.DialogHelper;
import com.leadcom.android.isp.helper.SimpleDialogHelper;
import com.leadcom.android.isp.holder.BaseViewHolder;
import com.leadcom.android.isp.holder.organization.ContactViewHolder;
import com.leadcom.android.isp.holder.organization.SquadViewHolder;
import com.leadcom.android.isp.listener.OnViewHolderClickListener;
import com.leadcom.android.isp.model.Model;
import com.leadcom.android.isp.model.organization.Member;
import com.leadcom.android.isp.model.organization.Squad;

import java.util.List;

import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;

/**
 * <b>功能描述：</b>组织 - 小组列表<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2018/03/17 21:00 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>version: 1.0.0 <br />
 * <b>修改时间：</b>2017/10/04 18:50 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */
public class SquadsFragment extends BaseOrganizationFragment {

    public static SquadsFragment newInstance(String params) {
        SquadsFragment sf = new SquadsFragment();
        Bundle bundle = new Bundle();
        // 传过来的组织id
        bundle.putString(PARAM_QUERY_ID, params);
        sf.setArguments(bundle);
        return sf;
    }

    public static void open(BaseFragment fragment, String groupId) {
        fragment.openActivity(SquadsFragment.class.getName(), groupId, REQUEST_CREATE, true, false);
    }

    private SquadAdapter mAdapter;
    private static int dialIndex = -1;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        dialIndex = -1;
        enableSwipe(false);
        isLoadingComplete(true);
        setCustomTitle(R.string.ui_group_squad_fragment_title);
        setRightText(R.string.ui_base_text_add);
        setNothingText(R.string.ui_group_squad_nothing);
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

    private OnViewHolderClickListener onViewHolderClickListener = new OnViewHolderClickListener() {
        @Override
        public void onClick(int index) {
            Model model = mAdapter.get(index);
            if (model instanceof Squad) {
                // 展开小组成员列表
            } else if (model instanceof Member) {
                UserPropertyFragment.open(SquadsFragment.this, model.getId());
            }
        }
    };
    private ContactViewHolder.OnPhoneDialListener onPhoneDialListener = new ContactViewHolder.OnPhoneDialListener() {
        @Override
        public void onDial(int index) {
            dialIndex = index;
            requestPhoneCallPermission();
        }
    };

    @Override
    public void permissionGranted(String[] permissions, int requestCode) {
        super.permissionGranted(permissions, requestCode);
        if (requestCode == GRANT_PHONE_CALL) {
            warningDial();
        }
    }

    private void warningDial() {
        if (dialIndex < 0) return;
        Model model = mAdapter.get(dialIndex);
        if (model instanceof Member) {
            Member member = (Member) model;
            final String text = member.getPhone();
            if (!isEmpty(text)) {
                String yes = getString(R.string.ui_base_text_dial);
                String no = getString(R.string.ui_base_text_cancel);
                SimpleDialogHelper.init(Activity()).show(text, yes, no, new DialogHelper.OnDialogConfirmListener() {
                    @Override
                    public boolean onConfirm() {
                        dialPhone(text);
                        return true;
                    }
                }, null);
            }
        }
    }

    private void initializeAdapter() {
        if (null == mAdapter) {
            mAdapter = new SquadAdapter();
            mRecyclerView.setAdapter(mAdapter);
            OverScrollDecoratorHelper.setUpOverScroll(mRecyclerView, OverScrollDecoratorHelper.ORIENTATION_VERTICAL);
            fetchingRemoteSquads(mQueryId);
        }
    }

    @Override
    protected void onFetchingRemoteSquadsComplete(List<Squad> list) {
        if (null != list && list.size() > 0) {
            for (Squad squad : list) {
                mAdapter.add(squad);
            }
        }
        displayNothing(mAdapter.getItemCount() <= 0);
    }

    private class SquadAdapter extends RecyclerViewAdapter<BaseViewHolder, Model> {
        private static final int VT_SQUAD = 0, VT_MEMBER = 1;

        @Override
        public BaseViewHolder onCreateViewHolder(View itemView, int viewType) {
            switch (viewType) {
                case VT_SQUAD:
                    SquadViewHolder svh = new SquadViewHolder(itemView, SquadsFragment.this);
                    svh.addOnViewHolderClickListener(onViewHolderClickListener);
                    return svh;
                case VT_MEMBER:
                    ContactViewHolder cvh = new ContactViewHolder(itemView, SquadsFragment.this);
                    cvh.addOnViewHolderClickListener(onViewHolderClickListener);
                    cvh.setOnPhoneDialListener(onPhoneDialListener);
                    return cvh;
            }
            return null;
        }

        @Override
        public int itemLayout(int viewType) {
            return viewType == VT_MEMBER ? R.layout.tool_view_organization_contact : R.layout.holder_view_group_squad;
        }

        @Override
        public int getItemViewType(int position) {
            return get(position) instanceof Squad ? VT_SQUAD : VT_MEMBER;
        }

        @Override
        public void onBindHolderOfView(BaseViewHolder holder, int position, @Nullable Model item) {
            if (holder instanceof ContactViewHolder) {
                ((ContactViewHolder) holder).showContent((Member) item, "");
            } else if (holder instanceof SquadViewHolder) {
                ((SquadViewHolder) holder).showContent((Squad) item);
            }
        }

        @Override
        protected int comparator(Model item1, Model item2) {
            return 0;
        }
    }
}
