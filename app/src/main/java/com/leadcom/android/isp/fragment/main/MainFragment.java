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

    public static MainFragment newInstance(Bundle bundle) {
        MainFragment mf = new MainFragment();
        mf.setArguments(bundle);
        return mf;
    }

    public static Bundle getBundle(boolean isCreateNew) {
        Bundle bundle = new Bundle();
        bundle.putBoolean(PARAM_CREATE, isCreateNew);
        return bundle;
    }

    /**
     * 新建MainFragment的时候传入的参数，以此当作初始化显示的页面（也即ViewPager当前显示的页面的index），int型
     */
    public static final String PARAM_SELECTED = "mf_param1";
    private static final String PARAM_CREATE = "mf_create_type";
    private static final String TAG_HOME = "main_home";
    //    private static final String TAG_RECENT = "main_recent";
    private static final String TAG_MESSAGE = "main_message";
    private static final String TAG_GROUP = "main_group";
    private static final String TAG_MINE = "main_mine";

    private static final int SHOW_HOME = 0, SHOW_MSG = 1, SHOW_GROUP = 2, SHOW_MINE = 3;

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

    /**
     * 是否是新创建的MainFragment
     */
    public boolean isCreateNew = false;
    // 首页4个fragment
    private HomeFragment homeFragment;
    //    private RecentContactsFragment recentFragment;
    private SystemMessageFragment messageFragment;
    private GroupFragment groupFragment;
    private PersonalityFragment mineFragment;
    private int showType = SHOW_HOME;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        NimApplication.addNotificationChangeCallback(callback);
        //registerObservers(true);
    }

//    private void registerObservers(boolean register) {
//        NIMClient.getService(MsgServiceObserve.class).observeRecentContact(recentContactChangeObserver, register);
//    }
//
//    Observer<List<RecentContact>> recentContactChangeObserver = new Observer<List<RecentContact>>() {
//        @Override
//        public void onEvent(List<RecentContact> recentContacts) {
//            log("message observer onEvent(Main): " + (null == recentContacts ? "null" : recentContacts.size()));
//            showUnreadFlag();
//        }
//    };
//
//    private void checkUnreadTotalCountIgnoreMutex() {
//        NIMClient.getService(MsgService.class).queryRecentContacts().setCallback(new RequestCallback<List<RecentContact>>() {
//            @Override
//            public void onSuccess(List<RecentContact> list) {
//                int count = 0;
//                if (list == null || list.size() < 1) {
//                    return;
//                }
//                for (RecentContact contact : list) {
//                    int unread = contact.getUnreadCount();
//                    if (unread > 0) {
//                        if (contact.getSessionType() == SessionTypeEnum.Team) {
//                            // 查看群聊是否静音，静音的话不统计
//                            Team team = TeamDataCache.getInstance().getTeamById(contact.getContactId());
//                            if (team.getMessageNotifyType() != TeamMessageNotifyTypeEnum.Mute) {
//                                count += unread;
//                            }
//                        } else if (contact.getSessionType() == SessionTypeEnum.P2P) {
//                            boolean notify = NIMClient.getService(FriendService.class).isNeedMessageNotify(contact.getContactId());
//                            if (notify) {
//                                count += unread;
//                            }
//                        } else {
//                            count += unread;
//                        }
//                    }
//                }
//                showUnreadFlag(count);
//            }
//
//            @Override
//            public void onFailed(int code) {
//
//            }
//
//            @Override
//            public void onException(Throwable exception) {
//
//            }
//        });
//    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setDisplayPage();
        NimApplication.dispatchCallbacks();
    }

    @Override
    public void onDestroy() {
//        registerObservers(false);
        NimApplication.removeNotificationChangeCallback(callback);
        super.onDestroy();
    }

    private NotificationChangeHandleCallback callback = new NotificationChangeHandleCallback() {
        @Override
        public void onChanged() {
            showUnreadFlag(0);
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
        isCreateNew = bundle.getBoolean(PARAM_CREATE, false);
    }

    @Override
    protected void saveParamsToBundle(Bundle bundle) {
        super.saveParamsToBundle(bundle);
        bundle.putInt(PARAM_SELECTED, showType);
        bundle.putBoolean(PARAM_CREATE, isCreateNew);
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
//        checkUnreadTotalCountIgnoreMutex();
        showUnreadFlag(0);
    }

    private void showUnreadFlag(int num) {
        if (null != icon2Unread) {
            icon2Unread.setVisibility(App.app().getUnreadCount() > 0 ? View.VISIBLE : View.GONE);
            //icon2UnreadNum.setText(formatUnread(num));
        }
//        if (null != icon4Unread) {
//            int size = NimMessage.getUnRead();
//            icon4Unread.setVisibility(size > 0 ? View.VISIBLE : View.GONE);
//        }
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
                if (showType != SHOW_MSG) {
                    showType = SHOW_MSG;
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

    public void recreateFragment() {
        FragmentManager manager = Activity().getSupportFragmentManager();
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
        FragmentManager manager = Activity().getSupportFragmentManager();
        return manager.findFragmentByTag(tag);
    }

    private void removeFragment(Fragment fragment) {
        Activity().getSupportFragmentManager().beginTransaction().remove(fragment).commitAllowingStateLoss();
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
        FragmentTransaction transaction = Activity().getSupportFragmentManager().beginTransaction();
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
        FragmentManager manager = Activity().getSupportFragmentManager();
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
            return;
        }
        int color1 = getColor(R.color.textColorHintDark);
        int color2 = getColor(R.color.colorPrimary);

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
}
