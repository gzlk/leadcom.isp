package com.gzlk.android.isp.fragment.individual;

import android.widget.TextView;

import com.gzlk.android.isp.R;
import com.gzlk.android.isp.cache.Cache;
import com.gzlk.android.isp.fragment.login.BaseVerifyFragment;
import com.gzlk.android.isp.helper.DialogHelper;
import com.gzlk.android.isp.helper.SimpleDialogHelper;
import com.gzlk.android.isp.helper.StringHelper;
import com.gzlk.android.isp.helper.ToastHelper;
import com.gzlk.android.isp.listener.OnTitleButtonClickListener;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.view.ClearEditText;

/**
 * <b>功能描述：</b>修改手机号码<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/05/02 15:11 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/05/02 15:11 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class ModifyPhoneFragment extends BaseVerifyFragment {

    // UI
    @ViewId(R.id.ui_modify_phone_number_input_title)
    private TextView inputTitle;
    @ViewId(R.id.ui_modify_phone_number_input_value)
    private ClearEditText inputView;
    @ViewId(R.id.ui_modify_phone_number_input_warning)
    private TextView warningTextView;

    @Override
    public int getLayout() {
        return R.layout.fragment_modify_phone;
    }

    @Override
    public void doingInResume() {
        setLeftIcon(0);
        setCustomTitle(R.string.ui_text_modify_phone_number_fragment_title);
        setLeftText(R.string.ui_base_text_cancel);
        setRightText(R.string.ui_base_text_next_step);
        inputTitle.setText(StringHelper.getString(R.string.ui_text_modify_phone_number_86, ""));
        setRightTitleClickListener(new OnTitleButtonClickListener() {
            @Override
            public void onClick() {
                tryVerifyPhone();
            }
        });
        warningTextView.setText(StringHelper.getString(R.string.ui_text_modify_phone_number_input_warning, Cache.cache().me.getPhone()));
    }

    @Override
    protected boolean shouldSetDefaultTitleEvents() {
        return true;
    }

    @Override
    protected void destroyView() {

    }

    private void tryVerifyPhone() {
        final String phone = inputView.getValue();
        if (StringHelper.isEmpty(phone)) {
            ToastHelper.make().showMsg(R.string.ui_text_verify_phone_value_incorrect);
            return;
        }
        SimpleDialogHelper.init(Activity()).show(StringHelper.getString(R.string.ui_text_modify_phone_number_verify_warning, phone), StringHelper.getString(R.string.ui_base_text_ok), StringHelper.getString(R.string.ui_base_text_cancel), new DialogHelper.OnDialogConfirmListener() {
            @Override
            public boolean onConfirm() {
                resultData(phone);
                return true;
            }
        }, null);
    }
}
