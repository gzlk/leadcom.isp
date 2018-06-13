package com.leadcom.android.isp.activity;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.hlk.hlklib.lib.inject.Click;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.inject.ViewUtility;
import com.hlk.hlklib.lib.view.CorneredButton;
import com.leadcom.android.isp.R;
import com.leadcom.android.isp.application.App;
import com.leadcom.android.isp.cache.Cache;
import com.leadcom.android.isp.etc.SysInfoUtil;
import com.leadcom.android.isp.fragment.archive.ArchiveDetailsFragment;
import com.leadcom.android.isp.helper.PreferenceHelper;
import com.leadcom.android.isp.helper.StringHelper;
import com.leadcom.android.isp.model.archive.Archive;

/**
 * <b>功能描述：</b>网易云消息处理Activity<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/05/18 22:08 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/05/18 22:08 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class WelcomeActivity extends BaseActivity {

    private static boolean isFirstEnter = true;
    private boolean isWelcome = false;

    @ViewId(R.id.ui_welcome_root_container)
    private LinearLayout root;
    @ViewId(R.id.ui_welcome_to_join_in)
    private CorneredButton button;

    private String guided = "";

    public static void open(Context context, String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        ViewUtility.bind(this);
        resetButtonPosition();

        guided = PreferenceHelper.get(R.string.pf_is_guide_page_shown, "");
        if (savedInstanceState != null) {
            // 从堆栈恢复时，设置一个新的空白intent，不再重复解析之前的intent
            setIntent(new Intent());
        }
        if (!isFirstEnter) {
            // 不是第一次进入时才处理发送过来的intent
            handleIntent();
        } else {
            showWelcome();
        }
    }

    private void resetButtonPosition() {
        int width = getApplicationContext().getResources().getDisplayMetrics().widthPixels;
        if (width >= 720) {
            // 超过720p的，button需要向上移动15dp
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) button.getLayoutParams();
            params.bottomMargin = getResources().getDimensionPixelOffset(R.dimen.ui_static_dp_25);
            button.setLayoutParams(params);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isFirstEnter) {
            isFirstEnter = false;
            if (isWelcome) {
                delayToShowRoot();
            } else {
                handleIntentOrGoingToLogin();
            }
        }
    }

    private void handleIntentOrGoingToLogin() {
        if (isEmptyUser()) {
            // 当前没有登录则转到登录页面
            toLogin();
        } else {
            // 已经登录过则处理消息，并转到主页，主页有自动登录逻辑
            handleIntent();
        }
    }

    private void delayToShowRoot() {
        root.postDelayed(new Runnable() {
            @Override
            public void run() {
                animateRoot();
                if (StringHelper.isEmpty(guided)) {
                    // 如果没有显示过引导页，则此时一直显示，同时显示欢迎进入按钮
                    animateButton();
                } else {
                    delayToLogin();
                }
            }
        }, 1500);
    }

    private void delayToLogin() {
        root.postDelayed(new Runnable() {
            @Override
            public void run() {
                handleIntentOrGoingToLogin();
            }
        }, 2000);
    }

    @Click({R.id.ui_welcome_to_join_in})
    private void elementClick(View view) {
        PreferenceHelper.save(R.string.pf_is_guide_page_shown, "YES");
        toLogin();
    }

    private void toLogin() {
        if (Cache.cache().isNeedSync()) {
            // 登录信息已过期则需要重新登录
            LoginActivity.start(WelcomeActivity.this);
            finish();
        } else {
            switchToMain();
        }
    }

    // 渐变显示欢迎进入按钮
    private void animateButton() {
        button.animate().alpha(1)
                .setDuration(getInteger(R.integer.animation_default_duration))
                .start();
    }

    // 渐变显示欢迎页
    private void animateRoot() {
        root.animate()
                .alpha(1)
                .setDuration(getInteger(R.integer.animation_default_duration))
                .start();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        /*
          如果Activity在，不会走到onCreate，而是onNewIntent，这时候需要setIntent
          场景：点击通知栏跳转到此，会收到Intent
         */
        setIntent(intent);
        handleIntent();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.clear();
        super.onSaveInstanceState(outState);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, 0);
    }

    private boolean isEmptyUser() {
        return StringHelper.isEmpty(Cache.cache().userId);
    }

    @SuppressWarnings({"unchecked", "ConstantConditions"})
    private void handleIntent() {
        if (isEmptyUser()) {
            // 判断当前app是否正在运行
            if (!SysInfoUtil.stackResumed(this)) {
                LoginActivity.start(this);
            }
            finish();
        } else {
            Intent intent = getIntent();
            if (null != intent) {
                String action = intent.getAction();
                if (!StringHelper.isEmpty(action)) {
                    if (action.equals(Intent.ACTION_VIEW)) {
                        Uri uri = intent.getData();
                        if (null != uri) {
                            String path = uri.getPath();
                            String id = uri.getQueryParameter("id");
                            if (path.contains("archive")) {
                                String type = uri.getQueryParameter("type");
                                if (StringHelper.isEmpty(type, true)) {
                                    log("传入的参数错误：type = null");
                                }
                                // 默认组织档案
                                int tp = StringHelper.isEmpty(type, true) ? Archive.Type.GROUP : Integer.valueOf(type);
                                String groupId = uri.getQueryParameter("groupid");
                                if (StringHelper.isEmpty(groupId, true) || groupId.contains("undefined")) {
                                    groupId = "";
                                }
                                String authorId = uri.getQueryParameter("authorid");
                                ArchiveDetailsFragment.open(this, groupId, "", id, tp, false, true, authorId);
                            } else if (path.contains("user")) {
                                App.openUserInfo(this, id);
                            }
                            finish();
                        } else {
                            toLogin();
                        }
                    } else {
                        switchToMain();
                    }
                } else {
                    // 如果消息为空则打开登录页面，同步用户信息后登录
                    toLogin();
                }
            } else {
                //switchToMain();
                toLogin();
            }
        }
    }

    /**
     * 首次进入，打开欢迎界面
     */
    private void showWelcome() {
        getWindow().setBackgroundDrawableResource(R.drawable.ui_background_welcome);
        isWelcome = true;
    }

    private void switchToMain() {
        switchToMain(null);
    }

    private void switchToMain(Intent intent) {
        MainActivity.start(this, intent);
        finish();
    }
}
