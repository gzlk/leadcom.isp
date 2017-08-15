package com.gzlk.android.isp.fragment.activity.vote;

import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

import com.bigkoo.pickerview.OptionsPickerView;
import com.bigkoo.pickerview.TimePickerView;
import com.gzlk.android.isp.R;
import com.gzlk.android.isp.activity.BaseActivity;
import com.gzlk.android.isp.adapter.RecyclerViewAdapter;
import com.gzlk.android.isp.api.activity.AppVoteRequest;
import com.gzlk.android.isp.api.listener.OnSingleRequestListener;
import com.gzlk.android.isp.etc.Utils;
import com.gzlk.android.isp.fragment.base.BaseDownloadingUploadingSupportFragment;
import com.gzlk.android.isp.fragment.base.BaseFragment;
import com.gzlk.android.isp.helper.StringHelper;
import com.gzlk.android.isp.helper.ToastHelper;
import com.gzlk.android.isp.holder.BaseViewHolder;
import com.gzlk.android.isp.holder.activity.VoteOptionEditViewHolder;
import com.gzlk.android.isp.holder.common.SimpleClickableViewHolder;
import com.gzlk.android.isp.listener.OnTitleButtonClickListener;
import com.gzlk.android.isp.listener.OnViewHolderClickListener;
import com.gzlk.android.isp.model.activity.Activity;
import com.gzlk.android.isp.model.activity.vote.AppVote;
import com.gzlk.android.isp.model.activity.vote.AppVoteItem;
import com.hlk.hlklib.layoutmanager.CustomLinearLayoutManager;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.view.ClearEditText;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;

/**
 * <b>功能描述：</b>创建投票<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/06/29 00:31 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/06/29 00:31 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class VoteCreatorFragment extends BaseDownloadingUploadingSupportFragment {

    private static final String PARAM_POJO = "vcf_param_pojo";
    private static final String PARAM_TYPE = "vcf_param_type";

    public static VoteCreatorFragment newInstance(String params) {
        VoteCreatorFragment vcf = new VoteCreatorFragment();
        Bundle bundle = new Bundle();
        // tid
        bundle.putString(PARAM_QUERY_ID, params);
        vcf.setArguments(bundle);
        return vcf;
    }

    public static void open(BaseFragment fragment, String tid) {
        fragment.openActivity(VoteListFragment.class.getName(), tid, true, false);
    }

    public static void open(Context context, int requestCode, String tid) {
        BaseActivity.openActivity(context, VoteCreatorFragment.class.getName(), tid, requestCode, true, false);
    }

    /**
     * 最后选择的类型
     */
    private int lstType = AppVote.Type.SINGLE;

    @Override
    protected void getParamsFromBundle(Bundle bundle) {
        super.getParamsFromBundle(bundle);
        lstType = bundle.getInt(PARAM_TYPE, AppVote.Type.SINGLE);
        String json = bundle.getString(PARAM_POJO, "");
        if (!isEmpty(json)) {
            mAppVote = AppVote.fromJson(json);
        }
    }

    @Override
    protected void saveParamsToBundle(Bundle bundle) {
        super.saveParamsToBundle(bundle);
        bundle.putInt(PARAM_TYPE, lstType);
        //mAppVote.setTitle(titleHolder.getValue());
        mAppVote.setTitle(contentView.getValue());
        bundle.putString(PARAM_POJO, AppVote.toJson(mAppVote));
    }

    //    @ViewId(R.id.ui_activity_vote_creator_title)
//    private View titleView;
    @ViewId(R.id.ui_activity_vote_creator_content)
    private ClearEditText contentView;
    @ViewId(R.id.ui_tool_swipe_refreshable_recycler_view)
    private RecyclerView optionsView;
    @ViewId(R.id.ui_activity_vote_creator_type)
    private View typeView;
    @ViewId(R.id.ui_activity_vote_creator_max)
    private View maxView;
    @ViewId(R.id.ui_activity_vote_creator_end)
    private View timeView;
    @ViewId(R.id.ui_activity_vote_creator_notify)
    private View notifyView;

    //private SimpleInputableViewHolder titleHolder;
    private SimpleClickableViewHolder typeHolder, maxHolder, timeHolder, notifyHolder;

    private String[] items, types, dftOptions, ntfOptions;
    private AppVote mAppVote;
    private VoteItemAdapter mAdapter;

    @Override
    public int getLayout() {
        return R.layout.fragment_activity_vote_creator;
    }

    @Override
    public void doingInResume() {
        setCustomTitle(R.string.ui_activity_vote_creator_fragment_title);
        setRightText(R.string.ui_base_text_publish);
        setRightTitleClickListener(new OnTitleButtonClickListener() {
            @Override
            public void onClick() {
                tryPublishVote();
            }
        });
        initializeHolders();
        initializeAdapter();
    }

    @Override
    protected boolean shouldSetDefaultTitleEvents() {
        return true;
    }

    @Override
    protected void destroyView() {

    }

    private void fetchingActivity() {
        if (isEmpty(mAppVote.getActId())) {
            Activity act = Activity.getByTid(mQueryId);
            if (null != act) {
                mAppVote.setActId(act.getId());
            }
        }
    }

    private void tryPublishVote() {
        fetchingActivity();
        if (isEmpty(mAppVote.getActId())) {
            ToastHelper.make().showMsg(R.string.ui_activity_vote_creator_invalid_activity);
            return;
        }
//        mAppVote.setTitle(titleHolder.getValue());
//        if (isEmpty(mAppVote.getTitle())) {
//            ToastHelper.make().showMsg(R.string.ui_activity_vote_creator_title_invalid);
//            return;
//        }
        mAppVote.setTitle(contentView.getValue());
        if (isEmpty(mAppVote.getTitle())) {
            ToastHelper.make().showMsg(R.string.ui_activity_vote_creator_desc_invalid);
            return;
        }
        if (!checkOptions()) {
            ToastHelper.make().showMsg(R.string.ui_activity_vote_creator_options_invalid);
            return;
        }
        publishVote();
    }

    private boolean checkOptions() {
        if (null == mAppVote.getItemContentList()) {
            mAppVote.setItemContentList(new ArrayList<String>());
        }
        mAppVote.getItemContentList().clear();
        for (int i = 0, size = mAdapter.getItemCount(); i < size; i++) {
            AppVoteItem item = mAdapter.get(i);
            if (item.getId().equals("+")) {
                continue;
            }
            if (isEmpty(item.getContent())) {
                return false;
            } else {
                mAppVote.getItemContentList().add(item.getContent());
            }
        }
        return true;
    }

    private void publishVote() {
        showImageHandlingDialog(R.string.ui_activity_vote_creator_publishing);
        AppVoteRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<AppVote>() {
            @Override
            public void onResponse(AppVote appVote, boolean success, String message) {
                super.onResponse(appVote, success, message);
                hideImageHandlingDialog();
                if (success) {
                    mAppVote = appVote;
                    resultData(AppVote.toJson(mAppVote));
                }
            }
        }).add(mAppVote);
    }

    private void initializeHolders() {
        if (null == items) {
            items = StringHelper.getStringArray(R.array.ui_activity_vote_creator_items);
            optionsView.setLayoutManager(new CustomLinearLayoutManager(optionsView.getContext()));
            optionsView.setNestedScrollingEnabled(false);
        }
        if (null == types) {
            types = StringHelper.getStringArray(R.array.ui_activity_vote_types);
        }
        if (null == mAppVote) {
            mAppVote = new AppVote();
            mAppVote.setItemContentList(new ArrayList<String>());
            // 默认最大选择项目为1，也即单选投票
            mAppVote.setMaxSelectable(1);
        }
//        if (null == titleHolder) {
//            titleHolder = new SimpleInputableViewHolder(titleView, this);
//        }
//        titleHolder.showContent(format(items[0], isEmpty(mAppVote.getTitle()) ? "" : mAppVote.getTitle()));
        if (null == typeHolder) {
            typeHolder = new SimpleClickableViewHolder(typeView, this);
            typeHolder.addOnViewHolderClickListener(onViewHolderClickListener);
        }
        typeHolder.showContent(format(items[1], types[lstType]));
        if (null == maxHolder) {
            maxHolder = new SimpleClickableViewHolder(maxView, this);
            maxHolder.addOnViewHolderClickListener(onViewHolderClickListener);
        }
        maxHolder.showContent(format(items[2], getString(R.string.ui_activity_vote_creator_max_selectable, mAppVote.getMaxSelectable())));

        if (null == timeHolder) {
            timeHolder = new SimpleClickableViewHolder(timeView, this);
            timeHolder.addOnViewHolderClickListener(onViewHolderClickListener);
        }
        timeHolder.showContent(format(items[3], formatDateTime(mAppVote.getEndDate())));
        if (null == notifyHolder) {
            notifyHolder = new SimpleClickableViewHolder(notifyView, this);
            notifyHolder.addOnViewHolderClickListener(onViewHolderClickListener);
        }
        notifyHolder.showContent(format(items[4], getNotifyBeginTime()));

        contentView.setValue(isEmpty(mAppVote.getTitle()) ? "" : mAppVote.getTitle());
    }

    private String getNotifyBeginTime() {
        if (null == ntfOptions) {
            ntfOptions = StringHelper.getStringArray(R.array.ui_activity_vote_notify_time);
        }
        switch (mAppVote.getNotifyBeginTime()) {
            case 0:
                return ntfOptions[4];
            case 30:
                return ntfOptions[0];
            case 60:
                return ntfOptions[1];
            case 60 * 12:
                return ntfOptions[2];
            case 60 * 24:
                return ntfOptions[3];
            default:
                return ntfOptions[4];
        }
    }

    private OnViewHolderClickListener onViewHolderClickListener = new OnViewHolderClickListener() {
        @Override
        public void onClick(int index) {
            mAppVote.setTitle(contentView.getValue());
            //mAppVote.setContent(contentView.getValue());
            switch (index) {
                case 0:
                    // 投票类型
                    openTypePicker();
                    break;
                case 1:
                    // 最多可选择
                    openMaxSelectablePicker();
                    break;
                case 2:
                    // 结束时间
                    openDateTimePickerEnd();
                    break;
                case 3:
                    // 提醒
                    openOptionsPicker();
                    break;
            }
        }
    };

    private OptionsPickerView voteTypePickerView;
    private ArrayList<String> voteTypes;

    @SuppressWarnings("unchecked")
    private void openTypePicker() {
        Utils.hidingInputBoard(contentView);
        if (null == voteTypes) {
            voteTypes = new ArrayList<>();
            voteTypes.addAll(Arrays.asList(types));
            //voteTypes.add(types[1]);
            //voteTypes.add(types[2]);
        }
        if (null == voteTypePickerView) {
            voteTypePickerView = new OptionsPickerView.Builder(Activity(), new OptionsPickerView.OnOptionsSelectListener() {
                @Override
                public void onOptionsSelect(int i, int i1, int i2, View view) {
                    lstType = i;
                    mAppVote.setType(lstType);
                    if (lstType == AppVote.Type.UNLIMITED) {
                        mAppVote.setMaxSelectable(mAdapter.getItemCount() - 1);
                    }
                    initializeHolders();
                }
            }).setTitleBgColor(getColor(R.color.colorPrimary))
                    .setSubmitColor(Color.WHITE)
                    .setCancelColor(Color.WHITE)
                    .setContentTextSize(getFontDimension(R.dimen.ui_base_text_size))
                    .setOutSideCancelable(true).setSelectOptions(0)
                    .isCenterLabel(true).isDialog(false).setLabels("", "", "").build();
            voteTypePickerView.setPicker(voteTypes);
        }
        voteTypePickerView.show();
    }

    // 多选时最多可选择的项目数量
    private OptionsPickerView voteMaxSelectablePickerView;
    private ArrayList<String> maxSelectable = new ArrayList<>();

    // 根据当前增加的选项数量
    @SuppressWarnings("unchecked")
    private void resetMaxSelectable() {
        // 按照增加的选项数量动态增加最大可选择数量
        maxSelectable.clear();
        for (int i = 0, size = mAdapter.getItemCount(); i < size - 1; i++) {
            maxSelectable.add(String.valueOf(1 + i));
        }
        if (null != voteMaxSelectablePickerView) {
            voteMaxSelectablePickerView.setPicker(maxSelectable);
            int max = maxSelectable.indexOf(String.valueOf(mAppVote.getMaxSelectable()));
            if (max < 0) {
                max = 0;
                mAppVote.setMaxSelectable(Integer.valueOf(maxSelectable.get(max)));
                maxHolder.showContent(format(items[2], getString(R.string.ui_activity_vote_creator_max_selectable, mAppVote.getMaxSelectable())));
            }
            // 更改选项之后默认设置最大选择记录
            voteMaxSelectablePickerView.setSelectOptions(max);
        }
    }

    @SuppressWarnings("unchecked")
    private void openMaxSelectablePicker() {
        Utils.hidingInputBoard(contentView);
        if (null == maxSelectable) {
            maxSelectable = new ArrayList<>();
            resetMaxSelectable();
        }
        if (null == voteMaxSelectablePickerView) {
            voteMaxSelectablePickerView = new OptionsPickerView.Builder(Activity(), new OptionsPickerView.OnOptionsSelectListener() {
                @Override
                public void onOptionsSelect(int i, int i1, int i2, View view) {
                    mAppVote.setMaxSelectable(i + 1);
                    initializeHolders();
                }
            }).setTitleBgColor(getColor(R.color.colorPrimary))
                    .setSubmitColor(Color.WHITE)
                    .setCancelColor(Color.WHITE)
                    .setContentTextSize(getFontDimension(R.dimen.ui_base_text_size))
                    .setOutSideCancelable(true).setSelectOptions(0)
                    .isCenterLabel(true).isDialog(false).setLabels("个选项", "", "").build();
            voteMaxSelectablePickerView.setPicker(maxSelectable);
        }
        voteMaxSelectablePickerView.show();
    }

    private TimePickerView tpvEnd;

    private void openDateTimePickerEnd() {
        Utils.hidingInputBoard(contentView);
        if (null == tpvEnd) {
            tpvEnd = new TimePickerView.Builder(Activity(), new TimePickerView.OnTimeSelectListener() {
                @Override
                public void onTimeSelect(Date date, View v) {
                    String string = Utils.format(StringHelper.getString(R.string.ui_base_text_date_time_format), date);
                    mAppVote.setEndDate(string);
                    initializeHolders();
                }
            }).setType(new boolean[]{true, true, true, true, true, false})
                    .setTitleBgColor(getColor(R.color.colorPrimary))
                    .setSubmitColor(Color.WHITE)
                    .setCancelColor(Color.WHITE)
                    .setContentSize(getFontDimension(R.dimen.ui_base_text_size))
                    .setOutSideCancelable(false)
                    .isCenterLabel(true).isDialog(false).build();
            if (isEmpty(mAppVote.getEndDate())) {
                tpvEnd.setDate(Calendar.getInstance());
                String string = Utils.format(StringHelper.getString(R.string.ui_base_text_date_time_format), Calendar.getInstance().getTime());
                mAppVote.setEndDate(string);
            } else {
                Calendar calendar = Calendar.getInstance();
                calendar.setTime(Utils.parseDate(StringHelper.getString(R.string.ui_base_text_date_time_format), mAppVote.getEndDate()));
                tpvEnd.setDate(calendar);
            }
        }
        tpvEnd.show();
    }

    private OptionsPickerView optionsPickerView;
    private ArrayList<String> options;

    private int getNotifyTime(int index) {
        switch (index) {
            case 0:
                return 30;
            case 1:
                return 60;
            case 2:
                return 12 * 60;
            case 3:
                return 24 * 60;
            default:
                return 0;
        }
    }

    @SuppressWarnings("unchecked")
    private void openOptionsPicker() {
        if (null == ntfOptions) {
            ntfOptions = StringHelper.getStringArray(R.array.ui_activity_vote_notify_time);
        }
        if (null == options) {
            options = new ArrayList<>();
            options.addAll(Arrays.asList(ntfOptions));
        }
        if (null == optionsPickerView) {
            optionsPickerView = new OptionsPickerView.Builder(Activity(), new OptionsPickerView.OnOptionsSelectListener() {
                @Override
                public void onOptionsSelect(int i, int i1, int i2, View view) {
                    mAppVote.setNotifyBeginTime(getNotifyTime(i));
                    initializeHolders();
                }
            }).setTitleBgColor(getColor(R.color.colorPrimary))
                    .setSubmitColor(Color.WHITE)
                    .setCancelColor(Color.WHITE)
                    .setContentTextSize(getFontDimension(R.dimen.ui_base_text_size))
                    .setOutSideCancelable(true).setSelectOptions(4)
                    .isCenterLabel(true).isDialog(false).setLabels("", "", "").build();
            optionsPickerView.setPicker(options);
        }
        optionsPickerView.show();
    }

    private void initializeAdapter() {
        if (null == mAdapter) {
            mAdapter = new VoteItemAdapter();
            optionsView.setAdapter(mAdapter);
        }
        if (null == dftOptions) {
            dftOptions = StringHelper.getStringArray(R.array.ui_activity_vote_default_options);

            int size = mAdapter.getItemCount() + 1;
            AppVoteItem vi1 = new AppVoteItem();
            vi1.setId(String.valueOf(Utils.timestamp()));
            vi1.setLocalDeleted(true);
            vi1.setContent(format(dftOptions[0], size));
            mAdapter.add(vi1);

            size = mAdapter.getItemCount() + 1;
            AppVoteItem vi2 = new AppVoteItem();
            vi2.setId(String.valueOf(Utils.timestamp() + 1));
            vi2.setLocalDeleted(true);
            vi2.setContent(format(dftOptions[0], size));
            mAdapter.add(vi2);

            AppVoteItem viAdd = new AppVoteItem();
            viAdd.setId("+");
            viAdd.setSelectable(true);
            viAdd.setContent(dftOptions[1]);
            mAdapter.add(viAdd);
        }
    }

    private void resetAddItem() {
        AppVoteItem viAdd = new AppVoteItem();
        viAdd.setId("+");
        viAdd.setSelectable(true);
        viAdd.setContent(dftOptions[1]);
        if (!mAdapter.exist(viAdd)) {
            mAdapter.add(viAdd);
        }
    }

    private OnViewHolderClickListener optionViewHolderClickListener = new OnViewHolderClickListener() {
        @Override
        public void onClick(int index) {
            AppVoteItem item = mAdapter.get(index);
            if (item.getId().equals("+")) {
                int size = mAdapter.getItemCount();
                // 添加选项
                AppVoteItem vi = new AppVoteItem();
                vi.setId(String.valueOf(Utils.timestamp()));
                vi.setContent(format(dftOptions[0], size));
                if (size > 9) {
                    // 最多10个选项
                    mAdapter.remove(item);
                    mAdapter.add(vi);
                    setMaxSelectable(10);
                } else {
                    mAdapter.add(vi, size - 1);
                    setMaxSelectable(mAdapter.getItemCount() - 1);
                }
            } else {
                mAdapter.remove(item);
                resetAddItem();
                setMaxSelectable(mAdapter.getItemCount() - 1);
            }
            //resetMaxSelectable();
        }
    };

    private void setMaxSelectable(int max) {
        if (lstType == AppVote.Type.UNLIMITED) {
            mAppVote.setMaxSelectable(max);
        }
    }

    private BaseViewHolder.OnHandlerBoundDataListener<AppVoteItem> onHandlerBoundDataListener = new BaseViewHolder.OnHandlerBoundDataListener<AppVoteItem>() {
        @Override
        public AppVoteItem onHandlerBoundData(BaseViewHolder holder) {
            return mAdapter.get(holder.getAdapterPosition());
        }
    };

    private class VoteItemAdapter extends RecyclerViewAdapter<VoteOptionEditViewHolder, AppVoteItem> {
        @Override
        public VoteOptionEditViewHolder onCreateViewHolder(View itemView, int viewType) {
            VoteOptionEditViewHolder holder = new VoteOptionEditViewHolder(itemView, VoteCreatorFragment.this);
            holder.addOnViewHolderClickListener(optionViewHolderClickListener);
            holder.addOnHandlerBoundDataListener(onHandlerBoundDataListener);
            return holder;
        }

        @Override
        public int itemLayout(int viewType) {
            return R.layout.holder_view_vote_option_editable;
        }

        @Override
        public void onBindHolderOfView(VoteOptionEditViewHolder holder, int position, @Nullable AppVoteItem item) {
            holder.showContent(item);
        }

        @Override
        protected int comparator(AppVoteItem item1, AppVoteItem item2) {
            return 0;
        }
    }
}
