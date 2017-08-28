package com.gzlk.android.isp.fragment.individual;

import android.os.Bundle;
import android.widget.TextView;

import com.gzlk.android.isp.R;
import com.gzlk.android.isp.api.listener.OnSingleRequestListener;
import com.gzlk.android.isp.api.user.CollectionRequest;
import com.gzlk.android.isp.fragment.base.BaseTransparentSupportFragment;
import com.gzlk.android.isp.helper.StringHelper;
import com.gzlk.android.isp.helper.ToastHelper;
import com.gzlk.android.isp.holder.individual.CollectionItemViewHolder;
import com.gzlk.android.isp.model.Dao;
import com.gzlk.android.isp.model.user.Collection;
import com.hlk.hlklib.lib.inject.ViewId;

/**
 * <b>功能描述：</b>收藏详情<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/05/05 03:33 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/05/05 03:33 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class CollectionDetailsFragment extends BaseTransparentSupportFragment {

    public static CollectionDetailsFragment newInstance(String params) {
        CollectionDetailsFragment cdf = new CollectionDetailsFragment();
        Bundle bundle = new Bundle();
        bundle.putString(PARAM_QUERY_ID, params);
        cdf.setArguments(bundle);
        return cdf;
    }

    @ViewId(R.id.ui_collection_details_time)
    private TextView createTime;

    private CollectionItemViewHolder collectionHolder;

    @Override
    public int getLayout() {
        return R.layout.fragment_individual_collection_details;
    }

    @Override
    public void doingInResume() {
        setCustomTitle(R.string.ui_base_text_details);
        if (StringHelper.isEmpty(mQueryId)) {
            closeWithWarning(R.string.ui_text_collection_details_not_exists);
        } else {
            initializeHolder();
        }
    }

    @Override
    protected boolean shouldSetDefaultTitleEvents() {
        return true;
    }

    @Override
    protected void destroyView() {

    }

    private void initializeHolder() {
        if (null == collectionHolder) {
            collectionHolder = new CollectionItemViewHolder(mRootView, this);
            collectionHolder.setShowLargeImage(true);
            loadingCollection();
        }
    }

    private void loadingCollection() {
        Collection collection = new Dao<>(Collection.class).query(mQueryId);
        if (null == collection) {
            fetchingCollection();
        } else {
            collectionHolder.showContent(collection);
            showCreateTime(collection.getCreateDate());
        }
    }

    private void showCreateTime(String create) {
        String time = formatTimeAgo(create);
        createTime.setText(StringHelper.getString(R.string.ui_text_collection_details_create_at, time));
    }

    private void fetchingCollection() {
        CollectionRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<Collection>() {
            @Override
            public void onResponse(Collection collection, boolean success, String message) {
                super.onResponse(collection, success, message);
                if (success) {
                    if (null != collection && !StringHelper.isEmpty(collection.getId())) {
                        new Dao<>(Collection.class).save(collection);
                        collectionHolder.showContent(collection);
                        showCreateTime(collection.getCreateDate());
                    } else {
                        ToastHelper.make().showMsg(message);
                    }
                } else {
                    finish();
                }
            }
        }).find(mQueryId);
    }
}
