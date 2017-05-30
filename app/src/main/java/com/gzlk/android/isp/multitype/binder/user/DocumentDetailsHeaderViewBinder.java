package com.gzlk.android.isp.multitype.binder.user;

import android.support.annotation.NonNull;
import android.view.View;

import com.gzlk.android.isp.R;
import com.gzlk.android.isp.holder.archive.ArchiveDetailsHeaderViewHolder;
import com.gzlk.android.isp.model.archive.Archive;
import com.gzlk.android.isp.multitype.binder.BaseViewBinder;

/**
 * <b>功能描述：</b>档案详情页档案头部信息<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/04/27 08:22 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/04/27 08:22 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class DocumentDetailsHeaderViewBinder extends BaseViewBinder<Archive, ArchiveDetailsHeaderViewHolder> {

    @Override
    protected int itemLayout() {
        return R.layout.tool_view_document_details_header;
    }

    @Override
    public ArchiveDetailsHeaderViewHolder onCreateViewHolder(@NonNull View itemView) {
        return new ArchiveDetailsHeaderViewHolder(itemView, fragment.get());
    }

    @Override
    protected void onBindViewHolder(@NonNull ArchiveDetailsHeaderViewHolder holder, @NonNull Archive item) {
        holder.showContent(item);
    }
}
