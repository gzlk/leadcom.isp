package com.gzlk.android.isp.holder.activity;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.gzlk.android.isp.R;
import com.gzlk.android.isp.etc.Utils;
import com.gzlk.android.isp.fragment.base.BaseFragment;
import com.gzlk.android.isp.helper.StringHelper;
import com.gzlk.android.isp.holder.BaseViewHolder;
import com.gzlk.android.isp.model.activity.Activity;
import com.gzlk.android.isp.model.organization.Invitation;
import com.gzlk.android.isp.model.organization.Member;
import com.hlk.hlklib.lib.inject.Click;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.inject.ViewUtility;
import com.hlk.hlklib.lib.view.CorneredView;
import com.hlk.hlklib.lib.view.NineRectangleGridImageView;
import com.netease.nim.uikit.NimUIKit;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.team.TeamService;
import com.netease.nimlib.sdk.team.model.TeamMember;
import com.netease.nimlib.sdk.uinfo.UserInfoProvider;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.ImageSize;

import java.util.ArrayList;
import java.util.List;

/**
 * <b>功能描述：</b>活动<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/05/26 23:01 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/05/26 23:01 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class ActivityViewHolder extends BaseViewHolder {

    @ViewId(R.id.ui_holder_view_activity_item_container)
    private CorneredView rootContainer;
    @ViewId(R.id.ui_holder_view_activity_item_icon_container)
    private CorneredView iconContainer;
    @ViewId(R.id.ui_holder_view_activity_item_headers)
    private NineRectangleGridImageView<String> headers;
    @ViewId(R.id.ui_holder_view_activity_item_icon_text)
    private TextView iconText;
    @ViewId(R.id.ui_holder_view_activity_item_flag)
    private CorneredView flagView;
    @ViewId(R.id.ui_holder_view_activity_item_title)
    private TextView titleView;
    @ViewId(R.id.ui_holder_view_activity_item_time)
    private TextView timeView;
    @ViewId(R.id.ui_holder_view_activity_item_description)
    private TextView descView;

    public ActivityViewHolder(View itemView, BaseFragment fragment) {
        super(itemView, fragment);
        ViewUtility.bind(this, itemView);
        flagView.setVisibility(View.GONE);
    }

    public void showContent(String text) {
        String[] strings = text.split("\\|", -1);
        iconText.setText(strings[1]);
        titleView.setText(strings[2]);
        timeView.setText(null);
        descView.setText(strings[3]);
        headers.setVisibility(View.GONE);
        iconText.setVisibility(View.VISIBLE);
        flagView.setVisibility(strings[3].charAt(0) == '0' ? View.GONE : View.VISIBLE);
        iconContainer.setBackground(getColor(text.charAt(0) == '1' ? R.color.color_fe4848 : R.color.color_faaa2d));
    }

    public void showContent(Invitation invitation) {
        String actImg = invitation.getActImg();
        List<String> img = new ArrayList<>();
        img.add(actImg);
        headers.setAdapter(adapter);
        headers.setImagesData(img);
        timeView.setText(Utils.formatTimeAgo(StringHelper.getString(R.string.ui_base_text_date_time_format), invitation.getCreateTime()));
        headers.setVisibility(View.VISIBLE);
        iconText.setVisibility(View.GONE);
        iconContainer.setBackground(getColor(R.color.textColorHintLight));
        titleView.setText(invitation.getActName());
        descView.setText(format("%s邀请您加入活动", invitation.getInviterName()));
    }

    public void showContent(Activity activity) {
        String cover = activity.getImg();
        List<String> img = new ArrayList<>();
//        if (hasImage) {
//            img.add(activity.getImg());
//        } else {
//
//        }
        List<Member> members = Activity.getMembers(activity.getId());
        if (null != members && members.size() > 0) {
            for (Member member : members) {
                String head = member.getHeadPhoto();
                if (isEmpty(head)) {
                    head = isEmpty(cover) ? "drawable://" + R.mipmap.img_default_user_header : cover;
                }
                img.add(head);
            }
        } else {
            fetchingActivityMembers(activity.getTid(), cover);
        }
        headers.setAdapter(adapter);
        headers.setImagesData(img);
        flagView.setVisibility(activity.getUnreadNum() > 0 ? View.VISIBLE : View.GONE);
        timeView.setText(Utils.formatTimeAgo(StringHelper.getString(R.string.ui_base_text_date_time_format), activity.getBeginDate()));
        //headers.setVisibility(hasImage ? View.VISIBLE : View.GONE);
        headers.setVisibility(View.VISIBLE);
        iconText.setVisibility(View.GONE);
        iconContainer.setBackground(getColor(R.color.textColorHintLight));
        titleView.setText(activity.getTitle());
        descView.setText(activity.getContent());
    }

    private void showHeaders(List<String> list) {
        headers.setImagesData(list);
    }

    private void fetchingActivityMembers(String tid, final String cover) {
        // 该操作有可能只是从本地数据库读取缓存数据，也有可能会从服务器同步新的数据，因此耗时可能会比较长。
        NIMClient.getService(TeamService.class).queryMemberList(tid)
                .setCallback(new RequestCallback<List<TeamMember>>() {
                    @Override
                    public void onSuccess(List<TeamMember> members) {
                        //showTeamMembers(members);
                        List<String> list = new ArrayList<>();
                        for (TeamMember member : members) {
                            UserInfoProvider.UserInfo userInfo = NimUIKit.getUserInfoProvider().getUserInfo(member.getAccount());
                            String header = null != userInfo ? userInfo.getAvatar() : "";
                            if (isEmpty(header)) {
                                header = isEmpty(cover) ? "drawable://" + R.mipmap.img_default_user_header : cover;
                            }
                            list.add(header);
                        }
                        showHeaders(list);
                    }

                    @Override
                    public void onFailed(int i) {

                    }

                    @Override
                    public void onException(Throwable throwable) {

                    }
                });
    }

    @Click({R.id.ui_holder_view_activity_item_container})
    private void click(View view) {
        if (null != mOnViewHolderClickListener) {
            mOnViewHolderClickListener.onClick(getAdapterPosition());
        }
    }

    private NineRectangleGridImageView.NineRectangleGridImageViewAdapter<String> adapter = new NineRectangleGridImageView.NineRectangleGridImageViewAdapter<String>() {

        @Override
        protected void onDisplayImage(Context context, ImageView imageView, String s) {
            int size = getDimension(R.dimen.ui_static_dp_55);
            ImageLoader.getInstance().displayImage(s, imageView, new ImageSize(size, size));
        }

        //重写该方法自定义生成ImageView方式，用于九宫格头像中的一个个图片控件，可以设置ScaleType等属性
        @Override
        protected ImageView generateImageView(Context context) {
            return super.generateImageView(context);
        }
    };
}
