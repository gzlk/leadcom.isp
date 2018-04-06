package com.leadcom.android.isp.fragment.activity.vote;

import android.os.Bundle;

import com.leadcom.android.isp.R;
import com.leadcom.android.isp.api.activity.AppVoteRequest;
import com.leadcom.android.isp.api.listener.OnSingleRequestListener;
import com.leadcom.android.isp.cache.Cache;
import com.leadcom.android.isp.fragment.base.BaseSwipeRefreshSupportFragment;
import com.leadcom.android.isp.helper.popup.DeleteDialogHelper;
import com.leadcom.android.isp.helper.popup.DialogHelper;
import com.leadcom.android.isp.model.activity.vote.AppVote;
import com.netease.nim.uikit.impl.cache.TeamDataCache;
import com.netease.nimlib.sdk.team.constant.TeamMemberType;
import com.netease.nimlib.sdk.team.model.TeamMember;


/**
 * <b>功能描述：</b><br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2018/04/05 23:01 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>version: 1.0.0 <br />
 * <b>修改时间：</b>2017/10/04 18:50 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */
public abstract class BaseVoteFragment extends BaseSwipeRefreshSupportFragment {

    private static final String PARAM_OWNER = "bvf_is_self_owner";

    protected static Bundle getBundle(String tid) {
        Bundle bundle = new Bundle();
        // tid
        bundle.putString(PARAM_QUERY_ID, tid);
        return bundle;
    }

    protected boolean isSelfOwner = false;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        TeamMember member = TeamDataCache.getInstance().getTeamMember(mQueryId, Cache.cache().userId);
        isSelfOwner = member.getType() == TeamMemberType.Owner;
    }

    @Override
    protected void getParamsFromBundle(Bundle bundle) {
        super.getParamsFromBundle(bundle);
        bundle.putBoolean(PARAM_OWNER, isSelfOwner);
    }

    @Override
    protected void saveParamsToBundle(Bundle bundle) {
        super.saveParamsToBundle(bundle);
        bundle.putBoolean(PARAM_OWNER, isSelfOwner);
    }

    protected void warningDelete(final String voteId) {
        DeleteDialogHelper.helper().init(this).setOnDialogConfirmListener(new DialogHelper.OnDialogConfirmListener() {
            @Override
            public boolean onConfirm() {
                deleteVote(voteId);
                return true;
            }
        }).setTitleText(R.string.ui_activity_vote_details_delete).show();
    }

    private void deleteVote(final String voteId) {
        AppVoteRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<AppVote>() {
            @Override
            public void onResponse(AppVote appVote, boolean success, String message) {
                super.onResponse(appVote, success, message);
                onDeleteVoteComplete(success, voteId);
            }
        }).deleteTeamVote(mQueryId);
    }

    protected void onDeleteVoteComplete(boolean success, String voteId) {
    }

    @Override
    protected boolean shouldSetDefaultTitleEvents() {
        return true;
    }

    @Override
    protected void destroyView() {

    }

    @Override
    protected void onDelayRefreshComplete(@DelayType int type) {

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

}
