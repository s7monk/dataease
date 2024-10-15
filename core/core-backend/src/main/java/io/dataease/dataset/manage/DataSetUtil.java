package io.dataease.dataset.manage;

public class DataSetUtil {

    public static String replaceWithGn(String sql, String replacement) {
        return sql.replaceAll("(?i)\\bwithGn\\b", replacement);
    }
}
