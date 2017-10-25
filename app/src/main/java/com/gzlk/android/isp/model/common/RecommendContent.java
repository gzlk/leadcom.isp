package com.gzlk.android.isp.model.common;

import com.gzlk.android.isp.model.Model;
import com.gzlk.android.isp.model.archive.Archive;
import com.gzlk.android.isp.model.archive.RecommendArchive;

/**
 * <b>功能描述：</b>首页推荐的内容<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/06/03 23:21 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/06/03 23:21 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class RecommendContent extends Model {

    /**
     * 内容类型
     */
    public interface SourceType {
        /**
         * 活动
         */
        //int ACTIVITY = 1;
        /**
         * 档案
         */
        int ARCHIVE = 2;
        /**
         * 编辑展示位
         */
        int PRIORITY_PLACE = 1;
    }

    //被推荐的内容类型：1来自活动，2来自组织档案
    private int sourceType;
    //活动，当推荐的内容来自档案时此字段为空
    //private Activity activity;
    //组织档案，当推荐的内容来自活动时此字段为空
    private RecommendArchive groDocRcmd;
    //编辑展示位
    private PriorityPlace priorityPlace;
    //推荐时间
    private String createTime;

    public int getSourceType() {
        return sourceType;
    }

    public void setSourceType(int sourceType) {
        this.sourceType = sourceType;
    }

//    public Activity getActivity() {
//        return activity;
//    }
//
//    public void setActivity(Activity activity) {
//        this.activity = activity;
//    }

    public RecommendArchive getGroDocRcmd() {
        return groDocRcmd;
    }

    public void setGroDocRcmd(RecommendArchive groDocRcmd) {
        this.groDocRcmd = groDocRcmd;
    }

    public PriorityPlace getPriorityPlace() {
        return priorityPlace;
    }

    public void setPriorityPlace(PriorityPlace priorityPlace) {
        this.priorityPlace = priorityPlace;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }
}
