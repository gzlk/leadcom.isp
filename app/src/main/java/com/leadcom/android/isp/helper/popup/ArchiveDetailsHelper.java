package com.leadcom.android.isp.helper.popup;

import android.content.Intent;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.Html;
import android.text.TextWatcher;
import android.view.View;
import android.widget.TextView;

import com.google.gson.reflect.TypeToken;
import com.hlk.hlklib.lib.view.ClearEditText;
import com.hlk.hlklib.lib.view.CustomTextView;
import com.leadcom.android.isp.R;
import com.leadcom.android.isp.api.archive.ArchiveRequest;
import com.leadcom.android.isp.api.listener.OnSingleRequestListener;
import com.leadcom.android.isp.api.org.OrgRequest;
import com.leadcom.android.isp.api.org.SquadRequest;
import com.leadcom.android.isp.etc.Utils;
import com.leadcom.android.isp.fragment.base.BaseFragment;
import com.leadcom.android.isp.helper.StringHelper;
import com.leadcom.android.isp.helper.ToastHelper;
import com.leadcom.android.isp.lib.Json;
import com.leadcom.android.isp.model.Model;
import com.leadcom.android.isp.model.archive.Archive;
import com.leadcom.android.isp.model.archive.Label;
import com.leadcom.android.isp.model.common.Seclusion;
import com.leadcom.android.isp.model.organization.Organization;
import com.leadcom.android.isp.model.organization.RelateGroup;
import com.leadcom.android.isp.model.organization.Squad;
import com.leadcom.android.isp.model.organization.SubMember;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    private boolean showTypes = false;

    /**
     * 是否显示组织、个人档案选择项，默认不显示
     */
    public ArchiveDetailsHelper setShowTypes(boolean shown) {
        showTypes = shown;
        return this;
    }

    private boolean groupSquadSelectable = false;

    /**
     * 设置组织和支部是否可以选择
     */
    public ArchiveDetailsHelper setGroupSquadSelectable(boolean selectable) {
        groupSquadSelectable = selectable;
        return this;
    }

    private boolean showCommit = true;

    public ArchiveDetailsHelper setShowCommit(boolean shown) {
        showCommit = shown;
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
    private View archiveTypeUser, shareDraftButton, commitButton;

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
                    commitButton = settingDialogView.findViewById(R.id.ui_popup_rich_editor_setting_commit);

                    creatorText.addTextChangedListener(textWatcher);
                    participantText.addTextChangedListener(participate);
                }
                return settingDialogView;
            }

            private boolean creatorEditable = true;
            private TextWatcher textWatcher = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (creatorEditable) {
                        if (null != s && s.length() > 0) {
                            String source = s.toString();
                            mArchive.setSource(source);
                            updatingArchive(ArchiveRequest.TYPE_SOURCE);
                        }
                    }
                }
            };

            private boolean participateEditable = true;
            private TextWatcher participate = new TextWatcher() {
                @Override
                public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                }

                @Override
                public void onTextChanged(CharSequence s, int start, int before, int count) {

                }

                @Override
                public void afterTextChanged(Editable s) {
                    if (participateEditable) {
                        if (null != s && s.length() > 0) {
                            String member = s.toString();
                            mArchive.setParticipant(member);
                            updatingArchive(ArchiveRequest.TYPE_PARTICIPANT);
                        }
                    }
                }
            };

            @Override
            public void onBindData(View dialogView, DialogHelper helper) {
                if (isEmpty(mArchive.getTitle())) {
                    titleText.setText(Html.fromHtml(getString(R.string.ui_text_archive_creator_editor_title_empty)));
                } else {
                    titleText.setText(mArchive.getTitle());
                }
                commitButton.setVisibility(showCommit ? View.VISIBLE : View.GONE);
                shareDraftButton.setVisibility(View.GONE);

                creatorEditable = false;
                String source = mArchive.getSource();
                creatorText.setValue(isEmpty(source) ? mArchive.getUserName() : source);
                creatorText.focusEnd();
                creatorEditable = true;

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

                resetProperty(false);
                resetCategory(false);

                participateEditable = false;
                if (isEmpty(mArchive.getParticipant())) {
                    participantText.setValue("");
                } else {
                    participantText.setValue(mArchive.getParticipant());
                }
                participantText.focusEnd();
                participateEditable = true;

                if (mArchive.isDefaultHappenDate()) {
                    happenDate.setText(R.string.ui_text_archive_details_editor_setting_time_title);
                } else {
                    happenDate.setText(mArchive.getHappenDate().substring(0, 10));
                }

                //if (mArchive.isAttachmentArchive()) {
                //    isGroupArchive = true;
                //    isUserArchive = false;
                //}

                if (isEmpty(mArchive.getGroupId())) {
                    // 如果用户只有一个组织则直接填入组织id和名字
                    //if (isGroupArchive && Cache.cache().getGroups().size() == 1) {
                    //    mArchive.setGroupId(Cache.cache().getGroups().get(0).getGroupId());
                    //    resetGroupInfo(mArchive.getGroupId());
                    //} else {
                    groupNameText.setText(R.string.ui_text_archive_details_editor_setting_group_desc);
                    //}
                } else {
                    resetGroupInfo(mArchive.getGroupId());
                }

                if (isEmpty(mArchive.getSquadId())) {
                    branchText.setText(R.string.ui_text_archive_details_editor_setting_branch_desc);
                } else {
                    fetchingSquad(mArchive.getSquadId());
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

            private void fetchingSquad(final String squadId) {
                SquadRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<Squad>() {
                    @Override
                    public void onResponse(Squad squad, boolean success, String message) {
                        super.onResponse(squad, success, message);
                        if (success) {
                            if (null != squad && !isEmpty(squad.getId())) {
                                branchText.setText(squad.getName());
                            } else {
                                branchText.setText(R.string.ui_text_archive_details_editor_setting_branch_error);
                            }
                        } else {
                            branchText.setText(R.string.ui_text_archive_details_editor_setting_branch_not_exists);
                        }
                    }
                }).find(squadId);
            }

            /**
             * 重置当前选择是组织档案还是个人档案
             */
            private void resetGroupArchiveOrUser() {
                userIcon.setTextColor(getColor(isUserArchive ? R.color.colorPrimary : R.color.textColorHintLight));
                groupIcon.setTextColor(getColor(isGroupArchive ? R.color.colorPrimary : R.color.textColorHintLight));
                int groupVisibility = isGroupArchive ? View.VISIBLE : View.GONE;
                // 组织档案需要发生时间
                //if (!mArchive.isTemplateArchive()) {
                settingDialogView.findViewById(R.id.ui_popup_rich_editor_setting_time).setVisibility(View.VISIBLE);
                //}
                // 组织档案需要选择组织
                settingDialogView.findViewById(R.id.ui_popup_rich_editor_setting_group_picker).setVisibility(groupSquadSelectable && isGroupArchive ? View.VISIBLE : View.GONE);
                // 支部
                settingDialogView.findViewById(R.id.ui_popup_rich_editor_setting_branch_picker).setVisibility(groupSquadSelectable && isGroupArchive ? View.VISIBLE : View.GONE);
                // 组织档案需要设置档案的性质
                settingDialogView.findViewById(R.id.ui_popup_rich_editor_setting_property).setVisibility(groupVisibility);
                // 组织档案需要设置档案的类型
                settingDialogView.findViewById(R.id.ui_popup_rich_editor_setting_category).setVisibility(groupVisibility);
                // 个人档案需要选择标签
                settingDialogView.findViewById(R.id.ui_popup_rich_editor_setting_label).setVisibility(isUserArchive ? View.VISIBLE : View.GONE);
                // 参与人
                settingDialogView.findViewById(R.id.ui_popup_rich_editor_setting_participant).setVisibility(groupVisibility);
                // 模板档案不需要组织、个人选择
                settingDialogView.findViewById(R.id.ui_popup_rich_editor_setting_type).setVisibility(!showTypes || mArchive.isTemplateArchive() || mArchive.isAttachmentArchive() ? View.GONE : View.VISIBLE);
                // 模板档案不需要有封面
                //settingDialogView.findViewById(R.id.ui_popup_rich_editor_setting_cover).setVisibility(mArchive.isTemplateArchive() ? View.GONE : View.VISIBLE);
                // 模板档案不需要来源
                settingDialogView.findViewById(R.id.ui_popup_rich_editor_setting_source).setVisibility(mArchive.isTemplateArchive() ? View.GONE : View.VISIBLE);
                // 个人档案不需要分享草稿
                if (!mArchive.isAttachmentArchive()) {
                    //settingDialogView.findViewById(R.id.ui_popup_rich_editor_setting_share_draft).setVisibility(isGroupArchive ? View.VISIBLE : View.GONE);
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

    public void handleActivityResult(int requestCode, Intent data) {
        switch (requestCode) {
            case BaseFragment.REQUEST_CATEGORY:
                // 档案类型
                mArchive.setCategory(BaseFragment.getResultedData(data));
                resetCategory(true);
                break;
            case BaseFragment.REQUEST_PROPERTY:
                // 档案性质
                String res = BaseFragment.getResultedData(data);
                String[] properties = res.split(",");
                mArchive.setDocClassifyId(properties[0]);
                mArchive.setProperty(properties[1]);
                resetProperty(true);
                break;
            case BaseFragment.REQUEST_GROUP:
                // 档案所属组织
                ArrayList<RelateGroup> groups = RelateGroup.from(BaseFragment.getResultedData(data));
                if (null != groups && groups.size() > 0) {
                    // 设置组织id
                    mArchive.setGroupId(groups.get(0).getGroupId());
                    if (null != groupNameText) {
                        groupNameText.setText(Html.fromHtml(groups.get(0).getGroupName()));
                    }
                    updatingArchive(ArchiveRequest.TYPE_GROUP);
                }
                break;
            case BaseFragment.REQUEST_CREATE:
            case BaseFragment.REQUEST_SELECT:
                // 档案参与人选择完毕
                ArrayList<SubMember> members = SubMember.fromJson(BaseFragment.getResultedData(data));
                String names = "";
                String old = participantText.getValue();
                List<String> oNames = null;
                if (!isEmpty(old)) {
                    oNames = Arrays.asList(old.split("、"));
                }
                if (null != oNames) {
                    for (String name : oNames) {
                        names += (isEmpty(names) ? "" : "、") + name;
                    }
                }
                if (mArchive.isActivity()) {
                    oNames = null;
                    names = "";
                }
                if (null != members && members.size() > 0) {
                    for (SubMember member : members) {
                        if (null == oNames || !oNames.contains(member.getUserName())) {
                            if (!names.contains(member.getUserName())) {
                                names += (isEmpty(names) ? "" : "、") + member.getUserName();
                            }
                        }
                        if (mArchive.isActivity()) {
                            if (member.isGroup()) {
                                if (!mArchive.getGroupIdList().contains(member.getUserId())) {
                                    mArchive.getGroupIdList().add(member.getUserId());
                                }
                            } else if (member.isMember()) {
                                if (!mArchive.getGroSquMemberList().contains(member)) {
                                    mArchive.getGroSquMemberList().add(member);
                                }
                            }
                        } else {
                            if (!mArchive.getParticipantIdList().contains(member.getUserId())) {
                                mArchive.getParticipantIdList().add(member.getUserId());
                            }
                        }
                    }
                }
                mArchive.setParticipant(names);
                mArchive.setParticipator(names);
                participantText.setValue(names);
                participantText.focusEnd();
                //updatingArchive(ArchiveRequest.TYPE_PARTICIPANT);
                //if (null != participantHolder) {
                //    participantHolder.showContent(format(templateItems[2], names));
                //}
                break;
            case BaseFragment.REQUEST_SQUAD:
                // 选择了小组
                String string = BaseFragment.getResultedData(data);
                if (!isEmpty(string)) {
                    Squad squad = Squad.fromJson(string);
                    if (null != squad && !isEmpty(squad.getId())) {
                        mArchive.setBranch(squad.getName());
                        mArchive.setSquadId(squad.getId());
                        branchText.setText(squad.getName());
                        updatingArchive(ArchiveRequest.TYPE_SQUAD);
                    }
                }
                break;
            case BaseFragment.REQUEST_LABEL:
                String labelJson = BaseFragment.getResultedData(data);
                ArrayList<String> list = Json.gson().fromJson(labelJson, new TypeToken<ArrayList<String>>() {
                }.getType());
                if (null != list) {
                    mArchive.getLabel().clear();
                    mArchive.getLabel().addAll(list);
                    updatingArchive(ArchiveRequest.TYPE_LABEL);
                }
                if (null != labelText) {
                    labelText.setText(Label.getLabelDesc(mArchive.getLabel()));
                }
                break;
        }
    }

    private void resetProperty(boolean updatable) {
        if (isEmpty(mArchive.getProperty())) {
            propertyText.setText(R.string.ui_text_archive_details_editor_setting_property_title);
        } else {
            propertyText.setText(mArchive.getProperty());
        }
        if (updatable) {
            updatingArchive(ArchiveRequest.TYPE_PROPERTY);
        }
    }

    private void resetCategory(boolean updatable) {
        if (isEmpty(mArchive.getCategory())) {
            categoryText.setText(R.string.ui_text_archive_details_editor_setting_category_title);
        } else {
            categoryText.setText(mArchive.getCategory());
        }
        if (updatable) {
            updatingArchive(ArchiveRequest.TYPE_CATEGROY);
        }
    }

    public void resetPublicStatus() {
        boolean isPublic = mArchive.getAuthPublic() == Seclusion.Type.Public;
        publicIcon.setTextColor(getColor(isPublic ? R.color.colorPrimary : R.color.textColorHintLight));
        privateIcon.setTextColor(getColor(!isPublic ? R.color.colorPrimary : R.color.textColorHintLight));
        updatingArchive(ArchiveRequest.TYPE_AUTH);
    }

    public void showHappenDate(String time) {
        if (null != happenDate) {
            happenDate.setText(time);
        }
        updatingArchive(ArchiveRequest.TYPE_HAPPEN);
    }

    private void updatingArchive(int type) {
        ArchiveRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<Archive>() {
            @Override
            public void onResponse(Archive archive, boolean success, String message) {
                super.onResponse(archive, success, message);
                if (success) {
                    ToastHelper.make().showMsg(R.string.ui_text_archive_details_update_success);
                }
            }
        }).update(mArchive, type);
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
