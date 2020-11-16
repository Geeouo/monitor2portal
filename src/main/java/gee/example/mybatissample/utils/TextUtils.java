package gee.example.mybatissample.utils;

/**
 * @author Gee
 * @date 2020/11/12 16:45
 * @description
 */
public class TextUtils {
    public static boolean isEmpty(String str) {
        if (str == null || "".equals(str) || str.length() == 0)
            return true;
        else return false;
    }
}
