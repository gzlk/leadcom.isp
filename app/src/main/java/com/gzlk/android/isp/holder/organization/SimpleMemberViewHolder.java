package com.gzlk.android.isp.holder.organization;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.gzlk.android.isp.R;
import com.gzlk.android.isp.fragment.base.BaseFragment;
import com.gzlk.android.isp.helper.StringHelper;
import com.gzlk.android.isp.holder.common.SimpleClickableViewHolder;
import com.gzlk.android.isp.lib.view.ImageDisplayer;
import com.gzlk.android.isp.model.activity.Activity;
import com.gzlk.android.isp.model.common.SimpleClickableItem;
import com.gzlk.android.isp.model.organization.Member;
import com.gzlk.android.isp.model.organization.Organization;
import com.hlk.hlklib.lib.inject.ViewId;

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
        List<Member> members = Member.getMembersOfGroupOrSquad(org.getId(), "");
        String items = StringHelper.getStringArray(R.array.ui_organization_details_items)[1];
        int size = null == members ? 0 : members.size();
        showContent(new SimpleClickableItem(format(items, size)));
        showHeaders(members);
    }

    /**
     * 显示活动的成员头像
     */
    public void showContent(Activity activity) {
        headerContainer.removeAllViews();
        String items = StringHelper.getStringArray(R.array.ui_activity_property_items)[1];
        List<Member> members = activity.getActMemberList();
        items = format(items, null == members ? 0 : members.size());
        // 重新显示成员数量
        showContent(new SimpleClickableItem(items));
        if (null != members) {
            showHeaders(members);
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

    private ImageDisplayer.OnImageClickListener onImageClickListener = new ImageDisplayer.OnImageClickListener() {
        @Override
        public void onImageClick(String url) {
            if (null != mOnViewHolderClickListener) {
                mOnViewHolderClickListener.onClick(getAdapterPosition());
            }
        }
    };
}
