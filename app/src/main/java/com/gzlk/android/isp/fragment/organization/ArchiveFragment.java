package com.gzlk.android.isp.fragment.organization;

import android.view.View;

import com.gzlk.android.isp.R;
import com.gzlk.android.isp.fragment.individual.DocumentNewFragment;
import com.gzlk.android.isp.helper.StringHelper;
import com.gzlk.android.isp.helper.TooltipHelper;
import com.gzlk.android.isp.model.BaseArchive;

/**
 * <b>功能描述：</b>组织档案列表<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/05/05 10:39 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/05/05 10:39 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class ArchiveFragment extends BaseOrganizationFragment {

    @Override
    public int getLayout() {
        return R.layout.fragment_organization_document;
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
    protected void onDelayRefreshComplete(@DelayType int type) {

    }

    /**
     * 设置新的组织id并查找该组织的档案列表
     */
    public void setNewQueryId(String queryId) {
        if (!StringHelper.isEmpty(mQueryId) && mQueryId.equals(queryId)) {
            return;
        }
        mQueryId = queryId;
    }

    public void openTooltipMenu(View view) {
        showTooltip(view, R.id.ui_tool_view_tooltip_menu_organization_document, true, TooltipHelper.TYPE_RIGHT, onClickListener);
    }

    private View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.ui_tool_popup_menu_organization_document_new:
                    // 新建组织档案
                    openActivity(DocumentNewFragment.class.getName(), format("%d,,%s", BaseArchive.Type.ORGANIZATION, mQueryId), true, true);
                    break;
                case R.id.ui_tool_popup_menu_organization_document_manage:
                    // 管理组织档案
                    break;
            }
        }
    };
}
