package com.gzlk.android.isp.fragment.main;

import android.os.Bundle;

import com.gzlk.android.isp.R;
import com.gzlk.android.isp.activity.MainActivity;

/**
 * <b>功能描述：</b>个人<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/04/13 10:03 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/04/13 10:03 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class IndividualFragment extends BaseMainFragment {

    @Override
    protected void onDelayRefreshComplete(@DelayType int type) {

    }

    @Override
    public int getLayout() {
        return R.layout.fragment_main_individual;
    }

    @Override
    protected void getParamsFromBundle(Bundle bundle) {

    }

    @Override
    public void doingInResume() {
        //transparentToolbar();
    }

    @Override
    protected boolean supportDefaultTitle() {
        return false;
    }

    @Override
    protected void saveParamsToBundle(Bundle bundle) {

    }

    @Override
    protected void destroyView() {

    }

    private void transparentToolbar() {
        MainActivity main = (MainActivity) Activity();
        main.appBarLayout.getBackground().setAlpha(0);
    }
}
