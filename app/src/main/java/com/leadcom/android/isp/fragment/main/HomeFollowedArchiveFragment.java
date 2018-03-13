package com.leadcom.android.isp.fragment.main;

import com.leadcom.android.isp.fragment.base.BaseCmtLikeColFragment;
import com.leadcom.android.isp.model.Model;


/**
 * <b>功能描述：</b>首页 - 关注（关注组织里的个人公开档案列表）<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2018/03/13 20:44 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>version: 1.0.0 <br />
 * <b>修改时间：</b>2017/10/04 18:50 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */
public class HomeFollowedArchiveFragment extends BaseCmtLikeColFragment {

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
    protected void onLikeComplete(boolean success, Model model) {

    }

    @Override
    protected void onCollectComplete(boolean success, Model model) {

    }
}
