package com.netease.nim.uikit.api.wrapper;

import com.netease.nim.uikit.R;
import com.netease.nim.uikit.common.activity.ToolBarOptions;

/**
 * Created by hzxuwen on 2016/6/16.
 */
public class NimToolBarOptions extends ToolBarOptions {

    public NimToolBarOptions() {
        logoId = 0;//R.drawable.nim_actionbar_nest_dark_logo;
        navigateId = R.drawable.ic_action_arrow_back_light;//R.drawable.nim_actionbar_dark_back_icon;
        isNeedNavigate = true;
    }
}
