package space.wangjiang.sqlhelper.util;

/**
 * 字符串工具类
 */
public class StringUtil {

    public static String firstCharToLowerCase(String str) {
        char firstChar = str.charAt(0);
        if (firstChar >= 'A' && firstChar <= 'Z') {
            char[] arr = str.toCharArray();
            arr[0] = (char) (arr[0] + 32);
            return new String(arr);
        } else {
            return str;
        }
    }

    public static String firstCharToUpperCase(String str) {
        char firstChar = str.charAt(0);
        if (firstChar >= 'a' && firstChar <= 'z') {
            char[] arr = str.toCharArray();
            arr[0] = (char) (arr[0] - 32);
            return new String(arr);
        } else {
            return str;
        }
    }

    /**
     * 驼峰小写法
     * StrName --> strName
     * str_name --> strName
     */
    public static String lowerCamelCase(String name) {
        if (name == null) {
            return null;
        }
        return firstCharToLowerCase(upperCamelCase(name));
    }

    /**
     * 驼峰大写法
     * str_name --> StrName
     */
    public static String upperCamelCase(String name) {
        if (name == null) {
            return null;
        }
        name = firstCharToUpperCase(name);
        char[] array = name.toCharArray();
        for (int i = 0; i < array.length; i++) {
            //发现_，将后面的字符改为大写
            if (array[i] == '_' && i < array.length - 1) {
                array[i + 1] = Character.toUpperCase(array[i + 1]);
            }
        }
        String upperCamelCaseName = new String(array);
        return upperCamelCaseName.replace("_", "");
    }

    public static boolean isBlank(CharSequence cs) {
        int strLen;
        if (cs != null && (strLen = cs.length()) != 0) {
            for (int i = 0; i < strLen; ++i) {
                if (!Character.isWhitespace(cs.charAt(i))) {
                    return false;
                }
            }
        }
        return true;
    }

    public static boolean isNotBlank(CharSequence cs) {
        return !isBlank(cs);
    }

    public static boolean isEmpty(CharSequence cs) {
        return cs == null || cs.length() == 0;
    }

    public static boolean isNotEmpty(CharSequence cs) {
        return !isEmpty(cs);
    }

}
