package space.wangjiang.sqlhelper;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.parser.CCJSqlParserUtil;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectBody;
import net.sf.jsqlparser.statement.select.SelectItem;
import space.wangjiang.sqlhelper.util.StringUtil;

import java.util.ArrayList;
import java.util.List;

public class SqlHelper {

    private static final String SPACE = " ";

    /**
     * 解析SQL
     */
    public List<FieldItem> analysisSql(String sql, CommentPosition position) throws JSQLParserException {
        SelectBody selectBody = ((Select) CCJSqlParserUtil.parse(sql)).getSelectBody();
        List<SelectItem> selectItems = ((PlainSelect) selectBody).getSelectItems();
        List<FieldItem> fieldItems = new ArrayList<>();
        for (SelectItem selectItem : selectItems) {
            // 找到了 字段
            String fieldName = selectItem.toString();
            FieldItem fieldItem = new FieldItem();
            fieldItem.setName(getActualName(fieldName));
            fieldItem.setOriginName(fieldName);
            fieldItem.setComment(getComment(sql, fieldName, position));
            fieldItems.add(fieldItem);
        }
        return fieldItems;
    }

    /**
     * 获取执行后的实际字段名(即如果有别名的话，获取别名)
     * USER.USER_ID 会被解析为 USER_ID
     */
    private String getActualName(String fieldName) {
        // SQL 字段只会有三种情况
        // USER_ID
        // USER_ID AS userId
        // USER_ID userId
        String actualField = fieldName;
        if (fieldName.contains(SPACE)) {
            String[] array = fieldName.split(SPACE);
            actualField = array[array.length - 1];
        }
        // 如果存在表名则去除，即USER.USER_ID
        if (!actualField.contains(".")) {
            return actualField.trim();
        }
        String[] array = actualField.split("\\.");
        return array[array.length - 1].trim();
    }

    /**
     * 获取字段注释
     * <pre>
     * 一般情况下，注释在SQL的位置只有三种
     * 1.字段上面
     *  -- 用户ID
     *  user_id,
     *
     * 2.字段后面
     *  user_id, -- 用户ID
     *
     * 3.字段下面
     *  user_id,
     *  -- 用户ID
     *
     * 或者
     *  user_id
     *  -- 用户ID
     *  ,
     * </pre>
     * 自动处理较为复杂，很难将注释和字段对应起来
     * 但是一般编写SQL，注释位置往往是统一的，要不都是在上面，或者都是下面
     *
     * @param selectComment SELECT语句
     */
    private String getComment(String selectComment, String fieldName, CommentPosition position) {
        String[] array = selectComment.split("\n");
        // 注释所在的行号
        int index = -1;
        switch (position) {
            case TOP:
                // 找到上一行
                index = findLineIndex(selectComment, fieldName) - 1;
                break;
            case BEHIND:
                // 同一行
                index = findLineIndex(selectComment, fieldName);
                break;
            case BELOW:
                // 找到下一行
                index = findLineIndex(selectComment, fieldName) + 1;
                break;
        }
        return getCleanComment(array[index]);
    }

    /**
     * 获取干净的注释，去除掉 -- 或者 #
     */
    private String getCleanComment(String comment) {
        // 传入的comment可能和字段在一起，即 user_id. -- 用户ID
        // 注释内容可能是 -- XX#XX ，这样的注释解析可能会有问题，但是目前先简单处理
        if (comment.contains("#")) {
            String[] array = comment.split("#");
            comment = array[array.length - 1];
        } else if (comment.contains("--")) {
            String[] array = comment.split("--");
            comment = array[array.length - 1];
        } else {
            // 没有 -- 或 # ，不是注释行
            return "";
        }
        return comment.trim();
    }

    /**
     * 获取target在sql中的行号
     */
    private int findLineIndex(String sql, String target) {
        target = target.toUpperCase();
        String[] array = sql.split("\n");
        for (int i = 0; i < array.length; i++) {
            String line = array[i].toUpperCase();
            if (line.contains(target)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 将SQL字段转化为对应的Java代码
     */
    public String getVOCode(FieldItem fieldItem) {
        String template =
                "/**\n" +
                " * %s\n" +
                " */\n" +
                "private String %s;\n";
        // 转化为全小写
        String name = fieldItem.getName().toLowerCase();
        return String.format(template, fieldItem.getComment(), StringUtil.lowerCamelCase(name));
    }

}
