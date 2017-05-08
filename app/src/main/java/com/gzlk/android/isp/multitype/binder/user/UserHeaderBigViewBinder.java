package com.gzlk.android.isp.multitype.binder.user;

import android.support.annotation.NonNull;
import android.view.View;

import com.gzlk.android.isp.R;
import com.gzlk.android.isp.holder.UserHeaderBigViewHolder;
import com.gzlk.android.isp.listener.OnViewHolderClickListener;
import com.gzlk.android.isp.model.user.User;
import com.gzlk.android.isp.multitype.binder.BaseViewBinder;

import java.lang.ref.SoftReference;

/**
 * <b>功能描述：</b><br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/05/02 08:59 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/05/02 08:59 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class UserHeaderBigViewBinder extends BaseViewBinder<User, UserHeaderBigViewHolder> {

    private SoftReference<OnViewHolderClickListener> click;

    public UserHeaderBigViewBinder(OnViewHolderClickListener l) {
        super();
        click = new SoftReference<>(l);
    }

    @Override
    protected int itemLayout() {
        return R.layout.holder_view_individual_header_big;
    }

    @Override
    public UserHeaderBigViewHolder onCreateViewHolder(@NonNull View itemView) {
        UserHeaderBigViewHolder holder = new UserHeaderBigViewHolder(itemView, fragment.get());
        holder.addOnViewHolderClickListener(click.get());
        return holder;
    }

    @Override
    protected void onBindViewHolder(@NonNull UserHeaderBigViewHolder holder, @NonNull User item) {
        holder.showContent(item);
    }
}
