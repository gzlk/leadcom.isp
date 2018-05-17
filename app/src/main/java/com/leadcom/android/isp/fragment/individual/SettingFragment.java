package com.leadcom.android.isp.fragment.individual;

import android.content.Context;
import android.os.Bundle;
import android.view.View;

import com.hlk.hlklib.lib.inject.Click;
import com.hlk.hlklib.lib.inject.ViewId;
import com.leadcom.android.isp.BuildConfig;
import com.leadcom.android.isp.R;
import com.leadcom.android.isp.activity.BaseActivity;
import com.leadcom.android.isp.api.common.UpdateRequest;
import com.leadcom.android.isp.api.listener.OnSingleRequestListener;
import com.leadcom.android.isp.application.App;
import com.leadcom.android.isp.cache.Cache;
import com.leadcom.android.isp.etc.Utils;
import com.leadcom.android.isp.fragment.base.BaseFragment;
import com.leadcom.android.isp.fragment.base.BaseTransparentSupportFragment;
import com.leadcom.android.isp.helper.StringHelper;
import com.leadcom.android.isp.helper.ToastHelper;
import com.leadcom.android.isp.helper.UpgradeHelper;
import com.leadcom.android.isp.helper.popup.DeleteDialogHelper;
import com.leadcom.android.isp.helper.popup.DialogHelper;
import com.leadcom.android.isp.holder.common.SimpleClickableViewHolder;
import com.leadcom.android.isp.listener.OnViewHolderClickListener;
import com.leadcom.android.isp.model.common.SystemUpdate;

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

    // holders
    private SimpleClickableViewHolder passwordHolder;
    private SimpleClickableViewHolder messagingHolder;
    private SimpleClickableViewHolder cacheHolder;
    private SimpleClickableViewHolder upgradeHolder;
    private SimpleClickableViewHolder aboutHolder;
    private SimpleClickableViewHolder logHolder;
    private String[] strings;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        saveLogView.setVisibility(View.GONE);
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
            logHolder = new SimpleClickableViewHolder(saveLogView, this);
            logHolder.addOnViewHolderClickListener(holderClickListener);
            logHolder.showContent(strings[5]);
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
                    // 缓存管理
                    SettingCacheFragment.open(SettingFragment.this);
                    break;
                case 3:
                    // 检查更新
                    checkClientVersion();
                    break;
                case 4:
                    // 关于
                    openActivity(AboutFragment.class.getName(), "", true, false);
                    break;
                case 5:
                    // 保存log
                    //warningLogSaving();
                    break;
            }
        }
    };

    private void warningLogSaving() {
        // 清理log缓存目录
        //FileUtils.removeFile(App.app().getCachePath("nim") + "log/");
        DeleteDialogHelper.helper().init(this).setOnDialogConfirmListener(new DialogHelper.OnDialogConfirmListener() {
            @Override
            public boolean onConfirm() {
                //CrashSaver.save(App.app(), new IllegalArgumentException("Manual saving logs."), false);
                return true;
            }
        }).setTitleText(R.string.ui_text_setting_log_save_dialog_title).setConfirmText(R.string.ui_base_text_save).show();
    }

    /**
     * 检测服务器上的最新客户端版本并提示用户更新
     */
    private void checkClientVersion() {
        UpdateRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<SystemUpdate>() {
            @Override
            public void onResponse(SystemUpdate systemUpdate, boolean success, String message) {
                super.onResponse(systemUpdate, success, message);
                if (success) {
                    String ver = systemUpdate.getVersion();
                    //warningUpdatable("http://file.ws.126.net/3g/client/netease_newsreader_android.apk","2.0.1");
                    if (!StringHelper.isEmpty(ver) && ver.compareTo(BuildConfig.VERSION_NAME) > 0) {
                        String url = systemUpdate.getResourceURI();
                        if (StringHelper.isEmpty(url) || !Utils.isUrl(url)) {
                            ToastHelper.make().showMsg(R.string.ui_system_updatable_url_invalid);
                        } else {
                            warningUpdatable(url, ver);
                        }
                    } else {
                        ToastHelper.make().showMsg(R.string.ui_text_setting_fragment_no_update);
                    }
                }
            }
        }).getClientVersion();
    }

    private void warningUpdatable(final String url, final String version) {
        String text = StringHelper.getString(R.string.ui_system_updatable, StringHelper.getString(R.string.app_name_default), version);
        DeleteDialogHelper.helper().init(this).setOnDialogConfirmListener(new DialogHelper.OnDialogConfirmListener() {
            @Override
            public boolean onConfirm() {
                // 打开下载对话框，并开始下载（下载对话框可以隐藏）
                //showUpgradeDownloadingDialog();
                String app = getString(R.string.app_name_default);
                String title = getString(R.string.ui_system_updating_title, app);
                String description = getString(R.string.ui_system_updating_description);
                UpgradeHelper.helper(Activity(), version).startDownload(url, title, description);
                return true;
            }
        }).setTitleText(text).setConfirmText(R.string.ui_base_text_yes).show();
    }

}
