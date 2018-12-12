package com.leadcom.android.isp.fragment.base;

import android.os.Bundle;
import android.widget.TextView;

import com.leadcom.android.isp.R;
import com.leadcom.android.isp.helper.StringHelper;
import com.leadcom.android.isp.helper.ToastHelper;
import com.leadcom.android.isp.listener.OnTitleButtonClickListener;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.view.ClearEditText;

/**
 * <b>功能描述：</b>弹出新窗口输入内容的fragment<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/05/02 08:12 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/05/02 08:12 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class BasePopupInputSupportFragment extends BaseDelayRefreshSupportFragment {

    private static final String TITLE = "bpisf_title";
    private static final String HINT = "bpisf_hint";
    private static final String VALUE = "bpisf_value";
    private static final String LENGTH = "bpisf_max_length";
    private static final String INPUT = "bpisf_input_type";
    private static final String EXTRA = "bpisf_extra_regex";
    private static final String VERIFY = "bpisf_verify_regex";
    private static final String WARNING = "bpisf_warning";

    public static BasePopupInputSupportFragment newInstance(String params) {
        BasePopupInputSupportFragment bpisf = new BasePopupInputSupportFragment();
        String[] strings = splitParameters(params);
        Bundle bundle = new Bundle();
        bundle.putString(TITLE, strings[0]);
        bundle.putString(HINT, strings[1]);
        bundle.putString(VALUE, strings[2]);
        bundle.putInt(LENGTH, Integer.valueOf(strings[3]));
        bundle.putInt(INPUT, Integer.valueOf(strings[4]));
        String regex = strings[5];
        if (!StringHelper.isEmpty(regex)) {
            regex = regex.replace("#", "-").replace("@", ",");
        }
        bundle.putString(EXTRA, regex);
        regex = strings[6];
        if (!StringHelper.isEmpty(regex)) {
            regex = regex.replace("#", "-").replace("@", ",");
        }
        bundle.putString(VERIFY, regex);
        bundle.putString(WARNING, strings[7]);
        bpisf.setArguments(bundle);
        return bpisf;
    }

    public static boolean allowBlank = false;

    private String mTitle, mHint, mValue, mExtra, mVerify, mWarning;
    private int mMaxLen, mInput;

    @Override
    protected void getParamsFromBundle(Bundle bundle) {
        super.getParamsFromBundle(bundle);
        mTitle = bundle.getString(TITLE, "");
        mHint = bundle.getString(HINT, "");
        mValue = bundle.getString(VALUE, "");
        mMaxLen = bundle.getInt(LENGTH, 0);
        mInput = bundle.getInt(INPUT, 0);
        mExtra = bundle.getString(EXTRA, "");
        mVerify = bundle.getString(VERIFY, "");
        mWarning = bundle.getString(WARNING, "");
    }

    @Override
    protected void saveParamsToBundle(Bundle bundle) {
        super.saveParamsToBundle(bundle);
        bundle.putString(TITLE, mTitle);
        bundle.putString(HINT, mHint);
        String value = inputView.getValue();
        if (!StringHelper.isEmpty(value)) {
            mValue = value;
        }
        bundle.putString(VALUE, mValue);
        bundle.putInt(LENGTH, mMaxLen);
        bundle.putInt(INPUT, mInput);
        bundle.putString(EXTRA, mExtra);
        bundle.putString(VERIFY, mVerify);
        bundle.putString(WARNING, mWarning);
    }

    // UI
    @ViewId(R.id.ui_popup_input_value)
    private ClearEditText inputView;
    @ViewId(R.id.ui_popup_input_warning)
    private TextView warningTextView;

    @Override
    protected void onDelayRefreshComplete(@DelayType int type) {

    }

    @Override
    public int getLayout() {
        return R.layout.fragment_popup_input_fragment;
    }

    @Override
    public void doingInResume() {
        setCustomTitle(mTitle);
        inputView.setTextHint(mHint);
        inputView.setValue(mValue);
        if (!StringHelper.isEmpty(mValue)) {
            inputView.focusEnd();
        }
        inputView.setMaxLength(mMaxLen);
        inputView.setInputType(mInput);
        inputView.setValueExtract(mExtra);
        inputView.setValueVerify(mVerify);
        warningTextView.setText(mWarning);
        setRightText(R.string.ui_base_text_save);
        setRightTitleClickListener(new OnTitleButtonClickListener() {
            @Override
            public void onClick() {
                tryResult();
            }
        });
    }

    @Override
    protected boolean shouldSetDefaultTitleEvents() {
        return true;
    }

    @Override
    protected void destroyView() {

    }

    @Override
    public void onDestroy() {
        allowBlank = false;
        super.onDestroy();
    }

    private void tryResult() {
        String result = inputView.getValue();
        if (!allowBlank) {
            // 不允许空白输入时检测输入
            if (StringHelper.isEmpty(result)) {
                ToastHelper.helper().showMsg(R.string.ui_popup_input_invalid_input);
                return;
            }
        }
        resultData(result);
    }
}
