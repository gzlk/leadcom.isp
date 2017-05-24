package com.gzlk.android.isp.fragment.organization.archive;

import com.gzlk.android.isp.R;
import com.gzlk.android.isp.fragment.base.BaseTransparentSupportFragment;

/**
 * <b>功能描述：</b>未存档的组织档案列表<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/05/24 21:40 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/05/24 21:40 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class ArchivingFragment extends BaseTransparentSupportFragment {

    @Override
    public int getLayout() {
        return R.layout.fragment_archive_archiving;
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
