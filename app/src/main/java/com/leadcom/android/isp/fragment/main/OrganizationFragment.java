package com.leadcom.android.isp.fragment.main;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.leadcom.android.isp.R;
import com.leadcom.android.isp.cache.Cache;
import com.leadcom.android.isp.fragment.base.BaseViewPagerSupportFragment;
import com.leadcom.android.isp.fragment.organization.ArchiveNominateFragment;
import com.leadcom.android.isp.fragment.organization.ArchivesFragment;
import com.leadcom.android.isp.fragment.organization.ContactFragment;
import com.leadcom.android.isp.fragment.organization.CreateOrganizationFragment;
import com.leadcom.android.isp.fragment.organization.OnOrganizationChangedListener;
import com.leadcom.android.isp.fragment.organization.StructureFragment;
import com.leadcom.android.isp.helper.popup.DialogHelper;
import com.leadcom.android.isp.helper.PreferenceHelper;
import com.leadcom.android.isp.helper.popup.SimpleDialogHelper;
import com.leadcom.android.isp.model.organization.Member;
import com.leadcom.android.isp.model.organization.Organization;
import com.hlk.hlklib.lib.inject.Click;
import com.hlk.hlklib.lib.inject.ViewId;

/**
 * <b>功能描述：</b>主页 - 组织<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/04/20 10:49 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/04/20 10:49 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class OrganizationFragment extends BaseViewPagerSupportFragment {

    private static final String PARAM_SELECTED_ORG = "selected_org_id";

    @Override
    protected void getParamsFromBundle(Bundle bundle) {
        super.getParamsFromBundle(bundle);
        selectedOrganizationId = bundle.getString(PARAM_SELECTED_ORG, "");
    }

    @Override
    protected void saveParamsToBundle(Bundle bundle) {
        super.saveParamsToBundle(bundle);
        bundle.putString(PARAM_SELECTED_ORG, selectedOrganizationId);
    }

    @ViewId(R.id.ui_organization_top_channel_layout)
    private View channelLayout;
    @ViewId(R.id.ui_tool_organization_top_channel_1)
    private TextView channel1;
    @ViewId(R.id.ui_tool_organization_top_channel_2)
    private TextView channel2;
    @ViewId(R.id.ui_tool_organization_top_channel_3)
    private TextView channel3;
    @ViewId(R.id.ui_tool_organization_top_channel_4)
    private TextView channel4;

    private String selectedOrganizationId;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        isRemovable = true;
        super.onCreate(savedInstanceState);
    }

    @Override
    public int getLayout() {
        return R.layout.fragment_main_organization;
    }

    @Override
    public void doingInResume() {
        tryPaddingContent(channelLayout, true);
        super.doingInResume();
    }

    @Override
    protected void initializeFragments() {
        if (mFragments.size() < 1) {
            mFragments.add(new StructureFragment());
            mFragments.add(ContactFragment.newInstance(ContactFragment.getBundle(ContactFragment.TYPE_ORG, "", "")));
            mFragments.add(new ArchivesFragment());
            //mFragments.add(new LivenessFragment());
            //((StructureFragment) mFragments.get(0)).mainFragment = mainFragment;
            ((StructureFragment) mFragments.get(0)).setOnOrganizationChangedListener(organizationChangedListener);
            // 拉取到组织的
            ((StructureFragment) mFragments.get(0)).organizationFragment = this;
        }
    }

    /**
     * 管理员时，增加档案推荐列表
     */
    public void addRecommendedArchives(boolean addable, String groupId) {
        boolean exists = false;
        int recommend = 0;
        for (int i = 0, len = mFragments.size(); i < len; i++) {
            if (mFragments.get(i) instanceof ArchiveNominateFragment) {
                exists = true;
                recommend = i;
            }
        }
        if (!addable && exists) {
            // 删除这个fragment
            removeFragment(recommend);
            channel4.setVisibility(View.GONE);
        }
        if (addable) {
            if (!exists) {
                channel4.setVisibility(View.VISIBLE);
                addFragment(ArchiveNominateFragment.newInstance(groupId));
            }
//            else {
            //((ArchiveNominateFragment) mFragments.get(3)).setNewQueryId(groupId);
//            }
        }
    }

    private OnOrganizationChangedListener organizationChangedListener = new OnOrganizationChangedListener() {
        @Override
        public void onChanged(Organization item) {
            if (null == item) return;
            selectedOrganizationId = item.getId();
            if (getUserVisibleHint()) {
                // 如果当前显示的是组织页面才更改标题栏文字，否则不需要
                //mainFragment.setTitleText(item.getName());
            }
            ((ContactFragment) mFragments.get(1)).setNewQueryId(item.getId());
            String creatorId = item.getCreatorId();
            ((ContactFragment) mFragments.get(1)).setIsCreator(!isEmpty(creatorId) && creatorId.equals(Cache.cache().userId));
            //((ArchivesFragment) mFragments.get(2)).setNewQueryId(item.getId());
//            if (mFragments.size() > 3) {
//                ((ArchiveNominateFragment) mFragments.get(3)).setNewQueryId(item.getId());
//            }
        }
    };

    @Override
    protected void onViewPagerDisplayedChanged(boolean visible) {
        super.onViewPagerDisplayedChanged(visible);
        if (visible) {
            // 当前显示的是组织则判断能否显示右上角的 + 号
            resetRightIcon();
        }
    }

    public void needChangeTitle() {
        ((StructureFragment) mFragments.get(0)).changeSelectedGroup();
    }

    @Override
    protected void viewPagerSelectionChanged(int position) {
        int color1 = getColor(R.color.textColorHintDark);
        int color2 = getColor(R.color.colorPrimary);

        channel1.setTextColor(position == 0 ? color2 : color1);
        channel2.setTextColor(position == 1 ? color2 : color1);
        channel3.setTextColor(position == 2 ? color2 : color1);
        channel4.setTextColor(position == 3 ? color2 : color1);
        for (int i = 0; i < mFragments.size(); i++) {
            mFragments.get(i).setViewPagerDisplayedCurrent(i == position);
        }
        if (getUserVisibleHint()) {
            resetRightIcon();
        }
    }

    private void resetRightIcon() {
        // 如果当前显示的是组织页面才控制右上角的 + 显示与否
        int position = getDisplayedPage();
        boolean shown;
        //if (!BuildConfig.RELEASEABLE) {
        // 测试状态下可以添加组织
        //    shown = position <= 2;
        //} else {
        // 新增：只有当前登录用户在这个组织内可以添加成员时才显示 + 号
        shown = (position == 1 && canAddMember()) || (position == 2);
        //}
        //mainFragment.showRightIcon(shown);
    }

    private boolean canAddMember() {
        Member member = StructureFragment.my;
        return null != member && member.memberInvitable();
    }

    @Click({R.id.ui_tool_organization_top_channel_1, R.id.ui_tool_organization_top_channel_2,
            R.id.ui_tool_organization_top_channel_3, R.id.ui_tool_organization_top_channel_4})
    private void elementClick(View view) {
        switch (view.getId()) {
            case R.id.ui_tool_organization_top_channel_1:
                setDisplayPage(0);
                break;
            case R.id.ui_tool_organization_top_channel_2:
                setDisplayPage(1);
                break;
            case R.id.ui_tool_organization_top_channel_3:
                setDisplayPage(2);
                break;
            case R.id.ui_tool_organization_top_channel_4:
                setDisplayPage(3);
                break;
        }
    }

    public void rightIconClick(View view) {
        if (getDisplayedPage() == 0) {
            // 新建组织
            if (!Cache.isReleasable()) {
                String string = PreferenceHelper.get(R.string.pf_static_temp_organization_create_alert, "");
                if (isEmpty(string)) {
                    // 提醒测试人员这个功能目前只是为了测试方便而设置，release版本会取消
                    warningCreateOrganization();
                } else {
                    toCreateOrganization();
                }
            }
        } else if (getDisplayedPage() == 1) {
            // 打开手机通讯录加人到组织
            ((ContactFragment) mFragments.get(1)).addMemberToOrganizationFromPhoneContact(view);
        } else if (getDisplayedPage() == 2) {
            // 打开弹出菜单新建或管理组织档案
            ((ArchivesFragment) mFragments.get(2)).openTooltipMenu(view);
        }
    }

    private void warningCreateOrganization() {
        SimpleDialogHelper.init(Activity()).show(R.string.ui_organization_creator_warning, new DialogHelper.OnDialogConfirmListener() {
            @Override
            public boolean onConfirm() {
                PreferenceHelper.save(R.string.pf_static_temp_organization_create_alert, "YES");
                toCreateOrganization();
                return true;
            }
        });
    }

    private void toCreateOrganization() {
        CreateOrganizationFragment.open(this);
    }

    @Override
    protected boolean shouldSetDefaultTitleEvents() {
        return false;
    }

    @Override
    protected void destroyView() {

    }
}
