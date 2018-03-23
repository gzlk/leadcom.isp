package com.leadcom.android.isp.fragment.archive;

import android.os.Bundle;
import android.view.View;

import com.leadcom.android.isp.R;
import com.leadcom.android.isp.application.App;
import com.leadcom.android.isp.fragment.base.BaseFragment;
import com.leadcom.android.isp.fragment.base.BaseTransparentSupportFragment;
import com.hlk.hlklib.lib.inject.Click;

/**
 * <b>功能描述：</b>新建档案方式选择器<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/10/04 19:14 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>version: 1.0.0 <br />
 * <b>修改时间：</b>2017/10/04 19:14 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */
public class ArchiveCreateSelectorFragment extends BaseTransparentSupportFragment {

    public static ArchiveCreateSelectorFragment newInstance(String params) {
        ArchiveCreateSelectorFragment acsf = new ArchiveCreateSelectorFragment();
        Bundle bundle = new Bundle();
        // 传过来的组织id，也即要创建的档案所属的组织，为empty时创建的是个人档案
        bundle.putString(PARAM_QUERY_ID, params);
        acsf.setArguments(bundle);
        return acsf;
    }

    public static void open(BaseFragment fragment, String groupId) {
        fragment.openActivity(ArchiveCreateSelectorFragment.class.getName(), groupId, REQUEST_SELECT, false, false, true);
    }

    @Override
    public int getLayout() {
        return R.layout.fragment_archive_creator_selector;
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

    @Click({R.id.ui_archive_creator_selector_cancel,
            R.id.ui_archive_creator_selector_normal,
            R.id.ui_archive_creator_selector_rich_text,
            R.id.ui_archive_creator_selector_attachment,
            R.id.ui_archive_creator_selector_moment})
    private void elementClick(View view) {
        view.startAnimation(App.clickAnimation());
        switch (view.getId()) {
            case R.id.ui_archive_creator_selector_cancel:
                finish();
                break;
            case R.id.ui_archive_creator_selector_normal:
                break;
            case R.id.ui_archive_creator_selector_rich_text:
                resultData(ArchiveEditorFragment.MULTIMEDIA);
                break;
            case R.id.ui_archive_creator_selector_attachment:
                resultData(ArchiveEditorFragment.ATTACHABLE);
                break;
            case R.id.ui_archive_creator_selector_moment:
                resultData(ArchiveEditorFragment.MOMENT);
                break;
        }
    }
}
