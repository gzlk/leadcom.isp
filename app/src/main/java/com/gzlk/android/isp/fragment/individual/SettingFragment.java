package com.gzlk.android.isp.fragment.individual;

import android.os.Bundle;
import android.view.View;

import com.gzlk.android.isp.R;
import com.gzlk.android.isp.application.App;
import com.gzlk.android.isp.fragment.base.BaseTransparentSupportFragment;
import com.gzlk.android.isp.helper.StringHelper;
import com.gzlk.android.isp.holder.common.SimpleClickableViewHolder;
import com.gzlk.android.isp.listener.OnViewHolderClickListener;
import com.hlk.hlklib.lib.inject.Click;
import com.hlk.hlklib.lib.inject.ViewId;

/**
 * <b>功能描述：</b>个人设置页面<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/04/18 00:21 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/04/18 00:21 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class SettingFragment extends BaseTransparentSupportFragment {

    // UI
    @ViewId(R.id.ui_setting_to_password)
    private View passwordView;
    @ViewId(R.id.ui_setting_to_messaging)
    private View messagingView;
    @ViewId(R.id.ui_setting_to_privacy)
    private View privacyView;

    // holders
    private SimpleClickableViewHolder passwordHolder;
    private SimpleClickableViewHolder messagingHolder;
    private SimpleClickableViewHolder privacyHolder;
    private String[] strings;

    @Override
    public int getLayout() {
        return R.layout.fragment_setting;
    }

    @Override
    protected void getParamsFromBundle(Bundle bundle) {

    }

    @Override
    public void doingInResume() {
        initializeHolders();
        setLeftText(0);
        setCustomTitle(R.string.ui_text_setting_fragment_title);
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

    @SuppressWarnings("ConstantConditions")
    private void initializeHolders() {
        if (null == strings) {
            strings = StringHelper.getStringArray(R.array.ui_individual_settings);
        }
        if (null == passwordHolder) {
            passwordHolder = new SimpleClickableViewHolder(passwordView, SettingFragment.this);
            passwordHolder.addOnViewHolderClickListener(holderClickListener);
            passwordHolder.showContent(strings[0]);
        }
        if (null == messagingHolder) {
            messagingHolder = new SimpleClickableViewHolder(messagingView, SettingFragment.this);
            messagingHolder.addOnViewHolderClickListener(holderClickListener);
            messagingHolder.showContent(strings[1]);
        }
        if (null == privacyHolder) {
            privacyHolder = new SimpleClickableViewHolder(privacyView, SettingFragment.this);
            privacyHolder.addOnViewHolderClickListener(holderClickListener);
            privacyHolder.showContent(strings[2]);
        }
    }

    @SuppressWarnings("ConstantConditions")
    @Click({R.id.ui_setting_sign_out})
    private void elementClick(View view) {
        App.app().logout();
        finishToSignIn();
    }

    private OnViewHolderClickListener holderClickListener = new OnViewHolderClickListener() {
        @Override
        public void onClick(int index) {
            switch (index) {
                case 0:
                    // 重置密码
                    openActivity(SettingPasswordFragment.class.getName(), "", true, false);
                    break;
                case 1:
                    // 消息设置
                    openActivity(SettingMessagingFragment.class.getName(), "", true, false);
                    break;
                case 2:
                    // 隐私设置
                    openActivity(AboutFragment.class.getName(), "", true, false);
                    break;
            }
        }
    };
}
