package com.leadcom.android.isp.fragment.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.TextView;

import com.hlk.hlklib.lib.inject.Click;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.view.CustomTextView;
import com.leadcom.android.isp.R;
import com.leadcom.android.isp.application.App;
import com.leadcom.android.isp.application.NimApplication;
import com.leadcom.android.isp.fragment.archive.ArchiveCreateSelectorFragment;
import com.leadcom.android.isp.fragment.archive.ArchiveEditorFragment;
import com.leadcom.android.isp.fragment.base.BaseFragment;
import com.leadcom.android.isp.fragment.base.BaseTransparentSupportFragment;
import com.leadcom.android.isp.fragment.individual.SettingFragment;
import com.leadcom.android.isp.fragment.individual.moment.MomentCreatorFragment;
import com.leadcom.android.isp.listener.NotificationChangeHandleCallback;
import com.leadcom.android.isp.nim.model.notification.NimMessage;
import com.netease.nim.uikit.impl.cache.TeamDataCache;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.RequestCallback;
import com.netease.nimlib.sdk.friend.FriendService;
import com.netease.nimlib.sdk.msg.MsgService;
import com.netease.nimlib.sdk.msg.MsgServiceObserve;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.RecentContact;
import com.netease.nimlib.sdk.team.constant.TeamMessageNotifyTypeEnum;
import com.netease.nimlib.sdk.team.model.Team;

import java.util.List;

/**
 * <b>功能描述：</b>首页<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/04/06 16:43 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/04/06 16:43 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class MainFragment extends BaseTransparentSupportFragment {
    /**
     * 新建MainFragment的时候传入的参数，以此当作初始化显示的页面（也即ViewPager当前显示的页面的index），int型
     */
    public static final String PARAM_SELECTED = "mf_param1";
    private static final String TAG_HOME = "main_home";
    private static final String TAG_RECENT = "main_recent";
    private static final String TAG_GROUP = "main_group";
    private static final String TAG_MINE = "main_mine";

    private static final int SHOW_HOME = 0, SHOW_RECENT = 1, SHOW_GROUP = 2, SHOW_MINE = 3;

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

    // 首页4个fragment
    private HomeFragment homeFragment;
    private RecentContactsFragment recentFragment;
    private GroupFragment groupFragment;
    private PersonalityFragment mineFragment;
    private int showType = SHOW_HOME;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NimApplication.addNotificationChangeCallback(callback);
        registerObservers(true);
        setDisplayPage();
    }

    private void registerObservers(boolean register) {
        NIMClient.getService(MsgServiceObserve.class).observeRecentContact(recentContactChangeObserver, register);
    }

    Observer<List<RecentContact>> recentContactChangeObserver = new Observer<List<RecentContact>>() {
        @Override
        public void onEvent(List<RecentContact> recentContacts) {
            log("message observer onEvent(Main): " + (null == recentContacts ? "null" : recentContacts.size()));
            showUnreadFlag();
        }
    };

    private void checkUnreadTotalCountIgnoreMutex() {
        NIMClient.getService(MsgService.class).queryRecentContacts().setCallback(new RequestCallback<List<RecentContact>>() {
            @Override
            public void onSuccess(List<RecentContact> list) {
                int count = 0;
                if (list == null || list.size() < 1) {
                    return;
                }
                for (RecentContact contact : list) {
                    int unread = contact.getUnreadCount();
                    if (unread > 0) {
                        if (contact.getSessionType() == SessionTypeEnum.Team) {
                            // 查看群聊是否静音，静音的话不统计
                            Team team = TeamDataCache.getInstance().getTeamById(contact.getContactId());
                            if (team.getMessageNotifyType() != TeamMessageNotifyTypeEnum.Mute) {
                                count += unread;
                            }
                        } else if (contact.getSessionType() == SessionTypeEnum.P2P) {
                            boolean notify = NIMClient.getService(FriendService.class).isNeedMessageNotify(contact.getContactId());
                            if (notify) {
                                count += unread;
                            }
                        } else {
                            count += unread;
                        }
                    }
                }
                showUnreadFlag(count);
            }

            @Override
            public void onFailed(int code) {

            }

            @Override
            public void onException(Throwable exception) {

            }
        });
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        NimApplication.dispatchCallbacks();
    }

    @Override
    public void onDestroy() {
        registerObservers(false);
        NimApplication.removeNotificationChangeCallback(callback);
        super.onDestroy();
    }

    private NotificationChangeHandleCallback callback = new NotificationChangeHandleCallback() {
        @Override
        public void onChanged() {
            showUnreadFlag();
        }
    };

    @Override
    public void onActivityResult(int requestCode, Intent data) {
        if (requestCode == REQUEST_SELECT) {
            String result = getResultedData(data);
            if (result.equals(ArchiveEditorFragment.MOMENT)) {
                MomentCreatorFragment.open(this, "[]");
            } else {
                ArchiveEditorFragment.open(MainFragment.this, "", result);
            }
        }
        super.onActivityResult(requestCode, data);
    }

    @Override
    protected void getParamsFromBundle(Bundle bundle) {
        super.getParamsFromBundle(bundle);
        showType = bundle.getInt(PARAM_SELECTED, SHOW_HOME);
    }

    @Override
    protected void saveParamsToBundle(Bundle bundle) {
        super.saveParamsToBundle(bundle);
        bundle.putInt(PARAM_SELECTED, showType);
    }

    @Override
    public int getLayout() {
        return R.layout.fragment_main;
    }

    @Override
    public void doingInResume() {
    }

    @Override
    protected boolean shouldSetDefaultTitleEvents() {
        return false;
    }

    /**
     * 显示有未读消息的标记
     */
    public void showUnreadFlag() {
        checkUnreadTotalCountIgnoreMutex();
    }

    private void showUnreadFlag(int num) {
        if (null != icon2Unread) {
            icon2Unread.setVisibility(num > 0 ? View.VISIBLE : View.GONE);
            icon2UnreadNum.setText(formatUnread(num));
        }
        if (null != icon4Unread) {
            int size = NimMessage.getUnRead();
            icon4Unread.setVisibility(size > 0 ? View.VISIBLE : View.GONE);
        }
    }

    @Override
    protected void destroyView() {

    }

    @Click({R.id.ui_tool_main_bottom_clickable_1, R.id.ui_tool_main_bottom_clickable_2,
            R.id.ui_tool_main_bottom_clickable_center,
            R.id.ui_tool_main_bottom_clickable_3, R.id.ui_tool_main_bottom_clickable_4})
    private void elementClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.ui_tool_main_bottom_clickable_1:
                if (showType != SHOW_HOME) {
                    showType = SHOW_HOME;
                    setDisplayPage();
                }
                break;
            case R.id.ui_tool_main_bottom_clickable_2:
                if (showType != SHOW_RECENT) {
                    showType = SHOW_RECENT;
                    setDisplayPage();
                }
                break;
            case R.id.ui_tool_main_bottom_clickable_center:
                iconCenter.startAnimation(App.clickAnimation());
                //ShortcutFragment.open(MainFragment.this);
                ArchiveCreateSelectorFragment.open(MainFragment.this, "");
                break;
            case R.id.ui_tool_main_bottom_clickable_3:
                if (showType != SHOW_GROUP) {
                    showType = SHOW_GROUP;
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
                SettingFragment.open(MainFragment.this);
                break;
            case R.id.ui_ui_custom_title_right_icon_2_container:
                // 打开消息页面
                SystemMessageFragment.open(MainFragment.this);
                break;
            case R.id.ui_ui_custom_title_left_container:
                // 搜索
                FullTextQueryFragment.open(MainFragment.this);
                break;
        }
    }

    private Fragment findFragment(String tag) {
        FragmentManager manager = Activity().getSupportFragmentManager();
        return manager.findFragmentByTag(tag);
    }

    private void initializeHome() {
        Fragment fragment = findFragment(TAG_HOME);
        if (null != fragment) {
            if (null == homeFragment) {
                homeFragment = (HomeFragment) fragment;
            }
        } else {
            homeFragment = new HomeFragment();
        }
    }

    private void initializeRecent() {
        Fragment fragment = findFragment(TAG_RECENT);
        if (null != fragment) {
            if (null == recentFragment) {
                recentFragment = (RecentContactsFragment) fragment;
                recentFragment.mainFragment = this;
            }
        } else {
            recentFragment = new RecentContactsFragment();
            recentFragment.mainFragment = this;
        }
    }

    private void initializeGroup() {
        Fragment fragment = findFragment(TAG_GROUP);
        if (null != fragment) {
            if (null == groupFragment) {
                groupFragment = (GroupFragment) fragment;
            }
        } else {
            groupFragment = new GroupFragment();
        }
    }

    private void initializeMine() {
        Fragment fragment = findFragment(TAG_MINE);
        if (null != fragment) {
            if (null == mineFragment) {
                mineFragment = (PersonalityFragment) fragment;
            }
        } else {
            mineFragment = new PersonalityFragment();
        }
    }

    private void hideFragments() {
        FragmentTransaction transaction = Activity().getSupportFragmentManager().beginTransaction();
        if (null != homeFragment && showType != SHOW_HOME) {
            transaction.hide(homeFragment);
        }
        if (null != recentFragment && showType != SHOW_RECENT) {
            transaction.hide(recentFragment);
        }
        if (null != groupFragment && showType != SHOW_GROUP) {
            transaction.hide(groupFragment);
        }
        if (null != mineFragment && showType != SHOW_MINE) {
            transaction.hide(mineFragment);
        }
        transaction.commit();
    }

    private void showFragment(BaseFragment fragment, String tag) {
        FragmentManager manager = Activity().getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        if (!fragment.isAdded()) {
            transaction.add(R.id.ui_fragment_main_frame_layout, fragment, tag);
        } else {
            transaction.show(fragment);
        }
        transaction.commit();
    }

    private void setDisplayPage() {
        hideFragments();
        switch (showType) {
            case SHOW_HOME:
                initializeHome();
                showFragment(homeFragment, TAG_HOME);
                break;
            case SHOW_RECENT:
                initializeRecent();
                showFragment(recentFragment, TAG_RECENT);
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
            return;
        }
        int color1 = getColor(R.color.textColorHintDark);
        int color2 = getColor(R.color.colorPrimary);

        iconView1.setText(showType == SHOW_HOME ? R.string.ui_icon_home : R.string.ui_icon_home_unselected);
        iconView1.setTextColor(showType == SHOW_HOME ? color2 : color1);
        textView1.setTextColor(showType == SHOW_HOME ? color2 : color1);

        iconView2.setText(showType == SHOW_RECENT ? R.string.ui_icon_chat_left_solid : R.string.ui_icon_chat_left_hollow);
        iconView2.setTextColor(showType == SHOW_RECENT ? color2 : color1);
        textView2.setTextColor(showType == SHOW_RECENT ? color2 : color1);

        textView2d5.setTextColor(color1);

        iconView3.setText(showType == SHOW_GROUP ? R.string.ui_icon_pentagon_solid : R.string.ui_icon_pentagon_hollow);
        iconView3.setTextColor(showType == SHOW_GROUP ? color2 : color1);
        textView3.setTextColor(showType == SHOW_GROUP ? color2 : color1);

        iconView4.setText(showType == SHOW_MINE ? R.string.ui_icon_main_individual_solid : R.string.ui_icon_main_individual_holow);
        iconView4.setTextColor(showType == SHOW_MINE ? color2 : color1);
        textView4.setTextColor(showType == SHOW_MINE ? color2 : color1);
    }
}
