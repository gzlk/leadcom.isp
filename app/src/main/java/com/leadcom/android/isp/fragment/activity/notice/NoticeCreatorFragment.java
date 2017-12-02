package com.leadcom.android.isp.fragment.activity.notice;

import android.os.Bundle;

import com.google.gson.reflect.TypeToken;
import com.leadcom.android.isp.R;
import com.leadcom.android.isp.api.activity.AppNoticeRequest;
import com.leadcom.android.isp.api.listener.OnSingleRequestListener;
import com.leadcom.android.isp.fragment.base.BaseTransparentSupportFragment;
import com.leadcom.android.isp.helper.ToastHelper;
import com.leadcom.android.isp.holder.common.SimpleInputableViewHolder;
import com.leadcom.android.isp.lib.Json;
import com.leadcom.android.isp.listener.OnTitleButtonClickListener;
import com.leadcom.android.isp.model.activity.Activity;
import com.leadcom.android.isp.model.activity.AppNotice;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.view.ClearEditText;

/**
 * <b>功能描述：</b>通知创建页面<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/06/15 21:17 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/06/15 21:17 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class NoticeCreatorFragment extends BaseTransparentSupportFragment {

    public static NoticeCreatorFragment newInstance(String params) {
        NoticeCreatorFragment ncf = new NoticeCreatorFragment();
        Bundle bundle = new Bundle();
        // 传过来的tid
        bundle.putString(PARAM_QUERY_ID, params);
        ncf.setArguments(bundle);
        return ncf;
    }

    private static final String PARAM_TITLE = "ncf_param_title";
    private static final String PARAM_ACTID = "ncf_param_act_id";

    private String mTitle = "";
    private String activityId = "";

    @Override
    protected void getParamsFromBundle(Bundle bundle) {
        super.getParamsFromBundle(bundle);
        mTitle = bundle.getString(PARAM_TITLE, "");
        activityId = bundle.getString(PARAM_ACTID, "");
    }

    @Override
    protected void saveParamsToBundle(Bundle bundle) {
        super.saveParamsToBundle(bundle);
        mTitle = titleHolder.getValue();
        bundle.putString(PARAM_TITLE, mTitle);
        bundle.putString(PARAM_ACTID, activityId);
    }

    @ViewId(R.id.ui_activity_notice_creator_content)
    private ClearEditText contentView;

    private SimpleInputableViewHolder titleHolder;

    @Override
    public int getLayout() {
        return R.layout.fragment_activity_notice_creator;
    }

    @Override
    public void doingInResume() {
        setCustomTitle(R.string.ui_activity_notice_creator_fragment_title);
        setRightIcon(R.string.ui_base_text_publish);
        setRightTitleClickListener(new OnTitleButtonClickListener() {
            @Override
            public void onClick() {
                tryPublishNotice();
            }
        });
        initializeHolder();
    }

    @Override
    protected boolean checkStillEditing() {
        // 标题或内容不为空时提醒正在编辑
        return !isEmpty(titleHolder.getValue()) || !isEmpty(contentView.getValue());
    }

    @Override
    protected boolean shouldSetDefaultTitleEvents() {
        return true;
    }

    @Override
    protected void destroyView() {

    }

    private void initializeHolder() {
        if (null == titleHolder) {
            titleHolder = new SimpleInputableViewHolder(mRootView, this);
        }
        titleHolder.showContent(format(getString(R.string.ui_activity_notice_creator_title), mTitle));

        if (isEmpty(activityId)) {
            Activity act = Activity.getByTid(mQueryId);
            if (null != act) {
                activityId = act.getId();
            }
        }
    }

    private void tryPublishNotice() {
        if (isEmpty(activityId)) {
            ToastHelper.make().showMsg(R.string.ui_activity_details_invalid_parameter);
            return;
        }
        mTitle = titleHolder.getValue();
        if (isEmpty(mTitle)) {
            ToastHelper.make().showMsg(R.string.ui_activity_notice_creator_title_invalid);
            return;
        }
        String content = contentView.getValue();
        if (isEmpty(content)) {
            ToastHelper.make().showMsg(R.string.ui_activity_notice_creator_content_invalid);
            return;
        }
        publishNotice(content);
    }

    private void publishNotice(String content) {
        setLoadingText(R.string.ui_activity_notice_creator_creating);
        displayLoading(true);
        AppNoticeRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<AppNotice>() {
            @Override
            public void onResponse(AppNotice appNotice, boolean success, String message) {
                super.onResponse(appNotice, success, message);
                displayLoading(false);
                if (success) {
                    if (null != appNotice) {
                        resultData(Json.gson().toJson(appNotice, new TypeToken<AppNotice>() {
                        }.getType()));
                    }
                } else {
                    ToastHelper.make().showMsg(message);
                }
            }
        }).add(activityId, mTitle, content);
    }
}
