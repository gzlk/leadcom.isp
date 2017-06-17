package com.gzlk.android.isp.fragment.activity.sign;

import android.os.Bundle;
import android.view.View;

import com.google.gson.reflect.TypeToken;
import com.gzlk.android.isp.R;
import com.gzlk.android.isp.fragment.base.BaseTransparentSupportFragment;
import com.gzlk.android.isp.fragment.map.AddressMapPickerFragment;
import com.gzlk.android.isp.helper.StringHelper;
import com.gzlk.android.isp.holder.common.SimpleClickableViewHolder;
import com.gzlk.android.isp.holder.common.SimpleInputableViewHolder;
import com.gzlk.android.isp.lib.Json;
import com.gzlk.android.isp.listener.OnTitleButtonClickListener;
import com.gzlk.android.isp.listener.OnViewHolderClickListener;
import com.gzlk.android.isp.model.activity.AppSigning;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.view.ClearEditText;

/**
 * <b>功能描述：</b>新建签到<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/06/16 18:08 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/06/16 18:08 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class SignCreatorFragment extends BaseTransparentSupportFragment {

    private static final String PARAM1 = "scf_param_sign_content";

    public static SignCreatorFragment newInstance(String params) {
        SignCreatorFragment scf = new SignCreatorFragment();
        Bundle bundle = new Bundle();
        // 网易云传过来的活动的tid
        bundle.putString(PARAM_QUERY_ID, params);
        scf.setArguments(bundle);
        return scf;
    }

    @Override
    protected void getParamsFromBundle(Bundle bundle) {
        super.getParamsFromBundle(bundle);
        String json = bundle.getString(PARAM1, "");
        if (!isEmpty(json)) {
            signing = Json.gson().fromJson(json, new TypeToken<AppSigning>() {
            }.getType());
        }
        if (null == signing) {
            signing = new AppSigning();
        }
    }

    @Override
    protected void saveParamsToBundle(Bundle bundle) {
        super.saveParamsToBundle(bundle);
        saveSigning();
        bundle.putString(PARAM1, Json.gson().toJson(signing, new TypeToken<AppSigning>() {
        }.getType()));
    }

    private void saveSigning() {
        signing.setTitle(titleHolder.getValue());
        signing.setDesc(contentView.getValue());
    }

    private AppSigning signing;

    @ViewId(R.id.ui_activity_sign_creator_title)
    private View titleView;
    @ViewId(R.id.ui_activity_sign_creator_content)
    private ClearEditText contentView;
    @ViewId(R.id.ui_activity_sign_creator_address)
    private View addressView;
    @ViewId(R.id.ui_activity_sign_creator_begin)
    private View beginView;
    @ViewId(R.id.ui_activity_sign_creator_end)
    private View endView;
    @ViewId(R.id.ui_activity_sign_creator_notify)
    private View notifyView;

    private SimpleInputableViewHolder titleHolder;
    private SimpleClickableViewHolder addressHolder, beginHolder, endHolder, notifyHolder;

    private String[] items;

    @Override
    public int getLayout() {
        return R.layout.fragment_activity_sing_creator;
    }

    @Override
    public void doingInResume() {
        setCustomTitle(R.string.ui_activity_sign_creator_fragment_title);
        setRightText(R.string.ui_base_text_publish);
        setRightTitleClickListener(new OnTitleButtonClickListener() {
            @Override
            public void onClick() {

            }
        });
        initializeHolders();
    }

    @Override
    protected boolean shouldSetDefaultTitleEvents() {
        return false;
    }

    @Override
    protected void destroyView() {

    }

    private void initializeHolders() {
        if (null == items) {
            items = StringHelper.getStringArray(R.array.ui_activity_sign_creator_items);
        }
        if (null == titleHolder) {
            titleHolder = new SimpleInputableViewHolder(titleView, this);
        }
        titleHolder.showContent(format(items[0], signing.getTitle()));
        if (null == addressHolder) {
            addressHolder = new SimpleClickableViewHolder(addressView, this);
            addressHolder.addOnViewHolderClickListener(onViewHolderClickListener);
        }
        addressHolder.showContent(format(items[2], signing.getAddress()));
        if (null == beginHolder) {
            beginHolder = new SimpleClickableViewHolder(beginView, this);
            beginHolder.addOnViewHolderClickListener(onViewHolderClickListener);
        }
        beginHolder.showContent(format(items[3], formatDateTime(signing.getBeginTime())));
        if (null == endHolder) {
            endHolder = new SimpleClickableViewHolder(endView, this);
            endHolder.addOnViewHolderClickListener(onViewHolderClickListener);
        }
        endHolder.showContent(format(items[4], formatDateTime(signing.getEndTime())));
        if (null == notifyHolder) {
            notifyHolder = new SimpleClickableViewHolder(notifyView, this);
            notifyHolder.addOnViewHolderClickListener(onViewHolderClickListener);
        }
        notifyHolder.showContent(format(items[5], "30分钟"));
    }

    private static final int REQ_ADDRESS = ACTIVITY_BASE_REQUEST + 10;

    private OnViewHolderClickListener onViewHolderClickListener = new OnViewHolderClickListener() {
        @Override
        public void onClick(int index) {
            switch (index) {
                case 0:
                    // 选择签到地址
                    openActivity(AddressMapPickerFragment.class.getName(), "", REQ_ADDRESS, true, false);
                    break;
                case 1:
                    // 选择签到开始时间
                    break;
                case 2:
                    // 签到结束时间
                    break;
                case 3:
                    // 签到通知时间
                    break;
            }
        }
    };
}
