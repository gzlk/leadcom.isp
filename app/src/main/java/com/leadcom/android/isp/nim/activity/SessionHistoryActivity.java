package com.leadcom.android.isp.nim.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;

import com.leadcom.android.isp.R;
import com.leadcom.android.isp.nim.searching.SearchingHistoryActivity;
import com.netease.nim.uikit.api.wrapper.NimToolBarOptions;
import com.netease.nim.uikit.business.session.module.Container;
import com.netease.nim.uikit.business.session.module.ModuleProxy;
import com.netease.nim.uikit.business.session.module.list.MessageListPanelEx;
import com.netease.nim.uikit.common.activity.ToolBarOptions;
import com.netease.nim.uikit.common.activity.UI;
import com.netease.nimlib.sdk.msg.constant.SessionTypeEnum;
import com.netease.nimlib.sdk.msg.model.IMMessage;

/**
 * <b>功能描述：</b>聊天历史记录<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/06/07 23:34 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/06/07 23:34 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class SessionHistoryActivity extends UI implements ModuleProxy {

    private static final String EXTRA_DATA_ACCOUNT = "EXTRA_DATA_ACCOUNT";
    private static final String EXTRA_DATA_SESSION_TYPE = "EXTRA_DATA_SESSION_TYPE";

    // context
    private SessionTypeEnum sessionType;
    private String account; // 对方帐号

    private MessageListPanelEx messageListPanel;

    public static void start(Context context, String account, SessionTypeEnum sessionType) {
        Intent intent = new Intent();
        intent.putExtra(EXTRA_DATA_ACCOUNT, account);
        intent.putExtra(EXTRA_DATA_SESSION_TYPE, sessionType);
        intent.setClass(context, SessionHistoryActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        View rootView = LayoutInflater.from(this).inflate(R.layout.nim_activity_session_history, null);
        setContentView(rootView);

        rootView.findViewById(R.id.ui_holder_view_searchable_icon_container).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                SearchingHistoryActivity.start(SessionHistoryActivity.this, account, sessionType);
            }
        });

        ToolBarOptions options = new NimToolBarOptions();
        options.titleId = R.string.ui_activity_property_chat_history_title;
        setToolBar(R.id.activity_toolbar, options);

        onParseIntent();

        Container container = new Container(this, account, sessionType, this);
        messageListPanel = new MessageListPanelEx(container, rootView, true, true);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        messageListPanel.onBackPressed();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        messageListPanel.onDestroy();
    }

    protected void onParseIntent() {
        account = getIntent().getStringExtra(EXTRA_DATA_ACCOUNT);
        sessionType = (SessionTypeEnum) getIntent().getSerializableExtra(EXTRA_DATA_SESSION_TYPE);
    }

    @Override
    public boolean sendMessage(IMMessage msg) {
        return false;
    }

    @Override
    public void onInputPanelExpand() {

    }

    @Override
    public void shouldCollapseInputPanel() {

    }

    @Override
    public boolean isLongClickEnabled() {
        return false;
    }

    @Override
    public void onItemFooterClick(IMMessage message) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (messageListPanel != null) {
            messageListPanel.onActivityResult(requestCode, resultCode, data);
        }
    }
}
