package com.leadcom.android.isp.fragment.organization;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.hlk.hlklib.lib.inject.Click;
import com.hlk.hlklib.lib.inject.ViewId;
import com.leadcom.android.isp.R;
import com.leadcom.android.isp.fragment.base.BaseFragment;
import com.leadcom.android.isp.fragment.base.BaseViewPagerSupportFragment;

import java.util.ArrayList;

import static com.leadcom.android.isp.fragment.organization.BaseOrganizationFragment.PARAM_NAME;

/**
 * <b>功能描述：</b>组织成员、下级组织/支部/成员拾取器<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2018/09/17 22:24 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>version: 1.0.0 <br />
 * <b>修改时间：</b>2017/10/04 18:50 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */
public class GroupAllPickerFragment extends BaseViewPagerSupportFragment {

    public static GroupAllPickerFragment newInstance(Bundle bundle) {
        GroupAllPickerFragment gapf = new GroupAllPickerFragment();
        gapf.setArguments(bundle);
        return gapf;
    }

    public static void open(BaseFragment fragment, String groupId, String groupName, ArrayList<String> selected) {
        Bundle bundle = new Bundle();
        bundle.putString(PARAM_QUERY_ID, groupId);
        bundle.putString(PARAM_NAME, groupName);
        bundle.putStringArrayList(PARAM_JSON, selected);
        fragment.openActivity(GroupAllPickerFragment.class.getName(), bundle, REQUEST_SELECT, true, false);
    }

    @ViewId(R.id.ui_group_concern_main_top_channel_1)
    private TextView topText1;
    @ViewId(R.id.ui_group_concern_main_top_channel_2)
    private TextView topText2;
    @ViewId(R.id.ui_group_concern_main_top_channel_1_line)
    private View topLine1;
    @ViewId(R.id.ui_group_concern_main_top_channel_2_line)
    private View topLine2;
    @ViewId(R.id.ui_ui_custom_title_left_container)
    private View leftView;
    private ArrayList<String> mSelected;

    @Override
    public int getLayout() {
        return R.layout.fragment_group_concern_main;
    }

    @Override
    protected void getParamsFromBundle(Bundle bundle) {
        super.getParamsFromBundle(bundle);
        mSelected = bundle.getStringArrayList(PARAM_JSON);
        if (null == mSelected) {
            mSelected = new ArrayList<>();
        }
    }

    @Override
    protected void saveParamsToBundle(Bundle bundle) {
        super.saveParamsToBundle(bundle);
        bundle.putStringArrayList(PARAM_JSON, mSelected);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        leftView.setVisibility(View.GONE);
        setCustomTitle(R.string.ui_group_activity_editor_participator_select_fragment_title);
        topText1.setText(R.string.ui_group_activity_editor_participator_select_1);
        topText2.setText(R.string.ui_group_activity_editor_participator_select_2);
    }

    @Override
    protected void initializeFragments() {

    }

    @Override
    protected void viewPagerSelectionChanged(int position) {

    }

    @Override
    protected boolean shouldSetDefaultTitleEvents() {
        return true;
    }

    @Click({R.id.ui_group_concern_main_top_channel_1,
            R.id.ui_group_concern_main_top_channel_2})
    private void click(View view) {

    }
}
