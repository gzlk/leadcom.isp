package com.gzlk.android.isp.fragment.organization;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextPaint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.daimajia.swipe.adapters.RecyclerSwipeAdapter;
import com.daimajia.swipe.util.Attributes;
import com.google.gson.reflect.TypeToken;
import com.gzlk.android.isp.R;
import com.gzlk.android.isp.activity.TitleActivity;
import com.gzlk.android.isp.fragment.base.BaseSwipeRefreshSupportFragment;
import com.gzlk.android.isp.helper.StringHelper;
import com.gzlk.android.isp.helper.ToastHelper;
import com.gzlk.android.isp.helper.TooltipHelper;
import com.gzlk.android.isp.holder.ContactViewHolder;
import com.gzlk.android.isp.holder.SearchableViewHolder;
import com.gzlk.android.isp.lib.Json;
import com.gzlk.android.isp.listener.OnTitleButtonClickListener;
import com.gzlk.android.isp.model.user.User;
import com.hlk.hlklib.lib.inject.ViewId;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * <b>功能描述：</b>通讯录(包括小组通讯录和组织通讯录)<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/05/09 00:25 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/05/09 00:25 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class ContactFragment extends BaseSwipeRefreshSupportFragment {

    private static final String PARAM_TYPE = "_cf_type_";
    /**
     * 打开的是小组的通讯录
     */
    public static final int TYPE_SQUAD = 1;
    /**
     * 打开的是组织的通讯录
     */
    public static final int TYPE_ORG = 2;

    public static ContactFragment newInstance(String params) {
        ContactFragment cf = new ContactFragment();
        String[] strings = splitParameters(params);
        Bundle bundle = new Bundle();
        bundle.putInt(PARAM_TYPE, Integer.valueOf(strings[0]));
        bundle.putString(PARAM_QUERY_ID, strings[1]);
        return cf;
    }

    @Override
    protected void getParamsFromBundle(Bundle bundle) {
        super.getParamsFromBundle(bundle);
        showType = bundle.getInt(PARAM_TYPE, TYPE_ORG);
    }

    @Override
    protected void saveParamsToBundle(Bundle bundle) {
        super.saveParamsToBundle(bundle);
        bundle.putInt(PARAM_TYPE, showType);
    }

    // view
    @ViewId(R.id.ui_holder_view_searchable_container)
    private View searchView;
    @ViewId(R.id.ui_tool_view_phone_contact_container)
    private View phoneContactView;

    // holder
    private SearchableViewHolder searchableViewHolder;
    private ArrayList<User> users;
    private ContactAdapter mAdapter;

    // 默认显示组织的联系人列表
    private int showType = TYPE_ORG;

    @Override
    public int getLayout() {
        return R.layout.fragment_contact;
    }

    @Override
    protected void onDelayRefreshComplete(@DelayType int type) {

    }

    @Override
    public void doingInResume() {
        searchView.setVisibility(showType == TYPE_SQUAD ? View.VISIBLE : View.GONE);
        phoneContactView.setVisibility(showType == TYPE_ORG ? View.VISIBLE : View.GONE);
        initializeTitleEvent();
        initializeHolders();
    }

    // 小组联系人列表时，需要处理标题栏
    private void initializeTitleEvent() {
        if (showType == TYPE_SQUAD) {
            setRightIcon(R.string.ui_icon_add);
            setRightTitleClickListener(new OnTitleButtonClickListener() {
                @Override
                public void onClick() {
                    showTooltip(((TitleActivity) Activity()).getRightButton(), R.id.ui_tool_view_tooltip_menu_squad_contact, true, TooltipHelper.TYPE_RIGHT, new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            popupMenuClickHandle(v);
                        }
                    });
                }
            });
        }
    }

    private void popupMenuClickHandle(View view) {
        switch (view.getId()) {
            case R.id.ui_tool_popup_menu_squad_contact_organization:
                ToastHelper.make().showMsg("组织通讯录");
                break;
            case R.id.ui_tool_popup_menu_squad_contact_phone:
                openActivity(PhoneContactFragment.class.getName(), "", true, false);
                break;
        }
    }

    @Override
    protected boolean shouldSetDefaultTitleEvents() {
        return true;
    }

    @Override
    protected void destroyView() {

    }

    @Override
    protected void onSwipeRefreshing() {

    }

    @Override
    protected void onLoadingMore() {

    }

    @Override
    protected String getLocalPageTag() {
        return null;
    }

    private void initializeHolders() {
        if (null == users) {
            users = Json.gson().fromJson(StringHelper.getString(R.string.temp_json_user), new TypeToken<ArrayList<User>>() {
            }.getType());
            Collections.sort(users, new UserComparator());
//            Collections.sort(users, new Comparator<User>() {
//                @Override
//                public int compare(User ua, User ub) {
//                    return ua.getSpell().compareTo(ub.getSpell());
//                }
//            });
        }
        if (null == searchableViewHolder) {
            searchableViewHolder = new SearchableViewHolder(mRootView, this);
            searchableViewHolder.setOnSearchingListener(searchingListener);
        }
        initializeAdapter();
    }

    private void initializeAdapter() {
        if (null == mAdapter) {
            mAdapter = new ContactAdapter();
            mAdapter.setMode(Attributes.Mode.Single);
            mRecyclerView.addItemDecoration(new StickDecoration());
            mRecyclerView.setAdapter(mAdapter);
            searchingListener.onSearching("");
        }
    }

    private class UserComparator implements Comparator<User> {
        @Override
        public int compare(User u1, User u2) {
            return u1.getSpell().compareTo(u2.getSpell());
        }
    }

    private String searchingText = "";
    private SearchableViewHolder.OnSearchingListener searchingListener = new SearchableViewHolder.OnSearchingListener() {
        @Override
        public void onSearching(String text) {
            if (!StringHelper.isEmpty(text)) {
                searchingText = text;
                mAdapter.clear();
                for (User user : users) {
                    if (user.getName().contains(text)) {
                        mAdapter.add(user);
                    }
                }
            } else {
                searchingText = "";
                mAdapter.addAll(users);
            }
        }
    };

    private ContactViewHolder.OnUserDeleteListener onUserDeleteListener = new ContactViewHolder.OnUserDeleteListener() {
        @Override
        public void onDelete(ContactViewHolder holder) {
            mAdapter.delete(holder);
        }
    };

    private class ContactAdapter extends RecyclerSwipeAdapter<ContactViewHolder> {

        private ArrayList<User> list = new ArrayList<>();

        private void delete(ContactViewHolder holder) {
            mItemManger.removeShownLayouts(holder.getSwipeLayout());
            int pos = holder.getAdapterPosition();
            remove(pos);
            mItemManger.closeAllItems();
        }

        private void addAll(List<User> all) {
            for (User user : all) {
                add(user);
            }
            Collections.sort(mAdapter.list, new UserComparator());
            notifyItemRangeChanged(0, list.size());
        }

        private void add(User user) {
            if (list.indexOf(user) < 0) {
                list.add(user);
                notifyItemChanged(list.size() - 1);
            }
        }

        private void clear() {
            int size = list.size();
            while (size > 0) {

                remove(size - 1);
                size = list.size();
            }
        }

        private void remove(int index) {
            list.remove(index);
            notifyItemRemoved(index);
        }

        private void delete(User user) {
            int index = list.indexOf(user);
            if (index >= 0) {
                remove(index);
            }
        }

        private int getFirstCharCount(char chr) {
            int ret = 0;
            for (User user : list) {
                if (user.getSpell().charAt(0) == chr) {
                    ret++;
                }
            }
            return ret;
        }

        @Override
        public ContactViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
            int layout = R.layout.holder_view_organization_contact;
            View view = LayoutInflater.from(viewGroup.getContext()).inflate(layout, viewGroup, false);
            ContactViewHolder holder = new ContactViewHolder(view, ContactFragment.this);
            holder.setOnUserDeleteListener(onUserDeleteListener);
            return holder;
        }

        @Override
        public void onBindViewHolder(ContactViewHolder holder, int position) {
            holder.showContent(list.get(position), searchingText);
            mItemManger.bindView(holder.itemView, position);
        }

        @Override
        public int getItemCount() {
            return list.size();
        }

        @Override
        public int getSwipeLayoutResourceId(int i) {
            return R.id.ui_holder_view_contact_swipe_layout;
        }
    }

    private class StickDecoration extends RecyclerView.ItemDecoration {
        private int topHeight = getDimension(R.dimen.ui_static_dp_20);
        private int padding = getDimension(R.dimen.ui_base_dimen_margin_padding);
        private int textSize = getDimension(R.dimen.ui_base_text_size_little);
        private TextPaint textPaint;
        private Paint paint;
        private static final String FMT = "%s(%d人)";
        private float baseLine, textHeight;

        StickDecoration() {
            super();
            paint = new Paint();
            paint.setColor(getColor(R.color.textColorHintLightLight));
            textPaint = new TextPaint();
            textPaint.setAntiAlias(true);
            textPaint.setTextSize(textSize);
            textPaint.setColor(getColor(R.color.textColorHint));
            Paint.FontMetrics fm = textPaint.getFontMetrics();
            // 计算文字高度
            textHeight = fm.bottom - fm.top;
            baseLine = fm.bottom;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            int position = parent.getChildAdapterPosition(view);
            if (isFirstInGroup(position)) {
                outRect.top = topHeight;
            } else {
                outRect.top = 0;
            }
        }

        @Override
        public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
            super.onDraw(c, parent, state);
            int left = parent.getPaddingLeft();
            int right = parent.getWidth() - parent.getPaddingRight();
            int childCount = parent.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View view = parent.getChildAt(i);
                int position = parent.getChildAdapterPosition(view);
                //String textLine = users.get(position).getSpell().substring(0, 1);
                //textLine = format(FMT, textLine, getFirstCharCount(textLine.charAt(0)));
                if (isFirstInGroup(position)) {
                    float top = view.getTop() - topHeight;
                    float bottom = view.getTop();
                    drawBackground(c, left, top, right, bottom);
                    drawText(c, position, padding, bottom - (topHeight - textHeight) / 2 - baseLine);
                    //c.drawRect(left, top, right, bottom, paint);//绘制矩形背景
                    //c.drawText(textLine, left + padding, bottom - 30, textPaint);//绘制文本
                }
            }
        }

        @Override
        public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
            super.onDrawOver(c, parent, state);
            int position = ((LinearLayoutManager) (parent.getLayoutManager())).findFirstVisibleItemPosition();
            if (position < 0) {
                return;
            }
            int left = parent.getPaddingLeft();
            int right = parent.getWidth() - parent.getPaddingRight();
            int top = parent.getPaddingTop();
            int bottom = top + topHeight;
            drawBackground(c, left, top, right, bottom);
            drawText(c, position, padding, bottom - (topHeight - textHeight) / 2 - baseLine);
            //c.drawRect(left, 0, right, topHeight, paint);//绘制红色矩形
            //String text = users.get(position).getSpell().substring(0, 1);
            //text = format(FMT, text, getFirstCharCount(text.charAt(0)));
            //c.drawText(text, 30, topHeight - 30, textPaint);//绘制文本
        }

        private void drawBackground(Canvas canvas, float left, float top, float right, float bottom) {
            // 绘制矩形背景
            canvas.drawRect(left, top, right, bottom, paint);
        }

        private void drawText(Canvas canvas, int position, float x, float y) {
            String text = mAdapter.list.get(position).getSpell().substring(0, 1);
            text = format(FMT, text, mAdapter.getFirstCharCount(text.charAt(0)));
            // 绘制文本
            canvas.drawText(text, x, y, textPaint);
        }

        private boolean isFirstInGroup(int position) {
            return position >= 0 && (position == 0 || mAdapter.list.get(position).getSpell().charAt(0) != mAdapter.list.get(position - 1).getSpell().charAt(0));
        }
    }
}
