package com.leadcom.android.isp.helper.publishable;

import com.leadcom.android.isp.api.listener.OnSingleRequestListener;
import com.leadcom.android.isp.api.user.CollectionRequest;
import com.leadcom.android.isp.helper.publishable.listener.OnCollectedListener;
import com.leadcom.android.isp.helper.publishable.listener.OnUncollectedListener;
import com.leadcom.android.isp.model.Model;
import com.leadcom.android.isp.model.archive.Archive;
import com.leadcom.android.isp.model.user.Collection;
import com.leadcom.android.isp.model.user.Moment;

/**
 * <b>功能描述：</b>收藏helper<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/10/31 21:09 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>version: 1.0.0 <br />
 * <b>修改时间：</b>2017/10/31 21:09 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */
public class CollectHelper extends Publishable {

    public static CollectHelper helper() {
        return new CollectHelper();
    }

    @Override
    public CollectHelper setModel(Model model) {
        if (null == model) {
            throw new IllegalArgumentException("Cannot set null object to collect helper.");
        }
        super.setModel(model);
        return this;
    }

    @Override
    public CollectHelper setArchive(Archive archive) {
        mArchive = archive;
        return this;
    }

    @Override
    public CollectHelper setMoment(Moment moment) {
        mMoment = moment;
        return this;
    }

    private OnCollectedListener collectedListener;

    public CollectHelper setCollectedListener(OnCollectedListener l) {
        collectedListener = l;
        return this;
    }

    private OnUncollectedListener uncollectedListener;

    public CollectHelper setUncollectedListener(OnUncollectedListener l) {
        uncollectedListener = l;
        return this;
    }

    public void collect(Collection source) {
        CollectionRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<Collection>() {
            @Override
            public void onResponse(Collection collection, boolean success, String message) {
                super.onResponse(collection, success, message);
                if (null != collectedListener) {
                    if (null != mArchive) {
                        if (success) {
                            mArchive.setColNum(mArchive.getColNum() + 1);
                            mArchive.setColId(collection.getId());
                            mArchive.setCollection(Archive.CollectionType.COLLECTED);
                        }
                        collectedListener.onCollected(success, mArchive);
                    }
                }
            }
        }).add(source, null);
    }

    public void uncollect(String collectionId) {
        CollectionRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<Collection>() {
            @Override
            public void onResponse(Collection collection, boolean success, String message) {
                super.onResponse(collection, success, message);
                if (null != uncollectedListener) {
                    if (null != mArchive) {
                        if (success) {
                            int num = mArchive.getColNum() - 1;
                            mArchive.setColNum(num >= 0 ? num : 0);
                            mArchive.setCollection(Archive.CollectionType.UN_COLLECT);
                            mArchive.setColId("");
                        }
                        uncollectedListener.onUncollected(success, mArchive);
                    }
                }
            }
        }).delete(collectionId);
    }
}
