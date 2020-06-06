package space.wangjiang.sqlhelper;

/**
 * SQL的字段
 */
public class FieldItem {

    /**
     * 执行后的实际字段名，如果userId
     */
    private String name;

    /**
     * 原始字段，如USER.USER_ID AS userId
     */
    private String originName;

    /**
     * 字段注释
     */
    private String comment;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOriginName() {
        return originName;
    }

    public void setOriginName(String originName) {
        this.originName = originName;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    @Override
    public String toString() {
        return "FieldItem{" +
                "name='" + name + '\'' +
                ", originName='" + originName + '\'' +
                ", comment='" + comment + '\'' +
                '}';
    }
}
