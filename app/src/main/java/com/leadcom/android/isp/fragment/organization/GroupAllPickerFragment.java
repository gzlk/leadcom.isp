package com.leadcom.android.isp.fragment.organization;

import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
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
    public static boolean IS_FOR_DELIVER = false;

    public static GroupAllPickerFragment newInstance(Bundle bundle) {
        GroupAllPickerFragment gapf = new GroupAllPickerFragment();
        gapf.setArguments(bundle);
        return gapf;
    }

    public static void open(BaseFragment fragment, String groupId, String groupName, ArrayList<String> groups, ArrayList<SubMember> members) {
        Bundle bundle = new Bundle();
        bundle.putString(PARAM_QUERY_ID, groupId);
        bundle.putString(GroupBaseFragment.PARAM_NAME, groupName);
        bundle.putStringArrayList(PARAM_JSON, groups);
        bundle.putSerializable(PARAM_MEMBERS, members);
        fragment.openActivity(GroupAllPickerFragment.class.getName(), bundle, REQUEST_SELECT, true, false);
    }

    @ViewId(R.id.ui_group_concern_main_top_channel_1)
    private TextView topText1;
    @ViewId(R.id.ui_group_concern_main_top_channel_2)
    private TextView topText2;
    @ViewId(R.id.ui_group_all_picker_group_squad)
    private TextView squadMember;
    @ViewId(R.id.ui_group_all_picker_group_member)
    private TextView groupMember;
    @ViewId(R.id.ui_group_concern_main_top_channel_1_line)
    private View topLine1;
    @ViewId(R.id.ui_group_concern_main_top_channel_2_line)
    private View topLine2;
    @ViewId(R.id.ui_group_all_picker_group_squad_layout)
    private View squadClickView;
    @ViewId(R.id.ui_group_all_picker_group_squad_line)
    private View squadLine;
    @ViewId(R.id.ui_group_all_picker_group_member_layout)
    private View memberClickView;
    @ViewId(R.id.ui_group_all_picker_group_member_line)
    private View memberLine;
    private ArrayList<String> selectedGroups;
    private ArrayList<SubMember> selectedMembers;
    private String mGroupName;

    @Override
    public int getLayout() {
        return R.layout.fragment_group_all_picker;
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
        mGroupName = bundle.getString(GroupBaseFragment.PARAM_NAME, "");
    }

    @Override
    protected void saveParamsToBundle(Bundle bundle) {
        super.saveParamsToBundle(bundle);
        bundle.putStringArrayList(PARAM_JSON, selectedGroups);
        bundle.putSerializable(PARAM_MEMBERS, selectedMembers);
        bundle.putString(GroupBaseFragment.PARAM_NAME, mGroupName);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setCustomTitle(mGroupName);
        topText1.setText(IS_FOR_DELIVER ? R.string.ui_group_member_fragment_title : R.string.ui_group_activity_editor_participator_select_1);
        topText2.setText(IS_FOR_DELIVER ? R.string.ui_group_squad_member_fragment_title : R.string.ui_group_activity_editor_participator_select_2);
        squadClickView.setVisibility(IS_FOR_DELIVER ? View.GONE : View.VISIBLE);
        memberClickView.setVisibility(IS_FOR_DELIVER ? View.GONE : View.VISIBLE);

        if (IS_FOR_DELIVER) {
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) topLine2.getLayoutParams();
            params.addRule(RelativeLayout.ALIGN_END, R.id.ui_group_concern_main_top_channel_2);
            topLine2.setLayoutParams(params);
        }
//        topText1.setTextSize(TypedValue.COMPLEX_UNIT_PX, getDimension(R.dimen.ui_base_text_size_small));
//        topText2.setTextSize(TypedValue.COMPLEX_UNIT_PX, getDimension(R.dimen.ui_base_text_size_small));
        setRightText(R.string.ui_base_text_complete);
        setRightTitleClickListener(new OnTitleButtonClickListener() {
            @Override
            public void onClick() {

                SquadsFragment squads = (SquadsFragment) mFragments.get(0);
                ArrayList<SubMember> members = squads.getSelectedItems();

                GroupContactPickFragment contact = (GroupContactPickFragment) mFragments.get(1);
                ArrayList<SubMember> contacts = contact.getSelectedItems();
                for (SubMember member : contacts) {
                    if (!member.isUserExistedIn(members)) {
                        members.add(member);
                    }
                }
                //members.addAll(contact.getSelectedItems());

                if (!IS_FOR_DELIVER) {
                    GroupsFragment groups = (GroupsFragment) mFragments.get(2);
                    members.addAll(groups.getSelectedItems());
                }

                resultData(SubMember.toJson(members));
            }
        });
    }

    @Override
    public void onDestroy() {
        IS_FOR_DELIVER = false;
        super.onDestroy();
    }

    @Override
    protected void initializeFragments() {
        if (mFragments.size() <= 0) {
            // 本组织支部以及成员
            Bundle bundle = SquadsFragment.getBundle(mQueryId, mGroupName, true, selectedMembers);
            SquadsFragment squads = SquadsFragment.newInstance(bundle);
            mFragments.add(squads);
            // 本组织成员
            bundle = GroupContactPickFragment.getBundle(mQueryId, false, false, false, "[]");
            mFragments.add(GroupContactPickFragment.newInstance(bundle));
            if (!IS_FOR_DELIVER) {
                // 下级组织
                bundle = GroupsFragment.getBundle(mQueryId, "", RelateGroup.RelationType.SUBORDINATE, true, selectedGroups);
                GroupsFragment groups = GroupsFragment.newInstance(bundle);
                mFragments.add(groups);
            }
        }
    }

    @Override
    protected void viewPagerSelectionChanged(int position) {
        int color1 = getColor(R.color.textColor);
        int color2 = getColor(R.color.textColorHint);
        int color3 = getColor(R.color.colorPrimary);
        int color4 = getColor(R.color.textColorHintLightLight);

        squadMember.setTextColor(position == 0 ? color1 : color2);
        groupMember.setTextColor(position == 1 ? color1 : color2);

        if (!IS_FOR_DELIVER) {
            squadLine.setVisibility(position == 0 ? View.VISIBLE : View.INVISIBLE);
            memberLine.setVisibility(position == 1 ? View.VISIBLE : View.INVISIBLE);
        }

        //topLine1.setVisibility(position == 2 ? View.VISIBLE : View.INVISIBLE);
        if (IS_FOR_DELIVER) {
            topLine1.setBackgroundColor(position == 1 ? color3 : color4);
            topText1.setTextColor(position == 1 ? color1 : color2);

            topLine2.setBackgroundColor(position == 0 ? color3 : color4);
            topText2.setTextColor(position == 0 ? color1 : color2);
        } else {
            topLine1.setBackgroundColor(position == 2 ? color3 : color4);
            topText1.setTextColor(position == 2 ? color1 : color2);

            topLine2.setBackgroundColor(position <= 1 ? color3 : color4);
            topText2.setTextColor(position <= 1 ? color1 : color2);
        }

        //topLine2.setVisibility(position <= 1 ? View.VISIBLE : View.INVISIBLE);
    }

    @Override
    protected boolean shouldSetDefaultTitleEvents() {
        return true;
    }

    @Click({R.id.ui_group_concern_main_top_channel_1,
            R.id.ui_group_concern_main_top_channel_2,
            R.id.ui_group_all_picker_group_squad_layout,
            R.id.ui_group_all_picker_group_member_layout})
    private void click(View view) {
        switch (view.getId()) {
            case R.id.ui_group_concern_main_top_channel_1:
                if (IS_FOR_DELIVER) {
                    setDisplayPage(1);
                } else {
                    setDisplayPage(2);
                }
                break;
            case R.id.ui_group_concern_main_top_channel_2:
                if (IS_FOR_DELIVER) {
                    setDisplayPage(0);
                }
                break;
            case R.id.ui_group_all_picker_group_squad_layout:
                setDisplayPage(0);
                break;
            case R.id.ui_group_all_picker_group_member_layout:
                setDisplayPage(1);
                break;
        }
    }
}
