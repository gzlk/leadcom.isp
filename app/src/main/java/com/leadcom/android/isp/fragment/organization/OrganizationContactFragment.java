package com.leadcom.android.isp.fragment.organization;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.leadcom.android.isp.R;
import com.leadcom.android.isp.adapter.RecyclerViewAdapter;
import com.leadcom.android.isp.api.listener.OnSingleRequestListener;
import com.leadcom.android.isp.api.org.MemberRequest;
import com.leadcom.android.isp.fragment.individual.UserPropertyFragment;
import com.leadcom.android.isp.helper.DialogHelper;
import com.leadcom.android.isp.helper.SimpleDialogHelper;
import com.leadcom.android.isp.helper.ToastHelper;
import com.leadcom.android.isp.holder.BaseViewHolder;
import com.leadcom.android.isp.holder.organization.ContactViewHolder;
import com.leadcom.android.isp.listener.OnHandleBoundDataListener;
import com.leadcom.android.isp.listener.OnTitleButtonClickListener;
import com.leadcom.android.isp.listener.OnViewHolderClickListener;
import com.leadcom.android.isp.model.organization.Member;

import java.util.List;

/**
 * <b>功能描述：</b>组织通讯录<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/05/12 22:43 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/05/12 22:43 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class OrganizationContactFragment extends BaseOrganizationFragment {

    private static final String PARAM_TITLE = "ocf_param_title";
    private static final String PARAM_DIAL_INDEX = "ocf_dial_index";

    public static OrganizationContactFragment newInstance(String params) {
        OrganizationContactFragment ocf = new OrganizationContactFragment();
        Bundle bundle = new Bundle();
        String[] strings = splitParameters(params);
        // 此时传入的是组织的id，需要显示此组织的成员列表
        bundle.putString(PARAM_QUERY_ID, strings[0]);
        // 小组的id，要把组织的用户邀请入这个小组
        bundle.putString(PARAM_SQUAD_ID, strings[1]);
        if (strings.length > 2) {
            bundle.putString(PARAM_TITLE, strings[2]);
        }
        ocf.setArguments(bundle);
        return ocf;
    }

    @Override
    protected void getParamsFromBundle(Bundle bundle) {
        super.getParamsFromBundle(bundle);
        mTitle = bundle.getString(PARAM_TITLE, "");
        dialIndex = bundle.getInt(PARAM_DIAL_INDEX, -1);
    }

    @Override
    protected void saveParamsToBundle(Bundle bundle) {
        super.saveParamsToBundle(bundle);
        bundle.putString(PARAM_TITLE, mTitle);
        bundle.putInt(PARAM_DIAL_INDEX, dialIndex);
    }

    private String mTitle = "";
    private int dialIndex = -1;
    private ContactAdapter mAdapter;

    @Override
    protected void onDelayRefreshComplete(@DelayType int type) {

    }

    @Override
    public void doingInResume() {
        if (isEmpty(mTitle)) {
            setCustomTitle(R.string.ui_squad_contact_menu_1);
        } else {
            setCustomTitle(mTitle);
        }
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
        displayLoading(true);
        fetchingRemoteMembers(mOrganizationId, "");
    }

    @Override
    protected void onLoadingMore() {
        isLoadingComplete(true);
    }

    @Override
    protected String getLocalPageTag() {
        return null;
    }

    private void initializeAdapter() {
        if (null == mAdapter) {
            if (!isEmpty(mTitle) && mTitle.contains("(下级)")) {
                setRightText(R.string.ui_organization_top_channel_3);
                setRightTitleClickListener(new OnTitleButtonClickListener() {
                    @Override
                    public void onClick() {
                        ArchivesFragment.open(OrganizationContactFragment.this, mQueryId, mTitle);
                    }
                });
            }
            mAdapter = new ContactAdapter();
            mRecyclerView.setAdapter(mAdapter);
            setLoadingText(R.string.ui_organization_contact_loading_text);
            setNothingText(R.string.ui_organization_contact_no_member);
            displayLoading(true);
            displayNothing(false);
            // 查找本地该组织名下所有成员
            fetchingRemoteMembers(mOrganizationId, "");
        }
    }

    @Override
    protected void onFetchingRemoteMembersComplete(List<Member> list) {
        if (null != list && list.size() > 0) {
            for (Member member : list) {
                member.setSquadId(mSquadId);
                member.setSelected(Member.isPhoneMemberOfGroupOrSquad(member.getPhone(), mOrganizationId, mSquadId));
            }
            mAdapter.add(list, false);
            mAdapter.sort();
        }
        displayLoading(false);
        displayNothing(mAdapter.getItemCount() < 1);
        stopRefreshing();
    }

    private OnHandleBoundDataListener<Member> onHandlerBoundDataListener = new OnHandleBoundDataListener<Member>() {
        @Override
        public Member onHandlerBoundData(BaseViewHolder holder) {
            int index = holder.getAdapterPosition();
            addMemberToSquad(mAdapter.get(index), index);
            return null;
        }
    };

    private OnViewHolderClickListener onViewHolderClickListener = new OnViewHolderClickListener() {
        @Override
        public void onClick(int index) {
            // 打开用户名片
            UserPropertyFragment.open(OrganizationContactFragment.this, mAdapter.get(index).getUserId());
        }
    };

    private void addMemberToSquad(Member member, final int index) {
        // 将不在小组内的组织成员添加到小组
        MemberRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<Member>() {
            @Override
            public void onResponse(Member member, boolean success, String message) {
                super.onResponse(member, success, message);
                if (success) {
                    mAdapter.get(index).setSelected(true);
                    mAdapter.notifyItemChanged(index);
                    ToastHelper.make().showMsg(message);
                }
            }
        }).addToSquadFromGroup(member.getUserId(), mSquadId);
    }

    private ContactViewHolder.OnPhoneDialListener phoneDialListener = new ContactViewHolder.OnPhoneDialListener() {
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
        final String text = mAdapter.get(dialIndex).getPhone();
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

    private class ContactAdapter extends RecyclerViewAdapter<ContactViewHolder, Member> {

        @Override
        public ContactViewHolder onCreateViewHolder(View itemView, int viewType) {
            ContactViewHolder holder = new ContactViewHolder(itemView, OrganizationContactFragment.this);
            holder.showInviteButton(!isEmpty(mSquadId));
            holder.setSquadId(mSquadId);
            if (isEmpty(mSquadId)) {
                holder.addOnViewHolderClickListener(onViewHolderClickListener);
            } else {
                holder.addOnHandlerBoundDataListener(onHandlerBoundDataListener);
            }
            if (!isEmpty(mTitle)) {
                // 关注的组织成员列表时，可以拨号
                holder.setOnPhoneDialListener(phoneDialListener);
            }
            return holder;
        }

        @Override
        public int itemLayout(int viewType) {
            // 这里不需要滑动删除啥的了，所以省了一层layout
            return R.layout.tool_view_organization_contact;
        }

        @Override
        public void onBindHolderOfView(ContactViewHolder holder, int position, @Nullable Member item) {
            holder.showContent(item, "");
        }

        @Override
        protected int comparator(Member item1, Member item2) {
            return item1.getSpell().compareTo(item2.getSpell());
        }
    }
}
