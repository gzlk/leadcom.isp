package com.leadcom.android.isp.fragment.organization;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.database.Cursor;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextPaint;
import android.view.View;
import android.widget.TextView;

import com.hlk.hlklib.etc.Utility;
import com.hlk.hlklib.lib.inject.ViewId;
import com.hlk.hlklib.lib.view.CorneredView;
import com.hlk.hlklib.tasks.AsyncedTask;
import com.leadcom.android.isp.R;
import com.leadcom.android.isp.adapter.RecyclerViewAdapter;
import com.leadcom.android.isp.api.listener.OnSingleRequestListener;
import com.leadcom.android.isp.api.org.InvitationRequest;
import com.leadcom.android.isp.application.App;
import com.leadcom.android.isp.cache.Cache;
import com.leadcom.android.isp.etc.Utils;
import com.leadcom.android.isp.fragment.base.BaseFragment;
import com.leadcom.android.isp.helper.StringHelper;
import com.leadcom.android.isp.helper.ToastHelper;
import com.leadcom.android.isp.helper.popup.DeleteDialogHelper;
import com.leadcom.android.isp.helper.popup.DialogHelper;
import com.leadcom.android.isp.helper.popup.SimpleDialogHelper;
import com.leadcom.android.isp.holder.common.InputableSearchViewHolder;
import com.leadcom.android.isp.holder.organization.PhoneContactViewHolder;
import com.leadcom.android.isp.lib.permission.MPermission;
import com.leadcom.android.isp.lib.permission.annotation.OnMPermissionDenied;
import com.leadcom.android.isp.lib.permission.annotation.OnMPermissionGranted;
import com.leadcom.android.isp.lib.permission.annotation.OnMPermissionNeverAskAgain;
import com.leadcom.android.isp.lib.view.SlidView;
import com.leadcom.android.isp.listener.OnTitleButtonClickListener;
import com.leadcom.android.isp.listener.OnViewHolderClickListener;
import com.leadcom.android.isp.model.common.Contact;
import com.leadcom.android.isp.model.organization.Invitation;
import com.leadcom.android.isp.model.organization.Member;
import com.leadcom.android.isp.model.user.Name;
import com.leadcom.android.isp.model.user.Word;
import com.leadcom.android.isp.service.ContactService;
import com.leadcom.android.isp.task.AsyncExecutableTask;

import java.io.UnsupportedEncodingException;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Random;

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

public class PhoneContactFragment extends GroupBaseFragment {

    private static final String PARAM_MEMBERS = "pcf_members";
    private static final int RANDOM_SIZE = 5000;
    /**
     * 标记是否为测试用，此时不读取手机联系人，而是随机生成人名和电话号码
     */
    private static boolean RANDOM = false;

    public static PhoneContactFragment newInstance(Bundle bundle) {
        PhoneContactFragment pcf = new PhoneContactFragment();
        pcf.setArguments(bundle);
        return pcf;
    }

    public static void open(BaseFragment fragment, String groupId, String squadId, String name, ArrayList<Member> members) {
        Bundle bundle = new Bundle();
        // 组织的id
        bundle.putString(PARAM_QUERY_ID, groupId);
        // 小组的id
        bundle.putString(PARAM_SQUAD_ID, squadId);
        bundle.putString(PARAM_NAME, name);
        // 已有的成员列表
        bundle.putSerializable(PARAM_MEMBERS, members);
        fragment.openActivity(PhoneContactFragment.class.getName(), bundle, true, false);
    }

    @ViewId(R.id.ui_phone_contact_slid_view)
    private SlidView slidView;
    @ViewId(R.id.ui_holder_view_searchable_container)
    private View searchView;
    @ViewId(R.id.ui_phone_contact_center_text_container)
    private CorneredView centerTextContainer;
    @ViewId(R.id.ui_phone_contact_center_text)
    private TextView centerTextView;

    // holder
    private InputableSearchViewHolder inputableSearchViewHolder;
    private ArrayList<Member> members = new ArrayList<>();
    private ArrayList<Contact> searching = new ArrayList<>();
    private ContactAdapter mAdapter, sAdapter;
    private static String searchingText = "";
    private ContactRefreshedReceiver refreshedReceiver;
    private String mName;

    @SuppressWarnings("unchecked")
    @Override
    protected void getParamsFromBundle(Bundle bundle) {
        super.getParamsFromBundle(bundle);
        mName = bundle.getString(PARAM_NAME, "");
        members = (ArrayList<Member>) bundle.getSerializable(PARAM_MEMBERS);
        if (null == members) {
            members = new ArrayList<>();
        }
    }

    @Override
    protected void saveParamsToBundle(Bundle bundle) {
        super.saveParamsToBundle(bundle);
        bundle.putString(PARAM_NAME, mName);
        bundle.putSerializable(PARAM_MEMBERS, members);
    }

    private final String[] permissions = new String[]{
            Manifest.permission.READ_CONTACTS
    };

    private void requestReadContactsPermission() {
        MPermission.printMPermissionResult(true, Activity(), permissions);
        MPermission.with(this)
                .setRequestCode(GRANT_CONTACTS)
                .permissions(permissions)
                .request();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        MPermission.onRequestPermissionsResult(this, requestCode, permissions, grantResults);
    }

    @OnMPermissionGranted(GRANT_CONTACTS)
    public void onBasePermissionRequested() {
        MPermission.printMPermissionResult(false, Activity(), permissions);
    }

    @OnMPermissionDenied(GRANT_CONTACTS)
    @OnMPermissionNeverAskAgain(GRANT_CONTACTS)
    public void onBasePermissionRequestFailed() {
        ToastHelper.make().showMsg(R.string.ui_text_permission_contact_denied);
        MPermission.printMPermissionResult(false, Activity(), permissions);
        finish();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        searchingText = "";
        requestReadContactsPermission();
        super.onActivityCreated(savedInstanceState);
        setLoadingText(R.string.ui_phone_contact_waiting_read_contacts);
        setCustomTitle(format("%s(%s)", StringHelper.getString(R.string.ui_squad_contact_menu_2), mName));
        if (null == refreshedReceiver) {
            refreshedReceiver = new ContactRefreshedReceiver();
            IntentFilter intent = new IntentFilter();
            intent.addAction(ContactService.ACTION_REFRESH_COMPLETE);
            Activity().registerReceiver(refreshedReceiver, intent);
        }
        if (!Cache.isReleasable()) {
            setRightIcon(R.string.ui_icon_more);
            setRightTitleClickListener(new OnTitleButtonClickListener() {
                @Override
                public void onClick() {
                    warningRandomContact();
                }
            });
        }
    }

    private void warningRandomContact() {
        DeleteDialogHelper.helper().init(this).setOnDialogConfirmListener(new DialogHelper.OnDialogConfirmListener() {
            @Override
            public boolean onConfirm() {
                RANDOM = true;
                new ContactTask(PhoneContactFragment.this, members).exec();
                return true;
            }
        }).setTitleText(StringHelper.getString(R.string.ui_phone_contact_random_contacts, RANDOM_SIZE)).setConfirmText(R.string.ui_base_text_confirm).show();
    }

    @Override
    public void onDestroy() {
        if (null != refreshedReceiver) {
            Activity().unregisterReceiver(refreshedReceiver);
        }
        super.onDestroy();
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
//        if (!Cache.isReleasable()) {
//            new Dao<>(Contact.class).clear();
//        }
        if (hasPermission(Manifest.permission.READ_CONTACTS)) {
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

    /**
     * 读取联系人
     */
    private void readyToReadContact() {
        setNothingText(R.string.ui_phone_contact_no_more);
        // 读取缓存中已经保存过的信息
        initializeAdapter();
    }

    private void initializeAdapter() {
        if (null == inputableSearchViewHolder) {
            inputableSearchViewHolder = new InputableSearchViewHolder(searchView, this);
            inputableSearchViewHolder.setOnSearchingListener(searchingListener);
            inputableSearchViewHolder.setMaxInputLength(11);
            slidView.setOnSlidChangedListener(onSlidChangedListener);
            slidView.clearIndex();
        }
        if (null == mAdapter) {
            displayLoading(true);
            mAdapter = new ContactAdapter();
            mAdapter.setName("mAdapter");
            mAdapter.setOnDataHandingListener(handingListener);
            mRecyclerView.addItemDecoration(new StickDecoration());
            mRecyclerView.setAdapter(mAdapter);
            sAdapter = new ContactAdapter();
            sAdapter.setName("sAdapter");
            sAdapter.setOnDataHandingListener(handingListener);
            // 读取缓存中已经处理过的联系人列表
            //gotContactFromCache();
            // 尝试读取手机联系人并更新当前列表
            new ContactTask(this, members).exec();
        }
    }

    private InputableSearchViewHolder.OnSearchingListener searchingListener = new InputableSearchViewHolder.OnSearchingListener() {
        @Override
        public void onSearching(String text) {
            if (!isEmpty(text) && Utils.isNumber(text)) {
                if (text.length() > 3) {
                    searchingText = text;
                    resetContactAdapter();
                }
            } else {
                searchingText = isEmpty(text) ? "" : text;
                resetContactAdapter();
            }
        }
    };

    private void resetCustomTitle(int count) {
        String text = StringHelper.getString(R.string.ui_phone_contact_title_number, count);
        setCustomTitle(text);
    }

    private void resetContactAdapter() {
        setLoadingText(isEmpty(searchingText) ? R.string.ui_phone_contact_waiting_read_contacts_pagination : R.string.ui_phone_contact_waiting_searching_contacts);
        if (!isEmpty(searchingText)) {
            // 搜索时用搜索结果的adapter
            if (mRecyclerView.getAdapter() != sAdapter) {
                mRecyclerView.setAdapter(sAdapter);
            }
            new FilterContactTask(sAdapter, searching).exec();
        } else {
            // 非搜索时用普通的adapter
            if (mRecyclerView.getAdapter() != mAdapter) {
                mRecyclerView.setAdapter(mAdapter);
            }
            resetCustomTitle(mAdapter.getItemCount());
        }

//        slidView.setVisibility(View.GONE);
//        mAdapter.clear();
//        if (contacts.size() > 0) {
//            for (Contact contact : contacts) {
//                // 搜索时
//                if (!isEmpty(searchingText) && !(contact.getName().contains(searchingText) || contact.getPhone().contains(searchingText))) {
//                    continue;
//                }
//                mAdapter.add(contact);
//                slidView.add(contact.getSpell());
//            }
//            mAdapter.sort();
//        }
//        slidView.setVisibility(mAdapter.getItemCount() <= 0 ? View.GONE : View.VISIBLE);
//        setNothingText(R.string.ui_phone_contact_no_more);
//        displayNothing(mAdapter.getItemCount() <= 0);
    }

    private SlidView.OnSlidChangedListener onSlidChangedListener = new SlidView.OnSlidChangedListener() {
        @Override
        public void slidChanged(String text, boolean shown, boolean active) {
            centerTextContainer.setVisibility(shown ? View.VISIBLE : View.GONE);
            centerTextView.setText(text);
            if (!isEmpty(text)) {
                scrolling(text);
            }
        }
    };

    private void scrolling(String text) {
        int position = -1;
        Iterator<Contact> iterator = isEmpty(searchingText) ? mAdapter.iterator() : sAdapter.iterator();
        int i = 0;
        while (iterator.hasNext()) {
            Contact contact = iterator.next();
            if (contact.getSpell().charAt(0) == text.charAt(0)) {
                position = i;
                break;
            }
            i++;
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
            Contact contact = isEmpty(searchingText) ? mAdapter.get(index) : sAdapter.get(index);
            String phone = contact.getPhone();
            if (phone.length() > 11) {
                phone = phone.substring(phone.length() - 11);
            }
            invite(phone, index);
        }
    };

    // 发起邀请
    private void invite(String phone, int index) {
        if (!StringHelper.isEmpty(mSquadId)) {
            // 添加到小组
            inviteToSquad(phone, index);
        } else {
            // 添加到组织
            inviteToOrganization(phone, index);
        }
    }

    // 邀请进小组
    private void inviteToSquad(String phone, final int index) {
        InvitationRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<Invitation>() {
            @Override
            public void onResponse(Invitation invitation, boolean success, String message) {
                super.onResponse(invitation, success, message);
                if (success) {
                    if (isEmpty(searchingText)) {
                        mAdapter.get(index).setInvited(true);
                        mAdapter.notifyItemChanged(index);
                    } else {
                        sAdapter.get(index).setInvited(true);
                        sAdapter.notifyItemChanged(index);
                    }
                    ToastHelper.make().showMsg(R.string.ui_phone_contact_invite_success);
                }
            }
        }).inviteToSquadFromPhoneContact(phone, mOrganizationId, mSquadId);
    }

    // 邀请进组织
    private void inviteToOrganization(String phone, final int index) {
        InvitationRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<Invitation>() {
            @Override
            public void onResponse(Invitation invitation, boolean success, String message) {
                super.onResponse(invitation, success, message);
                if (success) {
                    if (isEmpty(searchingText)) {
                        mAdapter.get(index).setInvited(true);
                        mAdapter.notifyItemChanged(index);
                    } else {
                        sAdapter.get(index).setInvited(true);
                        sAdapter.notifyItemChanged(index);
                    }
                    ToastHelper.make().showMsg(R.string.ui_phone_contact_invite_success);
                }
            }
        }).inviteToGroupFromPhoneContact(phone, mOrganizationId);
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
    }

    private ArrayList<String> chars = new ArrayList<>();

    private RecyclerViewAdapter.OnDataHandingListener handingListener = new RecyclerViewAdapter.OnDataHandingListener() {
        @Override
        public void onStart() {
            log("onDataHandingListener.onStart()");
            //isHandlingPagination = true;
            slidView.clearIndex();
            materialHorizontalProgressBar.setMax(0);
            materialHorizontalProgressBar.setProgress(0);
            materialHorizontalProgressBar.setSecondaryProgress(0);
            materialHorizontalProgressBar.setVisibility(View.VISIBLE);
        }

        @Override
        public void onProgress(int currentPage, int maxPage, int maxCount) {
            if (materialHorizontalProgressBar.getMax() != maxPage) {
                materialHorizontalProgressBar.setMax(maxPage);
            }
            log(format("onDataHandingListener.onProgress(%d, %d, %d)", currentPage + 1, maxPage, maxCount));
            materialHorizontalProgressBar.setProgress(currentPage + 1);
            int start = currentPage * RecyclerViewAdapter.PAGER_SIZE;
            int end = start + RecyclerViewAdapter.PAGER_SIZE;
            if (end >= maxCount) {
                end = maxCount - 1;
            }
            for (int i = start; i <= end; i++) {
                Contact contact = isEmpty(searchingText) ? App.app().getContacts().get(i) : searching.get(i);
                if (isEmpty(searchingText)) {
                    String chr = contact.getSpell().substring(0, 1);
                    if (!chars.contains(chr)) {
                        chars.add(chr);
                    }
                } else {
                    slidView.add(contact.getSpell());
                }
            }
        }

        @Override
        public void onComplete() {
            log("onDataHandingListener.onComplete()");
            displayLoading(false);
            setNothingText(R.string.ui_phone_contact_no_more);
            final int cnt = isEmpty(searchingText) ? mAdapter.getItemCount() : sAdapter.getItemCount();
            displayNothing(cnt <= 0);
            if (isEmpty(searchingText)) {
                slidView.clearIndex();
                for (String string : chars) {
                    slidView.add(string);
                }
            }
            slidView.post(new Runnable() {
                @Override
                public void run() {
                    slidView.setVisibility(cnt > 0 ? View.VISIBLE : View.GONE);
                }
            });
            resetCustomTitle(cnt);
            materialHorizontalProgressBar.setVisibility(View.GONE);
            //isHandlingPagination = false;
        }
    };

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
            String text = isEmpty(searchingText) ? mAdapter.get(position).getSpell().substring(0, 1) : sAdapter.get(position).getSpell().substring(0, 1);
            text = format(FMT, text, getFirstCharCount(text.charAt(0)));
            // 绘制文本
            canvas.drawText(text, x, y, textPaint);
        }

        private boolean isFirstInGroup(int position) {
            if (position > 0) {
                Contact cur, pre;
                if (isEmpty(searchingText)) {
                    cur = mAdapter.get(position);
                    pre = mAdapter.get(position - 1);
                } else {
                    cur = sAdapter.get(position);
                    pre = sAdapter.get(position - 1);
                }
                return cur.getSpell().charAt(0) != pre.getSpell().charAt(0);
            }
            return position == 0;
        }

        private int getFirstCharCount(char chr) {
            Iterator<Contact> iterator = isEmpty(searchingText) ? App.app().getContacts().iterator() : searching.iterator();
            int ret = 0;
            while (iterator.hasNext()) {
                if (iterator.next().getSpell().charAt(0) != chr) {
                    if (ret > 0) {
                        break;
                    }
                } else {
                    ret++;
                }
            }
            return ret;
        }
    }

    private static class FilterContactTask extends AsyncExecutableTask<Void, Void, Void> {

        FilterContactTask(ContactAdapter adapter, ArrayList<Contact> contacts) {
            adapterReference = new SoftReference<>(adapter);
            searchingResult = contacts;
        }

        private SoftReference<ContactAdapter> adapterReference;
        private ArrayList<Contact> searchingResult;

        @Override
        protected void doBeforeExecute() {
            super.doBeforeExecute();
        }

        @Override
        protected Void doInTask(Void... voids) {
            if (!isEmpty(searchingText)) {
                if (null == searchingResult) {
                    searchingResult = new ArrayList<>();
                }
                searchingResult.clear();
                for (int i = 0, len = App.app().getContacts().size(); i < len; i++) {
                    Contact contact = App.app().getContacts().get(i);
                    // 搜索时
                    if (contact.getName().contains(searchingText) || contact.getPhone().contains(searchingText)) {
                        searchingResult.add(contact);
                    }
                }
            }
            return null;
        }

        @Override
        protected void doAfterExecute() {
            if (!isEmpty(searchingText)) {
                if (null != adapterReference.get()) {
                    adapterReference.get().setData(searchingResult);
                }
            }
            super.doAfterExecute();
        }
    }

    private class ContactRefreshedReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (null != intent) {
                String action = intent.getAction();
                log("received broadcast " + action);
                if (ContactService.ACTION_REFRESH_COMPLETE.equals(action)) {
                    Activity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            resetContactAdapter();
                        }
                    });
                }
            }
        }
    }

    private static class ContactTask extends AsyncedTask<Void, Integer, Void> {
        private SoftReference<PhoneContactFragment> reference;
        private SoftReference<ArrayList<Member>> memberReference;

        ContactTask(PhoneContactFragment fragment, ArrayList<Member> members) {
            reference = new SoftReference<>(fragment);
            memberReference = new SoftReference<>(members);
        }

        private String[] FIELDS = new String[]{
                ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
                ContactsContract.CommonDataKinds.Phone.NUMBER
        };

        private List<String[]> nameList = new ArrayList<>();

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);
            PhoneContactFragment fragment = reference.get();
            fragment.displayLoading(false);
            //displayNothing(mAdapter.getItemCount() <= 0);
            fragment.materialHorizontalProgressBar.setVisibility(View.GONE);
            //resetContactAdapter();
            fragment.resetCustomTitle(App.app().getContacts().size());
            fragment.mAdapter.setData(App.app().getContacts());
            //new ReadingContactTask(fragment).exec();
        }

        @Override
        protected Void doInBackground(Void... params) {
            if (RANDOM) {
                RANDOM = false;
                randomContacts();
                handleContact();
            } else {
                if (App.app().getContacts().size() <= 0) {
                    gotContacts();
                    handleContact();
                }
            }
            return null;
        }

        @Override
        protected void onProgressUpdate(Integer... values) {
            super.onProgressUpdate(values);
            PhoneContactFragment fragment = reference.get();
            if (fragment.materialHorizontalProgressBar.getMax() != values[2]) {
                fragment.materialHorizontalProgressBar.setMax(values[2]);
            }
            if (values[0] == 0) {
                fragment.materialHorizontalProgressBar.setSecondaryProgress(values[1]);
            } else if (values[0] == 1) {
                fragment.materialHorizontalProgressBar.setProgress(values[1]);
            }
            if (values[0] == 1 && values[2] == 0) {
                fragment.warningNoContact();
            }
        }

        // 随机生成联系人列表
        private void randomContacts() {
            long start = System.currentTimeMillis();
            ArrayList<Word> words = Word.fromJson(StringHelper.getAssetString("json/custom.words.json"));
            ArrayList<Word> specials = Word.fromJson(StringHelper.getAssetString("json/special.words.json"));
            ArrayList<Name> names = Name.fromJson(StringHelper.getAssetString("json/custom.names.json"));
            String familyNames = StringHelper.getString(R.string.temp_100_family_names);
            assert familyNames != null;
            String[] lastNames = familyNames.replace("\n", "").split(" ");
            int familyNameSize = lastNames.length, wordSize = words.size(), specialSize = specials.size(), nameSize = names.size();
            // 姓，随机生成
            int randomLastName;
            // 是否单名，随机生成
            boolean isSingleName, isSpecialName;

            Random random = new Random();
            String name, phone;
            HashMap<String, String> namesMap = new HashMap<>();
            while (namesMap.size() < RANDOM_SIZE) {
                // 生成姓
                randomLastName = random.nextInt(familyNameSize);
                if (randomLastName == familyNameSize) {
                    randomLastName = randomLastName - 1;
                }
                name = lastNames[randomLastName];
                // 是否单字
                isSingleName = random.nextInt(100) % 2 == 0;
                if (isSingleName) {
                    // 是否生僻字
                    isSpecialName = random.nextInt(100) % 2 == 0;
                    Word word = isSpecialName ? specials.get(random.nextInt(specialSize)) : words.get(random.nextInt(wordSize));
                    name += word.getWord();
                } else {
                    Name n = names.get(random.nextInt(nameSize));
                    name += n.getName();
                }
                // 生成随机电话号码 130 - 139 之间
                phone = String.valueOf(getRandomPhone(random));
                // 不管名字是否相同
                namesMap.put(name, phone);
            }
            long end = System.currentTimeMillis();
            log(format("random contact(%d) cost: %d milliseconds.", RANDOM_SIZE, end - start));
            nameList.clear();
            for (Map.Entry<String, String> entry : namesMap.entrySet()) {
                nameList.add(new String[]{entry.getKey(), entry.getValue()});
            }
            end = System.currentTimeMillis();
            log(format("handle contact(%d) cost: %d milliseconds.", RANDOM_SIZE, end - start));
        }

        private long getRandomPhone(Random random) {
            return (13000000000L + random.nextInt(999999999));
        }

        /**
         * 随机汉字
         */
        @SuppressWarnings("unused")
        private char getRandomWord() {
            return (char) (0x4e00 + (int) (Math.random() * (0x9fa5 - 0x4e00 + 1)));
        }

        /**
         * 生成随机简体汉字
         */
        @SuppressWarnings("unused")
        private char getRandomChar() {
            String str = "";
            int highPos;
            int lowPos;
            Random random = new Random();

            highPos = (176 + Math.abs(random.nextInt(39)));
            lowPos = (161 + Math.abs(random.nextInt(93)));
            // 一个汉字由两个字节组成
            byte[] b = new byte[2];
            b[0] = (Integer.valueOf(highPos)).byteValue();
            b[1] = (Integer.valueOf(lowPos)).byteValue();
            try {
                str = new String(b, "GBK");
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
            return str.charAt(0);
        }

        // 读取手机联系人
        private void gotContacts() {
            try {
                long start = System.currentTimeMillis();
                ContentResolver resolver = App.app().getContentResolver();
                Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
                Cursor cursor = resolver.query(uri, FIELDS, null, null, null);
                int max = 0;
                if (null != cursor) {
                    try {
                        int index = 0;
                        max = cursor.getCount();
                        publishProgress(0, index, max);
                        while (cursor.moveToNext()) {
                            String name = cursor.getString(cursor.getColumnIndex(FIELDS[0]));
                            String phone = cursor.getString(cursor.getColumnIndex(FIELDS[1]));
                            if (!StringHelper.isEmpty(phone)) {
                                phone = Utility.filterNumbers(phone);
                            }
                            // 名字不为空且号码为手机号码时才加入缓存
                            if (!StringHelper.isEmpty(name) && Utils.isItMobilePhone(phone)) {
                                nameList.add(new String[]{name, phone});
                            }
                            index++;
                            //log(format("read progress, index: %d, name: %s, phone: %s", index, name, phone));
                            publishProgress(0, index, max);
                        }
                    } finally {
                        cursor.close();
                    }
                }
                long end = System.currentTimeMillis();
                log(format("reading contact(%d) cost: %d milliseconds.", max, end - start));
            } catch (Exception ignore) {
                ignore.printStackTrace();
            }
        }

        /**
         * 查找成员中是否有人具有相同的手机号码
         */
        private boolean isMemberExists(String phone) {
            for (int i = 0, len = memberReference.get().size(); i < len; i++) {
                Member member = memberReference.get().get(i);
                if (!isEmpty(member.getPhone()) && member.getPhone().equals(phone)) {
                    return true;
                }
            }
            return false;
        }

        // 处理联系人和本地缓存关系
        private void handleContact() {
            long start = System.currentTimeMillis();
            int index = 0, max = nameList.size();
            publishProgress(1, index, max);
            try {
                ArrayList<Contact> save = new ArrayList<>();
                for (String[] strings : nameList) {
                    String name = strings[0];
                    String phone = strings[1];
                    // contact 主键id为 phone 的 md5 值
                    Contact contact = new Contact();
                    contact.setName(name);
                    contact.setPhone(phone);
                    contact.setMember(isMemberExists(contact.getPhone()));
                    contact.setInvited(false);
                    save.add(contact);
                    index++;
                    //log(format("handle progress, index: %d, name: %s, phone: %s", index, name, phone));
                    publishProgress(1, index, max);
                }
                App.app().getContacts().clear();
                App.app().getContacts().addAll(save);
                Collections.sort(App.app().getContacts(), new Comparator<Contact>() {
                    @Override
                    public int compare(Contact o1, Contact o2) {
                        String first1 = o1.getSpell().split(" ")[0];
                        String first2 = o2.getSpell().split(" ")[0];
                        int compare = first1.compareTo(first2);
                        if (compare == 0) {
                            return o1.getName().compareTo(o2.getName());
                        }
                        return compare;
                    }
                });
                //if (!RANDOM) {
                // 不是随机生成的人名才保存
                ContactService.start(true);
                //}
            } catch (Exception ignore) {
                ignore.printStackTrace();
            }
            long end = System.currentTimeMillis();
            log(format("handing contact(%d) cost: %d milliseconds.", max, end - start));
        }
    }
}
