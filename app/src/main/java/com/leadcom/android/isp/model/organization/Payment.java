package com.leadcom.android.isp.model.organization;

import com.leadcom.android.isp.R;
import com.leadcom.android.isp.helper.StringHelper;
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

    /**
     * 财务类型
     */
    public interface Type {
        /**
         * 缴费
         */
        int PAYMENT = 1;
        /**
         * 支出
         */
        int EXPEND = 2;
        /**
         * 审核
         */
        int CHECK = 3;
    }

    /**
     * 状态
     */
    public interface State {
        /**
         * 未处理
         */
        int NORMAL = 0;
        /**
         * 已同意
         */
        int AGREE = 1;
        /**
         * 已拒绝
         */
        int REJECT = 2;
    }

    /**
     * 获取未处理状态的文字描述
     */
    public String getUnhandledStateText(int state) {
        switch (state) {
            case State.AGREE:
                return "同意";
            case State.NORMAL:
                return "不处理";
            case State.REJECT:
                return "拒绝";
        }
        return "";
    }

    private String getStateText(int state) {
        switch (state) {
            case State.AGREE:
                return "已同意";
            case State.NORMAL:
                return "未处理";
            case State.REJECT:
                return "已拒绝";
        }
        return "";
    }

    public String getColoredStateText(int state) {
        return format("<font color=\"#db7763\">(%s)</font>", getStateText(state));
    }

    private int type;
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

    // 支出相关属性
    private double expendAmount;
    private String expendFlowerId;
    private String expendDate;
    private String certifierId, approverId, receiverId, certifierName, approverName, receiverName;
    private int state, status, certifierState, approverState, receiverState;
    private String title;

    // 统计相关属性
    private double totalExpendAmount, usableAmount;

    private ArrayList<Attachment> image;

    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

    public boolean isPayment() {
        return type == Type.PAYMENT;
    }

    public boolean isExpend() {
        return type == Type.EXPEND;
    }

    public boolean isCheck() {
        return type == Type.CHECK;
    }

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

    public double getExpendAmount() {
        return expendAmount;
    }

    public void setExpendAmount(double expendAmount) {
        this.expendAmount = expendAmount;
    }

    public String getExpendFlowerId() {
        return expendFlowerId;
    }

    public void setExpendFlowerId(String expendFlowerId) {
        this.expendFlowerId = expendFlowerId;
    }

    public String getExpendDate() {
        return expendDate;
    }

    public void setExpendDate(String expendDate) {
        this.expendDate = expendDate;
    }

    public String getCertifierId() {
        return certifierId;
    }

    public void setCertifierId(String certifierId) {
        this.certifierId = certifierId;
    }

    public String getCertifierName() {
        return certifierName;
    }

    public void setCertifierName(String certifierName) {
        this.certifierName = certifierName;
    }

    public String getApproverId() {
        return approverId;
    }

    public void setApproverId(String approverId) {
        this.approverId = approverId;
    }

    public String getApproverName() {
        return approverName;
    }

    public void setApproverName(String approverName) {
        this.approverName = approverName;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getReceiverName() {
        return receiverName;
    }

    public void setReceiverName(String receiverName) {
        this.receiverName = receiverName;
    }

    /**
     * 指定用户是否可以更改支出申请的状态
     */
    public boolean isStateHandleable(String userId) {
        if (isEmpty(userId)) {
            return false;
        } else if (userId.equals(certifierId) || userId.equals(approverId) || userId.equals(receiverId)) {
            return true;
        }
        return false;
    }

    public int getState() {
        return state;
    }

    public void setState(int state) {
        this.state = state;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public int getCertifierState() {
        return certifierState;
    }

    public void setCertifierState(int certifierState) {
        this.certifierState = certifierState;
    }

    public int getApproverState() {
        return approverState;
    }

    public void setApproverState(int approverState) {
        this.approverState = approverState;
    }

    public int getReceiverState() {
        return receiverState;
    }

    public void setReceiverState(int receiverState) {
        this.receiverState = receiverState;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public double getTotalExpendAmount() {
        return totalExpendAmount;
    }

    public void setTotalExpendAmount(double totalExpendAmount) {
        this.totalExpendAmount = totalExpendAmount;
    }

    public double getUsableAmount() {
        return usableAmount;
    }

    public void setUsableAmount(double usableAmount) {
        this.usableAmount = usableAmount;
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
