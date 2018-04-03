package com.leadcom.android.isp.fragment.main;

import android.animation.Animator;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bigkoo.pickerview.TimePickerView;
import com.bigkoo.pickerview.listener.CustomListener;
import com.hlk.hlklib.layoutmanager.CustomLinearLayoutManager;
import com.hlk.hlklib.lib.inject.Click;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.view.CustomTextView;
import com.leadcom.android.isp.R;
import com.leadcom.android.isp.adapter.RecyclerViewAdapter;
import com.leadcom.android.isp.api.archive.ArchiveRequest;
import com.leadcom.android.isp.api.archive.DictionaryRequest;
import com.leadcom.android.isp.api.archive.RecommendArchiveRequest;
import com.leadcom.android.isp.api.listener.OnMultipleRequestListener;
import com.leadcom.android.isp.etc.Utils;
import com.leadcom.android.isp.fragment.archive.ArchiveDetailsWebViewFragment;
import com.leadcom.android.isp.fragment.base.BaseFragment;
import com.leadcom.android.isp.fragment.base.BaseSwipeRefreshSupportFragment;
import com.leadcom.android.isp.helper.ToastHelper;
import com.leadcom.android.isp.holder.BaseViewHolder;
import com.leadcom.android.isp.holder.common.InputableSearchViewHolder;
import com.leadcom.android.isp.holder.common.NothingMoreViewHolder;
import com.leadcom.android.isp.holder.common.TextViewHolder;
import com.leadcom.android.isp.holder.home.ArchiveHomeRecommendedViewHolder;
import com.leadcom.android.isp.listener.OnViewHolderClickListener;
import com.leadcom.android.isp.model.Model;
import com.leadcom.android.isp.model.archive.Archive;
import com.leadcom.android.isp.model.archive.Dictionary;
import com.leadcom.android.isp.model.archive.RecommendArchive;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * <b>功能描述：</b>档案搜索页面<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2018/03/20 09:34 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2018/03/20 09:34 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class ArchiveSearchFragment extends BaseSwipeRefreshSupportFragment {

    private static final String PARAM_TYPE = "param_searching_type";
    private static final String PARAM_TEXT = "param_searching_text";

    public static ArchiveSearchFragment newInstance(String params) {
        ArchiveSearchFragment asf = new ArchiveSearchFragment();
        String[] strings = splitParameters(params);
        Bundle bundle = new Bundle();
        // 搜索方式
        bundle.putInt(PARAM_TYPE, Integer.valueOf(strings[0]));
        // 传过来的组织id或者用户的id
        bundle.putString(PARAM_QUERY_ID, strings[1]);
        // 搜索的文本
        bundle.putString(PARAM_TEXT, strings[2]);
        asf.setArguments(bundle);
        return asf;
    }

    public static void open(BaseFragment fragment, int searchType, String searchId, String searchText) {
        fragment.openActivity(ArchiveSearchFragment.class.getName(), format("%d,%s,%s", searchType, searchId, searchText), false, false);
    }

    @ViewId(R.id.ui_holder_view_searchable_container)
    private View searchableView;
    @ViewId(R.id.ui_main_archive_search_content_background)
    private RelativeLayout selectorBg;
    @ViewId(R.id.ui_main_archive_search_functions)
    private LinearLayout functionView;
    @ViewId(R.id.ui_main_archive_search_functions_1_text)
    private TextView function1;
    @ViewId(R.id.ui_main_archive_search_functions_2_text)
    private TextView function2;
    @ViewId(R.id.ui_main_archive_search_functions_3_text)
    private TextView function3;
    @ViewId(R.id.ui_main_archive_search_functions_1_icon)
    private CustomTextView allow1;
    @ViewId(R.id.ui_main_archive_search_functions_2_icon)
    private CustomTextView allow2;
    @ViewId(R.id.ui_main_archive_search_functions_3_icon)
    private CustomTextView allow3;
    @ViewId(R.id.ui_main_archive_search_time_picker)
    private FrameLayout timePickerContainer;
    @ViewId(R.id.ui_main_archive_search_type_picker)
    private RelativeLayout typePickerContainer;
    @ViewId(R.id.ui_main_archive_search_type_list)
    private RecyclerView typeList;
    private TimePickerView timePickerView;
    private TypeAdapter tAdapter;
    private ArchiveAdapter mAdapter;
    private ArrayList<Dictionary> dictionaries = new ArrayList<>();
    private ArrayList<Archive> searched = new ArrayList<>();

    /**
     * 当前选择方式
     */
    private static final int FUNC_NONE = -1, FUNC_TIME = 0, FUNC_NATURE = 1, FUNC_TYPE = 2;
    private static int selectedFunction = FUNC_NONE;
    /**
     * 搜索对象：首页、组织内部、个人
     */
    public static final int SEARCH_HOME = 0, SEARCH_GROUP = 1, SEARCH_USER = 2;
    /**
     * 当前搜索方式
     */
    private int searchingFunction = SEARCH_HOME;
    private static String searchingMonth = "", searchingNature = "", searchingType = "";

    private String searchingText;

    @Override
    protected void getParamsFromBundle(Bundle bundle) {
        super.getParamsFromBundle(bundle);
        searchingFunction = bundle.getInt(PARAM_TYPE, SEARCH_HOME);
        searchingText = bundle.getString(PARAM_TEXT, "");
    }

    @Override
    protected void saveParamsToBundle(Bundle bundle) {
        super.saveParamsToBundle(bundle);
        bundle.putInt(PARAM_TYPE, searchingFunction);
        bundle.putString(PARAM_TEXT, searchingText);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        searchingText = "";
        searchingMonth = "";
        searchingNature = "";
        searchingType = "";
        searchingFunction = SEARCH_HOME;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        selectedFunction = FUNC_NONE;
        enableSwipe(false);
        isLoadingComplete(true);
        InputableSearchViewHolder searchViewHolder = new InputableSearchViewHolder(searchableView, this);
        //searchViewHolder.setBackground(getColor(R.color.colorPrimary));
        searchViewHolder.setOnSearchingListener(new InputableSearchViewHolder.OnSearchingListener() {
            @Override
            public void onSearching(String text) {
                if (!isEmpty(text)) {
                    searchingText = text;
                    searchingArchive();
                } else {
                    searchingText = "";
                }
            }
        });
        if (searchingFunction > SEARCH_HOME) {
            searchViewHolder.setInputHint(searchingFunction == SEARCH_GROUP ? R.string.ui_group_archive_fragment_title : R.string.ui_text_archive_list_fragment_title);
        } else {
            functionView.setVisibility(View.GONE);
        }
        initializeTimePickerView();
        initializePositions();
    }

    private void initializeTimePickerView() {
        timePickerView = new TimePickerView.Builder(Activity(), new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {
                searchingMonth = Utils.format("yyyy-MM", date);
                restoreSearchingResult();
            }
        }).setLayoutRes(R.layout.tool_view_custom_time_picker, new CustomListener() {
            @Override
            public void customLayout(View v) {
                v.findViewById(R.id.timepicker_cancel).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // 关闭时间选择器
                        hideSelector();
                        resetFunctionStatus();
                    }
                });
                v.findViewById(R.id.timepicker_confirm).setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        // 确定时间
                        timePickerView.returnData();
                    }
                });
            }
        }).setType(new boolean[]{true, true, false, false, false, false})
                .setDecorView(timePickerContainer)
                .setOutSideCancelable(false).setDividerColor(getColor(R.color.textColorHint))
                .setContentSize(getFontDimension(R.dimen.ui_base_text_size))
                .isCenterLabel(false)
                .build();
        timePickerView.setKeyBackCancelable(false);
    }

    private void initializePositions() {
        Handler().post(new Runnable() {
            @Override
            public void run() {
                timePickerView.show();
                hideSelector();
                resetFunctionStatus();
            }
        });
    }

    @Override
    protected void onDelayRefreshComplete(int type) {

    }

    @Override
    public void doingInResume() {
        initializeTypeAdapter();
        initializeArchiveAdapter();
    }

    @Override
    protected boolean shouldSetDefaultTitleEvents() {
        return false;
    }

    @Override
    protected void destroyView() {

    }

    @Override
    public int getLayout() {
        return R.layout.fragment_main_archive_search;
    }

    @Override
    protected void onSwipeRefreshing() {
        remotePageNumber = 1;
    }

    @Override
    protected void onLoadingMore() {

    }

    @Override
    protected String getLocalPageTag() {
        return null;
    }

    @Click({R.id.ui_main_archive_search_functions_1, R.id.ui_main_archive_search_functions_2,
            R.id.ui_main_archive_search_functions_3, R.id.ui_main_archive_search_content_background})
    private void viewClick(View view) {
        switch (view.getId()) {
            case R.id.ui_main_archive_search_content_background:
                hideSelector();
                resetFunctionStatus();
                break;
            case R.id.ui_main_archive_search_functions_1:
                if (selectedFunction != FUNC_TIME) {
                    selectedFunction = FUNC_TIME;
                    showSelector();
                } else {
                    hideSelector();
                }
                resetFunctionStatus();
                break;
            case R.id.ui_main_archive_search_functions_2:
                if (selectedFunction != FUNC_NATURE) {
                    selectedFunction = FUNC_NATURE;
                    showSelector();
                } else {
                    hideSelector();
                }
                resetFunctionStatus();
                break;
            case R.id.ui_main_archive_search_functions_3:
                if (selectedFunction != FUNC_TYPE) {
                    selectedFunction = FUNC_TYPE;
                    showSelector();
                } else {
                    hideSelector();
                }
                resetFunctionStatus();
                break;
        }
    }

    private void showSelector() {
        showSelectorBackground(true);
        showTimePicker(selectedFunction == FUNC_TIME);
        showTypePicker(selectedFunction > FUNC_TIME);
        if (selectedFunction > FUNC_TIME) {
            resetTypeList();
        }
    }

    private void hideSelector() {
        showSelectorBackground(false);
        showTimePicker(false);
        showTypePicker(false);
        selectedFunction = FUNC_NONE;
    }

    private void showSelectorBackground(final boolean shown) {
        if (shown && selectorBg.getVisibility() == View.VISIBLE) return;
        if (!shown && selectorBg.getVisibility() == View.GONE) return;
        selectorBg.animate()
                .alpha(shown ? 1.0f : 0.0f)
                .setDuration(duration())
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        if (shown) {
                            selectorBg.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if (!shown) {
                            selectorBg.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                }).start();
    }

    private void showTimePicker(final boolean shown) {
        allow1.animate()
                .rotation(shown ? -90 : 90)
                .setDuration(duration())
                .start();
        timePickerContainer.animate()
                .translationY(shown ? 0 : -timePickerContainer.getMeasuredHeight() * 1.1f)
                .alpha(shown ? 1.0f : 0.0f).setDuration(duration())
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        if (shown) {
                            timePickerContainer.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if (!shown) {
                            timePickerContainer.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                }).start();
    }

    private void showTypePicker(final boolean shown) {
        allow2.animate().rotation(shown && selectedFunction == FUNC_NATURE ? -90 : 90)
                .setDuration(duration()).start();
        allow3.animate().rotation(shown && selectedFunction == FUNC_TYPE ? -90 : 90)
                .setDuration(duration()).start();
        typePickerContainer.animate()
                .translationY(shown ? 0 : -typePickerContainer.getMeasuredHeight() * 1.1f)
                .alpha(shown ? 1.0f : 0.0f).setDuration(duration())
                .setListener(new Animator.AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                        if (shown) {
                            typePickerContainer.setVisibility(View.VISIBLE);
                        }
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        if (!shown) {
                            typePickerContainer.setVisibility(View.GONE);
                        }
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {

                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {

                    }
                }).start();
    }

    private void resetFunctionStatus() {
        int color1 = getColor(R.color.colorPrimary), color2 = getColor(R.color.textColorHint);
        function1.setTextColor(selectedFunction == FUNC_TIME ? color1 : color2);
        allow1.setTextColor(selectedFunction == FUNC_TIME ? color1 : color2);

        function2.setTextColor(selectedFunction == FUNC_NATURE ? color1 : color2);
        allow2.setTextColor(selectedFunction == FUNC_NATURE ? color1 : color2);

        function3.setTextColor(selectedFunction == FUNC_TYPE ? color1 : color2);
        allow3.setTextColor(selectedFunction == FUNC_TYPE ? color1 : color2);
    }

    private void resetTypeList() {
        tAdapter.clear();
        Dictionary d = none();
        d.setTypeCode(selectedFunction == FUNC_NATURE ? Dictionary.Type.ARCHIVE_NATURE : Dictionary.Type.ARCHIVE_TYPE);
        tAdapter.add(d);
        for (Dictionary dictionary : dictionaries) {
            if (dictionary.getTypeCode().equals(selectedFunction == FUNC_NATURE ? Dictionary.Type.ARCHIVE_NATURE : Dictionary.Type.ARCHIVE_TYPE)) {
                tAdapter.add(dictionary);
            }
        }
        if (tAdapter.getItemCount() <= 1) {
            // 重新拉取
            fetchingDictionary();
        }
    }

    private void fetchingDictionary() {
        DictionaryRequest.request().setOnMultipleRequestListener(new OnMultipleRequestListener<Dictionary>() {
            @Override
            public void onResponse(List<Dictionary> list, boolean success, int totalPages, int pageSize, int total, int pageNumber) {
                super.onResponse(list, success, totalPages, pageSize, total, pageNumber);
                if (success && null != list) {
                    dictionaries.addAll(list);
                    resetTypeList();
                }
            }
        }).list(selectedFunction == FUNC_NATURE ? Dictionary.Type.ARCHIVE_NATURE : Dictionary.Type.ARCHIVE_TYPE);
    }

    private void searchingArchive() {
        switch (searchingFunction) {
            case SEARCH_HOME:
                // 首页推荐的查询
                searchingHomeArchive();
                break;
            case SEARCH_GROUP:
                // 组织内档案搜索
                searchingGroupArchive();
                break;
            case SEARCH_USER:
                // 个人档案搜索
                searchingUserArchive();
                break;
        }
    }

    private Model last;

    private Model last() {
        if (null == last) {
            last = Model.getNoMore();
        }
        return last;
    }

    private Dictionary none;

    private Dictionary none() {
        if (null == none) {
            none = new Dictionary();
            none.setId("none");
            none.setName("不限");
            none.setSelected(true);
        }
        return none;
    }

    private void clearList(int count, int pageSize) {
        if (remotePageNumber <= 1) {
            searched.clear();
            mAdapter.clear();
        }
        isLoadingComplete(count < pageSize);
        remotePageNumber += count >= pageSize ? 1 : 0;
    }

    private void searchingHomeArchive() {
        mAdapter.remove(last());
        RecommendArchiveRequest.request().setOnMultipleRequestListener(new OnMultipleRequestListener<RecommendArchive>() {
            @Override
            public void onResponse(List<RecommendArchive> list, boolean success, int totalPages, int pageSize, int total, int pageNumber) {
                super.onResponse(list, success, totalPages, pageSize, total, pageNumber);
                int count = null == list ? 0 : list.size();
                clearList(count, pageSize);
                if (success && null != list) {
                    for (RecommendArchive archive : list) {
                        Archive doc = null == archive.getUserDoc() ? archive.getGroDoc() : archive.getUserDoc();
                        doc.resetAdditional(doc.getAddition());
                        searched.add(doc);
                    }
                }
                restoreSearchingResult();
            }
        }).listHomeFeatured(remotePageNumber, searchingText);
    }

    private void searchingGroupArchive() {
        ArchiveRequest.request().setOnMultipleRequestListener(new OnMultipleRequestListener<Archive>() {
            @Override
            public void onResponse(List<Archive> list, boolean success, int totalPages, int pageSize, int total, int pageNumber) {
                super.onResponse(list, success, totalPages, pageSize, total, pageNumber);
                int count = null == list ? 0 : list.size();
                clearList(count, pageSize);
                if (success && null != list) {
                    for (Archive archive : list) {
                        archive.resetAdditional(archive.getAddition());
                        searched.add(archive);
                    }
                }
                restoreSearchingResult();
            }
        }).search(mQueryId, searchingText, remotePageNumber);
    }

    private void searchingUserArchive() {
        ArchiveRequest.request().setOnMultipleRequestListener(new OnMultipleRequestListener<Archive>() {
            @Override
            public void onResponse(List<Archive> list, boolean success, int totalPages, int pageSize, int total, int pageNumber) {
                super.onResponse(list, success, totalPages, pageSize, total, pageNumber);
                int count = null == list ? 0 : list.size();
                clearList(count, pageSize);
                if (success && null != list) {
                    for (Archive archive : list) {
                        archive.resetAdditional(archive.getAddition());
                        searched.add(archive);
                    }
                }
                restoreSearchingResult();
            }
        }).search(mQueryId, remotePageNumber, searchingText);
    }

    private void restoreSearchingResult() {
        mAdapter.clear();
        for (Archive archive : searched) {
            if (isInMonth(archive) && isNature(archive) && isType(archive)) {
                mAdapter.update(archive);
            }
        }
        mAdapter.add(last());
        if (mAdapter.getItemCount() <= 1) {
            ToastHelper.make().showMsg(R.string.ui_text_home_archive_search_empty);
        }
    }

    private boolean isInMonth(Archive archive) {
        return isEmpty(searchingMonth) || (!isEmpty(archive.getCreateDate()) && archive.getCreateDate().contains(searchingMonth));
    }

    private boolean isNature(Archive archive) {
        return isEmpty(searchingNature) || (!isEmpty(archive.getProperty()) && archive.getProperty().contains(searchingNature));
    }

    private boolean isType(Archive archive) {
        return isEmpty(searchingType) || (!isEmpty(archive.getCategory()) && archive.getCategory().contains(searchingType));
    }

    private void initializeTypeAdapter() {
        if (null == tAdapter) {
            tAdapter = new TypeAdapter();
            typeList.setLayoutManager(new CustomLinearLayoutManager(typeList.getContext()));
            typeList.setAdapter(tAdapter);
        }
    }

    private OnViewHolderClickListener holderClickListener = new OnViewHolderClickListener() {
        @Override
        public void onClick(int index) {
            Dictionary dic = tAdapter.get(index);
            dic.setSelected(!dic.isSelected());
            tAdapter.update(dic);
            String selected = dic.isSelected() ? (dic.getId().equals("none") ? "" : dic.getName()) : "";
            if (dic.getTypeCode().equals(Dictionary.Type.ARCHIVE_NATURE)) {
                searchingNature = selected;
            } else {
                searchingType = selected;
            }
            for (int i = 0, len = tAdapter.getItemCount(); i < len; i++) {
                Dictionary d = tAdapter.get(i);
                if (!d.getId().equals(dic.getId()) && d.isSelected()) {
                    d.setSelected(false);
                    tAdapter.update(d);
                }
            }
            if (isEmpty(searchingNature) && isEmpty(searchingType)) {
                // 如果两个筛选条件都为空，则选中“不限”选项
                Dictionary d = none();
                d.setSelected(true);
                tAdapter.update(d);
            }
            restoreSearchingResult();
        }
    };

    private class TypeAdapter extends RecyclerViewAdapter<TextViewHolder, Dictionary> {
        @Override
        public TextViewHolder onCreateViewHolder(View itemView, int viewType) {
            TextViewHolder tvh = new TextViewHolder(itemView, ArchiveSearchFragment.this);
            tvh.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
            tvh.addOnViewHolderClickListener(holderClickListener);
            return tvh;
        }

        @Override
        public int itemLayout(int viewType) {
            return R.layout.holder_view_text_olny;
        }

        @Override
        public void onBindHolderOfView(TextViewHolder holder, int position, @Nullable Dictionary item) {
            holder.showContent(item);
        }

        @Override
        protected int comparator(Dictionary item1, Dictionary item2) {
            return 0;
        }
    }

    private OnViewHolderClickListener onViewHolderClickListener = new OnViewHolderClickListener() {
        @Override
        public void onClick(int index) {
            Model model = mAdapter.get(index);
            if (model instanceof Archive) {
                // 到档案详情
                Archive arc = (Archive) model;
                int type = isEmpty(arc.getGroupId()) ? Archive.Type.USER : Archive.Type.GROUP;
                ArchiveDetailsWebViewFragment.open(ArchiveSearchFragment.this, arc.getId(), type);
            }
        }
    };

    private void initializeArchiveAdapter() {
        if (null == mAdapter) {
            mAdapter = new ArchiveAdapter();
            mRecyclerView.setAdapter(mAdapter);
            if (!isEmpty(searchingText)) {
                searchingArchive();
            }
        }
    }

    private class ArchiveAdapter extends RecyclerViewAdapter<BaseViewHolder, Model> {

        private static final int VT_LAST = 0, VT_ARCHIVE = 1;

        @Override
        public BaseViewHolder onCreateViewHolder(View itemView, int viewType) {
            switch (viewType) {
                case VT_LAST:
                    return new NothingMoreViewHolder(itemView, ArchiveSearchFragment.this);
            }
            ArchiveHomeRecommendedViewHolder ahrvh = new ArchiveHomeRecommendedViewHolder(itemView, ArchiveSearchFragment.this);
            ahrvh.addOnViewHolderClickListener(onViewHolderClickListener);
            return ahrvh;
        }

        @Override
        public int itemLayout(int viewType) {
            return viewType == VT_LAST ? R.layout.holder_view_nothing_more : R.layout.holder_view_archive_home_feature;
        }

        @Override
        public int getItemViewType(int position) {
            Model model = get(position);
            if (model instanceof Archive) {
                return VT_ARCHIVE;
            }
            return VT_LAST;
        }

        @Override
        public void onBindHolderOfView(BaseViewHolder holder, int position, @Nullable Model item) {
            if (holder instanceof ArchiveHomeRecommendedViewHolder) {
                ((ArchiveHomeRecommendedViewHolder) holder).setSearchingText(searchingText);
                ((ArchiveHomeRecommendedViewHolder) holder).showContent((Archive) item);
            }
        }

        @Override
        protected int comparator(Model item1, Model item2) {
            return 0;
        }
    }
}