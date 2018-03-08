package com.leadcom.android.isp.holder.activity;

import android.content.Context;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.hlk.hlklib.lib.inject.Click;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.inject.ViewUtility;
import com.hlk.hlklib.lib.view.CorneredView;
import com.hlk.hlklib.lib.view.NineRectangleGridImageView;
import com.leadcom.android.isp.R;
import com.leadcom.android.isp.etc.Utils;
import com.leadcom.android.isp.fragment.base.BaseFragment;
import com.leadcom.android.isp.holder.BaseViewHolder;
import com.leadcom.android.isp.model.activity.Activity;
import com.leadcom.android.isp.model.activity.topic.AppTopic;
import com.leadcom.android.isp.model.common.SimpleClickableItem;
import com.leadcom.android.isp.model.organization.Invitation;
import com.netease.nimlib.sdk.msg.model.RecentContact;
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
    @ViewId(R.id.ui_holder_view_activity_item_unread)
    private TextView unreadNum;
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

    public void showContent(SimpleClickableItem item) {
        String text = item.getSource();
        String[] strings = text.split("\\|", -1);
        iconText.setText(strings[1]);
        titleView.setText(strings[2]);
        timeView.setText(null);
        descView.setText(strings[3]);
        descView.setVisibility(isEmpty(strings[3]) ? View.GONE : View.VISIBLE);
        headers.setVisibility(View.GONE);
        iconText.setVisibility(View.VISIBLE);
        //flagView.setVisibility(strings[3].charAt(0) == '0' ? View.GONE : (text.charAt(0) == '3' ? View.GONE : View.VISIBLE));
        flagView.setVisibility(item.getAdditionalNum() > 0 ? View.VISIBLE : View.GONE);
        unreadNum.setText(fragment().formatUnread(item.getAdditionalNum()));
        iconContainer.setBackground(getColor(text.charAt(0) == '2' ? R.color.color_faaa2d : R.color.color_fe4848));
    }

    public void showContent(Invitation invitation) {
        String actImg = invitation.getActImg();
        List<String> img = new ArrayList<>();
        img.add(actImg);
        headers.setAdapter(adapter);
        headers.setImagesData(img);
        timeView.setText(fragment().formatTimeAgo(invitation.getCreateTime()));
        headers.setVisibility(View.VISIBLE);
        iconText.setVisibility(View.GONE);
        iconContainer.setBackground(getColor(R.color.textColorHintLight));
        titleView.setText(invitation.getActName());
        descView.setText(format("%s邀请您加入活动", invitation.getInviterName()));
    }

    public void showContent(Activity activity) {
        List<String> img = new ArrayList<>();
        if (activity.getHeadPhotoList().size() < 1) {
            img.add(activity.getCover());
        } else {
            img.addAll(activity.getHeadPhotoList());
        }
        headers.setAdapter(adapter);
        headers.setImagesData(img);
        flagView.setVisibility(activity.getUnreadNum() > 0 ? View.VISIBLE : View.GONE);
        unreadNum.setText(fragment().formatUnread(activity.getUnreadNum()));
        timeView.setText(fragment().formatTimeAgo(activity.getBeginDate()));
        //headers.setVisibility(hasImage ? View.VISIBLE : View.GONE);
        headers.setVisibility(View.VISIBLE);
        iconText.setVisibility(View.GONE);
        iconContainer.setBackground(getColor(R.color.textColorHintLight));
        titleView.setText(activity.getTitle());
        descView.setText(activity.getIntro());
    }

    public void showContent(AppTopic topic) {
        headers.setAdapter(adapter);
        headers.setImagesData(topic.getHeadPhotoList());
        flagView.setVisibility(topic.getUnReadNum() > 0 ? View.VISIBLE : View.GONE);
        unreadNum.setText(fragment().formatUnread(topic.getUnReadNum()));
        timeView.setText(fragment().formatTimeAgo(topic.getCreateDate()));
        headers.setVisibility(View.VISIBLE);
        iconText.setVisibility(View.GONE);
        iconContainer.setBackground(getColor(R.color.textColorHintLight));
        titleView.setText(topic.getTitle());
        descView.setText(topic.getAccessToken());
    }

    public void showContent(RecentContact contact) {
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
            String url = (isEmpty(s) || (!Utils.isUrl(s) && !Utils.isLocalPath(s))) ? ("drawable://" + R.mipmap.img_default_user_header) : s;
            ImageLoader.getInstance().displayImage(url, imageView, new ImageSize(size, size));
        }

        //重写该方法自定义生成ImageView方式，用于九宫格头像中的一个个图片控件，可以设置ScaleType等属性
        @Override
        protected ImageView generateImageView(Context context) {
            return super.generateImageView(context);
        }
    };
}
