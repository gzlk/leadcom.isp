package com.leadcom.android.isp.fragment.organization;

import android.os.Bundle;
import android.view.View;

import com.leadcom.android.isp.R;
import com.leadcom.android.isp.api.listener.OnSingleRequestListener;
import com.leadcom.android.isp.api.org.OrgRequest;
import com.leadcom.android.isp.etc.Utils;
import com.leadcom.android.isp.fragment.base.BaseFragment;
import com.leadcom.android.isp.fragment.base.BaseSwipeRefreshSupportFragment;
import com.leadcom.android.isp.helper.StringHelper;
import com.leadcom.android.isp.helper.ToastHelper;
import com.leadcom.android.isp.holder.common.SimpleClickableViewHolder;
import com.leadcom.android.isp.holder.common.SimpleInputableViewHolder;
import com.leadcom.android.isp.listener.OnTitleButtonClickListener;
import com.leadcom.android.isp.listener.OnViewHolderClickListener;
import com.leadcom.android.isp.model.common.Attachment;
import com.leadcom.android.isp.model.organization.Organization;
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

public class GroupCreateFragment extends BaseSwipeRefreshSupportFragment {

    private static final String PARAM_NAME = "cof_name";
    private static final String PARAM_LOGO = "cof_logo";
    private static final String PARAM_INTRO = "cof_intro";

    public static GroupCreateFragment newInstance(String params) {
        GroupCreateFragment cof = new GroupCreateFragment();
        String[] strings = splitParameters(params, 4);
        Bundle bundle = new Bundle();
        // 组织id
        bundle.putString(PARAM_QUERY_ID, strings[0]);
        bundle.putString(PARAM_NAME, strings[1]);
        bundle.putString(PARAM_LOGO, strings[2]);
        bundle.putString(PARAM_INTRO, strings[3]);
        cof.setArguments(bundle);
        return cof;
    }

    public static void open(BaseFragment fragment) {
        open(fragment, "", "", "", "");
    }

    public static void open(BaseFragment fragment, Organization group) {
        open(fragment, group.getId(), group.getName(), group.getLogo(), group.getIntro());
    }

    private static void open(BaseFragment fragment, String groupId, String name, String logo, String intro) {
        fragment.openActivity(GroupCreateFragment.class.getName(), format("%s,%s,%s,%s", groupId, name, logo, intro), isEmpty(groupId) ? REQUEST_CREATE : REQUEST_CHANGE, true, true);
    }

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
    private String name, logo, intro;

    @Override
    protected void getParamsFromBundle(Bundle bundle) {
        super.getParamsFromBundle(bundle);
        name = bundle.getString(PARAM_NAME, "");
        logo = bundle.getString(PARAM_LOGO, "");
        intro = bundle.getString(PARAM_INTRO, "");
        if (isEmpty(intro, true)) {
            intro = "";
        }
    }

    @Override
    protected void saveParamsToBundle(Bundle bundle) {
        super.saveParamsToBundle(bundle);
        bundle.putString(PARAM_NAME, name);
        bundle.putString(PARAM_LOGO, logo);
        bundle.putString(PARAM_INTRO, intro);
    }

    @Override
    protected void onDelayRefreshComplete(@DelayType int type) {

    }

    @Override
    public int getLayout() {
        return R.layout.fragment_group_creator;
    }

    @Override
    public void doingInResume() {
        // 封面图只有一张
        maxSelectable = 1;
        setCustomTitle(isEmpty(mQueryId) ? R.string.ui_organization_creator_fragment_title : R.string.ui_organization_creator_fragment_title_edit);
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
            // 上传完毕之后继续未完成的组织创建动作
            createOrganization();
        }

        @Override
        public void onUploadingFailed(int code, String message) {

        }
    };

    private void tryCreateOrganization() {
        String name = nameHolder.getValue();
        if (isEmpty(name)) {
            ToastHelper.make().showMsg(R.string.ui_organization_creator_name_invalid);
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
        intro = descView.getValue();
        String name = nameHolder.getValue();
        String logo = "";
        if (getUploadedFiles().size() > 0) {
            logo = getUploadedFiles().get(0).getUrl();
        }
        if (!isEmpty(mQueryId)) {
            updateOrganization((name.equals(this.name) ? "" : name), (logo.equals(this.logo) ? "" : logo), intro);
            return;
        }
        OrgRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<Organization>() {
            @Override
            public void onResponse(Organization organization, boolean success, String message) {
                super.onResponse(organization, success, message);
                if (success) {
                    final String id = null == organization ? null : organization.getId();
                    Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            resultData(id);
                        }
                    });
                }
            }
        }).add(name, logo, intro);
    }

    private void updateOrganization(String name, String logo, String intro) {
        OrgRequest request = OrgRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<Organization>() {
            @Override
            public void onResponse(Organization organization, boolean success, String message) {
                super.onResponse(organization, success, message);
                if (success) {
                    Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            resultData(mQueryId);
                        }
                    });
                }
            }
        });
        if (isEmpty(name) && isEmpty(logo)) {
            request.update(mQueryId, OrgRequest.TYPE_INTRO, intro);
        } else {
            request.update(mQueryId, name, logo, intro);
        }
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
            if (!isEmpty(logo)) {
                logoHolder.showImage(logo);
            }
        }
        if (null == nameHolder) {
            nameHolder = new SimpleInputableViewHolder(nameView, this);
            nameHolder.showContent(format(items[1], name));
            descView.setValue(StringHelper.escapeFromHtml(intro));
        }
    }

    private OnViewHolderClickListener onViewHolderClickListener = new OnViewHolderClickListener() {
        @Override
        public void onClick(int index) {
            openImageSelector();
        }
    };
}
