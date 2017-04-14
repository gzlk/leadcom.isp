package com.gzlk.android.isp.fragment.individual;

import android.os.Bundle;

import com.gzlk.android.isp.fragment.base.BaseSwipeRefreshSupportFragment;

/**
 * <b>功能描述：</b><br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/04/14 08:48 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/04/14 08:48 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public abstract class BaseIndividualFragment extends BaseSwipeRefreshSupportFragment {

    @Override
    protected boolean shouldSetDefaultTitleEvents() {
        return false;
    }

    @Override
    protected void getParamsFromBundle(Bundle bundle) {

    }

    @Override
    protected void saveParamsToBundle(Bundle bundle) {

    }

}
