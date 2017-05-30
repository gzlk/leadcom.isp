package com.gzlk.android.isp.model.user;

import com.gzlk.android.isp.model.Model;
import com.gzlk.android.isp.model.archive.Archive;
import com.gzlk.android.isp.model.archive.ArchiveSource;
import com.hlk.hlklib.lib.inject.Click;
import com.litesuits.orm.db.annotation.Column;
import com.litesuits.orm.db.annotation.Ignore;
import com.litesuits.orm.db.annotation.Table;

/**
 * <b>功能描述：</b>个人收藏<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/04/20 00:18 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/04/20 00:18 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */
@Table(User.Table.COLLECTION)
public class Collection extends Model {

    public interface Field {
        String Module = "module";
        String ModuleId = "moduleId";
    }

    /**
     * 个人收藏类别
     */
    public interface Type {
        /**
         * 文本
         */
        int TEXT = 1;
        /**
         * 文档
         */
        int ARCHIVE = 2;
        /**
         * 图片
         */
        int IMAGE = 3;
        /**
         * 视频
         */
        int VIDEO = 4;
        /**
         * 附件
         */
        int ATTACHMENT = 5;
        /**
         * 连接
         */
        int LINK = 5;
    }

    /**
     * 收藏来源
     */
    public interface Module {
        /**
         * 个人档案
         */
        int INDIVIDUAL = 1;
        /**
         * 组织档案
         */
        int GROUP = 2;
        /**
         * 活动聊天
         */
        int ACTIVITY = 3;
        /**
         * 议题聊天
         */
        int DISCUSSION = 4;
        /**
         * 个人动态
         */
        int MOMENT = 5;
    }

    /**
     * 把source里的值取出来
     */
    public void compound() {
        if (null != source) {
            module = source.getModule();
            moduleId = source.getId();
        }
    }

    //收藏的类型(1->文本, 2->文档, 3-图片, 4->视频, 5->附件, 6->链接)
    @Column(Archive.Field.Type)
    private int type;
    //标签
    @Column(Archive.Field.Label)
    private String label;
    //来源(module:模块类型,id:模块ID)
    @Ignore
    private ArchiveSource source;
    //模块类型(1.个人档案,2.组织档案,3.活动聊天,4.议题聊天,5.个人动态)
    @Column(Field.Module)
    private int module;
    //模块ID(表的ID主键)
    @Column(Field.ModuleId)
    private String moduleId;
    //收藏的内容(文本,图片,语音,附件,链接)
    @Column(Archive.Field.Content)
    private String content;
    //收藏人ID
    @Column(Model.Field.UserId)
    private String userId;
    //原作者ID
    @Column(Archive.Field.CreatorId)
    private String creatorId;
    //原作者名称
    @Column(Archive.Field.CreatorName)
    private String creatorName;
    //创建日期
    @Column(Model.Field.CreateDate)
    private String createDate;
    //修改日期
    @Column(Archive.Field.LastModifiedDate)
    private String lastModifiedDate;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getLabel() {
        return label;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public ArchiveSource getSource() {
        return source;
    }

    public void setSource(ArchiveSource source) {
        this.source = source;
    }

    public int getModule() {
        return module;
    }

    public void setModule(int module) {
        this.module = module;
    }

    public String getModuleId() {
        return moduleId;
    }

    public void setModuleId(String moduleId) {
        this.moduleId = moduleId;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCreatorId() {
        return creatorId;
    }

    public void setCreatorId(String creatorId) {
        this.creatorId = creatorId;
    }

    public String getCreatorName() {
        return creatorName;
    }

    public void setCreatorName(String creatorName) {
        this.creatorName = creatorName;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(String lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }
}
