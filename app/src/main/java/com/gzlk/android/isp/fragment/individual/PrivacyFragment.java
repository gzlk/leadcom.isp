package com.gzlk.android.isp.fragment.individual;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.gzlk.android.isp.R;
import com.gzlk.android.isp.adapter.RecyclerViewAdapter;
import com.gzlk.android.isp.cache.Cache;
import com.gzlk.android.isp.fragment.base.BaseSwipeRefreshSupportFragment;
import com.gzlk.android.isp.helper.StringHelper;
import com.gzlk.android.isp.holder.ArchiveSecurityViewHolder;
import com.gzlk.android.isp.listener.OnTitleButtonClickListener;
import com.gzlk.android.isp.listener.OnViewHolderClickListener;
import com.gzlk.android.isp.model.archive.Security;
import com.gzlk.android.isp.model.archive.Archive;
import com.gzlk.android.isp.model.Dao;
import com.gzlk.android.isp.model.Model;
import com.gzlk.android.isp.model.organization.Member;
import com.gzlk.android.isp.model.organization.Organization;
import com.gzlk.android.isp.model.user.User;
import com.litesuits.orm.db.assit.QueryBuilder;

import org.json.JSONObject;

import java.util.ArrayList;
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

    /**
     * 隐私设置
     */
    public static final int REQUEST_SECURITY = ACTIVITY_BASE_REQUEST + 10;

    public static PrivacyFragment newInstance(String params) {
        PrivacyFragment ssf = new PrivacyFragment();
        Bundle bundle = new Bundle();
        bundle.putString(PARAM_QUERY_ID, params);
        ssf.setArguments(bundle);
        return ssf;
    }

    private String[] items;
    private SecurityAdapter mAdapter;

    @Override
    protected void onDelayRefreshComplete(@DelayType int type) {

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

    private void resultPrivacy() {
        try {
            JSONObject object = new JSONObject();
            for (int i = 0, size = mAdapter.getItemCount(); i < size; i++) {
                Model model = mAdapter.get(i);
                if (model.isLocalDeleted()) {
                    if (i <= 1) {
                        // 1=公开，2=不公开
                        object.put("status", (i + 1));
                    } else if (i == 2) {
                        continue;
                    } else {
                        if (model instanceof User) {
                            // 对某人公开
                            object.put("status", 3);
                            object.put("userId", model.getId());
                            object.put("userName", ((User) model).getName());
                        } else if (model instanceof Organization) {
                            // 对某个群体公开
                            object.put("status", 4);
                            object.put("groupId", model.getId());
                            object.put("groupName", ((Organization) model).getName());
                        }
                    }
                    break;
                }
            }
            resultData(object.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
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

    private void initializeAdapter() {
        if (null == items) {
            items = StringHelper.getStringArray(R.array.ui_security_setting_items);
        }
        if (null == mAdapter) {
            mAdapter = new SecurityAdapter();
            mRecyclerView.setAdapter(mAdapter);
            resetStaticItems();
        }
    }

    private void resetStaticItems() {
        if (mAdapter.getItemCount() > 3) {
            int size = mAdapter.getItemCount();
            while (size > 3) {
                mAdapter.remove(size - 1);
                size = mAdapter.getItemCount();
            }
        } else {
            int index = 0, type = Integer.valueOf(mQueryId);
            // 没有基本选项时添加基本选项
            for (String string : items) {
                if (type == Archive.Type.USER && index > 1) {
                    // 个人档案只有公开和私密两种
                    break;
                }
                Security security = new Security(string);
                mAdapter.add(security);
                index++;
            }
        }
    }

    // 单选
    private void resetSingleSelect(int index) {
        // 重置基础的3个选项
        if (index < 2) {
            resetStaticItems();
        }
        for (int i = 0, size = mAdapter.getItemCount(); i < size; i++) {
            Security security = (Security) mAdapter.get(i);
            security.setSelected(security.getIndex() == index);
            security.setLocalDeleted(security.isSelected());
            mAdapter.notifyItemChanged(i);
        }
        if (index == 2) {
            // 增加其他选项
            resetExtraSelection();
        }
    }

    private void resetExtraSelection() {
        // 查找我所在的组织列表
        List<String> orgs = getMyOrganizations();
        if (orgs.size() > 0) {
            QueryBuilder<Organization> query = new QueryBuilder<>(Organization.class).whereIn(Model.Field.Id, orgs.toArray());
            List<Organization> organizations = new Dao<>(Organization.class).query(query);
            if (null != organizations && organizations.size() > 0) {
                for (Organization organization : organizations) {
                    mAdapter.add(organization);
                }
            }
        }
        // 测试用户
        mAdapter.add(new User() {{
            setId("9999");
            setName("测试员");
        }});
        // 从通讯录选择
        mAdapter.add(new Model() {{
            setId("00");
        }});
    }

    private List<String> getMyOrganizations() {
        QueryBuilder<Member> query = new QueryBuilder<>(Member.class)
                .whereEquals(Model.Field.UserId, Cache.cache().userId)
                .whereAppendAnd()
                .whereAppend(Organization.Field.GroupId + " IS NOT NULL ")
                .whereAppendAnd()
                .whereAppend(Organization.Field.SquadId + " IS NULL")
                .orderBy(Model.Field.CreateDate);
        List<Member> members = new Dao<>(Member.class).query(query);
        List<String> result = new ArrayList<>();
        for (Member member : members) {
            result.add(member.getGroupId());
        }
        return result;
    }

    private void resetExtraSelected(int index) {
        String id = mAdapter.get(index).getId();
        for (int i = 3, size = mAdapter.getItemCount(); i < size; i++) {
            Model model = mAdapter.get(i);
            model.setLocalDeleted(model.getId().equals(id));
            mAdapter.notifyItemChanged(i);
        }
    }

    private OnViewHolderClickListener viewHolderClickListener = new OnViewHolderClickListener() {
        @Override
        public void onClick(int index) {
            if (index <= 2) {
                resetSingleSelect(index);
            } else {
                // 单选
                resetExtraSelected(index);
                // 多选
//                Model model = mAdapter.get(index);
//                model.setLocalDeleted(!model.isLocalDeleted());
//                mAdapter.notifyItemChanged(index);
            }
        }
    };

    private class SecurityAdapter extends RecyclerViewAdapter<ArchiveSecurityViewHolder, Model> {
        private int VT_STATIC, VT_GROUP, VT_USER;

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
        public int getItemViewType(int position) {
            if (position <= 2) {
                return VT_STATIC;
            } else if (get(position) instanceof User) {
                return VT_USER;
            }
            return VT_GROUP;
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
