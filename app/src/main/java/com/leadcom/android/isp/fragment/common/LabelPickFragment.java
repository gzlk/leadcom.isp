package com.leadcom.android.isp.fragment.common;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.hlk.hlklib.lib.view.ClearEditText;
import com.leadcom.android.isp.R;
import com.leadcom.android.isp.adapter.RecyclerViewAdapter;
import com.leadcom.android.isp.api.activity.ActLabelRequest;
import com.leadcom.android.isp.api.archive.DictionaryRequest;
import com.leadcom.android.isp.api.listener.OnMultipleRequestListener;
import com.leadcom.android.isp.etc.Utils;
import com.leadcom.android.isp.fragment.base.BaseFragment;
import com.leadcom.android.isp.fragment.base.BaseSwipeRefreshSupportFragment;
import com.leadcom.android.isp.helper.StringHelper;
import com.leadcom.android.isp.helper.ToastHelper;
import com.leadcom.android.isp.helper.popup.DialogHelper;
import com.leadcom.android.isp.holder.BaseViewHolder;
import com.leadcom.android.isp.holder.common.LabelViewHolder;
import com.leadcom.android.isp.holder.common.TextViewHolder;
import com.leadcom.android.isp.lib.Json;
import com.leadcom.android.isp.listener.OnTitleButtonClickListener;
import com.leadcom.android.isp.listener.OnViewHolderClickListener;
import com.leadcom.android.isp.model.Model;
import com.leadcom.android.isp.model.activity.Label;
import com.leadcom.android.isp.model.archive.Dictionary;

import java.util.ArrayList;
import java.util.List;

/**
 * <b>功能描述：</b>活动标签拾取器<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/05/29 20:13 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/05/29 20:13 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class LabelPickFragment extends BaseSwipeRefreshSupportFragment {

    private static final String PARAM_SELECTED = "lpf_selected";
    private static final String PARAM_LABEL_TYPE = "lpf_label_type";
    private static final String PARAM_MAX = "lpf_max_selectable";

    /**
     * 档案性质
     */
    public static final int TYPE_PROPERTY = 1;
    /**
     * 档案类型
     */
    public static final int TYPE_CATEGORY = 2;
    /**
     * 档案标签
     */
    public static final int TYPE_LABEL = 3;

    public static LabelPickFragment newInstance(Bundle bundle) {
        LabelPickFragment lpf = new LabelPickFragment();
        lpf.setArguments(bundle);
        return lpf;
    }

    /**
     * 打开选择页
     *
     * @param fragment 实现打开的host
     * @param type     类型1=档案性质；2=档案类型；3=档案标签
     * @param selected 已选中的项目
     */
    public static void open(BaseFragment fragment, int type, ArrayList<String> selected) {
        Bundle bundle = new Bundle();
        bundle.putString(PARAM_QUERY_ID, String.valueOf(type));
        bundle.putInt(PARAM_MAX, type == TYPE_LABEL ? 2 : 1);
        bundle.putStringArrayList(PARAM_SELECTED, selected);
        fragment.openActivity(LabelPickFragment.class.getName(), bundle, request(type), true, false);
    }

    private static int request(int type) {
        switch (type) {
            case TYPE_CATEGORY:
                return REQUEST_CATEGORY;
            case TYPE_PROPERTY:
                return REQUEST_PROPERTY;
            default:
                return REQUEST_LABEL;
        }
    }

    public static void open(BaseFragment fragment, int type, String selected) {
        ArrayList<String> select = new ArrayList<>();
        if (!isEmpty(selected)) {
            select.add(selected);
        }
        open(fragment, type, select);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        layoutType = TYPE_FLEX;
        gridOrientation = StaggeredGridLayoutManager.VERTICAL;
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    protected void getParamsFromBundle(Bundle bundle) {
        super.getParamsFromBundle(bundle);
        labelNames = bundle.getStringArrayList(PARAM_SELECTED);
        if (null == labelNames) {
            labelNames = new ArrayList<>();
        }
        mType = Integer.valueOf(mQueryId);
        // 默认只可选中一个
        mMaxSelectable = bundle.getInt(PARAM_MAX, 1);
    }

    @Override
    protected void saveParamsToBundle(Bundle bundle) {
        super.saveParamsToBundle(bundle);
        bundle.putInt(PARAM_LABEL_TYPE, mType);
        bundle.putInt(PARAM_MAX, mMaxSelectable);
        bundle.putStringArrayList(PARAM_SELECTED, labelNames);
    }

    private ArrayList<String> labelNames;
    private LabelAdapter mAdapter;
    private int mType, mMaxSelectable;

    @Override
    protected void onDelayRefreshComplete(@DelayType int type) {

    }

    private String fetchingTitle() {
        int res;
        switch (mType) {
            case TYPE_CATEGORY:
                res = R.string.ui_text_archive_details_editor_setting_category_title;
                break;
            case TYPE_LABEL:
                res = R.string.ui_text_document_label_picker_fragment_title;
                break;
            default:
                res = R.string.ui_text_archive_details_editor_setting_property_title;
                break;
        }
        return StringHelper.getString(res);
    }

    @Override
    public void doingInResume() {
        setCustomTitle(fetchingTitle());
        setRightText(R.string.ui_base_text_finish);
        setRightTitleClickListener(new OnTitleButtonClickListener() {
            @Override
            public void onClick() {
                resultSelected();
            }
        });
        initializeAdapter();
    }

    private void resultSelected() {
        if (mType == TYPE_LABEL) {
            labelNames.clear();
            for (int i = 0, size = mAdapter.getItemCount(); i < size; i++) {
                Model model = mAdapter.get(i);
                if (model.isSelected() && model instanceof Label) {
                    Label label = (Label) model;
                    if (!labelNames.contains(label.getName())) {
                        labelNames.add(label.getName());
                    }
                    // 使用次数加1
                    label.setSignaNum(label.getSignaNum() + 1);
                    Label.save(label);
                }
            }
            resultData(Json.gson().toJson(labelNames));
        } else {
            String name = "";
            for (int i = 0, size = mAdapter.getItemCount(); i < size; i++) {
                Model model = mAdapter.get(i);
                if (model.isSelected() && model instanceof Dictionary) {
                    Dictionary dictionary = (Dictionary) model;
                    name = dictionary.getName();
                }
            }
            if (!isEmpty(name)) {
                resultData(name);
            }
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
        fetching();
    }

    @Override
    protected void onLoadingMore() {
        isLoadingComplete(true);
    }

    @Override
    protected String getLocalPageTag() {
        return null;
    }

    private void initializeAdapter() {
        if (null == mAdapter) {
            mAdapter = new LabelAdapter();
            mRecyclerView.setAdapter(mAdapter);
            setTestData();
            fetching();
        }
    }

    private void fetching() {
        switch (mType) {
            case TYPE_CATEGORY:
            case TYPE_PROPERTY:
                loadingDictionary();
                break;
            case TYPE_LABEL:
                loadingTopestLabels();
                break;
        }
    }

    // 获取热门标签
    private void loadingTopestLabels() {
        ActLabelRequest.request().setOnMultipleRequestListener(new OnMultipleRequestListener<Label>() {
            @Override
            public void onResponse(List<Label> list, boolean success, int totalPages, int pageSize, int total, int pageNumber) {
                super.onResponse(list, success, totalPages, pageSize, total, pageNumber);
                updateList(list);
                stopRefreshing();
            }
        }).getTopLabels(30);
    }

    private void loadingDictionary() {
        DictionaryRequest.request().setOnMultipleRequestListener(new OnMultipleRequestListener<Dictionary>() {
            @Override
            public void onResponse(List<Dictionary> list, boolean success, int totalPages, int pageSize, int total, int pageNumber) {
                super.onResponse(list, success, totalPages, pageSize, total, pageNumber);
                if (success && null != list) {
                    for (Dictionary dictionary : list) {
                        if (dictionary.getCode() <= 0) continue;
                        dictionary.setSelected(labelNames.contains(dictionary.getName()));
                        // 新的标签始终加在倒数第三个位置
                        int index = mAdapter.getItemCount() - 2;
                        if (!mAdapter.exist(dictionary)) {
                            mAdapter.add(dictionary, index);
                        }
                    }
                }
                readLocalDictionary();
                stopRefreshing();
            }
        }).list(mType == TYPE_CATEGORY ? Dictionary.Type.ARCHIVE_TYPE : Dictionary.Type.ARCHIVE_NATURE);
    }

    private void updateList(List<Label> list) {
        if (null != list) {
            for (Label label : list) {
                label.setSelected(labelNames.contains(label.getName()));
                // 新的标签始终加在倒数第三个位置
                int index = mAdapter.getItemCount() - 2;
                if (!mAdapter.exist(label)) {
                    mAdapter.add(label, index);
                }
            }
        }
        readLocalLabels();
    }

    private void setTestData() {
        mAdapter.add(new Model() {{
            setId(mType == TYPE_LABEL ? StringHelper.getString(R.string.ui_archive_label_picker_hotest) : fetchingTitle());
        }});
        mAdapter.add(new Model() {{
            setId(StringHelper.getString(R.string.ui_archive_label_picker_create_title, ""));
        }});
        mAdapter.add(new Label() {{
            setId("0");
            setName(StringHelper.getString(R.string.ui_archive_label_picker_self_defined));
        }});
    }

    private void readLocalLabels() {
        List<Label> locals = Label.getLocal();
        if (null != locals) {
            for (Label label : locals) {
                label.setSelected(labelNames.contains(label.getName()));
                label.setLocal(true);
                mAdapter.update(label);
            }
        }
    }

    private void readLocalDictionary() {
        List<Dictionary> local = Dictionary.getLocal(mType == TYPE_CATEGORY ? Dictionary.Type.ARCHIVE_TYPE : Dictionary.Type.ARCHIVE_NATURE);
        if (null != local && local.size() > 0) {
            for (Dictionary dictionary : local) {
                dictionary.setLocal(true);
                dictionary.setSelected(labelNames.contains(dictionary.getName()));
                mAdapter.update(dictionary);
            }
        }
    }

    private OnViewHolderClickListener onViewHolderClickListener = new OnViewHolderClickListener() {
        @Override
        public void onClick(int index) {
            Model model = mAdapter.get(index);
            if (model.getId().equals("0")) {
                // 自定义
                openCreateDialog();
            } else if (mMaxSelectable > 1) {
                int selected = selected();
                // 如果已选择的数量超过2个且当前项未被选中则说明是要选中一个新的，提醒
                if (selected >= mMaxSelectable && !model.isSelected()) {
                    ToastHelper.make().showMsg(StringHelper.getString(R.string.ui_archive_label_picker_picked_max, mMaxSelectable, fetchingTitle()));
                } else {
                    model.setSelected(!model.isSelected());
                    mAdapter.notifyItemChanged(index);
                }
            } else {
                model.setSelected(!model.isSelected());
                mAdapter.notifyItemChanged(index);
                for (int i = 0, size = mAdapter.getItemCount(); i < size; i++) {
                    Model m = mAdapter.get(i);
                    if (m.isSelected() && !m.getId().equals(model.getId())) {
                        m.setSelected(false);
                        mAdapter.update(m);
                    }
                }
            }
        }
    };

    private int selected() {
        int cnt = 0;
        for (int i = 0, size = mAdapter.getItemCount(); i < size; i++) {
            if (mAdapter.get(i).isSelected()) {
                cnt++;
            }
        }
        return cnt;
    }

    private View dialogView;
    private ClearEditText labelName;

    private void openCreateDialog() {
        DialogHelper.init(Activity()).addOnDialogInitializeListener(new DialogHelper.OnDialogInitializeListener() {
            @Override
            public View onInitializeView() {
                if (null == dialogView) {
                    dialogView = LayoutInflater.from(mRecyclerView.getContext())
                            .inflate(R.layout.popup_dialog_activity_label_creator, null, false);
                    labelName = dialogView.findViewById(R.id.ui_dialog_activity_label_creator_name);
                }
                String title = StringHelper.getString(R.string.ui_archive_label_picker_create_title, fetchingTitle());
                ((TextView) dialogView.findViewById(R.id.ui_dialog_activity_label_creator_title)).setText(title);
                title = StringHelper.getString(R.string.ui_archive_label_picker_create_hint, fetchingTitle());
                ((ClearEditText) dialogView.findViewById(R.id.ui_dialog_activity_label_creator_name)).setTextHint(title);
                labelName.setValue("");
                return dialogView;
            }

            @Override
            public void onBindData(View dialogView, DialogHelper helper) {

            }
        }).addOnDialogConfirmListener(new DialogHelper.OnDialogConfirmListener() {
            @Override
            public boolean onConfirm() {
                Utils.hidingInputBoard(labelName);
                selfDefineItem();
                return true;
            }
        }).setPopupType(DialogHelper.SLID_IN_BOTTOM).show();
    }

    private void selfDefineItem() {
        if (null != labelName) {
            String name = labelName.getValue();
            if (isEmpty(name)) {
                ToastHelper.make().showMsg(StringHelper.getString(R.string.ui_archive_label_picker_create_invalid_name, fetchingTitle()));
                return;
            }
            if (mType == TYPE_LABEL) {
                Label label = new Label();
                label.setId(String.valueOf(Utils.timestamp()));
                // 本地新建的标签
                label.setLocal(true);
                label.setName(name);
                Label.save(label);
                mAdapter.add(label);
            } else {
                Dictionary dictionary = new Dictionary();
                dictionary.setId(String.valueOf(Utils.timestamp()));
                dictionary.setName(name);
                dictionary.setTypeName(mType == TYPE_CATEGORY ? Dictionary.Type.NAME_TYPE : Dictionary.Type.NAME_NATURE);
                dictionary.setLocal(true);
                dictionary.setCode(111);
                dictionary.setTypeCode(mType == TYPE_CATEGORY ? Dictionary.Type.ARCHIVE_TYPE : Dictionary.Type.ARCHIVE_NATURE);
                Dictionary.save(dictionary);
                mAdapter.add(dictionary);
            }
        }
    }

    private class LabelAdapter extends RecyclerViewAdapter<BaseViewHolder, Model> {

        private static final int VT_TITLE = 0, VT_LABEL = 1;

        @Override
        public BaseViewHolder onCreateViewHolder(View itemView, int viewType) {
            if (viewType == VT_LABEL) {
                LabelViewHolder holder = new LabelViewHolder(itemView, LabelPickFragment.this);
                holder.addOnViewHolderClickListener(onViewHolderClickListener);
                return holder;
            } else {
                TextViewHolder holder = new TextViewHolder(itemView, LabelPickFragment.this);
                holder.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
                holder.showBottomLine(false);
                return holder;
            }
        }

        @Override
        public boolean isItemNeedFullLine(int position) {
            Model model = get(position);
            // 不是label的话，就全行占满
            return !(model instanceof Label) || !(model instanceof Dictionary);
        }

        @Override
        public int getItemViewType(int position) {
            Model model = get(position);
            return ((model instanceof Label) || (model instanceof Dictionary)) ? VT_LABEL : VT_TITLE;
        }

        @Override
        public int itemLayout(int viewType) {
            return viewType == VT_LABEL ? R.layout.holder_view_activity_label : R.layout.holder_view_text_olny;
        }

        private void resizeWidth(View itemView, int position) {
            int dimen = getDimension(R.dimen.ui_base_border_size_normal);
            int margin = getDimension(R.dimen.ui_static_dp_1);
            int width = (getScreenWidth() - (dimen * gridSpanCount) - margin * 8) / gridSpanCount;
            ViewGroup.LayoutParams params = itemView.getLayoutParams();
            params.width = width * getItemSpanSize(position);
            itemView.setLayoutParams(params);
        }

        @Override
        public void onBindHolderOfView(BaseViewHolder holder, int position, @Nullable Model item) {
            if (holder instanceof LabelViewHolder) {
                ((LabelViewHolder) holder).showContent(item);
            } else if (holder instanceof TextViewHolder) {
                assert item != null;
                ((TextViewHolder) holder).showContent(item.getId());
            }
            //resizeWidth(holder.itemView, position);
        }

        @Override
        protected int comparator(Model item1, Model item2) {
            return 0;
        }
    }
}
