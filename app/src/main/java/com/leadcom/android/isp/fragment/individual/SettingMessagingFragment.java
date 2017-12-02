package com.leadcom.android.isp.fragment.individual;

import android.os.Bundle;
import android.view.View;

import com.leadcom.android.isp.R;
import com.leadcom.android.isp.application.App;
import com.leadcom.android.isp.fragment.base.BaseTransparentSupportFragment;
import com.leadcom.android.isp.helper.StringHelper;
import com.leadcom.android.isp.holder.common.SimpleClickableViewHolder;
import com.leadcom.android.isp.holder.common.ToggleableViewHolder;
import com.hlk.hlklib.lib.inject.ViewId;

/**
 * <b>功能描述：</b>消息管理设置<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/04/18 09:31 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/04/18 09:31 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class SettingMessagingFragment extends BaseTransparentSupportFragment {

    // UI
    @ViewId(R.id.ui_setting_messaging_sound)
    private View soundView;
    @ViewId(R.id.ui_setting_messaging_vibration)
    private View vibrationView;

    // Holder
    private SimpleClickableViewHolder titleHolder;
    private ToggleableViewHolder soundHolder;
    private ToggleableViewHolder vibrationHolder;

    String[] strings;

    @Override
    public int getLayout() {
        return R.layout.fragment_individual_setting_messaging;
    }

    @Override
    protected void getParamsFromBundle(Bundle bundle) {

    }

    @Override
    public void doingInResume() {
        setCustomTitle(R.string.ui_text_setting_messaging_fragment_title);
        initializeHolders();
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

    private void initializeHolders() {
        if (null == strings) {
            strings = StringHelper.getStringArray(R.array.ui_individual_setting_messaging);
        }
        if (null == titleHolder) {
            titleHolder = new SimpleClickableViewHolder(mRootView, SettingMessagingFragment.this);
            titleHolder.showContent(format(strings[0], StringHelper.getString(R.string.ui_base_text_messaging_closed)));
        }
        if (null == soundHolder) {
            soundHolder = new ToggleableViewHolder(soundView, SettingMessagingFragment.this);
            soundHolder.addOnViewHolderToggleChangedListener(toggleChangedListener);
            soundHolder.showContent(format(strings[1], App.nimSound ? 1 : 0));
        }
        if (null == vibrationHolder) {
            vibrationHolder = new ToggleableViewHolder(vibrationView, SettingMessagingFragment.this);
            vibrationHolder.addOnViewHolderToggleChangedListener(toggleChangedListener);
            vibrationHolder.showContent(format(strings[2], App.nimVibrate ? 1 : 0));
        }
        resetTitle();
    }

    private void resetTitle() {
        boolean sound = soundHolder.isToggled();
        boolean vibration = vibrationHolder.isToggled();
        titleHolder.showContent(format(strings[0], (sound || vibration ? StringHelper.getString(R.string.ui_base_text_messaging_opened) : getString(R.string.ui_base_text_messaging_closed))));
        App.resetNimMessageNotify(sound, vibration);
    }

    private ToggleableViewHolder.OnViewHolderToggleChangedListener toggleChangedListener = new ToggleableViewHolder.OnViewHolderToggleChangedListener() {
        @Override
        public void onChange(int index, boolean togged) {
            resetTitle();
        }
    };
}
