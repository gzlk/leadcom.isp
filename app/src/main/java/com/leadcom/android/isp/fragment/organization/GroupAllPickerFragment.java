package com.leadcom.android.isp.fragment.organization;

import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.TextView;

import com.hlk.hlklib.lib.inject.Click;
import com.hlk.hlklib.lib.inject.ViewId;
import com.leadcom.android.isp.R;
import com.leadcom.android.isp.fragment.base.BaseFragment;
import com.leadcom.android.isp.fragment.base.BaseViewPagerSupportFragment;
import com.leadcom.android.isp.listener.OnTitleButtonClickListener;
import com.leadcom.android.isp.model.organization.RelateGroup;
import com.leadcom.android.isp.model.organization.SubMember;

import java.util.ArrayList;

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

    private static final String PARAM_MEMBERS = "gpf_param_selected_members";

    public static GroupAllPickerFragment newInstance(Bundle bundle) {
        GroupAllPickerFragment gapf = new GroupAllPickerFragment();
        gapf.setArguments(bundle);
        return gapf;
    }

    public static void open(BaseFragment fragment, String groupId, String groupName, ArrayList<String> groups, ArrayList<SubMember> members) {
        Bundle bundle = new Bundle();
        bundle.putString(PARAM_QUERY_ID, groupId);
        bundle.putString(BaseOrganizationFragment.PARAM_NAME, groupName);
        bundle.putStringArrayList(PARAM_JSON, groups);
        bundle.putSerializable(PARAM_MEMBERS, members);
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
    private ArrayList<String> selectedGroups;
    private ArrayList<SubMember> selectedMembers;

    @Override
    public int getLayout() {
        return R.layout.fragment_group_concern_main;
    }

    @Override
    protected void getParamsFromBundle(Bundle bundle) {
        super.getParamsFromBundle(bundle);
        selectedGroups = bundle.getStringArrayList(PARAM_JSON);
        if (null == selectedGroups) {
            selectedGroups = new ArrayList<>();
        }
        selectedMembers = (ArrayList<SubMember>) bundle.getSerializable(PARAM_MEMBERS);
        if (null == selectedMembers) {
            selectedMembers = new ArrayList<>();
        }
    }

    @Override
    protected void saveParamsToBundle(Bundle bundle) {
        super.saveParamsToBundle(bundle);
        bundle.putStringArrayList(PARAM_JSON, selectedGroups);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        leftView.setVisibility(View.GONE);
        setCustomTitle(R.string.ui_group_activity_editor_participator_select_fragment_title);
        topText1.setText(R.string.ui_group_activity_editor_participator_select_1);
        topText2.setText(R.string.ui_group_activity_editor_participator_select_2);
        topText1.setTextSize(TypedValue.COMPLEX_UNIT_PX, getDimension(R.dimen.ui_base_text_size_small));
        topText2.setTextSize(TypedValue.COMPLEX_UNIT_PX, getDimension(R.dimen.ui_base_text_size_small));
        setRightText(R.string.ui_base_text_complete);
        setRightTitleClickListener(new OnTitleButtonClickListener() {
            @Override
            public void onClick() {
                GroupsFragment groups = (GroupsFragment) mFragments.get(0);
                ArrayList<SubMember> members = groups.getSelectedItems();
                SquadsFragment squads = (SquadsFragment) mFragments.get(1);
                members.addAll(squads.getSelectedItems());
                resultData(SubMember.toJson(members));
            }
        });
    }

    @Override
    protected void initializeFragments() {
        if (mFragments.size() <= 0) {
            // 下级组织
            Bundle bundle = GroupsFragment.getBundle(mQueryId, "", RelateGroup.RelationType.SUBORDINATE, true, selectedGroups);
            GroupsFragment groups = GroupsFragment.newInstance(bundle);
            mFragments.add(groups);
            // 本组织支部以及成员
            bundle = SquadsFragment.getBundle(mQueryId, true, selectedMembers);
            SquadsFragment squads = SquadsFragment.newInstance(bundle);
            mFragments.add(squads);
        }
    }

    @Override
    protected void viewPagerSelectionChanged(int position) {
        int color1 = getColor(R.color.textColor);
        int color2 = getColor(R.color.textColorHint);
        topLine1.setVisibility(position == 0 ? View.VISIBLE : View.INVISIBLE);
        topText1.setTextColor(position == 0 ? color1 : color2);

        topLine2.setVisibility(position == 1 ? View.VISIBLE : View.INVISIBLE);
        topText2.setTextColor(position == 1 ? color1 : color2);
    }

    @Override
    protected boolean shouldSetDefaultTitleEvents() {
        return true;
    }

    @Click({R.id.ui_group_concern_main_top_channel_1,
            R.id.ui_group_concern_main_top_channel_2})
    private void click(View view) {
        switch (view.getId()) {
            case R.id.ui_group_concern_main_top_channel_1:
                setDisplayPage(0);
                break;
            case R.id.ui_group_concern_main_top_channel_2:
                setDisplayPage(1);
                break;
        }
    }
}
