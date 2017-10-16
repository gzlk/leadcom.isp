package com.gzlk.android.isp.fragment.archive;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.google.gson.reflect.TypeToken;
import com.gzlk.android.isp.R;
import com.gzlk.android.isp.adapter.RecyclerViewAdapter;
import com.gzlk.android.isp.fragment.base.BaseFragment;
import com.gzlk.android.isp.fragment.base.BaseSwipeRefreshSupportFragment;
import com.gzlk.android.isp.helper.StringHelper;
import com.gzlk.android.isp.holder.archive.ArchiveSecurityViewHolder;
import com.gzlk.android.isp.lib.Json;
import com.gzlk.android.isp.listener.OnTitleButtonClickListener;
import com.gzlk.android.isp.listener.OnViewHolderClickListener;
import com.gzlk.android.isp.model.Dao;
import com.gzlk.android.isp.model.Model;
import com.gzlk.android.isp.model.common.Seclusion;
import com.gzlk.android.isp.model.archive.Security;
import com.gzlk.android.isp.model.organization.Member;
import com.gzlk.android.isp.model.organization.Organization;
import com.litesuits.orm.db.assit.QueryBuilder;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * <b>功能描述：</b>隐私设置<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/05/16 21:46 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/05/16 21:46 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class PrivacyFragment extends BaseSwipeRefreshSupportFragment {

    public static PrivacyFragment newInstance(String params) {
        PrivacyFragment ssf = new PrivacyFragment();
        String[] strings = splitParameters(params);
        Bundle bundle = new Bundle();
        isIndividual = Boolean.valueOf(strings[0]);
        // 已选中的隐私项目
        bundle.putString(PARAM_QUERY_ID, StringHelper.replaceJson(strings[1], true));
        ssf.setArguments(bundle);
        return ssf;
    }

    /**
     * 设置隐私
     */
    public static void open(BaseFragment fragment, String json, boolean individual) {
        fragment.openActivity(PrivacyFragment.class.getName(), format("%s,%s", individual, json), REQUEST_SECURITY, true, false);
    }

    private static boolean isIndividual = false;
    private String[] items;
    private SecurityAdapter mAdapter;

    @Override
    protected void onDelayRefreshComplete(@DelayType int type) {

    }

    @Override
    public void onDestroy() {
        isIndividual = false;
        super.onDestroy();
    }

    @Override
    public void doingInResume() {
        setCustomTitle(R.string.ui_security_fragment_title);
        setLeftIcon(0);
        setLeftText(R.string.ui_base_text_cancel);
        setRightIcon(0);
        setRightText(R.string.ui_base_text_finish);
        setRightTitleClickListener(new OnTitleButtonClickListener() {
            @Override
            public void onClick() {
                // 完成隐私设置
                resultPrivacy();
            }
        });
        initializeAdapter();
    }

    /**
     * 根据json字符串获取隐私设置对象
     */
    public static Seclusion getSeclusion(String json) {
        if (StringHelper.isEmpty(json)) {
            Seclusion seclusion = new Seclusion();
            seclusion.setStatus(Seclusion.Type.Public);
            return seclusion;
        }
        return Json.gson().fromJson(json, new TypeToken<Seclusion>() {
        }.getType());
    }

    /**
     * 序列号隐私设置对象为json字符串
     */
    public static String getSeclusion(Seclusion seclusion) {
        return Json.gson().toJson(seclusion, new TypeToken<Seclusion>() {
        }.getType());
    }

    private static String getSecurityNames(List<String> list) {
        if (null == list || list.size() < 1) {
            return "";
        }
        String ret = "";
        for (String string : list) {
            ret += (isEmpty(ret) ? "" : ",") + string;
        }
        return ret;
    }

    public static String getPrivacy(Seclusion seclusion) {
        String names = getSecurityNames(seclusion.getUserNames());
        switch (seclusion.getStatus()) {
            case Seclusion.Type.Private:
                // 只能自己可见
                return StringHelper.getString(R.string.ui_base_text_private);
            case Seclusion.Type.Public:
                // 对所有人公开
                return StringHelper.getString(R.string.ui_base_text_public);
            case Seclusion.Type.Group:
                // 组织内公开
                return StringHelper.getString(R.string.ui_security_force_to_group);
            case Seclusion.Type.Specify:
                // 对指定人公开
                return StringHelper.getString(R.string.ui_security_force_to_user, names);
        }
        return StringHelper.getString(R.string.ui_security_fragment_title);
    }

    private void resultPrivacy() {
        Seclusion seclusion = getSeclusion(mQueryId);
        int status = seclusion.getStatus();
        for (int i = 0, size = mAdapter.getItemCount(); i < size; i++) {
            Model model = mAdapter.get(i);
            if (model.isSelected()) {
                if (model instanceof Security) {
                    Security security = (Security) model;
                    status = security.getIndex();
                    if (security.getIndex() == Seclusion.Type.Public) {
                        // 如果是对组织公开，则直接跳出循环
                        seclusion.setUserIds(null);
                        seclusion.setUserNames(null);
                        seclusion.setGroupIds(null);
                        seclusion.setGroupNames(null);
                        break;
                    }
                } else if (model instanceof Organization) {
                    Organization org = (Organization) model;
                    if (null == seclusion.getGroupIds()) {
                        seclusion.setGroupIds(new ArrayList<String>());
                        seclusion.setGroupNames(new ArrayList<String>());
                    }
                    if (!seclusion.getGroupIds().contains(org.getId())) {
                        seclusion.getGroupIds().add(org.getId());
                        seclusion.getGroupNames().add(org.getName());
                    }
                } else if (model instanceof Member) {
                    Member member = (Member) model;
                    if (null == seclusion.getUserIds()) {
                        seclusion.setUserIds(new ArrayList<String>());
                        seclusion.setUserNames(new ArrayList<String>());
                    }
                    if (!seclusion.getUserIds().contains(member.getUserId())) {
                        seclusion.getUserIds().add(member.getUserId());
                        seclusion.getUserNames().add(member.getUserName());
                    }
                }
            }
        }
        seclusion.setStatus(status);
        // 如果用户对象数量大于0则可以清空组织列表，此时只是对选中的组织公开
        resultData(getSeclusion(seclusion));
    }

    @Override
    protected boolean shouldSetDefaultTitleEvents() {
        return true;
    }

    @Override
    public int getLayout() {
        return R.layout.fragment_archive_security;
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

    private ArrayList<Security> securities = new ArrayList<>();

    private void resetSecurityItems() {
        securities.clear();
        Seclusion seclusion = PrivacyFragment.getSeclusion(mQueryId);
        for (String string : items) {
            Security security = new Security(string);
            // 设置传过来的默认选择项
            security.setSelected(security.getIndex() == seclusion.getStatus());
            securities.add(security);
        }
    }

    private void initializeAdapter() {
        if (null == items) {
            items = StringHelper.getStringArray(R.array.ui_security_setting_items);
            resetSecurityItems();
        }
        if (null == mAdapter) {
            mAdapter = new SecurityAdapter();
            mRecyclerView.setAdapter(mAdapter);
            resetStaticItems();
        }
    }

    private void resetStaticItems() {
        if (mAdapter.getItemCount() < 1) {
            // 空列表
            for (Security security : securities) {
                if (isIndividual) {
                    if (security.isUserVisible()) {
                        mAdapter.update(security);
                    }
                } else {
                    if (security.isGroupVisible()) {
                        mAdapter.update(security);
                    }
                }
            }
        } else {
            // 重置为基本选项
            int index = 0;
            int size = mAdapter.getItemCount();
            while (index < size) {
                Model model = mAdapter.get(index);
                if (model instanceof Security) {
                    // 保留基本选项
                    index++;
                } else {
                    // 删除其余所有选项
                    mAdapter.remove(index);
                    size = mAdapter.getItemCount();
                }
            }
        }
    }

    // 单选
    private void resetSingleSelect(Security selected) {
        // 重置基础的3个选项
        //if (selected.getModelTag() == 1) {
        resetStaticItems();
        //}
        for (int i = 0, size = mAdapter.getItemCount(); i < size; i++) {
            Model model = mAdapter.get(i);
            if (model instanceof Security) {
                Security security = (Security) model;
                security.setSelected(security.getIndex() == selected.getIndex());
                mAdapter.notifyItemChanged(i);
            }
        }
        if (selected.getIndex() > 2) {
            // 增加其他选项
            resetGroupSelections(selected);
        }
    }

    // 重置组织列表
    private void resetGroupSelections(Security selected) {
        // 查找我所在的组织列表
        List<String> groups = getMyOrganizations();
        if (groups.size() > 0) {
            QueryBuilder<Organization> query = new QueryBuilder<>(Organization.class).whereIn(Model.Field.Id, groups.toArray());
            List<Organization> organizations = new Dao<>(Organization.class).query(query);
            if (null != organizations && organizations.size() > 0) {
                int index = mAdapter.indexOf(selected) + 1;
                for (Organization organization : organizations) {
                    mAdapter.add(organization, index);
                    index++;
                }
            }
        }
        // 测试用户
//        mAdapter.add(new User() {{
//            setId("9999");
//            setName("测试员");
//        }});
        // 从通讯录选择
//        mAdapter.add(new Model() {{
//            setId("00");
//        }});
    }

    private List<String> getMyOrganizations() {
        List<Member> members = Member.getMyMembersOfJoinedGroups();
        List<String> result = new ArrayList<>();
        for (Member member : members) {
            result.add(member.getGroupId());
        }
        return result;
    }

    // 点击了组织时，需要移除所有的成员列表，并重新加上当前选中的组织成员
    private void clearMemberSelections(int index) {

        Organization org = (Organization) mAdapter.get(index);
        org.setSelected(!org.isSelected());
        mAdapter.notifyItemChanged(index);

        Iterator<Model> iterator = mAdapter.iterator();
        while (iterator.hasNext()) {
            Model model = iterator.next();
            if (model instanceof Member) {
                iterator.remove();
            }
        }
        mAdapter.notifyItemRangeChanged(2, mAdapter.getItemCount());

        if (multiGroupSelected()) {
            // 多个组织选中时，不再显示用户列表
            return;
        }

        expandSelectedGroupMembers();
    }

    private void expandSelectedGroupMembers() {
        Organization org = null;
        for (int i = 0, len = mAdapter.getItemCount(); i < len; i++) {
            Model model = mAdapter.get(i);
            if (model instanceof Organization) {
                Organization organization = (Organization) model;
                if (organization.isSelected()) {
                    org = organization;
                    break;
                }
            }
        }
        if (null != org) {
            List<Member> members = Member.getMembersOfGroupOrSquad(org.getId(), "");
            if (null != members && members.size() > 0) {
                // 在当前点击组织后面添加成员列表
                int index = mAdapter.indexOf(org);
                int i = index + 1;
                for (Member member : members) {
                    if (!mAdapter.exist(member)) {
                        mAdapter.add(member, i);
                        i++;
                    }
                }
            }
        }
    }

    private boolean multiGroupSelected() {
        int size = 0;
        for (int i = 0, len = mAdapter.getItemCount(); i < len; i++) {
            Model model = mAdapter.get(i);
            if (model instanceof Organization) {
                Organization org = (Organization) model;
                if (org.isSelected()) {
                    size++;
                }
            }
        }
        return size > 1;
    }

    private OnViewHolderClickListener viewHolderClickListener = new OnViewHolderClickListener() {
        @Override
        public void onClick(int index) {
            Model model = mAdapter.get(index);
            if (model instanceof Security) {
                // 重置基本选项
                resetSingleSelect((Security) model);
            } else if (model instanceof Organization) {
                // 选中了组织部分，组织可多选
                clearMemberSelections(index);
            } else if (model instanceof Member) {
                // 选中了组织中的成员，可多选
                Member member = (Member) model;
                member.setSelected(!member.isSelected());
                mAdapter.notifyItemChanged(index);
            }
        }
    };

    private class SecurityAdapter extends RecyclerViewAdapter<ArchiveSecurityViewHolder, Model> {

        @Override
        public ArchiveSecurityViewHolder onCreateViewHolder(View itemView, int viewType) {
            ArchiveSecurityViewHolder holder = new ArchiveSecurityViewHolder(itemView, PrivacyFragment.this);
            holder.addOnViewHolderClickListener(viewHolderClickListener);
            return holder;
        }

        @Override
        public int itemLayout(int viewType) {
            return R.layout.holder_view_security_item;
        }

        @Override
        public void onBindHolderOfView(ArchiveSecurityViewHolder holder, int position, @Nullable Model item) {
            holder.showContent(item);
        }

        @Override
        protected int comparator(Model item1, Model item2) {
            return 0;
        }
    }
}
