package com.gzlk.android.isp.nim.action;

import android.app.Activity;
import android.content.Intent;
import android.support.v4.content.ContextCompat;

import com.gzlk.android.isp.R;
import com.gzlk.android.isp.fragment.base.BaseFragment;
import com.gzlk.android.isp.helper.StringHelper;
import com.netease.nim.uikit.session.actions.BaseAction;
import com.yanzhenjie.album.Album;

/**
 * <b>功能描述：</b>网易云信选择照片的Action<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/06/07 14:44 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/06/07 14:44 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public abstract class ChooseImageAction extends BaseAction {

    private boolean chooseByCamera;
    private static final int maxSelectable = 9;

    /**
     * 构造函数
     *
     * @param iconResId 图标 res id
     * @param titleId   图标标题的string res id
     */
    protected ChooseImageAction(int iconResId, int titleId, boolean byCamera) {
        super(iconResId, titleId);
        chooseByCamera = byCamera;
    }

    @Override
    public void onClick() {
        if (chooseByCamera) {
            startCamera();
        } else {
            startGallery();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode== Activity.RESULT_OK){
            switch (requestCode){
                case BaseFragment.REQUEST_CAMERA:
                    break;
                case BaseFragment.REQUEST_GALLERY:
                    break;
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    private int getColor(int resId) {
        return ContextCompat.getColor(getActivity(), resId);
    }

    private String getString(int resId, Object... objects) {
        return getActivity().getString(resId, objects);
    }

    private int defaultMaxSelectable() {
        return StringHelper.getInteger(R.integer.integer_max_image_pick_size);
    }

    private void startGallery() {
        Album.album(getActivity()).requestCode(BaseFragment.REQUEST_GALLERY)
                .toolBarColor(getColor(R.color.colorPrimary))
                .statusBarColor(getColor(R.color.colorPrimary))
                .title(getString(R.string.ui_base_text_choose_image, defaultMaxSelectable()))
                //.checkedList(isSupportCompress ? waitingFroCompressImages : getWaitingForUploadFiles())
                .selectCount(defaultMaxSelectable()).columnCount(3).camera(true).start();
    }

    private void startCamera() {
        Album.camera(getActivity()).requestCode(BaseFragment.REQUEST_CAMERA).start();
    }
}
