package com.leadcom.android.isp.holder.individual;

import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.leadcom.android.isp.R;
import com.leadcom.android.isp.fragment.base.BaseFragment;
import com.leadcom.android.isp.helper.StringHelper;
import com.leadcom.android.isp.holder.BaseViewHolder;
import com.leadcom.android.isp.lib.view.ImageDisplayer;
import com.leadcom.android.isp.model.user.Moment;
import com.hlk.hlklib.lib.inject.Click;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.inject.ViewUtility;

/**
 * <b>功能描述：</b>首页个人动态列表头部<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/10/19 08:47 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/10/19 08:47 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class MomentHomeCameraViewHolder extends BaseViewHolder {

    @ViewId(R.id.ui_holder_view_moment_camera_icon)
    private View cameraIcon;
    @ViewId(R.id.ui_holder_view_moment_camera_message_layer)
    private LinearLayout messageView;
    @ViewId(R.id.ui_holder_view_moment_camera_message_header)
    private ImageDisplayer headerView;
    @ViewId(R.id.ui_holder_view_moment_camera_message_number)
    private TextView messageNumber;

    private int imageSize;

    public MomentHomeCameraViewHolder(View itemView, BaseFragment fragment) {
        super(itemView, fragment);
        ViewUtility.bind(this, itemView);
        imageSize = getDimension(R.dimen.ui_base_user_header_image_size_small);
        headerView.addOnImageClickListener(new ImageDisplayer.OnImageClickListener() {
            @Override
            public void onImageClick(ImageDisplayer displayer, String url) {
                messageView.performClick();
            }
        });
    }

    public void showIcon(boolean shown) {
        cameraIcon.setVisibility(shown ? View.VISIBLE : View.GONE);
    }

    public void showContent(Moment moment) {
        showContent(moment.getContent(), moment.getAuthPublic());
    }

    public void showContent(String headerUrl, int msgs) {
        messageView.setVisibility(msgs > 0 ? View.VISIBLE : View.GONE);
        headerView.displayImage(headerUrl, imageSize, false, false);
        messageNumber.setText(StringHelper.getString(R.string.ui_individual_moment_list_message_numbers, msgs));
    }

    @Click({R.id.ui_holder_view_moment_camera_icon,
            R.id.ui_holder_view_moment_camera_message_layer})
    private void elementClick(View view) {
        if (view.getId() == R.id.ui_holder_view_moment_camera_message_layer) {
            messageView.setVisibility(View.GONE);
        }
        if (null != mOnViewHolderElementClickListener) {
            mOnViewHolderElementClickListener.onClick(view, getAdapterPosition());
        }
    }
}
