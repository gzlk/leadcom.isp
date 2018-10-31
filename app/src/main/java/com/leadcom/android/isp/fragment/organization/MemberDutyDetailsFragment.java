package com.leadcom.android.isp.fragment.organization;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.View;

import com.leadcom.android.isp.R;
import com.leadcom.android.isp.adapter.RecyclerViewAdapter;
import com.leadcom.android.isp.api.archive.ArchiveRequest;
import com.leadcom.android.isp.api.listener.OnMultipleRequestListener;
import com.leadcom.android.isp.api.user.MemberDutyRequest;
import com.leadcom.android.isp.fragment.archive.ArchiveDetailsFragment;
import com.leadcom.android.isp.fragment.base.BaseFragment;
import com.leadcom.android.isp.fragment.base.BaseSwipeRefreshSupportFragment;
import com.leadcom.android.isp.helper.StringHelper;
import com.leadcom.android.isp.holder.common.TextViewHolder;
import com.leadcom.android.isp.listener.OnViewHolderClickListener;
import com.leadcom.android.isp.model.Model;
import com.leadcom.android.isp.model.archive.Archive;

import java.util.List;


/**
 * <b>功能描述：</b>组织成员履历详情展示页面<br />
 * <b>创建作者：</b>SYSTEM <br />
 * <b>创建时间：</b>2018/08/14 22:46 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>version: 1.0.0 <br />
 * <b>修改时间：</b>2017/10/04 18:50 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */
public class MemberDutyDetailsFragment extends BaseSwipeRefreshSupportFragment {

    private static final String PARAM_USER_ID = "gmddf_user_id";
    private static final String PARAM_USER_NAME = "gmddf_user_name";
    private static final String PARAM_OPTION = "gmddf_option";
    private static final String PARAM_COUNT = "gmddf_count";
    private static final String PARAM_YEAR = "gmddf_year";
    private static final String PARAM_NATURE = "gmddf_nature";
    private static final String PARAM_TYPE = "gmddf_type";
    private static final String PARAM_SQUAD_ID = "gmddf_squad_id";

    public static MemberDutyDetailsFragment newInstance(Bundle bundle) {
        MemberDutyDetailsFragment gmddf = new MemberDutyDetailsFragment();
        gmddf.setArguments(bundle);
        return gmddf;
    }

    public static void open(BaseFragment fragment, String groupId, String squadId, String userId, String userName, int option, long count, int year, String nature, String type) {
        Bundle bundle = new Bundle();
        bundle.putString(PARAM_QUERY_ID, groupId);
        bundle.putString(PARAM_USER_ID, userId);
        bundle.putString(PARAM_USER_NAME, userName);
        bundle.putInt(PARAM_OPTION, option);
        bundle.putLong(PARAM_COUNT, count);
        bundle.putInt(PARAM_YEAR, year);
        bundle.putString(PARAM_NATURE, nature);
        bundle.putString(PARAM_TYPE, type);
        bundle.putString(PARAM_SQUAD_ID, squadId);
        fragment.openActivity(MemberDutyDetailsFragment.class.getName(), bundle, true, false);
    }

    private String mUserId, mUserName, mNature, mType, mSquadId;
    private int mOpe, mYear;
    private long mCount;

    private DutyAdapter mAdapter;

    @Override
    protected void getParamsFromBundle(Bundle bundle) {
        super.getParamsFromBundle(bundle);
        mUserId = bundle.getString(PARAM_USER_ID, "");
        mUserName = bundle.getString(PARAM_USER_NAME, "");
        mNature = bundle.getString(PARAM_NATURE, "");
        mType = bundle.getString(PARAM_TYPE, "");
        mOpe = bundle.getInt(PARAM_OPTION, MemberDutyRequest.OPE_NONE);
        mCount = bundle.getLong(PARAM_COUNT, 0);
        mYear = bundle.getInt(PARAM_YEAR, MemberDutyRequest.YEAR_ALL);
        mSquadId = bundle.getString(PARAM_SQUAD_ID, "");
    }

    @Override
    protected void saveParamsToBundle(Bundle bundle) {
        super.saveParamsToBundle(bundle);
        bundle.putString(PARAM_USER_ID, mUserId);
        bundle.putString(PARAM_USER_NAME, mUserName);
        bundle.putInt(PARAM_OPTION, mOpe);
        bundle.putLong(PARAM_COUNT, mCount);
        bundle.putInt(PARAM_YEAR, mYear);
        bundle.putString(PARAM_NATURE, mNature);
        bundle.putString(PARAM_TYPE, mType);
        bundle.putString(PARAM_SQUAD_ID, mSquadId);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setCustomTitle(StringHelper.getString(R.string.ui_group_member_duty_details_title, mUserName, (mOpe == MemberDutyRequest.OPE_ACTIVITY ? "活动" : "档案"), mCount));
    }

    @Override
    protected void onSwipeRefreshing() {
        loadingDutyArchives();
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

    @Override
    public void onActivityResult(int requestCode, Intent data) {
        switch (requestCode) {
            case REQUEST_DELETE:
                String id = getResultedData(data);
                Model result = getResultModel(data, RESULT_ARCHIVE);
                if (null != result) {
                    //mAdapter.update((Archive) result);
                } else if (!isEmpty(id)) {
                    Archive archive = new Archive();
                    archive.setId(id);
                    mAdapter.remove(archive);
                }
                break;
        }
        super.onActivityResult(requestCode, data);
    }

    private void initializeAdapter() {
        if (null == mAdapter) {
            mAdapter = new DutyAdapter();
            mRecyclerView.setAdapter(mAdapter);
            loadingDutyArchives();
        }
    }

    private void loadingDutyArchives() {
        displayLoading(true);
        displayNothing(false);
        ArchiveRequest.request().setOnMultipleRequestListener(new OnMultipleRequestListener<Archive>() {
            @Override
            public void onResponse(List<Archive> list, boolean success, int totalPages, int pageSize, int total, int pageNumber) {
                super.onResponse(list, success, totalPages, pageSize, total, pageNumber);
                if (success && null != list) {
                    for (Archive archive : list) {
                        archive.setId(archive.getDocId());
                        archive.setGroupId(mQueryId);
                        mAdapter.update(archive);
                    }
                }
                stopRefreshing();
                isLoadingComplete(true);
                displayLoading(false);
                displayNothing(mAdapter.getItemCount() <= 0);
            }
        }).listMemberDuty(mOpe, mQueryId, mSquadId, mUserId, mYear, mNature, mType);
    }

    private OnViewHolderClickListener clickListener = new OnViewHolderClickListener() {
        @Override
        public void onClick(int index) {
            ArchiveDetailsFragment.open(MemberDutyDetailsFragment.this, mAdapter.get(index));
        }
    };

    private class DutyAdapter extends RecyclerViewAdapter<TextViewHolder, Archive> {

        @Override
        public TextViewHolder onCreateViewHolder(View itemView, int viewType) {
            TextViewHolder holder = new TextViewHolder(itemView, MemberDutyDetailsFragment.this);
            holder.setGravity(Gravity.START);
            holder.addOnViewHolderClickListener(clickListener);
            return holder;
        }

        @Override
        public int itemLayout(int viewType) {
            return R.layout.holder_view_text_olny;
        }

        @Override
        public void onBindHolderOfView(TextViewHolder holder, int position, @Nullable Archive item) {
            assert item != null;
            holder.showContent(format("%d、%s", position + 1, item.getTitle()));
        }

        @Override
        protected int comparator(Archive item1, Archive item2) {
            return 0;
        }
    }
}
