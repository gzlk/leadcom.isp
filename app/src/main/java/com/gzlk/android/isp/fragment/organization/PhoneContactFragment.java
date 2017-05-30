package com.gzlk.android.isp.fragment.organization;

import android.Manifest;
import android.content.ContentResolver;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextPaint;
import android.view.View;
import android.widget.TextView;

import com.gzlk.android.isp.BuildConfig;
import com.gzlk.android.isp.R;
import com.gzlk.android.isp.adapter.RecyclerViewAdapter;
import com.gzlk.android.isp.api.SystemRequest;
import com.gzlk.android.isp.api.listener.OnSingleRequestListener;
import com.gzlk.android.isp.application.App;
import com.gzlk.android.isp.helper.DialogHelper;
import com.gzlk.android.isp.helper.SimpleDialogHelper;
import com.gzlk.android.isp.helper.StringHelper;
import com.gzlk.android.isp.helper.ToastHelper;
import com.gzlk.android.isp.holder.PhoneContactViewHolder;
import com.gzlk.android.isp.holder.common.SearchableViewHolder;
import com.gzlk.android.isp.lib.view.SlidView;
import com.gzlk.android.isp.listener.OnViewHolderClickListener;
import com.gzlk.android.isp.model.common.Contact;
import com.gzlk.android.isp.model.Dao;
import com.gzlk.android.isp.model.Model;
import com.gzlk.android.isp.model.organization.Invitation;
import com.gzlk.android.isp.model.organization.Organization;
import com.gzlk.android.isp.model.user.User;
import com.hlk.hlklib.etc.Utility;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.view.CorneredView;
import com.hlk.hlklib.tasks.AsyncedTask;
import com.litesuits.orm.db.assit.QueryBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * <b>功能描述：</b>手机通讯录<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/05/09 16:11 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/05/09 16:11 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class PhoneContactFragment extends BaseOrganizationFragment {

    public static PhoneContactFragment newInstance(String params) {
        PhoneContactFragment pcf = new PhoneContactFragment();
        String[] strings = splitParameters(params);
        Bundle bundle = new Bundle();
        bundle.putString(PARAM_QUERY_ID, strings[0]);
        bundle.putString(PARAM_SQUAD_ID, strings[1]);
        pcf.setArguments(bundle);
        return pcf;
    }

    private static boolean hasPermission = false;

    @ViewId(R.id.ui_phone_contact_slid_view)
    private SlidView slidView;
    @ViewId(R.id.ui_phone_contact_center_text_container)
    private CorneredView centerTextContainer;
    @ViewId(R.id.ui_phone_contact_center_text)
    private TextView centerTextView;

    // holder
    private SearchableViewHolder searchableViewHolder;

    private ContactAdapter mAdapter;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        hasPermission = false;
        checkPermission();
        super.onActivityCreated(savedInstanceState);
    }

    @Override
    public int getLayout() {
        return R.layout.fragment_phone_contact;
    }

    @Override
    protected void onDelayRefreshComplete(@DelayType int type) {

    }

    @Override
    public void doingInResume() {
        setCustomTitle(R.string.ui_squad_contact_menu_2);
        if (BuildConfig.DEBUG) {
            new Dao<>(Contact.class).clear();
        }
        if (hasPermission) {
            readyToReadContact();
        }
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

    // 尝试读取手机通讯录
    private void checkPermission() {
        if (!hasPermission(Manifest.permission.READ_CONTACTS)) {
            String text = StringHelper.getString(R.string.ui_text_permission_contact_request);
            String denied = StringHelper.getString(R.string.ui_text_permission_contact_denied);
            tryGrantPermission(Manifest.permission.READ_CONTACTS, GRANT_CONTACTS, text, denied);
        } else {
            hasPermission = true;
        }
    }

    @Override
    public void permissionGranted(String[] permissions, int requestCode) {
        if (requestCode == GRANT_CONTACTS) {
            hasPermission = true;
        }
        super.permissionGranted(permissions, requestCode);
    }

    @Override
    public void permissionGrantFailed(int requestCode) {
        super.permissionGrantFailed(requestCode);
        if (requestCode == GRANT_CONTACTS) {
            setNothingText(R.string.ui_phone_contact_no_permission);
            displayNothing(true);
            finish();
        }
    }

    /**
     * 读取联系人
     */
    private void readyToReadContact() {
        setNothingText(R.string.ui_phone_contact_no_more);
        // 读取缓存中已经保存过的信息
        initializeAdapter();
    }

    private void initializeAdapter() {
        if (null == searchableViewHolder) {
            searchableViewHolder = new SearchableViewHolder(mRootView, this);
            searchableViewHolder.setOnSearchingListener(searchingListener);
            slidView.setOnSlidChangedListener(onSlidChangedListener);
            slidView.clearIndex();
        }
        if (null == mAdapter) {
            mAdapter = new ContactAdapter();
            mRecyclerView.addItemDecoration(new StickDecoration());
            mRecyclerView.setAdapter(mAdapter);
            // 读取缓存中已经处理过的联系人列表
            gotContactFromCache("");
            // 尝试读取手机联系人并更新当前列表
            new ContactTask().exec();
        }
    }

    private String searchingText = "";

    private SearchableViewHolder.OnSearchingListener searchingListener = new SearchableViewHolder.OnSearchingListener() {
        @Override
        public void onSearching(String text) {
            searchingText = StringHelper.isEmpty(text) ? "" : text;
            gotContactFromCache(searchingText);
        }
    };

    private void gotContactFromCache(String searchingText) {
        mAdapter.clear();
        QueryBuilder<Contact> query = new QueryBuilder<>(Contact.class);
        if (!StringHelper.isEmpty(searchingText)) {
            String like = "%" + searchingText + "%";
            query = query.where(Model.Field.Name + " LIKE ?", like);
        }
        query = query.orderBy(Model.Field.Name);
        List<Contact> contacts = new Dao<>(Contact.class).query(query);
        if (null != contacts) {
            for (Contact contact : contacts) {
                // 检索此用户是否已被邀请
                contact.setInvited(invited(contact.getPhone()));
                contact.setMember(isMember(contact.getUserId(), mOrganizationId, mSquadId));
                mAdapter.add(contact);
                slidView.add(contact.getSpell());
            }
            mAdapter.sort();
        }
    }

    private User getUser(String phone) {
        List<User> users = new Dao<>(User.class).query(User.Field.Phone, phone);
        return (null == users || users.size() < 1) ? null : users.get(0);
    }

    private boolean invited(String phone) {
        User user = getUser(phone);
        if (null != user) {
            QueryBuilder<Invitation> builder = new QueryBuilder<>(Invitation.class);
            if (!StringHelper.isEmpty(mSquadId)) {
                // 邀请进小组的
                builder = builder.whereEquals(Organization.Field.SquadId, mSquadId);
                //.whereAppendAnd().whereAppend(Organization.Field.GroupId + " IS NULL");
            } else {
                // 邀请进组织的
                builder = builder.whereEquals(Organization.Field.GroupId, mOrganizationId);
                //.whereAppendAnd().whereAppend(Organization.Field.SquadId + " IS NULL");
            }
            builder = builder.whereAppendAnd().whereEquals(Invitation.Field.InviteeId, user.getId());
            List<Invitation> list = new Dao<>(Invitation.class).query(builder);
            return null != list && list.size() > 0;
        }
        return false;
    }

    private SlidView.OnSlidChangedListener onSlidChangedListener = new SlidView.OnSlidChangedListener() {
        @Override
        public void slidChanged(String text, boolean shown) {
            centerTextContainer.setVisibility(shown ? View.VISIBLE : View.GONE);
            centerTextView.setText(text);
            scrolling(text);
        }
    };

    private void scrolling(String text) {
        int position = -1;
        for (int i = 0, len = mAdapter.getItemCount(); i < len; i++) {
            if (mAdapter.get(i).getSpell().charAt(0) == text.charAt(0)) {
                position = i;
                break;
            }
        }
        if (position >= 0) {
            LinearLayoutManager layoutManager = (LinearLayoutManager) mRecyclerView.getLayoutManager();
            int firstPosition = layoutManager.findFirstVisibleItemPosition();
            int lastPosition = layoutManager.findLastVisibleItemPosition();
            if (position <= firstPosition) {
                mRecyclerView.scrollToPosition(position);
            } else if (position <= lastPosition) {
                int top = mRecyclerView.getChildAt(position - firstPosition).getTop();
                mRecyclerView.scrollBy(0, top);
            } else {
                mRecyclerView.scrollToPosition(position);
            }
        }
    }

    private OnViewHolderClickListener onViewHolderClickListener = new OnViewHolderClickListener() {
        @Override
        public void onClick(int index) {
            // 添加指定index的联系人到小组或组织
            Contact contact = mAdapter.get(index);
            invite(contact.getPhone());
        }
    };

    // 发起邀请
    private void invite(String phone) {
        if (!StringHelper.isEmpty(mSquadId)) {
            // 添加到小组
            inviteToSquad(phone);
        } else {
            // 添加到组织
            inviteToOrganization(phone);
        }
    }

    // 邀请进小组
    private void inviteToSquad(String phone) {
        SystemRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<User>() {
            @Override
            public void onResponse(User user, boolean success, String message) {
                super.onResponse(user, success, message);
                if (success) {
                    ToastHelper.make().showMsg(R.string.ui_phone_contact_invite_success);
                }
            }
        }).inviteJoinIntoSquad(phone, mSquadId);
    }

    // 邀请进组织
    private void inviteToOrganization(String phone) {
        SystemRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<User>() {
            @Override
            public void onResponse(User user, boolean success, String message) {
                super.onResponse(user, success, message);
                if (success) {
                    ToastHelper.make().showMsg(R.string.ui_phone_contact_invite_success);
                }
            }
        }).inviteJoinIntoGroup(phone, mOrganizationId);
    }

    private void warningNoContact() {
        SimpleDialogHelper.init(Activity()).show(R.string.ui_phone_contact_nothing_read, new DialogHelper.OnDialogConfirmListener() {
            @Override
            public boolean onConfirm() {
                return true;
            }
        });
    }

    private class ContactAdapter extends RecyclerViewAdapter<PhoneContactViewHolder, Contact> {

        @Override
        public PhoneContactViewHolder onCreateViewHolder(View itemView, int viewType) {
            PhoneContactViewHolder holder = new PhoneContactViewHolder(itemView, PhoneContactFragment.this);
            holder.addOnViewHolderClickListener(onViewHolderClickListener);
            return holder;
        }

        @Override
        public int itemLayout(int viewType) {
            return R.layout.holder_view_phone_contact;
        }

        @Override
        public void onBindHolderOfView(PhoneContactViewHolder holder, int position, @Nullable Contact item) {
            holder.showContent(item, searchingText);
        }

        @Override
        protected int comparator(Contact item1, Contact item2) {
            return item1.getSpell().compareTo(item2.getSpell());
        }

        private int getFirstCharCount(char chr) {
            int ret = 0, size = getItemCount();
            for (int i = 0; i < size; i++) {
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
                if (isFirstInGroup(position)) {
                    float top = view.getTop() - topHeight;
                    float bottom = view.getTop();
                    drawBackground(c, left, top, right, bottom);
                    drawText(c, position, padding, bottom - (topHeight - textHeight) / 2 - baseLine);
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

    private class ContactTask extends AsyncedTask<Void, Integer, Void> {

        private String[] FIELDS = new String[]{
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER
        };

        private List<String[]> contacts = new ArrayList<>();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            materialHorizontalProgressBar.setVisibility(View.VISIBLE);
            displayNothing(false);
            displayLoading(true);
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            displayLoading(false);
            //displayNothing(mAdapter.getItemCount() <= 0);
            materialHorizontalProgressBar.setVisibility(View.GONE);
            gotContactFromCache("");
        }

        @Override
        protected Void doInBackground(Void... params) {
            gotContacts();
            handleContact();
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            if (materialHorizontalProgressBar.getMax() != values[2]) {
                materialHorizontalProgressBar.setMax(values[2]);
            }
            if (values[0] == 0) {
                materialHorizontalProgressBar.setSecondaryProgress(values[1]);
            } else if (values[0] == 1) {
                materialHorizontalProgressBar.setProgress(values[1]);
            }
            if (values[0] == 1 && values[1] == 0) {
                warningNoContact();
            }
        }

        // 读取手机联系人
        @SuppressWarnings("ConstantConditions")
        private void gotContacts() {
            try {
                ContentResolver resolver = App.app().getContentResolver();
                Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
                Cursor cursor = resolver.query(uri, FIELDS, null, null, null);
                if (null != cursor) {
                    try {
                        int index = 0, max = cursor.getCount();
                        publishProgress(0, index, max);
                        while (cursor.moveToNext()) {
                            String name = cursor.getString(cursor.getColumnIndex(FIELDS[0]));
                            String phone = cursor.getString(cursor.getColumnIndex(FIELDS[1]));
                            if (!StringHelper.isEmpty(phone)) {
                                phone = Utility.filterNumbers(phone);
                            }
                            // 名字不为空且号码为手机号码时才加入缓存
                            if (!StringHelper.isEmpty(name) && Utility.isItMobilePhone(phone, true)) {
                                contacts.add(new String[]{name, phone});
                            }
                            index++;
                            if (BuildConfig.DEBUG) {
                                Thread.sleep(3);
                            }
                            publishProgress(0, index, max);
                        }
                    } finally {
                        cursor.close();
                    }
                }
            } catch (Exception ignore) {
                ignore.printStackTrace();
            }
        }

        private String getUserId(String phone, Dao<User> dao) {
            List<User> list = dao.query(User.Field.Phone, phone);
            if (null != list && list.size() > 0) {
                return list.get(0).getId();
            }
            return null;
        }

        // 处理联系人和本地缓存关系
        private void handleContact() {
            int index = 0, max = contacts.size();
            publishProgress(1, index, max);
            try {
                Dao<Contact> dao = new Dao<>(Contact.class);
                Dao<User> udao = new Dao<>(User.class);
                for (String[] strings : contacts) {
                    String name = strings[0];
                    String phone = strings[1];
                    Contact contact = new Contact();
                    contact.setUserId(getUserId(phone, udao));
                    contact.setName(name);
                    contact.setPhone(phone);
                    contact.setInvited(false);
                    dao.save(contact);
                    // 逐条插入
                    if (BuildConfig.DEBUG) {
                        Thread.sleep(3);
                    }
                    index++;
                    publishProgress(1, index, max);
                }
            } catch (Exception ignore) {
                ignore.printStackTrace();
            }
        }
    }
}
