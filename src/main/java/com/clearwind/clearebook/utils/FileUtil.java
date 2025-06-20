package com.clearwind.clearebook.utils;

import java.io.File;

public class FileUtil {
    /**
     * 获取文件的后缀名
     *
     * @param filePath 文件路径字符串
     * @return 文件后缀名，如 "epub", "mobi" 等；如果无后缀，返回空字符串
     */
    public static String getFileExtension(String filePath) {
        if (filePath == null || filePath.isEmpty()) {
            return "";
        }
        return getFileExtension(new File(filePath));
    }

    /**
     * 获取文件的后缀名
     *
     * @param file 输入文件
     * @return 文件后缀名，如 "epub", "mobi" 等；如果无后缀，返回空字符串
     */
    public static String getFileExtension(File file) {
        if (file == null || !file.exists() || file.isDirectory()) {
            return "";
        }
        String name = file.getName();
        int lastIndexOfDot = name.lastIndexOf('.');
        if (lastIndexOfDot > 0 && lastIndexOfDot < name.length() - 1) {
            return name.substring(lastIndexOfDot + 1);
        }
        return "";
    }
}
