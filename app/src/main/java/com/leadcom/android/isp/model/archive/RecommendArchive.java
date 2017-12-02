package com.leadcom.android.isp.model.archive;

import com.leadcom.android.isp.model.Model;
import com.leadcom.android.isp.model.organization.Organization;
import com.leadcom.android.isp.model.user.User;
import com.litesuits.orm.db.annotation.Column;
import com.litesuits.orm.db.annotation.Ignore;
import com.litesuits.orm.db.annotation.Table;

/**
 * <b>功能描述：</b>推荐档案<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/10/17 09:52 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/10/17 09:52 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */
@Table(Archive.Table.RECOMMEND_ARCHIVE)
public class RecommendArchive extends Model {

    /**
     * 推荐的档案类型
     */
    public interface RecommendType {
        /**
         * 组织档案
         */
        int GROUP = 1;
        /**
         * 个人档案
         */
        int USER = 2;
    }

    /**
     * 档案推荐状态
     */
    public interface RecommendStatus {
        /**
         * 未推荐
         */
        int UN_RECOMMEND = 0;
        /**
         * 已推荐
         */
        int RECOMMENDED = 1;
    }

    /**
     * 档案的推荐审核状态
     */
    public interface Check {
        /**
         * 推荐档案未通过审核
         */
        int UNCHECKED = 0;
        /**
         * 推荐档案已通过审核
         */
        int CHECKED = 1;
    }

    /**
     * 推荐是否审核
     */
    public interface Handle {
        /**
         * 未审核
         */
        int UNHANDLED = 0;
        /**
         * 已审核
         */
        int HANDLED = 1;
    }

    @Column(Archive.Field.Type)
    private int type;                   //档案类型(1.组织档案,2.个人档案)
    @Column(Organization.Field.GroupId)
    private String groupId;             //组织ID
    @Column(Organization.Field.GroupName)
    private String groupName;           //组织名称
    @Column(Field.UserId)
    private String userId;              //档案作者的用户ID
    @Column(Field.UserName)
    private String userName;            //档案作者的用户名称
    @Column(User.Field.HeadPhoto)
    private String headPhoto;           //档案作者的用户头像
    @Column(Archive.Field.ReferrerId)
    private String referrerId;          //推荐人的用户ID
    @Column(Archive.Field.ReferrerName)
    private String referrerName;        //推荐人的用户名称
    @Column(Archive.Field.ReferrerHeadPhoto)
    private String referrerHeadPhoto;   //推荐人的用户头像
    @Column(Archive.Field.ArchiveId)
    private String docId;               //档案ID
    @Column(Field.CreateDate)
    private String createDate;          //创建时间
    @Ignore
    private Archive userDoc;            //个人档案
    @Ignore
    private Archive groDoc;             //组织档案
    @Column(Archive.Field.Recommend)
    private int recommend;              //当前组织是否推荐(0.未推荐,1.已推荐)
    @Column(Archive.Field.Check)
    private int check;                  // 审核是否通过(0.未通过,1.已通过)
    @Column(Archive.Field.Sort)
    private int sort;                   // 排序(数值型)
    @Column(Archive.Field.Handle)
    private int handle;                 // 推荐是否审核(0.未审核,1.已审核)

    public Archive getDoc() {
        return null == userDoc ? groDoc : userDoc;
    }

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getGroupName() {
        return groupName;
    }

    public void setGroupName(String groupName) {
        this.groupName = groupName;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getHeadPhoto() {
        return headPhoto;
    }

    public void setHeadPhoto(String headPhoto) {
        this.headPhoto = headPhoto;
    }

    public String getReferrerId() {
        return referrerId;
    }

    public void setReferrerId(String referrerId) {
        this.referrerId = referrerId;
    }

    public String getReferrerName() {
        return referrerName;
    }

    public void setReferrerName(String referrerName) {
        this.referrerName = referrerName;
    }

    public String getReferrerHeadPhoto() {
        return referrerHeadPhoto;
    }

    public void setReferrerHeadPhoto(String referrerHeadPhoto) {
        this.referrerHeadPhoto = referrerHeadPhoto;
    }

    public String getDocId() {
        return docId;
    }

    public void setDocId(String docId) {
        this.docId = docId;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public Archive getUserDoc() {
        if (null == userDoc) {
            if (type == RecommendType.USER) {
                userDoc = Archive.get(docId);
            }
        }
        return userDoc;
    }

    public void setUserDoc(Archive userDoc) {
        this.userDoc = userDoc;
    }

    public Archive getGroDoc() {
        if (null == groDoc) {
            if (type == RecommendType.GROUP) {
                groDoc = Archive.get(docId);
            }
        }
        return groDoc;
    }

    public void setGroDoc(Archive groDoc) {
        this.groDoc = groDoc;
    }

    public int getRecommend() {
        return recommend;
    }

    public void setRecommend(int recommend) {
        this.recommend = recommend;
    }

    public boolean isRecommended() {
        return recommend == RecommendStatus.RECOMMENDED;
    }

    public int getCheck() {
        return check;
    }

    public void setCheck(int check) {
        this.check = check;
    }

    public boolean isCheck() {
        return check == Check.CHECKED;
    }

    public int getSort() {
        return sort;
    }

    public void setSort(int sort) {
        this.sort = sort;
    }

    public int getHandle() {
        return handle;
    }

    public void setHandle(int handle) {
        this.handle = handle;
    }

    public boolean isHandle() {
        return handle == Handle.HANDLED;
    }
}
