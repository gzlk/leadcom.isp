package com.gzlk.android.isp.fragment.base;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.gzlk.android.isp.activity.MainActivity;
import com.gzlk.android.isp.etc.Utils;
import com.gzlk.android.isp.fragment.main.MainFragment;

/**
 * <b>功能描述：</b><br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/04/04 20:08 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/04/04 20:08 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public abstract class BaseFragment extends PermissionHandleFragment {

    /**
     * 当前 fragment 的 layout 资源 id
     */
    private int mLayout;

    protected View _mView;

    protected Gson mGson = new Gson();

    /**
     * BackKey事件
     */
    public boolean onBackKeyEvent() {
        return false;
    }

    /**
     * 将字符串以半角逗号分隔成字符数组
     */
    protected static String[] splitParameters(String params) {
        if (params.charAt(0) == '[') {
            // json 对象
            return new Gson().fromJson(params, new TypeToken<String[]>() {
            }.getType());
        } else {
            // 普通字符串
            return params.split(",", -1);
        }
    }

    /**
     * 关闭Activity
     *
     * @param toMainUI true=直接返回主页面并且固定显示“抢”页，false=普通Activity关闭（受backState堆栈的顺序影响）
     */
    public void finish(boolean toMainUI) {
        // 这里固定为2，也即首页中的“抢”
        finish(toMainUI ? 2 : -1);
    }

    /**
     * 直接返回到主页面，并且指定主页面要显示的页面index
     *
     * @param mainPageDisplayIndex 指定主页面中ViewPager要显示的index。小于0时跟普通Activity的backKey事件一样（并不一定能够返回到主页面）
     */
    public void finish(int mainPageDisplayIndex) {
        Utils.hidingInputBoard(_mView);
        if (mainPageDisplayIndex < 0) {
            Activity().finish();
        } else {
            // 打开主UI
            Intent intent = new Intent(Activity(), MainActivity.class);

            Bundle bundle = new Bundle();
            // 这里默认返回到主页的时候都显示“找”页面
            bundle.putInt(MainFragment.PARAM_SELECTED, mainPageDisplayIndex);
            intent.putExtras(bundle);

            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            Activity().startActivity(intent);
        }
    }
}
