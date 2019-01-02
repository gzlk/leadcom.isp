package com.leadcom.android.isp.fragment.individual;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.hlk.hlklib.lib.inject.Click;
import com.hlk.hlklib.lib.inject.ViewId;
import com.leadcom.android.isp.BuildConfig;
import com.leadcom.android.isp.R;
import com.leadcom.android.isp.activity.BaseActivity;
import com.leadcom.android.isp.application.App;
import com.leadcom.android.isp.cache.Cache;
import com.leadcom.android.isp.fragment.base.BaseFragment;
import com.leadcom.android.isp.fragment.base.BaseTransparentSupportFragment;
import com.leadcom.android.isp.helper.LogcatHelper;
import com.leadcom.android.isp.helper.StringHelper;
import com.leadcom.android.isp.helper.UpgradeHelper;
import com.leadcom.android.isp.helper.popup.DeleteDialogHelper;
import com.leadcom.android.isp.helper.popup.DialogHelper;
import com.leadcom.android.isp.holder.common.SimpleClickableViewHolder;
import com.leadcom.android.isp.holder.common.ToggleableViewHolder;
import com.leadcom.android.isp.listener.OnViewHolderClickListener;

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

    public static void open(BaseFragment fragment) {
        fragment.openActivity(SettingFragment.class.getName(), "", true, false);
    }

    public static void open(Context context) {
        BaseActivity.openActivity(context, SettingFragment.class.getName(), "", true, false);
    }

    // UI
    @ViewId(R.id.ui_setting_to_password)
    private View passwordView;
    @ViewId(R.id.ui_setting_to_messaging)
    private View messagingView;
    @ViewId(R.id.ui_setting_to_cache)
    private View cacheView;
    @ViewId(R.id.ui_about_to_upgrade)
    private View upgradeView;
    @ViewId(R.id.ui_setting_to_about)
    private View aboutView;
    @ViewId(R.id.ui_setting_to_log)
    private View saveLogView;
    @ViewId(R.id.ui_setting_api_debug)
    private View apiDebugView;

    // holders
    private SimpleClickableViewHolder passwordHolder;
    private SimpleClickableViewHolder messagingHolder;
    private SimpleClickableViewHolder cacheHolder;
    private SimpleClickableViewHolder upgradeHolder;
    private SimpleClickableViewHolder aboutHolder;
    private ToggleableViewHolder logHolder, apiHolder;
    private String[] strings;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        saveLogView.setVisibility(Cache.isReleasable() ? View.GONE : View.VISIBLE);
        apiDebugView.setVisibility(!Cache.isReleasable() ? View.VISIBLE : View.GONE);
    }

    @Override
    public int getLayout() {
        return R.layout.fragment_individual_setting;
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
        if (null == cacheHolder) {
            cacheHolder = new SimpleClickableViewHolder(cacheView, this);
            cacheHolder.addOnViewHolderClickListener(holderClickListener);
            cacheHolder.showContent(strings[2]);
        }
        if (null == upgradeHolder) {
            upgradeHolder = new SimpleClickableViewHolder(upgradeView, this);
            upgradeHolder.addOnViewHolderClickListener(holderClickListener);
            upgradeHolder.showContent(format(strings[3], BuildConfig.VERSION_NAME));
        }
        if (null == aboutHolder) {
            aboutHolder = new SimpleClickableViewHolder(aboutView, SettingFragment.this);
            aboutHolder.addOnViewHolderClickListener(holderClickListener);
            aboutHolder.showContent(strings[4]);
        }
        if (null == logHolder) {
            logHolder = new ToggleableViewHolder(saveLogView, this);
            logHolder.addOnViewHolderToggleChangedListener(toggleChangedListener);
            logHolder.showContent(format(strings[5], LogcatHelper.helper().isStarting() ? 1 : 0));
        }
        if (null == apiHolder) {
            apiHolder = new ToggleableViewHolder(apiDebugView, this);
            apiHolder.addOnViewHolderToggleChangedListener(apiToggle);
            apiHolder.showContent(format(strings[6], (App.app().isNormalApi() ? "正常" : "我的电脑"), (App.app().isNormalApi() ? 1 : 0)));
        }
    }

    @SuppressWarnings("unused")
    @Click({R.id.ui_setting_sign_out})
    private void elementClick(View view) {
        App.app().logout();
        finishToSignIn();
    }

    private ToggleableViewHolder.OnViewHolderToggleChangedListener apiToggle = new ToggleableViewHolder.OnViewHolderToggleChangedListener() {
        @Override
        public void onChange(int index, boolean togged) {
            App.app().setApiDebug(togged);
            apiHolder.showContent(format(strings[6], (App.app().isNormalApi() ? "正常" : "我的电脑"), (App.app().isNormalApi() ? 1 : 0)));
        }
    };

    private ToggleableViewHolder.OnViewHolderToggleChangedListener toggleChangedListener = new ToggleableViewHolder.OnViewHolderToggleChangedListener() {

        @Override
        public void onChange(int index, boolean togged) {
            if (togged) {
                // 清理log缓存目录
                //FileUtils.removeFile(App.app().getCachePath(App.LOGCAT_DIR));
                // 保存log
                warningLogSaving();
            } else {
                LogcatHelper.helper().stop();
            }
        }
    };

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
                    // 缓存管理
                    SettingCacheFragment.open(SettingFragment.this);
                    break;
                case 3:
                    // 检查更新
                    UpgradeHelper.helper(Activity()).checkVersion(true);
                    break;
                case 4:
                    // 关于
                    openActivity(AboutFragment.class.getName(), "", true, false);
                    break;
            }
        }
    };

    private void warningLogSaving() {
        DeleteDialogHelper.helper().init(this).setOnDialogConfirmListener(new DialogHelper.OnDialogConfirmListener() {
            @Override
            public boolean onConfirm() {
                LogcatHelper.helper().start();
                return true;
            }
        }).setTitleText(R.string.ui_text_setting_log_save_dialog_title).setConfirmText(R.string.ui_base_text_confirm).show();
    }
}
