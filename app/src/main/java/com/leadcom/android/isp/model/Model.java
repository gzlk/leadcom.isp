package com.leadcom.android.isp.model;

import com.leadcom.android.isp.R;
import com.leadcom.android.isp.helper.StringHelper;
import com.litesuits.orm.db.annotation.Column;
import com.litesuits.orm.db.annotation.Ignore;
import com.litesuits.orm.db.annotation.NotNull;
import com.litesuits.orm.db.annotation.PrimaryKey;
import com.litesuits.orm.db.enums.AssignType;

/**
 * <b>功能描述：</b>所有mode基类<br />
 * <b>创建作者：</b>Hsiang Leekwok <br />
 * <b>创建时间：</b>2017/04/19 12:55 <br />
 * <b>作者邮箱：</b>xiang.l.g@gmail.com <br />
 * <b>最新版本：</b>Version: 1.0.0 <br />
 * <b>修改时间：</b>2017/04/19 12:55 <br />
 * <b>修改人员：</b><br />
 * <b>修改备注：</b><br />
 */

public class Model extends BaseModel {

    /**
     * 字段名
     */
    public static class Field {
        public static final String Id = "id";
        public static final String PrimaryId = "_id";
        public static final String CreateDate = "createDate";
        public static final String Name = "name";
        public static final String UserId = "userId";
        public static final String UserName = "userName";
        public static final String CreatorId = "creatorId";
        public static final String CreatorName = "creatorName";
        public static final String UUID = "uuid";
        public static final String AccessToken = "accessToken";
        public static final String HasRead = "hasRead";
    }

    /**
     * 内容在UI上显示的折叠状态
     */
    public interface ExpandStatus {
        /**
         * 没有经过处理
         */
        int NONE = 0;
        /**
         * 无需折叠，行数没有超过预定值
         */
        int NOT_OVERFLOW = 1;
        /**
         * 折叠状态
         */
        int COLLAPSED = 2;
        /**
         * 全展开状态
         */
        int EXPANDED = 3;
    }

    public static final String DFT_DATE = StringHelper.getString(R.string.ui_base_text_date_default);
    public static final String NO_MORE_ID = StringHelper.getString(R.string.ui_base_text_nothing_more_id);
    public static final String EMPTY_JSON = "{}";
    public static final String EMPTY_ARRAY = "[]";
    protected static final String NO_NAME = StringHelper.getString(R.string.ui_base_text_no_name);

    protected static boolean isEmpty(String string) {
        return StringHelper.isEmpty(string);
    }

    protected static String format(String fmt, Object... args) {
        return StringHelper.format(fmt, args);
    }

    public static Model getNoMore() {
        return getNoMore("");
    }

    public static Model getNoMore(int noMoreText) {
        return getNoMore(StringHelper.getString(noMoreText));
    }

    public static Model getNoMore(String noMoreText) {
        Model model = new Model();
        model.setId(NO_MORE_ID);
        model.setAccessToken(isEmpty(noMoreText) ? StringHelper.getString(R.string.ui_base_text_nothing_more) : noMoreText);
        return model;
    }

    @PrimaryKey(AssignType.BY_MYSELF)
    @NotNull
    @Column(Field.Id)
    private String id;

    @Column(Model.Field.AccessToken)
    private String accessToken;        //用户令牌

    @Column(Field.HasRead)
    private boolean isRead;

    @Ignore
    private boolean localDeleted;
    @Ignore
    private boolean isSelectable;
    @Ignore
    private boolean isSelected;
    @Ignore
    private int collapseStatus;
    @Ignore
    private boolean singleSelectable;

    //@NotNull
    //@PrimaryKey(AssignType.BY_MYSELF)
    @Column(Field.PrimaryId)
    private String _id;

    /**
     * 保存事件
     */
    public void onSave() {
        if (!isEmpty(_id)) {
            id = _id;
        }
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getId() {
        if (isEmpty(id) || (!isEmpty(_id) && !id.equals(_id))) {
            id = _id;
        }
        return id;
    }

    public void setId(String id) {
        this.id = id;
        this._id = id;
    }

    public String getAccessToken() {
        return accessToken;
    }

    public void setAccessToken(String accessToken) {
        this.accessToken = accessToken;
    }

    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }

    /**
     * 本地是否已删除
     */
    public boolean isLocalDeleted() {
        return localDeleted;
    }

    /**
     * 本地是否已删除
     */
    public void setLocalDeleted(boolean localDeleted) {
        this.localDeleted = localDeleted;
    }

    /**
     * 是否可选择
     */
    public boolean isSelectable() {
        return isSelectable;
    }

    /**
     * 是否可选择
     */
    public void setSelectable(boolean selectable) {
        this.isSelectable = selectable;
    }

    /**
     * 是否已选中
     */
    public boolean isSelected() {
        return isSelected;
    }

    /**
     * 是否已选中
     */
    public void setSelected(boolean selected) {
        this.isSelected = selected;
    }

    public boolean isSingleSelectable() {
        return singleSelectable;
    }

    public void setSingleSelectable(boolean singleSelectable) {
        this.singleSelectable = singleSelectable;
    }

    @Override
    public boolean equals(Object object) {
        return null != object && getClass() == object.getClass() && object instanceof Model && equals((Model) object);
    }

    public boolean equals(Model model) {
        return null != model && !isEmpty(getId()) && !isEmpty(model.getId()) && model.getId().equals(getId());
    }

    public boolean isCollapsed() {
        return collapseStatus == ExpandStatus.COLLAPSED;
    }

    public boolean isExpanded() {
        return collapseStatus == ExpandStatus.EXPANDED;
    }

    public int getCollapseStatus() {
        return collapseStatus;
    }

    public void setCollapseStatus(int collapseStatus) {
        this.collapseStatus = collapseStatus;
    }
}
