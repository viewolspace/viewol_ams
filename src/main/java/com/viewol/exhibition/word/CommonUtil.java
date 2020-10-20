package com.viewol.exhibition.word;

public class CommonUtil {

    public static void main(String[] args) {
        String str = "abc\\";
        System.out.println("===> " + addIfNoSuffix(str, ".doc", "\\"));
        System.out.println("===> " + removeIfHaveSuffix(str, ".doc", "\\"));
    }

    /**
     * 判断一个字符串是否含有指定的后缀信息，如果没有则添加（可以指定多个后缀匹配，默认添加第一个）
     *
     * @param str     需要检查的字符串
     * @param suffixs 需要匹配的后缀
     * @return 新字符串
     * @date 2020-01-05
     */
    public static String addIfNoSuffix(String str, String... suffixs) {
        if (str == null || str.length() == 0) {
            return str;
        }
        boolean isTrue = true;
        for (String suffix : suffixs) {
            int len = suffix.length();
            String subSuffix = str.substring(str.length() - len);
            if (suffix.equals(subSuffix)) {
                isTrue = false;
                break;
            }
        }
        if (isTrue) {
            str += suffixs[0];
        }
        return str;
    }

    /**
     * 判断一个字符串是否含有指定的后缀信息，如果有则移除（可以指定多个后缀匹配，默认添加第一个）
     *
     * @param str     需要检查的字符串
     * @param suffixs 需要匹配的后缀
     * @return 新字符串
     * @date 2020-01-05
     */
    public static String removeIfHaveSuffix(String str, String... suffixs) {
        if (str == null || str.length() == 0) {
            return str;
        }
        for (String suffix : suffixs) {
            int len = suffix.length();
            String subSuffix = str.substring(str.length() - len);
            if (suffix.equals(subSuffix)) {
                return str.substring(0, str.length() - len);
            }
        }
        return str;
    }


}
