package com.leadcom.android.isp.adapter;

import android.support.annotation.Nullable;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.leadcom.android.isp.etc.ReflectionUtil;
import com.leadcom.android.isp.helper.StringHelper;
import com.leadcom.android.isp.holder.BaseViewHolder;
import com.leadcom.android.isp.listener.RecycleAdapter;

import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

/**
 * <b>功能描述：</b>RecyclerView的适配器，提供添加删除功能<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2016/12/22 13:19 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2016/12/22 13:19 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */
public abstract class RecyclerViewAdapter<VH extends RecyclerView.ViewHolder, T>
        extends RecyclerView.Adapter<VH> implements RecycleAdapter<T> {

    /**
     * 设置item所占的列数，只能用在GridLayoutManager中，其余的LayoutManager无效
     */
    @Override
    public int getItemSpanSize(int position) {
        return 1;
    }


    @Override
    public boolean isItemNeedFullLine(int position) {
        return false;
    }

    /**
     * 创建ViewHolder
     */
    public abstract VH onCreateViewHolder(View itemView, int viewType);

    /**
     * 返回单个item的layout布局
     */
    public abstract int itemLayout(int viewType);

    /**
     * 绑定数据
     */
    public abstract void onBindHolderOfView(VH holder, int position, @Nullable T item);

    @Override
    public void onViewAttachedToWindow(VH holder) {
        super.onViewAttachedToWindow(holder);
        if (holder instanceof BaseViewHolder) {
            ((BaseViewHolder) holder).attachedFromWindow();
        }
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
        RecyclerView.LayoutManager manager = recyclerView.getLayoutManager();
        if (manager instanceof GridLayoutManager) {
            GridLayoutManager gridManager = ((GridLayoutManager) manager);
            final int spanCount = gridManager.getSpanCount();
            gridManager.setSpanSizeLookup(new GridLayoutManager.SpanSizeLookup() {
                @Override
                public int getSpanSize(int position) {
                    // 第一个item占满整行
                    boolean isFullLineSupport = isItemNeedFullLine(position);
                    if (isFullLineSupport) {
                        return spanCount;
                    } else {
                        int size = getItemSpanSize(position);
                        // 小于1时默认占1列，大于等于spanCount时也即占满全行
                        return size < 1 ? 1 : (size >= spanCount ? spanCount : size);
                    }
                }
            });
        }
    }

    @Override
    public VH onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        View view = inflater.inflate(itemLayout(viewType), parent, false);
        return onCreateViewHolder(view, viewType);
    }

    @Override
    public void onBindViewHolder(VH holder, int position) {
        onBindHolderOfView(holder, position, innerList.get(position));
        // 是否占满屏幕宽度的设定
        ViewGroup.LayoutParams params = holder.itemView.getLayoutParams();
        if (params != null && params instanceof StaggeredGridLayoutManager.LayoutParams) {
            StaggeredGridLayoutManager.LayoutParams p = (StaggeredGridLayoutManager.LayoutParams) params;
            p.setFullSpan(isItemNeedFullLine(position));
            //int size = getItemSpanSize(position);
        }
    }

    @Override
    public void onViewDetachedFromWindow(VH holder) {
        if (holder instanceof BaseViewHolder) {
            ((BaseViewHolder) holder).detachedFromWindow();
        }
        super.onViewDetachedFromWindow(holder);
    }

    @Override
    public int getItemCount() {
        return innerList.size();
    }

    private List<T> innerList = new ArrayList<>();

    @Override
    public void clear() {
        int size = innerList.size();
        while (size > 0) {
            remove(size - 1);
            size = innerList.size();
        }
    }

    @Override
    public void remove(int position) {
        if (position >= 0) {
            innerList.remove(position);
            notifyItemRemoved(position);
        }
    }

    @Override
    public void remove(T item) {
        remove(indexOf(item));
    }

    @Override
    public void remove(String itemId) {
        T item = get(itemId);
        if (null != item) {
            remove(item);
        }
    }

    /**
     * 是否可以通过字符串的 id 方式比较
     */
    private boolean isModelComparable() {
        ParameterizedType pt = (ParameterizedType) this.getClass().getGenericSuperclass();
        Class clazz = (Class) pt.getActualTypeArguments()[1];
        return (ReflectionUtil.hasMethod(clazz.getName(), "setId", new Class[]{String.class}));
    }

    @Override
    public void add(T item) {
        if (!exist(item)) {
            innerList.add(item);
            notifyItemInserted(innerList.size() - 1);
        }
    }

    @Override
    public void add(T item, boolean replace) {
        if (replace) {
            update(item);
        } else {
            add(item);
        }
    }

    @Override
    public void add(T item, int position) {
        if (!exist(item)) {
            innerList.add(position, item);
            notifyItemInserted(position);
        }
    }

    @Override
    public void add(List<T> list) {
        for (T t : list) {
            add(t);
        }
    }

    @Override
    public void add(List<T> list, int position) {
        int index = position;
        for (T t : list) {
            if (!exist(t)) {
                add(t, index);
                index++;
            }
        }
    }

    @Override
    public T get(int position) {
        return innerList.get(position);
    }

    @Override
    public T get(String itemId) {
        if (isModelComparable()) {
            // 如果参数类含有 setId(String val) 方法的话，说明是可以比较的
            for (T item : innerList) {
                String id = (String) ReflectionUtil.getFieldValue(item, "_id");
                if (StringHelper.isEmpty(id)) {
                    id = (String) ReflectionUtil.getFieldValue(item, "id");
                }
                if (!StringHelper.isEmpty(id) && id.equals(itemId)) {
                    return item;
                }
            }
        }
        return null;
    }

    @Override
    public boolean exist(T item) {
        return indexOf(item) >= 0;
    }

    @Override
    public int indexOf(T item) {
        return innerList.indexOf(item);
    }

    @Override
    public void replace(T item, int index) {
        if (index >= 0 && index < innerList.size()) {
            innerList.set(index, item);
            notifyItemChanged(index);
        }
    }

    /**
     * 更新一个指定id的item内容
     */
    @Override
    public void update(T item) {
        int index = indexOf(item);
        if (index >= 0) {
            innerList.set(index, item);
            notifyItemChanged(index);
        } else {
            add(item);
        }
    }

    @Override
    public void update(List<T> list) {
        update(list, false);
    }

    @Override
    public void update(List<T> list, boolean replaceable) {
        if (replaceable) {
            Iterator<T> iterator = innerList.iterator();
            while (iterator.hasNext()) {
                // 移除旧列表里不在list中的记录
                T t = iterator.next();
                if (list.indexOf(t) < 0) {
                    iterator.remove();
                    //notifyItemRemoved(index);
                }
            }
            notifyDataSetChanged();
        }
        for (T t : list) {
            update(t);
        }
    }

    @Override
    public Iterator<T> iterator() {
        return innerList.iterator();
    }

    @Override
    public void add(List<T> list, boolean fromStart) {
        int index = 0;
        for (T t : list) {
            if (!exist(t)) {
                if (fromStart) {
                    add(t, index);
                    index++;
                } else {
                    add(t);
                }
            }
        }
    }

    @Override
    public void swap(int i, int j) {
        // instead of using a raw type here, it's possible to capture
        // the wildcard but it will require a call to a supplementary
        // private method
        final List<T> l = innerList;
        l.set(i, l.set(j, l.get(i)));
    }

    @Override
    public void sort() {
        Collections.sort(innerList, new Comparator<T>() {
            @Override
            public int compare(T o1, T o2) {
                return comparator(o1, o2);
            }
        });
        notifyItemRangeChanged(0, innerList.size());
    }

    /**
     * 重新排序
     */
    protected abstract int comparator(T item1, T item2);
}
