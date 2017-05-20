package com.gzlk.android.isp.activity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.LinearLayout;

import com.gzlk.android.isp.R;
import com.gzlk.android.isp.cache.Cache;
import com.gzlk.android.isp.crash.system.SysInfoUtil;
import com.gzlk.android.isp.helper.StringHelper;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.inject.ViewUtility;
import com.netease.nimlib.sdk.NimIntent;
import com.netease.nimlib.sdk.msg.model.IMMessage;

import java.util.ArrayList;

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

    @ViewId(R.id.ui_welcome_root_container)
    private LinearLayout root;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        ViewUtility.bind(this);

        if (savedInstanceState != null) {
            // 从堆栈恢复时，设置一个新的空白intent，不再重复解析之前的intent
            setIntent(new Intent());
        }
        if (!isFirstEnter) {
            // 不是第一次进入时才处理发送过来的intent
            handleIntent();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (isFirstEnter) {
            isFirstEnter = false;
            // 第一次进入显示欢迎页面
            animateRoot();
            root.postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (StringHelper.isEmpty(Cache.cache().userId)) {
                        // 当前没有登录则转到登录页面
                        LoginActivity.start(WelcomeActivity.this);
                        finish();
                    } else {
                        // 已经登录过则处理消息，并转到主页，主页有自动登录逻辑
                        handleIntent();
                    }
                }
            }, 1500);
        }
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
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(0, 0);
    }

    @SuppressWarnings("unchecked")
    private void handleIntent() {
        if (StringHelper.isEmpty(Cache.cache().userId)) {
            // 判断当前app是否正在运行
            if (!SysInfoUtil.stackResumed(this)) {
                LoginActivity.start(this);
            }
            finish();
        } else {
            Intent intent = getIntent();
            if (null != intent) {
                ArrayList<IMMessage> messages = (ArrayList<IMMessage>) intent.getSerializableExtra(NimIntent.EXTRA_NOTIFY_CONTENT);
                if (null == messages || messages.size() > 1) {
                    // 如果消息为空或者消息数量大于1个则直接打开默认的首页即可
                    switchToMain();
                } else {
                    // 针对发过来的消息打开首页并按照intent内容提示用户
                    switchToMain(new Intent().putExtra(NimIntent.EXTRA_NOTIFY_CONTENT, messages.get(0)));
                }
            } else {
                switchToMain();
            }
        }
    }

    private void switchToMain() {
        switchToMain(null);
    }

    private void switchToMain(Intent intent) {
        MainActivity.start(this, intent);
        finish();
    }
}
