package com.gzlk.android.isp.activity;

import android.content.Intent;
import android.os.Bundle;

import com.gzlk.android.isp.cache.Cache;
import com.gzlk.android.isp.fragment.main.MainFragment;
import com.gzlk.android.isp.helper.StringHelper;
import com.gzlk.android.isp.model.nim.NimMessage;
import com.netease.nimlib.sdk.NimIntent;
import com.netease.nimlib.sdk.msg.constant.MsgTypeEnum;
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

public class NIMMessageActivity extends TitleActivity {

    private static boolean isFirstEnter = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        isToolbarSupported = false;
        supportTransparentStatusBar = true;
        super.onCreate(savedInstanceState);
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
            if (StringHelper.isEmpty(Cache.cache().userId)) {
                // 当前没有登录则转到登录页面
                switchToLogin();
            } else {
                // 已经登录过则处理消息，并转到主页，主页有自动登录逻辑
                handleIntent();
            }
        }
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
        Intent intent = getIntent();
        if (null != intent) {
            ArrayList<IMMessage> messages = (ArrayList<IMMessage>) intent.getSerializableExtra(NimIntent.EXTRA_NOTIFY_CONTENT);
            if (null != messages && messages.size() > 0) {
                // 处理收到的消息列表
                for (IMMessage msg : messages) {
                    if (msg.getMsgType() == MsgTypeEnum.custom) {
                        NimMessage nim = (NimMessage) msg.getAttachment();
                        handleNimMessage(msg.getFromAccount(), msg.getFromNick(), nim);
                    }
                }
                // 转到系统通知页面
            } else {
                switchToMain();
            }
        } else {
            switchToMain();
        }
    }

    private void handleNimMessage(String fromAccount, String fromName, NimMessage msg) {
        switch (msg.getType()) {
            case NimMessage.Type.JOIN:
                break;
            case NimMessage.Type.APPROVE:
                break;
            case NimMessage.Type.DISAPPROVE:
                break;
            case NimMessage.Type.INVITE:
                break;
            case NimMessage.Type.AGREE:
                break;
            case NimMessage.Type.DISAGREE:
                break;
        }
    }

    private void switchToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(intent);
        finish();
    }

    private void switchToMain() {
        switchToMain(0);
    }

    private void switchToMain(int selectedFragment) {
        Intent intent = new Intent(this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        Bundle bundle = new Bundle();
        bundle.putInt(MainFragment.PARAM_SELECTED, selectedFragment);
        intent.putExtras(bundle);

        startActivity(intent);
        finish();
    }
}
