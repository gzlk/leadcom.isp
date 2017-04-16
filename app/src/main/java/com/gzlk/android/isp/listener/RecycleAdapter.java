package com.gzlk.android.isp.listener;

/**
 * <b>功能描述：</b>RecyclerView.Adapter 的扩展方法<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/04/13 18:55 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/04/13 18:55 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public interface RecycleAdapter<T> {

    /**
     * 清除列表
     */
    void clear();

    /**
     * 删除指定位置的item
     */
    void remove(int position);

    /**
     * 删除指定的item
     */
    void remove(T item);

    /**
     * 在列表末尾添加一个item
     */
    void add(T item);

    /**
     * 在指定位置添加一个item
     */
    void add(T item, int position);

    /**
     * 当前列表中是否存在指定值的item(value可以指定为任意字段的值)
     */
    boolean exist(T item);

}
