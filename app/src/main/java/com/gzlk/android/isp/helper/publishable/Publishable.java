package com.gzlk.android.isp.helper.publishable;

import com.gzlk.android.isp.model.archive.Archive;
import com.gzlk.android.isp.model.user.Moment;

/**
 * <b>功能描述：</b><br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/10/31 21:10 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>version: 1.0.0 <br />
 * <b>修改时间：</b>2017/10/31 21:10 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */
public abstract class Publishable {

    Archive mArchive;
    Moment mMoment;

    public abstract Publishable setArchive(Archive archive);

    public abstract Publishable setMoment(Moment moment);
}
