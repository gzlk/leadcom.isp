package com.leadcom.android.isp.fragment.main;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.leadcom.android.isp.R;
import com.leadcom.android.isp.adapter.RecyclerViewAdapter;
import com.leadcom.android.isp.fragment.base.BaseFragment;
import com.leadcom.android.isp.fragment.base.BaseSwipeRefreshSupportFragment;
import com.leadcom.android.isp.helper.ToastHelper;
import com.leadcom.android.isp.holder.activity.ActivityViewHolder;
import com.leadcom.android.isp.listener.OnViewHolderClickListener;
import com.leadcom.android.isp.nim.session.NimSessionHelper;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.team.TeamService;
import com.netease.nimlib.sdk.team.model.Team;

import java.util.List;

import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;


/**
 * <b>功能描述：</b>群聊列表<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2018/03/19 21:50 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>version: 1.0.0 <br />
 * <b>修改时间：</b>2017/10/04 18:50 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */
public class TeamListFragment extends BaseSwipeRefreshSupportFragment {

    public static void open(BaseFragment fragment) {
        fragment.openActivity(TeamListFragment.class.getName(), "", true, false);
    }

    private TeamAdapter mAdapter;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        enableSwipe(false);
        isLoadingComplete(true);
        setCustomTitle(R.string.ui_text_home_recent_contact_context_menu_1);
        setNothingText(R.string.ui_text_home_team_empty);
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

    private void fetchingTeams() {
        NIMClient.getService(TeamService.class).queryTeamList().setCallback(new RequestCallback<List<Team>>() {
            @Override
            public void onSuccess(List<Team> param) {
                if (null != param) {
                    mAdapter.add(param);
                }
                displayNothing(mAdapter.getItemCount() <= 0);
            }

            @Override
            public void onFailed(int code) {
                ToastHelper.make().showMsg(getString(R.string.ui_text_home_team_fetching_failed, code));
                displayNothing(mAdapter.getItemCount() <= 0);
            }

            @Override
            public void onException(Throwable exception) {
                ToastHelper.make().showMsg(getString(R.string.ui_text_home_team_fetching_failed, -1));
                displayNothing(mAdapter.getItemCount() <= 0);
            }
        });
    }

    private void initializeAdapter() {
        if (null == mAdapter) {
            mAdapter = new TeamAdapter();
            mRecyclerView.setAdapter(mAdapter);
            OverScrollDecoratorHelper.setUpOverScroll(mRecyclerView, OverScrollDecoratorHelper.ORIENTATION_VERTICAL);
            fetchingTeams();
        }
    }

    private OnViewHolderClickListener onViewHolderClickListener = new OnViewHolderClickListener() {
        @Override
        public void onClick(int index) {
            NimSessionHelper.startTeamSession(Activity(), mAdapter.get(index).getId());
        }
    };

    private class TeamAdapter extends RecyclerViewAdapter<ActivityViewHolder, Team> {

        @Override
        public ActivityViewHolder onCreateViewHolder(View itemView, int viewType) {
            ActivityViewHolder avh = new ActivityViewHolder(itemView, TeamListFragment.this);
            avh.addOnViewHolderClickListener(onViewHolderClickListener);
            return avh;
        }

        @Override
        public int itemLayout(int viewType) {
            return R.layout.holder_view_activity_home_item;
        }

        @Override
        public void onBindHolderOfView(ActivityViewHolder holder, int position, @Nullable Team item) {
            holder.showContent(item);
        }

        @Override
        protected int comparator(Team item1, Team item2) {
            return 0;
        }
    }
}
