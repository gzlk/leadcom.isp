package com.gzlk.android.isp.fragment.login;

import android.Manifest;
import android.os.Bundle;
import android.view.View;

import com.gzlk.android.isp.R;
import com.gzlk.android.isp.api.SystemRequest;
import com.gzlk.android.isp.api.listener.OnSingleRequestListener;
import com.gzlk.android.isp.api.user.UserRequest;
import com.gzlk.android.isp.application.App;
import com.gzlk.android.isp.cache.Cache;
import com.gzlk.android.isp.etc.Utils;
import com.gzlk.android.isp.fragment.base.BaseDelayRefreshSupportFragment;
import com.gzlk.android.isp.helper.StringHelper;
import com.gzlk.android.isp.helper.ToastHelper;
import com.gzlk.android.isp.model.user.User;
import com.hlk.hlklib.lib.inject.Click;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.view.CleanableEditText;
import com.hlk.hlklib.lib.view.ClearEditText;
import com.hlk.hlklib.lib.view.CorneredButton;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.StatusCode;
import com.netease.nimlib.sdk.auth.AuthService;
import com.netease.nimlib.sdk.auth.LoginInfo;

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
    public int getLayout() {
        return R.layout.fragment_sign_in;
    }

    @Override
    public void doingInResume() {
        if (checkStoragePermission()) {
            if (null != Cache.cache().me && isAdded()) {
                accountText.setValue(Cache.cache().me.getPhone());
                accountText.focusEnd();
                signInButton.setEnabled(false);
                signInButton.setText(R.string.ui_text_sign_in_still_processing);
                // 同步用户信息，如果同步失败则需要重新登录
                syncUserInfo();
                //checkNimStatus();
            }
        } else {
            // 尝试获取相关基本的运行时权限
            grantStoragePermission();
        }
    }

    @SuppressWarnings({"unchecked", "ConstantConditions"})
    private void doLogin() {
        LoginInfo info = App.app().loginInfo();
        NIMClient.getService(AuthService.class).login(info).setCallback(new RequestCallback<LoginInfo>() {
            @Override
            public void onSuccess(LoginInfo loginInfo) {
                ToastHelper.make().showMsg(R.string.ui_text_sing_nim_success);
                // 打开主页面
                finish(true);
            }

            @Override
            public void onFailed(int i) {
                ToastHelper.make().showMsg(StringHelper.getString(R.string.ui_text_sign_nim_failed, i));
                needToReLogin();
            }

            @Override
            public void onException(Throwable throwable) {
                ToastHelper.make().showMsg(StringHelper.getString(R.string.ui_text_sing_nim_exception, throwable.getMessage()));
                needToReLogin();
            }
        });
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
        }).find(Cache.cache().userId, true);
//        SystemRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<User>() {
//            @SuppressWarnings("ConstantConditions")
//            @Override
//            public void onResponse(User user, boolean success, String message) {
//                super.onResponse(user, success, message);
//                if (success) {
//                    cacheUser(user);
//                    // 同步成功之后检测网易云登录状态
//                    checkNimStatus();
//                } else {
//                    ToastHelper.make().showMsg(message);
//                    needToReLogin();
//                }
//            }
//        }).sync();
    }

    private void cacheUser(User user) {
        Cache.cache().setCurrentUser(user);
        Cache.cache().saveCurrentUser();
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
                    // 登录成功之后检测网易云账号登录状态
                    checkNimStatus();
                } else {
                    needToReLogin();
                }
            }
        }).signIn(account, password, "");
    }

    private void checkNimStatus() {
        StatusCode code = NIMClient.getStatus();
        if (code.shouldReLogin() || code.wontAutoLogin()) {
            // 如果网易云需要重新登录则重新登陆网易云
            doLogin();
        } else {
            // 如不需要重新登录网易云则进入主页面
            delayRefreshLoading(1000, DELAY_TYPE_TIME_DELAY);
        }
    }

//    /**
//     * 检测基本权限要求
//     */
//    private boolean checkBasePermission() {
//        basePermissions.clear();
//        permissionRequest = "";
//        // 存储设备
//        if (!hasPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) && needGrantPermission()) {
//            permissionRequest = StringHelper.getString(R.string.ui_grant_permission_storage);
//            basePermissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
//        }
//        // 定位，在使用时获取好了
////        if (!hasPermission(Manifest.permission.ACCESS_FINE_LOCATION) && needGrantPermission()) {
////            permissionRequest = permissionRequest + "、" + StringHelper.getString(R.string.ui_grant_permission_gps);
////            basePermissions.add(Manifest.permission.ACCESS_FINE_LOCATION);
////        }
//        // 电话设备，在使用时获取好了
//        if (!hasPermission(Manifest.permission.READ_PHONE_STATE) && needGrantPermission()) {
//            permissionRequest = permissionRequest + "、" + StringHelper.getString(R.string.ui_grant_permission_phone_state);
//            basePermissions.add(Manifest.permission.READ_PHONE_STATE);
//        }
//
//        return StringHelper.isEmpty(permissionRequest);
//    }
//
//    private void grandBasePermissions() {
//        if (!StringHelper.isEmpty(permissionRequest) && basePermissions.size() > 0) {
//            String text = StringHelper.getString(R.string.ui_base_text_permission_warning, permissionRequest);
//            // 提醒用户需要相关权限
//            SimpleDialogHelper.init(Activity()).show(text, new DialogHelper.OnDialogConfirmListener() {
//                @Override
//                public boolean onConfirm() {
//                    tryGrantPermissions(basePermissions.toArray(new String[basePermissions.size()]), GRANT_BASE, "");
//                    return true;
//                }
//            });
//        }
//    }

    private boolean checkStoragePermission() {
        // 存储设备
        return hasPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE);
    }

    private void grantStoragePermission() {
        signInButton.setEnabled(false);
        String text = StringHelper.getString(R.string.ui_grant_permission_storage_warning);
        String denied = StringHelper.getString(R.string.ui_grant_permission_storage_denied);
        tryGrantPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE, GRANT_STORAGE, text, denied);
    }

    /**
     * 子类需要重载此方法以便处理权限申请成功之后的事情
     */
    @Override
    public void permissionGranted(String[] permissions, int requestCode) {
        if (requestCode == GRANT_STORAGE && permissions[0].equals(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
            signInButton.setEnabled(true);
        }
    }

    /**
     * 子类需要重载此方法以便处理权限申请失败之后的事情
     */
    @Override
    public void permissionGrantFailed(int requestCode) {
        signInButton.setEnabled(true);
    }
}
