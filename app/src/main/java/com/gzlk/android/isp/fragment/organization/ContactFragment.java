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
import com.gzlk.android.isp.R;
import com.gzlk.android.isp.activity.TitleActivity;
import com.gzlk.android.isp.helper.StringHelper;
import com.gzlk.android.isp.helper.ToastHelper;
import com.gzlk.android.isp.helper.TooltipHelper;
import com.gzlk.android.isp.holder.ContactViewHolder;
import com.gzlk.android.isp.holder.SearchableViewHolder;
import com.gzlk.android.isp.listener.OnTitleButtonClickListener;
import com.gzlk.android.isp.model.Dao;
import com.gzlk.android.isp.model.organization.Member;
import com.gzlk.android.isp.model.organization.Organization;
import com.gzlk.android.isp.model.organization.Squad;
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

public class ContactFragment extends BaseOrganizationFragment {

    private static final String PARAM_TYPE = "_cf_type_";
    /**
     * 没有查询任何数据
     */
    public static final int TYPE_NONE = 0;
    /**
     * 打开的是小组的通讯录
     */
    public static final int TYPE_SQUAD = 1;
    /**
     * 打开的是组织的通讯录
     */
    public static final int TYPE_ORG = 2;

    /**
     * 新建一个实例
     * param: 0=type,1=groupId,2=squadId
     */
    public static ContactFragment newInstance(String params) {
        ContactFragment cf = new ContactFragment();
        String[] strings = splitParameters(params);
        Bundle bundle = new Bundle();
        bundle.putInt(PARAM_TYPE, Integer.valueOf(strings[0]));
        bundle.putString(PARAM_QUERY_ID, strings[1]);
        cf.setArguments(bundle);
        return cf;
    }

    @Override
    protected void getParamsFromBundle(Bundle bundle) {
        super.getParamsFromBundle(bundle);
        showType = bundle.getInt(PARAM_TYPE, TYPE_NONE);
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
    private ArrayList<Member> members = new ArrayList<>();
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

    public void setNewQueryId(String queryId) {
        if (!StringHelper.isEmpty(mQueryId) && mQueryId.equals(queryId)) {
            return;
        }
        mQueryId = queryId;
        members.clear();
        mAdapter.clear();
        loadingQueryItem();
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
        // 找到当前打开的小组
        Squad squad = new Dao<>(Squad.class).query(mQueryId);
        switch (view.getId()) {
            case R.id.ui_tool_popup_menu_squad_contact_organization:
                // 打开组织通讯录并尝试将里面的用户邀请到小组
                openActivity(OrganizationContactFragment.class.getName(), format("%s,%s", squad.getGroupId(), squad.getId()), true, false);
                break;
            case R.id.ui_tool_popup_menu_squad_contact_phone:
                // 打开手机通讯录，并尝试将用户拉进小组
                openActivity(PhoneContactFragment.class.getName(), format("%s,%s", squad.getGroupId(), squad.getId()), true, false);
                break;
        }
    }

    /**
     * 打开手机通讯录并添加成员到当前组织
     */
    public void addMemberToOrganizationFromPhoneContact(View view) {
        if (showType != TYPE_ORG) return;
        if(StringHelper.isEmpty(mQueryId)){
            ToastHelper.make().showMsg(R.string.ui_organization_structure_no_group_exist);
            return;
        }
        openActivity(PhoneContactFragment.class.getName(), format("%s,", mQueryId), true, false);
    }

    @Override
    protected boolean shouldSetDefaultTitleEvents() {
        // 小组才显示标题栏，组织通讯录不需要
        return showType == TYPE_SQUAD;
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
        loadingQueryItem();
    }

    /**
     * 加载查询的对象
     */
    private void loadingQueryItem() {
        switch (showType) {
            case TYPE_ORG:
                loadingLocalMembers(mQueryId, "");
                break;
            case TYPE_SQUAD:
                loadingSquad();
                break;
        }
    }

    private void loadingSquad() {
        Squad squad = new Dao<>(Squad.class).query(mQueryId);
        if (null == squad) {
            fetchingRemoteSquad(mSquadId);
        } else {
            setCustomTitle(squad.getName());
            loadingLocalMembers(squad.getGroupId(), squad.getId());
        }
    }

    @Override
    protected void onFetchingRemoteSquadComplete(Squad squad) {
        if (null != squad && !StringHelper.isEmpty(squad.getId())) {
            setCustomTitle(squad.getName());
            loadingLocalMembers(squad.getGroupId(), squad.getId());
        }
    }

    @Override
    protected void onLoadingLocalMembersComplete(String organizationId, String squadId, List<Member> list) {
        if (null != list && list.size() > 0) {
            members.addAll(list);
        }
        mAdapter.addAll(members);
        // 拉取远程成员列表
        fetchingRemoteMembers(organizationId, squadId);
    }

    @Override
    protected void onFetchingRemoteMembersComplete(List<Member> list) {
        if (null != list && list.size() > 0) {
            for (Member member : list) {
                if (!members.contains(member)) {
                    members.add(member);
                }
            }
            Collections.sort(members, new MemberComparator());
            searchingListener.onSearching("");
        }
    }

    /**
     * 根据加入时间排序
     */
    private class MemberComparator implements Comparator<Member> {
        @Override
        public int compare(Member u1, Member u2) {
            return u1.getCreateDate().compareTo(u2.getCreateDate());
        }
    }

    private String searchingText = "";
    private SearchableViewHolder.OnSearchingListener searchingListener = new SearchableViewHolder.OnSearchingListener() {
        @Override
        public void onSearching(String text) {
            if (!StringHelper.isEmpty(text)) {
                searching(text);
            } else {
                searchingText = "";
                mAdapter.addAll(members);
            }
        }
    };

    private void searching(String text) {
        searchingText = text;
        mAdapter.clear();
        for (Member member : members) {
            // 根据姓名和手机号码模糊查询
            if (member.getUserName().contains(text) || member.getPhone().contains(text)) {
                mAdapter.add(member);
            }
        }
    }

    private ContactViewHolder.OnUserDeleteListener onUserDeleteListener = new ContactViewHolder.OnUserDeleteListener() {
        @Override
        public void onDelete(ContactViewHolder holder) {
            mAdapter.delete(holder);
        }
    };

    private class ContactAdapter extends RecyclerSwipeAdapter<ContactViewHolder> {

        private ArrayList<Member> list = new ArrayList<>();

        private void delete(ContactViewHolder holder) {
            mItemManger.removeShownLayouts(holder.getSwipeLayout());
            int pos = holder.getAdapterPosition();
            remove(pos);
            mItemManger.closeAllItems();
        }

        private void addAll(List<Member> all) {
            if (null == all || all.size() < 1) return;

            for (Member member : all) {
                add(member);
            }
            Collections.sort(mAdapter.list, new MemberComparator());
            notifyItemRangeChanged(0, list.size());
        }

        private void add(Member member) {
            if (list.indexOf(member) < 0) {
                list.add(member);
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

        private void delete(Member member) {
            int index = list.indexOf(member);
            if (index >= 0) {
                remove(index);
            }
        }

        private int getFirstCharCount(char chr) {
            int ret = 0;
            for (Member member : list) {
                if (member.getSpell().charAt(0) == chr) {
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
                //String textLine = members.get(position).getSpell().substring(0, 1);
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
            //String text = members.get(position).getSpell().substring(0, 1);
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
