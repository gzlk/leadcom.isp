package com.gzlk.android.isp.fragment.organization;

import android.view.View;

import com.gzlk.android.isp.R;
import com.gzlk.android.isp.api.listener.OnSingleRequestListener;
import com.gzlk.android.isp.api.org.OrgRequest;
import com.gzlk.android.isp.etc.Utils;
import com.gzlk.android.isp.fragment.base.BaseSwipeRefreshSupportFragment;
import com.gzlk.android.isp.helper.StringHelper;
import com.gzlk.android.isp.helper.ToastHelper;
import com.gzlk.android.isp.holder.common.SimpleClickableViewHolder;
import com.gzlk.android.isp.holder.common.SimpleInputableViewHolder;
import com.gzlk.android.isp.listener.OnTitleButtonClickListener;
import com.gzlk.android.isp.listener.OnViewHolderClickListener;
import com.gzlk.android.isp.model.common.Attachment;
import com.gzlk.android.isp.model.organization.Organization;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.view.ClearEditText;

import java.util.ArrayList;

/**
 * <b>功能描述：</b>新建组织<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/05/28 22:34 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/05/28 22:34 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class CreateOrganizationFragment extends BaseSwipeRefreshSupportFragment {

    // view
    @ViewId(R.id.ui_group_creator_cover)
    private View logoView;
    @ViewId(R.id.ui_group_creator_name)
    private View nameView;
    @ViewId(R.id.ui_group_creator_description)
    private ClearEditText descView;

    // holder
    private SimpleClickableViewHolder logoHolder;
    private SimpleInputableViewHolder nameHolder;

    private String[] items;

    @Override
    protected void onDelayRefreshComplete(@DelayType int type) {

    }

    @Override
    public int getLayout() {
        return R.layout.fragment_organization_creator;
    }

    @Override
    public void doingInResume() {
        // 封面图只有一张
        maxSelectable = 1;
        setCustomTitle(R.string.ui_organization_creator_fragment_title);
        setRightText(R.string.ui_base_text_save);
        setRightTitleClickListener(new OnTitleButtonClickListener() {
            @Override
            public void onClick() {
                tryCreateOrganization();
            }
        });
        initializeHolders();
        addOnImageSelectedListener(onImageSelectedListener);
        setOnFileUploadingListener(mOnFileUploadingListener);
    }

    private OnImageSelectedListener onImageSelectedListener = new OnImageSelectedListener() {
        @Override
        public void onImageSelected(ArrayList<String> selected) {
            // 只能选择一张图片，因此可以直接显示图片
            logoHolder.showImage(selected.get(0));
        }
    };
    private OnFileUploadingListener mOnFileUploadingListener = new OnFileUploadingListener() {

        @Override
        public void onUploading(int all, int current, String file, long size, long uploaded) {

        }

        @Override
        public void onUploadingComplete(ArrayList<Attachment> uploaded) {
            createOrganization();
        }
    };

    private void tryCreateOrganization() {
        String name = nameHolder.getValue();
        if (isEmpty(name)) {
            ToastHelper.make().showMsg(R.string.ui_organization_creator_name_invalid);
            return;
        }
        String desc = descView.getValue();
        if (isEmpty(desc)) {
            ToastHelper.make().showMsg(R.string.ui_organization_creator_desc_invalid);
            return;
        }
        Utils.hidingInputBoard(nameView);
        if (getWaitingForUploadFiles().size() > 0) {
            uploadFiles();
        } else {
            createOrganization();
        }
    }

    private void createOrganization() {
        String desc = descView.getValue();
        String name = nameHolder.getValue();
        String logo = null;
        if (getUploadedFiles().size() > 0) {
            logo = getUploadedFiles().get(0).getUrl();
        }
        OrgRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<Organization>() {
            @Override
            public void onResponse(Organization organization, boolean success, String message) {
                super.onResponse(organization, success, message);
                if (success) {
                    Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            finish();
                        }
                    });
                }
            }
        }).add(name, logo, desc);
    }

    @Override
    protected boolean checkStillEditing() {
        return !isEmpty(nameHolder.getValue()) || !isEmpty(descView.getValue());
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

    }

    @Override
    protected void onLoadingMore() {

    }

    @Override
    protected String getLocalPageTag() {
        return null;
    }

    private void initializeHolders() {
        if (null == items) {
            items = StringHelper.getStringArray(R.array.ui_organization_creator_items);
        }
        if (null == logoHolder) {
            logoHolder = new SimpleClickableViewHolder(logoView, this);
            logoHolder.showContent(format(items[0], "(点击选择图片)"));
            logoHolder.addOnViewHolderClickListener(onViewHolderClickListener);
        }
        if (null == nameHolder) {
            nameHolder = new SimpleInputableViewHolder(nameView, this);
            nameHolder.showContent(format(items[1], ""));
        }
    }

    private OnViewHolderClickListener onViewHolderClickListener = new OnViewHolderClickListener() {
        @Override
        public void onClick(int index) {
            openImageSelector();
        }
    };
}
