package com.leadcom.android.isp.model.common;

import com.leadcom.android.isp.model.Model;

/**
 * <b>功能描述：</b>首页推荐的图片<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/06/03 23:24 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/06/03 23:24 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class FocusImage extends Model {
    //播放间隔
    private String playerInterval;
    //播放帧数
    private String playerFrames;
    private String createTime;
    //图片地址
    private String imageUrl;
    //链接
    private String targetPath;
    //标题
    private String title;
    private String createrId;
    private String createrName;
    private String type;
    private int status;

    public String getPlayerInterval() {
        return playerInterval;
    }

    public void setPlayerInterval(String playerInterval) {
        this.playerInterval = playerInterval;
    }

    public String getPlayerFrames() {
        return playerFrames;
    }

    public void setPlayerFrames(String playerFrames) {
        this.playerFrames = playerFrames;
    }

    public String getCreateTime() {
        return createTime;
    }

    public void setCreateTime(String createTime) {
        this.createTime = createTime;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getTargetPath() {
        return targetPath;
    }

    public void setTargetPath(String targetPath) {
        this.targetPath = targetPath;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCreaterId() {
        return createrId;
    }

    public void setCreaterId(String createrId) {
        this.createrId = createrId;
    }

    public String getCreaterName() {
        return createrName;
    }

    public void setCreaterName(String createrName) {
        this.createrName = createrName;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }
}
