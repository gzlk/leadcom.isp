package com.gzlk.android.isp.holder;

import android.view.View;
import android.widget.TextView;

import com.gzlk.android.isp.R;
import com.gzlk.android.isp.fragment.base.BaseFragment;
import com.gzlk.android.isp.fragment.individual.DocumentNewFragment;
import com.hlk.hlklib.lib.inject.Click;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.inject.ViewUtility;
import com.hlk.hlklib.lib.view.CustomTextView;

/**
 * <b>功能描述：</b><br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/04/20 14:18 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/04/20 14:18 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class IndividualFunctionViewHolder extends BaseViewHolder {

    @ViewId(R.id.ui_tool_individual_main_functions_1)
    private TextView textView1;
    //@ViewId(R.id.ui_tool_individual_main_functions_2)
    @ViewId(R.id.ui_tool_individual_main_functions_2_text)
    private TextView textView2;
    @ViewId(R.id.ui_tool_individual_main_functions_2_new)
    private CustomTextView textView2Icon;
    @ViewId(R.id.ui_tool_individual_main_functions_3)
    private TextView textView3;
    private TextView textView4;

    public IndividualFunctionViewHolder(View itemView, BaseFragment fragment) {
        super(itemView, fragment);
        ViewUtility.bind(this, itemView);
    }

    @Click({R.id.ui_tool_individual_main_functions_1,
            R.id.ui_tool_individual_main_functions_2,
            R.id.ui_tool_individual_main_functions_3,
            R.id.ui_tool_individual_main_functions_2_new})
    private void elementClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.ui_tool_individual_main_functions_1:
                changeFunction(0);
                break;
            case R.id.ui_tool_individual_main_functions_2:
                changeFunction(1);
                openActivity(DocumentNewFragment.class.getName(), "", true, true);
                break;
            case R.id.ui_tool_individual_main_functions_3:
                changeFunction(2);
                break;
            case R.id.ui_tool_individual_main_functions_2_new:
                // 新建档案
                tooltipCreateDocument();
                break;
        }
    }

    private void tooltipCreateDocument() {
//        Tooltip.make(getContext(),
//                new Tooltip.Builder(101)
//                        .anchor(textView2Icon, Tooltip.Gravity.BOTTOM)
//                        .closePolicy(new Tooltip.ClosePolicy()
//                                .insidePolicy(true, false)
//                                .outsidePolicy(true, false), 3000)
//                        .activateDelay(5000)
//                        .showDelay(300)
//                        .text(StringHelper.getString(R.string.ui_text_document_create_tooltip))
//                        .maxWidth(500)
//                        .withArrow(true)
//                        .withOverlay(true)
//                        //.typeface(mYourCustomFont)
//                        .floatingAnimation(Tooltip.AnimationBuilder.DEFAULT)
//                        .build()
//        ).show();
    }

    private int selected = -1;

    public void setSelected(int index) {
        changeFunction(index);
    }

    private void changeFunction(int index) {
        if (selected != index) {
            selected = index;
            int color1 = getColor(R.color.textColor);
            int color2 = getColor(R.color.colorPrimary);

            if (null != textView1) {
                textView1.setTextColor(index == 0 ? color2 : color1);
            }
            if (null != textView2) {
                textView2.setTextColor(index == 1 ? color2 : color1);
            }
            if (null != textView2Icon) {
                textView2Icon.setTextColor(index == 1 ? color2 : color1);
                textView2Icon.setVisibility(index == 1 ? View.VISIBLE : View.GONE);
            }
            if (null != textView3) {
                textView3.setTextColor(index == 2 ? color2 : color1);
            }

            if (null != mOnFunctionChangeListener) {
                mOnFunctionChangeListener.onChange(index);
            }
        }
    }

    private OnFunctionChangeListener mOnFunctionChangeListener;

    public void addOnFunctionChangeListener(OnFunctionChangeListener l) {
        mOnFunctionChangeListener = l;
    }

    /**
     * 选择了不同的个人信息列表
     */
    public interface OnFunctionChangeListener {
        void onChange(int index);
    }
}
