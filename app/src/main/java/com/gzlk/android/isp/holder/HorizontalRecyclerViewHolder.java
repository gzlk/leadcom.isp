package com.gzlk.android.isp.holder;

import android.graphics.Rect;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.View;
import android.view.ViewGroup;

import com.gzlk.android.isp.R;
import com.gzlk.android.isp.adapter.RecyclerViewAdapter;
import com.gzlk.android.isp.fragment.base.BaseFragment;
import com.gzlk.android.isp.listener.OnViewHolderClickListener;
import com.gzlk.android.isp.listener.RecycleAdapter;
import com.hlk.hlklib.layoutmanager.CustomGridLayoutManager;
import com.hlk.hlklib.layoutmanager.CustomLinearLayoutManager;
import com.hlk.hlklib.layoutmanager.CustomStaggeredGridLayoutManager;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.inject.ViewUtility;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * <b>功能描述：</b>横向RecyclerView<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/01/12 10:20 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/01/12 10:20 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class HorizontalRecyclerViewHolder extends BaseViewHolder {

    @ViewId(R.id.ui_tool_horizontal_recycler_view)
    private RecyclerView recyclerView;

    private ItemAdapter itemAdapter;

    private int spanCount = 4;

    // 原始数据
    private List<String> data = new ArrayList<>();

    /**
     * 横向RecyclerView
     *
     * @param itemView       宿主layout
     * @param fragment       宿主fragment
     * @param onlyHorizontal true=横向排列，false=瀑布型排列
     */
    public HorizontalRecyclerViewHolder(View itemView, BaseFragment fragment, boolean onlyHorizontal) {
        super(itemView, fragment);
        ViewUtility.bind(this, itemView);
        if (null != recyclerView) {
            if (onlyHorizontal) {
                CustomLinearLayoutManager llm = new CustomLinearLayoutManager(recyclerView.getContext());
                llm.setOrientation(CustomLinearLayoutManager.HORIZONTAL);
                recyclerView.setLayoutManager(llm);
            } else {
                // 瀑布流
                CustomStaggeredGridLayoutManager cglm = new CustomStaggeredGridLayoutManager(spanCount, StaggeredGridLayoutManager.VERTICAL);
                recyclerView.setLayoutManager(cglm);
            }
        }
    }

    private SpacesItemDecoration spacesItemDecoration;

    /**
     * 设置每个item之间的间隔
     */
    public void setItemDecoration(boolean haven) {
        if (haven) {
            spacesItemDecoration = new SpacesItemDecoration();
            recyclerView.addItemDecoration(spacesItemDecoration);
        } else {
            if (null != spacesItemDecoration) {
                recyclerView.removeItemDecoration(spacesItemDecoration);
            }
        }
    }

    /**
     * 设置列数（仅仅在瀑布模型下有效）
     */
    public void setSpanCount(int spanCount) {
        this.spanCount = spanCount;
        RecyclerView.LayoutManager lm = recyclerView.getLayoutManager();
        if (lm instanceof StaggeredGridLayoutManager) {
            ((StaggeredGridLayoutManager) recyclerView.getLayoutManager()).setSpanCount(spanCount);
        } else if (lm instanceof GridLayoutManager) {
            ((GridLayoutManager) recyclerView.getLayoutManager()).setSpanCount(spanCount);
        } else {
            throw new IllegalArgumentException("Your view holder only support horizontal");
        }
    }

    private boolean displaySelectedEffect = false;

    /**
     * 是否显示选中效果
     */
    public void displaySelectedEffect(boolean display) {
        displaySelectedEffect = display;
    }

    public void setDataSources(String[] strings) {
        setDataSources(Arrays.asList(strings));
    }

    public void setDataSources(List<String> strings) {
        data.clear();
        data.addAll(strings);
        initializeItems();
    }

    private void initializeItems() {
        if (null == itemAdapter) {
            itemAdapter = new ItemAdapter();
            recyclerView.setAdapter(itemAdapter);
            // 一个一个增加底部功能列表
            displayItems();
        }
    }

    private int selectedIndex = -1;

    public void setSelectedIndex(int index) {
        selectedIndex = index;
    }

    /**
     * 显示列表
     */
    public void displayItems() {
        itemAdapter.clear();
        for (String string : data) {
            Item item = new Item();
            item.text = string;
            item.selected = string.contains(format("%d|", selectedIndex));
            itemAdapter.add(item);
        }
    }

    private class ItemAdapter extends RecyclerViewAdapter<TextViewHolder, Item> implements RecycleAdapter<Item> {

        private void resizeWidth(View itemView) {
            int dimen = getDimension(R.dimen.ui_base_border_size_normal);
            int width = (fragment().getScreenWidth() - (dimen * spanCount)) / spanCount;
            ViewGroup.LayoutParams params = itemView.getLayoutParams();
            params.width = width;
            itemView.setLayoutParams(params);
        }

        @Override
        public TextViewHolder onCreateViewHolder(View itemView, int viewType) {
            TextViewHolder holder = new TextViewHolder(itemView, fragment());
            holder.showSelectedEffect(displaySelectedEffect);
            holder.addOnViewHolderClickListener(holderClickListener);
            return holder;
        }

        @Override
        public int itemLayout(int viewType) {
            return R.layout.holder_view_text_olny;
        }

        @Override
        public void onBindHolderOfView(TextViewHolder holder, int position, Item item) {
            holder.showContent(item.text, item.selected);
            resizeWidth(holder.itemView);
        }

        @Override
        protected int comparator(Item item1, Item item2) {
            return item1.text.compareTo(item2.text);
        }
    }

    private OnViewHolderClickListener holderClickListener = new OnViewHolderClickListener() {
        @Override
        public void onClick(int index) {
            if (displaySelectedEffect) {
                resetClickStatus(index);
            }
            if (null != mOnViewHolderClickListener) {
                mOnViewHolderClickListener.onClick(index);
            }
        }
    };

    private void resetClickStatus(int index) {
        for (int i = 0, len = itemAdapter.getItemCount(); i < len; i++) {
            itemAdapter.get(i).selected = (i == index);
            itemAdapter.notifyItemChanged(i);
        }
    }

    private class Item {
        String text;
        boolean selected;
    }

    private class SpacesItemDecoration extends RecyclerView.ItemDecoration {

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            int dimen = getDimension(R.dimen.ui_base_border_size_normal);
            int position = parent.getChildAdapterPosition(view);
            outRect.top = 0;
            outRect.left = 0;
            StaggeredGridLayoutManager manager = (StaggeredGridLayoutManager) parent.getLayoutManager();
            int spanCount = manager.getSpanCount();
            int column = position % spanCount;
            // 最后行底部无空白，其余行有空白
            outRect.bottom = dimen;
            // 最后列右侧无空白，其余列右侧有空白
            outRect.right = (column < (spanCount - 1)) ? dimen : 0;
        }
    }

}
