package com.gzlk.android.isp.fragment.activity;

import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextPaint;
import android.view.View;

import com.daimajia.swipe.util.Attributes;
import com.google.gson.reflect.TypeToken;
import com.gzlk.android.isp.R;
import com.gzlk.android.isp.adapter.RecyclerViewSwipeAdapter;
import com.gzlk.android.isp.api.listener.OnMultipleRequestListener;
import com.gzlk.android.isp.api.listener.OnSingleRequestListener;
import com.gzlk.android.isp.api.org.InvitationRequest;
import com.gzlk.android.isp.api.org.MemberRequest;
import com.gzlk.android.isp.cache.Cache;
import com.gzlk.android.isp.fragment.base.BaseSwipeRefreshSupportFragment;
import com.gzlk.android.isp.fragment.individual.UserPropertyFragment;
import com.gzlk.android.isp.fragment.main.ActivityFragment;
import com.gzlk.android.isp.fragment.organization.GroupsContactPickerFragment;
import com.gzlk.android.isp.helper.DialogHelper;
import com.gzlk.android.isp.helper.SimpleDialogHelper;
import com.gzlk.android.isp.helper.ToastHelper;
import com.gzlk.android.isp.holder.organization.ContactViewHolder;
import com.gzlk.android.isp.lib.Json;
import com.gzlk.android.isp.listener.OnTitleButtonClickListener;
import com.gzlk.android.isp.listener.OnViewHolderClickListener;
import com.gzlk.android.isp.model.organization.Invitation;
import com.gzlk.android.isp.model.organization.Member;
import com.gzlk.android.isp.model.organization.SubMember;
import com.netease.nim.uikit.cache.SimpleCallback;
import com.netease.nim.uikit.cache.TeamDataCache;
import com.netease.nimlib.sdk.team.constant.TeamMemberType;
import com.netease.nimlib.sdk.team.model.TeamMember;

import java.util.ArrayList;
import java.util.List;

/**
 * <b>功能描述：</b>活动成员列表<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/06/06 14:27 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/06/06 14:27 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class ActivityMemberFragment extends BaseSwipeRefreshSupportFragment {

    private static final String PARAM_MASTER = "amf_member_is_master";
    private static final String PARAM_GROUP_ID = "amf_group_id";
    private static final String PARAM_FOR_PICKER = "amf_for_picker";
    private static final String PARAM_DIAL_INDEX = "amf_dial_index";

    public static ActivityMemberFragment newInstance(String params) {
        ActivityMemberFragment amf = new ActivityMemberFragment();
        String[] strings = splitParameters(params);
        Bundle bundle = new Bundle();
        // 活动的id
        bundle.putString(PARAM_QUERY_ID, strings[0]);
        // 活动所属的组织id
        bundle.putString(PARAM_GROUP_ID, strings[1]);
        // 是否成员选取
        bundle.putBoolean(PARAM_FOR_PICKER, Boolean.valueOf(strings[2]));
        amf.setArguments(bundle);
        return amf;
    }

    @Override
    protected void getParamsFromBundle(Bundle bundle) {
        super.getParamsFromBundle(bundle);
        isMaster = bundle.getBoolean(PARAM_MASTER, false);
        groupId = bundle.getString(PARAM_GROUP_ID, "");
        forPicker = bundle.getBoolean(PARAM_FOR_PICKER, false);
        dialIndex = bundle.getInt(PARAM_DIAL_INDEX, -1);
    }

    @Override
    protected void saveParamsToBundle(Bundle bundle) {
        super.saveParamsToBundle(bundle);
        bundle.putBoolean(PARAM_MASTER, isMaster);
        bundle.putBoolean(PARAM_FOR_PICKER, forPicker);
        bundle.putString(PARAM_GROUP_ID, groupId);
        bundle.putInt(PARAM_DIAL_INDEX, dialIndex);
    }

    private MembersAdapter mAdapter;
    // 活动所属的组织
    private String groupId = "";
    // 当前登录者是否是活动的创建者
    private boolean isMaster = false;
    // 是否是活动成员拾取
    private boolean forPicker = false;
    private int dialIndex = -1;

    @Override
    protected void onSwipeRefreshing() {
        remotePageNumber = 1;
        fetchingMembers();
    }

    @Override
    protected void onLoadingMore() {
        fetchingMembers();
    }

    @Override
    protected String getLocalPageTag() {
        return null;
    }

    @Override
    public void doingInResume() {
        setCustomTitle(R.string.ui_activity_member_fragment_title);
        setNothingText(R.string.ui_activity_member_empty);
        checkPermission();
        initializeAdapter();
    }

    private void checkPermission() {
        TeamDataCache.getInstance().fetchTeamMember(mQueryId, Cache.cache().userId, new SimpleCallback<TeamMember>() {
            @Override
            public void onResult(boolean success, TeamMember result) {
                if (success && null != result) {
                    isMaster = result.getType() == TeamMemberType.Owner || result.getType() == TeamMemberType.Manager;
                }
                resetRightTitleIcon();
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, Intent data) {
        if (requestCode == REQ_MEMBER) {
            handleMemberSelected(getResultedData(data));
        }
        super.onActivityResult(requestCode, data);
    }

    private void handleMemberSelected(String json) {
        if (isEmpty(json) || json.equals(EMPTY_ARRAY)) {
            return;
        }
        List<SubMember> members = Json.gson().fromJson(json, new TypeToken<ArrayList<SubMember>>() {
        }.getType());
        if (null != members && members.size() > 0) {
            ArrayList<String> ids = new ArrayList<>();
            for (SubMember member : members) {
                if (!ids.contains(member.getUserId())) {
                    ids.add(member.getUserId());
                }
            }
            updateActivity(ids);
        }
    }

    private void updateActivity(ArrayList<String> ids) {
        setLoadingText(R.string.ui_activity_member_invite_loading);
        displayLoading(true);
        InvitationRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<Invitation>() {
            @Override
            public void onResponse(Invitation invitation, boolean success, String message) {
                super.onResponse(invitation, success, message);
                if (success) {
                    ToastHelper.make().showMsg(R.string.ui_activity_member_invite_success);
                }
                displayLoading(false);
            }
        }).activityInvite(mQueryId, ids);
    }

    private static final int REQ_MEMBER = ACTIVITY_BASE_REQUEST + 10;

    // 尝试从组织通讯录里选取其他成员进本活动
    private void pickNewMembers(String groupId) {
        //ArrayList<SubMember> members = new ArrayList<>();
        //for (int i = 0, size = mAdapter.getItemCount(); i < size; i++) {
        //    members.add(new SubMember(mAdapter.get(i)));
        //}
        //String json = Json.gson().toJson(members, new TypeToken<ArrayList<SubMember>>() {
        //}.getType());
        ////openActivity(GroupContactPickFragment.class.getName(), format("%s,true,false,%s", groupId, replaceJson(json, false)), REQ_MEMBER, true, false);
        //openActivity(GroupSquadContactPickerFragment.class.getName(), format("%s,true,%s", groupId, replaceJson(json, false)), REQ_MEMBER, true, false);

        GroupsContactPickerFragment.open(this, groupId, REQ_MEMBER);
    }

    @Override
    protected boolean shouldSetDefaultTitleEvents() {
        return true;
    }

    @Override
    protected void destroyView() {

    }

    private OnViewHolderClickListener onViewHolderClickListener = new OnViewHolderClickListener() {
        @Override
        public void onClick(int index) {
            if (forPicker) {
                // 单选
                pickSelected(index);
            } else {
                // 打开个人属性页
                openActivity(UserPropertyFragment.class.getName(), mAdapter.get(index).getUserId(), false, false, true);
            }
        }
    };

    private void pickSelected(int index) {
        Member m = mAdapter.get(index);
        if (!isEmpty(m.getUserId()) && m.getUserId().equals(Cache.cache().userId)) {
            ToastHelper.make().showMsg(R.string.ui_activity_member_pick_not_me);
            return;
        }
        for (int i = 0, size = mAdapter.getItemCount(); i < size; i++) {
            Member member = mAdapter.get(i);
            member.setSelected(i == index);
            mAdapter.notifyItemChanged(i);
        }
    }

    private void resetRightTitleIcon() {
        setRightIcon(forPicker ? 0 : R.string.ui_icon_add);
        setRightText(forPicker ? R.string.ui_base_text_confirm : 0);
        setRightTitleClickListener(new OnTitleButtonClickListener() {
            @Override
            public void onClick() {
                if (forPicker) {
                    resultPickedMember();
                } else {
                    pickNewMembers(groupId);
                }
            }
        });
    }

    private void resultPickedMember() {
        String account = "";
        for (int i = 0, size = mAdapter.getItemCount(); i < size; i++) {
            if (mAdapter.get(i).isSelected()) {
                Member member = mAdapter.get(i);
                account = member.getUserId();
                break;
            }
        }
        resultData(account);
    }

    private void fetchingMembers() {
        setLoadingText(R.string.ui_activity_member_loading);
        displayLoading(true);
        MemberRequest.request().setOnMultipleRequestListener(new OnMultipleRequestListener<Member>() {
            @Override
            public void onResponse(List<Member> list, boolean success, int totalPages, int pageSize, int total, int pageNumber) {
                super.onResponse(list, success, totalPages, pageSize, total, pageNumber);
                if (success) {
                    if (null != list) {
                        if (list.size() >= pageSize) {
                            remotePageNumber++;
                            isLoadingComplete(false);
                        } else {
                            isLoadingComplete(true);
                        }
                        for (Member member : list) {
                            if (forPicker) {
                                if (!isEmpty(member.getUserId()) && member.getUserId().equals(Cache.cache().userId)) {
                                    member.setLocalDeleted(true);
                                }
                            }
                        }
                        mAdapter.update(list, false);
                    } else {
                        isLoadingComplete(true);
                    }
                } else {
                    isLoadingComplete(true);
                }
                displayNothing(mAdapter.getItemCount() < 1);
                displayLoading(false);
                stopRefreshing();
            }
        }).list(Member.Type.ACTIVITY, mQueryId, remotePageNumber);
    }

    private void initializeAdapter() {
        if (null == mAdapter) {
            mAdapter = new MembersAdapter();
            mAdapter.setMode(Attributes.Mode.Single);
            mRecyclerView.addItemDecoration(new StickDecoration());
            mRecyclerView.setAdapter(mAdapter);
            // 加载活动的成员列表
            fetchingMembers();
        }
    }

    @Override
    protected void onDelayRefreshComplete(@DelayType int type) {

    }

    private ContactViewHolder.OnUserDeleteListener onUserDeleteListener = new ContactViewHolder.OnUserDeleteListener() {
        @Override
        public void onDelete(ContactViewHolder holder) {
            kickOut(holder);
            //mAdapter.delete(holder);
        }
    };

    private void kickOut(final ContactViewHolder holder) {
        Member member = mAdapter.get(holder.getAdapterPosition());
        final String memberId = member.getId();
        MemberRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<Member>() {
            @Override
            public void onResponse(Member member, boolean success, String message) {
                super.onResponse(member, success, message);
                if (success) {
                    mAdapter.delete(holder);
                    Member.remove(memberId);
                }
            }
        }).activityKickOut(member.getActId(), member.getUserId());
    }

    private ContactViewHolder.OnPhoneDialListener phoneDialListener = new ContactViewHolder.OnPhoneDialListener() {
        @Override
        public void onDial(int index) {
            dialIndex = index;
            requestPhoneCallPermission();
        }
    };

    @Override
    public void permissionGranted(String[] permissions, int requestCode) {
        super.permissionGranted(permissions, requestCode);
        if (requestCode == GRANT_PHONE_CALL) {
            warningDial();
        }
    }

    private void warningDial() {
        if (dialIndex < 0) return;
        final String text = mAdapter.get(dialIndex).getPhone();
        if (!isEmpty(text)) {
            String yes = getString(R.string.ui_base_text_dial);
            String no = getString(R.string.ui_base_text_cancel);
            SimpleDialogHelper.init(Activity()).show(text, yes, no, new DialogHelper.OnDialogConfirmListener() {
                @Override
                public boolean onConfirm() {
                    dialPhone(text);
                    return true;
                }
            }, null);
        }
    }

    private class MembersAdapter extends RecyclerViewSwipeAdapter<ContactViewHolder, Member> {

        @Override
        protected int comparator(Member item1, Member item2) {
            return 0;
        }

        private void delete(ContactViewHolder holder) {
            mItemManger.removeShownLayouts(holder.getSwipeLayout());
            int pos = holder.getAdapterPosition();
            remove(pos);
            mItemManger.closeAllItems();
        }

        @Override
        public ContactViewHolder onCreateViewHolder(View itemView, int viewType) {
            ContactViewHolder holder = new ContactViewHolder(itemView, ActivityMemberFragment.this);
            // 显示拾取器
            holder.showPicker(forPicker);
            holder.button2Text(R.string.ui_base_text_kick_out);
            // 打开个人名片
            holder.addOnViewHolderClickListener(onViewHolderClickListener);
            // 踢出成员
            holder.setOnUserDeleteListener(onUserDeleteListener);
            // 拨号
            holder.setOnPhoneDialListener(phoneDialListener);
            return holder;
        }

        @Override
        public int itemLayout(int viewType) {
            return R.layout.holder_view_organization_contact;
        }

        @Override
        public void onBindHolderOfView(ContactViewHolder holder, int position, @Nullable Member member) {
            boolean isMe = null != member && !isEmpty(member.getUserId()) && member.getUserId().equals(Cache.cache().userId);
            Member actMember = ActivityFragment.myActMember;
            boolean isManager = null != actMember && actMember.activityMemberDeletable();
            // 管理者不需要踢出
            if (forPicker) {
                holder.showButton2(false);
            } else {
                holder.showButton2(!isMe && isManager);
            }
            //holder.showButton2(!isManager && !forPicker);
            holder.showContent(member, "");
            mItemManger.bindView(holder.itemView, position);
        }

        @Override
        public int getSwipeLayoutResourceId(int i) {
            return R.id.ui_holder_view_contact_swipe_layout;
        }

        private int getFirstCharCount(char chr) {
            int ret = 0;
            for (int i = 0, size = getItemCount(); i < size; i++) {
                if (get(i).getSpell().charAt(0) == chr) {
                    ret++;
                }
            }
            return ret;
        }
    }

    private class StickDecoration extends RecyclerView.ItemDecoration {
        private int topHeight = getDimension(R.dimen.ui_static_dp_20);
        private int padding = getDimension(R.dimen.ui_base_dimen_margin_padding);
        private int textSize = getDimension(R.dimen.ui_base_text_size_little);
        private TextPaint textPaint;
        private Paint paint;
        private static final String FMT = "%s(%d人)";
        private float baseLine, textHeight;

        StickDecoration() {
            super();
            paint = new Paint();
            paint.setColor(getColor(R.color.textColorHintLightLight));
            textPaint = new TextPaint();
            textPaint.setAntiAlias(true);
            textPaint.setTextSize(textSize);
            textPaint.setColor(getColor(R.color.textColorHint));
            Paint.FontMetrics fm = textPaint.getFontMetrics();
            // 计算文字高度
            textHeight = fm.bottom - fm.top;
            baseLine = fm.bottom;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            super.getItemOffsets(outRect, view, parent, state);
            int position = parent.getChildAdapterPosition(view);
            if (isFirstInGroup(position)) {
                outRect.top = topHeight;
            } else {
                outRect.top = 0;
            }
        }

        @Override
        public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
            super.onDraw(c, parent, state);
            int left = parent.getPaddingLeft();
            int right = parent.getWidth() - parent.getPaddingRight();
            int childCount = parent.getChildCount();
            for (int i = 0; i < childCount; i++) {
                View view = parent.getChildAt(i);
                int position = parent.getChildAdapterPosition(view);
                //String textLine = members.get(position).getSpell().substring(0, 1);
                //textLine = format(FMT, textLine, getFirstCharCount(textLine.charAt(0)));
                if (isFirstInGroup(position)) {
                    float top = view.getTop() - topHeight;
                    float bottom = view.getTop();
                    drawBackground(c, left, top, right, bottom);
                    drawText(c, position, padding, bottom - (topHeight - textHeight) / 2 - baseLine);
                    //c.drawRect(left, top, right, bottom, paint);//绘制矩形背景
                    //c.drawText(textLine, left + padding, bottom - 30, textPaint);//绘制文本
                }
            }
        }

        @Override
        public void onDrawOver(Canvas c, RecyclerView parent, RecyclerView.State state) {
            super.onDrawOver(c, parent, state);
            int position = ((LinearLayoutManager) (parent.getLayoutManager())).findFirstVisibleItemPosition();
            if (position < 0) {
                return;
            }
            int left = parent.getPaddingLeft();
            int right = parent.getWidth() - parent.getPaddingRight();
            int top = parent.getPaddingTop();
            int bottom = top + topHeight;
            drawBackground(c, left, top, right, bottom);
            drawText(c, position, padding, bottom - (topHeight - textHeight) / 2 - baseLine);
            //c.drawRect(left, 0, right, topHeight, paint);//绘制红色矩形
            //String text = members.get(position).getSpell().substring(0, 1);
            //text = format(FMT, text, getFirstCharCount(text.charAt(0)));
            //c.drawText(text, 30, topHeight - 30, textPaint);//绘制文本
        }

        private void drawBackground(Canvas canvas, float left, float top, float right, float bottom) {
            // 绘制矩形背景
            canvas.drawRect(left, top, right, bottom, paint);
        }

        private void drawText(Canvas canvas, int position, float x, float y) {
            String text = mAdapter.get(position).getSpell().substring(0, 1);
            text = format(FMT, text, mAdapter.getFirstCharCount(text.charAt(0)));
            // 绘制文本
            canvas.drawText(text, x, y, textPaint);
        }

        private boolean isFirstInGroup(int position) {
            return position >= 0 && (position == 0 || mAdapter.get(position).getSpell().charAt(0) != mAdapter.get(position - 1).getSpell().charAt(0));
        }
    }
}
