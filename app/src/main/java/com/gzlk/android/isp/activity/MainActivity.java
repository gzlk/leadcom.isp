package com.gzlk.android.isp.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;

import com.gzlk.android.isp.R;
import com.gzlk.android.isp.api.listener.OnSingleRequestListener;
import com.gzlk.android.isp.api.user.UserRequest;
import com.gzlk.android.isp.fragment.main.MainFragment;
import com.gzlk.android.isp.helper.DialogHelper;
import com.gzlk.android.isp.helper.SimpleDialogHelper;
import com.gzlk.android.isp.helper.StringHelper;
import com.gzlk.android.isp.model.Dao;
import com.gzlk.android.isp.model.nim.NimMessage;
import com.gzlk.android.isp.model.user.User;
import com.netease.nimlib.sdk.NIMClient;
import com.netease.nimlib.sdk.NimIntent;
import com.netease.nimlib.sdk.Observer;
import com.netease.nimlib.sdk.msg.MsgServiceObserve;
import com.netease.nimlib.sdk.msg.constant.MsgTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;

import java.util.List;

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

    public static void start(Context context) {
        start(context, 0);
    }

    public static void start(Context context, int selectedIndex) {
        start(context, new Intent().putExtra(MainFragment.PARAM_SELECTED, selectedIndex));
    }

    public static void start(Context context, Intent extras) {
        Intent intent = new Intent(context, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        if (null != extras) {
            if (!intent.hasExtra(MainFragment.RESULT_STRING)) {
                // 默认打开第一页
                intent.putExtra(MainFragment.PARAM_SELECTED, 0);
            }
            intent.putExtras(extras);
        }
        context.startActivity(intent);
    }

    private MainFragment mainFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        supportPressAgainToExit = true;
        supportTransparentStatusBar = true;
        isToolbarSupported = false;
        super.onCreate(savedInstanceState);
        NIMClient.getService(MsgServiceObserve.class).observeReceiveMessage(incomingMessageObserver, true);
        if (null == mainFragment) {
            mainFragment = new MainFragment();
        }
        setMainFrameLayout(mainFragment);
        parseIntent();
    }

    @Override
    protected boolean onBackKeyEvent(int keyCode, KeyEvent event) {
        return mainFragment.onBackKeyEvent();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        setIntent(intent);
        parseIntent();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        outState.clear();
    }

    @Override
    protected void onDestroy() {
        NIMClient.getService(MsgServiceObserve.class).observeReceiveMessage(incomingMessageObserver, false);
        super.onDestroy();
    }

    Observer<List<IMMessage>> incomingMessageObserver = new Observer<List<IMMessage>>() {
        @Override
        public void onEvent(List<IMMessage> messages) {
            // 处理新收到的消息，为了上传处理方便，SDK 保证参数 messages 全部来自同一个聊天对象。
            for (IMMessage msg : messages) {
                if (msg.getMsgType() == MsgTypeEnum.custom) {

                }
            }
        }
    };

    private void parseIntent() {
        Intent intent = getIntent();
        if (null != intent) {
            if (intent.hasExtra(NimIntent.EXTRA_NOTIFY_CONTENT)) {
                // 点击通知栏传过来的消息
                IMMessage message = (IMMessage) getIntent().getSerializableExtra(NimIntent.EXTRA_NOTIFY_CONTENT);
                if (message.getMsgType() == MsgTypeEnum.custom) {
                    NimMessage nim = (NimMessage) message.getAttachment();
                    handleNimMessage(message.getFromAccount(), nim);
                }
            }
        }
    }

    private void handleNimMessage(String fromAccount, NimMessage msg) {
        User user = new Dao<>(User.class).query(fromAccount);
        if (null == user) {
            fetchingUser(fromAccount, msg);
        } else {
            handleNimMessageDetails(user.getName(), msg);
        }
    }

    private void fetchingUser(String account, final NimMessage msg) {
        UserRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<User>() {
            @Override
            public void onResponse(User user, boolean success, String message) {
                super.onResponse(user, success, message);
                if (success) {
                    if (null != user && !StringHelper.isEmpty(user.getId())) {
                        new Dao<>(User.class).save(user);
                        handleNimMessageDetails(user.getName(), msg);
                    }
                }
            }
        }).find(account);
    }

    private void handleNimMessageDetails(String fromName, NimMessage msg) {
        String text = "";
        switch (msg.getType()) {
            case NimMessage.Type.JOIN:
                text = StringHelper.getString(R.string.ui_dialog_text_group_join, fromName, "您的组织");
                break;
            case NimMessage.Type.APPROVE:
                text = StringHelper.getString(R.string.ui_dialog_text_group_approve, "xx组织");
                break;
            case NimMessage.Type.DISAPPROVE:
                text = StringHelper.getString(R.string.ui_dialog_text_group_disapprove, "xx组织");
                break;
            case NimMessage.Type.INVITE:
                text = StringHelper.getString(R.string.ui_dialog_text_group_invite, fromName, "xx组织");
                break;
            case NimMessage.Type.AGREE:
                text = StringHelper.getString(R.string.ui_dialog_text_group_agree, fromName, "xx组织");
                break;
            case NimMessage.Type.DISAGREE:
                text = StringHelper.getString(R.string.ui_dialog_text_group_disagree, fromName, "xx组织");
                break;
        }
        if (!StringHelper.isEmpty(text)) {
            SimpleDialogHelper.init(this).show(text, StringHelper.getString(R.string.ui_base_text_ok), "", new DialogHelper.OnDialogConfirmListener() {
                @Override
                public boolean onConfirm() {
                    return true;
                }
            }, null);
        }
    }
}
