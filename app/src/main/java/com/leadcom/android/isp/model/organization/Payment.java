package com.leadcom.android.isp.model.organization;

import com.leadcom.android.isp.model.Model;
import com.leadcom.android.isp.model.common.Attachment;

import java.util.ArrayList;

/**
 * <b>功能描述：</b>记账记录<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2018/12/24 21:30 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2018/12/24 21:30  <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */
public class Payment extends Model {

    private double payAmount, totalPayAmount;
    private String remark;
    private String payDate;
    private String createDate;
    private String userId;
    private String userName;
    private String userHeadPhoto;
    private String createUserId;
    private String groupId;
    private String squadId;
    private ArrayList<Attachment> image;

    public double getPayAmount() {
        return payAmount;
    }

    public void setPayAmount(double payAmount) {
        this.payAmount = payAmount;
    }

    public double getTotalPayAmount() {
        return totalPayAmount;
    }

    public void setTotalPayAmount(double totalPayAmount) {
        this.totalPayAmount = totalPayAmount;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getPayDate() {
        return payDate;
    }

    public void setPayDate(String payDate) {
        this.payDate = payDate;
    }

    public String getCreateDate() {
        return createDate;
    }

    public void setCreateDate(String createDate) {
        this.createDate = createDate;
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

    public String getUserHeadPhoto() {
        return userHeadPhoto;
    }

    public void setUserHeadPhoto(String userHeadPhoto) {
        this.userHeadPhoto = userHeadPhoto;
    }

    public String getCreateUserId() {
        return createUserId;
    }

    public void setCreateUserId(String createUserId) {
        this.createUserId = createUserId;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getSquadId() {
        return squadId;
    }

    public void setSquadId(String squadId) {
        this.squadId = squadId;
    }

    public ArrayList<Attachment> getImage() {
        if (null == image) {
            image = new ArrayList<>();
        }
        return image;
    }

    public void setImage(ArrayList<Attachment> image) {
        this.image = image;
    }
}
