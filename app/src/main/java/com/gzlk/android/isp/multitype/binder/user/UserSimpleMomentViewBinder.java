package com.gzlk.android.isp.multitype.binder.user;

import android.support.annotation.NonNull;
import android.view.View;

import com.gzlk.android.isp.R;
import com.gzlk.android.isp.holder.UserSimpleMomentViewHolder;
import com.gzlk.android.isp.model.SimpleClickableItem;
import com.gzlk.android.isp.multitype.binder.BaseViewBinder;

/**
 * <b>功能描述：</b><br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/05/08 13:34 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/05/08 13:34 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class UserSimpleMomentViewBinder extends BaseViewBinder<SimpleClickableItem, UserSimpleMomentViewHolder> {
    @Override
    protected int itemLayout() {
        return R.layout.holder_view_user_simple_moment;
    }

    @Override
    public UserSimpleMomentViewHolder onCreateViewHolder(@NonNull View itemView) {
        return new UserSimpleMomentViewHolder(itemView, fragment.get());
    }

    @Override
    protected void onBindViewHolder(@NonNull UserSimpleMomentViewHolder holder, @NonNull SimpleClickableItem item) {
        holder.showContent(item);
    }
}
