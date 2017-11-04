package com.gzlk.android.isp.helper.publishable;

import com.gzlk.android.isp.model.Model;
import com.gzlk.android.isp.model.archive.Archive;
import com.gzlk.android.isp.model.archive.Comment;
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

    String getHostId() {
        if (null != mArchive) {
            return mArchive.getId();
        }
        if (null != mMoment) {
            return mMoment.getId();
        }
        return "";
    }

    int getMethodType() {
        if (null != mArchive) {
            return null == mArchive.getGroEntity() ? Comment.Type.USER : Comment.Type.GROUP;
        }
        return Comment.Type.MOMENT;
    }

    public Publishable setModel(Model model) {
        if (model instanceof Archive) {
            mArchive = (Archive) model;
        } else if (model instanceof Moment) {
            mMoment = (Moment) model;
        }
        return this;
    }

    public abstract Publishable setArchive(Archive archive);

    public abstract Publishable setMoment(Moment moment);
}
