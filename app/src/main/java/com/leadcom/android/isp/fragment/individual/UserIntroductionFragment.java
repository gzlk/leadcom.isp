package com.leadcom.android.isp.fragment.individual;

import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import com.hlk.hlklib.lib.inject.ViewId;
import com.leadcom.android.isp.R;
import com.leadcom.android.isp.fragment.base.BaseFragment;
import com.leadcom.android.isp.fragment.base.BaseLayoutSupportFragment;
import com.leadcom.android.isp.lib.view.ImageDisplayer;


/**
 * <b>功能描述：</b>用户个人简介页面<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2018/04/01 17:02 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>version: 1.0.0 <br />
 * <b>修改时间：</b>2017/10/04 18:50 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */
public class UserIntroductionFragment extends BaseLayoutSupportFragment {

    private static final String PARAM_NAME = "uif_name";
    private static final String PARAM_GROUP = "uif_group";
    private static final String PARAM_HEAD = "uif_header";
    private static final String PARAM_DATE = "uif_date";
    private static final String PARAM_INTRO = "uif_intro";

    public static UserIntroductionFragment newInstance(Bundle bundle) {
        UserIntroductionFragment uif = new UserIntroductionFragment();
        uif.setArguments(bundle);
        return uif;
    }

    public static void open(BaseFragment fragment, String groupId, String name, String header, String date, String intro) {
        Bundle bundle = new Bundle();
        bundle.putString(PARAM_NAME, name);
        bundle.putString(PARAM_GROUP, groupId);
        bundle.putString(PARAM_HEAD, header);
        bundle.putString(PARAM_DATE, date);
        bundle.putString(PARAM_INTRO, intro);
        fragment.openActivity(UserIntroductionFragment.class.getName(), bundle, true, false);
    }

    @ViewId(R.id.ui_tool_view_document_user_header_layout)
    private View headerLayout;
    @ViewId(R.id.ui_tool_view_document_user_header_image)
    private ImageDisplayer headerView;
    @ViewId(R.id.ui_tool_view_document_user_header_name)
    private TextView nameView;
    @ViewId(R.id.ui_tool_view_document_user_header_time)
    private TextView timeView;
    @ViewId(R.id.ui_main_personality_introduction_text)
    private TextView introView;

    private String name, groupId, header, date, intro;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        setCustomTitle(isEmpty(groupId) ? R.string.ui_text_personality_introduction_fragment_title : R.string.ui_organization_introduction_fragment_title);
        headerLayout.setVisibility(View.VISIBLE);
        headerView.displayImage(header, getDimension(R.dimen.ui_base_user_header_image_size_small), false, false);
        nameView.setText(name);
        timeView.setText(formatDate(date, R.string.ui_base_text_date_format));
        introView.setText(Html.fromHtml(intro));
    }

    @Override
    public int getLayout() {
        return R.layout.fragment_main_personality_introduction;
    }

    @Override
    protected void getParamsFromBundle(Bundle bundle) {
        name = bundle.getString(PARAM_NAME, "");
        groupId = bundle.getString(PARAM_GROUP, "");
        header = bundle.getString(PARAM_HEAD, "");
        date = bundle.getString(PARAM_DATE, "");
        intro = bundle.getString(PARAM_INTRO, "");
    }

    @Override
    public void doingInResume() {

    }

    @Override
    protected boolean shouldSetDefaultTitleEvents() {
        return true;
    }

    @Override
    protected void saveParamsToBundle(Bundle bundle) {
        bundle.putString(PARAM_NAME, name);
        bundle.putString(PARAM_GROUP, groupId);
        bundle.putString(PARAM_HEAD, header);
        bundle.putString(PARAM_DATE, date);
        bundle.putString(PARAM_INTRO, intro);
    }

    @Override
    protected void destroyView() {

    }
}
