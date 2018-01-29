package com.leadcom.android.isp.helper;

import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hlk.hlklib.layoutmanager.CustomStaggeredGridLayoutManager;
import com.leadcom.android.isp.R;
import com.leadcom.android.isp.adapter.RecyclerViewAdapter;
import com.leadcom.android.isp.api.archive.DictionaryRequest;
import com.leadcom.android.isp.api.listener.OnMultipleRequestListener;
import com.leadcom.android.isp.fragment.base.BaseFragment;
import com.leadcom.android.isp.holder.activity.ActivityLabelViewHolder;
import com.leadcom.android.isp.listener.OnViewHolderClickListener;
import com.leadcom.android.isp.model.archive.Dictionary;

import java.util.List;

/**
 * <b>功能描述：</b>字典选择器<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2018/01/28 09:08 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>version: 1.0.0 <br />
 * <b>修改时间：</b>2017/10/04 18:50 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */
public class DictionaryHelper {

    public static DictionaryHelper helper(BaseFragment fragment) {
        return new DictionaryHelper(fragment);
    }

    private BaseFragment fragment;
    private View dialogView;
    private TextView titleLabel;
    private View loadingView;
    private RecyclerView recyclerView;
    private DictionaryAdapter mAdapter;
    private static final int DFT_SPAN_COUNT = 2;
    private String lastTypeCode = "";
    private int layoutPadding;
    private DialogHelper dialogHelper;

    private DictionaryHelper(BaseFragment fragment) {
        this.fragment = fragment;
        layoutPadding = this.fragment.getDimension(R.dimen.ui_base_dimen_margin_padding);
    }

    public void showDialog(final String type, final String selected) {
        if (null == dialogHelper) {
            dialogHelper = DialogHelper.init(fragment.Activity()).addOnDialogInitializeListener(new DialogHelper.OnDialogInitializeListener() {
                @Override
                public View onInitializeView() {
                    if (null == dialogView) {
                        dialogView = View.inflate(fragment.Activity(), R.layout.popup_dialog_dictionary_selector, null);
                        titleLabel = dialogView.findViewById(R.id.ui_dialog_dictionary_selector_title_label);
                        loadingView = dialogView.findViewById(R.id.ui_tool_loading_container);
                        recyclerView = dialogView.findViewById(R.id.ui_tool_swipe_refreshable_recycler_view);
                        recyclerView.setLayoutManager(new CustomStaggeredGridLayoutManager(DFT_SPAN_COUNT, CustomStaggeredGridLayoutManager.VERTICAL));
                        mAdapter = new DictionaryAdapter();
                        recyclerView.setAdapter(mAdapter);
                    }
                    return dialogView;
                }

                @Override
                public void onBindData(View dialogView, DialogHelper helper) {
                    titleLabel.setText(type.equals(Dictionary.Type.ARCHIVE_NATURE) ? R.string.ui_text_archive_details_editor_setting_property_title : R.string.ui_text_archive_details_editor_setting_category_title);
                    loadingDictionary(type, selected);
                }
            }).setPopupType(DialogHelper.SLID_IN_BOTTOM).setAdjustScreenWidth(true);
        }
        dialogHelper.show();
    }

    private void loadingDictionary(String type, final String selected) {
        if (!lastTypeCode.equals(type)) {
            mAdapter.clear();
        }
        lastTypeCode = type;
        List<Dictionary> list = Dictionary.get(type);
        if (null != list && list.size() > 0) {
            for (Dictionary dictionary : list) {
                if (dictionary.getName().equals(selected)) {
                    dictionary.setSelected(true);
                }
                mAdapter.add(dictionary);
            }
        } else {
            loadingView.setVisibility(View.VISIBLE);
        }
        DictionaryRequest.request().setOnMultipleRequestListener(new OnMultipleRequestListener<Dictionary>() {
            @Override
            public void onResponse(List<Dictionary> list, boolean success, int totalPages, int pageSize, int total, int pageNumber) {
                super.onResponse(list, success, totalPages, pageSize, total, pageNumber);
                loadingView.setVisibility(View.GONE);
                if (success) {
                    for (Dictionary dictionary : list) {
                        if (dictionary.getName().equals(selected)) {
                            dictionary.setSelected(true);
                        }
                    }
                    mAdapter.update(list, true);
                }
            }
        }).list(type);
    }

    private OnViewHolderClickListener onViewHolderClickListener = new OnViewHolderClickListener() {
        @Override
        public void onClick(final int index) {
            for (int i = 0, len = mAdapter.getItemCount(); i < len; i++) {
                Dictionary dic = mAdapter.get(i);
                if (i == index) {
                    dic.setSelected(!dic.isSelected());
                } else {
                    dic.setSelected(false);
                }
                mAdapter.notifyItemChanged(i);
            }
            fragment.Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    if (null != selectedListener) {
                        selectedListener.onSelected(lastTypeCode, mAdapter.get(index).getName());
                    }
                    dialogHelper.dismiss();
                }
            }, fragment.duration());
        }
    };

    private class DictionaryAdapter extends RecyclerViewAdapter<ActivityLabelViewHolder, Dictionary> {

        @Override
        public ActivityLabelViewHolder onCreateViewHolder(View itemView, int viewType) {
            ActivityLabelViewHolder holder = new ActivityLabelViewHolder(itemView, fragment);
            holder.addOnViewHolderClickListener(onViewHolderClickListener);
            return holder;
        }

        @Override
        public int itemLayout(int viewType) {
            return R.layout.holder_view_activity_label;
        }

        private void resizeWidth(View itemView, int position) {
            int dimen = fragment.getDimension(R.dimen.ui_base_border_size_normal);
            int margin = fragment.getDimension(R.dimen.ui_static_dp_1);
            int width = (fragment.getScreenWidth() - layoutPadding * 2 - (dimen * DFT_SPAN_COUNT) - margin * 8) / DFT_SPAN_COUNT;
            ViewGroup.LayoutParams params = itemView.getLayoutParams();
            params.width = width * getItemSpanSize(position);
            itemView.setLayoutParams(params);
        }

        @Override
        public void onBindHolderOfView(ActivityLabelViewHolder holder, int position, @Nullable Dictionary item) {
            holder.showContent(item);
            resizeWidth(holder.itemView, position);
        }

        @Override
        protected int comparator(Dictionary item1, Dictionary item2) {
            return 0;
        }
    }

    private OnDictionarySelectedListener selectedListener;

    public DictionaryHelper setOnDictionarySelectedListener(OnDictionarySelectedListener l) {
        selectedListener = l;
        return this;
    }

    public interface OnDictionarySelectedListener {
        void onSelected(String selectedType, String selectedName);
    }
}
