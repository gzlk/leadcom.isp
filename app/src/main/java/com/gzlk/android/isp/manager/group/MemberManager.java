package com.gzlk.android.isp.manager.group;

import com.gzlk.android.isp.api.listener.OnSingleRequestListener;
import com.gzlk.android.isp.api.org.MemberRequest;
import com.gzlk.android.isp.helper.StringHelper;
import com.gzlk.android.isp.manager.BaseManager;
import com.gzlk.android.isp.manager.Manageable;
import com.gzlk.android.isp.model.organization.Member;

/**
 * <b>功能描述：</b>组织成员管理<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/05/20 23:12 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/05/20 23:12 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class MemberManager extends BaseManager<Member> implements Manageable<Member> {

    public static MemberManager manager() {
        return new MemberManager();
    }

    @Override
    protected Class<Member> requestedType() {
        return Member.class;
    }

    @Override
    public void find(String byId) {
        Member member = dao.query(byId);
        if (null != member) {
            fireSingleManageListener(member);
        } else {
            MemberRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<Member>() {
                @Override
                public void onResponse(Member member, boolean success, String message) {
                    super.onResponse(member, success, message);
                    if (success) {
                        if (null != member && !StringHelper.isEmpty(member.getId())) {
                            dao.save(member);
                            fireSingleManageListener(member);
                        }
                    }
                }
            }).find(byId);
        }
    }
}
