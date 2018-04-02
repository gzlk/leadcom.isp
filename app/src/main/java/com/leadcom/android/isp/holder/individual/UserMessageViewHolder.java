package com.leadcom.android.isp.holder.individual;

import android.view.View;
import android.widget.TextView;

import com.leadcom.android.isp.R;
import com.leadcom.android.isp.fragment.base.BaseFragment;
import com.leadcom.android.isp.holder.BaseViewHolder;
import com.leadcom.android.isp.lib.view.ImageDisplayer;
import com.leadcom.android.isp.model.user.Moment;
import com.leadcom.android.isp.model.user.UserMessage;
import com.hlk.hlklib.lib.inject.Click;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.inject.ViewUtility;
import com.hlk.hlklib.lib.view.CustomTextView;

/**
 * <b>功能描述：</b><br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/10/19 13:08 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/10/19 13:08 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class UserMessageViewHolder extends BaseViewHolder {

    @ViewId(R.id.ui_holder_view_individual_user_message_header)
    private ImageDisplayer headerView;
    @ViewId(R.id.ui_holder_view_individual_user_message_name)
    private TextView nameView;
    @ViewId(R.id.ui_holder_view_individual_user_message_content)
    private CustomTextView contentView;
    @ViewId(R.id.ui_holder_view_individual_user_message_time)
    private TextView timeView;
    @ViewId(R.id.ui_holder_view_individual_user_message_image)
    private ImageDisplayer imageView;
    @ViewId(R.id.ui_holder_view_individual_user_message_info)
    private View infoView;

    private int imageSize;

    public UserMessageViewHolder(View itemView, BaseFragment fragment) {
        super(itemView, fragment);
        ViewUtility.bind(this, itemView);
        imageSize = getDimension(R.dimen.ui_base_user_header_image_size_big);
        headerView.addOnImageClickListener(new ImageDisplayer.OnImageClickListener() {
            @Override
            public void onImageClick(ImageDisplayer displayer, String url) {
                // 打开用户名片页
                headerView.performClick();
            }
        });
        imageView.addOnImageClickListener(new ImageDisplayer.OnImageClickListener() {
            @Override
            public void onImageClick(ImageDisplayer displayer, String url) {
                // 打开详情页
                infoView.performClick();
            }
        });
    }

    public void showContent(UserMessage msg) {
        headerView.displayImage(msg.getHeadPhoto(), imageSize, false, false);
        nameView.setText(msg.getUserName());
        switch (msg.getType()) {
            case UserMessage.Type.COMMENT:
            case UserMessage.Type.COMMENT_USER:
                contentView.setText(msg.getContent());
                contentView.setTextColor(getColor(R.color.textColor));
                break;
            case UserMessage.Type.LIKE:
                contentView.setText(R.string.ui_icon_heart_hollow);
                contentView.setTextColor(getColor(R.color.colorPrimary));
                break;
        }
        timeView.setText(fragment().formatTimeAgo(msg.getCreateDate()));
        if (msg.getSourceType() == UserMessage.SourceType.MOMENT) {
            Moment moment = msg.getUserMmt();
            //infoView.setVisibility(moment.getImage().size() > 0 ? View.GONE : View.VISIBLE);
            imageView.setVisibility(moment.getImage().size() > 0 ? View.VISIBLE : View.GONE);
            imageView.displayImage(moment.getImage().size() > 0 ? moment.getImage().get(0) : "", imageSize, false, false);
        } else {
            //Archive archive = null == msg.getGroDocRcmd() ? msg.getUserDoc() : msg.getGroDocRcmd();
            //infoView.setVisibility(View.GONE);
            imageView.setVisibility(View.VISIBLE);
            imageView.displayImage("drawable://" + R.drawable.img_default_archive, imageSize, false, false);
        }
        //resetSwipeButtonHeight(deleteButton);
    }

    @Click({R.id.ui_holder_view_individual_user_message_info,
            R.id.ui_holder_view_individual_user_message_header,
            R.id.ui_tool_view_contact_button2})
    private void elementClick(View view) {
        if (null != mOnViewHolderElementClickListener) {
            mOnViewHolderElementClickListener.onClick(view, getAdapterPosition());
        }
    }
}
