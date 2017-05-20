package com.gzlk.android.isp.manager;

import com.gzlk.android.isp.manager.listener.MultipleManageListener;
import com.gzlk.android.isp.manager.listener.SingleManageListener;
import com.gzlk.android.isp.model.Dao;
import com.gzlk.android.isp.model.Model;
import com.litesuits.orm.db.assit.QueryBuilder;

import java.util.List;

/**
 * <b>功能描述：</b>Model的管理器<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/05/20 17:29 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/05/20 17:29 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public abstract class BaseManager<T extends Model> {

    /**
     * 当前表
     */
    private Class<T> clazz;

    protected Dao<T> dao;

    public BaseManager() {
        dao = new Dao<>(requestedType());
    }

    protected abstract Class<T> requestedType();

    protected SingleManageListener<T> mSingleManageListener;

    @SuppressWarnings("unchecked")
    public <S extends BaseManager<T>> S setSingleManageListener(SingleManageListener<T> singleManageListener) {
        mSingleManageListener = singleManageListener;
        return (S) this;
    }

    protected void fireSingleManageListener(T t) {
        if (null != mSingleManageListener) {
            mSingleManageListener.onLoaded(t);
        }
    }

    protected MultipleManageListener<T> mMultipleManageListener;

    @SuppressWarnings("unchecked")
    public <S extends BaseManager<T>> S setMultipleManageListener(MultipleManageListener<T> multipleManageListener) {
        mMultipleManageListener = multipleManageListener;
        return (S) this;
    }

    public void save(T entity) {
        dao.save(entity);
    }

    public void save(List<T> list) {
        dao.save(list);
    }

    public abstract void find(String byId);
}
