package com.leadcom.android.isp.model;

import com.leadcom.android.isp.application.App;
import com.litesuits.orm.LiteOrm;
import com.litesuits.orm.db.assit.QueryBuilder;
import com.litesuits.orm.db.assit.WhereBuilder;

import java.util.List;

/**
 * <b>功能描述：</b>数据访问层<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/04/19 15:30 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/04/19 15:30 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class Dao<E> {

    public LiteOrm orm;

    /**
     * 当前类
     */
    private Class<E> clazz;

    public Dao(Class<E> clazz) {
        orm = App.Orm;
        this.clazz = clazz;
    }

    public void save(E entity) {
        if (null != entity && null != orm) {
            if (entity instanceof Model) {
                ((Model) entity).onSave();
            }
            orm.save(entity);
        }
    }

    public void save(List<E> list) {
        if (null != list && list.size() > 0 && null != orm) {
            for (E entity : list) {
                if (entity instanceof Model) {
                    ((Model) entity).onSave();
                }
            }
            orm.save(list);
        }
    }

    /**
     * 按照主键id查询
     */
    public E query(String id) {
        List<E> temp = query(Model.Field.Id, id);
        return (null == temp || temp.size() <= 0) ? null : temp.get(0);
        //return orm.queryById(id, clazz);
    }

    /**
     * 按指定字段值查询多个对象
     */
    public List<E> query(String field, Object value) {
        return query(new QueryBuilder<E>(clazz).whereEquals(field, value));
    }

    /**
     * 按指定字段值查询单个对象
     */
    public E querySingle(String field, Object value) {
        List<E> list = query(field, value);
        return (null == list || list.size() < 1) ? null : list.get(0);
    }

    public List<E> in(String field, List<String> values) {
        return query(new QueryBuilder<E>(clazz).whereIn(field, values));
    }

    /**
     * 查询整个列表
     */
    public List<E> query() {
        return null == orm ? null : orm.query(clazz);
    }

    /**
     * 指定查询条件
     */
    public List<E> query(QueryBuilder<E> builder) {
        return null == orm ? null : orm.query(builder);
    }

    /**
     * 统计表内的记录数量
     */
    public long getCount() {
        return null == orm ? 0L : orm.queryCount(clazz);
    }

    /**
     * 统计查询条件内的记录数量
     */
    public long getCount(QueryBuilder<E> builder) {
        return null == orm ? 0L : orm.queryCount(builder);
    }

    public void clear() {
        if (null != orm) {
            orm.deleteAll(clazz);
        }
    }

    public void delete(String id) {
        if (null != orm) {
            E e = query(id);
            if (null != e) {
                delete(e);
            }
        }
    }

    public void delete(E entity) {
        if (null != orm) {
            orm.delete(entity);
        }
    }

    public void delete(WhereBuilder where) {
        if (null != orm) {
            orm.delete(where);
        }
    }
}
