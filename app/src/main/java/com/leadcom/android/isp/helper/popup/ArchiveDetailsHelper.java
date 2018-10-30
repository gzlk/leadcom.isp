package com.leadcom.android.isp.helper.popup;

import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.view.View;
import android.widget.TextView;

import com.hlk.hlklib.lib.view.ClearEditText;
import com.hlk.hlklib.lib.view.CustomTextView;
import com.leadcom.android.isp.R;
import com.leadcom.android.isp.api.listener.OnSingleRequestListener;
import com.leadcom.android.isp.api.org.OrgRequest;
import com.leadcom.android.isp.cache.Cache;
import com.leadcom.android.isp.etc.Utils;
import com.leadcom.android.isp.fragment.base.BaseFragment;
import com.leadcom.android.isp.helper.StringHelper;
import com.leadcom.android.isp.model.Model;
import com.leadcom.android.isp.model.archive.Archive;
import com.leadcom.android.isp.model.archive.Label;
import com.leadcom.android.isp.model.common.Seclusion;
import com.leadcom.android.isp.model.organization.Organization;

/**
 * <b>功能描述：</b>档案详细属性设定对话框helper<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2018/10/30 15:49 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2018/10/30 15:49  <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */
public class ArchiveDetailsHelper {

    public static ArchiveDetailsHelper helper() {
        return new ArchiveDetailsHelper();
    }

    private int layout = R.layout.popup_dialog_rich_editor_archive_setting;
    private AppCompatActivity activity;
    private Archive mArchive;

    public ArchiveDetailsHelper init(BaseFragment fragment) {
        activity = fragment.Activity();
        return this;
    }

    public ArchiveDetailsHelper init(AppCompatActivity activity) {
        this.activity = activity;
        return this;
    }

    public ArchiveDetailsHelper setArchive(Archive archive) {
        mArchive = archive;
        return this;
    }

    private boolean isEmpty(String string) {
        return StringHelper.isEmpty(string);
    }

    private String getString(int res) {
        return StringHelper.getString(res);
    }

    /**
     * 获取颜色
     */
    private int getColor(int res) {
        return ContextCompat.getColor(activity, res);
    }

    /**
     * 是否是组织档案，默认个人档案
     */
    private boolean isGroupArchive = false, isUserArchive = true;
    private View settingDialogView;
    private TextView titleText, publicText, labelText, createTime, happenDate, propertyText, categoryText, groupNameText,
            branchText;
    private CustomTextView userIcon, groupIcon, publicIcon, privateIcon;
    private ClearEditText participantText, siteText;
    private ClearEditText creatorText;
    private View archiveTypeUser, shareDraftButton;

    public void show() {
        isGroupArchive = !isEmpty(mArchive.getGroupId());
        isUserArchive = !isGroupArchive;
        DialogHelper.init(activity).addOnDialogInitializeListener(new DialogHelper.OnDialogInitializeListener() {
            @Override
            public View onInitializeView() {
                if (null == settingDialogView) {
                    settingDialogView = View.inflate(activity, layout, null);
                    titleText = settingDialogView.findViewById(R.id.ui_popup_rich_editor_setting_title);
                    publicText = settingDialogView.findViewById(R.id.ui_popup_rich_editor_setting_public_text);
                    labelText = settingDialogView.findViewById(R.id.ui_popup_rich_editor_setting_label_text);
                    creatorText = settingDialogView.findViewById(R.id.ui_popup_rich_editor_setting_creator);
                    createTime = settingDialogView.findViewById(R.id.ui_popup_rich_editor_setting_create_time);
                    propertyText = settingDialogView.findViewById(R.id.ui_popup_rich_editor_setting_property_text);
                    categoryText = settingDialogView.findViewById(R.id.ui_popup_rich_editor_setting_category_text);
                    participantText = settingDialogView.findViewById(R.id.ui_popup_rich_editor_setting_participant_text);
                    siteText = settingDialogView.findViewById(R.id.ui_popup_rich_editor_setting_site_text);
                    happenDate = settingDialogView.findViewById(R.id.ui_popup_rich_editor_setting_time_text);
                    userIcon = settingDialogView.findViewById(R.id.ui_popup_rich_editor_setting_type_user_icon);
                    groupIcon = settingDialogView.findViewById(R.id.ui_popup_rich_editor_setting_type_group_icon);
                    groupNameText = settingDialogView.findViewById(R.id.ui_popup_rich_editor_setting_group_picker_text);
                    archiveTypeUser = settingDialogView.findViewById(R.id.ui_popup_rich_editor_setting_type_user);
                    publicIcon = settingDialogView.findViewById(R.id.ui_popup_rich_editor_setting_public_public_icon);
                    privateIcon = settingDialogView.findViewById(R.id.ui_popup_rich_editor_setting_public_private_icon);
                    shareDraftButton = settingDialogView.findViewById(R.id.ui_popup_rich_editor_setting_share_draft);
                    branchText = settingDialogView.findViewById(R.id.ui_popup_rich_editor_setting_branch_picker_text);
                }
                return settingDialogView;
            }

            @Override
            public void onBindData(View dialogView, DialogHelper helper) {
                if (isEmpty(mArchive.getTitle())) {
                    titleText.setText(Html.fromHtml(getString(R.string.ui_text_archive_creator_editor_title_empty)));
                } else {
                    titleText.setText(mArchive.getTitle());
                }
                String source = mArchive.getSource();
                creatorText.setValue(isEmpty(source) ? mArchive.getUserName() : source);
                creatorText.focusEnd();
                String text = mArchive.getCreateDate();
                if (!isEmpty(text) && !text.equals(Model.DFT_DATE)) {
                    text = Utils.format(mArchive.getCreateDate(), getString(R.string.ui_base_text_date_time_format), "yyyy.MM.dd");
                } else {
                    text = Utils.formatDateOfNow("yyyy.MM.dd");
                }
                createTime.setText(text);
                labelText.setText(Label.getLabelDesc(mArchive.getLabel()));
                Seclusion seclusion = new Seclusion();
                //seclusion.setGroupIds(mArchive.getAuthGro());
                //seclusion.setUserIds(mArchive.getAuthUser());
                //seclusion.setUserNames(mArchive.getAuthUserName());
                // 这一步一定要在最后设置，否则状态会被重置
                seclusion.setStatus(mArchive.getAuthPublic());
                publicText.setText(Seclusion.getPrivacy(seclusion));

                siteText.setValue(mArchive.getSite());
                siteText.focusEnd();

                resetProperty();
                resetCategory();

                if (isEmpty(mArchive.getParticipant())) {
                    participantText.setValue("");
                } else {
                    participantText.setValue(mArchive.getParticipant());
                }
                participantText.focusEnd();
                if (mArchive.isDefaultHappenDate()) {
                    happenDate.setText(R.string.ui_text_archive_details_editor_setting_time_title);
                } else {
                    happenDate.setText(mArchive.getHappenDate().substring(0, 10));
                }

                if (mArchive.isAttachmentArchive()) {
                    isGroupArchive = true;
                    isUserArchive = false;
                }

                if (isEmpty(mArchive.getGroupId())) {
                    // 如果用户只有一个组织则直接填入组织id和名字
                    if (isGroupArchive && Cache.cache().getGroups().size() == 1) {
                        mArchive.setGroupId(Cache.cache().getGroups().get(0).getGroupId());
                        resetGroupInfo(mArchive.getGroupId());
                    } else {
                        groupNameText.setText(R.string.ui_text_archive_details_editor_setting_group_desc);
                    }
                } else {
                    resetGroupInfo(mArchive.getGroupId());
                }

                archiveTypeUser.setVisibility(mArchive.isAttachmentArchive() ? View.GONE : View.VISIBLE);
                if (mArchive.isAttachmentArchive()) {
                    shareDraftButton.setVisibility(View.GONE);
                }
                resetGroupArchiveOrUser();
            }

            private void resetGroupInfo(String groupId) {
                Organization group = Organization.get(groupId);
                if (null != group) {
                    groupNameText.setText(Html.fromHtml(group.getName()));
                    mArchive.setGroupName(group.getName());
                } else {
                    fetchingGroup(mArchive.getGroupId());
                }
            }

            private void fetchingGroup(String groupId) {
                OrgRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<Organization>() {
                    @Override
                    public void onResponse(Organization organization, boolean success, String message) {
                        super.onResponse(organization, success, message);
                        if (success && null != organization) {
                            groupNameText.setText(Html.fromHtml(organization.getName()));
                            mArchive.setGroupName(organization.getName());
                        }
                    }
                }).find(groupId);
            }

            private void resetProperty() {
                if (isEmpty(mArchive.getProperty())) {
                    propertyText.setText(R.string.ui_text_archive_details_editor_setting_property_title);
                } else {
                    propertyText.setText(mArchive.getProperty());
                }
            }

            private void resetCategory() {
                if (isEmpty(mArchive.getCategory())) {
                    categoryText.setText(R.string.ui_text_archive_details_editor_setting_category_title);
                } else {
                    categoryText.setText(mArchive.getCategory());
                }
            }

            /**
             * 重置当前选择是组织档案还是个人档案
             */
            private void resetGroupArchiveOrUser() {
                userIcon.setTextColor(getColor(isUserArchive ? R.color.colorPrimary : R.color.textColorHintLight));
                groupIcon.setTextColor(getColor(isGroupArchive ? R.color.colorPrimary : R.color.textColorHintLight));
                int groupVisibility = isGroupArchive ? View.VISIBLE : View.GONE;
                // 组织档案需要发生时间
                if (!mArchive.isTemplateArchive()) {
                    settingDialogView.findViewById(R.id.ui_popup_rich_editor_setting_time).setVisibility(groupVisibility);
                }
                // 组织档案需要选择组织
                settingDialogView.findViewById(R.id.ui_popup_rich_editor_setting_group_picker).setVisibility(groupVisibility);
                // 组织档案需要设置档案的性质
                settingDialogView.findViewById(R.id.ui_popup_rich_editor_setting_property).setVisibility(groupVisibility);
                // 组织档案需要设置档案的类型
                settingDialogView.findViewById(R.id.ui_popup_rich_editor_setting_category).setVisibility(groupVisibility);
                // 个人档案需要选择标签
                settingDialogView.findViewById(R.id.ui_popup_rich_editor_setting_label).setVisibility(isUserArchive ? View.VISIBLE : View.GONE);
                // 模板档案不需要组织、个人选择
                settingDialogView.findViewById(R.id.ui_popup_rich_editor_setting_type).setVisibility(mArchive.isTemplateArchive() || mArchive.isAttachmentArchive() ? View.GONE : View.VISIBLE);
                // 模板档案不需要有封面
                //settingDialogView.findViewById(R.id.ui_popup_rich_editor_setting_cover).setVisibility(mArchive.isTemplateArchive() ? View.GONE : View.VISIBLE);
                // 模板档案不需要来源
                settingDialogView.findViewById(R.id.ui_popup_rich_editor_setting_source).setVisibility(mArchive.isTemplateArchive() ? View.GONE : View.VISIBLE);
                // 模板档案需要显示支部选择器
                settingDialogView.findViewById(R.id.ui_popup_rich_editor_setting_branch_picker).setVisibility(isGroupArchive ? View.VISIBLE : View.GONE);
                // 个人档案不需要分享草稿
                if (!mArchive.isAttachmentArchive()) {
                    settingDialogView.findViewById(R.id.ui_popup_rich_editor_setting_share_draft).setVisibility(isGroupArchive ? View.VISIBLE : View.GONE);
                }
            }

        }).addOnEventHandlerListener(new DialogHelper.OnEventHandlerListener() {
            @Override
            public int[] clickEventHandleIds() {
                return new int[]{
                        R.id.ui_popup_rich_editor_setting_type_user,
                        R.id.ui_popup_rich_editor_setting_type_group,
                        R.id.ui_popup_rich_editor_setting_group_picker,
                        R.id.ui_popup_rich_editor_setting_branch_picker,
                        R.id.ui_popup_rich_editor_setting_time,
                        R.id.ui_popup_rich_editor_setting_property,
                        R.id.ui_popup_rich_editor_setting_category,
                        R.id.ui_popup_rich_editor_setting_participant,
                        //R.id.ui_popup_rich_editor_setting_public,
                        R.id.ui_popup_rich_editor_setting_public_public,
                        R.id.ui_popup_rich_editor_setting_public_private,
                        R.id.ui_popup_rich_editor_setting_label,
                        R.id.ui_popup_rich_editor_setting_share,
                        R.id.ui_popup_rich_editor_setting_share_draft,
                        R.id.ui_popup_rich_editor_setting_commit};
            }

            @Override
            public boolean onClick(View view) {
                if (null != clickListener) {
                    clickListener.onClick(view);
                }
                return false;
            }
        }).setAdjustScreenWidth(true).setPopupType(DialogHelper.SLID_IN_RIGHT).show();
    }

    public void resetPublicStatus(boolean isPublic) {
        publicIcon.setTextColor(getColor(isPublic ? R.color.colorPrimary : R.color.textColorHintLight));
        privateIcon.setTextColor(getColor(!isPublic ? R.color.colorPrimary : R.color.textColorHintLight));
    }

    public void showHappenDate(String time) {
        if (null != happenDate) {
            happenDate.setText(time);
        }
    }

    private OnElementClickListener clickListener;

    public interface OnElementClickListener {
        void onClick(View view);
    }

    public ArchiveDetailsHelper setOnElementClickListener(OnElementClickListener l) {
        clickListener = l;
        return this;
    }
}
