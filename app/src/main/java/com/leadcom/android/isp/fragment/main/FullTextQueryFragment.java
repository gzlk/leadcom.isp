package com.leadcom.android.isp.fragment.main;

import android.support.annotation.Nullable;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;

import com.google.gson.reflect.TypeToken;
import com.hlk.hlklib.lib.inject.Click;
import com.hlk.hlklib.lib.inject.ViewId;
import com.leadcom.android.isp.R;
import com.leadcom.android.isp.adapter.RecyclerViewAdapter;
import com.leadcom.android.isp.api.common.FullTextQueryRequest;
import com.leadcom.android.isp.api.listener.OnSingleRequestListener;
import com.leadcom.android.isp.application.App;
import com.leadcom.android.isp.fragment.archive.ArchiveDetailsWebViewFragment;
import com.leadcom.android.isp.fragment.base.BaseFragment;
import com.leadcom.android.isp.fragment.base.BaseSwipeRefreshSupportFragment;
import com.leadcom.android.isp.helper.StringHelper;
import com.leadcom.android.isp.holder.BaseViewHolder;
import com.leadcom.android.isp.holder.common.FullTextQueryViewHolder;
import com.leadcom.android.isp.holder.common.InputableSearchViewHolder;
import com.leadcom.android.isp.holder.common.TextViewHolder;
import com.leadcom.android.isp.lib.Json;
import com.leadcom.android.isp.listener.OnViewHolderClickListener;
import com.leadcom.android.isp.model.Model;
import com.leadcom.android.isp.model.activity.Activity;
import com.leadcom.android.isp.model.archive.Archive;
import com.leadcom.android.isp.model.common.SimpleClickableItem;
import com.leadcom.android.isp.model.organization.Organization;
import com.leadcom.android.isp.model.query.FullTextQuery;
import com.leadcom.android.isp.model.user.SimpleUser;
import com.leadcom.android.isp.nim.session.NimSessionHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

/**
 * <b>功能描述：</b>全文检索页面<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/08/11 15:25 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/08/11 15:25 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class FullTextQueryFragment extends BaseSwipeRefreshSupportFragment {

    public static void open(BaseFragment fragment) {
        fragment.openActivity(FullTextQueryFragment.class.getName(), "", false, false, true);
    }

    // view
    @ViewId(R.id.ui_main_query_input_container)
    private LinearLayout inputLayout;
    @ViewId(R.id.ui_main_query_searchable)
    private View searchableView;

    // holder
    private InputableSearchViewHolder inputableSearchViewHolder;

    private SearchAdapter mAdapter;
    private String[] items;

    @Override
    protected void onDelayRefreshComplete(@DelayType int type) {

    }

    @Override
    public void doingInResume() {
        tryPaddingContent(inputLayout, false);
        initializeAdapter();
    }

    @Override
    protected boolean shouldSetDefaultTitleEvents() {
        return false;
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
    public int getLayout() {
        return R.layout.fragment_main_query;
    }

    @Override
    protected String getLocalPageTag() {
        return null;
    }

    @Click({R.id.ui_main_query_cancel})
    private void elementClick(View view) {
        switch (view.getId()) {
            case R.id.ui_main_query_cancel:
                // 取消
                finish();
                break;
        }
    }

    private void loadingQuery(String keyword) {
        setNothingText(R.string.ui_full_text_query_nothing);
        displayLoading(true);
        displayNothing(false);
        FullTextQueryRequest.request().setOnSingleRequestListener(new OnSingleRequestListener<FullTextQuery>() {
            @Override
            public void onResponse(FullTextQuery fullTextQuery, boolean success, String message) {
                super.onResponse(fullTextQuery, success, message);
                mAdapter.clear();
                if (success) {
                    try {
                        JSONObject object = new JSONObject(message);
                        if (object.has("data")) {
                            JSONObject map = object.getJSONObject("data").getJSONObject("map");
                            if (null != map) {
                                JSONArray doc, act, usr, grp;
                                // 成员
                                if (map.has("user")) {
                                    usr = map.getJSONObject("user").getJSONArray("list");
                                    parseUsers(usr);
                                }
                                // 组织
                                if (map.has("group")) {
                                    grp = map.getJSONObject("group").getJSONArray("list");
                                    parseGroups(grp);
                                }
                                // 活动
                                if (map.has("activity")) {
                                    act = map.getJSONObject("activity").getJSONArray("list");
                                    parseActivity(act);
                                }
                                // 档案
                                if (map.has("doc")) {
                                    doc = map.getJSONObject("doc").getJSONArray("list");
                                    parseDocs(doc);
                                }
                            }
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                displayLoading(false);
                displayNothing(mAdapter.getItemCount() < 1);
            }
        }).search(keyword);
    }

    private void parseDocs(JSONArray doc) {
        String json = doc.toString();
        json = json.replace("\"type\":\"\"", "\"type\":\"0\"");
        ArrayList<Archive> archives = Json.gson().fromJson(json, new TypeToken<ArrayList<Archive>>() {
        }.getType());
        if (null != archives && archives.size() > 0) {
            SimpleClickableItem item = new SimpleClickableItem(items[3]);
            mAdapter.add(item);
            for (Archive archive : archives) {
                mAdapter.add(archive);
            }
        }
    }

    private void parseActivity(JSONArray act) {
        ArrayList<Activity> activities = Json.gson().fromJson(act.toString(), new TypeToken<ArrayList<Activity>>() {
        }.getType());
        if (null != activities && activities.size() > 0) {
            SimpleClickableItem item = new SimpleClickableItem(items[2]);
            mAdapter.add(item);
            for (Activity activity : activities) {
                mAdapter.add(activity);
            }
        }
    }

    private void parseUsers(JSONArray usr) {
        ArrayList<SimpleUser> users = Json.gson().fromJson(usr.toString(), new TypeToken<ArrayList<SimpleUser>>() {
        }.getType());
        if (null != users && users.size() > 0) {
            SimpleClickableItem item = new SimpleClickableItem(items[0]);
            mAdapter.add(item);
            for (SimpleUser user : users) {
                mAdapter.add(user);
            }
        }
    }

    private void parseGroups(JSONArray grp) {
        ArrayList<Organization> groups = Json.gson().fromJson(grp.toString(), new TypeToken<ArrayList<Organization>>() {
        }.getType());
        if (null != groups && groups.size() > 0) {
            SimpleClickableItem item = new SimpleClickableItem(items[1]);
            mAdapter.add(item);
            for (Organization group : groups) {
                mAdapter.add(group);
            }
        }
    }

    private void initializeAdapter() {
        if (null == mAdapter) {
            setNothingText(R.string.ui_full_text_query_nothing_static);
            displayNothing(true);
            setLoadingText(R.string.ui_full_text_query_loading);
            mAdapter = new SearchAdapter();
            mRecyclerView.setAdapter(mAdapter);
        }
        if (null == items) {
            items = StringHelper.getStringArray(R.array.ui_full_text_query_base_items);
        }
        if (null == inputableSearchViewHolder) {
            inputableSearchViewHolder = new InputableSearchViewHolder(searchableView, this);
            inputableSearchViewHolder.setOnSearchingListener(onSearchingListener);
            inputableSearchViewHolder.setBackground(getColor(R.color.colorPrimary));
        }
    }

    private InputableSearchViewHolder.OnSearchingListener onSearchingListener = new InputableSearchViewHolder.OnSearchingListener() {
        @Override
        public void onSearching(String text) {
            if (!isEmpty(text)) {
                mQueryId = text;
                loadingQuery(mQueryId);
            }
        }
    };

    private OnViewHolderClickListener onViewHolderClickListener = new OnViewHolderClickListener() {
        @Override
        public void onClick(int index) {
            Model model = mAdapter.get(index);
            if (model instanceof SimpleUser) {
                App.openUserInfo(FullTextQueryFragment.this, ((SimpleUser) model).getUserId());
            } else if (model instanceof Organization) {
                GroupFragment.open(FullTextQueryFragment.this, model.getId());
            } else if (model instanceof Archive) {
                Archive archive = (Archive) model;
                // 档案类型：1个人档案，2组织档案(这里的档案类型不一样)
                int type = archive.getType() == 1 ? Archive.Type.USER : Archive.Type.GROUP;
                ArchiveDetailsWebViewFragment.open(FullTextQueryFragment.this, archive.getId(), type);
            } else if (model instanceof Activity) {
                NimSessionHelper.startTeamSession(Activity(), ((Activity) model).getTid());
            }
        }
    };

    private class SearchAdapter extends RecyclerViewAdapter<BaseViewHolder, Model> {

        private static final int VT_TITLE = 0, VT_CLICK = 1;

        @Override
        public BaseViewHolder onCreateViewHolder(View itemView, int viewType) {
            switch (viewType) {
                case VT_CLICK:
                    FullTextQueryViewHolder ftqvh = new FullTextQueryViewHolder(itemView, FullTextQueryFragment.this);
                    ftqvh.addOnViewHolderClickListener(onViewHolderClickListener);
                    return ftqvh;
                default:
                    TextViewHolder holder = new TextViewHolder(itemView, FullTextQueryFragment.this);
                    holder.setGravity(Gravity.START | Gravity.CENTER_VERTICAL);
                    return holder;
            }
        }

        @Override
        public int getItemViewType(int position) {
            Model model = get(position);
            if (model instanceof SimpleUser || model instanceof Organization ||
                    model instanceof Activity || model instanceof Archive) {
                return VT_CLICK;
            }
            return VT_TITLE;
        }

        @Override
        public int itemLayout(int viewType) {
            return viewType == VT_TITLE ? R.layout.holder_view_text_olny : R.layout.holder_view_full_text_query_item;
        }

        @Override
        public void onBindHolderOfView(BaseViewHolder holder, int position, @Nullable Model item) {
            if (holder instanceof FullTextQueryViewHolder) {
                ((FullTextQueryViewHolder) holder).setSearchingText(mQueryId);
                ((FullTextQueryViewHolder) holder).showContent(item);
            } else if (holder instanceof TextViewHolder) {
                ((TextViewHolder) holder).showContent(((SimpleClickableItem) item).getValue());
            }
        }

        @Override
        protected int comparator(Model item1, Model item2) {
            return 0;
        }
    }
}
