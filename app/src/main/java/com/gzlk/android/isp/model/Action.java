package com.gzlk.android.isp.model;

import com.litesuits.orm.db.assit.QueryBuilder;

import java.util.List;

/**
 * <b>功能描述：</b>Model基类的db操作<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/04/19 15:06 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/04/19 15:06 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public interface Action<E> {
    /**
     * 保存单个实例，等于插入或更新
     */
    void save(E entity);

    /**
     * 保存多个实例
     */
    void save(List<E> list);

    /**
     * 查询整个表
     */
    List<E> query();

    /**
     * 按照主键的id查询
     */
    E query(String id);

    /**
     * 指定字段名和值的查询
     */
    List<E> query(String field, Object value);

    /**
     * 自定义复杂的查询条件
     */
    List<E> query(QueryBuilder<E> where);

    /**
     * 删除整个表
     */
    void clear();

    /**
     * 删除指定对象
     */
    void delete(E entity);

    /**
     * 复杂条件的删除
     */
    void delete(QueryBuilder<E> where);
}
