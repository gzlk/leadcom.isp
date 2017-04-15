package com.gzlk.android.isp.model;

/**
 * <b>功能描述：</b><br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/04/15 20:22 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/04/15 20:22 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class Test {
    // {"name":"测试啦","id":"58ba5a4037db4820dcb67c7f","content":"我是测试内容","serNum":21}
    private String name;
    private String id;
    private String content;
    private int serNum;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public int getSerNum() {
        return serNum;
    }

    public void setSerNum(int serNum) {
        this.serNum = serNum;
    }
}
