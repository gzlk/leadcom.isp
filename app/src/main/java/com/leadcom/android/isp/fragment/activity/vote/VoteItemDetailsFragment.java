package com.leadcom.android.isp.fragment.activity.vote;

import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import com.google.android.flexbox.FlexboxLayout;
import com.leadcom.android.isp.R;
import com.leadcom.android.isp.api.activity.AppVoteRequest;
import com.leadcom.android.isp.api.listener.OnSingleRequestListener;
import com.leadcom.android.isp.cache.Cache;
import com.leadcom.android.isp.fragment.base.BaseFragment;
import com.leadcom.android.isp.fragment.base.BaseSwipeRefreshSupportFragment;
import com.leadcom.android.isp.helper.ToastHelper;
import com.leadcom.android.isp.holder.activity.VoteItemUserViewHolder;
import com.leadcom.android.isp.model.activity.vote.AppVote;
import com.leadcom.android.isp.model.activity.vote.AppVoteItem;
import com.leadcom.android.isp.model.activity.vote.AppVoteRecord;
import com.hlk.hlklib.lib.inject.ViewId;

/**
 * <b>功能描述：</b>投票选项的投票详情<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/08/23 13:46 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/08/23 13:46 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class VoteItemDetailsFragment extends BaseSwipeRefreshSupportFragment {

    private static final String PARAM_ITEM_ID = "vdf_item_id";

    public static VoteItemDetailsFragment newInstance(Bundle bundle) {
        VoteItemDetailsFragment vdf = new VoteItemDetailsFragment();
        vdf.setArguments(bundle);
        return vdf;
    }

    public static void open(BaseFragment fragment, String voteId, String voteItemId) {
        Bundle bundle = new Bundle();
        // 投票的id
        bundle.putString(PARAM_QUERY_ID, voteId);
        // 投票选项的id
        bundle.putString(PARAM_ITEM_ID, voteItemId);
        fragment.openActivity(VoteItemDetailsFragment.class.getName(), bundle, true, false);
    }

    @ViewId(R.id.ui_activity_vote_item_title)
    private TextView voteOption;
    @ViewId(R.id.ui_activity_vote_item_count)
    private TextView voteCount;
    @ViewId(R.id.ui_activity_vote_item_users)
    private FlexboxLayout headersLayout;

    private String voteItemId;

    private AppVote mAppVote;
    private AppVoteItem mAppVoteItem;

    @Override
    public int getLayout() {
        return R.layout.fragment_activity_vote_item_details;
    }

    @Override
    protected void getParamsFromBundle(Bundle bundle) {
        super.getParamsFromBundle(bundle);
        voteItemId = bundle.getString(PARAM_ITEM_ID, "");
    }

    @Override
    public void doingInResume() {
        setCustomTitle(R.string.ui_activity_vote_item_details_fragment_title);
        if (null == mAppVote) {
            loadingVoteDetails();
        }
    }

    @Override
    protected boolean shouldSetDefaultTitleEvents() {
        return true;
    }

    @Override
    protected void saveParamsToBundle(Bundle bundle) {
        super.saveParamsToBundle(bundle);
        bundle.putString(PARAM_ITEM_ID, voteItemId);
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
    protected void destroyView() {

    }

    @Override
    protected void onDelayRefreshComplete(@DelayType int type) {

    }

    private void loadingVoteDetails() {
        showImageHandlingDialog(R.string.ui_activity_vote_details_loading);
        AppVoteRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<AppVote>() {
            @Override
            public void onResponse(AppVote appVote, boolean success, String message) {
                super.onResponse(appVote, success, message);
                hideImageHandlingDialog();
                if (success) {
                    mAppVote = appVote;
                    mAppVote.setCommVoteItemList(commVoteItemList);
                    mAppVote.setCommVoteRecordList(commVoteRecordList);
                    for (AppVoteRecord record : mAppVote.getCommVoteRecordList()) {
                        if (record.getUserId().equals(Cache.cache().userId)) {
                            mAppVote.setActVote(record);
                            break;
                        }
                    }
                    if (voteItemId.equals(AppVoteItem.REFUSED_ID)) {
                        mAppVoteItem = AppVoteItem.getRefuseItem();
                    } else {
                        for (AppVoteItem item : mAppVote.getActVoteItemList()) {
                            if (item.getId().equals(voteItemId)) {
                                mAppVoteItem = item;
                                break;
                            }
                        }
                    }
                    showDetails();
                } else {
                    ToastHelper.make().showMsg(message);
                    finish();
                }
            }
        }).findTeamVote(mQueryId, AppVoteRequest.FIND_ALL, remotePageNumber);
    }

    private void showDetails() {
        voteOption.setText(Html.fromHtml(getString(R.string.ui_activity_vote_item_title_text, mAppVoteItem.getContent())));
        showVotedHeaders(voteItemId, mAppVote);
    }

    private void showVotedHeaders(String voteItemId, AppVote vote) {
        headersLayout.removeAllViews();
        if (null != vote.getActVoteList()) {
            for (AppVoteRecord record : vote.getActVoteList()) {
                if (voteItemId.equals(AppVoteItem.REFUSED_ID)) {
                    if (record.getStatus() == AppVote.Status.REFUSED) {
                        addHeader(record);
                    }
                } else {
                    if (record.getStatus() == AppVote.Status.HAS_VOTED) {
                        if (null != record.getItemIdList() && record.getItemIdList().contains(voteItemId)) {
                            addHeader(record);
                        }
                    }
                }
            }
        }
        voteCount.setText(getString(R.string.ui_activity_vote_item_count, voted));
    }

    private int voted = 0;

    private void addHeader(AppVoteRecord record) {
        voted++;
        View view = View.inflate(voteOption.getContext(), R.layout.holder_view_activity_vote_item_details_user, null);
        headersLayout.addView(view);
        VoteItemUserViewHolder holder = new VoteItemUserViewHolder(view, this);
        holder.showContent(record);
    }

}
