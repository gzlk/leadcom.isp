package com.gzlk.android.isp.activity;

import android.os.Bundle;

/**
 * <b>功能描述：</b>登录页面<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/04/06 16:56 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/04/06 16:56 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class LoginActivity extends TitleActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // loading 页面不需要标题栏
        showToolbar(false);
    }
}
