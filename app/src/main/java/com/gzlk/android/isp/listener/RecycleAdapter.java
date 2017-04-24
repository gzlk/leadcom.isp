package com.gzlk.android.isp.listener;

import java.util.List;

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
     * 增加一个对象并指定是否覆盖已有的记录
     */
    void add(T item, boolean replace);

    /**
     * 在指定位置添加一个item
     */
    void add(T item, int position);

    /**
     * 获取指定位置的item
     */
    T get(int position);

    /**
     * 当前列表中是否存在指定值的item(value可以指定为任意字段的值)
     */
    boolean exist(T item);

    /**
     * 更新一个item，如果item已存在则更新，否则添加到末尾
     */
    void update(T item);

    /**
     * 将旧列表里的记录换成指定的记录列表
     */
    void update(List<T> list);

    /**
     * 添加纪录
     *
     * @param list      需要添加的记录
     * @param fromStart 是否从头部开始添加
     */
    void add(List<T> list, boolean fromStart);

    /**
     * 将列表内容重新排序，重新排序会导致整个列表重新绘制，如果holder里处理的东西太多可能会造成性能问题，万不得已时也要慎用
     */
    void sort();

    /**
     * 设置item是否占满整行
     */
    boolean isItemNeedFullLine(int position);

    /**
     * 设置item所占的列数，只能用在GridLayoutManager中，其余的LayoutManager可能会无效
     */
    int getItemSpanSize(int position);
}
