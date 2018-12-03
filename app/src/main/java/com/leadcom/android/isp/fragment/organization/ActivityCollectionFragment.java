package com.leadcom.android.isp.fragment.organization;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.hlk.hlklib.lib.inject.Click;
import com.hlk.hlklib.lib.inject.ViewId;
import com.leadcom.android.isp.R;
import com.leadcom.android.isp.api.archive.ArchiveRequest;
import com.leadcom.android.isp.api.listener.OnSingleRequestListener;
import com.leadcom.android.isp.cache.Cache;
import com.leadcom.android.isp.fragment.base.BaseFragment;
import com.leadcom.android.isp.fragment.base.BaseViewPagerSupportFragment;
import com.leadcom.android.isp.helper.ToastHelper;
import com.leadcom.android.isp.listener.OnTitleButtonClickListener;
import com.leadcom.android.isp.model.archive.Archive;
import com.leadcom.android.isp.model.operation.GRPOperation;
import com.leadcom.android.isp.model.organization.Member;
import com.leadcom.android.isp.model.organization.Role;

/**
 * <b>功能描述：</b>组织活动成员报名统计页面<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2018/09/21 15:57 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2018/09/21 15:57  <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */
public class ActivityCollectionFragment extends BaseViewPagerSupportFragment {

    public static ActivityCollectionFragment newInstance(Bundle bundle) {
        ActivityCollectionFragment acf = new ActivityCollectionFragment();
        acf.setArguments(bundle);
        return acf;
    }

    public static void open(BaseFragment fragment, Archive archive) {
        if (null == archive || !archive.isActivity()) {
            throw new IllegalArgumentException("cannot open page with none activity flag.");
        }
        Bundle bundle = new Bundle();
        bundle.putString(PARAM_QUERY_ID, archive.getId());
        bundle.putSerializable(PARAM_JSON, archive);
        fragment.openActivity(ActivityCollectionFragment.class.getName(), bundle, true, false);
    }

    @ViewId(R.id.ui_group_concern_main_top_channel_1)
    private TextView topText1;
    @ViewId(R.id.ui_group_concern_main_top_channel_2)
    private TextView topText2;
    @ViewId(R.id.ui_group_concern_main_top_channel_1_line)
    private View topLine1;
    @ViewId(R.id.ui_group_concern_main_top_channel_2_line)
    private View topLine2;
    private Archive mArchive;

    @Override
    protected void getParamsFromBundle(Bundle bundle) {
        super.getParamsFromBundle(bundle);
        mArchive = (Archive) bundle.getSerializable(PARAM_JSON);
    }

    @Override
    protected void saveParamsToBundle(Bundle bundle) {
        super.saveParamsToBundle(bundle);
        bundle.putSerializable(PARAM_JSON, mArchive);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (null == mArchive || !mArchive.isActivity()) {
            ToastHelper.make().showMsg(R.string.ui_group_activity_collection_not_activity);
            finish();
        } else {
            setCustomTitle(R.string.ui_group_activity_collection_fragment_title);
            if (Role.hasOperation(mArchive.getGroupId(), GRPOperation.ACTIVITY_REPLY) && !mArchive.getGroupId().equals(mArchive.getFromGroupId())) {
                setRightText(R.string.ui_base_text_reply);
                setRightTitleClickListener(new OnTitleButtonClickListener() {
                    @Override
                    public void onClick() {
                        // 查看活动详情
                        fetchingActivityDetails();
                    }
                });
            }
        }
    }

    @Override
    public int getLayout() {
        return R.layout.fragment_group_activity_member_count;
    }

    @Override
    protected void initializeFragments() {
        if (mFragments.size() <= 0) {
            //Bundle bundle = ActivityCollectionDetailsFragment.getBundle(mArchive.getGroupId(), mArchive.getGroActivityId(), Member.Type.GROUP);
            //mFragments.add(ActivityCollectionDetailsFragment.newInstance(bundle));
            Bundle bundle = ActivityCollectionDetailsFragment.getBundle(mArchive.getGroupId(), mArchive.getGroActivityId(), Member.Type.ACTIVITY);
            mFragments.add(ActivityCollectionDetailsFragment.newInstance(bundle));
        }
    }

    @Override
    protected void viewPagerSelectionChanged(int position) {
        int color1 = getColor(Cache.sdk >= 23 ? R.color.textColor : R.color.textColorLight);
        int color2 = getColor(Cache.sdk >= 23 ? R.color.textColorHint : R.color.transparent_40_white);
        int color3 = getColor(Cache.sdk >= 23 ? R.color.colorPrimary : R.color.textColorLight);

        topLine1.setBackgroundColor(color3);
        topLine2.setBackgroundColor(color3);

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

    private void fetchingActivityDetails() {
        ArchiveRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<Archive>() {
            @Override
            public void onResponse(Archive archive, boolean success, String message) {
                super.onResponse(archive, success, message);
                if (success && null != archive) {
                    mArchive.setState(archive.getState());
                    mArchive.setHappenDate(archive.getHappenDate());
                }
                // 回复上级组织本组织成员的活动报名参与情况
                ActivityReplyFragment.open(ActivityCollectionFragment.this, mArchive);
            }
        }).findActivity(mArchive.getGroActivityId());
    }
}
