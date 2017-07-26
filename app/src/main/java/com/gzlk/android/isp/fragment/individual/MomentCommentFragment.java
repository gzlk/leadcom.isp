package com.gzlk.android.isp.fragment.individual;

import android.os.Bundle;

import com.gzlk.android.isp.R;
import com.gzlk.android.isp.fragment.base.BaseFragment;
import com.gzlk.android.isp.fragment.base.BaseLayoutSupportFragment;
import com.gzlk.android.isp.listener.OnTitleButtonClickListener;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.view.ClearEditText;

/**
 * <b>功能描述：</b>评论发布页面<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/07/24 21:06 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/07/24 21:06 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class MomentCommentFragment extends BaseLayoutSupportFragment {

    public static MomentCommentFragment newInstance(String params) {
        return new MomentCommentFragment();
    }

    public static final int REQ_COMMENT = ACTIVITY_BASE_REQUEST + 100;

    public static void open(BaseFragment fragment) {
        fragment.openActivity(MomentCommentFragment.class.getName(), "", REQ_COMMENT, true, false);
    }

    @ViewId(R.id.ui_individual_moment_comment_content)
    private ClearEditText commentContent;

    @Override
    public int getLayout() {
        return R.layout.fragment_comment;
    }

    @Override
    protected void getParamsFromBundle(Bundle bundle) {

    }

    @Override
    protected boolean shouldSetDefaultTitleEvents() {
        return true;
    }

    @Override
    protected void saveParamsToBundle(Bundle bundle) {

    }

    @Override
    protected void destroyView() {

    }

    @Override
    public void doingInResume() {
        setCustomTitle(R.string.ui_base_text_comment);
        setRightText(R.string.ui_base_text_send);
        setRightTitleClickListener(new OnTitleButtonClickListener() {
            @Override
            public void onClick() {
                String text = commentContent.getValue();
                if (!isEmpty(text)) {
                    // 返回评论内容
                    resultData(text);
                }
            }
        });
    }
}
