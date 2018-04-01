package com.leadcom.android.isp.fragment.individual;

import android.content.Intent;
import android.os.Bundle;

import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.view.ClearEditText;
import com.hlk.hlklib.lib.view.CorneredEditText;
import com.leadcom.android.isp.R;
import com.leadcom.android.isp.fragment.base.BaseFragment;
import com.leadcom.android.isp.fragment.base.BaseLayoutSupportFragment;
import com.leadcom.android.isp.helper.StringHelper;
import com.leadcom.android.isp.listener.OnTitleButtonClickListener;


/**
 * <b>功能描述：</b>编辑个人姓名和简介<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2018/04/01 15:30 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>version: 1.0.0 <br />
 * <b>修改时间：</b>2017/10/04 18:50 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */
public class UserNameEditFragment extends BaseLayoutSupportFragment {

    public static final String PARAM_NAME = "unef_name";
    public static final String PARAM_INTRO = "unef_intro";

    public static UserNameEditFragment newInstance(Bundle bundle) {
        UserNameEditFragment unef = new UserNameEditFragment();
        unef.setArguments(bundle);
        return unef;
    }

    public static void open(BaseFragment fragment, String name, String intro) {
        Bundle bundle = new Bundle();
        bundle.putString(PARAM_NAME, name);
        bundle.putString(PARAM_INTRO, intro);
        fragment.openActivity(UserNameEditFragment.class.getName(), bundle, REQUEST_CHANGE, true, true);
    }

    private String oldName, oldIntro;

    @ViewId(R.id.ui_main_personality_name_edit_name)
    private CorneredEditText cetName;
    @ViewId(R.id.ui_main_personality_name_edit_intro)
    private ClearEditText cetIntro;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        cetName.setText(oldName);
        cetIntro.setValue(StringHelper.escapeFromHtml(oldIntro));
        setCustomTitle(R.string.ui_text_personality_name_edit_fragment_title);
        setRightText(R.string.ui_base_text_complete);
        setRightTitleClickListener(new OnTitleButtonClickListener() {
            @Override
            public void onClick() {
                Intent data = new Intent();
                data.putExtra(PARAM_NAME, cetName.getValue());
                data.putExtra(PARAM_INTRO, StringHelper.escapeToHtml(cetIntro.getValue()));
                resultData(data);
            }
        });
    }

    @Override
    protected boolean checkStillEditing() {
        return !isEmpty(cetName.getValue()) || !isEmpty(cetIntro.getValue());
    }

    @Override
    public int getLayout() {
        return R.layout.fragment_main_personality_name_edit;
    }

    @Override
    protected void getParamsFromBundle(Bundle bundle) {
        oldName = bundle.getString(PARAM_NAME, "");
        oldIntro = bundle.getString(PARAM_INTRO, "");
    }

    @Override
    public void doingInResume() {

    }

    @Override
    protected boolean shouldSetDefaultTitleEvents() {
        return true;
    }

    @Override
    protected void saveParamsToBundle(Bundle bundle) {
        bundle.putString(PARAM_NAME, oldName);
        bundle.putString(PARAM_INTRO, oldIntro);
    }

    @Override
    protected void destroyView() {

    }
}
