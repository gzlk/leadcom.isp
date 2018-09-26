package com.leadcom.android.isp.fragment.login;

import android.Manifest;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.view.View;

import com.leadcom.android.isp.R;
import com.leadcom.android.isp.api.common.SystemRequest;
import com.leadcom.android.isp.api.listener.OnSingleRequestListener;
import com.leadcom.android.isp.api.user.UserRequest;
import com.leadcom.android.isp.application.App;
import com.leadcom.android.isp.cache.Cache;
import com.leadcom.android.isp.etc.Utils;
import com.leadcom.android.isp.fragment.base.BaseDelayRefreshSupportFragment;
import com.leadcom.android.isp.helper.StringHelper;
import com.leadcom.android.isp.helper.ToastHelper;
import com.leadcom.android.isp.helper.popup.DialogHelper;
import com.leadcom.android.isp.helper.popup.SimpleDialogHelper;
import com.leadcom.android.isp.lib.permission.MPermission;
import com.leadcom.android.isp.lib.permission.annotation.OnMPermissionDenied;
import com.leadcom.android.isp.lib.permission.annotation.OnMPermissionGranted;
import com.leadcom.android.isp.lib.permission.annotation.OnMPermissionNeverAskAgain;
import com.leadcom.android.isp.model.user.User;
import com.hlk.hlklib.lib.inject.Click;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.view.CleanableEditText;
import com.hlk.hlklib.lib.view.ClearEditText;
import com.hlk.hlklib.lib.view.CorneredButton;

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
    @ViewId(R.id.ui_sign_in_password)
    private CleanableEditText passwordText;
    @ViewId(R.id.ui_sign_in_to_sign_in)
    private CorneredButton signInButton;

    private boolean stillInSignIn = false;

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

    private void signIn(String account, String password) {
        SystemRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<User>() {

            @SuppressWarnings("ConstantConditions")
            @Override
            public void onResponse(User user, boolean success, String message) {
                super.onResponse(user, success, message);
                // 检测服务器返回的状态
                if (success) {
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
