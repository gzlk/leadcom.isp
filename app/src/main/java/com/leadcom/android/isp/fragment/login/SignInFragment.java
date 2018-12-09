package com.leadcom.android.isp.fragment.login;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.hlk.hlklib.layoutmanager.CustomLinearLayoutManager;
import com.hlk.hlklib.lib.inject.Click;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.view.CleanableEditText;
import com.hlk.hlklib.lib.view.ClearEditText;
import com.hlk.hlklib.lib.view.CorneredButton;
import com.leadcom.android.isp.R;
import com.leadcom.android.isp.adapter.RecyclerViewAdapter;
import com.leadcom.android.isp.api.common.SystemRequest;
import com.leadcom.android.isp.api.listener.OnSingleRequestListener;
import com.leadcom.android.isp.api.user.UserRequest;
import com.leadcom.android.isp.application.App;
import com.leadcom.android.isp.cache.Cache;
import com.leadcom.android.isp.etc.Utils;
import com.leadcom.android.isp.fragment.base.BaseDelayRefreshSupportFragment;
import com.leadcom.android.isp.helper.PreferenceHelper;
import com.leadcom.android.isp.helper.StringHelper;
import com.leadcom.android.isp.helper.ToastHelper;
import com.leadcom.android.isp.helper.popup.DialogHelper;
import com.leadcom.android.isp.helper.popup.SimpleDialogHelper;
import com.leadcom.android.isp.holder.common.AccountItemViewHolder;
import com.leadcom.android.isp.lib.Json;
import com.leadcom.android.isp.lib.permission.MPermission;
import com.leadcom.android.isp.lib.permission.annotation.OnMPermissionDenied;
import com.leadcom.android.isp.lib.permission.annotation.OnMPermissionGranted;
import com.leadcom.android.isp.lib.permission.annotation.OnMPermissionNeverAskAgain;
import com.leadcom.android.isp.listener.OnViewHolderElementClickListener;
import com.leadcom.android.isp.model.Model;
import com.leadcom.android.isp.model.user.User;
import com.leadcom.android.isp.view.SwipeItemLayout;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;

/**
 * <b>功能描述：</b>登录页面<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/04/10 20:57 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/04/10 20:57 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class SignInFragment extends BaseDelayRefreshSupportFragment {

    private static final String PARAM_STILL_SIGN_IN = "sif_still_in_sign_in";
    @ViewId(R.id.ui_sign_in_account)
    private ClearEditText accountText;
    @ViewId(R.id.ui_sign_in_account_input_inputted)
    private TextView inputtedText;
    @ViewId(R.id.ui_sign_in_account_input_rest_stub)
    private TextView restText;
    @ViewId(R.id.ui_sign_in_accounts_layout)
    private View accountsView;
    @ViewId(R.id.ui_tool_swipe_refreshable_recycler_view)
    private RecyclerView recyclerView;
    @ViewId(R.id.ui_sign_in_password)
    private CleanableEditText passwordText;
    @ViewId(R.id.ui_sign_in_to_sign_in)
    private CorneredButton signInButton;

    private boolean stillInSignIn = false, isEditable = false;
    private ArrayList<String> loginedAccounts;

    @Override
    protected void getParamsFromBundle(Bundle bundle) {
        super.getParamsFromBundle(bundle);
        stillInSignIn = bundle.getBoolean(PARAM_STILL_SIGN_IN, false);
    }

    @Override
    protected void saveParamsToBundle(Bundle bundle) {
        super.saveParamsToBundle(bundle);
        bundle.putBoolean(PARAM_STILL_SIGN_IN, stillInSignIn);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        String json = PreferenceHelper.get(Cache.get(R.string.pf_last_login_user_accounts, R.string.pf_last_login_user_accounts_beta), "[]");
        loginedAccounts = Json.gson().fromJson(json, new TypeToken<ArrayList<String>>() {
        }.getType());
        if (loginedAccounts.size() > 0) {
            Collections.sort(loginedAccounts, new Comparator<String>() {
                @Override
                public int compare(String o1, String o2) {
                    return o1.compareTo(o2);
                }
            });
        }
        CustomLinearLayoutManager layoutManager = new CustomLinearLayoutManager(recyclerView.getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.addOnItemTouchListener(new SwipeItemLayout.OnSwipeItemTouchListener(recyclerView.getContext()));
        mAdapter = new AccountAdapter();
        recyclerView.setAdapter(mAdapter);
        accountText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                inputtedAccount = "";
                String saved = "";
                if (s.length() >= 2) {
                    inputtedAccount = s.toString();
                    saved = getSameAs(inputtedAccount);
                }
                if (!isEmpty(saved)) {
                    inputtedText.setText(inputtedAccount);
                    restText.setText(saved.replace(inputtedAccount, ""));
                    if (isEditable) {
                        if (inputtedAccount.equals(saved)) {
                            showPopupListAccounts(false, duration());
                        } else {
                            showPopupListAccounts(true, duration());
                        }
                    }
                } else {
                    inputtedText.setText("");
                    restText.setText("");
                    if (accountsView.getAlpha() >= 0) {
                        showPopupListAccounts(false, duration());
                    }
                }
            }
        });
        showPopupListAccounts(false, 0);
    }

    private String getSameAs(String text) {
        if (loginedAccounts.size() > 0) {
            for (String string : loginedAccounts) {
                if (string.contains(text)) return string;
            }
        }
        return "";
    }

    private String inputtedAccount = "";
    private AccountAdapter mAdapter;
    private boolean isAnimating = false, isShowing = false;

    private void showPopupListAccounts(final boolean shown, long duration) {
        if (isAnimating) {
            return;
        }
        if (shown && isShowing) {
            // 已处于显示状态时，不需要再次进行动画显示，直接显示结果变化
            filterInputted();
            return;
        }
        if (!shown && !isShowing) {
            // 已处于隐藏状态时，不需要再次进行动画隐藏
            return;
        }
        accountsView.animate().alpha(shown ? 1.0f : 0.0f)
                //.translationY(shown ? 0 : -accountsView.getMeasuredHeight() * 1.1f)
                .setDuration(duration).setListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (!shown) {
                    accountsView.setVisibility(View.GONE);
                } else {
                    filterInputted();
                }
                isAnimating = false;
                isShowing = accountsView.getVisibility() == View.VISIBLE;
            }

            @Override
            public void onAnimationStart(Animator animation) {
                isAnimating = true;
                super.onAnimationStart(animation);
                if (shown) {
                    accountsView.setVisibility(View.VISIBLE);
                }
            }
        }).start();
    }

    private void filterInputted() {
        ArrayList<String> temp = new ArrayList<>();
        for (String string : loginedAccounts) {
            if (string.contains(inputtedAccount)) {
                temp.add(string);
            }
        }
        Iterator<Model> iterator = mAdapter.iterator();
        int index = 0;
        while (iterator.hasNext()) {
            Model model = iterator.next();
            if (!temp.contains(model.getId())) {
                iterator.remove();
                mAdapter.notifyItemRemoved(index);
            }
            index++;
        }
        for (String string : temp) {
            Model model = new Model();
            model.setId(string);
            mAdapter.update(model);
        }
        if (mAdapter.getItemCount() <= 0) {
            showPopupListAccounts(false, duration());
        }
    }

    private OnViewHolderElementClickListener elementClickListener = new OnViewHolderElementClickListener() {
        @Override
        public void onClick(View view, int index) {
            Model model = mAdapter.get(index);
            switch (view.getId()) {
                case R.id.ui_holder_view_account_item_layout:
                    // 选择
                    accountText.setValue(model.getId());
                    showPopupListAccounts(false, duration());
                    accountText.focusEnd();
                    break;
                case R.id.ui_tool_view_contact_button2:
                    // 删除
                    loginedAccounts.remove(model.getId());
                    mAdapter.remove(model);
                    saveLoginedAccounts();
                    break;
            }
        }
    };

    private class AccountAdapter extends RecyclerViewAdapter<AccountItemViewHolder, Model> {

        @Override
        public AccountItemViewHolder onCreateViewHolder(View itemView, int viewType) {
            AccountItemViewHolder aivh = new AccountItemViewHolder(itemView, SignInFragment.this);
            aivh.setOnViewHolderElementClickListener(elementClickListener);
            return aivh;
        }

        @Override
        public int itemLayout(int viewType) {
            return R.layout.holder_view_account_item_deletable;
        }

        @Override
        public void onBindHolderOfView(AccountItemViewHolder holder, int position, @Nullable Model item) {
            assert item != null;
            holder.showContent(item.getId(), inputtedAccount);
        }

        @Override
        protected int comparator(Model item1, Model item2) {
            return item1.getId().compareTo(item2.getId());
        }
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestStoragePermission();
    }

    private final String[] permissions = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE
    };

    private void requestStoragePermission() {
        MPermission.printMPermissionResult(true, Activity(), permissions);
        MPermission.with(this)
                .setRequestCode(GRANT_STORAGE)
                .permissions(permissions)
                .request();
    }

    @OnMPermissionGranted(GRANT_STORAGE)
    private void onStoragePermissionGranted() {
        MPermission.printMPermissionResult(false, Activity(), permissions);
        if (null != signInButton) {
            signInButton.setEnabled(true);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        MPermission.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }

    @OnMPermissionDenied(GRANT_STORAGE)
    private void onStoragePermissionDenied() {
        ToastHelper.make().showMsg(R.string.ui_grant_permission_storage_denied);
    }

    @OnMPermissionNeverAskAgain(GRANT_STORAGE)
    private void onStoragePermissionNeverAskAgain() {
        ToastHelper.make().showMsg(R.string.ui_grant_permission_storage_never_ask_again);
        if (null != signInButton) {
            //signInButton.setEnabled(false);
        }
    }

    @Override
    public int getLayout() {
        return R.layout.fragment_sign_in;
    }

    @Override
    public void doingInResume() {
        if (hasPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            isEditable = false;
            if (null != Cache.cache().me && isAdded()) {
                accountText.setValue(Cache.cache().me.getPhone());
                accountText.focusEnd();
                signInButton.setEnabled(false);
                signInButton.setText(R.string.ui_text_sign_in_still_processing);
                // 同步用户信息，如果同步失败则需要重新登录
                syncUserInfo();
            } else {
                accountText.setValue(Cache.cache().userPhone);
                if (!isEmpty(Cache.cache().userPhone)) {
                    passwordText.requestFocus();
                }
            }
            isEditable = true;
        } else {
            // 尝试获取相关基本的运行时权限
            //signInButton.setEnabled(false);
        }
    }

    @Override
    protected boolean shouldSetDefaultTitleEvents() {
        return false;
    }

    @Override
    protected void destroyView() {

    }

    @Click({R.id.ui_sign_in_to_sign_up, R.id.ui_sign_in_to_retrieve_password,
            R.id.ui_sign_in_to_sign_in})
    private void click(View view) {
        int id = view.getId();
        if (id == R.id.ui_sign_in_to_sign_in) {
            // 登录
            String account = accountText.getValue();
            String pwd = passwordText.getText().toString();
            if (!passwordText.verifyValue()) {
                pwd = "";
            }
            if (isEmpty(account)) {
                ToastHelper.make(Activity()).showMsg(R.string.ui_text_sign_in_account_value_incorrect);
            } else if (isEmpty(pwd)) {
                ToastHelper.make(Activity()).showMsg(R.string.ui_text_sign_in_password_value_incorrect);
            } else {
                if (!hasPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    String text = StringHelper.getString(R.string.ui_grant_permission_storage_warning);
                    SimpleDialogHelper.init(Activity()).show(text, new DialogHelper.OnDialogConfirmListener() {
                        @Override
                        public boolean onConfirm() {
                            requestStoragePermission();
                            return true;
                        }
                    }, null);
                    return;
                }
                Utils.hidingInputBoard(accountText);
                if (!stillInSignIn) {
                    stillInSignIn = true;
                    signInButton.setEnabled(false);
                    signInButton.setText(R.string.ui_text_sign_in_still_processing);
                    // 开始登录
                    signIn(account, pwd);
                } else {
                    ToastHelper.make().showMsg(R.string.ui_text_sign_in_still_processing);
                }
            }
        } else {
            String params = String.valueOf(id == R.id.ui_sign_in_to_sign_up ? PhoneVerifyFragment.VT_SIGN_UP : PhoneVerifyFragment.VT_PASSWORD);
            openActivity(PhoneVerifyFragment.class.getName(), params, true, true);
        }
    }

    @Override
    protected void onDelayRefreshComplete(@DelayType int type) {
        if (type == DELAY_TYPE_TIME_DELAY) {
            if (!isAdded()) {
                delayRefreshLoading(1000, DELAY_TYPE_TIME_DELAY);
            } else {
                finish(true);
            }
        }
    }

    /**
     * 需要重新登录
     */
    private void needToReLogin() {
        stillInSignIn = false;
        signInButton.setEnabled(true);
        signInButton.setText(R.string.ui_base_text_login);
    }

    private void syncUserInfo() {
        UserRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<User>() {
            @Override
            public void onResponse(User user, boolean success, String message) {
                super.onResponse(user, success, message);
                if (success) {
                    cacheUser(user);
                    // 同步成功之后检测网易云登录状态
                    checkNimStatus();
                } else {
                    ToastHelper.make().showMsg(message);
                    needToReLogin();
                }
            }
        }).find(Cache.cache().userId, "", true);
    }

    private void cacheUser(User user) {
        Cache.cache().setCurrentUser(user);
        Cache.cache().saveCurrentUser();
        App.app().setJPushAlias();
    }

    private void saveLoginedAccounts() {
        PreferenceHelper.save(Cache.get(R.string.pf_last_login_user_accounts, R.string.pf_last_login_user_accounts_beta), Json.gson().toJson(loginedAccounts));
    }

    private void signIn(final String account, String password) {
        SystemRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<User>() {

            @SuppressWarnings("ConstantConditions")
            @Override
            public void onResponse(User user, boolean success, String message) {
                super.onResponse(user, success, message);
                // 检测服务器返回的状态
                if (success) {
                    if (!loginedAccounts.contains(account)) {
                        loginedAccounts.add(account);
                        saveLoginedAccounts();
                    }
                    // 这里尝试访问一下全局me以便及时更新已登录的用户的信息
                    cacheUser(user);
                    // 保存用户关联的所有组织列表
                    App.app().fetchPermissions();
                    // 登录成功之后检测网易云账号登录状态
                    checkNimStatus();
                } else {
                    needToReLogin();
                }
            }
        }).signIn(account, password);
    }

    private void checkNimStatus() {
        // 如不需要重新登录网易云则进入主页面
        delayRefreshLoading(1000, DELAY_TYPE_TIME_DELAY);
    }

    private void grantStoragePermission() {
        signInButton.setEnabled(false);
        String text = StringHelper.getString(R.string.ui_grant_permission_storage_warning);
        String denied = StringHelper.getString(R.string.ui_grant_permission_storage_denied);
        tryGrantPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, GRANT_STORAGE, text, denied);
    }
}
