package com.leadcom.android.isp.holder.organization;

import android.text.Html;
import android.view.View;
import android.widget.TextView;

import com.hlk.hlklib.lib.inject.Click;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.inject.ViewUtility;
import com.hlk.hlklib.lib.view.CorneredButton;
import com.hlk.hlklib.lib.view.CustomTextView;
import com.leadcom.android.isp.R;
import com.leadcom.android.isp.cache.Cache;
import com.leadcom.android.isp.fragment.base.BaseFragment;
import com.leadcom.android.isp.helper.StringHelper;
import com.leadcom.android.isp.holder.BaseViewHolder;
import com.leadcom.android.isp.lib.view.ImageDisplayer;
import com.leadcom.android.isp.model.organization.Member;
import com.leadcom.android.isp.model.user.User;

/**
 * <b>功能描述：</b>联系人<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/05/09 09:17 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/05/09 09:17 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class ContactViewHolder extends BaseViewHolder {

    @ViewId(R.id.ui_tool_view_contact_button0)
    private TextView buttonTransferManager;
    @ViewId(R.id.ui_tool_view_contact_button0d5)
    private TextView buttonArchiveManager;
    @ViewId(R.id.ui_tool_view_contact_button1)
    private TextView buttonSetToManager;
    @ViewId(R.id.ui_tool_view_contact_button2)
    private TextView buttonKickOut;
    @ViewId(R.id.ui_holder_view_contact_header)
    private ImageDisplayer headerView;
    @ViewId(R.id.ui_holder_view_contact_name)
    private TextView nameView;
    @ViewId(R.id.ui_holder_view_contact_duty)
    private TextView dutyView;
    //@ViewId(R.id.ui_holder_view_contact_phone)
    //private TextView phoneView;
    @ViewId(R.id.ui_holder_view_contact_myself)
    private TextView myselfView;
    @ViewId(R.id.ui_holder_view_contact_manager)
    private CustomTextView managerView;
    @ViewId(R.id.ui_holder_view_contact_invite_button)
    private CorneredButton buttonInvite;
    @ViewId(R.id.ui_holder_view_contact_picker)
    private CustomTextView iconPicker;
    @ViewId(R.id.ui_holder_view_contact_locked)
    private CustomTextView lockFlag;
    @ViewId(R.id.ui_holder_view_item_left_blank)
    private View leftBlank;

    private boolean buttonInviteVisible = false;
    private boolean pickerVisible = false;
    //private boolean phoneVisible = false;
    private int imageSize;

    public ContactViewHolder(View itemView, BaseFragment fragment) {
        super(itemView, fragment);
        ViewUtility.bind(this, itemView);
        imageSize = getDimension(R.dimen.ui_static_dp_30);
        headerView.addOnImageClickListener(new ImageDisplayer.OnImageClickListener() {
            @Override
            public void onImageClick(ImageDisplayer displayer, String url) {
                if (null != mOnViewHolderClickListener) {
                    mOnViewHolderClickListener.onClick(getAdapterPosition());
                }
            }
        });
    }

//    public void setPhoneVisible(boolean visible) {
//        phoneVisible = visible;
//    }

    /**
     * 转让管理权
     */
    public void showButton0(boolean shown) {
        buttonTransferManager.setVisibility(shown ? View.VISIBLE : View.GONE);
    }

    public void button0Text(int text) {
        buttonTransferManager.setText(text);
    }

    public void button0Text(String text) {
        buttonTransferManager.setText(text);
    }

    /**
     * 设为档案管理员
     */
    public void showButton0d5(boolean shown) {
        buttonArchiveManager.setVisibility(shown ? View.VISIBLE : View.GONE);
    }

    public void button0d5Text(int text) {
        buttonArchiveManager.setText(text);
    }

    public void button0d5Text(String text) {
        buttonArchiveManager.setText(text);
    }

    /**
     * 设为管理员
     */
    public void showButton1(boolean shown) {
        buttonSetToManager.setVisibility(shown ? View.VISIBLE : View.GONE);
    }

    public void button1Text(int text) {
        buttonSetToManager.setText(text);
    }

    public void button1Text(String text) {
        buttonSetToManager.setText(text);
    }

    /**
     * 删除
     */
    public void showButton2(boolean shown) {
        buttonKickOut.setVisibility(shown ? View.VISIBLE : View.GONE);
    }

    public void button2Text(int text) {
        buttonKickOut.setText(text);
    }

    public void button2Text(String text) {
        buttonKickOut.setText(text);
    }

    public void showContent(User user, String searching) {
        String text = user.getName();
        text = getSearchingText(text, searching);
        nameView.setText(Html.fromHtml(text));
        //phoneView.setText(user.getPhone());
        String header = user.getHeadPhoto();
        if (isEmpty(header) || header.length() < 20) {
            header = "drawable://" + R.drawable.img_default_user_header;
        }
        headerView.displayImage(header, imageSize, false, false);
        myselfView.setVisibility(user.getId().equals(Cache.cache().userId) ? View.VISIBLE : View.GONE);
    }

    public void showContent(Member member, String searchingText) {
        String text = member.getUserName();
        if (StringHelper.isEmpty(text)) {
            text = StringHelper.getString(R.string.ui_base_text_no_name);
        }
        text = getSearchingText(text, searchingText);
        nameView.setText(Html.fromHtml(text));
        dutyView.setText(member.getDuty());
//        if (phoneVisible) {
//            text = member.getPhone();
//            if (StringHelper.isEmpty(text)) {
//                text = StringHelper.getString(R.string.ui_organization_member_no_phone);
//            }
//            text = getSearchingText(text, searchingText);
//            phoneView.setText(Html.fromHtml(text));
//        } else {
//            phoneView.setText(null);
//        }
        String header = member.getHeadPhoto();
        if (isEmpty(header) || header.length() < 20) {
            header = "drawable://" + R.drawable.img_default_user_header;
        }
        headerView.displayImage(header, imageSize, false, false);
        boolean isMe = !isEmpty(member.getUserId()) && member.getUserId().equals(Cache.cache().userId);
        myselfView.setVisibility(isMe ? View.VISIBLE : View.GONE);

        managerView.setText(member.isGroupManager() ? R.string.ui_icon_group_manager :
                (member.isArchiveManager() ? R.string.ui_icon_archive_manager :
                        (member.isSquadManager() ? R.string.ui_icon_group_manager : R.string.ui_icon_group_manager)));
        managerView.setTextColor(getColor(member.isGroupManager() ? R.color.color_dfc371 : R.color.colorPrimary));
        managerView.setVisibility((member.isGroupManager() || member.isArchiveManager() || member.isSquadManager()) ? View.VISIBLE : View.GONE);

        buttonInvite.setVisibility(buttonInviteVisible ? (isMe ? View.GONE : View.VISIBLE) : View.GONE);
        lockFlag.setVisibility(member.isLocalDeleted() ? View.VISIBLE : View.GONE);
        if (buttonInviteVisible) {
            // 只在显示按钮的时候才进行判断加入或不加入操作
            if (!isEmpty(squadId)) {
                buttonInvite.setEnabled(!member.isSelected());
                buttonInvite.setText(member.isSelected() ? R.string.ui_phone_contact_invited : R.string.ui_phone_contact_add);
            }
        }
        leftBlank.setVisibility(pickerVisible && !member.isSelectable() ? View.VISIBLE : View.GONE);
        iconPicker.setVisibility(pickerVisible ? View.VISIBLE : View.GONE);
        iconPicker.setTextColor(getColor(member.isSelected() ? R.color.colorPrimary : R.color.textColorHintLight));
    }

    private String squadId = "";

    /**
     * 设置小组的id以便加人到小组里去
     */
    public void setSquadId(String squadId) {
        this.squadId = squadId;
    }

    /**
     * 是否显示“添加”按钮
     */
    public void showInviteButton(boolean shown) {
        buttonInviteVisible = shown;
    }

    public void showPicker(boolean shown) {
        pickerVisible = shown;
    }

    @Click({R.id.ui_holder_view_contact_layout,
            R.id.ui_tool_view_contact_button0,
            R.id.ui_tool_view_contact_button0d5,
            R.id.ui_tool_view_contact_button1,
            R.id.ui_tool_view_contact_button2,
            R.id.ui_holder_view_contact_invite_button,
            R.id.ui_holder_view_contact_picker})
    private void click(View view) {
        if (null != mOnViewHolderElementClickListener) {
            mOnViewHolderElementClickListener.onClick(view, getAdapterPosition());
        }
        switch (view.getId()) {
            case R.id.ui_holder_view_contact_layout:
                // 点击了整个item view，打开用户详情页
                if (null != mOnViewHolderClickListener) {
                    mOnViewHolderClickListener.onClick(getAdapterPosition());
                }
                break;
            case R.id.ui_tool_view_contact_button0:
                // 转让群组
                if (null != onTransferManagementListener) {
                    onTransferManagementListener.onTransfer(ContactViewHolder.this);
                }
                break;
            case R.id.ui_tool_view_contact_button0d5:
                // 设为档案管理员
                if (null != onSetArchiveManagerListener) {
                    onSetArchiveManagerListener.onSetting(getAdapterPosition());
                }
                break;
            case R.id.ui_tool_view_contact_button1:
                // 设为管理员
                if (null != mOnHandlerBoundDataListener) {
                    mOnHandlerBoundDataListener.onHandlerBoundData(ContactViewHolder.this);
                }
                break;
            case R.id.ui_tool_view_contact_button2:
                // 删除用户
                if (null != onUserDeleteListener) {
                    onUserDeleteListener.onDelete(ContactViewHolder.this);
                }
                break;
            case R.id.ui_holder_view_contact_invite_button:
                if (null != mOnHandlerBoundDataListener) {
                    mOnHandlerBoundDataListener.onHandlerBoundData(ContactViewHolder.this);
                }
                break;
            case R.id.ui_holder_view_contact_picker:
                if (null != mOnViewHolderClickListener) {
                    mOnViewHolderClickListener.onClick(getAdapterPosition());
                }
                break;
//            case R.id.ui_holder_view_contact_phone:
//                if (!phoneVisible) return;
//                if (null != onPhoneDialListener) {
//                    onPhoneDialListener.onDial(getAdapterPosition());
//                }
//                break;
        }
    }

    /**
     * 点击手机号码进行拨号
     */
    public interface OnPhoneDialListener {
        void onDial(int index);
    }

    private OnPhoneDialListener onPhoneDialListener;

    public void setOnPhoneDialListener(OnPhoneDialListener l) {
        onPhoneDialListener = l;
    }

    private OnUserDeleteListener onUserDeleteListener;

    public void setOnUserDeleteListener(OnUserDeleteListener l) {
        onUserDeleteListener = l;
    }

    public interface OnUserDeleteListener {
        void onDelete(ContactViewHolder holder);
    }

    private OnTransferManagementListener onTransferManagementListener;

    public void setOnTransferManagementListener(OnTransferManagementListener l) {
        onTransferManagementListener = l;
    }

    public interface OnTransferManagementListener {
        void onTransfer(ContactViewHolder holder);
    }

    private OnSetArchiveManagerListener onSetArchiveManagerListener;

    public void setOnSetArchiveManagerListener(OnSetArchiveManagerListener l) {
        onSetArchiveManagerListener = l;
    }

    public interface OnSetArchiveManagerListener {
        void onSetting(int index);
    }
}
