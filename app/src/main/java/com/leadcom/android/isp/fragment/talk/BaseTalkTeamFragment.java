package com.leadcom.android.isp.fragment.talk;

import com.leadcom.android.isp.R;
import com.leadcom.android.isp.api.listener.OnSingleRequestListener;
import com.leadcom.android.isp.api.org.MemberRequest;
import com.leadcom.android.isp.fragment.base.BaseSwipeRefreshSupportFragment;
import com.leadcom.android.isp.helper.ToastHelper;
import com.leadcom.android.isp.helper.popup.DeleteDialogHelper;
import com.leadcom.android.isp.helper.popup.DialogHelper;
import com.leadcom.android.isp.model.organization.Member;
import com.leadcom.android.isp.model.organization.SubMember;
import com.netease.nim.uikit.impl.cache.TeamDataCache;
import com.netease.nimlib.sdk.team.model.TeamMember;

import java.util.ArrayList;

/**
 * <b>功能描述：</b><br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2018/04/03 14:48 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2018/04/03 14:48 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */
public abstract class BaseTalkTeamFragment extends BaseSwipeRefreshSupportFragment {

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
    protected boolean shouldSetDefaultTitleEvents() {
        return true;
    }

    @Override
    protected void destroyView() {

    }

    protected void prepareAddUserToTeam(ArrayList<SubMember> members) {
        ArrayList<String> ids = new ArrayList<>();
        String name = "";
        int count = 0;
        if (null != members && members.size() > 0) {
            for (SubMember member : members) {
                TeamMember tm = TeamDataCache.getInstance().getTeamMember(mQueryId, member.getUserId());
                if (null == tm || !tm.isInTeam()) {
                    // 成员中不存在用户时才添加
                    ids.add(member.getUserId());
                    if (count < 3) {
                        name += (isEmpty(name) ? "" : "、") + member.getUserName();
                    }
                    count++;
                }
            }
            name = "[" + name + "]";
            if (ids.size() > 1) {
                name += "等";
            }
        }
        if (ids.size() > 0) {
            warningAddNewUser(ids, name);
        } else {
            ToastHelper.make().showMsg(R.string.ui_team_talk_team_member_add_no_new_member);
        }
    }

    private void warningAddNewUser(final ArrayList<String> userIds, String names) {
        DeleteDialogHelper.helper().init(this).setOnDialogConfirmListener(new DialogHelper.OnDialogConfirmListener() {
            @Override
            public boolean onConfirm() {
                addNewUser(userIds);
                return true;
            }
        }).setTitleText(getString(R.string.ui_team_talk_team_member_add_dialog_title, names, userIds.size())).setConfirmText(R.string.ui_base_text_confirm).show();
    }

    private void addNewUser(ArrayList<String> userIds) {
        MemberRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<Member>() {
            @Override
            public void onResponse(Member member, boolean success, String message) {
                super.onResponse(member, success, message);
                onAddNewUserComplete(success);
            }
        }).addTeamMember(mQueryId, userIds);
    }

    protected void onAddNewUserComplete(boolean success) {
    }
}
