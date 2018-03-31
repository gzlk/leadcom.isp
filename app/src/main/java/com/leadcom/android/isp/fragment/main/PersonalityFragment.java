package com.leadcom.android.isp.fragment.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.daimajia.swipe.util.Attributes;
import com.hlk.hlklib.lib.inject.Click;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.view.ClearEditText;
import com.hlk.hlklib.lib.view.CustomTextView;
import com.leadcom.android.isp.R;
import com.leadcom.android.isp.adapter.RecyclerViewSwipeAdapter;
import com.leadcom.android.isp.api.common.QuantityRequest;
import com.leadcom.android.isp.api.listener.OnSingleRequestListener;
import com.leadcom.android.isp.api.user.UserRequest;
import com.leadcom.android.isp.application.App;
import com.leadcom.android.isp.application.NimApplication;
import com.leadcom.android.isp.cache.Cache;
import com.leadcom.android.isp.fragment.base.BaseSwipeRefreshSupportFragment;
import com.leadcom.android.isp.fragment.individual.ModifyPhoneFragment;
import com.leadcom.android.isp.fragment.individual.SettingFragment;
import com.leadcom.android.isp.fragment.individual.moment.MomentListFragment;
import com.leadcom.android.isp.fragment.login.CodeVerifyFragment;
import com.leadcom.android.isp.fragment.organization.ContactFragment;
import com.leadcom.android.isp.helper.DeleteDialogHelper;
import com.leadcom.android.isp.helper.DialogHelper;
import com.leadcom.android.isp.helper.EditableDialogHelper;
import com.leadcom.android.isp.helper.StringHelper;
import com.leadcom.android.isp.helper.ToastHelper;
import com.leadcom.android.isp.holder.BaseViewHolder;
import com.leadcom.android.isp.holder.common.TextViewHolder;
import com.leadcom.android.isp.holder.common.ToggleableViewHolder;
import com.leadcom.android.isp.holder.home.GroupDetailsViewHolder;
import com.leadcom.android.isp.holder.individual.UserHeaderBlurViewHolder;
import com.leadcom.android.isp.listener.NotificationChangeHandleCallback;
import com.leadcom.android.isp.listener.OnViewHolderElementClickListener;
import com.leadcom.android.isp.model.Model;
import com.leadcom.android.isp.model.common.Attachment;
import com.leadcom.android.isp.model.common.Quantity;
import com.leadcom.android.isp.model.common.SimpleClickableItem;
import com.leadcom.android.isp.model.user.User;
import com.leadcom.android.isp.model.user.UserExtra;
import com.leadcom.android.isp.nim.model.notification.NimMessage;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * <b>功能描述：</b>主页 - 个人<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2018/03/21 09:14 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2018/03/21 09:14 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class PersonalityFragment extends BaseSwipeRefreshSupportFragment {

    @ViewId(R.id.ui_main_tool_bar_background)
    private LinearLayout toolbarBackground;
    @ViewId(R.id.ui_main_tool_bar_padding_layout)
    private LinearLayout paddingLayout;
    @ViewId(R.id.ui_main_personality_title_text)
    private TextView titleText;
    @ViewId(R.id.ui_main_personality_title_left_icon)
    private CustomTextView leftIcon;
    @ViewId(R.id.ui_main_personality_title_left_icon_flag)
    private View leftFlag;
    @ViewId(R.id.ui_main_personality_title_right_icon)
    private CustomTextView rightIcon;
    private PersonalityAdapter mAdapter;
    private String[] items;

    private static int selectedIndex = 0, deleteIndex = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NimApplication.addNotificationChangeCallback(callback);
    }

    private NotificationChangeHandleCallback callback = new NotificationChangeHandleCallback() {
        @Override
        public void onChanged() {
            int size = NimMessage.getUnRead();
            if (null != leftFlag) {
                leftFlag.setVisibility((size > 0) ? View.VISIBLE : View.GONE);
            }
        }
    };

    @Override
    public void onDestroy() {
        NimApplication.removeNotificationChangeCallback(callback);
        super.onDestroy();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        tryPaddingContent(paddingLayout, false);

        // 头像选择是需要剪切的
        isChooseImageForCrop = true;
        // 头像是需要压缩的
        isSupportCompress = true;
        // 图片选择后的回调
        addOnImageSelectedListener(albumImageSelectedListener);
        // 文件上传完毕后的回调处理
        setOnFileUploadingListener(mOnFileUploadingListener);
        // 查找未读的推送通知
        NimApplication.dispatchCallbacks();
    }

    // 相册选择返回了
    private OnImageSelectedListener albumImageSelectedListener = new OnImageSelectedListener() {
        @Override
        public void onImageSelected(ArrayList<String> selected) {
            // 图片选择完毕之后立即压缩图片并且自动上传
            compressImage();
        }
    };

    private OnFileUploadingListener mOnFileUploadingListener = new OnFileUploadingListener() {
        @Override
        public void onUploading(int all, int current, String file, long size, long uploaded) {

        }

        @Override
        public void onUploadingComplete(ArrayList<Attachment> uploaded) {
            int size = uploaded.size();
            tryEditUserInfo(UserRequest.UPDATE_PHOTO, uploaded.get(size - 1).getUrl());
        }
    };

    private void tryEditUserInfo(final int type, final String value) {
        setLoadingText(R.string.ui_text_user_information_loading_updating);
        displayLoading(true);
        UserRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<User>() {
            @Override
            public void onResponse(User user, boolean success, String message) {
                super.onResponse(user, success, message);
                if (success) {
                    // 同步我的基本信息
                    //syncMineInformation();
                    fetchingRemoteUserInfo();
                    resetUserInformation(type, value);
                }
                displayLoading(false);
            }
        }).update(type, value);
    }

    private void fetchingRemoteUserInfo() {
        UserRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<User>() {
            @Override
            public void onResponse(User user, boolean success, String message) {
                super.onResponse(user, success, message);
                if (success && null != user) {
                    if (isMe()) {
                        // 随时更新我的信息
                        Cache.cache().setCurrentUser(user);
                        Cache.cache().saveCurrentUser();
                        resetExtras();
                    }
                }
            }
        }).find(Cache.cache().userId, true);
    }

    private void resetUserInformation(int type, String value) {
        switch (type) {
            case UserRequest.UPDATE_COMPANY:
                mAdapter.update(new SimpleClickableItem(format(items[5], value)));
                break;
            case UserRequest.UPDATE_DUTY:
                mAdapter.update(new SimpleClickableItem(format(items[6], value)));
                break;
            case UserRequest.UPDATE_NAME:
                ((User) mAdapter.get(0)).setName(value);
                mAdapter.notifyItemChanged(0);
                break;
            case UserRequest.UPDATE_PHONE:
                mAdapter.update(new SimpleClickableItem(format(items[7], value)));
                break;
            case UserRequest.UPDATE_PHOTO:
                ((User) mAdapter.get(0)).setHeadPhoto(value);
                mAdapter.notifyItemChanged(0);
                break;
            case UserRequest.UPDATE_SIGNATURE:
                ((User) mAdapter.get(0)).setSignature(value);
                mAdapter.notifyItemChanged(0);
                break;
        }
    }

    public void resetIconColor(int color) {
        leftIcon.setTextColor(color);
        rightIcon.setTextColor(color);
    }

    @Override
    protected void onDelayRefreshComplete(int type) {

    }

    @Override
    public void doingInResume() {
        initializeAdapter();
    }

    @Override
    protected boolean shouldSetDefaultTitleEvents() {
        return false;
    }

    @Override
    protected void destroyView() {

    }

    @Override
    public int getLayout() {
        return R.layout.fragment_main_personality;
    }

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
    public void onActivityResult(int requestCode, Intent data) {
        switch (requestCode) {
            case REQUEST_PHONE:
                String result = getResultedData(data);
                if (!isEmpty(result)) {
                    // 输入的手机号码不为空时
                    openActivity(CodeVerifyFragment.class.getName(), format("%d,%s", CodeVerifyFragment.VT_MODIFY_PHONE, result), REQUEST_PHONE_CONFIRM, true, false);
                }
                break;
            case REQUEST_PHONE_CONFIRM:
                // 手机号码修改成功了
                resetUserInformation(UserRequest.UPDATE_PHONE, getResultedData(data));
                break;
        }
        super.onActivityResult(requestCode, data);
    }

    @Click({R.id.ui_main_personality_title_left_icon_container, R.id.ui_main_personality_title_right_icon, R.id.ui_user_information_self_define})
    private void viewClick(View view) {
        switch (view.getId()) {
            case R.id.ui_main_personality_title_left_icon_container:
                // 打开消息列表
                view.startAnimation(App.clickAnimation());
                SystemMessageFragment.open(PersonalityFragment.this);
                break;
            case R.id.ui_main_personality_title_right_icon:
                view.startAnimation(App.clickAnimation());
                SettingFragment.open(PersonalityFragment.this);
                break;
            case R.id.ui_user_information_self_define:
                selectedIndex = 0;
                openSelfDefineDialog();
                break;
        }
    }

    private boolean isMe() {
        return isEmpty(mQueryId) || mQueryId.equals(Cache.cache().userId);
    }

    private void clearExtras() {
        Iterator<Model> iterator = mAdapter.iterator();
        int index = 0;
        while (iterator.hasNext()) {
            Model model = iterator.next();
            if (model instanceof UserExtra) {
                iterator.remove();
                mAdapter.notifyItemRemoved(index);
            }
            index++;
        }
    }

    private void resetExtras() {
        clearExtras();
        for (UserExtra extra : Cache.cache().me.getExtra()) {
            if (null != extra) {
                // 如果额外的属性是可显示状态或者不可显示但当前用户是登录用户时，也可以显示
                if (extra.getShow() == UserExtra.ShownType.SHOWN || (extra.getShow() == UserExtra.ShownType.HIDE && isMe())) {
                    mAdapter.update(extra);
                }
            }
        }
    }

    private void initializeItems() {
        if (null == items) {
            items = StringHelper.getStringArray(R.array.ui_text_personality_items);
        }
        for (String string : items) {
            if (string.charAt(0) == '-') {
                Model model = new Model();
                model.setId(string);
                mAdapter.add(model);
            } else {
                SimpleClickableItem item = new SimpleClickableItem(format(string, 0));
                if (item.getIndex() == 5) {
                    item.setSource(format(string, Cache.cache().me.getCompany()));
                } else if (item.getIndex() == 6) {
                    item.setSource(format(string, Cache.cache().me.getPosition()));
                } else if (item.getIndex() == 7) {
                    item.setSource(format(string, Cache.cache().userPhone));
                }
                item.reset();
                mAdapter.add(item);
            }
        }
        fetchingQuantity();
    }

    private void fetchingQuantity() {
        QuantityRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<Quantity>() {
            @Override
            public void onResponse(Quantity quantity, boolean success, String message) {
                super.onResponse(quantity, success, message);
                if (success && null != quantity) {
                    for (int i = 0; i < 4; i++) {
                        String text = "";
                        switch (i) {
                            case 0:
                                text = format(items[0], quantity.getUserNum());
                                break;
                            case 1:
                                text = format(items[1], quantity.getDocNum());
                                break;
                            case 2:
                                text = format(items[2], quantity.getMmtNum());
                                break;
                            case 3:
                                text = format(items[3], quantity.getColNum());
                                break;
                        }
                        if (!isEmpty(text)) {
                            SimpleClickableItem item = new SimpleClickableItem(text);
                            mAdapter.update(item);
                        }
                    }
                }
                fetchingRemoteUserInfo();
            }
        }).findUser();
    }

    private void initializeAdapter() {
        if (null == mAdapter) {
            mAdapter = new PersonalityAdapter();
            mRecyclerView.setAdapter(mAdapter);
            mAdapter.setMode(Attributes.Mode.Single);
            mRecyclerView.addOnScrollListener(scrollListener);
            mAdapter.add(Cache.cache().me);
            initializeItems();
        }
    }

    private int mDistance = 0;
    private static final int MAX_ALPHA = 255;
    private RecyclerView.OnScrollListener scrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            mDistance += dy;
            float percentage = mDistance * 1.0f / MAX_ALPHA;
            toolbarBackground.setAlpha(percentage);
            titleText.setAlpha(percentage);
        }
    };

    private void performItemClick(int index) {
        switch (index) {
            case 1:
                // 打开通讯录
                ContactFragment.open(this);
                break;
            case 2:
                // 打开个人档案
                IndividualFragment.open(this, IndividualFragment.TYPE_ARCHIVE_MINE);
                break;
            case 3:
                // 打开个人动态
                MomentListFragment.open(PersonalityFragment.this, Cache.cache().userId);
                break;
            case 4:
                // 打开个人搜藏
                IndividualFragment.open(this, IndividualFragment.TYPE_COLLECT);
                break;
            case 6:
                // 编辑单位信息
                selectedIndex = 6;
                prepareEditInfo(R.string.ui_text_personality_item_edit_company, Cache.cache().me.getCompany(), R.string.ui_text_personality_item_edit_company_hint);
                break;
            case 7:
                // 编辑职务信息
                selectedIndex = 7;
                prepareEditInfo(R.string.ui_text_personality_item_edit_duty, Cache.cache().me.getPosition(), R.string.ui_text_personality_item_edit_duty_hint);
                break;
            case 8:
                // 修改电话
                openActivity(ModifyPhoneFragment.class.getName(), Cache.cache().userPhone, REQUEST_PHONE, true, false);
                break;
            default:
                selectedIndex = index;
                if (mAdapter.get(selectedIndex) instanceof UserExtra) {
                    openSelfDefineDialog();
                }
                break;
        }
    }

    private EditableDialogHelper editableDialogHelper;

    private void prepareEditInfo(int title, final String value, int hint) {
        if (null == editableDialogHelper) {
            editableDialogHelper = EditableDialogHelper.helper().init(this);
        }
        editableDialogHelper.setOnDialogConfirmListener(new DialogHelper.OnDialogConfirmListener() {
            @Override
            public boolean onConfirm() {
                String input = editableDialogHelper.getInputValue();
                if (isEmpty(input)) {
                    ToastHelper.make().showMsg(R.string.ui_text_personality_item_input_empty);
                    return false;
                }
                if (!input.equals(value)) {
                    if (selectedIndex == 6) {
                        // 修改单位信息
                        tryEditUserInfo(UserRequest.UPDATE_COMPANY, input);
                    } else if (selectedIndex == 7) {
                        // 修改职务信息
                        tryEditUserInfo(UserRequest.UPDATE_DUTY, input);
                    }
                }
                return true;
            }
        }).setInputValue(value).setTitleText(title).setInputHint(hint).show();
    }

    private View selfDefineDialog, selfShown;
    private ClearEditText selfName, selfValue;
    private ToggleableViewHolder toggleHolder;

    // 打开自定义属性对话框
    private void openSelfDefineDialog() {
        DialogHelper.init(Activity()).addOnDialogInitializeListener(new DialogHelper.OnDialogInitializeListener() {
            @Override
            public View onInitializeView() {
                if (null == selfDefineDialog) {
                    selfDefineDialog = View.inflate(Activity(), R.layout.popup_dialog_individual_self_defined_property, null);
                }
                return selfDefineDialog;
            }

            @Override
            public void onBindData(View dialogView, DialogHelper helper) {
                if (null == toggleHolder) {
                    toggleHolder = new ToggleableViewHolder(selfDefineDialog, PersonalityFragment.this);
                }
                if (null == selfName) {
                    selfName = selfDefineDialog.findViewById(R.id.ui_popup_individual_self_defined_property_name);
                }
                if (null == selfValue) {
                    selfValue = selfDefineDialog.findViewById(R.id.ui_popup_individual_self_defined_property_value);
                }
                if (null == selfShown) {
                    selfShown = selfDefineDialog.findViewById(R.id.ui_popup_individual_self_defined_property_shown);
                }
                Model model = mAdapter.get(selectedIndex);
                selfShown.setVisibility((model instanceof UserExtra) ? View.VISIBLE : View.GONE);
                if (model instanceof UserExtra) {
                    UserExtra ue = (UserExtra) model;
                    selfName.setValue(ue.getTitle());
                    selfName.focusEnd();
                    selfValue.setValue(ue.getContent());
                    toggleHolder.showContent(getString(R.string.ui_text_user_property_self_defined_shown, ue.getShow()));
                } else {
                    toggleHolder.showContent(getString(R.string.ui_text_user_property_self_defined_shown, UserExtra.ShownType.HIDE));
                    selfName.setValue("");
                    selfValue.setValue("");
                }
            }
        }).addOnDialogConfirmListener(new DialogHelper.OnDialogConfirmListener() {
            @Override
            public boolean onConfirm() {
                String title = selfName.getValue();
                String value = selfValue.getValue();
                if (isEmpty(title) || isEmpty(value)) {
                    ToastHelper.make().showMsg(R.string.ui_text_user_property_self_defined_invalid);
                    return false;
                }
                UserExtra extra;
                if (selectedIndex > 0) {
                    extra = (UserExtra) mAdapter.get(selectedIndex);
                } else {
                    extra = new UserExtra();
                }
                extra.setTitle(selfName.getValue());
                extra.setContent(selfValue.getValue());
                extra.setShow(selfShown.getVisibility() == View.GONE ? UserExtra.ShownType.HIDE : (toggleHolder.isToggled() ? UserExtra.ShownType.SHOWN : UserExtra.ShownType.HIDE));
                //extra.setShow((selfShown.getVisibility() == View.VISIBLE || toggleHolder.isToggled()) ? UserExtra.ShownType.SHOWN : UserExtra.ShownType.HIDE);
                int index = Cache.cache().me.getExtra().indexOf(extra);
                if (index >= 0) {
                    Cache.cache().me.getExtra().set(index, extra);
                } else {
                    Cache.cache().me.getExtra().add(extra);
                }
                updateMyExtra();
                return true;
            }
        }).setPopupType(DialogHelper.SLID_IN_BOTTOM).show();
    }

    private void updateMyExtra() {
        setLoadingText(R.string.ui_text_user_information_loading_updating_extra);
        displayLoading(true);
        UserRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<User>() {
            @Override
            public void onResponse(User user, boolean success, String message) {
                super.onResponse(user, success, message);
                displayLoading(false);
                if (success) {
                    // 如果是删除某个自定义选项，此时删除adapter里的条目
                    if (deleteIndex > 0) {
                        mAdapter.remove(deleteIndex);
                        deleteIndex = 0;
                    }
                    fetchingRemoteUserInfo();
                }
            }
        }).update(Cache.cache().me.getExtra());
    }

    private OnViewHolderElementClickListener elementClickListener = new OnViewHolderElementClickListener() {
        @Override
        public void onClick(View view, int index) {
            switch (view.getId()) {
                case R.id.ui_holder_view_user_header_layout:
                    openImageSelector(true);
                    break;
                case R.id.ui_holder_view_simple_clickable:
                    // 打开或编辑置顶的项目
                    performItemClick(index);
                    break;
                case R.id.ui_tool_view_contact_button2:
                    // 删除自定义介绍项目
                    deleteIndex = index;
                    warningDeleteSelfDefinedProperty(deleteIndex);
                    break;
            }
        }
    };

    private void warningDeleteSelfDefinedProperty(final int index) {
        DeleteDialogHelper.helper().setOnDialogConfirmListener(new DialogHelper.OnDialogConfirmListener() {
            @Override
            public boolean onConfirm() {
                UserExtra ue = (UserExtra) mAdapter.get(index);
                Cache.cache().me.getExtra().remove(ue);
                updateMyExtra();
                return true;
            }
        }).init(this).setTitleText(R.string.ui_text_user_property_self_defined_delete).setConfirmText(R.string.ui_base_text_yes).show();
    }

    private class PersonalityAdapter extends RecyclerViewSwipeAdapter<BaseViewHolder, Model> {

        private static final int VT_USER = 0, VT_CLICK = 1, VT_DELETABLE = 2, VT_LINE = 3;

        @Override
        public BaseViewHolder onCreateViewHolder(View itemView, int viewType) {
            switch (viewType) {
                case VT_LINE:
                    return new TextViewHolder(itemView, PersonalityFragment.this);
                case VT_CLICK:
                case VT_DELETABLE:
                    GroupDetailsViewHolder gdv = new GroupDetailsViewHolder(itemView, PersonalityFragment.this);
                    gdv.setOnViewHolderElementClickListener(elementClickListener);
                    return gdv;
                case VT_USER:
                    UserHeaderBlurViewHolder uhbvh = new UserHeaderBlurViewHolder(itemView, PersonalityFragment.this);
                    uhbvh.setOnViewHolderElementClickListener(elementClickListener);
                    return uhbvh;
            }
            return null;
        }

        @Override
        public int itemLayout(int viewType) {
            switch (viewType) {
                case VT_USER:
                    return R.layout.holder_view_individual_header_blur;
                case VT_CLICK:
                    return R.layout.holder_view_group_details;
                case VT_DELETABLE:
                    return R.layout.holder_view_group_details_deletable;
                default:
                    return R.layout.tool_view_divider_big;
            }
        }

        @Override
        public int getItemViewType(int position) {
            Model model = get(position);
            if (model instanceof User) {
                return VT_USER;
            } else if (model instanceof UserExtra) {
                UserExtra extra = (UserExtra) model;
                if (extra.isStaticDiy()) {
                    return VT_CLICK;
                }
                return VT_DELETABLE;
            } else if (model.getId().equals("-")) {
                return VT_LINE;
            }
            return VT_CLICK;
        }

        @Override
        public void onBindHolderOfView(BaseViewHolder holder, int position, @Nullable Model item) {
            if (holder instanceof GroupDetailsViewHolder) {
                if (item instanceof UserExtra) {
                    ((GroupDetailsViewHolder) holder).showContent((UserExtra) item);
                } else {
                    ((GroupDetailsViewHolder) holder).showContent((SimpleClickableItem) item);
                }
            } else if (holder instanceof UserHeaderBlurViewHolder) {
                ((UserHeaderBlurViewHolder) holder).showContent((User) item);
            }
        }

        @Override
        protected int comparator(Model item1, Model item2) {
            return 0;
        }

        @Override
        public int getSwipeLayoutResourceId(int position) {
            return R.id.ui_holder_view_group_details_swipe_layout;
        }
    }
}
