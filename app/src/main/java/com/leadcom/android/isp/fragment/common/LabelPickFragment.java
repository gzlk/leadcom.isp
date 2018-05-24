package com.leadcom.android.isp.fragment.common;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.gson.reflect.TypeToken;
import com.leadcom.android.isp.R;
import com.leadcom.android.isp.adapter.RecyclerViewAdapter;
import com.leadcom.android.isp.api.activity.ActLabelRequest;
import com.leadcom.android.isp.api.listener.OnMultipleRequestListener;
import com.leadcom.android.isp.etc.Utils;
import com.leadcom.android.isp.fragment.base.BaseFragment;
import com.leadcom.android.isp.fragment.base.BaseSwipeRefreshSupportFragment;
import com.leadcom.android.isp.helper.popup.DialogHelper;
import com.leadcom.android.isp.helper.ToastHelper;
import com.leadcom.android.isp.holder.BaseViewHolder;
import com.leadcom.android.isp.holder.activity.ActivityLabelViewHolder;
import com.leadcom.android.isp.holder.common.TextViewHolder;
import com.leadcom.android.isp.lib.Json;
import com.leadcom.android.isp.listener.OnTitleButtonClickListener;
import com.leadcom.android.isp.listener.OnViewHolderClickListener;
import com.leadcom.android.isp.model.Model;
import com.leadcom.android.isp.model.activity.Label;
import com.hlk.hlklib.lib.view.ClearEditText;

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

    private static final String PARAM_SELECTED_NAMES = "lpf_selected_label_names";
    private static final String PARAM_ACT_ID = "lpf_activity_id";
    private static final String PARAM_LABEL_TYPE = "lpf_label_type";

    /**
     * 活动标签
     */
    public static final int TYPE_ACTIVITY = 1;
    /**
     * 档案标签
     */
    public static final int TYPE_ARCHIVE = 2;

    public static LabelPickFragment newInstance(String params) {
        LabelPickFragment lpf = new LabelPickFragment();
        String[] strings = splitParameters(params);
        Bundle bundle = new Bundle();
        // 传入的组织id
        bundle.putString(PARAM_QUERY_ID, strings[0]);
        // 活动的id
        bundle.putString(PARAM_ACT_ID, strings[1]);
        // 标签类型
        bundle.putInt(PARAM_LABEL_TYPE, Integer.valueOf(strings[2]));
        // 已选择了的标签列表
        bundle.putString(PARAM_SELECTED_NAMES, replaceJson(strings[3], true));
        lpf.setArguments(bundle);
        return lpf;
    }

    public static void open(BaseFragment fragment, String groupId, String activityId, int type, String selected) {
        String params = format("%s,%s,%d,%s", groupId, activityId, type, selected);
        fragment.openActivity(LabelPickFragment.class.getName(), params, REQUEST_LABEL, true, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        layoutType = TYPE_SGRID;
        gridOrientation = StaggeredGridLayoutManager.VERTICAL;
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    protected void getParamsFromBundle(Bundle bundle) {
        super.getParamsFromBundle(bundle);
        String json = bundle.getString(PARAM_SELECTED_NAMES, "[]");
        labelNames = Json.gson().fromJson(json, new TypeToken<ArrayList<String>>() {
        }.getType());
        activityId = bundle.getString(PARAM_ACT_ID, "");
        mType = bundle.getInt(PARAM_LABEL_TYPE, TYPE_ACTIVITY);
    }

    @Override
    protected void saveParamsToBundle(Bundle bundle) {
        super.saveParamsToBundle(bundle);
        bundle.putString(PARAM_SELECTED_NAMES, Json.gson().toJson(labelNames));
        bundle.putString(PARAM_ACT_ID, activityId);
        bundle.putInt(PARAM_LABEL_TYPE, mType);
    }

    private String activityId;
    private ArrayList<String> labelNames;
    private LabelAdapter mAdapter;
    private int mType;

    @Override
    protected void onDelayRefreshComplete(@DelayType int type) {

    }

    @Override
    public void doingInResume() {
        setCustomTitle(mType == TYPE_ACTIVITY ? R.string.ui_activity_label_picker_fragment_title : R.string.ui_text_document_label_picker_fragment_title);
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
        loadingTopestLabels();
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
            loadingTopestLabels();
        }
    }

    private void loadingLabels() {
        ActLabelRequest.request().setOnMultipleRequestListener(new OnMultipleRequestListener<Label>() {
            @Override
            public void onResponse(List<Label> list, boolean success, int totalPages, int pageSize, int total, int pageNumber) {
                super.onResponse(list, success, totalPages, pageSize, total, pageNumber);
            }
        }).list(activityId);
    }

    // 获取热门标签
    private void loadingTopestLabels() {
        ActLabelRequest.request().setOnMultipleRequestListener(new OnMultipleRequestListener<Label>() {
            @Override
            public void onResponse(List<Label> list, boolean success, int totalPages, int pageSize, int total, int pageNumber) {
                super.onResponse(list, success, totalPages, pageSize, total, pageNumber);
                if (success) {
                    updateList(list);
                }
                stopRefreshing();
            }
        }).getTopLabels(30);
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
        readLocals();
    }

    private void setTestData() {
        mAdapter.add(new Model() {{
            setId("热门标签");
        }});
        mAdapter.add(new Model() {{
            setId("其他");
        }});
        mAdapter.add(new Label() {{
            setId("0");
            setName("+自定义");
        }});
    }

    private void readLocals() {
        List<Label> locals = Label.getLocal();
        if (null != locals) {
            for (Label label : locals) {
                label.setSelected(labelNames.contains(label.getName()));
                if (!mAdapter.exist(label)) {
                    mAdapter.add(label);
                }
            }
        }
    }

    private OnViewHolderClickListener onViewHolderClickListener = new OnViewHolderClickListener() {
        @Override
        public void onClick(int index) {
            Label label = (Label) mAdapter.get(index);
            if (label.getId().equals("0")) {
                // 自定义
                openCreateDialog();
            } else {
                int selected = selected();
                // 如果已选择的数量超过2个且当前项未被选中则说明是要选中一个新的，提醒
                if (selected >= 2 && !label.isSelected()) {
                    ToastHelper.make().showMsg(R.string.ui_activity_label_picker_picked_max);
                } else {
                    label.setSelected(!label.isSelected());
                    mAdapter.notifyItemChanged(index);
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
                createLabel();
                return true;
            }
        }).setPopupType(DialogHelper.SLID_IN_BOTTOM).show();
    }

    private void createLabel() {
        if (null != labelName) {
            if (isEmpty(labelName.getValue())) {
                ToastHelper.make().showMsg("无效的活动类型");
                return;
            }
            Label label = new Label();
            label.setId(String.valueOf(Utils.timestamp()));
            // 本地新建的标签
            label.setLocal(true);
            label.setName(labelName.getValue());
            Label.save(label);
            mAdapter.add(label);
        }
    }

    private class LabelAdapter extends RecyclerViewAdapter<BaseViewHolder, Model> {

        private static final int VT_TITLE = 0, VT_LABEL = 1;

        @Override
        public BaseViewHolder onCreateViewHolder(View itemView, int viewType) {
            if (viewType == VT_LABEL) {
                ActivityLabelViewHolder holder = new ActivityLabelViewHolder(itemView, LabelPickFragment.this);
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
            // 不是label的话，就全行占满
            return !(get(position) instanceof Label);
        }

        @Override
        public int getItemSpanSize(int position) {
//            Model model = get(position);
//            if (model instanceof Label) {
//                Label label = (Label) model;
//                return (!isEmpty(label.getName()) && label.getName().length() > 4) ? 2 : 1;
//            } else {
//                return 1;
//            }
            return super.getItemSpanSize(position);
        }

        @Override
        public int getItemViewType(int position) {
            return (get(position) instanceof Label) ? VT_LABEL : VT_TITLE;
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
            if (holder instanceof ActivityLabelViewHolder) {
                ((ActivityLabelViewHolder) holder).showContent((Label) item);
            } else if (holder instanceof TextViewHolder) {
                assert item != null;
                ((TextViewHolder) holder).showContent(item.getId());
            }
            resizeWidth(holder.itemView, position);
        }

        @Override
        protected int comparator(Model item1, Model item2) {
            return 0;
        }
    }
}
