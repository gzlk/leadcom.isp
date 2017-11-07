package com.gzlk.android.isp.model.activity;

import com.gzlk.android.isp.model.Dao;
import com.gzlk.android.isp.model.Model;
import com.gzlk.android.isp.model.organization.Organization;
import com.litesuits.orm.db.annotation.Column;
import com.litesuits.orm.db.annotation.Table;
import com.litesuits.orm.db.assit.QueryBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * <b>功能描述：</b>活动的标签<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/05/25 22:42 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/05/25 22:42 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */
@Table(Activity.Table.LABEL)
public class Label extends Model {

    public static List<Label> getLabelsByActivityId(String activityId) {
        return new Dao<>(Label.class).query(Activity.Field.ActivityId, activityId);
    }

    public static List<Label> getLabelsById(List<String> ids) {
        if (null != ids && ids.size() > 0) {
            return new Dao<>(Label.class).in(Model.Field.Id, ids);
        }
        return new ArrayList<>();
    }

    public static Label getLabel(String labelName) {
        return new Dao<>(Label.class).querySingle(Field.Name, labelName);
    }

    /**
     * 查找本地保存的自定义标签列表(使用次数最多的10个)
     */
    public static List<Label> getLocal() {
        QueryBuilder<Label> builder = new QueryBuilder<>(Label.class)
                .whereEquals(Activity.Field.IsLocalStorage, true)
                .appendOrderDescBy(Activity.Field.UsedTimes).limit(0, 10);
        return new Dao<>(Label.class).query(builder);
    }

    public static void save(Label label) {
        new Dao<>(Label.class).save(label);
    }

    public static ArrayList<String> getLabelNames(ArrayList<String> labels) {
        ArrayList<String> list = new ArrayList<>();
        if (null != labels && labels.size() > 0) {
            for (String label : labels) {
                if (!list.contains(label)) {
                    list.add(label);
                }
            }
        }
        return list;
    }

    /**
     * 获取指定标签的说明文字（如xxx、xxx等x个标签）
     */
    public static String getLabelDesc(ArrayList<String> labels) {
        String ret = "";
        for (String name : labels) {
            Label label = Label.getLabel(name);
            if (null != label && !isEmpty(label.getName())) {
                ret += (isEmpty(ret) ? "" : "、") + label.getName();
            }
        }
        if (labels.size() < 1) {
            ret = "请选择标签";
        } else {
            ret += format("共%d个标签", labels.size());
        }
        return ret;
    }

    //名称
    @Column(Model.Field.Name)
    private String name;
    //组织id
    @Column(Organization.Field.GroupId)
    private String groupId;
    //活动id
    @Column(Activity.Field.ActivityId)
    private String actId;
    //是否本地保存的标签
    @Column(Activity.Field.IsLocalStorage)
    private boolean isLocal;
    // 使用次数
    @Column(Activity.Field.UsedTimes)
    private int signaNum;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getActId() {
        return actId;
    }

    public void setActId(String actId) {
        this.actId = actId;
    }

    public int getSignaNum() {
        return signaNum;
    }

    public void setSignaNum(int signaNum) {
        this.signaNum = signaNum;
    }

    public boolean isLocal() {
        return isLocal;
    }

    public void setLocal(boolean local) {
        isLocal = local;
    }
}
