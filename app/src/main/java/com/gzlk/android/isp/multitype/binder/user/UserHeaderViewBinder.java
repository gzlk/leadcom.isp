package com.gzlk.android.isp.multitype.binder.user;

import android.support.annotation.NonNull;
import android.view.View;

import com.gzlk.android.isp.R;
import com.gzlk.android.isp.holder.individual.IndividualHeaderViewHolder;
import com.gzlk.android.isp.model.user.User;
import com.gzlk.android.isp.multitype.binder.BaseViewBinder;

/**
 * <b>功能描述：</b>用户头像<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/04/20 22:19 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/04/20 22:19 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class UserHeaderViewBinder extends BaseViewBinder<User, IndividualHeaderViewHolder> {

    @Override
    protected int itemLayout() {
        return R.layout.holder_view_individual_header;
    }

    @Override
    public IndividualHeaderViewHolder onCreateViewHolder(@NonNull View itemView) {
        return new IndividualHeaderViewHolder(itemView, fragment.get());
    }

    @Override
    protected void onBindViewHolder(@NonNull IndividualHeaderViewHolder holder, @NonNull User item) {
        holder.showContent(item);
    }
}
