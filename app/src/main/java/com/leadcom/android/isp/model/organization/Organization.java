package com.leadcom.android.isp.model.organization;

import com.leadcom.android.isp.model.Dao;
import com.leadcom.android.isp.model.Model;
import com.leadcom.android.isp.model.common.Quantity;
import com.leadcom.android.isp.model.user.Collection;
import com.litesuits.orm.db.annotation.Column;
import com.litesuits.orm.db.annotation.Ignore;
import com.litesuits.orm.db.annotation.Table;

import java.util.ArrayList;

/**
 * <b>功能描述：</b>组织<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/05/07 08:56 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/05/07 08:56 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */
@Table(Organization.Table.GROUP)
public class Organization extends Model {

    /**
     * 其他组织的Id
     */
    public static final String OTHER_ID = "ad912b3b344f426a93860248ffd3c715";

    public interface Table {
        String GROUP = "organization";
        String SQUAD = "squad";
        String INVITATION = "invitation";
        String ROLE = "role";
        String RELATION = "relation";
        String PERMISSION = "permission";
        String MEMBER = "member";
        String CONCERN = "concern";
        String JOIN = "joinGroup";
    }

    public interface Field {
        String GroupId = "groupId";
        String GroupName = "groupName";
        String UpperId = "upperId";
        String UpperName = "upperName";
        String Nature = "nature";
        String Logo = "logo";
        String Introduction = "introduction";
        String MemberNumber = "memberNum";
        String Verified = "verified";
        String RoleId = "roleId";
        String RoleName = "roleName";
        String RoleDescription = "roleDescription";
        String SquadId = "squadId";
        String Rank = "rank";
        String SquadName = "squadName";
        String PermissionId = "permissionId";
        String GroupLogo = "groupLogo";
        String ModifyDate = "modifyDate";
        String Concerned = "concerned";
        String ShortName = "shortName";
        String GroupRoleId = "groupRoleId";
        String Level = "level";
    }

    /**
     * 组织资料授权状态
     */
    public interface AuthorizeType {
        /**
         * 已授权
         */
        int AUTHORIZED = 1;
        /**
         * 未授权
         */
        int NONE = 0;
    }

    /**
     * 组织性质
     */
    public interface NatureType {
        /**
         * 无任何性质
         */
        int NONE = 0;
        /**
         * 民盟系
         */
        int MINMENT = 1;
        /**
         * 统战系
         */
        int TONGZHAN = 2;
    }

    /**
     * 组织层级
     */
    public interface LevelType {
        /**
         * 基层
         */
        int BASE = 0;
        /**
         * 上级组织
         */
        int UPPER = 1;
    }

    public static Organization get(String id) {
        return new Dao<>(Organization.class).query(id);
    }

    @Column(Model.Field.Name)
    private String name;           //组织名
    @Column(Field.UpperId)
    private String upId;           //上级组织ID
    @Column(Field.UpperName)
    private String upName;         //上级组织名称
    @Column(Field.Nature)
    private int nature;         //组织性质
    @Column(Field.Logo)
    private String logo;           //组织LOGO
    @Column(Field.Introduction)
    private String intro;          //组织描述
    @Column(Model.Field.CreateDate)
    private String createDate;     //创建时间
    @Column(Field.ModifyDate)
    private String modifyDate;       //最后修改时间
    @Column(Model.Field.CreatorId)
    private String creatorId;      //创建者ID
    @Column(Model.Field.CreatorName)
    private String creatorName;    //创建者名称
    @Column(Collection.Field.CreatorHeadPhoto)
    private String creatorHeadPhoto;//创建者的用户头像
    @Column(Field.MemberNumber)
    private int memberNum;          // 成员数
    @Column(Field.Verified)
    private boolean verified;      //是否组织认证
    @Ignore
    private Member groMember;      // 当前登录者在组织里的角色
    @Column(Field.Concerned)
    private boolean concerned;     // 是否是我关注的组织
    private int concernTo;          // 关注方式 1.我关注的组织，2.关注我的组织
    @Column(Field.ShortName)
    private String shortName;                  //组织简称
    @Column(Field.Level)
    private String level;

    @Ignore
    private ArrayList<Concern> conGroup; // 关注的组织列表
    @Ignore
    private ArrayList<Member> groMemberList;//组织成员列表
    @Ignore
    private Quantity calculate;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUpId() {
        return upId;
    }

    public void setUpId(String upId) {
        this.upId = upId;
    }

    public String getUpName() {
        return upName;
    }

    public void setUpName(String upName) {
        this.upName = upName;
    }

    public int getNature() {
        return nature;
    }

    public void setNature(int nature) {
        this.nature = nature;
    }

    public boolean isMM() {
        return nature == NatureType.MINMENT;
    }

    public boolean isTZ() {
        return nature == NatureType.TONGZHAN;
    }

    public boolean isNoneNature() {
        return !isMM() && !isTZ();
    }

    public String getLogo() {
        return logo;
    }

    public void setLogo(String logo) {
        this.logo = logo;
    }

    public String getIntro() {
        return intro;
    }

    public void setIntro(String intro) {
        this.intro = intro;
    }

    public String getCreateDate() {
        if (isEmpty(createDate)) {
            createDate = DFT_DATE;
        }
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getModifyDate() {
        return modifyDate;
    }

    public void setModifyDate(String modifyDate) {
        this.modifyDate = modifyDate;
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

    public int getMemberNum() {
        return memberNum;
    }

    public void setMemberNum(int memberNum) {
        this.memberNum = memberNum;
    }

    public boolean isVerified() {
        return verified;
    }

    public void setVerified(boolean verified) {
        this.verified = verified;
    }

    public Member getGroMember() {
        return groMember;
    }

    public void setGroMember(Member groMember) {
        this.groMember = groMember;
    }

    public boolean isConcerned() {
        return concerned;
    }

    public void setConcerned(boolean concerned) {
        this.concerned = concerned;
    }

    public int getConcernTo() {
        return concernTo;
    }

    public void setConcernTo(int concernTo) {
        this.concernTo = concernTo;
    }

    public ArrayList<Concern> getConGroup() {
        return conGroup;
    }

    public void setConGroup(ArrayList<Concern> conGroup) {
        this.conGroup = conGroup;
    }

    public String getCreatorHeadPhoto() {
        return creatorHeadPhoto;
    }

    public void setCreatorHeadPhoto(String creatorHeadPhoto) {
        this.creatorHeadPhoto = creatorHeadPhoto;
    }

    public String getShortName() {
        return shortName;
    }

    public void setShortName(String shortName) {
        this.shortName = shortName;
    }

    public String getLevel() {
        return level;
    }

    public void setLevel(String level) {
        this.level = level;
    }

    public boolean isBaseLevel() {
        return !isEmpty(level) && Integer.valueOf(level) == LevelType.BASE;
    }

    public ArrayList<Member> getGroMemberList() {
        return groMemberList;
    }

    public void setGroMemberList(ArrayList<Member> groMemberList) {
        this.groMemberList = groMemberList;
    }

    public Quantity getCalculate() {
        return calculate;
    }

    public void setCalculate(Quantity calculate) {
        this.calculate = calculate;
    }
}
