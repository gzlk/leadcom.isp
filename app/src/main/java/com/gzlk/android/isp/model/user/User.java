package com.gzlk.android.isp.model.user;

import android.text.TextUtils;

import com.gzlk.android.isp.etc.Utils;
import com.gzlk.android.isp.model.Model;
import com.litesuits.orm.db.annotation.Column;
import com.litesuits.orm.db.annotation.Table;

import java.util.ArrayList;

/**
 * <b>功能描述：</b>用户信息<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/04/14 20:15 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/04/14 20:15 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

@Table(User.Table.USER)
public class User extends Model {

    public static class Table {
        public static final String USER = "user";
        /**
         * 个人收藏表
         */
        public static final String COLLECTION = "collection";
        /**
         * 用户隐私设置
         */
        public static final String PRIVACY = "privacy";
    }

    /**
     * 表内字段名称
     */
    public static class Field {
        public static final String Sex = "sex";
        public static final String LoginId = "loginId";
        public static final String QQ = "qq";
        public static final String QQAccessToken = "qqAccessToken";
        public static final String Phone = "phone";
        public static final String Email = "email";
        public static final String Password = "password";
        public static final String Birthday = "birthday";
        public static final String IdentifyNumber = "identifyNumber";
        public static final String Company = "company";
        public static final String Duty = "duty";
        public static final String IsAuth = "isAuth";
        public static final String HeadPhoto = "headPhoto";
        public static final String LastModifiedDate = "lastModifiedDate";
        public static final String LastLoginDate = "lastLoginDate";
        public static final String Captcha = "captcha";
        public static final String Spell = "spell";
        public static final String Signature = "signature";
        public static final String JoinedActs = "joinedActs";
        public static final String CreatedActs = "createdActs";
    }

    //用户id(同时也是网易云的accid)
    //密码(同时也是网易云的token)
    @Column(Field.Password)
    private String password;
    //登录的Id
    @Column(Field.LoginId)
    private String loginId;
    //qq
    @Column(Field.QQ)
    private String qq;
    //qq授权指令，用于QQ登录
    @Column(Field.QQAccessToken)
    private String qqAccessToken;
    //头像
    @Column(Field.HeadPhoto)
    private String headPhoto;
    //用户姓名
    @Column(Model.Field.Name)
    private String name;
    //手机
    @Column(Field.Phone)
    private String phone;
    //邮箱
    @Column(Field.Email)
    private String email;
    //性别
    @Column(Field.Sex)
    private String sex;
    //生日
    @Column(Field.Birthday)
    private String birthday;
    //身份证
    @Column(Field.IdentifyNumber)
    private String idNum;
    //单位名称
    @Column(Field.Company)
    private String company;
    //职位名称
    @Column(Field.Duty)
    private String position;
    //是否实名认证
    @Column(Field.IsAuth)
    private boolean isAuth;
    //最后修改时间
    @Column(Field.LastModifiedDate)
    private String lastModifiedDate;
    //最后登录时间
    @Column(Field.LastLoginDate)
    private String lastLoginDate;
    //创建时间
    @Column(Model.Field.CreateDate)
    private String createDate;
    //验证码
    @Column(Field.Captcha)
    private String captcha;

    // 拼音
    @Column(Field.Spell)
    private String spell;
    //签名（比如黄埔区民盟主委）
    @Column(Field.Signature)
    private String signature;

    //加入的活动id列表，格式[id1,id2,id3]
    @Column(Field.JoinedActs)
    private ArrayList<String> joinedActs;
    //发起的活动id列表，格式[id1,id2,id3]
    @Column(Field.CreatedActs)
    private ArrayList<String> createdActs;

    public String getName() {
        if (isEmpty(name)) {
            name = NO_NAME;
        }
        return name;
    }

    public void setName(String name) {
        this.name = name;
        setSpell(Utils.transformPinyin(name));
    }

    public String getSex() {
        return sex;
    }

    public void setSex(String sex) {
        this.sex = sex;
    }

    public String getLoginId() {
        return loginId;
    }

    public void setLoginId(String loginId) {
        this.loginId = loginId;
    }

    public String getQq() {
        return qq;
    }

    public void setQq(String qq) {
        this.qq = qq;
    }

    public String getQqAccessToken() {
        return qqAccessToken;
    }

    public void setQqAccessToken(String qqAccessToken) {
        this.qqAccessToken = qqAccessToken;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getBirthday() {
        return birthday;
    }

    public void setBirthday(String birthday) {
        this.birthday = birthday;
    }

    public String getIdNum() {
        return idNum;
    }

    public void setIdNum(String idNum) {
        this.idNum = idNum;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public boolean isAuth() {
        return isAuth;
    }

    public void setAuth(boolean auth) {
        isAuth = auth;
    }

    public String getHeadPhoto() {
        return headPhoto;
    }

    public void setHeadPhoto(String headPhoto) {
        this.headPhoto = headPhoto;
    }

    public String getLastModifiedDate() {
        return lastModifiedDate;
    }

    public void setLastModifiedDate(String lastModifiedDate) {
        this.lastModifiedDate = lastModifiedDate;
    }

    public String getLastLoginDate() {
        return lastLoginDate;
    }

    public void setLastLoginDate(String lastLoginDate) {
        this.lastLoginDate = lastLoginDate;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
    }

    public String getCaptcha() {
        return captcha;
    }

    public void setCaptcha(String captcha) {
        this.captcha = captcha;
    }

    public String getSpell() {
        if (TextUtils.isEmpty(spell)) {
            spell = Utils.transformPinyin(name);
        }
        return spell;
    }

    public void setSpell(String spell) {
        if (TextUtils.isEmpty(spell)) {
            this.spell = Utils.transformPinyin(name);
        } else {
            this.spell = spell;
        }
    }

    public String getSignature() {
        return signature;
    }

    public void setSignature(String signature) {
        this.signature = signature;
    }

    public ArrayList<String> getJoinedActs() {
        return joinedActs;
    }

    public void setJoinedActs(ArrayList<String> joinedActs) {
        this.joinedActs = joinedActs;
    }

    public ArrayList<String> getCreatedActs() {
        return createdActs;
    }

    public void setCreatedActs(ArrayList<String> createdActs) {
        this.createdActs = createdActs;
    }
}
