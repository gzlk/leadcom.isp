package com.leadcom.android.isp.fragment.main;

import android.animation.Animator;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.Gravity;
import android.view.View;
import android.widget.FrameLayout;
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
import com.leadcom.android.isp.api.archive.DictionaryRequest;
import com.leadcom.android.isp.api.listener.OnMultipleRequestListener;
import com.leadcom.android.isp.fragment.base.BaseFragment;
import com.leadcom.android.isp.fragment.base.BaseSwipeRefreshSupportFragment;
import com.leadcom.android.isp.holder.common.InputableSearchViewHolder;
import com.leadcom.android.isp.holder.common.TextViewHolder;
import com.leadcom.android.isp.listener.OnViewHolderClickListener;
import com.leadcom.android.isp.model.archive.Dictionary;

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

    public static void open(BaseFragment fragment) {
        fragment.openActivity(ArchiveSearchFragment.class.getName(), "", false, false);
    }

    @ViewId(R.id.ui_holder_view_searchable_container)
    private View searchableView;
    @ViewId(R.id.ui_main_archive_search_content_background)
    private RelativeLayout selectorBg;
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
    private ArrayList<Dictionary> dictionaries = new ArrayList<>();

    /**
     * 当前选择方式
     */
    private static final int FUNC_NONE = -1, FUNC_TIME = 0, FUNC_NATURE = 1, FUNC_TYPE = 2;
    private static int selectedFunction = FUNC_NONE;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        selectedFunction = -1;
        enableSwipe(false);
        isLoadingComplete(true);
        InputableSearchViewHolder searchViewHolder = new InputableSearchViewHolder(searchableView, this);
        searchViewHolder.setBackground(getColor(R.color.colorPrimary));
        searchViewHolder.setOnSearchingListener(new InputableSearchViewHolder.OnSearchingListener() {
            @Override
            public void onSearching(String text) {

            }
        });
        initializeTimePickerView();
        initializePositions();
    }

    private void initializeTimePickerView() {
        timePickerView = new TimePickerView.Builder(Activity(), new TimePickerView.OnTimeSelectListener() {
            @Override
            public void onTimeSelect(Date date, View v) {

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

    }

    @Override
    protected void onLoadingMore() {

    }

    @Override
    protected String getLocalPageTag() {
        return null;
    }

    @Click({R.id.ui_main_archive_search_functions_1, R.id.ui_main_archive_search_functions_2, R.id.ui_main_archive_search_functions_3})
    private void viewClick(View view) {
        switch (view.getId()) {
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
        for (Dictionary dictionary : dictionaries) {
            if (dictionary.getTypeCode().equals(selectedFunction == FUNC_NATURE ? Dictionary.Type.ARCHIVE_NATURE : Dictionary.Type.ARCHIVE_TYPE)) {
                tAdapter.add(dictionary);
            }
        }
        if (tAdapter.getItemCount() <= 0) {
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
            for (int i = 0, len = tAdapter.getItemCount(); i < len; i++) {
                Dictionary d = tAdapter.get(i);
                if (d.isSelected()) {
                    if (!d.getId().equals(dic.getId())) {
                        d.setSelected(false);
                        tAdapter.update(d);
                    } else {
                        d.setSelected(false);
                        tAdapter.update(d);
                    }
                } else if (d.getId().equals(dic.getId())) {
                    d.setSelected(true);
                    tAdapter.update(d);
                }
            }
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
}
