package com.leadcom.android.isp.fragment.main;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.hlk.hlklib.lib.inject.Click;
import com.hlk.hlklib.lib.inject.ViewId;
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
import com.leadcom.android.isp.fragment.individual.SettingFragment;
import com.leadcom.android.isp.fragment.individual.moment.MomentListFragment;
import com.leadcom.android.isp.fragment.organization.ContactFragment;
import com.leadcom.android.isp.helper.StringHelper;
import com.leadcom.android.isp.holder.BaseViewHolder;
import com.leadcom.android.isp.holder.common.TextViewHolder;
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
                    // 随时更新我的信息
                    Cache.cache().setCurrentUser(user);
                    Cache.cache().saveCurrentUser();
                }
            }
        }).find(Cache.cache().userId, true);
    }

    private void resetUserInformation(int type, String value) {
        switch (type) {
            case UserRequest.UPDATE_COMPANY:
                mAdapter.get(6).setId(format(items[5], value));
                mAdapter.notifyItemChanged(6);
                break;
            case UserRequest.UPDATE_DUTY:
                mAdapter.get(7).setId(format(items[6], value));
                mAdapter.notifyItemChanged(7);
                break;
            case UserRequest.UPDATE_NAME:
                ((User) mAdapter.get(0)).setName(value);
                mAdapter.notifyItemChanged(0);
                break;
            case UserRequest.UPDATE_PHONE:
                mAdapter.get(8).setId(format(items[7], value));
                mAdapter.notifyItemChanged(8);
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

    @Click({R.id.ui_main_personality_title_left_icon_container, R.id.ui_main_personality_title_right_icon})
    private void viewClick(View view) {
        switch (view.getId()) {
            case R.id.ui_main_personality_title_left_icon_container:
                // 打开消息列表
                SystemMessageFragment.open(PersonalityFragment.this);
                break;
            case R.id.ui_main_personality_title_right_icon:
                view.startAnimation(App.clickAnimation());
                SettingFragment.open(PersonalityFragment.this);
                break;
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
            }
        }).findUser();
    }

    private void initializeAdapter() {
        if (null == mAdapter) {
            mAdapter = new PersonalityAdapter();
            mRecyclerView.setAdapter(mAdapter);
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
            case 5:
                // 编辑单位信息
                break;
            case 6:
                // 编辑职务信息
                break;
            case 7:
                // 修改电话
                break;
        }
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
                    break;
            }
        }
    };

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
                return VT_DELETABLE;
            } else if (model.getId().equals("-")) {
                return VT_LINE;
            }
            return VT_CLICK;
        }

        @Override
        public void onBindHolderOfView(BaseViewHolder holder, int position, @Nullable Model item) {
            if (holder instanceof GroupDetailsViewHolder) {
                ((GroupDetailsViewHolder) holder).showContent((SimpleClickableItem) item);
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
            return R.id.ui_holder_view_contact_swipe_layout;
        }
    }
}
