package com.leadcom.android.isp.fragment.main;

import android.animation.Animator;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bigkoo.pickerview.builder.TimePickerBuilder;
import com.bigkoo.pickerview.listener.CustomListener;
import com.bigkoo.pickerview.listener.OnTimeSelectListener;
import com.bigkoo.pickerview.view.TimePickerView;
import com.hlk.hlklib.layoutmanager.CustomLinearLayoutManager;
import com.hlk.hlklib.lib.inject.Click;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.view.CustomTextView;
import com.leadcom.android.isp.R;
import com.leadcom.android.isp.adapter.RecyclerViewAdapter;
import com.leadcom.android.isp.api.archive.ArchiveRequest;
import com.leadcom.android.isp.api.archive.ClassifyRequest;
import com.leadcom.android.isp.api.archive.DictionaryRequest;
import com.leadcom.android.isp.api.listener.OnMultipleRequestListener;
import com.leadcom.android.isp.api.user.MemberDutyRequest;
import com.leadcom.android.isp.api.user.UserMsgRequest;
import com.leadcom.android.isp.etc.Utils;
import com.leadcom.android.isp.fragment.archive.ArchiveDetailsFragment;
import com.leadcom.android.isp.fragment.base.BaseFragment;
import com.leadcom.android.isp.fragment.individual.UserMessageFragment;
import com.leadcom.android.isp.fragment.organization.GroupBaseFragment;
import com.leadcom.android.isp.fragment.organization.MemberDutyDetailsFragment;
import com.leadcom.android.isp.helper.StringHelper;
import com.leadcom.android.isp.helper.ToastHelper;
import com.leadcom.android.isp.holder.BaseViewHolder;
import com.leadcom.android.isp.holder.common.InputableSearchViewHolder;
import com.leadcom.android.isp.holder.common.NothingMoreViewHolder;
import com.leadcom.android.isp.holder.common.TextViewHolder;
import com.leadcom.android.isp.holder.home.ArchiveHomeRecommendedViewHolder;
import com.leadcom.android.isp.holder.organization.GroupMemberDutyViewHolder;
import com.leadcom.android.isp.listener.OnTitleButtonClickListener;
import com.leadcom.android.isp.listener.OnViewHolderClickListener;
import com.leadcom.android.isp.listener.OnViewHolderElementClickListener;
import com.leadcom.android.isp.model.Model;
import com.leadcom.android.isp.model.archive.Archive;
import com.leadcom.android.isp.model.archive.Classify;
import com.leadcom.android.isp.model.archive.Dictionary;
import com.leadcom.android.isp.model.user.MemberDuty;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

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

public class ArchiveSearchFragment extends GroupBaseFragment {

    private static final String PARAM_TYPE = "param_searching_type";
    private static final String PARAM_TEXT = "param_searching_text";
    private static final String PARAM_GROUP_NAME = "param_group_name";
    private static final String PARAM_SQUAD_ID = "param_searching_squad_id";
    private static final String PARAM_SQUAD_NAME = "param_searching_squad_name";

    public static ArchiveSearchFragment newInstance(Bundle bundle) {
        ArchiveSearchFragment asf = new ArchiveSearchFragment();
        asf.setArguments(bundle);
        return asf;
    }

    public static void open(BaseFragment fragment, int searchType, String searchGroupId, String searchText, String searchGroupName) {
        open(fragment, searchType, searchGroupId, "", searchText, searchGroupName, "");
    }

    public static void open(BaseFragment fragment, int searchType, String searchGroupId, String searchSquadId, String searchText, String searchGroupName, String searchSquadName) {
        Bundle bundle = new Bundle();
        // 搜索方式
        bundle.putInt(PARAM_TYPE, searchType);
        // 传过来的组织id或者用户的id
        bundle.putString(PARAM_QUERY_ID, searchGroupId);
        // 搜索的文本
        bundle.putString(PARAM_TEXT, searchText);
        // 传过来的组织名称
        bundle.putString(PARAM_GROUP_NAME, searchGroupName);
        // 传过来的支部id
        bundle.putString(PARAM_SQUAD_ID, searchSquadId);
        // 传过来的小组名称
        bundle.putString(PARAM_SQUAD_NAME, searchSquadName);
        fragment.openActivity(ArchiveSearchFragment.class.getName(), bundle, true, false);
    }

    @ViewId(R.id.ui_holder_view_searchable_container)
    private View searchableView;
    @ViewId(R.id.ui_main_archive_search_content_background)
    private View selectorBg;
    @ViewId(R.id.ui_main_archive_search_functions)
    private LinearLayout functionView;
    @ViewId(R.id.ui_main_archive_search_functions_0)
    private LinearLayout functionView0;
    @ViewId(R.id.ui_main_archive_search_functions_1)
    private LinearLayout functionView1;
    @ViewId(R.id.ui_main_archive_search_functions_2)
    private LinearLayout functionView2;
    @ViewId(R.id.ui_main_archive_search_functions_3)
    private LinearLayout functionView3;
    @ViewId(R.id.ui_main_archive_search_functions_0_text)
    private TextView function0;
    @ViewId(R.id.ui_main_archive_search_functions_1_text)
    private TextView function1;
    @ViewId(R.id.ui_main_archive_search_functions_2_text)
    private TextView function2;
    @ViewId(R.id.ui_main_archive_search_functions_3_text)
    private TextView function3;
    @ViewId(R.id.ui_main_archive_search_functions_0_icon)
    private CustomTextView allow0;
    @ViewId(R.id.ui_main_archive_search_functions_1_icon)
    private CustomTextView allow1;
    @ViewId(R.id.ui_main_archive_search_functions_2_icon)
    private CustomTextView allow2;
    @ViewId(R.id.ui_main_archive_search_functions_3_icon)
    private CustomTextView allow3;
    @ViewId(R.id.ui_main_archive_search_time_picker)
    private FrameLayout timePickerContainer;
    @ViewId(R.id.ui_main_archive_search_type_picker)
    private View typePickerContainer;
    @ViewId(R.id.ui_main_archive_search_type_list)
    private RecyclerView typeList;
    @ViewId(R.id.ui_main_archive_search_subtype_list)
    private RecyclerView subTypeList;
    private TimePickerView timePickerView;
    private TypeAdapter tAdapter;
    private SubTypeAdapter sAdapter;
    private ArchiveAdapter mAdapter;
    private ArrayList<Dictionary> dictionaries = new ArrayList<>();
    private ArrayList<Classify> classifies = new ArrayList<>();
    private ArrayList<Archive> searched = new ArrayList<>();

    /**
     * 当前选择方式
     */
    private static final int FUNC_NONE = -1, FUNC_TIME = 0, FUNC_NATURE = 1, FUNC_TYPE = 2, FUNC_YEAR = 3;
    private static int selectedFunction = FUNC_NONE;
    /**
     * 搜索对象：首页、组织内部、个人
     */
    public static final int SEARCH_HOME = 0, SEARCH_GROUP = 1, SEARCH_USER = 2, SEARCH_DUTY = 3, SEARCH_DUTY_SQUAD = 4;
    /**
     * 当前搜索方式
     */
    private int searchingFunction = SEARCH_HOME;
    private String searchingYear = "", searchingNature = "", searchingNature1 = "", searchingType = "";

    private String searchingText, mGroupName, mSquadId, mSquadName;
    private boolean stillLoading = false;

    @Override
    protected void getParamsFromBundle(Bundle bundle) {
        super.getParamsFromBundle(bundle);
        searchingFunction = bundle.getInt(PARAM_TYPE, SEARCH_HOME);
        searchingText = bundle.getString(PARAM_TEXT, "");
        mGroupName = bundle.getString(PARAM_GROUP_NAME, "");
        mSquadId = bundle.getString(PARAM_SQUAD_ID, "");
        mSquadName = bundle.getString(PARAM_SQUAD_NAME, "");
    }

    @Override
    protected void saveParamsToBundle(Bundle bundle) {
        super.saveParamsToBundle(bundle);
        bundle.putInt(PARAM_TYPE, searchingFunction);
        bundle.putString(PARAM_TEXT, searchingText);
        bundle.putString(PARAM_GROUP_NAME, mGroupName);
        bundle.putString(PARAM_SQUAD_ID, mSquadId);
        bundle.putString(PARAM_SQUAD_NAME, mSquadName);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        searchingText = "";
        searchingYear = "";
        searchingNature = "";
        searchingType = "";
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        String title = StringHelper.getString(searchingFunction == SEARCH_HOME ? R.string.ui_text_archive_searching_home_title : (searchingFunction >= SEARCH_DUTY ? R.string.ui_group_member_duty_count_title : R.string.ui_group_archive_fragment_title));
        if (!isEmpty(mSquadName)) {
            title = format("支部%s(%s)", title, mSquadName);
        } else if (!isEmpty(mGroupName)) {
            title = format("%s%s(%s)", (searchingFunction == SEARCH_DUTY_SQUAD ? "支部" : "组织"), title, mGroupName);
        }
        setCustomTitle(title);

        selectedFunction = FUNC_NONE;

        if (searchingFunction < SEARCH_DUTY) {
            if (isMember(mQueryId)) {
                setRightIcon(R.string.ui_icon_comment);
                setRightTitleClickListener(new OnTitleButtonClickListener() {
                    @Override
                    public void onClick() {
                        // 用户动态相关的消息
                        UserMessageFragment.open(ArchiveSearchFragment.this, UserMsgRequest.TYPE_GROUP_ARCHIVE);
                    }
                });
            }
            //enableSwipe(false);
            //if (selectedFunction != SEARCH_GROUP) {
            //    isLoadingComplete(true);
            //}
            InputableSearchViewHolder searchViewHolder = new InputableSearchViewHolder(searchableView, this);
            //searchViewHolder.setBackground(getColor(R.color.colorPrimary));
            searchViewHolder.setOnSearchingListener(new InputableSearchViewHolder.OnSearchingListener() {
                @Override
                public void onSearching(String text) {
                    if (!isEmpty(text)) {
                        searchingText = text;
                        remotePageNumber = 1;
                        if (searchingFunction == SEARCH_GROUP) {
                            // 组织搜索可以翻页查询
                            setSupportLoadingMore(true);
                        }
                        searchingArchive();
                    } else {
                        searchingText = "";
                    }
                }
            });
            if (searchingFunction > SEARCH_HOME) {
                searchViewHolder.setInputHint(R.string.ui_group_archive_fragment_search_hint);
            } else {
                functionView.setVisibility(View.GONE);
            }
            initializeTimePickerView();
        } else {
            functionView0.setVisibility(View.VISIBLE);
            functionView1.setVisibility(View.GONE);
            functionView2.setGravity(Gravity.CENTER);
            functionView3.setGravity(Gravity.CENTER);
            searchableView.setVisibility(View.GONE);
            function2.setText(R.string.ui_text_archive_searching_selector_2_1);
            function3.setText(R.string.ui_text_archive_searching_selector_3_1);
        }

        initializePositions();
    }

    @Override
    public void onActivityResult(int requestCode, Intent data) {
        switch (requestCode) {
            case REQUEST_DELETE:
                String id = getResultedData(data);
                Model result = getResultModel(data, RESULT_ARCHIVE);
                if (null != result) {
                    mAdapter.update(result);
                } else if (!isEmpty(id)) {
                    Archive archive = new Archive();
                    archive.setId(id);
                    mAdapter.remove(archive);
                }
                break;
        }
        super.onActivityResult(requestCode, data);
    }

    private TextView timePickerTitle;

    private void initializeTimePickerView() {
        if (null == timePickerView) {
            Calendar start = Calendar.getInstance(Locale.getDefault());
            start.set(2015, 0, 1);
            Calendar end = Calendar.getInstance(Locale.getDefault());
            end.set(end.get(Calendar.YEAR), 11, 1);
            timePickerView = new TimePickerBuilder(Activity(), new OnTimeSelectListener() {
                @Override
                public void onTimeSelect(Date date, View v) {
                    String dd = Utils.format("yyyy", date);
                    if (!dd.equals(searchingYear)) {
                        remotePageNumber = 1;
                        searchingYear = dd;
                        setSupportLoadingMore(true);
                        searchingArchive();
                    }
                    if (null != timePickerTitle) {
                        timePickerTitle.setText(searchingYear + "年");
                    }
                    //restoreSearchingResult();
                }
            }).setLayoutRes(R.layout.tool_view_custom_time_picker, new CustomListener() {
                @Override
                public void customLayout(final View root) {
                    root.findViewById(R.id.timepicker_cancel).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            remotePageNumber = 1;
                            // 清除已选择了的日期
                            searchingYear = "";
                            resetTitle(root);
                            //hideSelector();
                            //resetFunctionStatus();
                            searchingArchive();
                        }
                    });
                    root.findViewById(R.id.timepicker_confirm).setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            // 确定时间
                            timePickerView.returnData();
                        }
                    });
                    resetTitle(root);
                }

                private void resetTitle(View root) {
                    if (null == timePickerTitle) {
                        timePickerTitle = root.findViewById(R.id.timepicker_title);
                    }
                    timePickerTitle.setText(searchingYear);
                }
            }).setType(new boolean[]{true, false, false, false, false, false})
                    .setDecorView(timePickerContainer)
                    .setOutSideCancelable(false).setDividerColor(getColor(R.color.textColorHint))
                    .setContentTextSize(getFontDimension(R.dimen.ui_base_text_size))
                    .setRangDate(start, Calendar.getInstance())
                    .setDate(Calendar.getInstance(Locale.getDefault()))
                    .isCenterLabel(false)
                    .build();
        }
        timePickerView.setKeyBackCancelable(false);
    }

    private void initializePositions() {
        Handler().post(new Runnable() {
            @Override
            public void run() {
                if (null != timePickerView) {
                    timePickerView.show();
                }
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
        return true;
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
        setSupportLoadingMore(true);
        searchingArchive();
    }

    @Override
    protected void onLoadingMore() {
        searchingArchive();
    }

    @Override
    protected String getLocalPageTag() {
        return null;
    }

    @Click({R.id.ui_main_archive_search_functions_1, R.id.ui_main_archive_search_functions_2,
            R.id.ui_main_archive_search_functions_3, R.id.ui_main_archive_search_functions_0,
            R.id.ui_main_archive_search_content_background})
    private void viewClick(View view) {
        switch (view.getId()) {
            case R.id.ui_main_archive_search_content_background:
                hideSelector();
                resetFunctionStatus();
                break;
            case R.id.ui_main_archive_search_functions_0:
                // 年份选择
                if (selectedFunction != FUNC_YEAR) {
                    selectedFunction = FUNC_YEAR;
                    tAdapter.clear();
                    subTypeList.setVisibility(View.GONE);
                    showSelector();
                } else {
                    hideSelector();
                }
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
                    tAdapter.clear();
                    sAdapter.clear();
                    //resetSelectedFuncNature();
                    showSelector();
                } else {
                    hideSelector();
                }
                resetFunctionStatus();
                break;
            case R.id.ui_main_archive_search_functions_3:
                if (selectedFunction != FUNC_TYPE) {
                    selectedFunction = FUNC_TYPE;
                    tAdapter.clear();
                    subTypeList.setVisibility(View.GONE);
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
        subTypeList.setVisibility(View.GONE);
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
        allow0.animate().rotation(shown && selectedFunction == FUNC_YEAR ? -90 : 90)
                .setDuration(duration()).start();
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

        function0.setTextColor(selectedFunction == FUNC_YEAR ? color1 : color2);
        allow0.setTextColor(selectedFunction == FUNC_YEAR ? color1 : color2);

        function1.setTextColor(selectedFunction == FUNC_TIME ? color1 : color2);
        allow1.setTextColor(selectedFunction == FUNC_TIME ? color1 : color2);

        function2.setTextColor(selectedFunction == FUNC_NATURE ? color1 : color2);
        allow2.setTextColor(selectedFunction == FUNC_NATURE ? color1 : color2);

        function3.setTextColor(selectedFunction == FUNC_TYPE ? color1 : color2);
        allow3.setTextColor(selectedFunction == FUNC_TYPE ? color1 : color2);
    }

    private void resetTypeList() {
        switch (selectedFunction) {
            case FUNC_TYPE:
                resetFuncType();
                break;
            case FUNC_NATURE:
                resetFuncNature();
                break;
            case FUNC_YEAR:
                resetFuncYear();
                break;
        }
    }

    private void resetFuncYear() {
        tAdapter.update(allYear);
        tAdapter.update(thisYear);
        tAdapter.update(lastYear);
    }

    private void resetFuncType() {
        boolean selectedExists = false;
        for (Dictionary dictionary : dictionaries) {
            if (dictionary.getTypeCode().equals(Dictionary.Type.ARCHIVE_TYPE)) {
                if (dictionary.getCode() == 0) {
                    continue;
                }
                tAdapter.add(dictionary);
                if (dictionary.isSelected()) {
                    selectedExists = true;
                }
            }
        }
        none.setSelected(!selectedExists);
        tAdapter.add(none, 0);
        if (tAdapter.getItemCount() <= 1) {
            // 重新拉取
            fetchingDictionary();
        }
    }

    // 清除已选中的档案性质选项
    private void resetSelectedFuncNature() {
        for (Classify classify : classifies) {
            if (classify.getParentId() == 0 && classify.isSelected()) {
                classify.setSelected(false);
            }
        }
    }

    private boolean hasNatureSelected() {
        for (Classify classify : classifies) {
            if (classify.getParentId() == 0 && classify.isSelected() && !classify.getId().equals("0"))
                return true;
        }
        return false;
    }

    private void resetFuncNature() {
        subTypeList.setVisibility(hasNatureSelected() ? View.VISIBLE : View.GONE);
        int parent = Integer.valueOf(isEmpty(searchingNature) ? "0" : searchingNature);
        if (parent > 0) {
            sAdapter.clear();
        }

        for (Classify classify : classifies) {
            if (classify.getParentId() == 0) {
                tAdapter.add(classify);
            } else if (classify.getParentId() > 0 && classify.getParentId() == parent) {
                sAdapter.add(classify);
            }
        }
        if (tAdapter.getItemCount() <= 0) {
            fetchingClassify(searchingNature);
        }
        if (!isEmpty(searchingNature) && !searchingNature.equals("0") && sAdapter.getItemCount() <= 0) {
            fetchingClassify(searchingNature);
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
        }).list(Dictionary.Type.ARCHIVE_TYPE);
    }

    private void fetchingClassify(final String classifyId) {
        ClassifyRequest.request().setOnMultipleRequestListener(new OnMultipleRequestListener<Classify>() {
            @Override
            public void onResponse(List<Classify> list, boolean success, int totalPages, int pageSize, int total, int pageNumber) {
                super.onResponse(list, success, totalPages, pageSize, total, pageNumber);
                if (success && null != list) {
                    for (Classify classify : list) {
                        if (classify.getId().equals("0") && classify.getParentId() == 0) {
                            // 默认选中顶层类别中的“全部”
                            classify.setSelected(true);
                        }
                        if (!classifies.contains(classify)) {
                            classify.setParentId(isEmpty(classifyId) ? 0 : Long.valueOf(classifyId));
                            classifies.add(classify);
                        }
                    }
                    resetTypeList();
                }
            }
        }).list(mQueryId, classifyId);
    }

    private void searchingArchive() {
        displayLoading(true);
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
            case SEARCH_DUTY:
                // 成员履职
                searchUserDuty();
                break;
            case SEARCH_DUTY_SQUAD:
                // 支部履职统计
                searchSquadDuty();
                break;
        }
    }

    private Model last = Model.getNoMore();

    private Model allYear = new Model() {{
        setId("year0");
        setSelected(true);
        setAccessToken("全部");
    }};
    private Model thisYear = new Model() {{
        setId("year1");
        setAccessToken("今年");
    }};

    private Model lastYear = new Model() {{
        setId("year2");
        setAccessToken("去年");
    }};

    private Dictionary none = new Dictionary() {{
        setId("none");
        setName("全部");
        setSelected(true);
    }};

    private void clearList(int count, int pageSize) {
        displayLoading(false);
        if (remotePageNumber <= 1) {
            searched.clear();
            mAdapter.clear();
        }
        isLoadingComplete(count < pageSize);
        remotePageNumber += count >= pageSize ? 1 : 0;
    }

    private void searchingHomeArchive() {
        if (isEmpty(searchingText)) {
            displayLoading(false);
            return;
        }
        mAdapter.remove(last);
        if (isStillLoading()) {
            return;
        }
        stillLoading = true;
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
                stillLoading = false;
                restoreSearchingResult();
            }
        }).listHomeRecommend(remotePageNumber, searchingText);
    }

    private boolean isStillLoading() {
        if (stillLoading) {
            ToastHelper.make().showMsg(R.string.ui_text_archive_searching_still_loading);
            return true;
        }
        return false;
    }

    private void searchingGroupArchive() {
        if (isStillLoading()) {
            return;
        }
        stillLoading = true;
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
                stillLoading = false;
                restoreSearchingResult();
            }
        }).search(mQueryId, searchingText, searchingYear, searchingNature1, searchingType, remotePageNumber);
    }

    private void searchingUserArchive() {
        if (isEmpty(searchingText)) {
            displayLoading(false);
            return;
        }
        if (isStillLoading()) {
            return;
        }
        stillLoading = true;
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
                stillLoading = false;
                restoreSearchingResult();
            }
        }).search(mQueryId, remotePageNumber, searchingText);
    }

    private void searchUserDuty() {
        if (isStillLoading()) {
            return;
        }
        stillLoading = true;
        setNothingText(R.string.ui_group_member_duty_count_nothing);
        displayNothing(false);
        mAdapter.clear();
        MemberDutyRequest.request().setOnMultipleRequestListener(new OnMultipleRequestListener<MemberDuty>() {
            @Override
            public void onResponse(List<MemberDuty> list, boolean success, int totalPages, int pageSize, int total, int pageNumber) {
                super.onResponse(list, success, totalPages, pageSize, total, pageNumber);
                if (success && null != list) {
                    for (MemberDuty duty : list) {
                        duty.setId(duty.getUserId());
                        mAdapter.update(duty);
                    }
                }
                isLoadingComplete(true);
                stillLoading = false;
                stopRefreshing();
                displayLoading(false);
                displayNothing(mAdapter.getItemCount() <= 0);
            }
        }).list(mQueryId, mSquadId, Integer.valueOf(isEmpty(searchingYear) ? "0" : searchingYear), searchingNature1, searchingType);
    }

    private void searchSquadDuty() {
        if (isStillLoading()) {
            return;
        }
        stillLoading = true;
        setNothingText(R.string.ui_group_member_duty_count_nothing);
        displayNothing(false);
        mAdapter.clear();
        MemberDutyRequest.request().setOnMultipleRequestListener(new OnMultipleRequestListener<MemberDuty>() {
            @Override
            public void onResponse(List<MemberDuty> list, boolean success, int totalPages, int pageSize, int total, int pageNumber) {
                super.onResponse(list, success, totalPages, pageSize, total, pageNumber);
                if (success && null != list) {
                    for (MemberDuty duty : list) {
                        duty.setId(duty.getSquadId());
                        mAdapter.update(duty);
                    }
                }
                isLoadingComplete(true);
                stillLoading = false;
                stopRefreshing();
                displayLoading(false);
                displayNothing(mAdapter.getItemCount() <= 0);
            }
        }).listSquads(mQueryId, Integer.valueOf(isEmpty(searchingYear) ? "0" : searchingYear), searchingNature1, searchingType);
    }

    private void restoreSearchingResult() {
        mAdapter.remove(last);
        for (Archive archive : searched) {
            //if (isInMonth(archive) && isNature(archive) && isType(archive)) {
            mAdapter.update(archive);
            //}
        }
        mAdapter.add(last);
        if (mAdapter.getItemCount() <= 1) {
            ToastHelper.make().showMsg(R.string.ui_text_home_archive_search_empty);
        }
        stopRefreshing();
    }

    private void initializeTypeAdapter() {
        if (null == tAdapter) {
            tAdapter = new TypeAdapter();
            typeList.setLayoutManager(new CustomLinearLayoutManager(typeList.getContext()));
            typeList.setAdapter(tAdapter);
        }
        if (null == sAdapter) {
            sAdapter = new SubTypeAdapter();
            subTypeList.setLayoutManager(new CustomLinearLayoutManager(subTypeList.getContext()));
            subTypeList.setAdapter(sAdapter);
        }
    }

    private void clearClassify(String parentId) {
        long parent = Long.valueOf(parentId);
        for (Classify classify : classifies) {
            if (classify.getParentId() > 0 && classify.getParentId() == parent) {
                classify.setSelected(false);
                if (sAdapter.indexOf(classify) >= 0) {
                    sAdapter.update(classify);
                }
            }
        }
    }

    private OnViewHolderClickListener holderClickListener = new OnViewHolderClickListener() {
        @Override
        public void onClick(int index) {
            Model model = tAdapter.get(index);
            if (model instanceof Dictionary) {
                Dictionary dic = (Dictionary) model;
                if (!dic.isSelected()) {
                    dic.setSelected(true);
                }
                tAdapter.update(dic);
                String selected = dic.getName();
                if (!searchingType.equals(selected)) {
                    remotePageNumber = 1;
                    searchingType = selected;
                    if (searchingType.contains("全部")) {
                        searchingType = "";
                    }
                }
                for (int i = 0, len = tAdapter.getItemCount(); i < len; i++) {
                    Dictionary d = (Dictionary) tAdapter.get(i);
                    if (!d.getId().equals(dic.getId()) && d.isSelected()) {
                        d.setSelected(false);
                        tAdapter.update(d);
                    }
                }
            } else if (model instanceof Classify) {
                Classify clazz = (Classify) model;
                clazz.setSelected(!clazz.isSelected());
                if (clazz.getParentId() <= 0) {
                    tAdapter.update(clazz);
                    subTypeList.setVisibility(!clazz.getId().equals("0") && clazz.isSelected() ? View.VISIBLE : View.GONE);
                    searchingNature = clazz.isSelected() ? clazz.getId() : "";
                    searchingNature1 = searchingNature;
                    if (!clazz.isSelected() && clazz.getId().equals("0")) {
                        // “全部”的取消选中不需要再次拉取档案内容
                        return;
                    }
                    for (int i = 0, len = tAdapter.getItemCount(); i < len; i++) {
                        Classify classify = (Classify) tAdapter.get(i);
                        if (!classify.getId().equals(clazz.getId()) && classify.isSelected()) {
                            classify.setSelected(false);
                            tAdapter.update(classify);
                            clearClassify(classify.getId());
                        }
                    }
                    if (isEmpty(searchingNature)) {
                        // 取消选择之后重置本类别下级类别的所有选中项
                        sAdapter.clear();
                        clearClassify(clazz.getId());
                    }
                    resetFuncNature();
                    //return;
                }
            } else if (model.getId().contains("year")) {
                if (!model.isSelected()) {
                    model.setSelected(true);
                    searchingYear = model.getId().replace("year", "");
                    tAdapter.update(model);
                }
                Iterator<Model> iterator = tAdapter.iterator();
                while (iterator.hasNext()) {
                    Model m = iterator.next();
                    if (!m.getId().equals(model.getId()) && m.isSelected()) {
                        m.setSelected(false);
                        tAdapter.update(m);
                    }
                }
            }
            loadingArchive();
        }
    };

    private void loadingArchive() {
        //restoreSearchingResult();
        remotePageNumber = 1;
        if (searchingFunction == SEARCH_GROUP) {
            // 组织搜索可以翻页查询
            setSupportLoadingMore(true);
        }
        searchingArchive();
    }

    private class TypeAdapter extends RecyclerViewAdapter<TextViewHolder, Model> {
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
        public void onBindHolderOfView(TextViewHolder holder, int position, @Nullable Model item) {
            if (item instanceof Dictionary) {
                holder.showContent((Dictionary) item);
            } else if (item instanceof Classify) {
                holder.showContent((Classify) item);
            } else {
                holder.showContent(item);
            }
        }

        @Override
        protected int comparator(Model item1, Model item2) {
            return 0;
        }
    }

    private OnViewHolderClickListener subTypeHolderClickListener = new OnViewHolderClickListener() {
        @Override
        public void onClick(int index) {
            Classify clazz = sAdapter.get(index);
            if (!clazz.isSelected()) {
                clazz.setSelected(true);
                sAdapter.update(clazz);
            } else {
                return;
            }
            searchingNature1 = clazz.isSelected() ? clazz.getId() : "";

            // 去除所有其他类别的选中状态
            for (Classify classify : classifies) {
                if (classify.getParentId() > 0) {
                    if (classify.getParentId() != clazz.getParentId()) {
                        if (classify.isSelected()) {
                            classify.setSelected(false);
                        }
                    }
                }
            }
            Iterator<Classify> iterator = sAdapter.iterator();
            while (iterator.hasNext()) {
                Classify classify = iterator.next();
                // 同一目录下的
                if (!classify.getId().equals(clazz.getId()) && classify.isSelected()) {
                    classify.setSelected(false);
                    sAdapter.update(classify);
                }
            }
            loadingArchive();
        }
    };

    private class SubTypeAdapter extends RecyclerViewAdapter<TextViewHolder, Classify> {
        @Override
        public TextViewHolder onCreateViewHolder(View itemView, int viewType) {
            TextViewHolder tvh = new TextViewHolder(itemView, ArchiveSearchFragment.this);
            tvh.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
            tvh.addOnViewHolderClickListener(subTypeHolderClickListener);
            return tvh;
        }

        @Override
        public int itemLayout(int viewType) {
            return R.layout.holder_view_text_olny;
        }

        @Override
        public void onBindHolderOfView(TextViewHolder holder, int position, @Nullable Classify item) {
            holder.showContent(item);
        }

        @Override
        protected int comparator(Classify item1, Classify item2) {
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
                ArchiveDetailsFragment.open(ArchiveSearchFragment.this, arc);
            }
        }
    };

    private void initializeArchiveAdapter() {
        if (null == mAdapter) {
            mAdapter = new ArchiveAdapter();
            mRecyclerView.setAdapter(mAdapter);
            //if (!isEmpty(searchingText)) {
            searchingArchive();
            //}
        }
    }

    private OnViewHolderElementClickListener elementClickListener = new OnViewHolderElementClickListener() {
        @Override
        public void onClick(View view, int index) {
            MemberDuty duty = (MemberDuty) mAdapter.get(index);
            boolean isSquad = !isEmpty(duty.getSquadId());
            switch (view.getId()) {
                case R.id.ui_holder_view_group_member_duty_count_archive:
                    // 查看用户的档案列表
                    //if (duty.getDocNum() <= 0) {
                    //    ToastHelper.make().showMsg(isSquad ? R.string.ui_group_member_duty_count_no_archive_squad : R.string.ui_group_member_duty_count_no_archive);
                    //} else {
                        if (isSquad) {
                            open(ArchiveSearchFragment.this, SEARCH_DUTY, mQueryId, duty.getSquadId(), "", mGroupName, duty.getSquadName());
                        } else {
                            MemberDutyDetailsFragment.open(ArchiveSearchFragment.this, mQueryId, mSquadId, duty.getUserId(), duty.getUserName(), MemberDutyRequest.OPE_ARCHIVE,
                                    duty.getDocNum(), Integer.valueOf(isEmpty(searchingYear) ? "0" : searchingYear), searchingNature1, searchingType);
                        }
                    //}
                    break;
                case R.id.ui_holder_view_group_member_duty_count_activity:
                    // 查看用户加入的活动列表
                    //if (duty.getActivityNum() <= 0) {
                    //    ToastHelper.make().showMsg(isSquad ? R.string.ui_group_member_duty_count_no_activity_squad : R.string.ui_group_member_duty_count_no_activity);
                    //} else {
                        if (isSquad) {
                            open(ArchiveSearchFragment.this, SEARCH_DUTY, mQueryId, duty.getSquadId(), "", mGroupName, duty.getSquadName());
                        } else {
                            MemberDutyDetailsFragment.open(ArchiveSearchFragment.this, mQueryId, mSquadId, duty.getUserId(), duty.getUserName(), MemberDutyRequest.OPE_ACTIVITY,
                                    duty.getActivityNum(), Integer.valueOf(isEmpty(searchingYear) ? "0" : searchingYear), searchingNature1, searchingType);
                        }
                    //}
                    break;
            }
        }
    };

    private class ArchiveAdapter extends RecyclerViewAdapter<BaseViewHolder, Model> {

        private static final int VT_LAST = 0, VT_ARCHIVE = 1, VT_DUTY = 2;

        @Override
        public BaseViewHolder onCreateViewHolder(View itemView, int viewType) {
            switch (viewType) {
                case VT_LAST:
                    return new NothingMoreViewHolder(itemView, ArchiveSearchFragment.this);
                case VT_DUTY:
                    GroupMemberDutyViewHolder gmdvh = new GroupMemberDutyViewHolder(itemView, ArchiveSearchFragment.this);
                    gmdvh.setOnViewHolderElementClickListener(elementClickListener);
                    return gmdvh;
            }
            ArchiveHomeRecommendedViewHolder ahrvh = new ArchiveHomeRecommendedViewHolder(itemView, ArchiveSearchFragment.this);
            ahrvh.setShowGroup(false);
            ahrvh.setHeaderShowable(true);
            ahrvh.addOnViewHolderClickListener(onViewHolderClickListener);
            return ahrvh;
        }

        @Override
        public int itemLayout(int viewType) {
            switch (viewType) {
                case VT_ARCHIVE:
                    return R.layout.holder_view_archive_home_feature;
                case VT_DUTY:
                    return R.layout.holder_view_group_member_duty_count;
                default:
                    return R.layout.holder_view_nothing_more;
            }
        }

        @Override
        public int getItemViewType(int position) {
            Model model = get(position);
            if (model instanceof Archive) {
                return VT_ARCHIVE;
            } else if (model instanceof MemberDuty) {
                return VT_DUTY;
            }
            return VT_LAST;
        }

        @Override
        public void onBindHolderOfView(BaseViewHolder holder, int position, @Nullable Model item) {
            if (holder instanceof ArchiveHomeRecommendedViewHolder) {
                ((ArchiveHomeRecommendedViewHolder) holder).setSearchingText(searchingText);
                ((ArchiveHomeRecommendedViewHolder) holder).showContent((Archive) item);
            } else if (holder instanceof GroupMemberDutyViewHolder) {
                ((GroupMemberDutyViewHolder) holder).showContent((MemberDuty) item);
            }
        }

        @Override
        protected int comparator(Model item1, Model item2) {
            return 0;
        }
    }
}
