package com.gzlk.android.isp.multitype.adapter;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.ViewGroup;

import com.gzlk.android.isp.holder.BaseViewHolder;
import com.gzlk.android.isp.listener.RecycleAdapter;
import com.gzlk.android.isp.model.Model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;

import me.drakeet.multitype.MultiTypeAdapter;

/**
 * <b>功能描述：</b><br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/04/20 23:36 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/04/20 23:36 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public abstract class BaseMultiTypeAdapter<T extends Model> extends MultiTypeAdapter implements RecycleAdapter<T> {

    private ArrayList<T> innerList = new ArrayList<>();

    public BaseMultiTypeAdapter() {
        super();
        setItems(innerList);
    }

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

    @Override
    public void onViewAttachedToWindow(RecyclerView.ViewHolder holder) {
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
                    // 占满整行
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
    public void onViewDetachedFromWindow(RecyclerView.ViewHolder holder) {
        if (holder instanceof BaseViewHolder) {
            ((BaseViewHolder) holder).detachedFromWindow();
        }
        super.onViewDetachedFromWindow(holder);
    }

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
        innerList.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public void remove(T item) {
        remove(innerList.indexOf(item));
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
    public T get(int position) {
        return innerList.get(position);
    }

    @Override
    public boolean exist(T item) {
        return innerList.indexOf(item) >= 0;
    }

    /**
     * 更新一个指定id的item内容
     */
    @Override
    public void update(T item) {
        if (exist(item)) {
            int index = innerList.indexOf(item);
            innerList.set(index, item);
            notifyItemChanged(index);
        } else {
            add(item);
        }
    }

    @Override
    public void update(List<T> list) {
        update(list, false);
//        Iterator<T> iterator = innerList.iterator();
//        int index = 0;
//        while (iterator.hasNext()) {
//            T t = iterator.next();
//            if (list.indexOf(t) < 0) {
//                iterator.remove();
//                notifyItemRemoved(index);
//            }
//            index++;
//        }
//        add(list, false);
    }

    @Override
    public void update(List<T> list, boolean replaceable) {
        for (T t : list) {
            update(t);
        }
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
