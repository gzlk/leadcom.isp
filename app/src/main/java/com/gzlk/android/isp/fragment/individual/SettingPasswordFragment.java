package com.gzlk.android.isp.fragment.individual;

import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.gzlk.android.isp.R;
import com.gzlk.android.isp.api.listener.OnRequestListener;
import com.gzlk.android.isp.api.user.UserRequest;
import com.gzlk.android.isp.cache.Cache;
import com.gzlk.android.isp.fragment.base.BaseTransparentSupportFragment;
import com.gzlk.android.isp.helper.StringHelper;
import com.gzlk.android.isp.helper.ToastHelper;
import com.gzlk.android.isp.listener.OnTitleButtonClickListener;
import com.gzlk.android.isp.model.user.User;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.view.ClearEditText;

/**
 * <b>功能描述：</b>个人 - 修改密码<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/04/18 08:38 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/04/18 08:38 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class SettingPasswordFragment extends BaseTransparentSupportFragment {

    @ViewId(R.id.ui_setting_password_old_container)
    private LinearLayout oldContainer;
    @ViewId(R.id.ui_setting_password_new_container)
    private LinearLayout newContainer;
    @ViewId(R.id.ui_setting_password_old)
    private ClearEditText oldPassword;
    @ViewId(R.id.ui_setting_password_new)
    private ClearEditText newPassword;
    @ViewId(R.id.ui_setting_password_confirm)
    private ClearEditText cfmPassword;

    @Override
    public int getLayout() {
        return R.layout.fragment_setting_password;
    }

    @Override
    protected void getParamsFromBundle(Bundle bundle) {

    }

    @Override
    public void doingInResume() {
        setCustomTitle(R.string.ui_text_edit_password_fragment_title);
        setRightText(R.string.ui_base_text_save);
        setRightTitleClickListener(new OnTitleButtonClickListener() {
            @Override
            public void onClick() {
                tryEditPassword();
            }
        });
        oldPassword.addOnFocusChangeListener(focusChangeListener);
        newPassword.addOnFocusChangeListener(focusChangeListener);
        cfmPassword.addOnFocusChangeListener(focusChangeListener);
    }

    @Override
    protected boolean shouldSetDefaultTitleEvents() {
        return true;
    }

    @Override
    protected void saveParamsToBundle(Bundle bundle) {

    }

    @Override
    protected void destroyView() {

    }

    private View.OnFocusChangeListener focusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (v == oldPassword) {
                highlight(oldContainer, hasFocus);
                highlight(newContainer, !hasFocus);
            } else {
                highlight(oldContainer, !hasFocus);
                highlight(newContainer, hasFocus);
            }
        }
    };

    private void highlight(View view, boolean light) {
        if (Build.VERSION.SDK_INT >= 21) {
            view.animate().translationZ(light ? getDimension(R.dimen.ui_base_translationZ) : getDimension(R.dimen.ui_base_translationZ_small)).setDuration(duration()).start();
        }
    }

    private void tryEditPassword() {
        String old = oldPassword.getValue();
        if (StringHelper.isEmpty(old)) {
            ToastHelper.make().showMsg(R.string.ui_text_edit_password_old_incorrect);
            return;
        }
        String newOne = newPassword.getValue();
        if (StringHelper.isEmpty(newOne)) {
            ToastHelper.make().showMsg(R.string.ui_text_edit_password_new_incorrect);
            return;
        }
        String newConfirm = cfmPassword.getValue();
        if (StringHelper.isEmpty(newConfirm) || !newConfirm.equals(newOne)) {
            ToastHelper.make().showMsg(R.string.ui_text_edit_password_new_not_equal);
            return;
        }
        editPassword(old, newOne);
    }

    private void editPassword(String old, String newOne) {
        UserRequest.request().setOnRequestListener(new OnRequestListener<User>() {
            @Override
            public void onResponse(User user, boolean success, String message) {
                super.onResponse(user, success, message);
                if (success) {
                    if (null != user && !StringHelper.isEmpty(user.getId())) {
                        Cache.cache().setCurrentUser(user);
                    }
                    ToastHelper.make().showMsg(R.string.ui_text_edit_password_success);
                    finish();
                }
            }
        }).update(UserRequest.TYPE_PASSWORD, newOne);
    }
}
