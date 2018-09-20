package com.leadcom.android.isp.fragment.organization;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.hlk.hlklib.lib.inject.Click;
import com.hlk.hlklib.lib.inject.ViewId;
import com.leadcom.android.isp.R;
import com.leadcom.android.isp.api.org.ConcernRequest;
import com.leadcom.android.isp.cache.Cache;
import com.leadcom.android.isp.fragment.base.BaseFragment;
import com.leadcom.android.isp.fragment.base.BaseViewPagerSupportFragment;
import com.leadcom.android.isp.statusbar.StatusBarUtils;


/**
 * <b>功能描述：</b>组织的关注、被关注列表<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2018/09/11 12:39 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2018/09/11 12:39  <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */
public class GroupConcernedMainFragment extends BaseViewPagerSupportFragment {

    private static final String PARAM_GROUP_NAME = "cof_group_name";

    public static GroupConcernedMainFragment newInstance(Bundle bundle) {
        GroupConcernedMainFragment gcmf = new GroupConcernedMainFragment();
        gcmf.setArguments(bundle);
        return gcmf;
    }

    public static void open(BaseFragment fragment, String groupId, String groupName) {
        Bundle bundle = new Bundle();
        bundle.putString(PARAM_QUERY_ID, groupId);
        bundle.putString(PARAM_GROUP_NAME, groupName);
        fragment.openActivity(GroupConcernedMainFragment.class.getName(), bundle, false, false, true);
    }

    @ViewId(R.id.ui_main_tool_bar_container)
    private View toolBar;
    @ViewId(R.id.ui_group_concern_main_top_channel_1)
    private TextView topText1;
    @ViewId(R.id.ui_group_concern_main_top_channel_2)
    private TextView topText2;
    @ViewId(R.id.ui_group_concern_main_top_channel_1_line)
    private View topLine1;
    @ViewId(R.id.ui_group_concern_main_top_channel_2_line)
    private View topLine2;

    private String mGroupName;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        //tryPaddingContent(toolBar, false);
        int color = getColor(Cache.sdk >= 23 ? R.color.colorPrimary : R.color.textColorLight);
        topLine1.setBackgroundColor(color);
        topLine2.setBackgroundColor(color);
        StatusBarUtils.setStatusBarLightMode(Activity().getWindow());
    }

    @Override
    protected void getParamsFromBundle(Bundle bundle) {
        super.getParamsFromBundle(bundle);
        mGroupName = bundle.getString(PARAM_GROUP_NAME, "");
    }

    @Override
    protected void saveParamsToBundle(Bundle bundle) {
        super.saveParamsToBundle(bundle);
        bundle.putString(PARAM_GROUP_NAME, mGroupName);
    }

    @Override
    protected void initializeFragments() {
        if (mFragments.size() < 1) {
            mFragments.add(GroupConcernedFragment.newInstance(GroupConcernedFragment.getBundle(mQueryId, mGroupName, ConcernRequest.CONCERN_TO)));
            mFragments.add(GroupConcernedFragment.newInstance(GroupConcernedFragment.getBundle(mQueryId, mGroupName, ConcernRequest.CONCERN_FROM)));
        }
    }

    @Override
    protected void viewPagerSelectionChanged(int position) {
        int color1 = getColor(Cache.sdk >= 23 ? R.color.textColor : R.color.textColorLight);
        int color2 = getColor(Cache.sdk >= 23 ? R.color.textColorHint : R.color.textColorLight);
        topLine1.setVisibility(position == 0 ? View.VISIBLE : View.INVISIBLE);
        topText1.setTextColor(position == 0 ? color1 : color2);

        topLine2.setVisibility(position == 1 ? View.VISIBLE : View.INVISIBLE);
        topText2.setTextColor(position == 1 ? color1 : color2);
    }

    @Override
    public int getLayout() {
        return R.layout.fragment_group_concern_main;
    }

    @Override
    protected boolean shouldSetDefaultTitleEvents() {
        return false;
    }

    @Click({R.id.ui_ui_custom_title_left_container,
            R.id.ui_group_concern_main_top_channel_1,
            R.id.ui_group_concern_main_top_channel_2})
    void click(View view) {
        switch (view.getId()) {
            case R.id.ui_ui_custom_title_left_container:
                finish();
                break;
            case R.id.ui_group_concern_main_top_channel_1:
                setDisplayPage(0);
                break;
            case R.id.ui_group_concern_main_top_channel_2:
                setDisplayPage(1);
                break;
        }
    }
}
