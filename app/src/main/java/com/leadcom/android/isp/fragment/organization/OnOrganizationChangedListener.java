package com.leadcom.android.isp.fragment.organization;

import com.leadcom.android.isp.model.organization.Organization;

/**
 * <b>功能描述：</b>组织列表中的滑动焦点改变事件<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/05/26 23:54 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/05/26 23:54 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public interface OnOrganizationChangedListener {
    void onChanged(Organization entity);
}
