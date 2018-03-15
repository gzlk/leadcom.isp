package com.leadcom.android.isp.nim.model.extension;

import com.leadcom.android.isp.nim.constant.SigningNotifyType;
import com.netease.nim.uikit.common.util.file.FileUtil;

import org.json.JSONObject;

/**
 * Created by zhoujianghua on 2015/7/8.
 */
public class StickerAttachment extends CustomAttachment {

    private final String KEY_CATALOG = "catalog";
    private final String KEY_CHARTLET = "chartlet";

    private String catalog;
    private String chartlet;

    public StickerAttachment() {
        super(SigningNotifyType.STICKER);
    }

    public StickerAttachment(String catalog, String emotion) {
        this();
        this.catalog = catalog;
        this.chartlet = FileUtil.getFileNameNoEx(emotion);
    }

    @Override
    protected void parseData(JSONObject data) {
        super.parseData(data);
        try {
            catalog = data.optString(KEY_CATALOG, "");
            chartlet = data.optString(KEY_CHARTLET, "");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected JSONObject packData() {
        JSONObject data = super.packData();
        try {
            data.put(KEY_CATALOG, catalog);
            data.put(KEY_CHARTLET, chartlet);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return data;
    }

    public void setCatalog(String catalog) {
        this.catalog = catalog;
    }

    public void setChartlet(String chartlet) {
        this.chartlet = chartlet;
    }

    public String getCatalog() {
        return catalog;
    }

    public String getChartlet() {
        return chartlet;
    }
}
