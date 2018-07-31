package com.leadcom.android.isp.fragment.organization;

import android.os.Bundle;
import android.util.TypedValue;
import android.view.View;
import android.widget.LinearLayout;

import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.view.CustomTextView;
import com.leadcom.android.isp.R;
import com.leadcom.android.isp.api.listener.OnSingleRequestListener;
import com.leadcom.android.isp.api.org.NatureRequest;
import com.leadcom.android.isp.fragment.base.BaseFragment;
import com.leadcom.android.isp.fragment.base.BaseViewPagerSupportFragment;
import com.leadcom.android.isp.helper.StringHelper;
import com.leadcom.android.isp.helper.ToastHelper;
import com.leadcom.android.isp.listener.OnTitleButtonClickListener;
import com.leadcom.android.isp.model.organization.MemberClassify;
import com.leadcom.android.isp.model.organization.MemberNature;
import com.leadcom.android.isp.model.organization.SimpleNature;

import java.util.ArrayList;


/**
 * <b>功能描述：</b>组织成员统计信息主页<br />
 * <b>创建作者：</b>SYSTEM <br />
 * <b>创建时间：</b>2018/07/29 23:39 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>version: 1.0.0 <br />
 * <b>修改时间：</b>2017/10/04 18:50 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */
public class MemberNatureMainFragment extends BaseViewPagerSupportFragment {

    private static final String PARAM_CHOOSE = "mnmf_choose";
    private static final String PARAM_USER_ID = "mnf_user_id";
    private static final String PARAM_GROUP_NAME = "mnf_group_name";

    public static MemberNatureMainFragment newInstance(Bundle bundle) {
        MemberNatureMainFragment mncf = new MemberNatureMainFragment();
        mncf.setArguments(bundle);
        return mncf;
    }

    public static void open(BaseFragment fragment, String groupId, String groupName, boolean choose, String userId) {
        Bundle bundle = new Bundle();
        bundle.putString(PARAM_QUERY_ID, groupId);
        bundle.putBoolean(PARAM_CHOOSE, choose);
        bundle.putString(PARAM_USER_ID, userId);
        bundle.putString(PARAM_GROUP_NAME, groupName);
        fragment.openActivity(MemberNatureMainFragment.class.getName(), bundle, true, false);
    }

    @ViewId(R.id.ui_fragment_group_member_nature_count_indicator)
    private LinearLayout indicator;
    private ArrayList<CustomTextView> dots = new ArrayList<>();
    private boolean forChoose;
    private String mUserId, mGroupName;

    @Override
    protected void getParamsFromBundle(Bundle bundle) {
        super.getParamsFromBundle(bundle);
        forChoose = bundle.getBoolean(PARAM_CHOOSE, false);
        mUserId = bundle.getString(PARAM_USER_ID, "");
        mGroupName = bundle.getString(PARAM_GROUP_NAME, "");
    }

    @Override
    protected void saveParamsToBundle(Bundle bundle) {
        super.saveParamsToBundle(bundle);
        bundle.putBoolean(PARAM_CHOOSE, forChoose);
        bundle.putString(PARAM_USER_ID, mUserId);
        bundle.putString(PARAM_GROUP_NAME, mGroupName);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (forChoose) {
            setCustomTitle(R.string.ui_group_member_nature_count_fragment_title1);
        } else {
            String title = isEmpty(mGroupName) ? "" : format("(%s)", mGroupName);
            setCustomTitle(StringHelper.getString(R.string.ui_group_member_nature_count_fragment_title, title));
        }
        if (forChoose) {
            setRightText(R.string.ui_base_text_complete);
            setRightTitleClickListener(new OnTitleButtonClickListener() {
                @Override
                public void onClick() {
                    ArrayList<SimpleNature> natures = new ArrayList<>();
                    for (BaseFragment fragment : mFragments) {
                        natures.addAll(((MemberNatureFragment) fragment).updateNatures());
                    }
                    updateUserNature(natures);
                }
            });
        }
        indicator.removeAllViews();
        dots.clear();
        CustomTextView ctv = new CustomTextView(indicator.getContext());
        ctv.setTextSize(TypedValue.COMPLEX_UNIT_PX, getDimension(R.dimen.ui_base_text_size_little));
        ctv.setText(R.string.ui_icon_radio_unselected);
        indicator.addView(ctv);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) ctv.getLayoutParams();
        params.rightMargin = getDimension(R.dimen.ui_static_dp_5);
        dots.add(ctv);

        ctv = new CustomTextView(indicator.getContext());
        ctv.setTextSize(TypedValue.COMPLEX_UNIT_PX, getDimension(R.dimen.ui_base_text_size_little));
        ctv.setText(R.string.ui_icon_radio_unselected);
        indicator.addView(ctv);
        params = (LinearLayout.LayoutParams) ctv.getLayoutParams();
        params.rightMargin = getDimension(R.dimen.ui_static_dp_5);
        dots.add(ctv);
    }

    private void updateUserNature(ArrayList<SimpleNature> natures) {
        if (natures.size() > 0) {
            NatureRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<MemberClassify>() {
                @Override
                public void onResponse(MemberClassify memberClassify, boolean success, String message) {
                    super.onResponse(memberClassify, success, message);
                    if (success) {
                        ToastHelper.make().showMsg(R.string.ui_group_member_nature_more_updated);
                        for (BaseFragment fragment : mFragments) {
                            ((MemberNatureFragment) fragment).onSwipeRefreshing();
                        }
                    }
                }
            }).updateUserNatures(mQueryId, mUserId, natures);
        } else {
            ToastHelper.make().showMsg(R.string.ui_group_member_nature_more_nothing_to_update);
        }
    }

    @Override
    protected void initializeFragments() {
        mFragments.clear();
        mFragments.add(MemberNatureFragment.getInstance(mQueryId, MemberNature.NatureType.TEXT, forChoose, mUserId));
        mFragments.add(MemberNatureFragment.getInstance(mQueryId, MemberNature.NatureType.TIME, forChoose, mUserId));
    }

    @Override
    protected void viewPagerSelectionChanged(int position) {
        for (int i = 0, len = dots.size(); i < len; i++) {
            CustomTextView ctv = dots.get(i);
            ctv.setText(i == position ? R.string.ui_icon_radio_disabled : R.string.ui_icon_radio_unselected);
            ctv.setTextColor(getColor(i == position ? R.color.colorPrimary : R.color.textColorHint));
        }
    }

    @Override
    public int getLayout() {
        return R.layout.fragment_group_member_nature_count;
    }

    @Override
    protected boolean shouldSetDefaultTitleEvents() {
        return true;
    }
}
