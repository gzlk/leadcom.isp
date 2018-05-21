package com.leadcom.android.isp.model.archive;

import java.util.ArrayList;

/**
 * <b>功能描述：</b>单个档案查询返回结果<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2018/05/20 20:29 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>version: 1.0.0 <br />
 * <b>修改时间：</b>2017/10/04 18:50 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */
public class ArchiveQuery {

    private Archive groDoc, userDoc, docDraft;
    private ArrayList<ArchiveLike> groDocLikeList, userDocLike;
    private ArrayList<Comment> groDocCmtList, userDocComment, docDraftCmtList;
    private ArchiveInfo additionResult;

    public Archive getGroDoc() {
        return groDoc;
    }

    public void setGroDoc(Archive groDoc) {
        this.groDoc = groDoc;
    }

    public Archive getUserDoc() {
        return userDoc;
    }

    public void setUserDoc(Archive userDoc) {
        this.userDoc = userDoc;
    }

    public Archive getDocDraft() {
        return docDraft;
    }

    public void setDocDraft(Archive docDraft) {
        this.docDraft = docDraft;
    }

    public ArrayList<ArchiveLike> getGroDocLikeList() {
        return groDocLikeList;
    }

    public void setGroDocLikeList(ArrayList<ArchiveLike> groDocLikeList) {
        this.groDocLikeList = groDocLikeList;
    }

    public ArrayList<ArchiveLike> getUserDocLike() {
        return userDocLike;
    }

    public void setUserDocLike(ArrayList<ArchiveLike> userDocLike) {
        this.userDocLike = userDocLike;
    }

    public ArrayList<Comment> getGroDocCmtList() {
        return groDocCmtList;
    }

    public void setGroDocCmtList(ArrayList<Comment> groDocCmtList) {
        this.groDocCmtList = groDocCmtList;
    }

    public ArrayList<Comment> getUserDocComment() {
        return userDocComment;
    }

    public void setUserDocComment(ArrayList<Comment> userDocComment) {
        this.userDocComment = userDocComment;
    }

    public ArrayList<Comment> getDocDraftCmtList() {
        return docDraftCmtList;
    }

    public void setDocDraftCmtList(ArrayList<Comment> docDraftCmtList) {
        this.docDraftCmtList = docDraftCmtList;
    }

    public ArchiveInfo getAdditionResult() {
        return additionResult;
    }

    public void setAdditionResult(ArchiveInfo additionResult) {
        this.additionResult = additionResult;
    }
}
