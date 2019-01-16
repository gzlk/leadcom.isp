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

    public interface ETC {
        String AGREE = "_isAgree";
        String CREATOR = "_isCreator";
        String LAST = "_isLast";
    }

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
     * 支出单进行状态
     */
    public interface UnderwayState {
        /**
         * 待处理
         */
        int WAITING = 0;
        /**
         * 进行中，至少有一个人已经同意了
         */
        int PROCESSING = 1;
        /**
         * 被否决，至少有一个人否决了
         */
        int REJECTED = 2;
    }

    /**
     * 用户类别
     */
    public interface UserType {
        String NONE = "";
        String CERTIFICATION = "证明人";
        String APPROVER = "审批人";
        String RECEIVER = "经办人";
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
    private String createUserId, createUserName;
    private String groupId;
    private String squadId;

    // 支出相关属性
    private double expendAmount;
    private String expendFlowerId;
    private String expendDate;
    private String certifierId, approverId, receiverId, certifierName, approverName, receiverName;
    private int state, status, certifierState, approverState, receiverState;
    private String title;
    private int underwayState;

    // 统计相关属性
    private double totalExpendAmount, totalExpendAmountStr, usableAmount;

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

    /**
     * 是否是填单人
     */
    public boolean isCreator(String userId) {
        return !isEmpty(createUserId) && createUserId.equals(userId);
    }

    public String getCreateUserName() {
        return createUserName;
    }

    public void setCreateUserName(String createUserName) {
        this.createUserName = createUserName;
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
        } else if (userId.equals(certifierId)) {
            return certifierState <= State.NORMAL;
        } else if (userId.equals(approverId)) {
            return approverState <= State.NORMAL;
        } else if (userId.equals(receiverId)) {
            return receiverState <= State.NORMAL;
        }
        return false;
    }

    /**
     * 是否是最后一个操作者
     */
    public boolean isLastHandler(String userId) {
        if (isCertificator(userId)) {
            return approverState > State.NORMAL && receiverState > State.NORMAL;
        }
        if (isApprovor(userId)) {
            return certifierState > State.NORMAL && receiverState > State.NORMAL;
        }
        if (isReceiver(userId)) {
            return certifierState > State.NORMAL && approverState > State.NORMAL;
        }
        return false;
    }

    public String getUserType(String userId) {
        if (isEmpty(userId)) return UserType.NONE;
        if (userId.equals(certifierId)) return UserType.CERTIFICATION;
        if (userId.equals(approverId)) return UserType.APPROVER;
        if (userId.equals(receiverId)) return UserType.RECEIVER;
        return UserType.NONE;
    }

    /**
     * 是否是证明人
     */
    public boolean isCertificator(String userId) {
        return !isEmpty(userId) && userId.equals(certifierId);
    }

    /**
     * 是否是审核人
     */
    public boolean isApprovor(String userId) {
        return !isEmpty(userId) && userId.equals(approverId);
    }

    /**
     * 是否是收款人
     */
    public boolean isReceiver(String userId) {
        return !isEmpty(userId) && userId.equals(receiverId);
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

    public int getUnderwayState() {
        return underwayState;
    }

    public void setUnderwayState(int underwayState) {
        this.underwayState = underwayState;
    }

    public int getCertifierState() {
        return certifierState;
    }

    public void setCertifierState(int certifierState) {
        this.certifierState = certifierState;
    }

    /**
     * 是否已证明
     */
    public boolean isCertified() {
        return certifierState >= State.AGREE;
    }

    /**
     * 是否已拒绝证明
     */
    public boolean isRejectCertified() {
        return certifierState == State.REJECT;
    }

    public int getApproverState() {
        return approverState;
    }

    public void setApproverState(int approverState) {
        this.approverState = approverState;
    }

    /**
     * 是否已审批
     */
    public boolean isApproved() {
        return approverState >= State.AGREE;
    }

    /**
     * 是否已拒绝审批
     */
    public boolean isRejectApproved() {
        return approverState == State.REJECT;
    }

    public int getReceiverState() {
        return receiverState;
    }

    public void setReceiverState(int receiverState) {
        this.receiverState = receiverState;
    }

    /**
     * 是否已接受
     */
    public boolean isReceived() {
        return receiverState >= State.AGREE;
    }

    /**
     * 是否已拒绝接受
     */
    public boolean isRejectReceived() {
        return receiverState == State.REJECT;
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

    public double getTotalExpendAmountStr() {
        return totalExpendAmountStr;
    }

    public void setTotalExpendAmountStr(double totalExpendAmountStr) {
        this.totalExpendAmountStr = totalExpendAmountStr;
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
