package com.leadcom.android.isp.model.archive;

import com.leadcom.android.isp.model.Dao;
import com.leadcom.android.isp.model.Model;
import com.litesuits.orm.db.annotation.Column;
import com.litesuits.orm.db.annotation.Table;

import java.util.List;

/**
 * <b>功能描述：</b>档案类别<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2018/01/27 15:59 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>version: 1.0.0 <br />
 * <b>修改时间：</b>2017/10/04 18:50 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */
@Table(Archive.Table.ARCHIVE_CATEGORY)
public class Dictionary extends Model {

    public static void save(Dictionary category) {
        new Dao<>(Dictionary.class).save(category);
    }

    public static void save(List<Dictionary> list) {
        new Dao<>(Dictionary.class).save(list);
    }

    /**
     * 根据类型code查询
     */
    public static List<Dictionary> get(String typeCode) {
        return new Dao<>(Dictionary.class).query(Archive.Field.TypeCode, typeCode);
    }

    /**
     * 获取全部本地缓存的类别
     */
    public static List<Dictionary> getAll() {
        return new Dao<>(Dictionary.class).query();
    }

    /**
     * 字典类型
     */
    public interface Type {
        /**
         * 档案性质
         */
        String ARCHIVE_NATURE = "archiveNature";
        /**
         * 档案类型
         */
        String ARCHIVE_TYPE = "archiveType";
    }

    @Column(Model.Field.Name)
    private String name;
    @Column(Archive.Field.TypeName)
    private String typeName;
    @Column(Archive.Field.Code)
    private int code;
    @Column(Archive.Field.Description)
    private String description;
    @Column(Archive.Field.ParentId)
    private String parentId;
    @Column(Archive.Field.TypeCode)
    private String typeCode;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTypeName() {
        return typeName;
    }

    public void setTypeName(String typeName) {
        this.typeName = typeName;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getParentId() {
        return parentId;
    }

    public void setParentId(String parentId) {
        this.parentId = parentId;
    }

    public String getTypeCode() {
        return typeCode;
    }

    public void setTypeCode(String typeCode) {
        this.typeCode = typeCode;
    }
}
