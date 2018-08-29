package com.leadcom.android.isp.model.user;

import com.google.gson.reflect.TypeToken;
import com.leadcom.android.isp.lib.Json;

import java.util.ArrayList;

/**
 * <b>功能描述：</b>常用中文名单字<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2018/08/29 13:29 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2018/08/29 13:29 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */
public class Word {

    public static ArrayList<Word> fromJson(String json) {
        return Json.gson().fromJson(json, new TypeToken<ArrayList<Word>>() {
        }.getType());
    }

    private int id;
    private String word;
    private int hot;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public int getHot() {
        return hot;
    }

    public void setHot(int hot) {
        this.hot = hot;
    }
}
