package com.leadcom.android.isp.fragment.talk;

import android.os.Bundle;

import com.leadcom.android.isp.R;
import com.leadcom.android.isp.fragment.base.BaseFragment;
import com.leadcom.android.isp.fragment.base.BaseSwipeRefreshSupportFragment;

/**
 * <b>功能描述：</b>群成员列表<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2018/03/29 16:13 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2018/03/29 16:13 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */
public class TalkTeamMembersFragment extends BaseSwipeRefreshSupportFragment {

    public static TalkTeamMembersFragment newInstance(String params) {
        TalkTeamMembersFragment ttmf = new TalkTeamMembersFragment();
        Bundle bundle = new Bundle();
        // 组织的tid
        bundle.putString(PARAM_QUERY_ID, params);
        ttmf.setArguments(bundle);
        return ttmf;
    }

    public static void open(BaseFragment fragment, String tid) {
        fragment.openActivity(TalkTeamMembersFragment.class.getName(), tid, REQUEST_MEMBER, true, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        enableSwipe(false);
        isLoadingComplete(true);
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
    protected void onDelayRefreshComplete(int type) {

    }

    @Override
    public void doingInResume() {

    }

    @Override
    protected boolean shouldSetDefaultTitleEvents() {
        return false;
    }

    @Override
    protected void destroyView() {

    }
}
