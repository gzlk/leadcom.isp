package com.leadcom.android.isp.fragment.organization;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.View;

import com.leadcom.android.isp.R;
import com.leadcom.android.isp.adapter.RecyclerViewAdapter;
import com.leadcom.android.isp.api.listener.OnMultipleRequestListener;
import com.leadcom.android.isp.api.listener.OnSingleRequestListener;
import com.leadcom.android.isp.api.org.NatureRequest;
import com.leadcom.android.isp.etc.Utils;
import com.leadcom.android.isp.fragment.base.BaseSwipeRefreshSupportFragment;
import com.leadcom.android.isp.helper.StringHelper;
import com.leadcom.android.isp.helper.ToastHelper;
import com.leadcom.android.isp.helper.popup.DateTimeHelper;
import com.leadcom.android.isp.holder.BaseViewHolder;
import com.leadcom.android.isp.holder.common.LabelViewHolder;
import com.leadcom.android.isp.holder.common.NothingMoreViewHolder;
import com.leadcom.android.isp.holder.common.SimpleClickableViewHolder;
import com.leadcom.android.isp.holder.common.TextViewHolder;
import com.leadcom.android.isp.listener.OnViewHolderClickListener;
import com.leadcom.android.isp.model.Model;
import com.leadcom.android.isp.model.organization.MemberClassify;
import com.leadcom.android.isp.model.organization.MemberNature;
import com.leadcom.android.isp.model.organization.SimpleNature;

import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * <b>功能描述：</b>文本类成员属性列表<br />
 * <b>创建作者：</b>SYSTEM <br />
 * <b>创建时间：</b>2018/07/29 23:52 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>version: 1.0.0 <br />
 * <b>修改时间：</b>2017/10/04 18:50 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */
public class MemberNatureFragment extends BaseSwipeRefreshSupportFragment {

    private static final String PARAM_TYPE = "mnf_type";
    private static final String PARAM_CHOOSE = "mnf_choose";
    private static final String PARAM_USERID = "mnf_user_id";

    public static MemberNatureFragment getInstance(String groupId, String type, boolean choose, String userId) {
        Bundle bundle = new Bundle();
        bundle.putString(PARAM_QUERY_ID, groupId);
        bundle.putString(PARAM_TYPE, type);
        bundle.putBoolean(PARAM_CHOOSE, choose);
        bundle.putString(PARAM_USERID, userId);
        MemberNatureFragment mntf = new MemberNatureFragment();
        mntf.setArguments(bundle);
        return mntf;
    }

    private NatureAdapter mAdapter;
    private String mType, mUserId;
    private boolean mChoose;
    private ArrayList<SimpleClickableViewHolder> clickableViewHolders = new ArrayList<>();

    @Override
    protected void getParamsFromBundle(Bundle bundle) {
        super.getParamsFromBundle(bundle);
        mType = bundle.getString(PARAM_TYPE, MemberNature.NatureType.TEXT);
        mChoose = bundle.getBoolean(PARAM_CHOOSE, false);
        mUserId = bundle.getString(PARAM_USERID, "");
    }

    @Override
    protected void saveParamsToBundle(Bundle bundle) {
        super.saveParamsToBundle(bundle);
        bundle.putString(PARAM_TYPE, mType);
        bundle.putBoolean(PARAM_CHOOSE, mChoose);
        bundle.putString(PARAM_USERID, mUserId);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        layoutType = TYPE_FLEX;
        super.onActivityCreated(savedInstanceState);
        isLoadingComplete(true);
    }

    @Override
    protected void onSwipeRefreshing() {
        loadingNatures();
    }

    @Override
    protected void onLoadingMore() {

    }

    @Override
    protected String getLocalPageTag() {
        return null;
    }

    @Override
    protected void onDelayRefreshComplete(int type) {

    }

    @Override
    public void doingInResume() {
        initializeAdapter();
    }

    @Override
    protected boolean shouldSetDefaultTitleEvents() {
        return false;
    }

    @Override
    protected void destroyView() {

    }

    /**
     * 更新用户的信息
     */
    public ArrayList<SimpleNature> updateNatures() {

        ArrayList<SimpleNature> natures = new ArrayList<>();
        if (!mChoose) return natures;

        Iterator<Model> iterator = mAdapter.iterator();
        while (iterator.hasNext()) {
            Model model = iterator.next();
            if (model instanceof MemberNature) {
                MemberNature nature = (MemberNature) model;
                if (nature.isTime() && !isEmpty(nature.getValue())) {
                    nature.setTemplateId(nature.getId());
                    natures.add(new SimpleNature(nature));
                } else if (nature.isSelected()) {
                    nature.setTemplateId(nature.getId());
                    nature.setValue(nature.getName());
                    natures.add(new SimpleNature(nature));
                }
            }
        }
        return natures;
    }

    private void loadingNatures() {
        setLoadingText(R.string.ui_group_member_nature_more_loading);
        displayLoading(true);
        NatureRequest.request().setOnMultipleRequestListener(new OnMultipleRequestListener<MemberClassify>() {
            @Override
            public void onResponse(List<MemberClassify> list, boolean success, int totalPages, int pageSize, int total, int pageNumber) {
                super.onResponse(list, success, totalPages, pageSize, total, pageNumber);
                if (success && null != list) {
                    int count = 0;
                    for (MemberClassify classify : list) {
                        if (isEmpty(classify.getId())) {
                            classify.setId(classify.getName());
                        }
                        mAdapter.add(classify);
                        for (MemberNature nature : classify.getAppUserNatureTemplateList()) {
                            if (isEmpty(nature.getId())) {
                                nature.setId(nature.getName());
                            }
                            nature.setSelected(nature.isChoose());
                            nature.setParentId(classify.getId());
                            mAdapter.add(nature);
                        }
                        if (!mChoose) {
                            count++;
                            Model model = new Model();
                            model.setId(format("line%d", count));
                            mAdapter.add(model);
                        }
                    }
                    // 加入最后一条padding
                    Model last = new Model();
                    last.setId("last");
                    mAdapter.add(last);
                }
                displayLoading(false);
                stopRefreshing();
            }
        }).listBy(mQueryId, mUserId, mType);
    }

    private void initializeAdapter() {
        if (null == mAdapter) {
            mAdapter = new NatureAdapter();
            mRecyclerView.setAdapter(mAdapter);
            loadingNatures();
        }
    }

    private OnViewHolderClickListener clickListener = new OnViewHolderClickListener() {
        @Override
        public void onClick(int index) {
            Model model = mAdapter.get(index);
            if (model instanceof MemberNature) {
                MemberNature nature = (MemberNature) model;
                if (!mChoose) {
                    // 如果不是用户属性修改，则打开对应属性的用户列表
                    NatureMemberListFragment.open(MemberNatureFragment.this, mQueryId, nature);
                    return;
                }
                if (nature.isSelected()) {
                    // 选中之后不能取消选中
                    return;
                }
                nature.setSelected(true);
                nature.setChoose(nature.isSelected());
                mAdapter.update(nature);
                Iterator<Model> iterator = mAdapter.iterator();
                while (iterator.hasNext()) {
                    Model m = iterator.next();
                    if (m instanceof MemberClassify && m.getId().equals(nature.getParentId())) {
                        MemberClassify classify = (MemberClassify) m;
                        for (MemberNature na : classify.getAppUserNatureTemplateList()) {
                            if (na.isSelected() && !na.getId().equals(nature.getId())) {
                                na.setSelected(false);
                                na.setChoose(false);
                                mAdapter.update(na);
                            }
                        }
                    }
                }
            }
        }
    };

    private OnViewHolderClickListener timeHolderClick = new OnViewHolderClickListener() {
        @Override
        public void onClick(final int index) {
            MemberNature nature = (MemberNature) mAdapter.get(index);
            String value = isEmpty(nature.getValue()) ? "" : (nature.getValue() + " 00:00:01");
            DateTimeHelper.helper().setOnDateTimePickListener(new DateTimeHelper.OnDateTimePickListener() {
                @Override
                public void onPicked(Date date) {
                    MemberNature check = (MemberNature) mAdapter.get(index);
                    for (SimpleClickableViewHolder holder : clickableViewHolders) {
                        if (holder.getTag().equals(check)) {
                            String fullTime = Utils.format(StringHelper.getString(R.string.ui_base_text_date_time_format), date);
                            check.setValue(fullTime.substring(0, 10));
                            mAdapter.update(check);
                        }
                    }
                }
            }).show(MemberNatureFragment.this, true, true, true, true, value);
        }
    };

    private class NatureAdapter extends RecyclerViewAdapter<BaseViewHolder, Model> {

        private static final int VT_TITLE = 0, VT_CONTENT = 1, VT_LINE = 2, VT_INPUT = 3, VT_LAST = 4;

        @Override
        public BaseViewHolder onCreateViewHolder(View itemView, int viewType) {
            switch (viewType) {
                case VT_TITLE:
                case VT_LAST:
                    TextViewHolder holder = new TextViewHolder(itemView, MemberNatureFragment.this);
                    holder.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
                    holder.showBottomLine(false);
                    return holder;
                case VT_CONTENT:
                    LabelViewHolder lvh = new LabelViewHolder(itemView, MemberNatureFragment.this);
                    lvh.addOnViewHolderClickListener(clickListener);
                    return lvh;
                case VT_LINE:
                    return new NothingMoreViewHolder(itemView, MemberNatureFragment.this);
                case VT_INPUT:
                    SimpleClickableViewHolder scvh = new SimpleClickableViewHolder(itemView, MemberNatureFragment.this);
                    scvh.addOnViewHolderClickListener(timeHolderClick);
                    clickableViewHolders.add(scvh);
                    return scvh;
            }
            return null;
        }

        @Override
        public int itemLayout(int viewType) {
            switch (viewType) {
                case VT_TITLE:
                case VT_LAST:
                    return R.layout.holder_view_text_olny;
                case VT_CONTENT:
                    return R.layout.holder_view_activity_label;
                case VT_LINE:
                    return R.layout.tool_view_half_line_horizontal;
                case VT_INPUT:
                    return R.layout.holder_view_simple_clickable;
            }
            return 0;
        }

        @Override
        public int getItemViewType(int position) {
            Model model = get(position);
            if (model.getId().contains("line"))
                return VT_LINE;
            if (model.getId().contains("last"))
                return VT_LAST;
            if (model instanceof MemberClassify)
                return VT_TITLE;

            MemberNature nature = (MemberNature) model;
            return (mChoose && nature.isTime()) ? VT_INPUT : VT_CONTENT;
        }

        @Override
        public void onBindHolderOfView(BaseViewHolder holder, int position, @Nullable Model item) {
            if (holder instanceof TextViewHolder) {
                if (item instanceof MemberClassify) {
                    MemberClassify classify = (MemberClassify) item;
                    String text = "<b>" + classify.getName() + "</b>";
                    ((TextViewHolder) holder).showContent(text);
                } else {
                    ((TextViewHolder) holder).showContent("");
                }
            } else if (holder instanceof LabelViewHolder) {
                ((LabelViewHolder) holder).showContent((MemberNature) item, mChoose);
            } else if (holder instanceof SimpleClickableViewHolder) {
                MemberNature nature = (MemberNature) item;
                assert nature != null;
                holder.setTag(nature);
                String value = isEmpty(nature.getValue()) ? "" : formatDate(nature.getValue() + " 00:00:01");
                ((SimpleClickableViewHolder) holder).showContent(Integer.valueOf(nature.getId()), nature.getName() + "：", value);
            }
        }

        @Override
        protected int comparator(Model item1, Model item2) {
            return 0;
        }
    }
}
