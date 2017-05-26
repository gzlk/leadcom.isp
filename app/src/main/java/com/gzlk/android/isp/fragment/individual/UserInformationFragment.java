package com.gzlk.android.isp.fragment.individual;

import android.content.Intent;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bigkoo.pickerview.TimePickerView;
import com.gzlk.android.isp.R;
import com.gzlk.android.isp.api.listener.OnSingleRequestListener;
import com.gzlk.android.isp.api.user.UserRequest;
import com.gzlk.android.isp.cache.Cache;
import com.gzlk.android.isp.etc.Utils;
import com.gzlk.android.isp.fragment.base.BasePopupInputSupportFragment;
import com.gzlk.android.isp.fragment.base.BaseSwipeRefreshSupportFragment;
import com.gzlk.android.isp.fragment.login.CodeVerifyFragment;
import com.gzlk.android.isp.helper.DialogHelper;
import com.gzlk.android.isp.helper.StringHelper;
import com.gzlk.android.isp.helper.ToastHelper;
import com.gzlk.android.isp.listener.OnViewHolderClickListener;
import com.gzlk.android.isp.model.Dao;
import com.gzlk.android.isp.model.Model;
import com.gzlk.android.isp.model.SimpleClickableItem;
import com.gzlk.android.isp.model.user.User;
import com.gzlk.android.isp.multitype.adapter.BaseMultiTypeAdapter;
import com.gzlk.android.isp.multitype.binder.SimpleClickableViewBinder;
import com.gzlk.android.isp.multitype.binder.user.UserHeaderBigViewBinder;
import com.gzlk.android.isp.multitype.binder.user.UserSimpleMomentViewBinder;
import com.hlk.hlklib.lib.inject.Click;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.view.CustomTextView;

import java.util.Calendar;
import java.util.Date;

/**
 * <b>功能描述：</b>用户的基本信息<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/05/02 08:02 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/05/02 08:02 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class UserInformationFragment extends BaseSwipeRefreshSupportFragment {

    public static UserInformationFragment newInstance(String params) {
        UserInformationFragment mf = new UserInformationFragment();
        Bundle bundle = new Bundle();
        bundle.putString(PARAM_QUERY_ID, params);
        mf.setArguments(bundle);
        return mf;
    }

    // UI
    @ViewId(R.id.ui_main_tool_bar_container)
    private View titleView;
    @ViewId(R.id.ui_main_tool_bar_background)
    private View titleBackground;
    @ViewId(R.id.ui_ui_custom_title_text)
    private TextView titleTextView;
    @ViewId(R.id.ui_ui_custom_title_right_icon_1)
    private CustomTextView rightIcon1;
    @ViewId(R.id.ui_ui_custom_title_right_icon_2_container)
    private RelativeLayout rightIcon2;
    @ViewId(R.id.ui_ui_custom_title_right_text)
    private TextView rightTextView;
    @ViewId(R.id.ui_ui_custom_title_right_icon)
    private CustomTextView rightIcon;

    private String[] items;
    private MyAdapter mAdapter;

    @Override
    protected void onDelayRefreshComplete(@DelayType int type) {

    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void doingInResume() {
        rightIcon1.setVisibility(View.GONE);
        rightIcon2.setVisibility(View.GONE);
        setSupportLoadingMore(false);
        initializeItems();
        titleTextView.setText(null);
        rightIcon.setText(null);
        tryPaddingContent(titleView, false);
        tryPaddingContent(titleBackground, false);
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
        return R.layout.fragment_user_information;
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

    @Click({R.id.ui_ui_custom_title_left_container, R.id.ui_ui_custom_title_right_container})
    private void elementClick(View view) {
        switch (view.getId()) {
            case R.id.ui_ui_custom_title_left_container:
                resultSucceededActivity();
                break;
            case R.id.ui_ui_custom_title_right_container:
                toEdit();
                break;
        }
    }

    @SuppressWarnings("ConstantConditions")
    private void toEdit() {
        User user = (User) mAdapter.get(0);
        if (user.getId().equals(Cache.cache().userId)) {
            user.setLocalDeleted(!user.isLocalDeleted());
            mAdapter.notifyItemChanged(0);
        }
    }

    private RecyclerView.OnScrollListener scrollListener = new RecyclerView.OnScrollListener() {
        private int scrolledY = 0;

        @Override
        public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
            super.onScrollStateChanged(recyclerView, newState);
        }

        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);
            if (!isViewPagerDisplayedCurrent()) {
                return;
            }
            scrolledY += dy;
            if (scrolledY >= 0 && scrolledY <= 500) {
                float alpha = scrolledY * 0.005f;
                if (null != titleBackground) {
                    titleBackground.setAlpha(alpha);
                }
            }
        }
    };

    private void initializeItems() {
        if (null == items) {
            items = StringHelper.getStringArray(R.array.ui_text_my_setting_items);
        }
        if (null == mAdapter) {
            mRecyclerView.addOnScrollListener(scrollListener);
            mRecyclerView.addItemDecoration(new SpacesItemDecoration());
            mAdapter = new MyAdapter();
            mAdapter.register(User.class, new UserHeaderBigViewBinder(onViewHolderClickListener).setFragment(this));
            mAdapter.register(SimpleClickableItem.class, new UserSimpleMomentViewBinder().setFragment(this));
            mAdapter.register(Model.class, new SimpleClickableViewBinder(onViewHolderClickListener).setFragment(this));
            mRecyclerView.setAdapter(mAdapter);
            titleBackground.setAlpha(0);
            fetchingUser();
        }
    }

    private OnViewHolderClickListener onViewHolderClickListener = new OnViewHolderClickListener() {
        @Override
        public void onClick(int index) {
            checkClickType(index);
        }
    };

    private void fetchingUser() {
        User user = new Dao<>(User.class).query(mQueryId);
        if (null == user) {
            fetchingRemoteUser();
        } else {
            checkUser(user);
        }
    }

    private void fetchingRemoteUser() {
        UserRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<User>() {
            @Override
            public void onResponse(User user, boolean success, String message) {
                super.onResponse(user, success, message);
                if (success) {
                    if (null != user && !StringHelper.isEmpty(user.getId())) {
                        //mAdapter.clear();
                        new Dao<>(User.class).save(user);
                        if (mAdapter.getItemCount() < 1) {
                            checkUser(user);
                        }
                    } else {
                        ToastHelper.make().showMsg(message);
                    }
                }
            }
        }).find(mQueryId);
    }

    @SuppressWarnings("ConstantConditions")
    private void checkUser(final User user) {
        mAdapter.add(user);
        if (user.getId().equals(Cache.cache().userId)) {
            rightTextView.setText(R.string.ui_base_text_edit);
        } else {
            rightTextView.setText(null);
        }
        // 动态
        mAdapter.add(new SimpleClickableItem(format(items[1], "")) {{
            setId(format(items[1], ""));
        }});
        // 性别
        mAdapter.add(new Model() {{
            setId(format(items[2], StringHelper.isEmpty(user.getSex()) ? "未设置" : user.getSex()));
        }});
        // 生日
        mAdapter.add(new Model() {{
            birthday = user.getBirthday();
            setId(format(items[3], StringHelper.isEmpty(user.getBirthday()) ? "未设置" : user.getBirthday().substring(0, 10)));
        }});
        // 身份证
        mAdapter.add(new Model() {{
            setId(format(items[4], "未设置"));
        }});
        // 单位
        mAdapter.add(new Model() {{
            setId(format(items[5], "未设置"));
        }});
        // 职务
        mAdapter.add(new Model() {{
            setId(format(items[6], "未设置"));
        }});
        // 注册时间
        mAdapter.add(new Model() {{
            setId(format(items[7], StringHelper.isEmpty(user.getCreateDate()) ? "" : user.getCreateDate().substring(0, 10)));
        }});
        // 电话
        mAdapter.add(new Model() {{
            setId(format(items[8], user.getPhone()));
        }});
    }

    private static final int REQUEST_PHONE = ACTIVITY_BASE_REQUEST + 10;
    private static final int REQUEST_PHONE_CONFIRM = REQUEST_PHONE + 1;
    private static final int REQUEST_ID = REQUEST_PHONE + 2;
    private static final int REQUEST_COMPANY = REQUEST_PHONE + 3;
    private static final int REQUEST_DUTY = REQUEST_PHONE + 4;
    private static final int REQUEST_NAME = REQUEST_PHONE + 5;

    private void checkClickType(int index) {
        User user = (User) mAdapter.get(0);
        switch (index) {
            case 0:
                // 修改昵称
                String name = user.getName();
                if (StringHelper.isEmpty(name)) {
                    name = "";
                }
                openActivity(BasePopupInputSupportFragment.class.getName(), StringHelper.getString(R.string.ui_popup_input_name, name), REQUEST_NAME, true, false);
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
                openActivity(BasePopupInputSupportFragment.class.getName(), StringHelper.getString(R.string.ui_popup_input_id_number), REQUEST_ID, true, false);
                break;
            case 5:
                // 工作单位
                openActivity(BasePopupInputSupportFragment.class.getName(), StringHelper.getString(R.string.ui_popup_input_company), REQUEST_COMPANY, true, false);
                break;
            case 6:
                // 职务
                openActivity(BasePopupInputSupportFragment.class.getName(), StringHelper.getString(R.string.ui_popup_input_duty), REQUEST_DUTY, true, false);
                break;
            case 8:
                // 修改手机号
                openActivity(ModifyPhoneFragment.class.getName(), user.getPhone(), REQUEST_PHONE, true, false);
                break;
        }
    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onActivityResult(int requestCode, Intent data) {
        String result = "";
        switch (requestCode) {
            case REQUEST_PHONE:
                result = getResultedData(data);
                if (!StringHelper.isEmpty(result)) {
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
            case REQUEST_COMPANY:
            case REQUEST_DUTY:
                result = getResultedData(data);
                if (requestCode == REQUEST_ID) {
                    if (result.length() == 14 || result.length() == 17) {
                        result = result + "X";
                    }
                }
                log("inputed: " + result);
                ToastHelper.make().showMsg("暂时不支持修改");
                break;
            case REQUEST_NAME:
                result = getResultedData(data);
                tryEditUserInfo(UserRequest.TYPE_NAME, result);
                break;
        }
        super.onActivityResult(requestCode, data);
    }

    private String birthday = "";

    private void openDatePicker() {
        TimePickerView tpv = new TimePickerView.Builder(Activity(), new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                tryEditUserInfo(UserRequest.TYPE_BIRTHDAY, Utils.format(StringHelper.getString(R.string.ui_base_text_date_format), date));
            }
        }).setType(new boolean[]{true, true, true, false, false, false})
                .setTitleBgColor(getColor(R.color.colorPrimary))
                .setSubmitColor(Color.WHITE)
                .setCancelColor(Color.WHITE)
                .setContentSize(getFontDimension(R.dimen.ui_static_sp_20))
                .setOutSideCancelable(false)
                .isCenterLabel(true).isDialog(false).build();
        if (StringHelper.isEmpty(birthday)) {
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
                tryEditUserInfo(UserRequest.TYPE_SEX, StringHelper.getString(view.getId() == R.id.ui_dialog_button_sex_male ? R.string.ui_base_text_sex_male : R.string.ui_base_text_sex_female));
                return true;
            }
        }).setAdjustScreenWidth(true).setPopupType(DialogHelper.TYPE_SLID).show();
    }

    private void tryEditUserInfo(final int type, final String value) {
        UserRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<User>() {
            @Override
            public void onResponse(User user, boolean success, String message) {
                super.onResponse(user, success, message);
                if (success && null != user && !StringHelper.isEmpty(user.getId())) {
                    resetUserInformation(type, value, user);
                }
            }
        }).update(type, value);
    }

    @SuppressWarnings("ConstantConditions")
    private void resetUserInformation(int type, String value, User user) {
        switch (type) {
            case UserRequest.TYPE_BIRTHDAY:
                user.setBirthday(value);
                mAdapter.get(3).setId(format(items[3], user.getBirthday()));
                mAdapter.notifyItemChanged(3);
                break;
            case UserRequest.TYPE_EMAIL:
                user.setEmail(value);
                break;
            case UserRequest.TYPE_NAME:
                user.setName(value);
                ((User) mAdapter.get(0)).setName(value);
                mAdapter.notifyItemChanged(0);
                break;
            case UserRequest.TYPE_PHONE:
                user.setPhone(value);
                mAdapter.get(8).setId(format(items[8], user.getPhone()));
                mAdapter.notifyItemChanged(8);
                break;
            case UserRequest.TYPE_SEX:
                user.setSex(value);
                mAdapter.get(2).setId(format(items[2], user.getSex()));
                mAdapter.notifyItemChanged(2);
                break;
        }
        // 重新拉取远程用户的信息，否则本地有null字段
        fetchingRemoteUser();
    }

    private class SpacesItemDecoration extends RecyclerView.ItemDecoration {

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int position = parent.getChildAdapterPosition(view);
            outRect.bottom = position > 0 ? getDimension(R.dimen.ui_static_dp_half) : 0;
        }
    }

    private class MyAdapter extends BaseMultiTypeAdapter<Model> {
        @Override
        protected int comparator(Model item1, Model item2) {
            return 0;
        }
    }
}
