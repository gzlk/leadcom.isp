package com.gzlk.android.isp.fragment.main;

import android.view.View;
import android.widget.TextView;

import com.gzlk.android.isp.R;
import com.gzlk.android.isp.fragment.base.BaseViewPagerSupportFragment;
import com.gzlk.android.isp.fragment.login.CodeVerifyFragment;
import com.gzlk.android.isp.fragment.login.PhoneVerifyFragment;
import com.gzlk.android.isp.fragment.login.ResetPasswordFragment;
import com.gzlk.android.isp.fragment.login.SignUpFragment;
import com.hlk.hlklib.lib.inject.Click;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.view.CustomTextView;

/**
 * <b>功能描述：</b>首页<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/04/06 16:43 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/04/06 16:43 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class MainFragment extends BaseViewPagerSupportFragment {
    /**
     * 新建MainFragment的时候传入的参数，以此当作初始化显示的页面（也即ViewPager当前显示的页面的index），int型
     */
    public static final String PARAM_SELECTED = "mf_param1";

    @ViewId(R.id.ui_tool_main_bottom_icon_1)
    private CustomTextView iconView1;
    @ViewId(R.id.ui_tool_main_bottom_icon_2)
    private CustomTextView iconView2;
    @ViewId(R.id.ui_tool_main_bottom_icon_3)
    private CustomTextView iconView3;
    @ViewId(R.id.ui_tool_main_bottom_icon_4)
    private CustomTextView iconView4;
    @ViewId(R.id.ui_tool_main_bottom_text_1)
    private TextView textView1;
    @ViewId(R.id.ui_tool_main_bottom_text_2)
    private TextView textView2;
    @ViewId(R.id.ui_tool_main_bottom_text_3)
    private TextView textView3;
    @ViewId(R.id.ui_tool_main_bottom_text_4)
    private TextView textView4;

    @Override
    public int getLayout() {
        return R.layout.fragment_main;
    }

    @Override
    public void doingInResume() {
        super.doingInResume();
        setLeftIcon(R.string.ui_icon_query);
        setLeftText(0);
        setRightIcon(R.string.ui_icon_chat);
    }

    @Override
    protected void initializeFragments() {
        if (mFragments.size() <= 0) {
            mFragments.add(SignUpFragment.newInstance("0"));
            mFragments.add(PhoneVerifyFragment.newInstance("0"));
            mFragments.add(CodeVerifyFragment.newInstance("0"));
            mFragments.add(ResetPasswordFragment.newInstance("1"));
        }
    }

    @Override
    protected void viewPagerSelectionChanged(int position) {
        int color1 = getColor(R.color.textColorHintDark);
        int color2 = getColor(R.color.colorPrimary);

        iconView1.setTextColor(position == 0 ? color2 : color1);
        textView1.setTextColor(position == 0 ? color2 : color1);

        iconView2.setTextColor(position == 1 ? color2 : color1);
        textView2.setTextColor(position == 1 ? color2 : color1);

        iconView3.setTextColor(position == 2 ? color2 : color1);
        textView3.setTextColor(position == 2 ? color2 : color1);

        iconView4.setTextColor(position == 3 ? color2 : color1);
        textView4.setTextColor(position == 3 ? color2 : color1);
    }

    @Override
    protected void destroyView() {

    }

    @Click({R.id.ui_tool_main_bottom_clickable_1, R.id.ui_tool_main_bottom_clickable_2,
            R.id.ui_tool_main_bottom_clickable_3, R.id.ui_tool_main_bottom_clickable_4})
    private void elementClick(View view) {
        int id = view.getId();
        switch (id) {
            case R.id.ui_tool_main_bottom_clickable_1:
                setDisplayPage(0);
                break;
            case R.id.ui_tool_main_bottom_clickable_2:
                setDisplayPage(1);
                break;
            case R.id.ui_tool_main_bottom_clickable_3:
                setDisplayPage(2);
                break;
            case R.id.ui_tool_main_bottom_clickable_4:
                setDisplayPage(3);
                break;
        }
    }
}
