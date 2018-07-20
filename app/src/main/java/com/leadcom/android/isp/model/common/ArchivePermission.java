package com.leadcom.android.isp.model.common;

import com.leadcom.android.isp.model.Model;


/**
 * <b>功能描述：</b>档案权限<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2018/05/26 22:24 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>version: 1.0.0 <br />
 * <b>修改时间：</b>2017/10/04 18:50 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */
public class ArchivePermission extends Model {

    private String docId;
    /*是否已推荐**/
    private boolean recommended;
    /*是否可以推荐**/
    private boolean recommendable;
    /*是否可以删除**/
    private boolean deletable;
    /*是否可以转发**/
    private boolean flowable;
    /*是否拥有公开档案的权限**/
    private boolean authorized;

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }

    public boolean isRecommended() {
        return recommended;
    }

    public void setRecommended(boolean recommended) {
        this.recommended = recommended;
    }

    public boolean isRecommendable() {
        return recommendable;
    }

    public void setRecommendable(boolean recommendable) {
        this.recommendable = recommendable;
    }

    public boolean isDeletable() {
        return deletable;
    }

    public void setDeletable(boolean deletable) {
        this.deletable = deletable;
    }

    public boolean isFlowable() {
        return flowable;
    }

    public void setFlowable(boolean flowable) {
        this.flowable = flowable;
    }

    public boolean isAuthorized() {
        return authorized;
    }

    public void setAuthorized(boolean authorized) {
        this.authorized = authorized;
    }
}
