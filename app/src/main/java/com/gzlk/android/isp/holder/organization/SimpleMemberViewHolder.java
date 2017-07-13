package com.gzlk.android.isp.holder.organization;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.gzlk.android.isp.R;
import com.gzlk.android.isp.fragment.base.BaseFragment;
import com.gzlk.android.isp.helper.StringHelper;
import com.gzlk.android.isp.holder.common.SimpleClickableViewHolder;
import com.gzlk.android.isp.lib.view.ImageDisplayer;
import com.gzlk.android.isp.model.Dao;
import com.gzlk.android.isp.model.activity.Activity;
import com.gzlk.android.isp.model.common.SimpleClickableItem;
import com.gzlk.android.isp.model.organization.Member;
import com.gzlk.android.isp.model.organization.Organization;
import com.hlk.hlklib.lib.inject.ViewId;
import com.litesuits.orm.db.assit.QueryBuilder;
import com.netease.nim.uikit.NimUIKit;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.team.TeamService;
import com.netease.nimlib.sdk.team.model.TeamMember;
import com.netease.nimlib.sdk.uinfo.UserInfoProvider;

import java.util.List;

/**
 * <b>功能描述：</b>群成员简略显示<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/05/08 10:47 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/05/08 10:47 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class SimpleMemberViewHolder extends SimpleClickableViewHolder {

    @ViewId(R.id.ui_holder_view_simple_member_view_headers)
    private LinearLayout headerContainer;

    private int imageSize;

    public SimpleMemberViewHolder(View itemView, BaseFragment fragment) {
        super(itemView, fragment);
        imageSize = getDimension(R.dimen.ui_static_dp_20);
    }

    @Override
    public void showContent(SimpleClickableItem item) {
        super.showContent(item);
    }

    /**
     * 显示组织成员头像列表
     */
    public void showContent(Organization org) {
        List<Member> members = getMembers(org.getId());
        String items = StringHelper.getStringArray(R.array.ui_organization_details_items)[1];
        int size = null == members ? 0 : members.size();
        showContent(new SimpleClickableItem(format(items, size)));
        showHeaders(members);
    }

    private List<Member> getMembers(String orgId) {
        QueryBuilder<Member> builder = new QueryBuilder<>(Member.class)
                .whereEquals(Organization.Field.GroupId, orgId)
                .whereAppendAnd()
                .whereAppend(Organization.Field.SquadId + " IS NULL");
        return new Dao<>(Member.class).query(builder);
    }

    /**
     * 显示活动的成员头像
     */
    public void showContent(Activity activity) {
        headerContainer.removeAllViews();
        String items = StringHelper.getStringArray(R.array.ui_activity_property_items)[1];
        List<Member> members = Activity.getMembers(activity.getId());
        if (null != members && members.size() > 0) {
            items = format(items, members.size());
            // 重新显示成员数量
            showContent(new SimpleClickableItem(items));
            showHeaders(members);
        } else {
            // 本地没有数据是只获取成员数量即可
            fetchingActivityMembers(activity.getTid());
        }
    }

    private void fetchingActivityMembers(String tid) {
        // 该操作有可能只是从本地数据库读取缓存数据，也有可能会从服务器同步新的数据，因此耗时可能会比较长。
        NIMClient.getService(TeamService.class).queryMemberList(tid)
                .setCallback(new RequestCallback<List<TeamMember>>() {
                    @Override
                    public void onSuccess(List<TeamMember> members) {
                        showTeamMembers(members);
                    }

                    @Override
                    public void onFailed(int i) {

                    }

                    @Override
                    public void onException(Throwable throwable) {

                    }
                });
    }

    private void showTeamMembers(List<TeamMember> members) {
        String items = StringHelper.getStringArray(R.array.ui_activity_property_items)[1];
        items = format(items, null == members ? 0 : members.size());
        // 重新显示成员数量
        showContent(new SimpleClickableItem(items));
        if (null != members) {
            int i = 0;
            for (TeamMember member : members) {
                UserInfoProvider.UserInfo userInfo = NimUIKit.getUserInfoProvider().getUserInfo(member.getAccount());
                String header = null != userInfo ? userInfo.getAvatar() : "";
                ImageDisplayer displayer = (ImageDisplayer) LayoutInflater.from(headerContainer.getContext())
                        .inflate(R.layout.tool_view_small_user_header, headerContainer, false);
                displayer.displayImage(header, imageSize, false, false);
                displayer.addOnImageClickListener(onImageClickListener);
                headerContainer.addView(displayer);
                if (i >= 9) {
                    break;
                }
                i++;
            }
        }
    }

    private void showHeaders(List<Member> members) {
        int i = 0;
        for (Member member : members) {
            ImageDisplayer displayer = (ImageDisplayer) LayoutInflater.from(headerContainer.getContext())
                    .inflate(R.layout.tool_view_small_user_header, headerContainer, false);
            displayer.displayImage(member.getHeadPhoto(), imageSize, false, false);
            displayer.addOnImageClickListener(onImageClickListener);
            headerContainer.addView(displayer);
            if (i >= 9) {
                break;
            }
            i++;
        }
    }

    private void showHeaders(int size) {
        String items = StringHelper.getStringArray(R.array.ui_activity_property_items)[1];
        items = format(items, size);
        // 重新显示成员数量
        showContent(new SimpleClickableItem(items));
        for (int i = 0; i < size; i++) {
            ImageDisplayer displayer = (ImageDisplayer) LayoutInflater.from(headerContainer.getContext())
                    .inflate(R.layout.tool_view_small_user_header, headerContainer, false);
            displayer.addOnImageClickListener(onImageClickListener);
            headerContainer.addView(displayer);
            if (i >= 9) {
                break;
            }
        }
    }

    private ImageDisplayer.OnImageClickListener onImageClickListener = new ImageDisplayer.OnImageClickListener() {
        @Override
        public void onImageClick(String url) {
            if (null != mOnViewHolderClickListener) {
                mOnViewHolderClickListener.onClick(getAdapterPosition());
            }
        }
    };
}
