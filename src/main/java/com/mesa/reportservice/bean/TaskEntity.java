package com.mesa.reportservice.bean;

/**
 * Created by wk1 on 2019/5/17.
 */
public class TaskEntity {

    private static Boolean state = false;

    private static String sql = "";

    public static Boolean getState() {
        return state;
    }

    public static void setState(Boolean state) {
        TaskEntity.state = state;
    }

    public static String getSql() {
        return sql;
    }

    public static void setSql(String sql) {
        TaskEntity.sql = sql;
    }
}
