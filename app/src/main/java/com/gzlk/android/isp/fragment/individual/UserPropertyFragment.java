package com.gzlk.android.isp.fragment.individual;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.bigkoo.pickerview.TimePickerView;
import com.gzlk.android.isp.R;
import com.gzlk.android.isp.adapter.RecyclerViewAdapter;
import com.gzlk.android.isp.api.listener.OnSingleRequestListener;
import com.gzlk.android.isp.api.user.UserRequest;
import com.gzlk.android.isp.cache.Cache;
import com.gzlk.android.isp.etc.Utils;
import com.gzlk.android.isp.fragment.base.BasePopupInputSupportFragment;
import com.gzlk.android.isp.fragment.common.BaseTransparentPropertyFragment;
import com.gzlk.android.isp.fragment.login.CodeVerifyFragment;
import com.gzlk.android.isp.helper.DialogHelper;
import com.gzlk.android.isp.helper.StringHelper;
import com.gzlk.android.isp.helper.ToastHelper;
import com.gzlk.android.isp.holder.BaseViewHolder;
import com.gzlk.android.isp.holder.common.SimpleClickableViewHolder;
import com.gzlk.android.isp.holder.individual.UserHeaderBigViewHolder;
import com.gzlk.android.isp.holder.individual.UserSimpleMomentViewHolder;
import com.gzlk.android.isp.listener.OnViewHolderClickListener;
import com.gzlk.android.isp.model.Model;
import com.gzlk.android.isp.model.common.Attachment;
import com.gzlk.android.isp.model.common.SimpleClickableItem;
import com.gzlk.android.isp.model.user.SimpleMoment;
import com.gzlk.android.isp.model.user.User;
import com.gzlk.android.isp.nim.session.NimSessionHelper;
import com.hlk.hlklib.lib.inject.Click;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.view.CorneredView;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

/**
 * <b>功能描述：</b>用户的基本属性页<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/05/02 08:02 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/05/02 08:02 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class UserPropertyFragment extends BaseTransparentPropertyFragment {

    public static UserPropertyFragment newInstance(String params) {
        UserPropertyFragment mf = new UserPropertyFragment();
        Bundle bundle = new Bundle();
        // 需要查看的用户的id
        bundle.putString(PARAM_QUERY_ID, params);
        mf.setArguments(bundle);
        return mf;
    }

    // UI
    @ViewId(R.id.ui_ui_custom_title_right_text)
    private TextView rightTextView;
    @ViewId(R.id.ui_user_information_to_archive)
    private CorneredView toArchive;
    @ViewId(R.id.ui_user_information_to_chat)
    private CorneredView toChat;

    private String[] items;
    private MyPropertyAdapter mAdapter;

    @Override
    public void doingInResume() {
        super.doingInResume();
        initializeItems();
        titleTextView.setText(null);
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

    @Override
    public int getLayout() {
        return R.layout.fragment_individual_information;
    }

    @Override
    protected void onSwipeRefreshing() {
        fetchingRemoteUserInfo();
    }

    @Override
    protected void onLoadingMore() {
        isLoadingComplete(true);
    }

    @Click({R.id.ui_ui_custom_title_right_container,
            R.id.ui_user_information_to_archive,
            R.id.ui_user_information_to_chat})
    private void click(View view) {
        switch (view.getId()) {
            case R.id.ui_ui_custom_title_right_container:
                //toEdit();
                break;
            case R.id.ui_user_information_to_archive:
                onBottomButtonClicked();
                break;
            case R.id.ui_user_information_to_chat:
                // 到单聊页面
                NimSessionHelper.startP2PSession(Activity(), mQueryId);
                //NimUIKit.startP2PSession(Activity(), mQueryId);
                //ToastHelper.make().showMsg("发消息");
                break;
        }
    }

    @Override
    protected void onBottomButtonClicked() {
        // 查看档案
        ToastHelper.make().showMsg("目前没有查看个人档案的UI页面");
    }

//    private void toEdit() {
//        User user = (User) mAdapter.get(0);
//        if (user.getId().equals(Cache.cache().userId)) {
//            user.setLocalDeleted(!user.isLocalDeleted());
//            mAdapter.notifyItemChanged(0);
//        }
//    }

    private boolean isMe() {
        return !isEmpty(mQueryId) && mQueryId.equals(Cache.cache().userId);
    }

    private void initializeItems() {
        if (null == items) {
            setSupportLoadingMore(false);
            items = StringHelper.getStringArray(R.array.ui_text_my_setting_items);
            // 头像选择是需要剪切的
            isChooseImageForCrop = true;
            // 头像是需要压缩的
            isSupportCompress = true;
            // 图片选择后的回调
            addOnImageSelectedListener(albumImageSelectedListener);
            // 文件上传完毕后的回调处理
            setOnFileUploadingListener(mOnFileUploadingListener);
        }
        if (null == mAdapter) {
            mAdapter = new MyPropertyAdapter();
            mRecyclerView.setAdapter(mAdapter);
            fetchingRemoteUserInfo();
        }
    }

    private OnViewHolderClickListener onViewHolderClickListener = new OnViewHolderClickListener() {
        @Override
        public void onClick(int index) {
            if (index == 1) {
                // 打开动态页
                openActivity(MomentListFragment.class.getName(), mQueryId, true, false);
            } else {
                if (mQueryId.equals(Cache.cache().userId)) {
                    // 只有我自己才能修改我自己的信息
                    checkClickType(index);
                }
            }
        }
    };

    private ArrayList<SimpleMoment> simpleMoments = new ArrayList<>();

    private void fetchingRemoteUserInfo() {
        setLoadingText(R.string.ui_text_user_information_loading);
        displayLoading(true);
        UserRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<User>() {
            @Override
            public void onResponse(User user, boolean success, String message) {
                super.onResponse(user, success, message);
                if (success) {
                    if (null != query && null != query.getUserMmt()) {
                        simpleMoments.clear();
                        simpleMoments.addAll(query.getUserMmt());
                    }
                    if (null != user) {
                        if (isMe()) {
                            // 随时更新我的信息
                            Cache.cache().setCurrentUser(user);
                            Cache.cache().saveCurrentUser();
                        }
                        checkUser(user);
                    } else {
                        ToastHelper.make().showMsg(message);
                    }
                }
                displayLoading(false);
            }
        }).find(mQueryId, true);
    }

    private void checkUser(final User user) {
        // 自己和自己不能聊天
        //toChat.setVisibility(isMe() ? View.GONE : View.VISIBLE);
        toChat.setVisibility(View.GONE);

        final String invalid = StringHelper.getString(R.string.ui_base_text_not_set);
        // 头像部分
        if (mAdapter.getItemCount() < 1) {
            mAdapter.add(user);
        } else {
            mAdapter.update(user);
        }
//        if (user.getId().equals(Cache.cache().userId)) {
//            rightTextView.setText(R.string.ui_base_text_edit);
//        } else {
//            rightTextView.setText(null);
//        }

        // 动态
        if (mAdapter.getItemCount() < 2) {
            mAdapter.add(new SimpleClickableItem(format(items[1], "")) {{
                setId(format(items[1], ""));
            }});
        } else {
            mAdapter.notifyItemChanged(1);
        }

        // 性别
        if (mAdapter.getItemCount() < 3) {
            mAdapter.add(new Model() {{
                setId(format(items[2], isEmpty(user.getSex()) ? invalid : user.getSex()));
            }});
        } else {
            mAdapter.get(2).setId(format(items[2], isEmpty(user.getSex()) ? invalid : user.getSex()));
            mAdapter.notifyItemChanged(2);
        }

        // 生日
        birthday = user.getBirthday();
        if (mAdapter.getItemCount() < 4) {
            mAdapter.add(new Model() {{
                setId(format(items[3], getBirthday(user)));
            }});
        } else {
            mAdapter.get(3).setId(format(items[3], getBirthday(user)));
            mAdapter.notifyItemChanged(3);
        }

        // 身份证
        if (mAdapter.getItemCount() < 5) {
            mAdapter.add(new Model() {{
                setId(format(items[4], isEmpty(user.getIdNum()) ? invalid : user.getIdNum()));
            }});
        } else {
            mAdapter.get(4).setId(format(items[4], isEmpty(user.getIdNum()) ? invalid : user.getIdNum()));
            mAdapter.notifyItemChanged(4);
        }

        // 单位
        if (mAdapter.getItemCount() < 6) {
            mAdapter.add(new Model() {{
                setId(format(items[5], isEmpty(user.getCompany()) ? invalid : user.getCompany()));
            }});
        } else {
            mAdapter.get(5).setId(format(items[5], isEmpty(user.getCompany()) ? invalid : user.getCompany()));
            mAdapter.notifyItemChanged(5);
        }

        // 职务
        if (mAdapter.getItemCount() < 7) {
            mAdapter.add(new Model() {{
                setId(format(items[6], isEmpty(user.getPosition()) ? invalid : user.getPosition()));
            }});
        } else {
            mAdapter.get(6).setId(format(items[6], isEmpty(user.getPosition()) ? invalid : user.getPosition()));
            mAdapter.notifyItemChanged(6);
        }

        // 注册时间
        if (mAdapter.getItemCount() < 8) {
            mAdapter.add(new Model() {{
                setId(format(items[7], isEmpty(user.getCreateDate()) ? "-" : user.getCreateDate().substring(0, 10)));
            }});
        }

        // 电话
        if (mAdapter.getItemCount() < 9) {
            mAdapter.add(new Model() {{
                setId(format(items[8], isEmpty(user.getPhone()) ? invalid : user.getPhone()));
            }});
        } else {
            mAdapter.get(8).setId(format(items[8], isEmpty(user.getPhone()) ? invalid : user.getPhone()));
            mAdapter.notifyItemChanged(8);
        }
    }

    private String getBirthday(User user) {
        String text = user.getBirthday();
        final String invalid = StringHelper.getString(R.string.ui_base_text_not_set);
        return isEmpty(user.getBirthday()) ? invalid : (text.length() < 10 ? text : text.substring(0, 10));
    }

    private static final int REQUEST_PHONE = ACTIVITY_BASE_REQUEST + 10;
    private static final int REQUEST_PHONE_CONFIRM = REQUEST_PHONE + 1;
    private static final int REQUEST_ID = REQUEST_PHONE + 2;
    private static final int REQUEST_COMPANY = REQUEST_PHONE + 3;
    private static final int REQUEST_DUTY = REQUEST_PHONE + 4;
    private static final int REQUEST_NAME = REQUEST_PHONE + 5;
    private static final int REQUEST_SIGNATURE = REQUEST_PHONE + 6;

    private void checkClickType(int index) {
        User user = (User) mAdapter.get(0);
        String value;
        switch (index) {
            case -2:
                // 头像选择
                openImageSelector();
                break;
            case -1:
                // 修改姓名
                value = user.getName();
                if (isEmpty(value)) {
                    value = "";
                }
                openActivity(BasePopupInputSupportFragment.class.getName(), StringHelper.getString(R.string.ui_popup_input_name, value), REQUEST_NAME, true, false);
                break;
            case 0:
                // 修改个性签名
                value = user.getSignature();
                if (isEmpty(value)) {
                    value = "";
                }
                openActivity(BasePopupInputSupportFragment.class.getName(), StringHelper.getString(R.string.ui_popup_input_signature, value), REQUEST_SIGNATURE, true, false);
                break;
            case 2:
                // 性别修改
                openSexPicker();
                break;
            case 3:
                // 生日选择
                openDatePicker();
                break;
            case 4:
                // 身份证号码
                value = user.getIdNum();
                if (isEmpty(value)) {
                    value = "";
                }
                openActivity(BasePopupInputSupportFragment.class.getName(), StringHelper.getString(R.string.ui_popup_input_id_number, value), REQUEST_ID, true, false);
                break;
            case 5:
                // 工作单位
                value = user.getCompany();
                if (isEmpty(value)) {
                    value = "";
                }
                openActivity(BasePopupInputSupportFragment.class.getName(), StringHelper.getString(R.string.ui_popup_input_company, value), REQUEST_COMPANY, true, false);
                break;
            case 6:
                // 职务
                value = user.getPosition();
                if (isEmpty(value)) {
                    value = "";
                }
                openActivity(BasePopupInputSupportFragment.class.getName(), StringHelper.getString(R.string.ui_popup_input_duty, value), REQUEST_DUTY, true, false);
                break;
            case 8:
                // 修改手机号
                openActivity(ModifyPhoneFragment.class.getName(), user.getPhone(), REQUEST_PHONE, true, false);
                break;
        }
    }

    @Override
    public void onActivityResult(int requestCode, Intent data) {
        String result;
        switch (requestCode) {
            case REQUEST_PHONE:
                result = getResultedData(data);
                if (!isEmpty(result)) {
                    // 输入的手机号码不为空时
                    openActivity(CodeVerifyFragment.class.getName(), format("%d,%s", CodeVerifyFragment.VT_MODIFY_PHONE, result), REQUEST_PHONE_CONFIRM, true, false);
                }
                break;
            case REQUEST_PHONE_CONFIRM:
                // 手机号码修改成功了
                mAdapter.get(8).setId(format(items[8], Cache.cache().me.getPhone()));
                mAdapter.notifyItemChanged(8);
                break;
            case REQUEST_ID:
                // 身份证号码末尾的 X 字母需要大写
                result = getResultedData(data).toUpperCase(Locale.getDefault());
                tryEditUserInfo(UserRequest.UPDATE_ID_NUM, result);
                break;
            case REQUEST_COMPANY:
                result = getResultedData(data);
                tryEditUserInfo(UserRequest.UPDATE_COMPANY, result);
                break;
            case REQUEST_DUTY:
                result = getResultedData(data);
                tryEditUserInfo(UserRequest.UPDATE_DUTY, result);
                break;
            case REQUEST_NAME:
                result = getResultedData(data);
                tryEditUserInfo(UserRequest.UPDATE_NAME, result);
                break;
            case REQUEST_SIGNATURE:
                result = getResultedData(data);
                tryEditUserInfo(UserRequest.UPDATE_SIGNATURE, result);
                break;
        }
        super.onActivityResult(requestCode, data);
    }

    private String birthday = "";

    private void openDatePicker() {
        TimePickerView tpv = new TimePickerView.Builder(Activity(), new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                tryEditUserInfo(UserRequest.UPDATE_BIRTHDAY, Utils.format(StringHelper.getString(R.string.ui_base_text_date_format), date));
            }
        }).setType(new boolean[]{true, true, true, false, false, false})
                .setTitleBgColor(getColor(R.color.colorPrimary))
                .setSubmitColor(Color.WHITE)
                .setCancelColor(Color.WHITE)
                .setContentSize(getFontDimension(R.dimen.ui_static_sp_20))
                .setOutSideCancelable(false)
                .isCenterLabel(true).isDialog(false).build();
        if (isEmpty(birthday)) {
            tpv.setDate(Calendar.getInstance());
        } else {
            Calendar calendar = Calendar.getInstance();
            calendar.setTime(Utils.parseDate(StringHelper.getString(R.string.ui_base_text_date_format), birthday));
            tpv.setDate(calendar);
        }
        tpv.show();
    }

    private void openSexPicker() {
        DialogHelper.init(Activity()).addOnDialogInitializeListener(new DialogHelper.OnDialogInitializeListener() {
            @Override
            public View onInitializeView() {
                return LayoutInflater.from(mRecyclerView.getContext()).inflate(R.layout.popup_dialog_sex_selector, null);
            }

            @Override
            public void onBindData(View dialogView, DialogHelper helper) {

            }
        }).addOnEventHandlerListener(new DialogHelper.OnEventHandlerListener() {
            @Override
            public int[] clickEventHandleIds() {
                return new int[]{R.id.ui_dialog_button_sex_male, R.id.ui_dialog_button_sex_female};
            }

            @Override
            public boolean onClick(View view) {
                tryEditUserInfo(UserRequest.UPDATE_SEX, StringHelper.getString(view.getId() == R.id.ui_dialog_button_sex_male ? R.string.ui_base_text_sex_male : R.string.ui_base_text_sex_female));
                return true;
            }
        }).setAdjustScreenWidth(true).setPopupType(DialogHelper.TYPE_SLID).show();
    }

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

    private void resetUserInformation(int type, String value) {
        switch (type) {
            case UserRequest.UPDATE_BIRTHDAY:
                mAdapter.get(3).setId(format(items[3], value));
                mAdapter.notifyItemChanged(3);
                break;
            case UserRequest.UPDATE_COMPANY:
                mAdapter.get(5).setId(format(items[5], value));
                mAdapter.notifyItemChanged(5);
                break;
            case UserRequest.UPDATE_DUTY:
                mAdapter.get(6).setId(format(items[6], value));
                mAdapter.notifyItemChanged(6);
                break;
            case UserRequest.UPDATE_EMAIL:
                break;
            case UserRequest.UPDATE_ID_NUM:
                mAdapter.get(4).setId(format(items[4], value));
                mAdapter.notifyItemChanged(4);
                break;
            case UserRequest.UPDATE_NAME:
                ((User) mAdapter.get(0)).setName(value);
                mAdapter.notifyItemChanged(0);
                break;
            case UserRequest.UPDATE_PHONE:
                mAdapter.get(8).setId(format(items[8], value));
                mAdapter.notifyItemChanged(8);
                break;
            case UserRequest.UPDATE_PHOTO:
                ((User) mAdapter.get(0)).setHeadPhoto(value);
                mAdapter.notifyItemChanged(0);
                break;
            case UserRequest.UPDATE_SEX:
                mAdapter.get(2).setId(format(items[2], value));
                mAdapter.notifyItemChanged(2);
                break;
            case UserRequest.UPDATE_SIGNATURE:
                ((User) mAdapter.get(0)).setSignature(value);
                mAdapter.notifyItemChanged(0);
                break;
        }
    }

    private class MyPropertyAdapter extends RecyclerViewAdapter<BaseViewHolder, Model> {

        private static final int VT_HEADER = 0, VT_MOMENT = 1, VT_CLICK = 2;

        @Override
        public BaseViewHolder onCreateViewHolder(View itemView, int viewType) {
            switch (viewType) {
                case VT_HEADER:
                    UserHeaderBigViewHolder uhbvh = new UserHeaderBigViewHolder(itemView, UserPropertyFragment.this);
                    uhbvh.addOnViewHolderClickListener(onViewHolderClickListener);
                    return uhbvh;
                case VT_MOMENT:
                    UserSimpleMomentViewHolder usmvh = new UserSimpleMomentViewHolder(itemView, UserPropertyFragment.this);
                    usmvh.addOnViewHolderClickListener(onViewHolderClickListener);
                    return usmvh;
                default:
                    SimpleClickableViewHolder scvh = new SimpleClickableViewHolder(itemView, UserPropertyFragment.this);
                    scvh.addOnViewHolderClickListener(onViewHolderClickListener);
                    return scvh;
            }
        }

        @Override
        public int getItemViewType(int position) {
            switch (position) {
                case 0:
                    return VT_HEADER;
                case 1:
                    return VT_MOMENT;
                default:
                    return VT_CLICK;
            }
        }

        @Override
        public int itemLayout(int viewType) {
            switch (viewType) {
                case VT_HEADER:
                    return R.layout.holder_view_individual_header_big;
                case VT_MOMENT:
                    return R.layout.holder_view_user_simple_moment;
                default:
                    return R.layout.holder_view_simple_clickable;
            }
        }

        @Override
        public void onBindHolderOfView(BaseViewHolder holder, int position, @Nullable Model item) {
            if (holder instanceof UserHeaderBigViewHolder) {
                ((UserHeaderBigViewHolder) holder).showContent((User) item);
            } else if (holder instanceof UserSimpleMomentViewHolder) {
                ((UserSimpleMomentViewHolder) holder).showContent(simpleMoments);
            } else if (holder instanceof SimpleClickableViewHolder) {
                ((SimpleClickableViewHolder) holder).showContent(item);
            }
        }

        @Override
        protected int comparator(Model item1, Model item2) {
            return 0;
        }
    }
}
