package com.gzlk.android.isp.activity;

import android.os.Bundle;
import android.view.KeyEvent;

import com.gzlk.android.isp.fragment.main.MainFragment;
import com.netease.nimlib.sdk.NIMClient;
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
    }

    @Override
    protected boolean onBackKeyEvent(int keyCode, KeyEvent event) {
        return mainFragment.onBackKeyEvent();
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
}
