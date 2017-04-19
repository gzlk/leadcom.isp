package com.gzlk.android.isp.fragment.individual;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.gzlk.android.isp.R;
import com.gzlk.android.isp.application.App;
import com.gzlk.android.isp.fragment.base.BaseLayoutSupportFragment;
import com.gzlk.android.isp.helper.StringHelper;
import com.gzlk.android.isp.helper.ToastHelper;
import com.gzlk.android.isp.holder.SimpleClickableViewHolder;
import com.gzlk.android.isp.listener.OnViewHolderClickListener;
import com.hlk.hlklib.lib.inject.ViewId;

/**
 * <b>功能描述：</b>关于<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/04/19 08:23 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/04/19 08:23 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class AboutFragment extends BaseLayoutSupportFragment {

    @ViewId(R.id.ui_about_to_evaluate)
    private View evaluateView;
    @ViewId(R.id.ui_about_to_help)
    private View helpView;
    @ViewId(R.id.ui_about_version)
    private TextView versionTextView;

    private SimpleClickableViewHolder evaluateHolder;
    private SimpleClickableViewHolder helpHolder;
    private String[] strings;

    @Override
    public int getLayout() {
        return R.layout.fragment_about;
    }

    @Override
    protected void getParamsFromBundle(Bundle bundle) {

    }

    @Override
    public void doingInResume() {
        setCustomTitle(R.string.ui_text_about_fragment_title);
        initializeHolders();
    }

    @Override
    protected boolean shouldSetDefaultTitleEvents() {
        return true;
    }

    @Override
    protected void saveParamsToBundle(Bundle bundle) {

    }

    @Override
    protected void destroyView() {

    }

    @SuppressWarnings("ConstantConditions")
    private void initializeHolders() {
        if (null == strings) {
            versionTextView.setText(StringHelper.getString(R.string.ui_text_about_version, App.app().version()));
            strings = StringHelper.getStringArray(R.array.ui_about);
        }
        if (null == evaluateHolder) {
            evaluateHolder = new SimpleClickableViewHolder(evaluateView, AboutFragment.this);
            evaluateHolder.addOnViewHolderClickListener(holderClickListener);
            evaluateHolder.showContent(strings[0]);
        }
        if (null == helpHolder) {
            helpHolder = new SimpleClickableViewHolder(helpView, AboutFragment.this);
            helpHolder.addOnViewHolderClickListener(holderClickListener);
            helpHolder.showContent(strings[1]);
        }
    }

    private OnViewHolderClickListener holderClickListener = new OnViewHolderClickListener() {
        @Override
        public void onClick(int index) {
            ToastHelper.make().showMsg(strings[index].replaceAll("[\\d+\\|]", ""));
        }
    };
}
