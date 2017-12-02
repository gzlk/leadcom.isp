package com.leadcom.android.isp.fragment.organization.archive;

import android.support.annotation.Nullable;
import android.view.View;

import com.leadcom.android.isp.R;
import com.leadcom.android.isp.adapter.RecyclerViewAdapter;
import com.leadcom.android.isp.fragment.base.BaseFragment;
import com.leadcom.android.isp.holder.archive.ArchiveManagementViewHolder;
import com.leadcom.android.isp.listener.OnViewHolderClickListener;
import com.leadcom.android.isp.model.Model;
import com.leadcom.android.isp.model.activity.ActArchive;
import com.leadcom.android.isp.model.archive.Archive;

import java.lang.ref.SoftReference;

/**
 * <b>功能描述：</b>组织档案审核的适配器，都是同一个<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/05/25 02:24 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/05/25 02:24 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class ArchiveAdapter extends RecyclerViewAdapter<ArchiveManagementViewHolder, Model> {

    private SoftReference<BaseFragment> fragment;

    public ArchiveAdapter(BaseFragment fragment) {
        super();
        this.fragment = new SoftReference<>(fragment);
    }

    private OnViewHolderClickListener onViewHolderClickListener;

    public void setOnViewHolderClickListener(OnViewHolderClickListener l) {
        onViewHolderClickListener = l;
    }

    private String searchingText = "";

    public void setSearchingText(String text) {
        searchingText = text;
    }

    @Override
    public ArchiveManagementViewHolder onCreateViewHolder(View itemView, int viewType) {
        ArchiveManagementViewHolder holder = new ArchiveManagementViewHolder(itemView, fragment.get());
        holder.addOnViewHolderClickListener(onViewHolderClickListener);
        return holder;
    }

    @Override
    public int itemLayout(int viewType) {
        return R.layout.holder_view_archive_management;
    }

    @Override
    public void onBindHolderOfView(ArchiveManagementViewHolder holder, int position, @Nullable Model item) {
        if (item instanceof Archive) {
            holder.showContent((Archive) item, searchingText);
        } else if (item instanceof ActArchive) {
            holder.showContent((ActArchive) item, searchingText);
        }
    }

    @Override
    protected int comparator(Model item1, Model item2) {
        return 0;
    }
}
