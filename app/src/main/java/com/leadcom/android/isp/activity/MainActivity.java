package com.leadcom.android.isp.activity;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.view.View;
import android.widget.TextView;

import com.hlk.hlklib.lib.inject.Click;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.view.CustomTextView;
import com.leadcom.android.isp.R;
import com.leadcom.android.isp.api.common.PushMsgRequest;
import com.leadcom.android.isp.api.listener.OnSingleRequestListener;
import com.leadcom.android.isp.application.App;
import com.leadcom.android.isp.cache.Cache;
import com.leadcom.android.isp.fragment.archive.ArchiveCreateSelectorFragment;
import com.leadcom.android.isp.fragment.archive.ArchiveEditorFragment;
import com.leadcom.android.isp.fragment.base.BaseFragment;
import com.leadcom.android.isp.fragment.individual.SettingFragment;
import com.leadcom.android.isp.fragment.individual.moment.MomentCreatorFragment;
import com.leadcom.android.isp.fragment.main.GroupFragment;
import com.leadcom.android.isp.fragment.main.HomeFragment;
import com.leadcom.android.isp.fragment.main.PersonalityFragment;
import com.leadcom.android.isp.fragment.main.SystemMessageFragment;
import com.leadcom.android.isp.helper.StringHelper;
import com.leadcom.android.isp.helper.ToastHelper;
import com.leadcom.android.isp.helper.UpgradeHelper;
import com.leadcom.android.isp.lib.permission.MPermission;
import com.leadcom.android.isp.lib.permission.annotation.OnMPermissionDenied;
import com.leadcom.android.isp.lib.permission.annotation.OnMPermissionGranted;
import com.leadcom.android.isp.lib.permission.annotation.OnMPermissionNeverAskAgain;
import com.leadcom.android.isp.listener.NotificationChangeHandleCallback;
import com.leadcom.android.isp.model.common.PushMessage;

/**
 * <b>功能描述：</b>主页窗体<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/04/06 16:44 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/04/06 16:44 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class MainActivity extends TitleActivity {

    /**
     * app内部打开
     */
    public static boolean innerOpen = true;

    public static void start(Context context) {
        start(context, 0);
    }

    public static void start(Context context, int selectedIndex) {
        start(context, new Intent().putExtra(PARAM_SELECTED, selectedIndex));
    }

    public static void start(Context context, Intent extras) {
        innerOpen = true;
        context.startActivity(getIntent(context, extras));
    }

    public static Intent getIntent(Context context, Intent extras) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        if (null != extras) {
            // 默认打开第一页
            intent.putExtra(PARAM_SELECTED, 0);
            intent.putExtras(extras);
        }
        return intent;
    }

    public static final String PARAM_SELECTED = "mf_param1";
    private static final String TAG_HOME = "main_home";
    //private static final String TAG_RECENT = "main_recent";
    private static final String TAG_MESSAGE = "main_message";
    private static final String TAG_GROUP = "main_group";
    private static final String TAG_MINE = "main_mine";
    private static final int SHOW_HOME = 0, SHOW_MSG = 1, SHOW_GROUP = 2, SHOW_MINE = 3;

    /**
     * 是否是新创建的MainFragment
     */
    public boolean isCreateNew = false;
    // 首页4个fragment
    private HomeFragment homeFragment;
    //private RecentContactsFragment recentFragment;
    private SystemMessageFragment messageFragment;
    private GroupFragment groupFragment;
    private PersonalityFragment mineFragment;
    private int showType = SHOW_GROUP;

    @Override
    protected void getParametersFromBundle(Bundle bundle) {
        showType = bundle.getInt(PARAM_SELECTED, SHOW_GROUP);
        super.getParametersFromBundle(bundle);
    }

    @Override
    public void saveParametersToBundle(Bundle bundle) {
        bundle.putInt(PARAM_SELECTED, showType);
        super.saveParametersToBundle(bundle);
    }

    @ViewId(R.id.ui_main_alpha_label_flag)
    private View alphaView;
    @ViewId(R.id.ui_tool_main_bottom_icon_1)
    private CustomTextView iconView1;
    @ViewId(R.id.ui_tool_main_bottom_icon_2)
    private CustomTextView iconView2;
    @ViewId(R.id.ui_tool_main_bottom_icon_unread)
    private View icon2Unread;
    @ViewId(R.id.ui_tool_main_bottom_icon_unread_num)
    private TextView icon2UnreadNum;
    @ViewId(R.id.ui_tool_main_bottom_clickable_center_icon)
    private View iconCenter;
    @ViewId(R.id.ui_tool_main_bottom_icon_3)
    private CustomTextView iconView3;
    @ViewId(R.id.ui_tool_main_bottom_icon_4)
    private CustomTextView iconView4;
    @ViewId(R.id.ui_tool_main_bottom_text_1)
    private TextView textView1;
    @ViewId(R.id.ui_tool_main_bottom_text_2)
    private TextView textView2;
    @ViewId(R.id.ui_tool_main_bottom_text_2d5)
    private TextView textView2d5;
    @ViewId(R.id.ui_tool_main_bottom_text_3)
    private TextView textView3;
    @ViewId(R.id.ui_tool_main_bottom_text_4)
    private TextView textView4;
    @ViewId(R.id.ui_tool_main_bottom_icon_4_unread)
    private View icon4Unread;

    @Override
    protected int getLayout() {
        return R.layout.activity_main;
    }

    private final String[] permissions = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE
    };

    private static final int REQ_BASE_PERMISSIONS = 100;

    private void requestBasePermissions() {
        MPermission.printMPermissionResult(true, this, permissions);
        MPermission.with(this)
                .setRequestCode(REQ_BASE_PERMISSIONS)
                .permissions(permissions)
                .request();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        MPermission.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }

    @OnMPermissionGranted(REQ_BASE_PERMISSIONS)
    public void onBasePermissionRequested() {
        MPermission.printMPermissionResult(false, this, permissions);
    }

    @OnMPermissionDenied(REQ_BASE_PERMISSIONS)
    @OnMPermissionNeverAskAgain(REQ_BASE_PERMISSIONS)
    public void onBasePermissionRequestFailed() {
        ToastHelper.make(this).showMsg(R.string.ui_text_permission_basic_denied);
        MPermission.printMPermissionResult(false, this, permissions);
    }

    private NotificationChangeHandleCallback callback = new NotificationChangeHandleCallback() {
        @Override
        public void onChanged() {
            fetchingUnreadCount();
        }
    };

    private void fetchingUnreadCount() {
        PushMsgRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<PushMessage>() {
            @Override
            public void onResponse(PushMessage pushMessage, boolean success, String message) {
                super.onResponse(pushMessage, success, message);
                if (success) {
                    int count = Integer.valueOf(pushMessage.getId());
                    App.app().setUnreadCount(count);
                    showUnreadFlag();
                }
            }
        }).unreadCount("");
    }

    /**
     * 显示有未读消息的标记
     */
    public void showUnreadFlag() {
//        checkUnreadTotalCountIgnoreMutex();
        showUnreadFlag(App.app().getUnreadCount());
    }

    private void showUnreadFlag(int num) {
        if (null != icon2Unread) {
            icon2Unread.setVisibility(num > 0 ? View.VISIBLE : View.GONE);
            icon2UnreadNum.setText(BaseFragment.formatUnread(num));
        }
//        if (null != icon4Unread) {
//            int size = NimMessage.getUnRead();
//            icon4Unread.setVisibility(size > 0 ? View.VISIBLE : View.GONE);
//        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        supportPressAgainToExit = true;
        //supportTransparentStatusBar = true;
        //isToolbarSupported = false;
        super.onCreate(savedInstanceState);
        App.addNotificationChangeCallback(callback);
        showToolbar(false);
        if (null != toolbarLine) {
            toolbarLine.setVisibility(View.GONE);
        }
        requestBasePermissions();
        if (savedInstanceState != null) {
            // 从堆栈恢复时，设置一个新的空白intent，不再重复解析之前的intent
            setIntent(new Intent());
        }

        setDisplayPage();
        App.dispatchCallbacks();
        UpgradeHelper.helper(this).checkVersion();
        if (!Cache.isReleasable()) {
            ToastHelper.make().showMsg(R.string.ui_text_main_inner_test_toast);
        }
        if (null != alphaView) {
            alphaView.setVisibility(Cache.isReleasable() ? View.GONE : View.VISIBLE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == BaseFragment.REQUEST_SELECT) {
            String result = BaseFragment.getResultedData(data);
            if (!StringHelper.isEmpty(result, true)) {
                if (result.equals(ArchiveEditorFragment.MOMENT)) {
                    MomentCreatorFragment.open(this, "[]");
                } else {
                    ArchiveEditorFragment.open(this, "", result);
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onAttachFragment(Fragment fragment) {
        if (null == homeFragment && fragment instanceof HomeFragment) {
            homeFragment = (HomeFragment) fragment;
        }
        if (null == messageFragment && fragment instanceof SystemMessageFragment) {
            messageFragment = (SystemMessageFragment) fragment;
        }
        if (null == groupFragment && fragment instanceof GroupFragment) {
            groupFragment = (GroupFragment) fragment;
        }
        if (null == mineFragment && fragment instanceof PersonalityFragment) {
            mineFragment = (PersonalityFragment) fragment;
        }
    }

    @Click({R.id.ui_tool_main_bottom_clickable_1, R.id.ui_tool_main_bottom_clickable_2,
            R.id.ui_tool_main_bottom_clickable_center,
            R.id.ui_tool_main_bottom_clickable_3, R.id.ui_tool_main_bottom_clickable_4})
    private void elementClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.ui_tool_main_bottom_clickable_1:
                if (showType != SHOW_GROUP) {
                    showType = SHOW_GROUP;
                    setDisplayPage();
                }
                break;
            case R.id.ui_tool_main_bottom_clickable_2:
                if (showType != SHOW_HOME) {
                    showType = SHOW_HOME;
                    setDisplayPage();
                }
                break;
            case R.id.ui_tool_main_bottom_clickable_center:
                iconCenter.startAnimation(App.clickAnimation());
                ArchiveCreateSelectorFragment.open(this, "");
                break;
            case R.id.ui_tool_main_bottom_clickable_3:
                if (showType != SHOW_MSG) {
                    showType = SHOW_MSG;
                    setDisplayPage();
                }
                break;
            case R.id.ui_tool_main_bottom_clickable_4:
                if (showType != SHOW_MINE) {
                    showType = SHOW_MINE;
                    setDisplayPage();
                }
                break;
            case R.id.ui_ui_custom_title_right_icon_1:
                // 打开个人设置
                SettingFragment.open(this);
                break;
            case R.id.ui_ui_custom_title_right_icon_2_container:
                // 打开消息页面
                SystemMessageFragment.open(this);
                break;
            case R.id.ui_ui_custom_title_left_container:
                // 搜索
                break;
        }
    }

    public void recreateFragment() {
        if (!isCreateNew) {
            return;
        }
        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        Fragment fragment = manager.findFragmentByTag(TAG_HOME);
        if (null != fragment) {
            transaction.remove(fragment);
            homeFragment = null;
        }

        fragment = manager.findFragmentByTag(TAG_MESSAGE);
        if (null != fragment) {
            transaction.remove(fragment);
            messageFragment = null;
        }

        fragment = manager.findFragmentByTag(TAG_GROUP);
        if (null != fragment) {
            transaction.remove(fragment);
            groupFragment = null;
        }

        fragment = manager.findFragmentByTag(TAG_MINE);
        if (null != fragment) {
            transaction.remove(fragment);
            mineFragment = null;
        }
        transaction.commitAllowingStateLoss();
        isCreateNew = false;
    }

    private Fragment findFragment(String tag) {
        FragmentManager manager = getSupportFragmentManager();
        return manager.findFragmentByTag(tag);
    }

    private void initializeHome() {
        Fragment fragment = findFragment(TAG_HOME);
        if (null != fragment) {
            log(format("find exist fragment by tag %s", TAG_HOME));
            if (isCreateNew) {
                // 如果是新建的，则清除现有的fragment
                recreateFragment();
                homeFragment = new HomeFragment();
                log(format("create new fragment to %s by tag %s and isCreateNew = true", homeFragment.toString(), TAG_MESSAGE));
            } else {
                if (null == homeFragment) {
                    log(format("reset fragment %s to %s by tag %s", "null", fragment.toString(), TAG_HOME));
                    homeFragment = (HomeFragment) fragment;
                }
            }
        } else {
            homeFragment = new HomeFragment();
            log(format("create new fragment to %s by tag %s", homeFragment.toString(), TAG_MESSAGE));
        }
    }

    private void initializeMessage() {
        Fragment fragment = findFragment(TAG_MESSAGE);
        if (null != fragment) {
            log(format("find exist fragment by tag %s", TAG_MESSAGE));
            if (isCreateNew) {
                recreateFragment();
                messageFragment = SystemMessageFragment.getInstance(true);
                log(format("create new fragment to %s by tag %s and isCreateNew = true", messageFragment.toString(), TAG_MESSAGE));
            } else {
                if (null == messageFragment) {
                    log(format("reset fragment %s to %s by tag %s", "null", fragment.toString(), TAG_MESSAGE));
                    messageFragment = (SystemMessageFragment) fragment;
                }
            }
        } else {
            messageFragment = SystemMessageFragment.getInstance(true);
            log(format("create new fragment to %s by tag %s", messageFragment.toString(), TAG_MESSAGE));
        }
    }

//    private void initializeRecent() {
//        Fragment fragment = findFragment(TAG_RECENT);
//        if (null != fragment) {
//            if (null == recentFragment) {
//                recentFragment = (RecentContactsFragment) fragment;
//                recentFragment.mainFragment = this;
//            }
//        } else {
//            recentFragment = new RecentContactsFragment();
//            recentFragment.mainFragment = this;
//        }
//    }

    private void initializeGroup() {
        Fragment fragment = findFragment(TAG_GROUP);
        if (null != fragment) {
            log(format("find exist fragment by tag %s", TAG_GROUP));
            if (isCreateNew) {
                recreateFragment();
                groupFragment = new GroupFragment();
                log(format("create new fragment to %s by tag %s and isCreateNew = true", groupFragment.toString(), TAG_GROUP));
            } else {
                if (null == groupFragment) {
                    log(format("reset fragment %s to %s by tag %s", "null", fragment.toString(), TAG_GROUP));
                    groupFragment = (GroupFragment) fragment;
                }
            }
        } else {
            groupFragment = new GroupFragment();
            log(format("create new fragment to %s by tag %s", groupFragment.toString(), TAG_GROUP));
        }
    }

    private void initializeMine() {
        Fragment fragment = findFragment(TAG_MINE);
        if (null != fragment) {
            log(format("find exist fragment by tag %s", TAG_MINE));
            if (isCreateNew) {
                recreateFragment();
                mineFragment = new PersonalityFragment();
                log(format("create new fragment to %s by tag %s and isCreateNew = true", mineFragment.toString(), TAG_MINE));
            } else {
                if (null == mineFragment) {
                    log(format("reset fragment %s to %s by tag %s", "null", fragment.toString(), TAG_MINE));
                    mineFragment = (PersonalityFragment) fragment;
                }
            }
        } else {
            mineFragment = new PersonalityFragment();
            log(format("create new fragment to %s by tag %s", mineFragment.toString(), TAG_MINE));
        }
    }

    private void hideFragments() {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        if (null != homeFragment && showType != SHOW_HOME) {
            log("hide fragment home");
            transaction.hide(homeFragment);
        }
        if (null != messageFragment && showType != SHOW_MSG) {
            log("hide fragment message");
            transaction.hide(messageFragment);
        }
//        if (null != recentFragment && showType != SHOW_RECENT) {
//            log("hide fragment recent");
//            transaction.hide(recentFragment);
//        }
        if (null != groupFragment && showType != SHOW_GROUP) {
            log("hide fragment group");
            transaction.hide(groupFragment);
        }
        if (null != mineFragment && showType != SHOW_MINE) {
            log("hide fragment mine");
            transaction.hide(mineFragment);
        }
        transaction.commitAllowingStateLoss();
    }

    private void showFragment(BaseFragment fragment, String tag) {
        FragmentManager manager = getSupportFragmentManager();
        Fragment f = manager.findFragmentByTag(tag);
        FragmentTransaction transaction = manager.beginTransaction();
        if (!fragment.isAdded() && null == f) {
            log("fragment " + tag + " is now add to fragment manager.");
            transaction.add(R.id.ui_fragment_main_frame_layout, fragment, tag);
        } else {
            if (null != f && fragment != f) {
                log(format("reset(show) fragment %s to %s by tag %s", fragment.toString(), f.toString(), tag));
                fragment = (BaseFragment) f;
            }
            log(format("now show fragment %s by tag %s", fragment.toString(), tag));
        }
        transaction.show(fragment);
        transaction.commitAllowingStateLoss();
        //setTranslucentStatus(tag.equals(TAG_MINE));
        if (null != alphaView) {
            if (!Cache.isReleasable()) {
                alphaView.setVisibility(tag.equals(TAG_MINE) ? View.GONE : View.VISIBLE);
            }
        }
    }

    private void setDisplayPage() {
        hideFragments();
        switch (showType) {
            case SHOW_HOME:
                initializeHome();
                showFragment(homeFragment, TAG_HOME);
                break;
            case SHOW_MSG:
                initializeMessage();
                showFragment(messageFragment, TAG_MESSAGE);
//            case SHOW_RECENT:
//                initializeRecent();
//                showFragment(recentFragment, TAG_RECENT);
                break;
            case SHOW_GROUP:
                initializeGroup();
                showFragment(groupFragment, TAG_GROUP);
                break;
            case SHOW_MINE:
                initializeMine();
                showFragment(mineFragment, TAG_MINE);
                break;
        }
        bottomSelectionChanged();
    }

    protected void bottomSelectionChanged() {
        if (null == iconView1) {
            log("views not define.");
            return;
        }
        int color1 = ContextCompat.getColor(this, R.color.textColorHintDark);
        int color2 = ContextCompat.getColor(this, R.color.colorPrimary);

        iconView1.setText(showType == SHOW_HOME ? R.string.ui_icon_home : R.string.ui_icon_home_unselected);
        iconView1.setTextColor(showType == SHOW_HOME ? color2 : color1);
        textView1.setTextColor(showType == SHOW_HOME ? color2 : color1);

        iconView2.setText(showType == SHOW_MSG ? R.string.ui_icon_chat_left_solid : R.string.ui_icon_chat_left_hollow);
        iconView2.setTextColor(showType == SHOW_MSG ? color2 : color1);
        textView2.setTextColor(showType == SHOW_MSG ? color2 : color1);

        textView2d5.setTextColor(color1);

        iconView3.setText(showType == SHOW_GROUP ? R.string.ui_icon_pentagon_solid : R.string.ui_icon_pentagon_hollow);
        iconView3.setTextColor(showType == SHOW_GROUP ? color2 : color1);
        textView3.setTextColor(showType == SHOW_GROUP ? color2 : color1);

        iconView4.setText(showType == SHOW_MINE ? R.string.ui_icon_main_individual_solid : R.string.ui_icon_main_individual_holow);
        iconView4.setTextColor(showType == SHOW_MINE ? color2 : color1);
        textView4.setTextColor(showType == SHOW_MINE ? color2 : color1);
    }

    @Override
    protected void onStart() {
        super.onStart();
        App.app().setAppStayInBackground(false);
    }

    @Override
    protected void onStop() {
        App.app().setAppStayInBackground(true);
        super.onStop();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.clear();
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onDestroy() {
        innerOpen = false;
        App.removeNotificationChangeCallback(callback);
        super.onDestroy();
    }
}
