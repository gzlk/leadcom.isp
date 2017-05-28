package com.gzlk.android.isp.fragment.activity;

import android.os.Bundle;
import android.view.View;

import com.gzlk.android.isp.R;
import com.gzlk.android.isp.api.activity.ActRequest;
import com.gzlk.android.isp.api.listener.OnSingleRequestListener;
import com.gzlk.android.isp.fragment.base.BaseSwipeRefreshSupportFragment;
import com.gzlk.android.isp.helper.StringHelper;
import com.gzlk.android.isp.holder.SimpleClickableViewHolder;
import com.gzlk.android.isp.holder.SimpleInputableViewHolder;
import com.gzlk.android.isp.listener.OnTitleButtonClickListener;
import com.gzlk.android.isp.listener.OnViewHolderClickListener;
import com.gzlk.android.isp.model.activity.Activity;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.view.ClearEditText;

/**
 * <b>功能描述：</b>新增活动<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/05/27 10:00 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/05/27 10:00 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class CreateActivityFragment extends BaseSwipeRefreshSupportFragment {

    private static final String PARAM_GROUP = "caf_group_id_params";

    public static CreateActivityFragment newInstance(String params) {
        CreateActivityFragment caf = new CreateActivityFragment();
        String[] strings = splitParameters(params);
        Bundle bundle = new Bundle();
        bundle.putString(PARAM_QUERY_ID, strings[0]);
        bundle.putString(PARAM_GROUP, strings[1]);
        caf.setArguments(bundle);
        return caf;
    }

    @Override
    protected void getParamsFromBundle(Bundle bundle) {
        super.getParamsFromBundle(bundle);
        mGroupId = bundle.getString(PARAM_GROUP, "");
    }

    @Override
    protected void saveParamsToBundle(Bundle bundle) {
        super.saveParamsToBundle(bundle);
        bundle.putString(PARAM_GROUP, mGroupId);
    }

    // view
    @ViewId(R.id.ui_activity_creator_cover)
    private View coverView;
    @ViewId(R.id.ui_activity_creator_title)
    private View titleView;
    @ViewId(R.id.ui_activity_creator_time)
    private View timeView;
    @ViewId(R.id.ui_activity_creator_address)
    private View addressView;
    @ViewId(R.id.ui_activity_creator_type)
    private View typeView;
    @ViewId(R.id.ui_activity_creator_privacy)
    private View privacyView;
    @ViewId(R.id.ui_activity_creator_member)
    private View memberView;
    @ViewId(R.id.ui_moment_new_text_content)
    private ClearEditText contentView;
    @ViewId(R.id.ui_tool_attachment_button)
    private View attachmentView;

    // holder
    private SimpleClickableViewHolder coverHolder;
    private SimpleInputableViewHolder titleHolder;
    private SimpleClickableViewHolder timeHolder;
    private SimpleInputableViewHolder addressHolder;
    private SimpleClickableViewHolder typeHolder;
    private SimpleClickableViewHolder privacyHolder;
    private SimpleClickableViewHolder memberHolder;

    /**
     * 活动所属的组织id
     */
    private String mGroupId = "";
    private String[] items;

    @Override
    protected void onDelayRefreshComplete(@DelayType int type) {

    }

    @Override
    public void doingInResume() {
        setCustomTitle(R.string.ui_activity_create_fragment_title);
        setRightText(R.string.ui_base_text_publish);
        setRightTitleClickListener(new OnTitleButtonClickListener() {
            @Override
            public void onClick() {
                tryPublishActivity();
            }
        });
        enableSwipe(!isEmpty(mQueryId));
        tryLoadActivity();
    }

    @Override
    protected boolean onBackKeyPressed() {
        return super.onBackKeyPressed();
    }

    @Override
    public int getLayout() {
        return R.layout.fragment_activity_creator;
    }

    @Override
    protected boolean shouldSetDefaultTitleEvents() {
        return false;
    }

    @Override
    protected void destroyView() {

    }

    @Override
    protected void onSwipeRefreshing() {
        fetchingActivity(true);
    }

    @Override
    protected void onLoadingMore() {

    }

    @Override
    protected String getLocalPageTag() {
        return null;
    }

    // 加载本地活动记录
    private void tryLoadActivity() {
        if (isEmpty(mQueryId)) {
            initializeHolder(null);
        } else {
            fetchingActivity(false);
        }
    }

    private void fetchingActivity(boolean fromRemote) {
        ActRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<Activity>() {
            @Override
            public void onResponse(Activity activity, boolean success, String message) {
                super.onResponse(activity, success, message);
                if (success) {
                    if (null != activity) {
                        initializeHolder(activity);
                    }
                }
                stopRefreshing();
            }
        }).find(mQueryId, fromRemote);
    }

    private void initializeHolder(Activity activity) {
        if (null == items) {
            items = StringHelper.getStringArray(R.array.ui_activity_create_items);
        }
        // cover
        if (null == coverHolder) {
            coverHolder = new SimpleClickableViewHolder(coverView, this);
        }
        String none = StringHelper.getString(R.string.ui_base_text_not_set);
        boolean non = null == activity;
        String value = format(items[0], non ? none : (isEmpty(activity.getImg()) ? none : activity.getImg()));
        coverHolder.showContent(value);

        // title
        if (null == titleHolder) {
            titleHolder = new SimpleInputableViewHolder(titleView, this);
        }
        value = format(items[1], non ? "" : activity.getTitle());
        titleHolder.showContent(value);

        // time
        if (null == timeHolder) {
            timeHolder = new SimpleClickableViewHolder(timeView, this);
        }
        value = format(items[2], non ? "" : activity.getCreateDate());
        timeHolder.showContent(value);

        // address
        if (null == addressHolder) {
            addressHolder = new SimpleInputableViewHolder(addressView, this);
        }
        value = format(items[3], "");
        addressHolder.showContent(value);

        // type
        if (null == typeHolder) {
            typeHolder = new SimpleClickableViewHolder(typeView, this);
        }
        value = format(items[4], "");
        typeHolder.showContent(value);

        // privacy
        if (null == privacyHolder) {
            privacyHolder = new SimpleClickableViewHolder(privacyView, this);
        }
        value = format(items[5], "未设置");
        privacyHolder.showContent(value);

        // member
        if (null == memberHolder) {
            memberHolder = new SimpleClickableViewHolder(memberView, this);
        }
        value = format(items[6], non ? 0 : (null == activity.getMemberNameArray() ? 0 : activity.getMemberNameArray().size()));
        memberHolder.showContent(value);
    }

    private OnViewHolderClickListener onViewHolderClickListener = new OnViewHolderClickListener() {
        @Override
        public void onClick(int index) {

        }
    };

    private void tryPublishActivity() {
    }
}
